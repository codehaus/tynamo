package org.trails.spring.mvc;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.reset;
import static org.easymock.classextension.EasyMock.verify;
import static org.trails.spring.mvc.TrailsControllerConstants.EDIT_VIEW;
import static org.trails.spring.mvc.TrailsControllerConstants.LIST_VIEW;
import static org.trails.spring.mvc.TrailsControllerConstants.SEARCH_VIEW;
import static org.trails.spring.mvc.TrailsControllerConstants.TRAILS_COMMAND_NAME;
import static org.trails.spring.mvc.TrailsControllerConstants.TRAILS_ENTITY_LIST;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratorType;
import javax.persistence.Id;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.trails.descriptor.DescriptorService;
import org.trails.descriptor.IClassDescriptor;
import org.trails.descriptor.IPropertyDescriptor;
import org.trails.descriptor.IdentifierDescriptor;
import org.trails.descriptor.TrailsClassDescriptor;
import org.trails.descriptor.TrailsPropertyDescriptor;
import org.trails.persistence.PersistenceService;
import org.trails.spring.mvc.commands.TrailsCommand;
import org.trails.spring.persistence.PagingCriteria;

public class TrailsMultiActionControllerTest extends TestCase {

  /** The class under test. */
  private TrailsMultiActionController trailsMultiActionController = null;
  
  private DescriptorService mockDescriptorService = createMock(DescriptorService.class);
  private PersistenceService mockPersistenceService = createMock(PersistenceService.class);
  private ObjectDataDescriptorHandler mockHandler = createMock(ObjectDataDescriptorHandler.class);
  private HttpServletRequest mockRequest = createMock(HttpServletRequest.class);
  
  private List allTypes = new ArrayList();
 
  @Override
  protected void setUp() throws Exception {
    trailsMultiActionController = new TrailsMultiActionController();
    
    reset(mockDescriptorService);
    reset(mockPersistenceService);
    reset(mockHandler);
    reset(mockRequest);
    
    pupulate(trailsMultiActionController);
  }

  /**
   * populates the controller.
   * @param controller
   */
  private void pupulate(TrailsMultiActionController controller) {
    controller.setDescriptorService(mockDescriptorService);
    controller.setPersistenceService(mockPersistenceService);
    controller.setDataDescriptorHandler(mockHandler);
  }

  /*
   * Test method for 'org.trails.spring.mvc.TrailsMultiActionController.listAllEntities(HttpServletRequest, HttpServletResponse)'
   */
  public void testListAllEntities() {
    // the return value that should ne in the returned ModelAndView.
    List descriptors = new ArrayList();
    
    expect(mockDescriptorService.getAllDescriptors()).andReturn(descriptors);
    
    replayDefaultMocks();
    
    ModelAndView modelAndView = trailsMultiActionController.listAllEntities(null, null);
    
    assertNotNull(modelAndView);
    assertEntityList(modelAndView, descriptors);
    
    verifyDefaultMocks();
  }

  /*
   * Test method for 'org.trails.spring.mvc.TrailsMultiActionController.listAllInstances(HttpServletRequest, HttpServletResponse, TrailsCommand)'
   */
  public void testListAllInstancesPagingSizeZero() {
    
    // objects needed for testing.
    List instances = new ArrayList();
    TrailsCommand trailsCommand = createTrailsCommand(0,0);
    IClassDescriptor testClassDescriptor = new TrailsClassDescriptor(TrailsMultiActionControllerTest.class);
    
    // add expected behaviour on our mocks.
    expect(mockDescriptorService.getClassDescriptor(TrailsMultiActionControllerTest.class)).andStubReturn(testClassDescriptor);
    expect(mockPersistenceService.getAllInstances(testClassDescriptor.getType())).andReturn(instances);
    expect(mockDescriptorService.getAllDescriptors()).andReturn(allTypes);
    expect(mockHandler.create(instances, testClassDescriptor, 0, -1)).andReturn(new ObjectDataDescriptorList(testClassDescriptor));
    
    // replay mocks
    replayDefaultMocks();

    ModelAndView modelAndView = trailsMultiActionController.listAllInstances(null, null, trailsCommand);
    
    assertNotNull(modelAndView);
    assertEquals(LIST_VIEW, modelAndView.getViewName());
    assertNotNull(modelAndView.getModel().get(TRAILS_COMMAND_NAME));
    assertEquals(ObjectDataDescriptorList.class, modelAndView.getModel().get(TRAILS_COMMAND_NAME).getClass());
    ObjectDataDescriptorList table = (ObjectDataDescriptorList)modelAndView.getModel().get(TRAILS_COMMAND_NAME);
    assertEquals(0, table.getRows().size());
    assertEntityList(modelAndView, allTypes);
    
    verifyDefaultMocks();
  }
  
  /*
   * Test method for 'org.trails.spring.mvc.TrailsMultiActionController.listAllInstances(HttpServletRequest, HttpServletResponse, TrailsCommand)'
   */
  public void testListAllInstancesPagingSizeNotZero() {
    // set the paging size
    trailsMultiActionController.setPagingSize(1);
    // objects needed for testing.
    List instances = new ArrayList();
    TrailsCommand trailsCommand = createTrailsCommand(1,2);
    IClassDescriptor testClassDescriptor = new TrailsClassDescriptor(TrailsMultiActionControllerTest.class);
    
    // add expected behaviour on our mocks.
    expect(mockDescriptorService.getClassDescriptor(TrailsMultiActionControllerTest.class)).andStubReturn(testClassDescriptor);
    expect(mockPersistenceService.getInstances(isA(PagingCriteria.class))).andReturn(instances);
    expect(mockDescriptorService.getAllDescriptors()).andReturn(allTypes);
    expect(mockHandler.create(instances, testClassDescriptor, 1, 0)).andReturn(new ObjectDataDescriptorList(testClassDescriptor));
    
    // replay mocks
    replayDefaultMocks();

    // TEST
    ModelAndView modelAndView = trailsMultiActionController.listAllInstances(null, null, trailsCommand);
    
    assertNotNull(modelAndView);
    assertEquals(LIST_VIEW, modelAndView.getViewName());
    assertNotNull(modelAndView.getModel().get(TRAILS_COMMAND_NAME));
    assertEquals(ObjectDataDescriptorList.class, modelAndView.getModel().get(TRAILS_COMMAND_NAME).getClass());
    ObjectDataDescriptorList table = (ObjectDataDescriptorList)modelAndView.getModel().get(TRAILS_COMMAND_NAME);
    assertEquals(0, table.getRows().size());
    assertEntityList(modelAndView, allTypes);
    
    verifyDefaultMocks();
  }  

  /*
   * Test method for 'org.trails.spring.mvc.TrailsMultiActionController.prepareToSearchInstances(HttpServletRequest, HttpServletResponse, TrailsCommand)'
   */
  public void testPrepareToSearchInstances() {
    // objects needed for testing
    TrailsCommand trailsCommand = createTrailsCommand(0,0);
    IClassDescriptor testClassDescriptor = new TrailsClassDescriptor(TrailsMultiActionControllerTest.class);
    
    expect(mockDescriptorService.getClassDescriptor(TrailsMultiActionControllerTest.class)).andStubReturn(testClassDescriptor);
    expect(mockDescriptorService.getAllDescriptors()).andReturn(allTypes);
    expect(mockHandler.createAndResolveChildern(isA(TrailsMultiActionControllerTest.class), isA(IClassDescriptor.class))).andReturn(new ObjectDataDescriptorList(testClassDescriptor));
    
    // replay mocks
    replayDefaultMocks();
   
    // TEST
    ModelAndView modelAndView = trailsMultiActionController.prepareToSearchInstances(null, null, trailsCommand);
    
    assertNotNull(modelAndView);
    assertEquals(SEARCH_VIEW, modelAndView.getViewName());
    assertEquals(ObjectDataDescriptorList.class, modelAndView.getModel().get(TRAILS_COMMAND_NAME).getClass());
    
    verifyDefaultMocks();
  }

  /*
   * Test method for 'org.trails.spring.mvc.TrailsMultiActionController.prepareToEditOrAddAnInstance(HttpServletRequest, HttpServletResponse, TrailsCommand)'
   */
  public void testPrepareToEditOrAddAnInstanceToBeAdded() {
    //  objects needed for testing
    TrailsCommand trailsCommand = createTrailsCommand(0,0);
    IClassDescriptor testClassDescriptor = new TrailsClassDescriptor(TrailsMultiActionControllerTest.class);
    
    expect(mockDescriptorService.getClassDescriptor(TrailsMultiActionControllerTest.class)).andStubReturn(testClassDescriptor);
    expect(mockDescriptorService.getAllDescriptors()).andReturn(allTypes);
    
    expect(mockHandler.createAndResolveChildern(isA(TrailsMultiActionControllerTest.class), isA(IClassDescriptor.class))).andReturn(new ObjectDataDescriptorList(testClassDescriptor));
    
    // replay mocks
    replayDefaultMocks();
   
    // TEST
    ModelAndView modelAndView = trailsMultiActionController.prepareToEditOrAddAnInstance(null, null, trailsCommand);
    
    assertNotNull(modelAndView);
    assertEquals(EDIT_VIEW, modelAndView.getViewName());
    assertEquals(ObjectDataDescriptorList.class, modelAndView.getModel().get(TRAILS_COMMAND_NAME).getClass());
   
    verifyDefaultMocks();
  }
  
  /*
   * Test method for 'org.trails.spring.mvc.TrailsMultiActionController.prepareToEditOrAddAnInstance(HttpServletRequest, HttpServletResponse, TrailsCommand)'
   */
  public void testPrepareToEditOrAddAnInstanceToBeEdited() {
    //  objects needed for testing
    TrailsCommand trailsCommand = createTrailsCommand(0,0);
    trailsCommand.setId("1");
    IClassDescriptor mockClassDescriptor = createMock(IClassDescriptor.class);
    IClassDescriptor realClassDescriptor = new TrailsClassDescriptor(TrailsMultiActionControllerTest.class);
    IPropertyDescriptor idDescriptor = createMock(IPropertyDescriptor.class);
    
    // add mock behaviour.
    expect(mockClassDescriptor.getType()).andStubReturn(TrailsMultiActionControllerTest.class);
    expect(mockClassDescriptor.getIdentifierDescriptor()).andStubReturn(idDescriptor);
    expect(mockClassDescriptor.getPropertyDescriptors()).andStubReturn(new ArrayList());
    expect(idDescriptor.getName()).andStubReturn("id");
    expect(idDescriptor.isNumeric()).andStubReturn(true);
    expect(mockDescriptorService.getClassDescriptor(TrailsMultiActionControllerTest.class)).andStubReturn(mockClassDescriptor);
    expect(mockPersistenceService.getInstance(TrailsMultiActionControllerTest.class, new Integer(1))).andReturn(this);
    expect(mockDescriptorService.getAllDescriptors()).andReturn(allTypes);
    expect(mockClassDescriptor.getPropertyDescriptors()).andStubReturn(new ArrayList());
    expect(mockHandler.createAndResolveChildern(this, mockClassDescriptor)).andReturn(new ObjectDataDescriptorList(realClassDescriptor));
    
    // replay mocks
    replayDefaultMocks();
    replay(mockClassDescriptor);
    replay(idDescriptor);
   
    // TEST
    ModelAndView modelAndView = trailsMultiActionController.prepareToEditOrAddAnInstance(null, null, trailsCommand);
    
    assertNotNull(modelAndView);
    assertEquals(EDIT_VIEW, modelAndView.getViewName());
    assertEquals(ObjectDataDescriptorList.class, modelAndView.getModel().get(TRAILS_COMMAND_NAME).getClass());
    
    verifyDefaultMocks();
  }  

  /*
   * Test method for 'org.trails.spring.mvc.TrailsMultiActionController.searchInstances(HttpServletRequest, HttpServletResponse, TrailsCommand)'
   */
  public void testSearchInstances() {
    //  objects needed for testing
    TrailsCommand trailsCommand = createTrailsCommand(0,0);
    IClassDescriptor testClassDescriptor = new TrailsClassDescriptor(TrailsMultiActionControllerTest.class);
    List instances = new ArrayList();
   
    // add behaviour for mocks.
    expect(mockDescriptorService.getClassDescriptor(TrailsMultiActionControllerTest.class)).andStubReturn(testClassDescriptor);
    expect(mockDescriptorService.getAllDescriptors()).andReturn(allTypes);
    expect(mockPersistenceService.getInstances(isA(DetachedCriteria.class))).andReturn(instances);
    
    replayDefaultMocks();
    // TEST
    ModelAndView modelAndView = trailsMultiActionController.searchInstances(mockRequest, null, trailsCommand);
    
    assertNotNull(modelAndView);
    assertEquals(LIST_VIEW, modelAndView.getViewName());
    assertEquals(ObjectDataDescriptorList.class, modelAndView.getModel().get(TRAILS_COMMAND_NAME).getClass());
    assertEquals(testClassDescriptor, ((ObjectDataDescriptorList)modelAndView.getModel().get(TRAILS_COMMAND_NAME)).getClassDescriptor());
    verifyDefaultMocks();
    
  }

  /*
   * Test method for 'org.trails.spring.mvc.TrailsMultiActionController.saveInstance(HttpServletRequest, HttpServletResponse, TrailsCommand)'
   */
  public void testSaveInstanceNewInstanceNoBindErrors() {
    final ModelAndView testModelAndView = new ModelAndView("test");
    trailsMultiActionController = new TrailsMultiActionController() {
      //overridden ad saveInstance forwards to listAllInstances and that one is already tested.
      @Override
      public ModelAndView listAllInstances(HttpServletRequest request, HttpServletResponse response, TrailsCommand command) {
        return testModelAndView;
      }
      
    };
    pupulate(trailsMultiActionController);
    //  objects needed for testing
    TrailsCommand trailsCommand = createTrailsCommand(0,0);
    trailsCommand.setId(null);
    trailsCommand.setType(TestBean.class);
    IClassDescriptor testClassDescriptor = new TrailsClassDescriptor(TestBean.class);
    testClassDescriptor.setPropertyDescriptors(Arrays.asList(new IdentifierDescriptor[] {new IdentifierDescriptor(TestBean.class, "id", Integer.class)}));
    
    // add behaviour for mocks.
    expect(mockDescriptorService.getClassDescriptor(TestBean.class)).andStubReturn(testClassDescriptor);
    expect(mockDescriptorService.getAllDescriptors()).andReturn(allTypes);
    expect(mockPersistenceService.save(isA(TestBean.class))).andReturn(new TestBean());
    replayDefaultMocks();
    //TEST
    ModelAndView modelAndView = trailsMultiActionController.saveInstance(mockRequest, null, trailsCommand);
    
    assertEquals(testModelAndView, modelAndView);
    verifyDefaultMocks();
  }
  
  /*
   * Test method for 'org.trails.spring.mvc.TrailsMultiActionController.saveInstance(HttpServletRequest, HttpServletResponse, TrailsCommand)'
   */
  public void testSaveInstanceNewInstanceWithBindErrors() {
    // override the TrailsMultiActionController so testing is easier.
    final TrailsServletRequestDataBinder mockBinder = createMock(TrailsServletRequestDataBinder.class);
    
    TrailsMultiActionController extendedMultiActionController = new TrailsMultiActionController() {

      @Override
      protected TrailsServletRequestDataBinder getDataBinder() {
        return mockBinder;
      }
     
    };
    extendedMultiActionController.setPersistenceService(mockPersistenceService);
    extendedMultiActionController.setDataDescriptorHandler(mockHandler);
    extendedMultiActionController.setDescriptorService(mockDescriptorService);
    
    //  objects needed for testing
    TrailsCommand trailsCommand = createTrailsCommand(0,0);
    trailsCommand.setId(null);
    trailsCommand.setType(TestBean.class);
    IClassDescriptor mockClassDescriptor = createMock(IClassDescriptor.class);
    IClassDescriptor realClassDescriptor = new TrailsClassDescriptor(TestBean.class);
    
    // add behaviour for mocks.
    expect(mockDescriptorService.getClassDescriptor(TestBean.class)).andStubReturn(mockClassDescriptor);
    expect(mockDescriptorService.getAllDescriptors()).andStubReturn(allTypes);
    expect(mockClassDescriptor.getType()).andStubReturn(TestBean.class);
    expect(mockBinder.hasErrors()).andStubReturn(true);
    expect(mockBinder.bind(mockRequest, mockClassDescriptor)).andReturn(new TestBean());
    expect(mockBinder.getErrors()).andStubReturn(new BindException(new TrailsMultiActionControllerTest(), "test"));
    expect(mockClassDescriptor.getIdentifierDescriptor()).andReturn(new TrailsPropertyDescriptor(TestBean.class, "id", Integer.class));
    expect(mockHandler.createAndResolveChildern(isA(TestBean.class), eq(mockClassDescriptor))).andReturn(new ObjectDataDescriptorList(realClassDescriptor));
    
    replayDefaultMocks();
    replay(mockBinder);
    replay(mockClassDescriptor);
    
    //TEST
    ModelAndView modelAndView = extendedMultiActionController.saveInstance(mockRequest, null, trailsCommand);
    
    assertEquals(EDIT_VIEW, modelAndView.getViewName());
    assertEquals(ObjectDataDescriptorList.class, modelAndView.getModel().get(TRAILS_COMMAND_NAME).getClass());
    assertEquals(realClassDescriptor, ((ObjectDataDescriptorList)modelAndView.getModel().get(TRAILS_COMMAND_NAME)).getClassDescriptor());
    verifyDefaultMocks();
  }  

  /*
   * Test method for 'org.trails.spring.mvc.TrailsMultiActionController.deleteInstance(HttpServletRequest, HttpServletResponse, TrailsCommand)'
   */
  public void testDeleteInstance() {
    //  objects needed for testing
    TrailsCommand trailsCommand = createTrailsCommand(0,0);
    trailsCommand.setId("1");
    trailsCommand.setType(TestBean.class);
    IClassDescriptor testClassDescriptor = new TrailsClassDescriptor(TestBean.class);
    testClassDescriptor.setPropertyDescriptors(Arrays.asList(new IdentifierDescriptor[] {new IdentifierDescriptor(TestBean.class, "id", Integer.class)}));
    
    List instances = new ArrayList();
    
    TestBean bean = new TestBean();
   
    // add behaviour for mocks.
    expect(mockDescriptorService.getClassDescriptor(TestBean.class)).andStubReturn(testClassDescriptor);
    expect(mockDescriptorService.getAllDescriptors()).andReturn(allTypes);
    expect(mockPersistenceService.getAllInstances(TestBean.class)).andReturn(instances);
    mockPersistenceService.remove(bean);
    expect(mockPersistenceService.getInstance(TestBean.class, new Integer(1))).andReturn(bean);
    expect(mockHandler.create(instances, testClassDescriptor, 0, -1)).andReturn(new ObjectDataDescriptorList(testClassDescriptor));
    
    replayDefaultMocks();
    // TEST
    ModelAndView modelAndView = trailsMultiActionController.deleteInstance(mockRequest, null, trailsCommand);
    
    assertNotNull(modelAndView);
    assertEquals(LIST_VIEW, modelAndView.getViewName());
    assertEquals(ObjectDataDescriptorList.class, modelAndView.getModel().get(TRAILS_COMMAND_NAME).getClass());
    assertEquals(testClassDescriptor, ((ObjectDataDescriptorList)modelAndView.getModel().get(TRAILS_COMMAND_NAME)).getClassDescriptor());
    verifyDefaultMocks();
  }

  // =======================================================
  // convenience methods.
  // =======================================================
  
  private TrailsCommand createTrailsCommand(int pageNumber, int totalNumberOfPages) {

    TrailsCommand trailsCommand = new TrailsCommand();
    trailsCommand.setType(TrailsMultiActionControllerTest.class);
    trailsCommand.setPageNumber(pageNumber);
    trailsCommand.setTotalNumberOfPages(totalNumberOfPages);
    return trailsCommand;
  }
  
  /**
   * Asserts that the {@link TrailsControllerConstants#TRAILS_ENTITY_LIST}
   * is present in the given modelAndView.
   * @param modelAndView
   */
  private void assertEntityList(ModelAndView modelAndView, List entities) {
    assertNotNull(modelAndView.getModel().get(TRAILS_ENTITY_LIST));
    assertEquals(entities, modelAndView.getModel().get(TRAILS_ENTITY_LIST));
  }

  /**
   * Verifies the {@link #mockDescriptorService},the {@link #mockPersistenceService} and the {@link #mockHandler}.
   *
   */
  private void verifyDefaultMocks() {
    verify(mockDescriptorService);
    verify(mockPersistenceService);
    verify(mockHandler);
    verify(mockRequest);
  }
  
  /**
   * Replays the {@link #mockDescriptorService},the {@link #mockPersistenceService} and the {@link #mockHandler}.
   *
   */
  private void replayDefaultMocks() {
    replay(mockDescriptorService);
    replay(mockPersistenceService);
    replay(mockHandler);
    replay(mockRequest);
  }  
  
  @Entity
  public static class TestBean {
    private Integer id;

    @Id(generate=GeneratorType.AUTO)
    public Integer getId() {
      return id;
    }

    public void setId(Integer id) {
      this.id = id;
    }
    
  }
}
