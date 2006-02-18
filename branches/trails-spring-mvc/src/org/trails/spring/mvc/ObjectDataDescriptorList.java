package org.trails.spring.mvc;

import java.util.ArrayList;
import java.util.List;

import org.trails.descriptor.IClassDescriptor;
import org.trails.descriptor.IPropertyDescriptor;
import org.trails.spring.util.ReflectionUtils;

/**
 * An ObjectDataDescriptorList holds a collection of {@link org.trails.spring.mvc.ObjectDataDescriptor}'s
 * and some additional information such as the {@link org.trails.descriptor.IClassDescriptor}
 * describing the instances in this ObjectDataDescriptorList, actually all the instances
 * in the collection of {@link org.trails.spring.mvc.ObjectDataDescriptor}'s should be of the
 * type described by the associated {@link #getClassDescriptor() classDescriptor}. 
 * <p>
 * The ObjectDataDescriptorList is used in the view's for rendering the the object's and acts
 * as a sort of ObjectTable. One can iterate of the {@link #getRows() rows} that contain
 * {@link org.trails.spring.mvc.ObjectDataDescriptor}'s.
 * <p>
 * The {@link org.trails.spring.mvc.PropertyDataDescriptor#getValue()} 
 * of a {@link org.trails.spring.mvc.PropertyDataDescriptor} in the 
 * {@link org.trails.spring.mvc.ObjectDataDescriptor}'s can also contain be
 * an ObjectDatadescriptorList, for instance if the value is collection.
 * <p> 
 * @author Lars Vonk
 *
 */
public class ObjectDataDescriptorList {
  /**
   * The rows in this ObjectDataDescriptorList.
   */
  private List<ObjectDataDescriptor> rows = new ArrayList<ObjectDataDescriptor>();
  /**
   * The class decriptor describing the {@link ObjectDataDescriptor}'s in
   * the {@link #getRows() rows}.
   */
  private IClassDescriptor classDescriptor = null;
  
  /**
   * Holds the {@link IPropertyDescriptor}'s as columnNames. This
   * is used for convenience so the ObjectDataDescriptorList
   * knows which properties the ObjectDataDescriptor in the {@link #getRows() rows}
   * contains.
   */
  private List<IPropertyDescriptor> columnNames = new ArrayList<IPropertyDescriptor>();
  /** The current page number, used for paging. */
  private int currentPageNumber;
  /** The total number of pages that this list is devided into. */
  private int totalNumberOfPages;
  
  /**
   * Creates an empty <code>ObjectDataDescriptorList</code>. The rows can
   * be added by the {@link #add(ObjectDataDescriptor)} method.
   * <p>
   * Use this constructor when the created {@link ObjectDataDescriptor}'s 
   * and its {@link PropertyDataDescriptor}'s need extra information
   * besides the {@link IPropertyDescriptor}'s and the {@link IClassDescriptor}'s, for example
   * when an property is an object reference the user needs to be able to choose
   * from all available instances for that property. The <code>ObjectDataDescriptorList</code>
   * cannot handle such actions because the {@link org.trails.persistence.PersistenceService}
   * is then necessary and an <code>ObjectDataDescriptorList</code> does not have access to the
   * {@link org.trails.persistence.PersistenceService}.
   * <p>
   * @param classDescriptor The descriptor of the type of the instances in this <code>ObjectDataDescriptorList</code>.
   */
  public ObjectDataDescriptorList(IClassDescriptor classDescriptor) {
    this.classDescriptor = classDescriptor;
    
    List propertiesDescriptors = this.classDescriptor.getPropertyDescriptors();
    for (Object object : propertiesDescriptors) {
      IPropertyDescriptor propertyDescriptor = (IPropertyDescriptor) object;
        columnNames.add(propertyDescriptor);
    }
  }

  /**
   * Creates an empty <code>ObjectDataDescriptorList</code>. The rows can
   * be added by the {@link #add(ObjectDataDescriptor)} method.
   * This is done when the created {@link ObjectDataDescriptor}'s and {@link PropertyDataDescriptor}'s need extra information
   * besides the {@link IPropertyDescriptor}'s and the {@link IClassDescriptor}'s, for example
   * when an property is an object reference the user needs to be able to choose
   * from all available instances for that property. For this the {@link org.trails.persistence.PersistenceService}
   * is necessary and an <code>ObjectDataDescriptorList</code> does not have access to the
   * {@link org.trails.persistence.PersistenceService}.
   * 
   * @param classDescriptor The descriptor of the type of the instances in this <code>ObjectDataDescriptorList</code>.
   * @param pageNumber the current page in case of paging.
   * @param totalNumberOfPages the total number of pages.
   */
  public ObjectDataDescriptorList(IClassDescriptor classDescriptor, int pageNumber, int totalNumberOfPages) {
    this(classDescriptor);
    setCurrentPageNumber(pageNumber);
    setTotalNumberOfPages(totalNumberOfPages);
  }
  
  /**
   * Creates and instantiates this ObjectDataDescriptorList.
   * @param instances A List of instances to put in this ObjectDataDescriptorList.
   * @param classDescriptor The descriptor of the type of the instances.
   */
  public <T> ObjectDataDescriptorList(List<T> instances, IClassDescriptor classDescriptor) {
    this(instances, classDescriptor, null);
  }
  /**
   * Creates and instantiates this ObjectDataDescriptorList. This construtor will also 
   * set the flag {@link ObjectDataDescriptor#setSelected(boolean)} flag
   * to <code>true</code> in the {@link ObjectDataDescriptor} for the given selectedInstance.
   * <br>This is usefull in case the to be created ObjectDataDescriptorList is actually
   * used for rendering an attribute in another ObjectDataDescriptorList.
   * 
   * @param instances A List of instances to put in this ObjectDataDescriptorList.
   * @param classDescriptor The descriptor of the type of the instances.
   * @param selectedInstance The instance that should be selected in the list.
   */
  public <T> ObjectDataDescriptorList(List<T> instances, IClassDescriptor classDescriptor, Object selectedInstance) {
    this.classDescriptor = classDescriptor;
    List propertiesDescriptors = this.classDescriptor.getPropertyDescriptors();
    for (int i = 0; i< instances.size(); i++) {
      Object instance = instances.get(i);
      ObjectDataDescriptor row = new ObjectDataDescriptor();
      if (selectedInstance != null && selectedInstance.equals(instance)) {
        row.setSelected(true);
      }
      
      row.setInstance(instance);
      // there can also be a null value in the list, e.g. on the
      // search page where the first "instance" should be empty.
      if (instance != null) {
        List<PropertyDataDescriptor> columns = new ArrayList<PropertyDataDescriptor>();
        for (Object object : propertiesDescriptors) {
  
          IPropertyDescriptor propertyDescriptor = (IPropertyDescriptor) object;
          // only add the column name to the list of column names if its not present yet.
          if (i==0) {
            columnNames.add(propertyDescriptor);
          }
          
          PropertyDataDescriptor column = new PropertyDataDescriptor(propertyDescriptor);
          Object value = ReflectionUtils.getFieldValueByGetMethod(instance, propertyDescriptor.getName());
          column.setValue(value);
          columns.add(column);
          
        }
        
        row.setColumns(columns);
      }
      rows.add(row);
    }    
  }  
  /**
   * Adds a row to this ObjectDataDescriptorList.
   * @param tableRow The row to be added.
   */
  public void add(ObjectDataDescriptor tableRow) {
    rows.add(tableRow);
  }
  
  // ========================================================
  // Basic Getters and setters.
  // ========================================================
  /**
   * Returns the classDescriptor.
   * @return Returns the classDescriptor.
   */
  public IClassDescriptor getClassDescriptor() {
    return classDescriptor;
  }


  /**
   * Sets the classDescriptor.
   * @param classDescriptor The classDescriptor to set.
   */
  public void setClassDescriptor(IClassDescriptor classDescriptor) {
    this.classDescriptor = classDescriptor;
  }


  /**
   * Returns the rows.
   * @return Returns the rows.
   */
  public List<ObjectDataDescriptor> getRows() {
    return rows;
  }

  /**
   * Sets the rows.
   * @param rows The rows to set.
   */
  public void setRows(List<ObjectDataDescriptor> rows) {
    this.rows = rows;
  }
  
  /**
   * Returns the columnNames.
   * @return Returns the columnNames.
   */
  public List<IPropertyDescriptor> getColumnNames() {
    return columnNames;
  }


  /**
   * Sets the columnNames.
   * @param columnNames The columnNames to set.
   */
  public void setColumnNames(List<IPropertyDescriptor> columnNames) {
    this.columnNames = columnNames;
  }
  /**
   * Returns the totalNumberOfPages.
   * @return Returns the totalNumberOfPages.
   */
  public int getTotalNumberOfPages() {
    return totalNumberOfPages;
  }

  /**
   * Sets the totalNumberOfPages.
   * @param totalNumberOfPages The totalNumberOfPages to set.
   */
  public void setTotalNumberOfPages(int totalNumberOfPages) {
    this.totalNumberOfPages = totalNumberOfPages;
  }  
  /**
   * Returns the currentPageNumber.
   * @return Returns the currentPageNumber.
   */
  public int getCurrentPageNumber() {
    return currentPageNumber;
  }

  /**
   * Sets the currentPageNumber.
   * @param currentPageNumber The currentPageNumber to set.
   */
  public void setCurrentPageNumber(int currentPageNumber) {
    this.currentPageNumber = currentPageNumber;
  }  
}
