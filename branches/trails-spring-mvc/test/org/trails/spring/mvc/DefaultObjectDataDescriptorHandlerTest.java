package org.trails.spring.mvc;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratorType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import junit.framework.TestCase;

import org.trails.descriptor.CollectionDescriptor;
import org.trails.descriptor.DescriptorService;
import org.trails.descriptor.IClassDescriptor;
import org.trails.descriptor.IPropertyDescriptor;
import org.trails.descriptor.IdentifierDescriptor;
import org.trails.descriptor.ObjectReferenceDescriptor;
import org.trails.descriptor.TrailsClassDescriptor;
import org.trails.descriptor.TrailsPropertyDescriptor;
import org.trails.persistence.PersistenceService;

public class DefaultObjectDataDescriptorHandlerTest extends TestCase {
  /** The class under test. */
  private DefaultObjectDataDescriptorHandler handler = new DefaultObjectDataDescriptorHandler();
  
  // mocks.
  private PersistenceService mockPersistence = createMock(PersistenceService.class);
  private DescriptorService mockDescriptor = createMock(DescriptorService.class);
  
  // property descriptors used in test.
  private CollectionDescriptor collectionDescriptor = null;
  private CollectionDescriptor collectionDescriptor2 = null;
  private IdentifierDescriptor identifierDescriptor = null;
  private ObjectReferenceDescriptor objectReferenceDescriptor = null;

  @Override
  protected void setUp() throws Exception {
    reset(mockDescriptor);
    reset(mockPersistence);
    
    handler.setDescriptorService(mockDescriptor);
    handler.setPersistenceService(mockPersistence);
    
    collectionDescriptor = new CollectionDescriptor(TestBean.class, "beans", TestBean2.class);
    collectionDescriptor2 = new CollectionDescriptor(TestBean.class, "beans2", TestBean2.class);
    identifierDescriptor = new IdentifierDescriptor(TestBean.class, "id", Integer.class);
    objectReferenceDescriptor = new ObjectReferenceDescriptor(TestBean.class, new TrailsPropertyDescriptor(TestBean2.class, "bean2", TestBean2.class), TestBean2.class);
  }

  /*
   * Test method for org.trails.spring.mvc.DefaultObjectDataDescriptorHandler.create(List, IClassDescriptor, int, int)
   */
  public void testCreate() {
//  object needed for testing.
    TestBean bean = new TestBean();
    TestBean2 bean2 = new TestBean2();
    bean.setBean2(bean2);
    bean.setBeans(Arrays.asList(new TestBean2[]{new TestBean2()}));
    bean.setId(new Integer(1));
    IClassDescriptor classDescriptor = createClassDescriptorForTestBean();
    IClassDescriptor classDescriptor2 = createClassDescriptorForTestBean2();
    List all = Arrays.asList(new TestBean2[]{new TestBean2()});
    List instances = new ArrayList();

    // add two the same
    instances.add(bean);
    instances.add(bean);

    // mock behaviour
    expect(mockDescriptor.getClassDescriptor(TestBean2.class)).andStubReturn(classDescriptor2);
    
    replay(mockDescriptor);
    replay(mockPersistence);
    
    ObjectDataDescriptorList list = handler.create(instances, classDescriptor, 1, 10);
    
    assertNotNull(list);
    assertNotNull(list.getRows());
    assertEquals(2 , list.getRows().size());
    assertTrue(list.getColumnNames().contains(collectionDescriptor));
    assertTrue(list.getColumnNames().contains(identifierDescriptor));
    assertTrue(list.getColumnNames().contains(objectReferenceDescriptor));
    assertEquals(classDescriptor, list.getClassDescriptor());

    for (Iterator iter = list.getRows().iterator(); iter.hasNext();) {
      ObjectDataDescriptor dataDescriptor = (ObjectDataDescriptor) iter.next();
      
      assertNotNull(dataDescriptor);
      assertNotNull(dataDescriptor.getColumns());
      assertEquals(4, dataDescriptor.getColumns().size());
      
      boolean[] allTested = new boolean[] {false, false, false, false};
      for (Iterator iter2 = dataDescriptor.getColumns().iterator(); iter2.hasNext();) {
        PropertyDataDescriptor element = (PropertyDataDescriptor) iter2.next();
        if (element.getPropertyDescriptor() == (collectionDescriptor)) {
          assertEquals(ObjectDataDescriptorList.class, element.getValue().getClass());
          assertTrue(element.isValueInObjectTable());
          allTested[0] = true;
        
        } else if(element.getPropertyDescriptor().equals(identifierDescriptor)) {
          assertEquals(bean.getId(), element.getValue());
          allTested[1] = true;
          
        } else if (element.getPropertyDescriptor().equals(objectReferenceDescriptor)) {
          // don't need to test the contents of the ObjectDataDescriptorList as that is already tested.
          assertEquals(bean2, element.getValue());
          allTested[2] = true;
        } else if(element.getPropertyDescriptor() == (collectionDescriptor2)) {
          assertEquals("", element.getValue());
          assertFalse(element.isValueInObjectTable());
          allTested[3] = true;
        } else {
          fail("unexpected IPropertyDescriptor encountered: " + element.getPropertyDescriptor());
        }
      }
      
      // test that all the propertydescriptors are handeld.
      for (int i = 0; i < allTested.length; i++) {
        assertTrue(allTested[i]);
      }
    }
  }

  /*
   * Test method for org.trails.spring.mvc.DefaultObjectDataDescriptorHandler.createAndResolveChildern(Object, IClassDescriptor)
   */
  public void testCreateAndResolveChildern() {
    // object needed for testing.
    TestBean bean = new TestBean();
    bean.setBean2(new TestBean2());
    bean.setBeans(Arrays.asList(new TestBean2[]{new TestBean2()}));
    bean.setId(new Integer(1));
    IClassDescriptor classDescriptor = createClassDescriptorForTestBean();
    IClassDescriptor classDescriptor2 = createClassDescriptorForTestBean2();
    List all = Arrays.asList(new TestBean2[]{new TestBean2()});
    
    // mock behaviour
    expect(mockDescriptor.getClassDescriptor(TestBean2.class)).andStubReturn(classDescriptor2);
    expect(mockPersistence.getAllInstances(TestBean2.class)).andReturn(all);
    
    replay(mockDescriptor);
    replay(mockPersistence);
    
    ObjectDataDescriptorList list = handler.createAndResolveChildern(bean, classDescriptor);
    
    assertNotNull(list);
    assertNotNull(list.getRows());
    assertEquals(1, list.getRows().size());
    assertTrue(list.getColumnNames().contains(collectionDescriptor));
    assertTrue(list.getColumnNames().contains(identifierDescriptor));
    assertTrue(list.getColumnNames().contains(objectReferenceDescriptor));
    assertEquals(classDescriptor, list.getClassDescriptor());
    
    ObjectDataDescriptor dataDescriptor = list.getRows().get(0);
    
    assertNotNull(dataDescriptor);
    assertNotNull(dataDescriptor.getColumns());
    assertEquals(4, dataDescriptor.getColumns().size());
    
    boolean[] allTested = new boolean[] {false, false, false, false};
    for (Iterator iter = dataDescriptor.getColumns().iterator(); iter.hasNext();) {
      PropertyDataDescriptor element = (PropertyDataDescriptor) iter.next();
      // we to use == because equals returns the sees two instances of a same collection
      // as the same propertydescriptor.
      if (element.getPropertyDescriptor() == (collectionDescriptor)) {
        assertEquals(ObjectDataDescriptorList.class, element.getValue().getClass());
        assertTrue(element.isValueInObjectTable());
        allTested[0] = true;
      
      } else if(element.getPropertyDescriptor().equals(identifierDescriptor)) {
        assertEquals(bean.getId(), element.getValue());
        allTested[1] = true;
        
      } else if (element.getPropertyDescriptor().equals(objectReferenceDescriptor)) {
        // don't need to test the contents of the ObjectDataDescriptorList as that is already tested.
        assertEquals(ObjectDataDescriptorList.class, element.getValue().getClass());
        assertTrue(element.isValueInObjectTable());
        allTested[2] = true;
      } else if(element.getPropertyDescriptor() == (collectionDescriptor2)) {
        assertEquals("", element.getValue());
        assertFalse(element.isValueInObjectTable());
        allTested[3] = true;
      } else {
        fail("unexpected IPropertyDescriptor encountered: " + element.getPropertyDescriptor());
      }
    }
    
    // test that all the propertydescriptors are handeld.
    for (int i = 0; i < allTested.length; i++) {
      assertTrue(allTested[i]);
    }
  }

  
  // ====================================================================
  // test beans
  // ====================================================================
  @Entity
  public static class TestBean {
    private Integer id;
    private List<TestBean2> beans;
    private List<TestBean2> beans2;
    private TestBean2 bean2;
    
    @ManyToOne
    public TestBean2 getBean2() {
      return bean2;
    }
    public void setBean2(TestBean2 bean2) {
      this.bean2 = bean2;
    }
    @Id(generate=GeneratorType.AUTO)
    public Integer getId() {
      return id;
    }
    public void setId(Integer id) {
      this.id = id;
    }
    @OneToMany
    public List<TestBean2> getBeans() {
      return beans;
    }
    public void setBeans(List<TestBean2> beans) {
      this.beans = beans;
    }
    
    @OneToMany
    public List<TestBean2> getBeans2() {
      return beans2;
    }
    public void setBeans2(List<TestBean2> beans2) {
      this.beans2 = beans2;
    }
  }
  
  //test bean
  @Entity
  public static class TestBean2 {
    private Integer id;
    @Id(generate=GeneratorType.AUTO)
    public Integer getId() {
      return id;
    }

    public void setId(Integer id) {
      this.id = id;
    }
    
  }
  // ====================================================================
  // convenience methods
  // ====================================================================
  private IClassDescriptor createClassDescriptorForTestBean() {
    IClassDescriptor classDescriptor = new TrailsClassDescriptor(TestBean.class);
    List<IPropertyDescriptor> props = new ArrayList<IPropertyDescriptor>();
    props.add(new IdentifierDescriptor(TestBean.class, "id", Integer.class));
    collectionDescriptor.setElementType(TestBean2.class);
    props.add(collectionDescriptor);
    collectionDescriptor2.setElementType(TestBean2.class);
    props.add(collectionDescriptor2);
    props.add(new ObjectReferenceDescriptor(TestBean.class, new TrailsPropertyDescriptor(TestBean2.class, "bean2", TestBean2.class), TestBean2.class));
    classDescriptor.setPropertyDescriptors(props);
    return classDescriptor;
  }
  
  private IClassDescriptor createClassDescriptorForTestBean2() {
    IClassDescriptor classDescriptor = new TrailsClassDescriptor(TestBean2.class);
    List<IPropertyDescriptor> props = new ArrayList<IPropertyDescriptor>();
    props.add(new IdentifierDescriptor(TestBean2.class, "id", Integer.class));
    classDescriptor.setPropertyDescriptors(props);
    return classDescriptor;
  }  
  
}
