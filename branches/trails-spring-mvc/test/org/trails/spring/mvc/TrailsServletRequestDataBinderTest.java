/*
 * Copyright 2005, Inspiring BV, the Netherlands
 *
 * info@inspiring.nl
 */

package org.trails.spring.mvc;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.reset;
import static org.easymock.classextension.EasyMock.verify;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import junit.framework.TestCase;

import org.apache.commons.collections.ArrayEnumeration;
import org.springframework.validation.BindException;
import org.trails.descriptor.DescriptorService;
import org.trails.descriptor.IClassDescriptor;
import org.trails.descriptor.IPropertyDescriptor;
import org.trails.descriptor.TrailsPropertyDescriptor;
import org.trails.persistence.PersistenceService;

public class TrailsServletRequestDataBinderTest extends TestCase {
  
  private PersistenceService mockPersistence = createMock(PersistenceService.class);
  private DescriptorService mockDescriptor =  createMock(DescriptorService.class);
  private IPropertyDescriptor mockPropertyDescriptor = createMock(IPropertyDescriptor.class);
  private IClassDescriptor mockClassDescriptor = createMock(IClassDescriptor.class);
  private IClassDescriptor mockClassDescriptor2 = createMock(IClassDescriptor.class);
  private HttpServletRequest mockRequest = createMock(HttpServletRequest.class);
  private TrailsPropertyDescriptor mockPropertyDescriptor1 = createMock(TrailsPropertyDescriptor.class);
  private TrailsPropertyDescriptor mockPropertyDescriptor2 = createMock(TrailsPropertyDescriptor.class);
  private TrailsPropertyDescriptor mockPropertyDescriptor3 = createMock(TrailsPropertyDescriptor.class);
  
  /**
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    reset(mockPersistence);
    reset(mockDescriptor);
    reset(mockPropertyDescriptor);
    reset(mockClassDescriptor);
    reset(mockClassDescriptor2);
    reset(mockRequest);
    reset(mockPropertyDescriptor1);
    reset(mockPropertyDescriptor2);
    reset(mockPropertyDescriptor3);
    
    expect(mockPropertyDescriptor.getDisplayName()).andStubReturn("DisplayName");
  }

  /**
   * Test method for TrailsServletRequestDataBinder.parseValidateAndSetParameterValueOntoInstance.
   */
  public void testParseValidateAndSetParameterValueOntoInstanceEmptyRequired() {
    TrailsServletRequestDataBinder binder = new TrailsServletRequestDataBinder(mockDescriptor, mockPersistence);
    binder.setErrors(new BindException(new Object(), "dummy"));
    
    DummyClass dummyClass = new DummyClass();
    expect(mockPropertyDescriptor.isRequired()).andReturn(Boolean.TRUE);
    
    replay(mockPropertyDescriptor);
    
    binder.parseValidateAndSetParameterValueOntoInstance(dummyClass, mockPropertyDescriptor, "");
    
    assertTrue(binder.hasErrors());
  }
  /**
   * Test method for TrailsServletRequestDataBinder.parseValidateAndSetParameterValueOntoInstance.
   */
  public void testParseValidateAndSetParameterValueOntoInstanceNullRequired() {
    TrailsServletRequestDataBinder binder = new TrailsServletRequestDataBinder(mockDescriptor, mockPersistence);
    binder.setErrors(new BindException(new Object(), "dummy"));
    
    DummyClass dummyClass = new DummyClass();

    expect(mockPropertyDescriptor.isRequired()).andReturn(Boolean.TRUE);
    
    replay(mockPropertyDescriptor);
    
    binder.parseValidateAndSetParameterValueOntoInstance(dummyClass, mockPropertyDescriptor, null);
    
    assertTrue(binder.hasErrors());
  }  
  
  /**
   * Test method for TrailsServletRequestDataBinder.parseValidateAndSetParameterValueOntoInstance.
   */
  public void testParseValidateAndSetParameterValueOntoInstanceStringValueNull() {
    TrailsServletRequestDataBinder binder = new TrailsServletRequestDataBinder(mockDescriptor, mockPersistence);
    binder.setErrors(new BindException(new Object(), "dummy"));
    
    DummyClass dummyClass = new DummyClass();
    dummyClass.setAString("XX");
    
    expect(mockPropertyDescriptor.isRequired()).andReturn(false);
    expect(mockPropertyDescriptor.getName()).andStubReturn("aString");
    expect(mockPropertyDescriptor.isString()).andStubReturn(true);

    replay(mockPropertyDescriptor);
    
    binder.parseValidateAndSetParameterValueOntoInstance(dummyClass, mockPropertyDescriptor, null);
    
    assertFalse(binder.hasErrors());
    // aString should now be made null
    assertNull(dummyClass.getAString());
  } 
  /**
   * Test method for TrailsServletRequestDataBinder.parseValidateAndSetParameterValueOntoInstance.
   */
  public void testParseValidateAndSetParameterValueOntoInstanceStringValueNotNull() {
    TrailsServletRequestDataBinder binder = new TrailsServletRequestDataBinder(mockDescriptor, mockPersistence);
    binder.setErrors(new BindException(new Object(), "dummy"));
    
    DummyClass dummyClass = new DummyClass();
    dummyClass.setAString("XX");
    
    expect(mockPropertyDescriptor.isRequired()).andReturn(false);
    expect(mockPropertyDescriptor.getName()).andStubReturn("aString");
    expect(mockPropertyDescriptor.isString()).andStubReturn(Boolean.TRUE);
    
    replay(mockPropertyDescriptor);
    
    binder.parseValidateAndSetParameterValueOntoInstance(dummyClass, mockPropertyDescriptor, "BB");
    
    assertFalse(binder.hasErrors());
    // aString should now be made null
    assertEquals("BB", dummyClass.getAString());
  }  
  
  /**
   * Test method for TrailsServletRequestDataBinder.parseValidateAndSetParameterValueOntoInstance.
   */
  public void testParseValidateAndSetParameterValueOntoInstanceIntegerValueNull() {
    TrailsServletRequestDataBinder binder = new TrailsServletRequestDataBinder(mockDescriptor, mockPersistence);
    binder.setErrors(new BindException(new Object(), "dummy"));
    
    DummyClass dummyClass = new DummyClass();
    dummyClass.setAnInteger(new Integer(3));
    
    expect(mockPropertyDescriptor.isRequired()).andReturn(false);
    expect(mockPropertyDescriptor.isString()).andReturn(false);
    expect(mockPropertyDescriptor.getName()).andStubReturn("anInteger");
    expect(mockPropertyDescriptor.getPropertyType()).andStubReturn(Integer.class);
    
    replay(mockPropertyDescriptor);
    
    binder.parseValidateAndSetParameterValueOntoInstance(dummyClass, mockPropertyDescriptor, null);
    
    assertFalse(binder.hasErrors());
    // aString should now be made null
    assertNull(dummyClass.getAnInteger());
  } 
  /**
   * Test method for TrailsServletRequestDataBinder.parseValidateAndSetParameterValueOntoInstance.
   */
  public void testParseValidateAndSetParameterValueOntoInstanceIntegerValueNotNull() {
    TrailsServletRequestDataBinder binder = new TrailsServletRequestDataBinder(mockDescriptor, mockPersistence);
    binder.setErrors(new BindException(new Object(), "dummy"));
    
    DummyClass dummyClass = new DummyClass();
    
    expect(mockPropertyDescriptor.isRequired()).andReturn(false);
    expect(mockPropertyDescriptor.isString()).andReturn(false);
    expect(mockPropertyDescriptor.getName()).andStubReturn("anInteger");
    expect(mockPropertyDescriptor.getPropertyType()).andStubReturn(Integer.class);
    
    replay(mockPropertyDescriptor);
    
    binder.parseValidateAndSetParameterValueOntoInstance(dummyClass, mockPropertyDescriptor, "3");
    
    assertFalse(binder.hasErrors());
    // aString should now be made null
    assertEquals(3, dummyClass.getAnInteger().intValue());
  }   
  /**
   * Test method for TrailsServletRequestDataBinder.parseValidateAndSetParameterValueOntoInstance.
   */
  public void testParseValidateAndSetParameterValueOntoInstanceIntValueNull() {
    TrailsServletRequestDataBinder binder = new TrailsServletRequestDataBinder(mockDescriptor, mockPersistence);
    binder.setErrors(new BindException(new Object(), "dummy"));
    DummyClass dummyClass = new DummyClass();
    
    expect(mockPropertyDescriptor.isRequired()).andReturn(false);
    expect(mockPropertyDescriptor.isString()).andReturn(false);
    expect(mockPropertyDescriptor.getName()).andStubReturn("primInt");
    expect(mockPropertyDescriptor.getPropertyType()).andStubReturn(int.class);
    
    replay(mockPropertyDescriptor);
    
    binder.parseValidateAndSetParameterValueOntoInstance(dummyClass, mockPropertyDescriptor, null);
    
    assertFalse(binder.hasErrors());
    // aString should now be made null
    assertEquals(0, dummyClass.getPrimInt());
  } 
  /**
   * Test method for TrailsServletRequestDataBinder.parseValidateAndSetParameterValueOntoInstance.
   */
  public void testParseValidateAndSetParameterValueOntoInstanceIntValueNotNull() {
    TrailsServletRequestDataBinder binder = new TrailsServletRequestDataBinder(mockDescriptor, mockPersistence);
    binder.setErrors(new BindException(new Object(), "dummy"));
    
    DummyClass dummyClass = new DummyClass();
    
    expect(mockPropertyDescriptor.isRequired()).andReturn(false);
    expect(mockPropertyDescriptor.isString()).andReturn(false);
    expect(mockPropertyDescriptor.getName()).andStubReturn("primInt");
    expect(mockPropertyDescriptor.getPropertyType()).andStubReturn(int.class);
    
    replay(mockPropertyDescriptor);
    
    binder.parseValidateAndSetParameterValueOntoInstance(dummyClass, mockPropertyDescriptor, "3");
    
    assertFalse(binder.hasErrors());
    // aString should now be made null
    assertEquals(3, dummyClass.getPrimInt());
  }    
  
  /**
   * Test method for TrailsServletRequestDataBinder.parseValidateAndSetParameterValueOntoInstance.
   */
  public void testParseValidateAndSetParameterValueOntoInstanceLongValueNull() {
    TrailsServletRequestDataBinder binder = new TrailsServletRequestDataBinder(mockDescriptor, mockPersistence);
    binder.setErrors(new BindException(new Object(), "dummy"));
    
    DummyClass dummyClass = new DummyClass();
    dummyClass.setALong(new Long(3));
    expect(mockPropertyDescriptor.isRequired()).andReturn(false);
    expect(mockPropertyDescriptor.isString()).andReturn(false);
    expect(mockPropertyDescriptor.getName()).andStubReturn("aLong");
    expect(mockPropertyDescriptor.getPropertyType()).andStubReturn(Long.class);
    
    replay(mockPropertyDescriptor);
    
    binder.parseValidateAndSetParameterValueOntoInstance(dummyClass, mockPropertyDescriptor, null);
    
    assertFalse(binder.hasErrors());
    // aString should now be made null
    assertNull(dummyClass.getALong());
  } 
  /**
   * Test method for TrailsServletRequestDataBinder.parseValidateAndSetParameterValueOntoInstance.
   */
  public void testParseValidateAndSetParameterValueOntoInstanceLongValueNotNull() {
    TrailsServletRequestDataBinder binder = new TrailsServletRequestDataBinder(mockDescriptor, mockPersistence);
    binder.setErrors(new BindException(new Object(), "dummy"));
    
    DummyClass dummyClass = new DummyClass();
    
    expect(mockPropertyDescriptor.isRequired()).andReturn(false);
    expect(mockPropertyDescriptor.isString()).andReturn(false);
    expect(mockPropertyDescriptor.getName()).andStubReturn("aLong");
    expect(mockPropertyDescriptor.getPropertyType()).andStubReturn(Long.class);
    
    replay(mockPropertyDescriptor);
    
    binder.parseValidateAndSetParameterValueOntoInstance(dummyClass, mockPropertyDescriptor, "3");
    
    assertFalse(binder.hasErrors());
    // aString should now be made null
    assertEquals(3, dummyClass.getALong().longValue());
  }   
  /**
   * Test method for TrailsServletRequestDataBinder.parseValidateAndSetParameterValueOntoInstance.
   */
  public void testParseValidateAndSetParameterValueOntoInstanceLongPrimitiveValueNull() {
    TrailsServletRequestDataBinder binder = new TrailsServletRequestDataBinder(mockDescriptor, mockPersistence);
    binder.setErrors(new BindException(new Object(), "dummy"));
    
    DummyClass dummyClass = new DummyClass();
    dummyClass.setPrimLong(new Long(3));
    expect(mockPropertyDescriptor.isRequired()).andReturn(false);
    expect(mockPropertyDescriptor.isString()).andReturn(false);
    expect(mockPropertyDescriptor.getName()).andStubReturn("primLong");
    expect(mockPropertyDescriptor.getPropertyType()).andStubReturn(long.class);
    
    replay(mockPropertyDescriptor);
    
    binder.parseValidateAndSetParameterValueOntoInstance(dummyClass, mockPropertyDescriptor, null);
    
    assertFalse(binder.hasErrors());
    // aString should now be made null
    assertEquals(0, dummyClass.getPrimInt());
  } 
  /**
   * Test method for TrailsServletRequestDataBinder.parseValidateAndSetParameterValueOntoInstance.
   */
  public void testParseValidateAndSetParameterValueOntoInstanceLongPrimitiveValueNotNull() {
    TrailsServletRequestDataBinder binder = new TrailsServletRequestDataBinder(mockDescriptor, mockPersistence);
    binder.setErrors(new BindException(new Object(), "dummy"));
    
    DummyClass dummyClass = new DummyClass();
    
    expect(mockPropertyDescriptor.isRequired()).andReturn(false);
    expect(mockPropertyDescriptor.isString()).andReturn(false);
    expect(mockPropertyDescriptor.getName()).andStubReturn("primLong");
    expect(mockPropertyDescriptor.getPropertyType()).andStubReturn(long.class);
    
    replay(mockPropertyDescriptor);
    
    
    binder.parseValidateAndSetParameterValueOntoInstance(dummyClass, mockPropertyDescriptor, "3");
    
    assertFalse(binder.hasErrors());
    // aString should now be made null
    assertEquals(3, dummyClass.getPrimLong());
  }
  
  /**
   * Test method for TrailsServletRequestDataBinder.parseValidateAndSetParameterValueOntoInstance.
   */
  public void testParseValidateAndSetParameterValueOntoInstanceBigDecimalValueNull() {
    TrailsServletRequestDataBinder binder = new TrailsServletRequestDataBinder(mockDescriptor, mockPersistence);
    binder.setErrors(new BindException(new Object(), "dummy"));
    
    DummyClass dummyClass = new DummyClass();
    dummyClass.setBigDecimal(new BigDecimal(3));
    
    expect(mockPropertyDescriptor.isRequired()).andReturn(false);
    expect(mockPropertyDescriptor.isString()).andReturn(false);
    expect(mockPropertyDescriptor.getName()).andStubReturn("BigDecimal");
    expect(mockPropertyDescriptor.getPropertyType()).andStubReturn(BigDecimal.class);
    
    replay(mockPropertyDescriptor);
        
    binder.parseValidateAndSetParameterValueOntoInstance(dummyClass, mockPropertyDescriptor, null);
    
    assertFalse(binder.hasErrors());
    // aString should now be made null
    assertNull(dummyClass.getBigDecimal());
  } 
  /**
   * Test method for TrailsServletRequestDataBinder.parseValidateAndSetParameterValueOntoInstance.
   */
  public void testParseValidateAndSetParameterValueOntoInstanceBigDecimalValueNotNull() {
    TrailsServletRequestDataBinder binder = new TrailsServletRequestDataBinder(mockDescriptor, mockPersistence);
    binder.setErrors(new BindException(new Object(), "dummy"));
    
    DummyClass dummyClass = new DummyClass();
    
    expect(mockPropertyDescriptor.isRequired()).andReturn(false);
    expect(mockPropertyDescriptor.isString()).andReturn(false);
    expect(mockPropertyDescriptor.getName()).andStubReturn("bigDecimal");
    expect(mockPropertyDescriptor.getPropertyType()).andStubReturn(BigDecimal.class);
    
    replay(mockPropertyDescriptor);
    
    binder.parseValidateAndSetParameterValueOntoInstance(dummyClass, mockPropertyDescriptor, "3");
    
    assertFalse(binder.hasErrors());
    // aString should now be made null
    assertEquals(new BigDecimal("3"), dummyClass.getBigDecimal());
  } 
  
  /**
   * Test method for TrailsServletRequestDataBinder.parseValidateAndSetParameterValueOntoInstance.
   */
  public void testParseValidateAndSetParameterValueOntoInstanceDateValueNull() {
    TrailsServletRequestDataBinder binder = new TrailsServletRequestDataBinder(mockDescriptor, mockPersistence);
    binder.setErrors(new BindException(new Object(), "dummy"));
    
    DummyClass dummyClass = new DummyClass();
    dummyClass.setADate(new Date());
    
    expect(mockPropertyDescriptor.isRequired()).andReturn(false);
    expect(mockPropertyDescriptor.isString()).andReturn(false);
    expect(mockPropertyDescriptor.isDate()).andReturn(true);
    expect(mockPropertyDescriptor.getName()).andStubReturn("aDate");
    expect(mockPropertyDescriptor.getPropertyType()).andStubReturn(Date.class);
    expect(mockPropertyDescriptor.getFormat()).andStubReturn("dd-MM-yyyy");
    
    replay(mockPropertyDescriptor);
    
    binder.parseValidateAndSetParameterValueOntoInstance(dummyClass, mockPropertyDescriptor, null);
    
    assertFalse(binder.hasErrors());
    // aString should now be made null
    assertNull(dummyClass.getADate());
  } 
  
  /**
   * Test method for TrailsServletRequestDataBinder.parseValidateAndSetParameterValueOntoInstance.
   */
  public void testParseValidateAndSetParameterValueOntoInstanceDateValueNotNull() throws Exception {
    Date date = new SimpleDateFormat("dd-MM-yyyy").parse("01-01-2005");
    TrailsServletRequestDataBinder binder = new TrailsServletRequestDataBinder(mockDescriptor, mockPersistence);
    binder.setErrors(new BindException(new Object(), "dummy"));
    
    DummyClass dummyClass = new DummyClass();
    
    expect(mockPropertyDescriptor.isRequired()).andReturn(false);
    expect(mockPropertyDescriptor.isString()).andReturn(false);
    expect(mockPropertyDescriptor.isDate()).andReturn(true);
    expect(mockPropertyDescriptor.getName()).andStubReturn("aDate");
    expect(mockPropertyDescriptor.getPropertyType()).andStubReturn(Date.class);
    expect(mockPropertyDescriptor.getFormat()).andStubReturn("dd-MM-yyyy");
    
    replay(mockPropertyDescriptor);
    
    binder.parseValidateAndSetParameterValueOntoInstance(dummyClass, mockPropertyDescriptor, "01-01-2005");
    
    assertFalse(binder.hasErrors());
    // aString should now be made null
    assertEquals(date, dummyClass.getADate());
  }   
  /**
   * Test method for TrailsServletRequestDataBinder.parseValidateAndSetParameterValueOntoInstance.
   */
  public void testParseValidateAndSetParameterValueOntoInstanceDateValueParseException() throws Exception {
    TrailsServletRequestDataBinder binder = new TrailsServletRequestDataBinder(mockDescriptor, mockPersistence);
    binder.setErrors(new BindException(new Object(), "dummy"));
    
    DummyClass dummyClass = new DummyClass();
    
    expect(mockPropertyDescriptor.isRequired()).andReturn(false);
    expect(mockPropertyDescriptor.isString()).andReturn(false);
    expect(mockPropertyDescriptor.isDate()).andReturn(true);
    expect(mockPropertyDescriptor.getName()).andStubReturn("aDate");
    expect(mockPropertyDescriptor.getPropertyType()).andStubReturn(Date.class);
    expect(mockPropertyDescriptor.getFormat()).andStubReturn("dd-MM-yyyy");
    
    replay(mockPropertyDescriptor);
    
    binder.parseValidateAndSetParameterValueOntoInstance(dummyClass, mockPropertyDescriptor, "xx-01-2005");
    
    assertTrue(binder.hasErrors());
    // aString should now be made null
    assertNull(dummyClass.getADate());
  }   
  /**
   * Test method for TrailsServletRequestDataBinder.parseValidateAndSetParameterValueOntoInstance.
   */
  public void testParseValidateAndSetParameterValueOntoInstanceLongValueNotANumber() {
    TrailsServletRequestDataBinder binder = new TrailsServletRequestDataBinder(mockDescriptor, mockPersistence);
    binder.setErrors(new BindException(new Object(), "dummy"));
    
    DummyClass dummyClass = new DummyClass();
    
    expect(mockPropertyDescriptor.isRequired()).andReturn(false);
    expect(mockPropertyDescriptor.isString()).andReturn(false);
    expect(mockPropertyDescriptor.getName()).andStubReturn("aLong");
    expect(mockPropertyDescriptor.getPropertyType()).andStubReturn(Long.class);
    
    replay(mockPropertyDescriptor);
    
    binder.parseValidateAndSetParameterValueOntoInstance(dummyClass, mockPropertyDescriptor, "XX");
    
    assertTrue(binder.hasErrors());
    // aString should now be made null
    assertNull(dummyClass.getALong());
  }     
  /**
   * Test method for TrailsServletRequestDataBinder.bind(HttpServletRequest request, IClassDescriptor classDescriptor, Object existingInstance) 
   * and TrailsServletRequestDataBinder.bind(HttpServletRequest request, IClassDescriptor classDescriptor).
   *
   */
  public void testBind() {
    TrailsServletRequestDataBinder binderToTest = new TrailsServletRequestDataBinder(mockDescriptor, mockPersistence) {

      /**
       * @see nl.inspiring.trails.spring.mvc.TrailsServletRequestDataBinder#parseValidateAndSetParameterValueOntoInstance(java.lang.Object, org.trails.descriptor.IPropertyDescriptor, java.lang.String)
       */
      @Override
      protected void parseValidateAndSetParameterValueOntoInstance(Object instance, IPropertyDescriptor descriptor, String value) {
        // no implementation as it is already tested.
        ;
      }
      
    };
    DummyClass dummyClass = new DummyClass();
    
    expect(mockClassDescriptor.getType()).andStubReturn(DummyClass2.class);
    ArrayEnumeration enumeration = new ArrayEnumeration(new String[] {"aString", "dummyClass.anInteger"});
    expect(mockRequest.getParameterNames()).andStubReturn(enumeration);
    expect(mockClassDescriptor.getPropertyDescriptor("aString")).andReturn(mockPropertyDescriptor1);
    expect(mockClassDescriptor.getPropertyDescriptor("dummyClass")).andReturn(mockPropertyDescriptor2);

    
    expect(mockPropertyDescriptor1.isCollection()).andStubReturn(false);
    expect(mockPropertyDescriptor1.isObjectReference()).andStubReturn(false);
    expect(mockPropertyDescriptor1.isIdentifier()).andStubReturn(false);
    
    expect(mockPropertyDescriptor2.isCollection()).andStubReturn(false);
    expect(mockPropertyDescriptor2.isObjectReference()).andStubReturn(true);
    expect(mockPropertyDescriptor2.isIdentifier()).andStubReturn(false);
    
    expect(mockPropertyDescriptor2.getPropertyType()).andStubReturn(DummyClass.class);
    expect(mockDescriptor.getClassDescriptor(DummyClass.class)).andReturn(mockClassDescriptor2);
    
    expect(mockClassDescriptor2.getIdentifierDescriptor()).andReturn(mockPropertyDescriptor3);
    
    expect(mockPropertyDescriptor3.getPropertyType()).andStubReturn(Integer.class);
    expect(mockRequest.getParameter("aString")).andReturn("2");
    expect(mockRequest.getParameter("dummyClass.anInteger")).andReturn("4");
    
    expect(mockPersistence.getInstance(DummyClass.class, new Integer(4))).andReturn(dummyClass);
    
    replay(mockClassDescriptor);
    replay(mockClassDescriptor2);
    replay(mockPropertyDescriptor1);
    replay(mockPropertyDescriptor2);
    replay(mockPropertyDescriptor3);
    replay(mockDescriptor);
    replay(mockPersistence);
    replay(mockRequest);

    // TEST IT
    Object boundedInstance = binderToTest.bind(mockRequest, mockClassDescriptor);
    
    assertNotNull(boundedInstance);
    assertTrue(boundedInstance.getClass().equals(DummyClass2.class));
    assertTrue(((DummyClass2)boundedInstance).getDummyClass().equals(dummyClass));

    verify(mockClassDescriptor);
    verify(mockPropertyDescriptor1);
    verify(mockPropertyDescriptor2);
    verify(mockDescriptor);
    verify(mockPersistence);
    verify(mockRequest);

  }
  // ====================================================================
  // Static test class
  // ====================================================================
  public static class DummyClass2 {
    private String aString;
    private DummyClass dummyClass;

    /**
     * Returns the aString.
     * @return Returns the aString.
     */
    public String getAString() {
      return aString;
    }
    /**
     * Sets the aString.
     * @param string The aString to set.
     */
    public void setAString(String string) {
      aString = string;
    }
    /**
     * Returns the dummyClass.
     * @return Returns the dummyClass.
     */
    public DummyClass getDummyClass() {
      return dummyClass;
    }
    /**
     * Sets the dummyClass.
     * @param dummyClass The dummyClass to set.
     */
    public void setDummyClass(DummyClass dummyClass) {
      this.dummyClass = dummyClass;
    }
  }
  
  
  
  public static class DummyClass {
    private String aString;
    private Long aLong;
    private Integer anInteger;
    private long primLong;
    private int primInt;
    private Date aDate;
    private BigDecimal bigDecimal;
    private DummyClass dummyClass;
    /**
     * Returns the aDate.
     * @return Returns the aDate.
     */
    public Date getADate() {
      return aDate;
    }
    /**
     * Sets the aDate.
     * @param date The aDate to set.
     */
    public void setADate(Date date) {
      aDate = date;
    }
    /**
     * Returns the aLong.
     * @return Returns the aLong.
     */
    public Long getALong() {
      return aLong;
    }
    /**
     * Sets the aLong.
     * @param long1 The aLong to set.
     */
    public void setALong(Long long1) {
      aLong = long1;
    }
    /**
     * Returns the anInteger.
     * @return Returns the anInteger.
     */
    public Integer getAnInteger() {
      return anInteger;
    }
    /**
     * Sets the anInteger.
     * @param anInteger The anInteger to set.
     */
    public void setAnInteger(Integer anInteger) {
      this.anInteger = anInteger;
    }
    /**
     * Returns the aString.
     * @return Returns the aString.
     */
    public String getAString() {
      return aString;
    }
    /**
     * Sets the aString.
     * @param string The aString to set.
     */
    public void setAString(String string) {
      aString = string;
    }
    /**
     * Returns the bigDecimal.
     * @return Returns the bigDecimal.
     */
    public BigDecimal getBigDecimal() {
      return bigDecimal;
    }
    /**
     * Sets the bigDecimal.
     * @param bigDecimal The bigDecimal to set.
     */
    public void setBigDecimal(BigDecimal bigDecimal) {
      this.bigDecimal = bigDecimal;
    }
    /**
     * Returns the primInt.
     * @return Returns the primInt.
     */
    public int getPrimInt() {
      return primInt;
    }
    /**
     * Sets the primInt.
     * @param primInt The primInt to set.
     */
    public void setPrimInt(int primInt) {
      this.primInt = primInt;
    }
    /**
     * Returns the primLong.
     * @return Returns the primLong.
     */
    public long getPrimLong() {
      return primLong;
    }
    /**
     * Sets the primLong.
     * @param primLong The primLong to set.
     */
    public void setPrimLong(long primLong) {
      this.primLong = primLong;
    }
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
      return "DummyClass";
    }
    
  }  

}
