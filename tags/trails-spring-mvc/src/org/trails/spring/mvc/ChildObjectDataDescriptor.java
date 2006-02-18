package org.trails.spring.mvc;

/**
 * Class is used for Objects that are values of in collection of a parent object
 * in a {@link org.trails.spring.mvc.ObjectDataDescriptorList}. For this situation
 * we only need the id of the class and we don't want to populate the entire
 * ObjectDataDescriptor with
 * {@link org.trails.spring.mvc.ObjectDataDescriptor#getColumns() colums} etc.
 * 
 * @author Lars Vonk
 *
 */
public class ChildObjectDataDescriptor extends ObjectDataDescriptor {
  
  /** Holds the id of the instance in the ObjectDataDescriptor. */
  private Object id = null;

  /**
   * Returns the id.
   * @return Returns the id.
   */
  public Object getId() {
    return id;
  }

  /**
   * Sets the id.
   * @param id The id to set.
   */
  public void setId(Object id) {
    this.id = id;
  } 
}
