package org.trails.spring.mvc;

import org.trails.descriptor.IPropertyDescriptor;

import junit.framework.TestCase;
import static org.easymock.EasyMock.*;
public class PropertyDataDescriptorTest extends TestCase {

  /**
   * Test method for 'org.trails.spring.mvc.PropertyDataDescriptor.getPropertyDescriptor()'
   */
  public void testGetPropertyDescriptor() {
    IPropertyDescriptor propertyDescriptor = createMock(IPropertyDescriptor.class);
    
    PropertyDataDescriptor propertyDataDescriptor = new PropertyDataDescriptor(propertyDescriptor);
    
    replay(propertyDescriptor);
    
    assertNotNull(propertyDataDescriptor.getPropertyDescriptor());
    // Use ==  because it should be referencing to the same object, otherwise
    // the equal of the propertyDescriptor might be called...
    assertTrue(propertyDescriptor == propertyDataDescriptor.getPropertyDescriptor());
    
    verify(propertyDescriptor);
  }

  /**
   * Test method for 'org.trails.spring.mvc.PropertyDataDescriptor.getValue()'
   */
  public void testGetSetValue() {
    IPropertyDescriptor propertyDescriptor = createMock(IPropertyDescriptor.class);
    PropertyDataDescriptor propertyDataDescriptor = new PropertyDataDescriptor(propertyDescriptor);
    
    assertNotNull(propertyDataDescriptor.getValue());
    assertEquals("", propertyDataDescriptor.getValue());
    
    replay(propertyDescriptor);
    
    propertyDataDescriptor.setValue("2");
    assertEquals("2", propertyDataDescriptor.getValue());
    
    verify(propertyDescriptor);
  }

  /**
   * Test method for 'org.trails.spring.mvc.PropertyDataDescriptor.isSearchable()'
   */
  public void testIsSearchable() {
    IPropertyDescriptor propertyDescriptor = createMock(IPropertyDescriptor.class);
       
    PropertyDataDescriptor propertyDataDescriptor = new PropertyDataDescriptor(propertyDescriptor);
    expect(propertyDescriptor.isSearchable()).andStubReturn(true);
    
    replay(propertyDescriptor);
    
    assertTrue(propertyDataDescriptor.isSearchable());
    
    verify(propertyDescriptor);
  }

  /**
   * Test method for 'org.trails.spring.mvc.PropertyDataDescriptor.isValueInObjectTable()'
   */
  public void testIsValueInObjectTable() {
    IPropertyDescriptor propertyDescriptor = createMock(IPropertyDescriptor.class);
    
    PropertyDataDescriptor propertyDataDescriptor = new PropertyDataDescriptor(propertyDescriptor);
    assertFalse(propertyDataDescriptor.isValueInObjectTable());
    
    propertyDataDescriptor.setValueInObjectTable(true);
    assertTrue(propertyDataDescriptor.isValueInObjectTable());
  }

  /**
   * Test method for 'org.trails.spring.mvc.PropertyDataDescriptor.isValueInvalid()'
   */
  public void testIsValueInvalid() {
    IPropertyDescriptor propertyDescriptor = createMock(IPropertyDescriptor.class);
    
    PropertyDataDescriptor propertyDataDescriptor = new PropertyDataDescriptor(propertyDescriptor);
    assertFalse(propertyDataDescriptor.isValueInvalid());
    
    propertyDataDescriptor.setValueInvalid(true);
    assertTrue(propertyDataDescriptor.isValueInvalid());
  }

}
