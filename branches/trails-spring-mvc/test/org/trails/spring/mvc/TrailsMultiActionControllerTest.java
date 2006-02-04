package org.trails.spring.mvc;

import java.util.ArrayList;
import java.util.List;

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
  
  private List allTypes = new ArrayList();
 
  @Override
  protected void setUp() throws Exception {
    trailsMultiActionController = new TrailsMultiActionController();
    
    reset(mockDescriptorService);
    reset(mockPersistenceService);
    
    trailsMultiActionController.setDescriptorService(mockDescriptorService);
    trailsMultiActionController.setPersistenceService(mockPersistenceService);
  }

  /*
   * Test method for 'org.trails.spring.mvc.TrailsMultiActionController.listAllEntities(HttpServletRequest, HttpServletResponse)'
   */
  public void testListAllEntities() {
    // the return value that should ne in the returned ModelAndView.
    List descriptors = new ArrayList();
    
    expect(mockDescriptorService.getAllDescriptors()).andReturn(descriptors);
    
    replayPersistenceAndDescriptor();
    
    ModelAndView modelAndView = trailsMultiActionController.listAllEntities(null, null);
    
    assertNotNull(modelAndView);
    assertEntityList(modelAndView, descriptors);
    
    verifyPersistenceAndDescriptor();
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
    
    // replay mocks
    replayPersistenceAndDescriptor();

    ModelAndView modelAndView = trailsMultiActionController.listAllInstances(null, null, trailsCommand);
    
    assertNotNull(modelAndView);
    assertEquals(LIST_VIEW, modelAndView.getViewName());
    assertNotNull(modelAndView.getModel().get(TRAILS_COMMAND_NAME));
    assertEquals(ObjectDataDescriptorList.class, modelAndView.getModel().get(TRAILS_COMMAND_NAME).getClass());
    ObjectDataDescriptorList table = (ObjectDataDescriptorList)modelAndView.getModel().get(TRAILS_COMMAND_NAME);
    assertEquals(0, table.getRows().size());
    assertEntityList(modelAndView, allTypes);
    
    verifyPersistenceAndDescriptor();
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
    
    // replay mocks
    replayPersistenceAndDescriptor();

    // TEST
    ModelAndView modelAndView = trailsMultiActionController.listAllInstances(null, null, trailsCommand);
    
    assertNotNull(modelAndView);
    assertEquals(LIST_VIEW, modelAndView.getViewName());
    assertNotNull(modelAndView.getModel().get(TRAILS_COMMAND_NAME));
    assertEquals(ObjectDataDescriptorList.class, modelAndView.getModel().get(TRAILS_COMMAND_NAME).getClass());
    ObjectDataDescriptorList table = (ObjectDataDescriptorList)modelAndView.getModel().get(TRAILS_COMMAND_NAME);
    assertEquals(0, table.getRows().size());
    assertEntityList(modelAndView, allTypes);
    
    verifyPersistenceAndDescriptor();
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
    
    // replay mocks
    replayPersistenceAndDescriptor();
   
    // TEST
    ModelAndView modelAndView = trailsMultiActionController.prepareToSearchInstances(null, null, trailsCommand);
    
    assertNotNull(modelAndView);
    assertEquals(SEARCH_VIEW, modelAndView.getViewName());
    assertEquals(ObjectDataDescriptorList.class, modelAndView.getModel().get(TRAILS_COMMAND_NAME).getClass());
    ObjectDataDescriptorList table = (ObjectDataDescriptorList)modelAndView.getModel().get(TRAILS_COMMAND_NAME);
    assertEquals(1, table.getRows().size());
    assertEquals(testClassDescriptor, table.getClassDescriptor());
    
    verifyPersistenceAndDescriptor();
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
    
    // replay mocks
    replayPersistenceAndDescriptor();
   
    // TEST
    ModelAndView modelAndView = trailsMultiActionController.prepareToEditOrAddAnInstance(null, null, trailsCommand);
    
    assertNotNull(modelAndView);
    assertEquals(EDIT_VIEW, modelAndView.getViewName());
    assertEquals(ObjectDataDescriptorList.class, modelAndView.getModel().get(TRAILS_COMMAND_NAME).getClass());
    ObjectDataDescriptorList table = (ObjectDataDescriptorList)modelAndView.getModel().get(TRAILS_COMMAND_NAME);
    assertEquals(testClassDescriptor, table.getClassDescriptor());
    assertEquals(1, table.getRows().size());
   
    verifyPersistenceAndDescriptor();
  }
  
  /*
   * Test method for 'org.trails.spring.mvc.TrailsMultiActionController.prepareToEditOrAddAnInstance(HttpServletRequest, HttpServletResponse, TrailsCommand)'
   */
  public void testPrepareToEditOrAddAnInstanceToBeEdited() {
    //  objects needed for testing
    TrailsCommand trailsCommand = createTrailsCommand(0,0);
    trailsCommand.setId("1");
    IClassDescriptor mockClassDescriptor = createMock(IClassDescriptor.class);
    IPropertyDescriptor idDescriptor = createMock(IPropertyDescriptor.class);
    
    
    expect(mockClassDescriptor.getType()).andStubReturn(TrailsMultiActionControllerTest.class);
    expect(mockClassDescriptor.getIdentifierDescriptor()).andStubReturn(idDescriptor);
    expect(idDescriptor.getName()).andStubReturn("id");
    expect(idDescriptor.isNumeric()).andStubReturn(true);
    expect(mockDescriptorService.getClassDescriptor(TrailsMultiActionControllerTest.class)).andStubReturn(mockClassDescriptor);
    expect(mockPersistenceService.getInstance(TrailsMultiActionControllerTest.class, new Integer(1))).andReturn(this);
    expect(mockDescriptorService.getAllDescriptors()).andReturn(allTypes);
    expect(mockClassDescriptor.getPropertyDescriptors()).andStubReturn(new ArrayList());
    // replay mocks
    replayPersistenceAndDescriptor();
    replay(mockClassDescriptor);
    replay(idDescriptor);
   
    // TEST
    ModelAndView modelAndView = trailsMultiActionController.prepareToEditOrAddAnInstance(null, null, trailsCommand);
    
    assertNotNull(modelAndView);
    assertEquals(EDIT_VIEW, modelAndView.getViewName());
    assertEquals(ObjectDataDescriptorList.class, modelAndView.getModel().get(TRAILS_COMMAND_NAME).getClass());
    ObjectDataDescriptorList table = (ObjectDataDescriptorList)modelAndView.getModel().get(TRAILS_COMMAND_NAME);
    assertEquals(1, table.getRows().size());
    assertEquals(this, table.getRows().get(0).getInstance());
    
    assertEquals(mockClassDescriptor, table.getClassDescriptor());
    
    verifyPersistenceAndDescriptor();
  }  

  /*
   * Test method for 'org.trails.spring.mvc.TrailsMultiActionController.searchInstances(HttpServletRequest, HttpServletResponse, TrailsCommand)'
   */
  public void testSearchInstances() {

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
   * Verifies the {@link #mockDescriptorService} and the {@link #mockPersistenceService}.
   *
   */
  private void verifyPersistenceAndDescriptor() {
    verify(mockDescriptorService);
    verify(mockPersistenceService);
  }
  
  /**
   * Verifies the {@link #mockDescriptorService} and the {@link #mockPersistenceService}.
   *
   */
  private void replayPersistenceAndDescriptor() {
    replay(mockDescriptorService);
    replay(mockPersistenceService);
  }  
}
