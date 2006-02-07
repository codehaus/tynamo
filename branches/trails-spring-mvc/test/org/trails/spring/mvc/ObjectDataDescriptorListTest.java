package org.trails.spring.mvc;

import static org.easymock.EasyMock.*;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.trails.descriptor.IClassDescriptor;
import org.trails.descriptor.IPropertyDescriptor;

import sun.security.krb5.internal.bj;

public class ObjectDataDescriptorListTest extends TestCase {

  /** The class descriptor. */
  private IClassDescriptor classDescriptor = createMock(IClassDescriptor.class);
  /** The property descriptor. */
  private IPropertyDescriptor prop1 = createMock(IPropertyDescriptor.class);
  /** the list of propertydescriptors. */
  private List props = Arrays.asList(new IPropertyDescriptor[] {prop1});
  
  
  
  @Override
  protected void setUp() throws Exception {
    reset(classDescriptor);
    reset(prop1);
  }

  /**
   * Test method for 'org.trails.spring.mvc.ObjectDataDescriptorList.ObjectDataDescriptorList(IClassDescriptor)'
   */
  public void testObjectDataDescriptorListIClassDescriptor() {
    addBehaviourForMocks();
    
    ObjectDataDescriptorList dataDescriptorList = new ObjectDataDescriptorList(classDescriptor);
    
    assertTrue(dataDescriptorList.getColumnNames().contains(prop1));
    verifyMocks();
  }

  /**
   * Test method for 'org.trails.spring.mvc.ObjectDataDescriptorList.ObjectDataDescriptorList(IClassDescriptor, int, int)'
   */
  public void testObjectDataDescriptorListIClassDescriptorIntInt() {
    addBehaviourForMocks();
    
    ObjectDataDescriptorList dataDescriptorList = new ObjectDataDescriptorList(classDescriptor, 2, 3);
    
    assertTrue(dataDescriptorList.getColumnNames().contains(prop1));
    assertTrue(dataDescriptorList.getClassDescriptor().equals(classDescriptor));
    assertEquals(2, dataDescriptorList.getCurrentPageNumber());
    assertEquals(3, dataDescriptorList.getTotalNumberOfPages());
    
    verifyMocks();
  }

  /**
   * Test method for 'org.trails.spring.mvc.ObjectDataDescriptorList.ObjectDataDescriptorList(List<T>, IClassDescriptor) <T>'
   */
  public void testObjectDataDescriptorListListIClassDescriptor() {
    // needed for testing
    TestBean bean1 = new TestBean();
    TestBean bean2 = new TestBean();
    List beans = Arrays.asList(new TestBean[] {bean1, bean2});
    
    addBehaviourForMocks();
    
    ObjectDataDescriptorList dataDescriptorList = new ObjectDataDescriptorList(beans, classDescriptor);
    
    assertTrue(dataDescriptorList.getColumnNames().contains(prop1));
    assertTrue(dataDescriptorList.getClassDescriptor().equals(classDescriptor));
    assertNotNull(dataDescriptorList.getRows());
    assertEquals(2, dataDescriptorList.getRows().size());
    
    verifyMocks();
  }

  /**
   * Test method for 'org.trails.spring.mvc.ObjectDataDescriptorList.ObjectDataDescriptorList(List<T>, IClassDescriptor, Object) <T>'
   */
  public void testObjectDataDescriptorListListIClassDescriptorObject() {
    // needed for testing
    TestBean bean1 = new TestBean();
    TestBean bean2 = new TestBean();
    List beans = Arrays.asList(new TestBean[] {bean1, bean2});
    
    addBehaviourForMocks();
    
    ObjectDataDescriptorList dataDescriptorList = new ObjectDataDescriptorList(beans, classDescriptor, bean2);
    
    assertTrue(dataDescriptorList.getColumnNames().contains(prop1));
    assertTrue(dataDescriptorList.getClassDescriptor().equals(classDescriptor));
    assertNotNull(dataDescriptorList.getRows());
    assertEquals(2, dataDescriptorList.getRows().size());
    // verify that the selectedInstance is actually set.
    for (Iterator iter = dataDescriptorList.getRows().iterator(); iter.hasNext();) {
      ObjectDataDescriptor dataDescriptor = (ObjectDataDescriptor) iter.next();
      if (dataDescriptor.isSelected()) {
        assertEquals(bean2, dataDescriptor.getInstance());
      }
    }
    verifyMocks();
  }

  /**
   * Adds behaviour for the mocks.
   *
   */
  private void addBehaviourForMocks() {
    expect(classDescriptor.getPropertyDescriptors()).andStubReturn(props);
    expect(prop1.getName()).andStubReturn("name1");
    
    replay(classDescriptor);
    replay(prop1);
  }
  
  /**
   * Verify behaviour of the mocks.
   *
   */
  private void verifyMocks() {
    
    verify(classDescriptor);
    verify(prop1);
  }  
  
  /*
   * Needed for testing.
   */
  public static class TestBean {
    private String name1;

    public String getName1() {
      return name1;
    }

    public void setName1(String name1) {
      this.name1 = name1;
    }
  }
}
