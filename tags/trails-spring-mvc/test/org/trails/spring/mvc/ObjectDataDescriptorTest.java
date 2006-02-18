package org.trails.spring.mvc;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.trails.descriptor.IPropertyDescriptor;

import junit.framework.TestCase;

import static org.easymock.classextension.EasyMock.*;

public class ObjectDataDescriptorTest extends TestCase {

  /**
   * Test method for 'org.trails.spring.mvc.ObjectDataDescriptor.setColumns(List<PropertyDataDescriptor>)'
   */
  public void testSetGetColumns() {
    ObjectDataDescriptor dataDescriptor = new ObjectDataDescriptor();
    assertNotNull(dataDescriptor.getColumns());
    assertEquals(0, dataDescriptor.getColumns().size());
    
    List<PropertyDataDescriptor> columns = new ArrayList<PropertyDataDescriptor>();
    dataDescriptor.setColumns(columns);
    assertTrue(columns.equals(dataDescriptor.getColumns()));
  }

  /**
   * Test method for 'org.trails.spring.mvc.ObjectDataDescriptor.getInstance()'
   */
  public void testGetSetInstance() {
    ObjectDataDescriptor dataDescriptor = new ObjectDataDescriptor();
    assertNull(dataDescriptor.getInstance());
    
    Object instance = new Object();
    dataDescriptor.setInstance(instance);
    assertTrue(instance.equals(dataDescriptor.getInstance()));
  }

  /**
   * Test method for 'org.trails.spring.mvc.ObjectDataDescriptor.getIdentifierValue()'
   */
  public void testGetIdentifierValue() {
    PropertyDataDescriptor propertyDataDescriptor = createMock(PropertyDataDescriptor.class);
    IPropertyDescriptor propertyDescriptor = createMock(IPropertyDescriptor.class);
    
    List<PropertyDataDescriptor> columns = new ArrayList<PropertyDataDescriptor>();
    columns.add(propertyDataDescriptor);
    
    expect(propertyDataDescriptor.getPropertyDescriptor()).andStubReturn(propertyDescriptor);
    expect(propertyDataDescriptor.getValue()).andStubReturn("1");
    expect(propertyDescriptor.isIdentifier()).andStubReturn(true);
    
    replay(propertyDataDescriptor);
    replay(propertyDescriptor);

    ObjectDataDescriptor dataDescriptor = new ObjectDataDescriptor();
    dataDescriptor.setColumns(columns);
    
    assertEquals("1", dataDescriptor.getIdentifierValue());
    
    verify(propertyDataDescriptor);
    verify(propertyDescriptor);
  }

  /**
   * Test method for 'org.trails.spring.mvc.ObjectDataDescriptor.isSelected()'
   */
  public void testIsSetSelected() {
    ObjectDataDescriptor dataDescriptor = new ObjectDataDescriptor();
    assertFalse(dataDescriptor.isSelected());
    
    dataDescriptor.setSelected(true);
    assertTrue(dataDescriptor.isSelected());
  }

}
