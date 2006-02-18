package org.trails.spring.mvc;

import org.trails.descriptor.IPropertyDescriptor;

/**
 * A <code>PropertyDataDescriptor</code> encapsulates a property and its value that needs
 * to be rendered on the view. Like the {@link org.trails.spring.mvc.ObjectDataDescriptor}
 * that has the {@link org.trails.descriptor.IClassDescriptor} as its counterpart
 * , the <code>PropertyDataDescriptor</code> has the {@link org.trails.descriptor.IPropertyDescriptor}
 * as its counterpart.
 * <p>
 * The {@link #getValue() value} of the property can also be put in
 * an {@link org.trails.spring.mvc.ObjectDataDescriptorList}. This is usefull is for instance
 * the value of this property is a collection of other objects. By putting the
 * value in an {@link org.trails.spring.mvc.ObjectDataDescriptorList} the values
 * can be rendered as an option select box in the view. To check if the {@link #getValue() value}
 * is in an{@link org.trails.spring.mvc.ObjectDataDescriptorList}the method {@link #isValueInObjectTable()}
 * can be called. That also means that if one put the value in an 
 * {@link org.trails.spring.mvc.ObjectDataDescriptorList} the indicator {@link #isValueInObjectTable()}
 * should be set to <code>true</code> via the method {@link #setValueInObjectTable(boolean)}.
 * <p>
 * There is also a need to support invalid entered values in a form. To be able to do so
 * the indicator {@link #isValueInvalid()} can be used. If this method returns <code>true</code>
 * than it can be that the {@link #getValue() value} of this <code>PropertyDataDescriptor</code> 
 * is not of the type described by the associated {@link #getPropertyDescriptor()}!
 * <br>By default a value is NOT invalid.
 * <p>
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
   *      <code>null</code> is not allowed as value for this parameter.
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
   * Returns the value or "" if the value is <code>null</code>.
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
   * @return <code>true</code> if seacrhable, <code>false</code> if not.
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
