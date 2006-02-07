package org.trails.spring.mvc.view;

import javax.servlet.http.HttpServletRequest;

import org.trails.descriptor.IClassDescriptor;
import org.trails.spring.mvc.ObjectDataDescriptorList;
import org.trails.spring.mvc.TrailsControllerConstants;

import junit.framework.TestCase;

import static org.easymock.classextension.EasyMock.*;

public class TrailsViewTest extends TestCase {

  private HttpServletRequest httpServletRequest = createMock(HttpServletRequest.class);
  private ObjectDataDescriptorList dataDescriptorList = createMock(ObjectDataDescriptorList.class);
  private IClassDescriptor classDescriptor = createMock(IClassDescriptor.class);
  
  /**
   * Test method for 'org.trails.spring.mvc.view.TrailsView.prepareForRendering(HttpServletRequest, HttpServletResponse)'
   */
  public void testPrepareForRenderingNotListExists() throws Exception {
    expect(httpServletRequest.getAttribute(TrailsControllerConstants.TRAILS_COMMAND_NAME)).andStubReturn(dataDescriptorList);
    expect(dataDescriptorList.getClassDescriptor()).andStubReturn(classDescriptor);
    expect(classDescriptor.getDisplayName()).andStubReturn("Car");
    
    TrailsViewForTest trailsView = new TrailsViewForTest();
    trailsView.setBeanName("edit");
    trailsView.setExists(true);
    
    replay(httpServletRequest);
    replay(dataDescriptorList);
    replay(classDescriptor);
    String viewName = trailsView.prepareForRendering(httpServletRequest, null);
    
    assertNotNull(viewName);
    assertEquals("editCar", viewName);
    
  }
  // Overridden clas so its testable. We only need to test testPrepareForRendering.
  private static class TrailsViewForTest extends TrailsView {
    private boolean exists = false;
    @Override
    public String getUrl() {
      return "edit";
    }
    /**
     * Overridden so the test can be executed.
     */
    @Override
    protected boolean doesViewExist(String customUrlPathToView) {
      return exists;
    }
    public void setExists(boolean exists) {
      this.exists = exists;
    }
    
    
  }

}
