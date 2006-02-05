package org.trails.spring.mvc;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.web.servlet.ModelAndView;
import org.trails.descriptor.DescriptorService;
import org.trails.descriptor.IClassDescriptor;
import org.trails.descriptor.IPropertyDescriptor;
import org.trails.descriptor.TrailsClassDescriptor;
import org.trails.persistence.PersistenceService;
import org.trails.spring.mvc.commands.TrailsCommand;
import org.trails.spring.persistence.PagingCriteria;

import junit.framework.TestCase;
import static org.easymock.EasyMock.*;
import static org.trails.spring.mvc.TrailsControllerConstants.*;

public class TrailsMultiActionControllerTest extends TestCase {

  /** The class under test. */
  private TrailsMultiActionController trailsMultiActionController = null;
  
  private DescriptorService mockDescriptorService = createMock(DescriptorService.class);
  private PersistenceService mockPersistenceService = createMock(PersistenceService.class);
  private ObjectDataDescriptorHandler mockHandler = createMock(ObjectDataDescriptorHandler.class);
  
  private List allTypes = new ArrayList();
 
  @Override
  protected void setUp() throws Exception {
    trailsMultiActionController = new TrailsMultiActionController();
    
    reset(mockDescriptorService);
    reset(mockPersistenceService);
    reset(mockHandler);
    
    trailsMultiActionController.setDescriptorService(mockDescriptorService);
    trailsMultiActionController.setPersistenceService(mockPersistenceService);
    trailsMultiActionController.setDataDescriptorHandler(mockHandler);
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
    expect(mockHandler.create(instances, testClassDescriptor, false, 0, -1)).andReturn(new ObjectDataDescriptorList(testClassDescriptor));
    
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
    expect(mockHandler.create(instances, testClassDescriptor, false, 1, 0)).andReturn(new ObjectDataDescriptorList(testClassDescriptor));
    
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
    expect(mockHandler.create(isA(TrailsMultiActionControllerTest.class), isA(IClassDescriptor.class))).andReturn(new ObjectDataDescriptorList(testClassDescriptor));
    
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
    
    expect(mockHandler.create(isA(TrailsMultiActionControllerTest.class), isA(IClassDescriptor.class))).andReturn(new ObjectDataDescriptorList(testClassDescriptor));
    
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
    expect(mockHandler.create(this, mockClassDescriptor)).andReturn(new ObjectDataDescriptorList(realClassDescriptor));
    
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
    expect(mockHandler.create(instances, testClassDescriptor, false, 0, -1)).andReturn(new ObjectDataDescriptorList(instances, testClassDescriptor));
    
    replayDefaultMocks();
    // TEST
    ModelAndView modelAndView = trailsMultiActionController.searchInstances(null, null, trailsCommand);
    
    assertNotNull(modelAndView);
    assertEquals(LIST_VIEW, modelAndView.getViewName());
    assertEquals(ObjectDataDescriptorList.class, modelAndView.getModel().get(TRAILS_COMMAND_NAME).getClass());
    assertEquals(testClassDescriptor, ((ObjectDataDescriptorList)modelAndView.getModel().get(TRAILS_COMMAND_NAME)).getClassDescriptor());
    verifyDefaultMocks();
    
  }

  /*
   * Test method for 'org.trails.spring.mvc.TrailsMultiActionController.saveInstance(HttpServletRequest, HttpServletResponse, TrailsCommand)'
   */
  public void testSaveInstance() {

  }

  /*
   * Test method for 'org.trails.spring.mvc.TrailsMultiActionController.deleteInstance(HttpServletRequest, HttpServletResponse, TrailsCommand)'
   */
  public void testDeleteInstance() {

  }

  /*
   * Test method for 'org.trails.spring.mvc.TrailsMultiActionController.getDescriptorService()'
   */
  public void testGetDescriptorService() {

  }

  /*
   * Test method for 'org.trails.spring.mvc.TrailsMultiActionController.setDescriptorService(DescriptorService)'
   */
  public void testSetDescriptorService() {

  }

  /*
   * Test method for 'org.trails.spring.mvc.TrailsMultiActionController.getPagingSize()'
   */
  public void testGetPagingSize() {

  }

  /*
   * Test method for 'org.trails.spring.mvc.TrailsMultiActionController.setPagingSize(int)'
   */
  public void testSetPagingSize() {

  }

  /*
   * Test method for 'org.trails.spring.mvc.TrailsMultiActionController.getPersistenceService()'
   */
  public void testGetPersistenceService() {

  }

  /*
   * Test method for 'org.trails.spring.mvc.TrailsMultiActionController.setPersistenceService(PersistenceService)'
   */
  public void testSetPersistenceService() {

  }

  /*
   * Test method for 'org.trails.spring.mvc.TrailsMultiActionController.getId(IClassDescriptor, TrailsCommand)'
   */
  public void testGetId() {

  }

  /*
   * Test method for 'org.trails.spring.mvc.TrailsMultiActionController.addTrailsModelToModelAndView(TrailsCommand, Object, ModelAndView)'
   */
  public void testAddTrailsModelToModelAndViewTrailsCommandObjectModelAndView() {

  }

  /*
   * Test method for 'org.trails.spring.mvc.TrailsMultiActionController.addTrailsModelToModelAndView(IClassDescriptor, Object, ModelAndView)'
   */
  public void testAddTrailsModelToModelAndViewIClassDescriptorObjectModelAndView() {

  }

  /*
   * Test method for 'org.trails.spring.mvc.TrailsMultiActionController.getNewDataBinder()'
   */
  public void testGetNewDataBinder() {

  }

  /*
   * Test method for 'org.trails.spring.mvc.TrailsMultiActionController.handleObjectCreation(HttpServletRequest, HttpServletResponse, IClassDescriptor, Object)'
   */
  public void testHandleObjectCreation() {

  }

  /*
   * Test method for 'org.trails.spring.mvc.TrailsMultiActionController.handleObjectEdit(HttpServletRequest, HttpServletResponse, IClassDescriptor, Object)'
   */
  public void testHandleObjectEdit() {

  }

  /*
   * Test method for 'org.trails.spring.mvc.TrailsMultiActionController.handleObjectSave(HttpServletRequest, HttpServletResponse, IClassDescriptor, Object)'
   */
  public void testHandleObjectSave() {

  }

  /*
   * Test method for 'org.trails.spring.mvc.TrailsMultiActionController.bindAndValidate(HttpServletRequest, TrailsServletRequestDataBinder, IClassDescriptor)'
   */
  public void testBindAndValidateHttpServletRequestTrailsServletRequestDataBinderIClassDescriptor() {

  }

  /*
   * Test method for 'org.trails.spring.mvc.TrailsMultiActionController.bindAndValidate(HttpServletRequest, TrailsServletRequestDataBinder, IClassDescriptor, Object)'
   */
  public void testBindAndValidateHttpServletRequestTrailsServletRequestDataBinderIClassDescriptorObject() {

  }

  /*
   * Test method for 'org.trails.spring.mvc.TrailsMultiActionController.performValidation(Object, BindException)'
   */
  public void testPerformValidation() {

  }

  /*
   * Test method for 'org.trails.spring.mvc.TrailsMultiActionController.getSelectedClassDescriptor(TrailsCommand)'
   */
  public void testGetSelectedClassDescriptorTrailsCommand() {

  }

  /*
   * Test method for 'org.trails.spring.mvc.TrailsMultiActionController.getSelectedClassDescriptor(Class)'
   */
  public void testGetSelectedClassDescriptorClass() {

  }
  
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
  }
  
  /**
   * Replays the {@link #mockDescriptorService},the {@link #mockPersistenceService} and the {@link #mockHandler}.
   *
   */
  private void replayDefaultMocks() {
    replay(mockDescriptorService);
    replay(mockPersistenceService);
    replay(mockHandler);
  }  
}
