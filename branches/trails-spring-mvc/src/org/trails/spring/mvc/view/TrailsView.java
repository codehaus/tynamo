package org.trails.spring.mvc.view;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.view.JstlView;
import org.trails.spring.mvc.ObjectDataDescriptorList;
import org.trails.spring.mvc.TrailsControllerConstants;

/**
 * A TrailsView is an extension on a JstlView and
 * allows to overwrite the default views used. 
 * <p>
 * See {@link #prepareForRendering(HttpServletRequest, HttpServletResponse)} for
 * more information about overwriting the default views.
 * 
 * @author Jurjan Woltman
 *
 */
public class TrailsView extends JstlView {

  /**
   * Logging instance.
   */
  private final static Log log = LogFactory.getLog(TrailsView.class);
  /** Name of the default view View. */
  private final static String DEFAULT_VIEW_URL   = "view";
  /** Name of the default edit View. */
  private final static String DEFAULT_EDIT_URL   = "edit";
  /** Name of the default list View. */
  private final static String DEFAULT_LIST_URL   = "list";
  /** Name of the default seach View. */
  private final static String DEFAULT_SEARCH_URL = "search";
  /** Name of the default create View. */
  private final static String DEFAULT_CREATE_URL = "create";

  /** list of all available view names. */
  private final static List DEFAULT_VIEW_LIST = Arrays.asList(new String[]{DEFAULT_VIEW_URL, DEFAULT_EDIT_URL, DEFAULT_SEARCH_URL, DEFAULT_CREATE_URL, DEFAULT_LIST_URL});
  
  /**
   * Override default behaviour to make it possible to override the default view based on type of model.
   * For example, if we want to show a list of projects. Project will be our main entity and Trails will use
   * the default 'list' view, 'list.jsp'.  But if you want to show a list of Projects in custom view you can 
   * override this by creating a JSP page with the name, list[ClassDescriptor().getPluralDisplayName()].jsp. In case of project the view must be listProjects.jsp
   * For displaying a single instance,  ClassDescriptor().getDisplayName() is used as suffix.
   * 
   * @see org.springframework.web.servlet.view.InternalResourceView#prepareForRendering(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected String prepareForRendering(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String customUrl = getUrl();
    
    Object command = request.getAttribute(TrailsControllerConstants.TRAILS_COMMAND_NAME);
    
    if (command instanceof ObjectDataDescriptorList) {
      ObjectDataDescriptorList objectTable = (ObjectDataDescriptorList) command;
      
      String viewName = getBeanName();
      
      if (viewName != null && DEFAULT_VIEW_LIST.contains(viewName)) {
        
        String viewNameSuffix = objectTable.getClassDescriptor().getDisplayName();
        if(viewName.equals(DEFAULT_LIST_URL)) {
          viewNameSuffix = objectTable.getClassDescriptor().getPluralDisplayName().replaceAll("\\s", "");
        }
        
        log.debug("View name suffix: " + viewNameSuffix);
        String customUrlPath = getUrl().replaceAll(viewName, viewName + viewNameSuffix);

        if (doesViewExist(customUrlPath)) {
          log.debug("Custom view found.");

          customUrl = customUrlPath;
        } else {
          log.debug("No custom view found, using default view.");
        }
      }
    }
    
    return customUrl;
  }
  
  /**
   * Checks if the given customUrlPathToView points to a real file.
   * @param customUrlPathToView The path to a possible custom view.
   * @return Returns <code>true</code> if the customUrlPathToView points 
   *    to an exising file, <code>false</code> if not.
   */
  protected boolean doesViewExist(String customUrlPathToView) {
    return new File(getWebApplicationContext().getServletContext().getRealPath(customUrlPathToView)).exists();
  }

}
