package org.trails.spring.mvc;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.validator.ClassValidator;
import org.hibernate.validator.InvalidValue;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.trails.descriptor.DescriptorService;
import org.trails.descriptor.IClassDescriptor;
import org.trails.descriptor.IPropertyDescriptor;
import org.trails.descriptor.TrailsPropertyDescriptor;
import org.trails.persistence.PersistenceService;
import org.trails.spring.mvc.commands.TrailsCommand;
import org.trails.spring.persistence.PagingCriteria;
import org.trails.spring.util.HibernateUtil;
import org.trails.spring.util.ReflectionUtils;
import org.trails.spring.util.TrailsUtil;
import org.trails.validation.ValidationException;
/**
 * The TrailsMultiActionController is the controller as defined in the MVC layers. This controller
 * has the default implementations of all the actions in a Trails application.
 * 
 * @author Lars Vonk
 * @author Jurjan Woltman
 */
public class TrailsMultiActionController extends MultiActionController {
  
  /** Logger used to produce log messages. */
  private static Log log = LogFactory.getLog(TrailsMultiActionController.class);
  
  /** Config parameter for pagingsize. */
  private int pagingSize;  
  
  // ==========================================================================
  // Trails Services references
  // ==========================================================================

  /** 
   * The DescriptorService that is needed to retrieve the descriptions of all the 
   * persistent types that are present. 
   */
  private DescriptorService descriptorService = null;
  
  /** The persisentence service that is needed to perform the necessary database actions. */
  private PersistenceService persistenceService = null;
  
  // ==========================================================================
  // Handler references
  // ==========================================================================
  /** The handler for creating {@link ObjectDataDescriptor}'s. */
  private ObjectDataDescriptorHandler dataDescriptorHandler = null;
  
  // ==========================================================================
  // Trails default model operations
  // ==========================================================================
  
  /**
   * Method returns a <code>ModelAndView</code> containing <i>index</i> as viewName
   * , the {@link #DEFAULT_COMMAND_NAME} as modelName and all the <i>main</i> entities as modelObject.
   * @param request The servlets request.
   * @param response The servlets response.
   * @return The ModelAndView that will list all the <i>main</i> entities.
   */
  public ModelAndView listAllEntities(HttpServletRequest request, HttpServletResponse response) {
    log.info("listing all entities");
    return new ModelAndView(TrailsControllerConstants.INDEX_VIEW, TrailsControllerConstants.TRAILS_ENTITY_LIST, getDescriptorService().getAllDescriptors());
  }
  
  /**
   * Methods locates and lists all the instances of a certain type (or class). The type that is used
   * to retrieve all the instances of is retrieved from the given <code>command</code> parameter.
   * <p>
   * This method returns a ModelAndView with <i>list</i> as viewName, the {@link #DEFAULT_COMMAND_NAME} as modelName
   * and a {@link List} all the instances of the {@link TrailsCommand#getType() type} present in the given <code>command</code> as modelObject.
   * 
   * @param request The servlets request.
   * @param response The servlets response.
   * @return The ModelAndView that will list all the instances of a certain type.
   */
  public ModelAndView listAllInstances(HttpServletRequest request, HttpServletResponse response, TrailsCommand command) {
    IClassDescriptor classDescriptor = getSelectedClassDescriptor(command);
    
    List instances = null;
    int totalNumberOfPages = -1;

    if (getPagingSize() > 0) {
      PagingCriteria pagingCriteria = new PagingCriteria(classDescriptor, command.getPageNumber(), getPagingSize());
      instances = getPersistenceService().getInstances(pagingCriteria);
      totalNumberOfPages = pagingCriteria.getTotalPageNumbers();
    } else {
      instances = getPersistenceService().getAllInstances(classDescriptor.getType());
    }
    
    List propertiesDescriptors = classDescriptor.getPropertyDescriptors();
    ObjectDataDescriptorList objectDataDescriptorListParent = getDataDescriptorHandler().create(instances, classDescriptor, command.getPageNumber(), totalNumberOfPages);
    ModelAndView nextView = new ModelAndView(TrailsControllerConstants.LIST_VIEW, TrailsControllerConstants.TRAILS_COMMAND_NAME, objectDataDescriptorListParent);
    nextView.addObject(TrailsControllerConstants.TRAILS_ENTITY_LIST, getDescriptorService().getAllDescriptors());
    
    return nextView;
  }
  
  /**
   * This method returns a ModelAndView with <i>search</i> as viewName, the {@link #DEFAULT_COMMAND_NAME} as modelName
   * and an empty instance of the <code>IClassDescriptor</code> of the {@link TrailsCommand#getType() type} described
   * by the given <code>command</code> as modelObject.
   *
   * @param request The request.
   * @param response The response.
   * @return The ModelAndView that allows one to enter search criteria for seaching instances of a certain type (or class).
   */
  public ModelAndView prepareToSearchInstances(HttpServletRequest request, HttpServletResponse response, TrailsCommand command) {
    Object searchInstance = ReflectionUtils.getNewInstance(getSelectedClassDescriptor(command).getType());
    ObjectDataDescriptorList objectDataDescriptorList = getDataDescriptorHandler().createAndResolveChildern(searchInstance, getSelectedClassDescriptor(command));
    
    ModelAndView nextView = new ModelAndView(TrailsControllerConstants.SEARCH_VIEW, TrailsControllerConstants.TRAILS_COMMAND_NAME, objectDataDescriptorList); 
    nextView.addObject(TrailsControllerConstants.TRAILS_ENTITY_LIST, getDescriptorService().getAllDescriptors());

    return nextView;
  }
  
  /**
   * Prepares to edits or adds an instance of a {@link TrailsCommand#getType()} that can be retrieved
   * from the given <code>command</code>. 
   * <p>
   * This method determines on the data in the given <code>command</code> whether or
   * not this action is to prepare to edit an instance or to add an instance. If there is no value for an
   * identifier property present in the given <code>command</code> its considered to be
   * a new instance (as database identifiers are typically set when inserting an entity in the database).
   * If there is an identifier property present then the instance is considered to an
   * existing instance and is updated in the database. 
   * <p>
   * This method returns a ModelAndView with {@link TrailsControllerConstants.EDIT_VIEW} as viewName
   * , the {@link #DEFAULT_COMMAND_NAME} as modelName and an {@link ObjectDataDescriptorList} containing 
   * information about the instance that is to be added or edited as modelObject.
   * 
   * @param request The servlet request.
   * @param response The response.
   * @param command The {@link TrailsCommand command} object. 
   * @return A ModelAndView that allows one to edit an existing instance or add a new instance of a certain type.
   */
  public ModelAndView prepareToEditOrAddAnInstance(HttpServletRequest request, HttpServletResponse response, TrailsCommand command) {
    ModelAndView modelAndView = null;
    
    // Holds the instance to be added or edited.
    Object instance = null;
    
    // Get the class type of the instance to be added or edited.
    IClassDescriptor classDescriptor = getSelectedClassDescriptor(command);

    // Check if there is an instanceId present in the command object. If there is an instanceId 
    // we are dealing with an existing instance otherwise we are adding a new instance.
    Serializable instanceId = getId(classDescriptor, command);
    
    // instanceId present => editing a existing instance.
    if(instanceId != null) {
      // get the instance.
      instance = getPersistenceService().getInstance(classDescriptor.getType(), instanceId);
      modelAndView = handleObjectEdit(request, response, classDescriptor, instance);
    } else {
      // Create a new 'empty' instance of the correct type so it can be used
      // in the ObjectDataDescriptorList to be rendered on the View.
      instance = ReflectionUtils.getNewInstance(classDescriptor.getType());
      modelAndView = handleObjectCreation(request, response, classDescriptor, instance);
    }
    
    return modelAndView;  
  }
  /**
   * Searches instances of a {@link TrailsCommand#getType() type} defined in the given <code>command</code>.
   * <p>
   * This method returns a ModelAndView with <i>list</i> as viewName, the {@link #DEFAULT_COMMAND_NAME} as modelName
   * and an {@link ObjectDataDescriptorList} containing information about the instances that are found with the given search criteria as modelObject.
   * 
   * @param request The request.
   * @param response The response.
   * @param command The TrailsCommand object.
   * @return The ModelAndView containg all instances that are found.
   */
  public ModelAndView searchInstances(HttpServletRequest request, HttpServletResponse response, TrailsCommand command) {
    // Get the class type of the instance that is used to enter the search criteria.
    IClassDescriptor classDescriptor = getSelectedClassDescriptor(command);
    
    // create a new instance that is used as search criteria.
    Object searchInstance = ReflectionUtils.getNewInstance(classDescriptor.getType());
    TrailsServletRequestDataBinder dataBinder = new TrailsServletRequestDataBinder(getDescriptorService(), getPersistenceService());
    dataBinder.bind(request, classDescriptor, searchInstance);
    
    // now retrieve all the instances.
    DetachedCriteria detachedCriteria = HibernateUtil.createDetachedCriteriaForObject(searchInstance, classDescriptor);
    List instances = getPersistenceService().getInstances(detachedCriteria);
    
    ModelAndView nextView = new ModelAndView(TrailsControllerConstants.LIST_VIEW, TrailsControllerConstants.TRAILS_COMMAND_NAME, new ObjectDataDescriptorList(instances, classDescriptor)); 
    nextView.addObject(TrailsControllerConstants.TRAILS_ENTITY_LIST, getDescriptorService().getAllDescriptors());
    
    return nextView;
  }
  
  /**
   * Saves an instance present in the request in the database. If the instance already existed
   * its updated. 
   * <p>
   * If this method updates an existing instance it returns a ModelAndView with {@link TrailsControllerConstants.EDIT_VIEW}
   * as viewName, the {@link #DEFAULT_COMMAND_NAME} as modelName
   * and an {@link ObjectDataDescriptorList} containing the updated instance as modelObject.
   * <br>
   * If this method successfully added a new instance it forwards the method call to
   * {@link #listAllInstances(HttpServletRequest, HttpServletResponse, TrailsCommand)}
   * with the given parameters as parameters for the method call. If a validation error
   * occurred the method forwards to 
   * {@link #prepareToEditOrAddAnInstance(HttpServletRequest, HttpServletResponse, TrailsCommand)}.   * 
   * 
   * @param request The request.
   * @param response The response.
   * @param command The {@link TrailsCommand} command object.
   * @return The ModelAndView, see the {@link #saveInstance(HttpServletRequest, HttpServletResponse, TrailsCommand) method}
   *      description for more information.
   */
  public ModelAndView saveInstance(HttpServletRequest request, HttpServletResponse response, TrailsCommand command) {
    ModelAndView nextView  = null;

    TrailsServletRequestDataBinder dataBinder = getDataBinder();
    IClassDescriptor classDescriptor = getSelectedClassDescriptor(command);
    Serializable instanceId = getId(classDescriptor, command);
    
    Object instance = null;
    
    // if there was an instanceId it means an existing instance must be updated.
    if(instanceId != null && !instanceId.equals("")) {
      instance = getPersistenceService().getInstance(classDescriptor.getType(), instanceId);
      instance = bindAndValidate(request, dataBinder, classDescriptor, instance);
      
      // if updating an existing instance succeeded we forward to EDIT_VIEW.
      if (!dataBinder.hasErrors()) {
        log.debug("Updating instance: " + instance);
        nextView = handleObjectSave(request, response, classDescriptor, instance, dataBinder);
      } 
      
    } else {
      instance = bindAndValidate(request, dataBinder, classDescriptor);

      // if creating a new instance succeeded we forward to listAllInstances.
      if (!dataBinder.hasErrors()) {
        log.debug("Saving instance: " + instance);
        nextView = handleObjectSave(request, response, classDescriptor, instance, dataBinder);
      }
    }  

    // if it has error we forward to prepareToEditOrAddAnInstance.
    if (dataBinder.hasErrors()) {
      log.debug("errors: " + dataBinder.getErrors());
      // we return to the edit or add page in case of an error.
      // TODO: preserve the entered values of the submitted form.
      nextView = handleObjectSaveValidationErrors(request, response, classDescriptor, instance, dataBinder.getErrors());
    }
    
    // always add the complete entity list.
    nextView.addObject(TrailsControllerConstants.TRAILS_ENTITY_LIST, getDescriptorService().getAllDescriptors());
    
    return nextView;  
  }

  /**
   * Delete the instance present in the request from the database. After the deletion 
   * a list of all 'left' instances is shown. 
   * 
   * @param request The request.
   * @param response The response.
   * @param command The {@link TrailsCommand} command object.
   * @return a ModelAndView with the list of all 'left' instance for the selectedClassDescriptor. 
   */
  public ModelAndView deleteInstance(HttpServletRequest request, HttpServletResponse response, TrailsCommand command) {
    IClassDescriptor classDescriptor = getSelectedClassDescriptor(command);

    Serializable instanceId = getId(classDescriptor, command);
    
    if (instanceId != null && !instanceId.equals("")) {
      
      Object instance = getPersistenceService().getInstance(classDescriptor.getType(), instanceId);
      if (instance != null) {
        getPersistenceService().remove(instance);  
      }
    }
    
    return listAllInstances(request, response, command);
  }  
  
  // ==========================================================================
  // Getters and setters needed for dependency injection.
  //===========================================================================

  /**
   * Returns the descriptorService.
   * @return Returns the descriptorService.
   */
  public DescriptorService getDescriptorService() {
    return descriptorService;
  }

  /**
   * Sets the descriptorService.
   * @param descriptorService The descriptorService to set.
   */
  public void setDescriptorService(DescriptorService descriptorService) {
    this.descriptorService = descriptorService;
  }
  
  /**
   * Returns the pagingSize.
   * @return Returns the pagingSize.
   */
  public int getPagingSize() {
    return pagingSize;
  }

  /**
   * Sets the pagingSize.
   * @param pagingSize The pagingSize to set.
   */
  public void setPagingSize(int pagingSize) {
    this.pagingSize = pagingSize;
  }  
  
  /**
   * Returns the persistenceService.
   * @return Returns the persistenceService.
   */
  public PersistenceService getPersistenceService() {
    return persistenceService;
  }
  /**
   * Sets the persistenceService.
   * @param persistenceService The persistenceService to set.
   */
  public void setPersistenceService(PersistenceService persistenceService) {
    this.persistenceService = persistenceService;
  }
  
  /**
   * @return Returns the dataDescriptorHandler.
   */
  public ObjectDataDescriptorHandler getDataDescriptorHandler() {
    return dataDescriptorHandler;
  }

  /**
   * @param dataDescriptorHandler The dataDescriptorHandler to set.
   */
  public void setDataDescriptorHandler(ObjectDataDescriptorHandler dataDescriptorHandler) {
    this.dataDescriptorHandler = dataDescriptorHandler;
  }
  
  // ==========================================================================
  // Protected methods.
  //===========================================================================


  /**
   * Returns correct type of id to retrieve an instance or null in case id could not determined.
   * 
   * @param descriptor descriptor of the class
   * @param command command object with selection parameters
   * @return
   */
  protected Serializable getId(IClassDescriptor descriptor, TrailsCommand command) {
    Serializable result = null;
    
    if(descriptor != null && command.getId() != null && !command.getId().equals("")) {
      IPropertyDescriptor identifierDescriptor = descriptor.getIdentifierDescriptor();
      log.debug("Identifier property: " + identifierDescriptor.getName() + ", value: " + command.getId());
      
      if(identifierDescriptor.isNumeric()) {
        result = Integer.valueOf(command.getId());
      } else if (identifierDescriptor.isString()) {
        result = String.valueOf(command.getId());
      } // There should be more checks here to convert to the correct type.
    }
    
    return result;
  }

  /**
   * Adds a Trails model for the given instance to the supplied ModelAndView. The class
   * descriptor is derived from the TrailsCommand instance.
   * 
   * @param command the Trails command.
   * @param instance the domain object instance.
   * @param mav the model and view.
   */
  protected void addTrailsModelToModelAndView(TrailsCommand command, Object instance, ModelAndView mav) {
	  addTrailsModelToModelAndView(getSelectedClassDescriptor(command), instance, mav);
  }

  /**
   * Adds a Trails model for the given instance to the supplied ModelAndView. The class
   * descriptor is used to populate the model.
   * 
   * @param classDescriptor the class descriptor.
   * @param instance the domain object instance.
   * @param mav the model and view.
   */
  protected void addTrailsModelToModelAndView(IClassDescriptor classDescriptor, Object instance, ModelAndView mav) {
	  mav.addObject(TrailsControllerConstants.TRAILS_COMMAND_NAME, getDataDescriptorHandler().createAndResolveChildern(instance, classDescriptor));
  }

  /**
   * Returns a new instance of a Trails data binder.
   * Override this method if you want an existing databinder to be used.
   * @return the data binder instance.
   */
  protected TrailsServletRequestDataBinder getDataBinder() {
	  return new TrailsServletRequestDataBinder(getDescriptorService(), getPersistenceService());
  }
  
  /**
   * Handles creating of a new object instance.
   * 
   * @param request the request
   * @param response the response
   * @param classDescriptor the class descriptor
   * @param instance the created instance
   * @return the next view
   */
  protected ModelAndView handleObjectCreation(HttpServletRequest request, HttpServletResponse response, IClassDescriptor classDescriptor, Object instance) {
    ModelAndView result;
    
    ObjectDataDescriptorList objectTable = getDataDescriptorHandler().createAndResolveChildern(instance, classDescriptor);
    result = new ModelAndView(TrailsControllerConstants.EDIT_VIEW, TrailsControllerConstants.TRAILS_COMMAND_NAME, objectTable);
    result.addObject(TrailsControllerConstants.TRAILS_ENTITY_LIST, getDescriptorService().getAllDescriptors());

    return result;
  }

  /**
   * Handles editing of an existing object instance.
   * 
   * @param request the request
   * @param response the response
   * @param classDescriptor the class descriptor
   * @param instance the created instance
   * @return the next view
   */
  protected ModelAndView handleObjectEdit(HttpServletRequest request, HttpServletResponse response, IClassDescriptor classDescriptor, Object instance) {
    ModelAndView result;
    
    ObjectDataDescriptorList objectTable = getDataDescriptorHandler().createAndResolveChildern(instance, classDescriptor);
    result = new ModelAndView(TrailsControllerConstants.EDIT_VIEW, TrailsControllerConstants.TRAILS_COMMAND_NAME, objectTable);
    result.addObject(TrailsControllerConstants.TRAILS_ENTITY_LIST, getDescriptorService().getAllDescriptors());

    return result;
  }


  /**
   * Handles saving of a domain object.
   * 
   * @param request the request
   * @param response the response
   * @param classDescriptor the class descriptor
   * @param instance the instance to be stored
   * @return the next view
   */
  protected ModelAndView handleObjectSave(HttpServletRequest request, HttpServletResponse response
                                          , IClassDescriptor classDescriptor, Object instance, TrailsServletRequestDataBinder dataBinder) {
    ModelAndView modelAndView = null;
    try {
      getPersistenceService().save(instance);
      modelAndView = listAllInstances(request, response, new TrailsCommand(instance, classDescriptor));
    } catch (ValidationException e) {
      
      dataBinder.getErrors().addError(new FieldError(dataBinder.getErrors().getObjectName()
          , null
          , null
          , false, null, null, e.getMessage()));
    }
    return modelAndView; 
  }  

  /**
   * Binds data from the request using the provided binder instance and class descriptor, then validates
   * the bound object using the {@link ClassValidator} from Hibernate. At the end it validates the bound
   * data using any validators configured for this controller using the
   * {@link #performValidation(Object, BindException)} method.
   * 
   * @param request the request.
   * @param binder the binder.
   * @param classDescriptor the class descriptor.
   * @return the newly bound instance. Any errors are contained in the binder instance.
   */
  protected Object bindAndValidate(HttpServletRequest request, TrailsServletRequestDataBinder binder, IClassDescriptor classDescriptor) {
    Object result = binder.bind(request, classDescriptor);
    // validate the Hibernate validators.
    performHibernateValidation(binder.getErrors(), classDescriptor, result);
    // validate by the declared Validators.
    performValidation(result, binder.getErrors());
    return result;
  }

  /**
   * Binds data from the request using the provided binder instance and class descriptor on the provided instance, 
   * then validates the bound data using any validators configured for this controller using the
   * {@link #performValidation(Object, BindException)} method.
   * 
   * @param request the request.
   * @param binder the binder.
   * @param classDescriptor the class descriptor.
   * @param instance The instance bound onto.
   * @return the newly bound instance. Any errors are contained in the binder instance.
   */
  protected Object bindAndValidate(HttpServletRequest request, TrailsServletRequestDataBinder binder, IClassDescriptor classDescriptor, Object instance) {
    Object result = binder.bind(request, classDescriptor, instance);
    // validate the Hibernate validators.
    performHibernateValidation(binder.getErrors(), classDescriptor, result);
    // validate by the declared Validators.
    performValidation(result, binder.getErrors());
    return result;
  }


  /**
   * Performs validation using any configured validators.
   * 
   *  The validation is done here instead of in generic Spring code because here the TrailsCommand object
   *  has been converted into an actual domain object.
   * 
   * @param command the command object.
   * @param errors the errors container.
   */
  protected void performValidation(Object command, BindException errors) {
    if (getValidators() != null) {
      for (int i = 0; i < getValidators().length; i++) {
        Validator validator = getValidators()[i]; 
        if (validator.supports(command.getClass())) {
          ValidationUtils.invokeValidator(validator, command, errors);
        }
      }
    }
  }
  /**
   * Retrieves the IClassDescriptor from the DescriptorService using the 
   * 'type' attribute in given TrailsCommand.
   * TODO : add extra exception handling if descriptor could not retrieved.
   */
  protected IClassDescriptor getSelectedClassDescriptor(TrailsCommand command) {
    return getSelectedClassDescriptor(command.getType());
  }

  /**
   * Retrieves the IClassDescriptor from the DescriptorService using the 
   * 'type' attribute in given TrailsCommand.
   * TODO : add extra exception handling if descriptor could not retrieved.
   */
  protected IClassDescriptor getSelectedClassDescriptor(Class clazz) {
    // If command.getType() is null, or doesnot result in an IClassDescriptor it has to be a programming exception.
    // In that case we just let it result in a Runtime and do not peform any unnesesary checks.
    return getDescriptorService().getClassDescriptor(clazz);
  }

  /**
   * Performs validation using the Hibernate annotations. This is done explicitly
   * so we have more control over it.
   * 
   * @param errors The errors from the binder.
   * @param classDescriptor the classDescriptor describing the instance
   * @param instance The instance to be validated.
   */
  private void performHibernateValidation(BindException errors, IClassDescriptor classDescriptor, Object instance) {
    ClassValidator validator = new ClassValidator(classDescriptor.getType());
    InvalidValue[] invalidValues = validator.getInvalidValues(instance);
    
    for (InvalidValue invalidValue : invalidValues) {
      String message = invalidValue.getMessage();
      IPropertyDescriptor propertyDescriptor = classDescriptor.getPropertyDescriptor(invalidValue.getPropertyName());
      if (propertyDescriptor != null) {
        message = propertyDescriptor.getDisplayName() + " " + invalidValue.getMessage();
      }
      errors.addError(new FieldError(errors.getObjectName()
                                     , invalidValue.getPropertyName()
                                     , invalidValue.getValue()
                                     , false, null, null, message));
    }
  }  

  /**
   * Handles saving of an object with validation errors.
   * 
   * @param request the request
   * @param response the response
   * @param classDescriptor the class descriptor
   * @param instance the instance to be stored
   * @param errors the errors
   * @return the next view
   */
  private ModelAndView handleObjectSaveValidationErrors(HttpServletRequest request, HttpServletResponse response, IClassDescriptor classDescriptor, Object instance, BindException errors) {
    ModelAndView modelAndView = null;

    TrailsCommand command = new TrailsCommand(instance, classDescriptor);
    modelAndView = handleObjectEdit(request, response, classDescriptor, instance);

    // add the errors
    Map model = errors.getModel();
    model.putAll(modelAndView.getModel());
    modelAndView.getModel().putAll(model);      
    // set the errornous attribtues in the ObjectDataDescriptorList.
    ObjectDataDescriptorList dataDescriptorList = (ObjectDataDescriptorList) model.get(TrailsControllerConstants.TRAILS_COMMAND_NAME);
    // there should be exactly one object, if not it might be deleted in the mean while for now we log and ignore this.
    if (dataDescriptorList.getRows().size() == 1) {
    ObjectDataDescriptor objectDataDescriptor = dataDescriptorList.getRows().get(0);
      // overwrite the errournous attributes.
      for (Iterator iter = objectDataDescriptor.getColumns().iterator(); iter.hasNext();) {
        PropertyDataDescriptor propertyDataDescriptor = (PropertyDataDescriptor) iter.next();
        TrailsPropertyDescriptor trailsPropertyDescriptor = (TrailsPropertyDescriptor) propertyDataDescriptor.getPropertyDescriptor();
        if (TrailsUtil.isNormalProperty(trailsPropertyDescriptor)) {
          // retrieve the value from the request.
          String value = request.getParameter(trailsPropertyDescriptor.getName());
          log.debug("Cheking property: " + trailsPropertyDescriptor.getName() + " for errors.");
          if (errors.getFieldError(trailsPropertyDescriptor.getDisplayName()) != null) {
            log.debug("Property: " + trailsPropertyDescriptor.getName() + " contained an invalid value.");
            propertyDataDescriptor.setValueInvalid(true);
            // now overwrite the value with value from the request
            propertyDataDescriptor.setValue(value);
          } else if (trailsPropertyDescriptor.isDate()) {
            propertyDataDescriptor.setValue(convertToDate(value, trailsPropertyDescriptor.getFormat()));
          } else {
            propertyDataDescriptor.setValue(value);
          }
        }
      }
    } else {
      log.warn("There should be exactly one instance of : " + classDescriptor.getType() 
              + " in the ObjectDataDescriptorList. current number: " + dataDescriptorList.getRows().size());
    }
    return modelAndView;
  }
  /**
   * 
   * @param value
   * @return
   */
  private Date convertToDate(String value, String format) {
    Date retVal = null;
    SimpleDateFormat dateFormat = new SimpleDateFormat(format);

    try {
      retVal = (Date) (value != null ? dateFormat.parse(value) : value);
    } catch (ParseException e) {
      log.debug("could not parse date.");
    }
    return retVal;
  }
}
