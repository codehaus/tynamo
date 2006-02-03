/*
 * Copyright 2005, Inspiring BV, the Netherlands
 *
 * info@inspiring.nl
 */

package org.trails.spring.mvc;

import org.trails.descriptor.IPropertyDescriptor;

/**
 * A <code>PropertyDataDescriptor</code> encapsulates a the property that needs
 * to be rendered on the screen and the {@link org.trails.descriptor.IPropertyDescriptor}
 * described the property to be rendered.
 * Also this <code>PropertyDataDescriptor</code> has some more information that can be usefull
 * when rendering the property on the screen.
 * 
 * @author Lars Vonk
 *
 */
public class PropertyDataDescriptor { 
  /**
   * Holds the value of the property of this column.
   */
  private Object value = null;
  /**
   * Holds the descriptor of the property of this column.
   */
  private IPropertyDescriptor propertyDescriptor = null;
  
  /**
   * Are the values in this column in an <code>ObjectDataDescriptorList</code>
   * This flag is needed when the property is an object reference
   * and the values holds all the possible instances of 
   * the {@link IPropertyDescriptor#getPropertyType() type} of the
   * property. The <code>ObjectDataDescriptorList</code> of these instances are typically shown
   * on the screen as a &lt;Select&gt; box and the user can choose
   * the instance it wants.
   */
  private boolean valueInObjectTable = false;
  
  /**
   * Flag indicating if the {@link #value} is valid. this is needed in
   * order to show invalid fields in the views.
   */
  private boolean valueInvalid = false;
  
  /**
   * Creates a <code>PropertyDataDescriptor</code>.
   * @param propertyDescriptor The descriptor of the property in this <code>PropertyDataDescriptor</code>.
   */
  public PropertyDataDescriptor(IPropertyDescriptor propertyDescriptor) {
    this.propertyDescriptor = propertyDescriptor;
  }

  /**
   * Returns the propertyDescriptor.
   * @return Returns the propertyDescriptor.
   */
  public IPropertyDescriptor getPropertyDescriptor() {
    return propertyDescriptor;
  }

  /**
   * Sets the propertyDescriptor.
   * @param propertyDescriptor The propertyDescriptor to set.
   */
  public void setPropertyDescriptor(IPropertyDescriptor propertyDescriptor) {
    this.propertyDescriptor = propertyDescriptor;
  }

  /**
   * Returns the value.
   * @return Returns the value.
   */
  public Object getValue() {
    return (value == null ? "" : value);
  }

  /**
   * Sets the value.
   * @param value The value to set.
   */
  public void setValue(Object value) {
    this.value = value;
  }
  
  /**
   * Is the Property in this column seachable?
   * @return
   */
  public boolean isSearchable() {
    return this.propertyDescriptor.isSearchable();
  }

  /**
   * Returns the valueInObjectTable.
   * @return Returns the valueInObjectTable.
   */
  public boolean isValueInObjectTable() {
    return valueInObjectTable;
  }

  /**
   * Sets the valueInObjectTable.
   * @param valueInObjectTable The valueInObjectTable to set.
   */
  public void setValueInObjectTable(boolean isObjectTable) {
    this.valueInObjectTable = isObjectTable;
  }

  /**
   * Returns the valueInvalid.
   * @return Returns the valueInvalid.
   */
  public boolean isValueInvalid() {
    return valueInvalid;
  }

  /**
   * Sets the valueInvalid.
   * @param valueInvalid The valueInvalid to set.
   */
  public void setValueInvalid(boolean valueInvalid) {
    this.valueInvalid = valueInvalid;
  }
  
}
