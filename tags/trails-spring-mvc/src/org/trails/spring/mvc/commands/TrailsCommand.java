package org.trails.spring.mvc.commands;

import org.trails.descriptor.IClassDescriptor;
import org.trails.spring.util.ReflectionUtils;

/**
 * Command class for Trails.
 * 
 * @author Jurjan Woltman
 *
 */
public class TrailsCommand {

  /** the type of the class shown. */
  private Class type = null;
  /** The id of the class.*/
  private String id = null;
  /** The current page number. Default 1. */
  private int pageNumber = 1;
  /** The total number of pages that can be shown. Default 1.*/
  private int totalNumberOfPages = 1;
  /** Holds the id of the parent class in case of an parent-child relationship. */
  private String parentId = null;
  /** Holds the parent class in case of an parent-child relationship. */
  private String parentClass = null;
  /**
   * Default constructor.
   *
   */
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

  /**
   * @return Returns the parentClass.
   */
  public String getParentClass() {
    return parentClass;
  }

  /**
   * @param parentClass The parentClass to set.
   */
  public void setParentClass(String parentClass) {
    this.parentClass = parentClass;
  }

  /**
   * @return Returns the parentId.
   */
  public String getParentId() {
    return parentId;
  }

  /**
   * @param parentId The parentId to set.
   */
  public void setParentId(String parentId) {
    this.parentId = parentId;
  }
}
