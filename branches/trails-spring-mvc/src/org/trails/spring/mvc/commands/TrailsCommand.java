/*
 * Copyright 2005, Inspiring BV, the Netherlands
 *
 * info@inspiring.nl
 */

package org.trails.spring.mvc.commands;

import java.util.Collection;


import org.trails.descriptor.IClassDescriptor;
import org.trails.spring.util.ReflectionUtils;

/**
 * @author Jurjan Woltman
 *
 */
public class TrailsCommand {

  private Class type = null;
  
  private Collection items = null;
  
  private String id = null;
  
  private int pageNumber = 0;
  
  private int totalNumberOfPages = 0;
  
  public TrailsCommand() {}
  
  /**
   * Creates a TrailsCommand instance based on the provided domain object and class descriptor.
   * 
   * @param domainObject the domain object.
   * @param classDescriptor the class descriptor.
   */
  public TrailsCommand(Object domainObject, IClassDescriptor classDescriptor) {
    type = domainObject.getClass();
    Object idObject = ReflectionUtils.getFieldValueByGetMethod(domainObject, classDescriptor.getIdentifierDescriptor().getName());
    if (idObject != null) {
      id = idObject.toString();
    }
  }

  /**
   * Returns the id.
   * @return Returns the id.
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the id.
   * @param id The id to set.
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Returns the items.
   * @return Returns the items.
   */
  public Collection getItems() {
    return items;
  }

  /**
   * Sets the items.
   * @param items The items to set.
   */
  public void setItems(Collection items) {
    this.items = items;
  }

  /**
   * Returns the pageNumber.
   * @return Returns the pageNumber.
   */
  public int getPageNumber() {
    return pageNumber;
  }

  /**
   * Sets the pageNumber.
   * @param pageNumber The pageNumber to set.
   */
  public void setPageNumber(int pageNumber) {
    this.pageNumber = pageNumber;
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
   * Returns the type.
   * @return Returns the type.
   */
  public Class getType() {
    return type;
  }

  /**
   * Sets the type.
   * @param type The type to set.
   */
  public void setType(Class type) {
    this.type = type;
  }
}
