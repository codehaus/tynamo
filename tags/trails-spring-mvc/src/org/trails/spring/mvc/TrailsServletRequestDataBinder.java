package org.trails.spring.mvc;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.trails.descriptor.DescriptorService;
import org.trails.descriptor.IClassDescriptor;
import org.trails.descriptor.IPropertyDescriptor;
import org.trails.descriptor.TrailsPropertyDescriptor;
import org.trails.persistence.PersistenceService;
import org.trails.spring.util.ReflectionUtils;
import org.trails.spring.util.TrailsUtil;

/**
 * A databinder that binds the data from the ServletRequest onto an instance of a certain class.
 * This databinder uses trails specific features in order to correctly bind the attributes
 * from a certain {{HttpServletRequest}}.
 * 
 * @author Lars Vonk
 *
 */
public class TrailsServletRequestDataBinder {
  
  /** The errors. */
  private BindException errors = null;

  /**
   * Logger used to produce log messages.
   */
  private static Log log = LogFactory.getLog(TrailsServletRequestDataBinder.class);
  
  /**
   * The descriptorService used to retrieve information about objectReferences.
   */
  private DescriptorService descriptorService = null;
  
  /**
   * The persistenceService used to retrieve information about object references.
   */
  private PersistenceService persistenceService = null;

  /**
   * Constructor.
   */
  public TrailsServletRequestDataBinder(DescriptorService descriptorService, PersistenceService persistenceService) {
    this.descriptorService = descriptorService;
    this.persistenceService = persistenceService;
  }

  /**
   * Method delegates to {@link #bind(HttpServletRequest, IClassDescriptor, Object)} with
   * <code>null</code> as value for the parameter <code>existingInstance</code>.
   *  
   * @see #bind(HttpServletRequest, IClassDescriptor, Object)
   * @param request The servlet request from which the values will be retrieved.
   * @param classDescriptor The class descriptor of the object in which the values of the given <code>request</code> will be bound.
   * @return An object of type {@link IClassDescriptor#getType()} with the values of the given <code>request</code>.
   */
  public Object bind(HttpServletRequest request, IClassDescriptor classDescriptor) {
    return bind(request, classDescriptor, null);
  }

  /**
   * Method binds the parameters in the given <code>request</code> into a new Instance of
   * the class described by the given <code>classDescriptor</code> or into the given <code>existingInstance</code>
   * if it is not <code>null</code>. The bound instance is then returned by this method.
   * This method expects that the parameter names in the given <code>request</code> are the same as the attribute names in the
   * class described by the given <code>classDescriptor</code>.
   * <br>
   * This <code>TrailsServletRequestDataBinder</code> can also handle nested objects, that means that attibutes can also be another object.
   * If an attribute is an objectReference the corresponding parameter in the given <code>request</code> will look somehing like
   * <code>foo.id</code>. This means that the instance of type {@link IClassDescriptor#getType()} should have an attribute <code>foo</code>
   * of a certain class. That The <code>foo</code> class should have an attribute property named <code>id</code>, which is his identifier.
   * <br>
   * With the identifier value the instance is retrieved with the {@link #getPersistenceService()} and bound onto the returned Object.
   * <p>
   * 
   * @param request request The servlet request from which the values will be retrieved.
   * @param classDescriptor The class descriptor of the object in which the values of the given <code>request</code> will be bound.
   * @param existingInstance The instance in which the values will be bound, if it is <code>null</code> a new instance is returned.
   * @return An instance of the type described by the given <code>classDescriptor</code>.
   */
  public Object bind(HttpServletRequest request, IClassDescriptor classDescriptor, Object existingInstance) {

    // Get all settable attributes present on the type described by the given classDescriptor.
    List<String> attibutes = ReflectionUtils.getSettableFieldNames(classDescriptor.getType(), true);
    
    if (log.isDebugEnabled()) {
      log.debug("Binding request parameters onto an instance of class: " + classDescriptor.getType());
    }    
  
    // The instance to be returned, if there was an existing one we use that one.
    Object boundInstance = existingInstance;
    if(boundInstance == null) {
      boundInstance = ReflectionUtils.getNewInstance(classDescriptor.getType());
    }
    // a BindException object that can be filled with validation errors.
    errors = new BindException(request, "trailsModel");
    
    // loop over all settable attributes and retrieve their values from the ServletRequest.
    for (Iterator iter = attibutes.iterator(); iter.hasNext();) {
      String attributeName = (String) iter.next();
      if (log.isDebugEnabled()) {
        log.debug("Handling attribute: " + attributeName);
      }
      // check the type of the property and handle it accordingly
      TrailsPropertyDescriptor propertyDescriptor = (TrailsPropertyDescriptor) classDescriptor.getPropertyDescriptor(attributeName);
      
      if (TrailsUtil.isNormalProperty(propertyDescriptor)) {
        String value = request.getParameter(attributeName);
        if (value != null) {
          if (log.isDebugEnabled()) {
            log.debug("Validating and setting attribute: " + attributeName + " to: " + value);
          }        
          parseValidateAndSetParameterValueOntoInstance(boundInstance, propertyDescriptor, value);
        }      
      } else if (propertyDescriptor.isObjectReference()) {
        // get the id value from the request.
        String idValueAsString = getIdValueForObjectReference(request, attributeName);         
        if (StringUtils.isNotEmpty(idValueAsString)) {
          if (log.isDebugEnabled()) {
            log.debug("Setting object reference attribute: " + attributeName + " to an instance of: " + propertyDescriptor.getPropertyType() + " with with id: " + idValueAsString);
          }  
          // retrieve the instance of the objectReference with the given id. 
          Object referencedObject = getPersistenceService().getInstance(propertyDescriptor.getPropertyType(), parseIdValue(idValueAsString, propertyDescriptor));
          if (log.isDebugEnabled()) {
            log.debug("Referenced object: " + referencedObject + " found.");
          }  
          // now set the referencedObject on the boundInstance.
          ReflectionUtils.setFieldValueBySetMethod(boundInstance, attributeName, referencedObject, propertyDescriptor.getPropertyType(), true);
        }
      } 
      
      // FYI: Identifier properties and collection properties do not need to be handled
      // Identifier's may not be changed. And collection properties are not handled
      // as it is not (yet) supported to change or a child from a parent's view.

    }
    return boundInstance;
  }

    
  /**
   * Sets and validates the given <code>value</code> onto the attribute described by the given <code>propertyDescriptor</code>
   * onto the given <code>instance</code>.
   * 
   * @param instance The instance to set the value on.
   * @param descriptor The descriptor describing the attribute to be set to the value.
   * @param value The value to be parsed and validated to the correct type and set upon the given instance.
   * @param errors BindException that is filled in case the given <code>value</code> is in error.
   */
  protected void parseValidateAndSetParameterValueOntoInstance(Object instance, IPropertyDescriptor descriptor, String value) {
    // convert "" (empty) to null.
    if (value != null && "".equals(value)) {
      value = null;
    }
    // if the value is null but was required then a ValidationException is thrown
    if (value == null && descriptor.isRequired()) {
      addFieldError(errors, descriptor.getDisplayName(), value, descriptor.getDisplayName() + " is required.");
    } else {
      // parse the value to the correct type.
      
      try {
        if (descriptor.isString()) {
          
          log.debug("Property is a String: " + descriptor.getName());
          ReflectionUtils.setFieldValueBySetMethod(instance, descriptor.getName(), value, String.class, true);
        } else if (descriptor.getPropertyType().equals(Integer.class)) {
          
          log.debug("Property is an Integer: " + descriptor.getName());
          Integer i = (Integer) (value != null ? Integer.valueOf(value) : value);
          ReflectionUtils.setFieldValueBySetMethod(instance, descriptor.getName(), i, descriptor.getPropertyType(), true);
        } else if (descriptor.getPropertyType().equals(int.class)) {
          
          log.debug("Property is an int: " + descriptor.getName());
          int i = (int) (value != null ? Integer.parseInt(value) : 0);
          ReflectionUtils.setFieldValueBySetMethod(instance, descriptor.getName(), i, descriptor.getPropertyType(), true);
        } else if (descriptor.getPropertyType().equals(Long.class)) {
          
          log.debug("Property is a Long: " + descriptor.getName());
          Long l = (Long) (value != null ? Long.valueOf(value) : value);
          ReflectionUtils.setFieldValueBySetMethod(instance, descriptor.getName(), l, descriptor.getPropertyType(), true);
        } else if (descriptor.getPropertyType().equals(long.class)) {
          
          log.debug("Property is a long: " + descriptor.getName());
          long l = (long) (value != null ? Long.parseLong(value) : 0);
          ReflectionUtils.setFieldValueBySetMethod(instance, descriptor.getName(), l, descriptor.getPropertyType(), true);
        } else if (descriptor.getPropertyType().equals(BigDecimal.class)) {
          
          log.debug("Property is a BigDecimal: " + descriptor.getName());
          BigDecimal b = (BigDecimal) (value != null ? new BigDecimal(value) : value);
          ReflectionUtils.setFieldValueBySetMethod(instance, descriptor.getName(), b, BigDecimal.class, true);
        } else if (descriptor.isDate()) {
          
          log.debug("Property is a Date: " + descriptor.getName());
          String format = descriptor.getFormat();
          SimpleDateFormat dateFormat = new SimpleDateFormat(format);
          Date dateValue = (Date) (value != null ? dateFormat.parse(value) : value);
          ReflectionUtils.setFieldValueBySetMethod(instance, descriptor.getName(), dateValue, Date.class, true);
        }
      } catch (ParseException e) {
        log.debug("ParseException.", e);
        // invalid date format.
        addFieldError(errors, descriptor.getDisplayName(), value, value + " is not a valid date, format that should be used: " + descriptor.getFormat());
        //throw new ValidationException(descriptor, value + " is not a valid date, format that should be used: " + format);
      } catch (NumberFormatException e) {
        log.debug("NumberFormatException.", e);
        addFieldError(errors, descriptor.getDisplayName(), value, descriptor.getDisplayName() + " is not a numeric");
      }
    }
  }
  
  /**
   * Where there binding errors diring binding.
   * @return
   */
  public boolean hasErrors() {
    
    return (errors != null && errors.hasErrors());
  }
  
  /**
   * Returns there binding errors during binding.
   * @return
   */
  public BindException getErrors() {
    
    return errors;
  }
  /**
   * Sets the binding errors.
   * @param errors the errors to set
   */
  public void setErrors(BindException errors) {
    
    this.errors = errors;
  }  
  // ==================================================================
  // Default getters and setters
  // ==================================================================  
  /**
   * Returns the descriptorService.
   * @return Returns the descriptorService.
   */
  public DescriptorService getDescriptorService() {
    return descriptorService;
  }

  /**
   * Sets the descriptorService.
   * @param descriptorService The descriptorService to set.
   */
  public void setDescriptorService(DescriptorService descriptorService) {
    this.descriptorService = descriptorService;
  }  
  /**
   * Returns the persistenceService.
   * @return Returns the persistenceService.
   */
  public PersistenceService getPersistenceService() {
    return persistenceService;
  }
  /**
   * Sets the persistenceService.
   * @param persistenceService The persistenceService to set.
   */
  public void setPersistenceService(PersistenceService persistenceService) {
    this.persistenceService = persistenceService;
  }
  // ============================================================
  // private convenience methods.
  // ============================================================
  /**
   * 
   * @param request
   * @param objectReferenceName
   * @return
   */
  private String getIdValueForObjectReference(HttpServletRequest request, String objectReferenceName) {
    // the id value to be returned.
    String idValue = null;
    // loop over all request parameter names. The one that starts with "objectReferenceName."
    // is the one that contains the id value.
    Enumeration allNamesEnum = request.getParameterNames(); 
    while(allNamesEnum.hasMoreElements()) {
      String name = (String) allNamesEnum.nextElement();
      if (name.startsWith(objectReferenceName + ".")) {
        // found it!
        idValue = request.getParameter(name);
        break;
      }
    }
    return idValue;
  }
  /**
   * TODO How should this be implemented?
   * @param idValueAsString
   * @param trailsPropertyDescriptor
   * @return
   */
  private Serializable parseIdValue(String idValueAsString, TrailsPropertyDescriptor trailsPropertyDescriptor) {
    Serializable id = null;
    TrailsPropertyDescriptor idDescriptor = (TrailsPropertyDescriptor)((IClassDescriptor)getDescriptorService().getClassDescriptor(trailsPropertyDescriptor.getPropertyType())).getIdentifierDescriptor();
    if (isLong(idDescriptor.getPropertyType())) {
      
      id = Long.valueOf(idValueAsString);
    } else if (isInteger(idDescriptor.getPropertyType())) {
      
      id = Integer.valueOf(idValueAsString);
    } else {
      
      id = idValueAsString;
    }
    return id;
  }
  
  /**
   * 
   * @param errors
   * @param objectName
   * @param propertyName
   * @param value
   * @param defaultMessage
   */
  private void addFieldError(BindException errors, String propertyName, Object value, String defaultMessage) {
    errors.addError( new FieldError(errors.getObjectName(), propertyName, value, false, null, null, defaultMessage));
  }
  /**
   * 
   * @param clazz
   * @return
   */
  private boolean isLong(Class clazz) {
    return (clazz.equals(Long.class) || clazz.equals(long.class));
  }
  
  /**
   * 
   * @param clazz
   * @return
   */
  private boolean isInteger(Class clazz) {
    return (clazz.equals(Integer.class) || clazz.equals(int.class));
  }  
}
