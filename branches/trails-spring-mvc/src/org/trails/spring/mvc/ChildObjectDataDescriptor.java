package org.trails.spring.mvc;

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
