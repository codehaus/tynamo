/*
 * Copyright 2005, Inspiring BV, the Netherlands
 *
 * info@inspiring.nl
 */

package org.trails.spring.mvc;

import java.util.ArrayList;
import java.util.List;


import org.trails.descriptor.IClassDescriptor;
import org.trails.descriptor.IPropertyDescriptor;
import org.trails.spring.util.ReflectionUtils;

/**
 * Holds a <i>table</i> of instances of the same <code>Class</code> and
 * some usefull data that is used for rendering the instances
 * on the screen.
 * 
 * @author Lars Vonk
 *
 */
public class ObjectDataDescriptorList {
  /**
   * The rows in this table.
   */
  private List<ObjectDataDescriptor> rows = new ArrayList<ObjectDataDescriptor>();
  /**
   * The class decriptor.
   */
  private IClassDescriptor classDescriptor = null;
  
  /**
   * Holds the name of the columns.
   */
  private List<IPropertyDescriptor> columnNames = new ArrayList<IPropertyDescriptor>();
  
  private int currentPageNumber;
  
  private int totalNumberOfPages;
  
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
   */
  public ObjectDataDescriptorList(IClassDescriptor classDescriptor) {
    this.classDescriptor = classDescriptor;
    
    List propertiesDescriptors = this.classDescriptor.getPropertyDescriptors();
    for (Object object : propertiesDescriptors) {
      IPropertyDescriptor propertyDescriptor = (IPropertyDescriptor) object;
      // only add the column name to the list of column names if its not present yet.
//      if(!columnNames.contains(propertyDescriptor)) {
        columnNames.add(propertyDescriptor);
//      }
    }
  }

  /**
   * Creates an empty <code>ObjectDataDescriptorList</code>. The rows can
   * be added by the {@link #add(ObjectDataDescriptor)} method.
   * This is done when the created {@link ObjectDataDescriptor}'s and {@link PropertyDataDescriptor}'s need extra information
   * besides the {@link IPropertyDescriptor}'s and the {@link IClassDescriptor}'s, for example
   * when an property is an object reference the user needs to be able to choose
   * from all available instances for that proerty. For this the {@link org.trails.persistence.PersistenceService}
   * is necessary and an <code>ObjectDataDescriptorList</code> does not have access to the
   * {@link org.trails.persistence.PersistenceService}.
   * 
   * @param classDescriptor The descriptor of the type of the instances in this <code>ObjectDataDescriptorList</code>.
   * @param pageNumber the current page in case pagination.
   * @param totalNumberOfPages the total number of pages.
   */
  public ObjectDataDescriptorList(IClassDescriptor classDescriptor, int pageNumber, int totalNumberOfPages) {
    this(classDescriptor);
    setCurrentPageNumber(pageNumber);
    setTotalNumberOfPages(totalNumberOfPages);
  }
  
  /**
   * Creates an <code>ObjectDataDescriptorList</code> initialized with the values
   * of the given <code>instance</code>.
   *   
   * @param instance The instance that is rendered on the screen.is put in this <code>ObjectDataDescriptorList</code>
   * @param classDescriptor The descriptor of the type of the instance.
   */
  public ObjectDataDescriptorList(Object instance, IClassDescriptor classDescriptor) {
    List list = new ArrayList();
    list.add(instance);
    init(list, classDescriptor, null);
  }
  
  /**
   * Creates and instantiates this ObjectDataDescriptorList.
   * @param instances A List of instances to put in this ObjectDataDescriptorList.
   * @param classDescriptor The descriptor of the type of the instances.
   */
  public <T> ObjectDataDescriptorList(List<T> instances, IClassDescriptor classDescriptor) {
    init(instances, classDescriptor, null);
  }
  /**
   * Creates and instantiates this ObjectDataDescriptorList.
   * @param instances A List of instances to put in this ObjectDataDescriptorList.
   * @param classDescriptor The descriptor of the type of the instances.
   */
  public <T> ObjectDataDescriptorList(List<T> instances, IClassDescriptor classDescriptor, Object selectedInstance) {
    init(instances, classDescriptor, selectedInstance);
  }  
  /**
   * Creates and instantiates this ObjectDataDescriptorList.
   * @param instances A List of instances to put in this ObjectDataDescriptorList.
   * @param classDescriptor The descriptor of the type of the instances.
   */
  private <T> void init(List<T> instances, IClassDescriptor classDescriptor, Object selectedInstance) {
    this.classDescriptor = classDescriptor;
    List propertiesDescriptors = this.classDescriptor.getPropertyDescriptors();
    for (int i = 0; i< instances.size(); i++) {
      Object instance = instances.get(i);
      ObjectDataDescriptor row = new ObjectDataDescriptor();
      if (selectedInstance != null && instance.equals(selectedInstance)) {
        row.setSelected(true);
      }
      
      row.setInstance(instance);
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
