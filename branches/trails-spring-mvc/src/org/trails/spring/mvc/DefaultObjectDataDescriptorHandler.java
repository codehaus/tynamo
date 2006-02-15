package org.trails.spring.mvc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.trails.descriptor.CollectionDescriptor;
import org.trails.descriptor.DescriptorService;
import org.trails.descriptor.IClassDescriptor;
import org.trails.descriptor.TrailsPropertyDescriptor;
import org.trails.persistence.PersistenceService;
import org.trails.spring.util.ReflectionUtils;

/**
 * Default implementation of an {@link ObjectDataDescriptorHandler}.
 * @author Lars Vonk
 *
 */
public class DefaultObjectDataDescriptorHandler implements ObjectDataDescriptorHandler {
  
  /** Logger. */
  private static final Log log = LogFactory.getLog(DefaultObjectDataDescriptorHandler.class);

  /** The persistenceService. */
  private PersistenceService persistenceService = null;
  /** The descriptorService. */ 
  private DescriptorService descriptorService = null;
  
  /**
   * @see org.trails.spring.mvc.ObjectDataDescriptorHandler#createAndResolveChildern(java.lang.Object, org.trails.descriptor.IClassDescriptor)
   */
  public ObjectDataDescriptorList createAndResolveChildern(Object instance, IClassDescriptor classDescriptor) {
    // Create a new ObjectDataDescriptorList that holds the information to be rendered in the view.
    ObjectDataDescriptorList objectDataDescriptorList = new ObjectDataDescriptorList(classDescriptor);
    
    // Create a row for the instance to be edited, added or search.
    ObjectDataDescriptor objectDataDescriptor = createObjectDataDescriptor(instance, classDescriptor, true);
    objectDataDescriptorList.add(objectDataDescriptor);
    return objectDataDescriptorList;
  }

  /**
   * @see org.trails.spring.mvc.ObjectDataDescriptorHandler#createObjectDataDescriptorList(java.util.List, org.trails.descriptor.IClassDescriptor, boolean, int, int)
   */
  public ObjectDataDescriptorList create(List instances, IClassDescriptor classDescriptor, int pageNumber,
      int totalNumberOfPages) {
    // Create a new ObjectDataDescriptorList that holds the information to be rendered in the view.
    ObjectDataDescriptorList objectDataDescriptorList = new ObjectDataDescriptorList(classDescriptor, pageNumber, totalNumberOfPages);
    List propertyDescriptors = classDescriptor.getPropertyDescriptors();
    for (Iterator iter = instances.iterator(); iter.hasNext();) {
      Object instance = (Object) iter.next();
      ObjectDataDescriptor objectDataDescriptor = createObjectDataDescriptor(instance, classDescriptor, false);
      objectDataDescriptorList.add(objectDataDescriptor);
    }

    return objectDataDescriptorList;
  }
  
  /**
   * Method creates an {@link ObjectDataDescriptor} for the given instance with
   * its corresponding {@link IClassDescriptor}.
   * @param instance The instance for which the {@link ObjectDataDescriptor} is created.
   * @param classDescriptor The {@link IClassDescriptor} describing the given <code>instance</code>.
   * @param resolveAllChildern indicator whether or not all the childern of an instance shoudl be resolved
   *      in case of an object reference.
   * @return An {@link ObjectDataDescriptor}.
   */
  private ObjectDataDescriptor createObjectDataDescriptor(Object instance, IClassDescriptor classDescriptor, boolean resolveAllChildern) {
    // Create a row for the instance to be edited or added.
    ObjectDataDescriptor objectDataDescriptor = new ObjectDataDescriptor();
    objectDataDescriptor.setInstance(instance);
    
    List propertyDescriptors = classDescriptor.getPropertyDescriptors();
    log.debug("Creating object data descriptor with #descriptors " + propertyDescriptors.size());
    
    List<PropertyDataDescriptor> propertyDataDescriptors = new ArrayList<PropertyDataDescriptor>();
    // Loop through all the properties and create a column for each property
    // then add the necessary information into that column for that property
    // so that it can be rendered correctly in the View.
    for (Object object: propertyDescriptors) {
      
      TrailsPropertyDescriptor propertyDescriptor = (TrailsPropertyDescriptor) object;
      PropertyDataDescriptor propertyDataDescriptor = new PropertyDataDescriptor(propertyDescriptor);
      Object value = ReflectionUtils.getFieldValueByGetMethod(instance, propertyDescriptor.getName());
      // If the property is an object reference it means it has can have a reference to multiple instances of another type.
      // Locate all the instances of that type and add it to the column as value. Also we set an indicator (valueInObjectTable) 
      // that indicates that the value is an ObjectDataDescriptorList to true, this gives us easy rendering in the View.
      if (propertyDescriptor.isObjectReference() && resolveAllChildern) {
        log.debug("Handeling ObjectReference for property: " + propertyDescriptor.getName() + ", resolveAllChildern = " + resolveAllChildern);
        List instances = getPersistenceService().getAllInstances(propertyDescriptor.getPropertyType());
        IClassDescriptor descriptor = getDescriptorService().getClassDescriptor(propertyDescriptor.getPropertyType());
        ObjectDataDescriptorList table = new ObjectDataDescriptorList(instances, descriptor, value);
        propertyDataDescriptor.setValue(table);
        propertyDataDescriptor.setValueInObjectTable(true);

      } else if(propertyDescriptor.isCollection() && value != null) {
        
        log.debug("Handeling Collections for property: " + propertyDescriptor.getName() + "value = " + value);
        // If it is a Collection we retrieve all the childern and put them into a seperate ObjectDataDescriptorList
        // so they can be rendered in the view.
        CollectionDescriptor collectionDescriptor = (CollectionDescriptor) propertyDescriptor;
        IClassDescriptor classDescriptorChild = getDescriptorService().getClassDescriptor(collectionDescriptor.getElementType());
        String identifierName = classDescriptorChild.getIdentifierDescriptor().getName();
        Collection childern = (Collection) value;
        ObjectDataDescriptorList objectDataDescriptorList = new ObjectDataDescriptorList(classDescriptorChild);
        for (Iterator iter = childern.iterator(); iter.hasNext();) {
          Object child = (Object) iter.next();
          ChildObjectDataDescriptor childObjectDataDescriptor = new ChildObjectDataDescriptor();
          childObjectDataDescriptor.setInstance(child);
          Object id = ReflectionUtils.getFieldValueByGetMethod(child, identifierName);
          childObjectDataDescriptor.setId(id);
          objectDataDescriptorList.add(childObjectDataDescriptor);
          
          log.debug("Child added to ObjectDataDescriptorList.");
        }
        propertyDataDescriptor.setValueInObjectTable(true);
        propertyDataDescriptor.setValue(objectDataDescriptorList);
    
      } else {
        log.debug("Nomral property: " + propertyDescriptor.getName());
        // a "normal" property.
        propertyDataDescriptor.setValue(value);
      }
      propertyDataDescriptors.add(propertyDataDescriptor);
    }
    objectDataDescriptor.setColumns(propertyDataDescriptors);
    return objectDataDescriptor;

  }
  
  // =========================================================
  // Used for DI the persistence and descriptor service.
  // =========================================================
  /**
   * @return Returns the descriptorService.
   */
  public DescriptorService getDescriptorService() {
    return descriptorService;
  }

  /**
   * @param descriptorService The descriptorService to set.
   */
  public void setDescriptorService(DescriptorService descriptorService) {
    this.descriptorService = descriptorService;
  }

  /**
   * @return Returns the persistenceService.
   */
  public PersistenceService getPersistenceService() {
    return persistenceService;
  }

  /**
   * @param persistenceService The persistenceService to set.
   */
  public void setPersistenceService(PersistenceService persistenceService) {
    this.persistenceService = persistenceService;
  }  

}
