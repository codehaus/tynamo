package org.trails.spring.mvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * An <code>ObjectDataDescriptor</code> describes an instance of an Object. This
 * is the instance counterpart of the {@link org.trails.descriptor.IClassDescriptor}.
 * <p>
 * An <code>ObjectDataDescriptor</code> is always wrapped in an
 * {@link org.trails.spring.mvc.ObjectDataDescriptorList}. This
 * list holds the {@link org.trails.descriptor.IClassDescriptor}
 * describing the type of the {@link #getInstance() instance}
 * encapsulated by this <code>ObjectDataDescriptor</code>.
 * <p>
 * The prupose of this class is to be able to add extra information
 * about a certain instance of a Class that is described by an 
 * {@link org.trails.descriptor.IClassDescriptor}.
 * <p>
 * @author Lars Vonk
 *
 */
public class ObjectDataDescriptor {
  
  /**
   * Holds the instances represented by this table column.
   */
  private Object instance = null;
  
  /**
   * Is the value selected in case this ObjectDataDescriptor
   * is rendered in a list of values?
   */
  private boolean selected = false;
  /**
   * A List of columns.
   */
  private List<PropertyDataDescriptor> columns = new ArrayList<PropertyDataDescriptor>();

  /**
   * Returns the columns. Or an empty list is now
   * columns are set.
   * @return Returns the columns.
   */
  public List<PropertyDataDescriptor> getColumns() {
    return columns;
  }

  /**
   * Sets the columns.
   * @param propertyDataDescriptors The columns to set.
   */
  public void setColumns(List<PropertyDataDescriptor> propertyDataDescriptors) {
    this.columns = propertyDataDescriptors;
  }

  /**
   * Returns the instance.
   * @return Returns the instance.
   */
  public Object getInstance() {
    return instance;
  }

  /**
   * Sets the instance.
   * @param instance The instance to set.
   */
  public void setInstance(Object instance) {
    this.instance = instance;
  }
  // ==========================================================================
  // Convenience methods
  // ==========================================================================
  
  /**
   * Returns the value of the identifier property
   * of the encapsulated {@link #getInstance() instance}.
   * @return The value of the identifer property.
   */
  public Object getIdentifierValue() {
    Object value = null;
    
    for (PropertyDataDescriptor column: columns) {
      if (column.getPropertyDescriptor().isIdentifier()) {
        value = column.getValue();
      }
    }
    
    return value;
  }
  /**
   * Returns the selected.
   * @return Returns the selected.
   */
  public boolean isSelected() {
    return selected;
  }

  /**
   * Sets the selected.
   * @param selected The selected to set.
   */
  public void setSelected(boolean selected) {
    this.selected = selected;
  }
}
