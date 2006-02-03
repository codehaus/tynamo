/*
 * Copyright 2005, Inspiring BV, the Netherlands
 *
 * info@inspiring.nl
 */

package org.trails.spring.mvc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.trails.descriptor.TrailsPropertyDescriptor;

/**
 * A <code>ObjectDataDescriptor</code> is a row in an {@link org.trails.spring.mvc.ObjectDataDescriptorList}.
 * A row is a collection of {@link org.trails.spring.mvc.PropertyDataDescriptor}'s.
 * 
 * @author Lars Vonk
 *
 */
public class ObjectDataDescriptor {
  
  /**
   * Holds the instances represented by this table column.
   */
  private Object instance = null;
  
  /**
   * 
   */
  private boolean selected = false;
  /**
   * A List of columns.
   * TODO: This really should be sorted somehow.
   */
  private List<PropertyDataDescriptor> columns = new ArrayList<PropertyDataDescriptor>();
  //private Map namedColumns = new TreeMap();

  /**
   * Returns the columns.
   * @return Returns the columns.
   */
  public List<PropertyDataDescriptor> getColumns() {
    return columns;
  }

  /**
   * Sets the columns.
   * @param columns The columns to set.
   */
  public void setColumns(List<PropertyDataDescriptor> columns) {
    this.columns = columns;
    //initMap();
  }

//  private void initMap() {
//    namedColumns.clear();
//    for(PropertyDataDescriptor d : columns) {
//      namedColumns.put(d.getPropertyDescriptor().getName(), d);
//    }
//  }

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
  
  public Object getIdentifierValue() {
    Object value = null;
    
    for (PropertyDataDescriptor column: columns) {
      if (((TrailsPropertyDescriptor)column.getPropertyDescriptor()).isIdentifier()) {
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
  
//  public Map getColumnMap() {
//    return namedColumns;
//  }
}
