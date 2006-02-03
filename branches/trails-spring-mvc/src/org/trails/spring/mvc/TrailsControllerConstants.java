package org.trails.spring.mvc;

/**
 * Interface with a set constants which are used in Controllers.
 * 
 * @author Jurjan Woltman
 *
 */
public interface TrailsControllerConstants {

  /** The default command name for a command in the trails app. */
  public final static String TRAILS_COMMAND_NAME = "trailsModel";
  
  /** List of all available entities in the system. */
  public final static String TRAILS_ENTITY_LIST = "trailsEntities";
  
  /** Default paging size when listing collections. */
  public final static int DEFAULT_PAGING_SIZE = 10;
  
  // ==========================================================================
  // Default available views
  // ==========================================================================
  
  /** Default view bame for index. */
  public final static String INDEX_VIEW = "index";
  
  /** Default view bame for listings. */
  public final static String LIST_VIEW = "list";
  
  /** Default view bame for edit. */
  public final static String EDIT_VIEW = "edit";
  
  /** Default view name for search. */
  public final static String SEARCH_VIEW = "search";
  
}
