/*
 * Copyright 2005, Inspiring BV, the Netherlands
 *
 * info@inspiring.nl
 */

package org.trails.spring.mvc;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import junit.framework.TestCase;

import org.apache.commons.collections.ArrayEnumeration;
import org.easymock.MockControl;
import org.easymock.classextension.MockClassControl;
import org.springframework.validation.BindException;
import org.trails.descriptor.DescriptorService;
import org.trails.descriptor.IClassDescriptor;
import org.trails.descriptor.IPropertyDescriptor;
import org.trails.descriptor.TrailsPropertyDescriptor;
import org.trails.persistence.PersistenceService;

public class TrailsServletRequestDataBinderTest extends TestCase {
  


  private MockControl controlPersistence = MockControl.createControl(PersistenceService.class);
  private PersistenceService mockPersistence = (PersistenceService) controlPersistence.getMock();
  
  private MockControl controlDescriptor = MockControl.createControl(DescriptorService.class);
  private DescriptorService mockDescriptor = (DescriptorService) controlDescriptor.getMock(); 
  
  private MockControl controlPropertyDescriptor = MockControl.createControl(IPropertyDescriptor.class);
  private IPropertyDescriptor mockPropertyDescriptor = (IPropertyDescriptor) controlPropertyDescriptor.getMock();
  
  private MockControl controlClassDescriptor = MockControl.createControl(IClassDescriptor.class);
  private IClassDescriptor mockClassDescriptor = (IClassDescriptor) controlClassDescriptor.getMock();
  
  private MockControl controlClassDescriptor2 = MockControl.createControl(IClassDescriptor.class);
  private IClassDescriptor mockClassDescriptor2 = (IClassDescriptor) controlClassDescriptor2.getMock();  
  
  private MockControl controlRequest = MockControl.createControl(HttpServletRequest.class);
  private HttpServletRequest mockRequest = (HttpServletRequest) controlRequest.getMock();
  
  private MockControl controlPropertyDescriptor1 = MockClassControl.createControl(TrailsPropertyDescriptor.class);
  private TrailsPropertyDescriptor mockPropertyDescriptor1 = (TrailsPropertyDescriptor) controlPropertyDescriptor1.getMock();
  
  private MockControl controlPropertyDescriptor2 = MockClassControl.createControl(TrailsPropertyDescriptor.class);
  private TrailsPropertyDescriptor mockPropertyDescriptor2 = (TrailsPropertyDescriptor) controlPropertyDescriptor2.getMock();

  private MockControl controlPropertyDescriptor3 = MockClassControl.createControl(TrailsPropertyDescriptor.class);
  private TrailsPropertyDescriptor mockPropertyDescriptor3 = (TrailsPropertyDescriptor) controlPropertyDescriptor3.getMock();
  
  /**
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    controlPersistence.reset();
    controlDescriptor.reset();
    controlPropertyDescriptor.reset();
    controlPropertyDescriptor1.reset();
    controlPropertyDescriptor2.reset();
    controlPropertyDescriptor3.reset();
    controlClassDescriptor2.reset();
    controlClassDescriptor.reset();
    controlRequest.reset();
    
    mockPropertyDescriptor.getDisplayName();
    controlPropertyDescriptor.setDefaultReturnValue("DisplayName");
  }

  /**
   * Test method for TrailsServletRequestDataBinder.parseValidateAndSetParameterValueOntoInstance.
   */
  public void testParseValidateAndSetParameterValueOntoInstanceEmptyRequired() {
    TrailsServletRequestDataBinder binder = new TrailsServletRequestDataBinder(mockDescriptor, mockPersistence);
    binder.setErrors(new BindException(new Object(), "dummy"));
    
    DummyClass dummyClass = new DummyClass();
    mockPropertyDescriptor.isRequired();
    controlPropertyDescriptor.setReturnValue(true);
    
    controlPropertyDescriptor.replay();
    
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
    mockPropertyDescriptor.isRequired();
    controlPropertyDescriptor.setReturnValue(true);
    
    controlPropertyDescriptor.replay();
    
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
    
    mockPropertyDescriptor.isRequired();
    controlPropertyDescriptor.setReturnValue(false);
    mockPropertyDescriptor.getName();
    controlPropertyDescriptor.setDefaultReturnValue("aString");
    mockPropertyDescriptor.isString();
    controlPropertyDescriptor.setDefaultReturnValue(true);
    
    controlPropertyDescriptor.replay();
    
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
    
    mockPropertyDescriptor.isRequired();
    controlPropertyDescriptor.setReturnValue(false);
    mockPropertyDescriptor.getName();
    controlPropertyDescriptor.setDefaultReturnValue("aString");
    mockPropertyDescriptor.isString();
    controlPropertyDescriptor.setDefaultReturnValue(true);
    
    controlPropertyDescriptor.replay();
    
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
    mockPropertyDescriptor.isRequired();
    controlPropertyDescriptor.setReturnValue(false);
    mockPropertyDescriptor.isString();
    controlPropertyDescriptor.setReturnValue(false);
    mockPropertyDescriptor.getName();
    controlPropertyDescriptor.setDefaultReturnValue("anInteger");
    mockPropertyDescriptor.getPropertyType();
    controlPropertyDescriptor.setDefaultReturnValue(Integer.class);
    
    controlPropertyDescriptor.replay();
    
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
    
    mockPropertyDescriptor.isRequired();
    controlPropertyDescriptor.setReturnValue(false);
    mockPropertyDescriptor.isString();
    controlPropertyDescriptor.setReturnValue(false);
    mockPropertyDescriptor.getName();
    controlPropertyDescriptor.setDefaultReturnValue("anInteger");
    mockPropertyDescriptor.getPropertyType();
    controlPropertyDescriptor.setDefaultReturnValue(Integer.class);
    
    controlPropertyDescriptor.replay();
    
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
    dummyClass.setPrimInt(new Integer(3));
    mockPropertyDescriptor.isRequired();
    controlPropertyDescriptor.setReturnValue(false);
    mockPropertyDescriptor.isString();
    controlPropertyDescriptor.setReturnValue(false);
    mockPropertyDescriptor.getName();
    controlPropertyDescriptor.setDefaultReturnValue("primInt");
    mockPropertyDescriptor.getPropertyType();
    controlPropertyDescriptor.setDefaultReturnValue(int.class);
    
    controlPropertyDescriptor.replay();
    
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
    
    mockPropertyDescriptor.isRequired();
    controlPropertyDescriptor.setReturnValue(false);
    mockPropertyDescriptor.isString();
    controlPropertyDescriptor.setReturnValue(false);
    mockPropertyDescriptor.getName();
    controlPropertyDescriptor.setDefaultReturnValue("primInt");
    mockPropertyDescriptor.getPropertyType();
    controlPropertyDescriptor.setDefaultReturnValue(int.class);
    
    controlPropertyDescriptor.replay();
    
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
    mockPropertyDescriptor.isRequired();
    controlPropertyDescriptor.setReturnValue(false);
    mockPropertyDescriptor.isString();
    controlPropertyDescriptor.setReturnValue(false);
    mockPropertyDescriptor.getName();
    controlPropertyDescriptor.setDefaultReturnValue("aLong");
    mockPropertyDescriptor.getPropertyType();
    controlPropertyDescriptor.setDefaultReturnValue(Long.class);
    
    controlPropertyDescriptor.replay();
    
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
    
    mockPropertyDescriptor.isRequired();
    controlPropertyDescriptor.setReturnValue(false);
    mockPropertyDescriptor.isString();
    controlPropertyDescriptor.setReturnValue(false);
    mockPropertyDescriptor.getName();
    controlPropertyDescriptor.setDefaultReturnValue("aLong");
    mockPropertyDescriptor.getPropertyType();
    controlPropertyDescriptor.setDefaultReturnValue(Long.class);
    
    controlPropertyDescriptor.replay();
    
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
    dummyClass.setPrimLong(new Integer(3));
    mockPropertyDescriptor.isRequired();
    controlPropertyDescriptor.setReturnValue(false);
    mockPropertyDescriptor.isString();
    controlPropertyDescriptor.setReturnValue(false);
    mockPropertyDescriptor.getName();
    controlPropertyDescriptor.setDefaultReturnValue("primLong");
    mockPropertyDescriptor.getPropertyType();
    controlPropertyDescriptor.setDefaultReturnValue(long.class);
    
    controlPropertyDescriptor.replay();
    
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
    
    mockPropertyDescriptor.isRequired();
    controlPropertyDescriptor.setReturnValue(false);
    mockPropertyDescriptor.isString();
    controlPropertyDescriptor.setReturnValue(false);
    mockPropertyDescriptor.getName();
    controlPropertyDescriptor.setDefaultReturnValue("primLong");
    mockPropertyDescriptor.getPropertyType();
    controlPropertyDescriptor.setDefaultReturnValue(long.class);
    
    controlPropertyDescriptor.replay();
    
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
    mockPropertyDescriptor.isRequired();
    controlPropertyDescriptor.setReturnValue(false);
    mockPropertyDescriptor.isString();
    controlPropertyDescriptor.setReturnValue(false);
    mockPropertyDescriptor.getName();
    controlPropertyDescriptor.setDefaultReturnValue("bigDecimal");
    mockPropertyDescriptor.getPropertyType();
    controlPropertyDescriptor.setDefaultReturnValue(BigDecimal.class);
    
    controlPropertyDescriptor.replay();
    
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
    
    mockPropertyDescriptor.isRequired();
    controlPropertyDescriptor.setReturnValue(false);
    mockPropertyDescriptor.isString();
    controlPropertyDescriptor.setReturnValue(false);
    mockPropertyDescriptor.getName();
    controlPropertyDescriptor.setDefaultReturnValue("bigDecimal");
    mockPropertyDescriptor.getPropertyType();
    controlPropertyDescriptor.setDefaultReturnValue(BigDecimal.class);
    
    controlPropertyDescriptor.replay();
    
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
    mockPropertyDescriptor.isRequired();
    controlPropertyDescriptor.setReturnValue(false);
    mockPropertyDescriptor.isString();
    controlPropertyDescriptor.setReturnValue(false);
    mockPropertyDescriptor.isDate();
    controlPropertyDescriptor.setReturnValue(true);   
    mockPropertyDescriptor.getName();
    controlPropertyDescriptor.setDefaultReturnValue("aDate");
    mockPropertyDescriptor.getPropertyType();
    controlPropertyDescriptor.setDefaultReturnValue(Date.class);
    mockPropertyDescriptor.getFormat();
    controlPropertyDescriptor.setDefaultReturnValue("dd-MM-yyyy");
    
    controlPropertyDescriptor.replay();
    
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
    
    mockPropertyDescriptor.isRequired();
    controlPropertyDescriptor.setReturnValue(false);
    mockPropertyDescriptor.isString();
    controlPropertyDescriptor.setReturnValue(false);
    mockPropertyDescriptor.isDate();
    controlPropertyDescriptor.setReturnValue(true);    
    mockPropertyDescriptor.getName();
    controlPropertyDescriptor.setDefaultReturnValue("aDate");
    mockPropertyDescriptor.getPropertyType();
    controlPropertyDescriptor.setDefaultReturnValue(Date.class);
    mockPropertyDescriptor.getFormat();
    controlPropertyDescriptor.setDefaultReturnValue("dd-MM-yyyy");
    
    controlPropertyDescriptor.replay();
    
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
    
    mockPropertyDescriptor.isRequired();
    controlPropertyDescriptor.setReturnValue(false);
    mockPropertyDescriptor.isString();
    controlPropertyDescriptor.setReturnValue(false);
    mockPropertyDescriptor.isDate();
    controlPropertyDescriptor.setReturnValue(true);    
    mockPropertyDescriptor.getName();
    controlPropertyDescriptor.setDefaultReturnValue("aDate");
    mockPropertyDescriptor.getPropertyType();
    controlPropertyDescriptor.setDefaultReturnValue(Date.class);
    mockPropertyDescriptor.getFormat();
    controlPropertyDescriptor.setDefaultReturnValue("dd-MM-yyyy");
    
    controlPropertyDescriptor.replay();
    
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
    
    mockPropertyDescriptor.isRequired();
    controlPropertyDescriptor.setReturnValue(false);
    mockPropertyDescriptor.isString();
    controlPropertyDescriptor.setReturnValue(false);
    mockPropertyDescriptor.getName();
    controlPropertyDescriptor.setDefaultReturnValue("aLong");
    mockPropertyDescriptor.getPropertyType();
    controlPropertyDescriptor.setDefaultReturnValue(Long.class);
    
    controlPropertyDescriptor.replay();
    
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
    
    mockClassDescriptor.getType();
    controlClassDescriptor.setDefaultReturnValue(DummyClass2.class);
    
    mockRequest.getParameterNames();
    ArrayEnumeration enumeration = new ArrayEnumeration(new String[] {"aString", "dummyClass.anInteger"});
    controlRequest.setDefaultReturnValue(enumeration);
    
    mockClassDescriptor.getPropertyDescriptor("aString");
    controlClassDescriptor.setReturnValue(mockPropertyDescriptor1);
    
    mockClassDescriptor.getPropertyDescriptor("dummyClass");
    controlClassDescriptor.setReturnValue(mockPropertyDescriptor2);
    
    mockPropertyDescriptor1.isCollection();
    controlPropertyDescriptor1.setDefaultReturnValue(false);
    mockPropertyDescriptor1.isObjectReference();
    controlPropertyDescriptor1.setDefaultReturnValue(false);
    mockPropertyDescriptor1.isIdentifier();
    controlPropertyDescriptor1.setDefaultReturnValue(false);
    
    mockPropertyDescriptor2.isCollection();
    controlPropertyDescriptor2.setDefaultReturnValue(false);
    mockPropertyDescriptor2.isObjectReference();
    controlPropertyDescriptor2.setDefaultReturnValue(true);
    mockPropertyDescriptor2.isIdentifier();
    controlPropertyDescriptor2.setDefaultReturnValue(false);
    
    mockPropertyDescriptor2.getPropertyType();
    controlPropertyDescriptor2.setDefaultReturnValue(DummyClass.class);
    
    mockDescriptor.getClassDescriptor(DummyClass.class);
    controlDescriptor.setReturnValue(mockClassDescriptor2);
    
    mockClassDescriptor2.getIdentifierDescriptor();
    controlClassDescriptor2.setReturnValue(mockPropertyDescriptor3);
    
    mockPropertyDescriptor3.getPropertyType();
    controlPropertyDescriptor3.setDefaultReturnValue(Integer.class);
    
    mockRequest.getParameter("aString");
    controlRequest.setReturnValue("2");
    
    mockRequest.getParameter("dummyClass.anInteger");
    controlRequest.setReturnValue("4");
    
    mockPersistence.getInstance(DummyClass.class, new Integer(4));
    controlPersistence.setReturnValue(dummyClass);
    
    controlClassDescriptor.replay();
    controlClassDescriptor2.replay();
    controlPropertyDescriptor1.replay();
    controlPropertyDescriptor2.replay();
    controlPropertyDescriptor3.replay();
    controlDescriptor.replay();
    controlPersistence.replay();
    controlRequest.replay();

    // TEST IT
    Object boundedInstance = binderToTest.bind(mockRequest, mockClassDescriptor);
    
    assertNotNull(boundedInstance);
    assertTrue(boundedInstance.getClass().equals(DummyClass2.class));
    assertTrue(((DummyClass2)boundedInstance).getDummyClass().equals(dummyClass));
    controlClassDescriptor.verify();
    controlPropertyDescriptor1.verify();
    controlPropertyDescriptor2.verify();
    controlDescriptor.verify();
    controlPersistence.verify();
    controlRequest.verify();
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
