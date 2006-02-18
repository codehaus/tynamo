package org.trails.spring.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utillity class for Reflection.
 * 
 * @author Jurjan Woltman
 * @author Lars Vonk
 */
public final class ReflectionUtils {
  
  /**
   * Logger used to produce log messages.
   */
  private static Log log = LogFactory.getLog(ReflectionUtils.class);
  
  /**
   * Prefix for get methods.
   */
  private static final String GET_PREFIX = "get";
  private static final String SET_PREFIX = "set";

  /**
   * Private constructor to prevent creating an instance of this class.
   */
  private ReflectionUtils() {
    //empty
  }
  
  /**
   * Returns the Class with the given <code>className</code>.
   * If it cannot be found a ReflectionUtilsException is thrown.
   * @param className The name of the class to find.
   * @return The Class with the given <code>className</code>.
   */
  public static Class getClass(String className) {
    Class clazz = null;
    
    try {
      clazz = Class.forName(className);
    } catch (ClassNotFoundException e) {
      throw new ReflectionUtilsException("Cannot find class of type: " + className, e);
    }
    
    return clazz;
  }
  
  /**
   * Creates an instance of the given type <code>clazz</code>.
   * @param clazz The class to create an instance from.
   * @return An instance of <code>clazz</code>.
   */
  public static Object getNewInstance(Class clazz) {
    Object returnValue = null;
    try {
      returnValue = clazz.newInstance();
    } catch (Exception e) {
      throw new ReflectionUtilsException("Cannot create an instance of type: " + clazz.getName(), e);
    }
    
    return returnValue;
  }
  /**
   * Sets a value to static field of a class, for example private field which cannot be accessed through 'normal'
   * Setter methods.
   * 
   * @param clazz the class the static field belongs to.
   * @param fieldName the name of the field that will be set.
   * @param value the value that will be set.
   */
  public static void setStaticFieldValue(Class clazz, String fieldName, Object value) {
    try {
      Field field = clazz.getDeclaredField(fieldName);
      field.setAccessible(true);
      field.set(null, value);
    } catch (Exception e) {
       throw new ReflectionUtilsException("Unable to set static field:" + fieldName + " in class:" 
                                                 + (clazz != null ? clazz.getName() : "null") 
                                                 + " to value:" + value, e);
    }
  }
  
  /**
   * Sets a value to field of an instance, for example private field which cannot be accessed through 'normal'
   * Setter methods.
   * 
   * @param instance the instance where the field belongs to.
   * @param fieldName the name of the field that will be set.
   * @param value the value that will be set.
   */
  public static void setFieldValue(Object instance, String fieldName, Object value) {
    try {
      Field field = instance.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      field.set(instance, value);
    } catch (Exception e) {
       throw new ReflectionUtilsException("Unable to set value of field: " + fieldName + " for specified instance of class: " 
                                                 + (instance != null ? instance.getClass().getName() : "unknown (instance was null)")
                                                 + " to value: " + value, e);
    }
  }
  
  /**
   * Gets a value from a static field of a Class, for example a private static field which cannot be accessed through 'normal'
   * Setter methods.
   * 
   * @param clazz The Class from which the attribute will be retrieved.
   * @param fieldName the name of the field that will be set.
   * @return Object The value of the given <code>fieldName</code>.
   */
  public static Object getStaticFieldValue(Class clazz, String fieldName) {
    try {
      Field field = clazz.getDeclaredField(fieldName);
      field.setAccessible(true);
      
      return field.get(null);
    } catch (Exception e) {
       throw new ReflectionUtilsException("Unable to get value of static field: " + fieldName + " in class: " + clazz, e);
    }
  }
  
  /**
   * Gets a value from a field of an instance, for example a private field which cannot be accessed through 'normal'
   * getter methods.
   * 
   * @param instance the instance where the field belongs to.
   * @param fieldName the name of the field that will be set.
   * @return Object The value of the given <code>fieldName</code>.
   */
  public static Object getFieldValue(Object instance, String fieldName) {
    try {
      Field field = instance.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      
      return field.get(instance);
    } catch (Exception e) {
       throw new ReflectionUtilsException("Unable to get value of field:" + fieldName + " in class: " 
                                                 + (instance != null ? instance.getClass().getName() : "unknown (instance was null)"), e);
    }
  }
  
  /**
   * Returns the value of the given <code>fieldName</code> by calling its corresponding
   * get method. If the get method throws an {@link InvocationTargetException} the 
   * {@link InvocationTargetException#getCause() cause} is inspected and dealt with as follows:
   * <p>
   * <ul>
   * <li>
   * If the {@link InvocationTargetException#getCause() cause} 
   * is an instance of {@link RuntimeException} the {@link InvocationTargetException#getCause() cause} is rethrown.
   * </li>
   * <li>
   * If the {@link InvocationTargetException#getCause() cause} was not an instance of a {@link RuntimeException}
   * then the {@link InvocationTargetException#getCause() cause} is wrapped in a {@link ReflectionUtilsException} and rethrown.
   * This is done because otherwise all callers of this method are forced to handle checked exceptions, where normally
   * get methods will not result in checked exceptions. One can still retrieve the actual checked exception as it 
   * is wrapped as cause in the {@link ReflectionUtilsException}.
   * </li>
   * </ul>
   * <br> 
   * @param instance The instance from which the value will be retrieved.
   * @param fieldName The name of the attribute which value will be retrieved via its get method.
   * @return The value returned by the get method.
   * @throws IllegalArgumentException if instance of fieldName is null.
   * @throws ReflectionUtilsException If invoking the get method failes.
   */
  public static Object getFieldValueByGetMethod(Object instance, String fieldName) throws IllegalArgumentException, ReflectionUtilsException {
    
    if (instance == null) {
      throw new IllegalArgumentException("Parameter [instance] may not be null.");
    }
    
    if (fieldName == null) {
      throw new IllegalArgumentException("Parameter [fieldName] may not be null.");
    }
    
    //Get the class of the given instance.
    Class clazz = instance.getClass();

    try {
    
      // Get the corresponding getMethod of the fieldName. 
      Method method = clazz.getMethod(createGetMethod(fieldName), (Class[])null);

      // Call the method.
      return method.invoke(instance, (Object[])null);
      
    } catch (InvocationTargetException e) {
      // If this exception is thrown it means that the called
      // method threw an exception. If this exception is a Runtime we throw the
      // original exception. If i is not a Runtime we rethrow it as a ReflectionUtilsException.
      if (e.getCause() instanceof RuntimeException) {
        throw (RuntimeException) e.getCause();
      } else {
        throw new ReflectionUtilsException("Unable to get value of field:" + fieldName + " through the get method in class: " 
            + clazz.getName(), e.getCause());
      }
    } catch (Exception e) {
      throw new ReflectionUtilsException("Unable to get value of field:" + fieldName + " through the get method in class: " 
          + clazz.getName(), e);
    } 
  }

  /**
   * Sets the given value onto the given object instance through its corresponding set method as
   * defined per Java Bean specification. If an {@link InvocationTargetException} is thrown it
   * is dealt with as follows:
   * <p>
   * <ul>
   * <li>
   * If the {@link InvocationTargetException#getCause() cause} 
   * is an instance of {@link RuntimeException} the {@link InvocationTargetException#getCause() cause} is rethrown.
   * </li>
   * <li>
   * If the {@link InvocationTargetException#getCause() cause} was not an instance of a {@link RuntimeException}
   * then the {@link InvocationTargetException#getCause() cause} is wrapped in a {@link ReflectionUtilsException} and rethrown.
   * This is done because otherwise all callers of this method are forced to handle checked exceptions, where normally
   * get methods will not result in checked exceptions. One can still retrieve the actual checked exception as it 
   * is wrapped as cause in the {@link ReflectionUtilsException}.
   * </li>
   * </ul>
   * <br> 
   * @param instance The instance from which the value will be retrieved.
   * @param fieldName The name of the attribute which value will be retrieved via its get method.
   * @return The value returned by the get method.
   * @throws IllegalArgumentException if instance of fieldName is null.
   * @throws ReflectionUtilsException If invoking the get method failes.
   */
  public static void setFieldValueBySetMethod(Object instance, String fieldName, Object value, Class classNameOfValue, boolean includeSuperClasses) throws IllegalArgumentException, ReflectionUtilsException {
    
    if (instance == null) {
      throw new IllegalArgumentException("Parameter [instance] may not be null.");
    }
    
    if (fieldName == null) {
      throw new IllegalArgumentException("Parameter [fieldName] may not be null.");
    }
    
    //Get the class of the given instance.
    Class clazz = instance.getClass();

    try {
    
      // Get the corresponding getMethod of the fieldName. 
      Method method = null;

      if (includeSuperClasses) {
        method = clazz.getMethod(createSetMethod(fieldName), new Class[] {classNameOfValue});
      } else {
        method = clazz.getDeclaredMethod(createSetMethod(fieldName), new Class[] {classNameOfValue});
      }

      // Call the method.
      if (classNameOfValue.isArray()) {
        method.invoke(instance, value);
      } else {
        method.invoke(instance, new Object[]{value});
      }
      
    } catch (InvocationTargetException e) {
      // If this exception is thrown it means that the called
      // method threw an exception. If this exception is a Runtime we throw the
      // original exception. If it is not a Runtime we rethrow it as a ReflectionUtilsException.
      if (e.getCause() instanceof RuntimeException) {
        throw (RuntimeException) e.getCause();
      } else {
        throw new ReflectionUtilsException("Unable to set value of field:" + fieldName + " to the value: " + value + "through the set method in class: " 
            + clazz.getName(), e.getCause());
      }
    } catch (Exception e) {
      throw new ReflectionUtilsException("Unable to set value of field:" + fieldName + " to the value: " + value + " through the set method in class: " 
          + clazz.getName(), e);
    } 
  }
  
  
  /**
   * Gets all settable fields names of the given <code>clazz</code>.
   * @param clazz
   * @param includeSuperClasses
   * @return
   */
  public static List<String> getSettableFieldNames(Class clazz, boolean includeSuperClasses) {
    
    List<String> settableFields = new ArrayList<String>();
    Field[] fields = null;
    
    if (includeSuperClasses) {
      fields = clazz.getDeclaredFields();
    } else {
      fields = clazz.getFields();
    }
    log.debug("Fields: " + fields.length);
    for (int i = 0; i < fields.length; i++) {
      if (hasSetMethodForField(fields[i], clazz)) {
        settableFields.add(fields[i].getName());
      }
    }
    
    return settableFields;
  }
  
  // ==========================================================================
  // Private convenience methods.
  // ==========================================================================
  
  /**
   * 
   * @param field
   * @param clazz
   */
  private static boolean hasSetMethodForField(Field field, Class clazz) {
    boolean hasSetMethod = false; 
    String setMethodName = createSetMethod(field.getName());
    try {
      if (clazz.getMethod(setMethodName, new Class[] {field.getType()}) != null ) {
        log.debug("Set method: " + setMethodName + "( "+ field.getType() +") found in class: " + clazz);
        hasSetMethod = true;
      } else {
        log.debug("Set method: " + setMethodName + "( "+ field.getType() +") not found in class: " + clazz);
      }
    } catch (SecurityException e) {
      throw new ReflectionUtilsException("SecurityException while getting setMethod: " + setMethodName + " on class: " + clazz, e);
    } catch (NoSuchMethodException e) {
      // nothing found.
      log.debug("Set method: " + setMethodName + "( "+ field.getType() +") not found in class: " + clazz);
      hasSetMethod = false;
    }
    return hasSetMethod;
  }
  /**
   * Returns the name of the get method as per JavaBean convention.
   * @param fieldName the name of the field.
   * @return The corresponding getMethod.
   */
  private static String createGetMethod(String fieldName) {
    String methodName = ReflectionUtils.GET_PREFIX + StringUtils.capitalise(fieldName);
    return methodName;
  }
  /**
   * Returns the name of the set method as per JavaBean convention.
   * @param fieldName the name of the field.
   * @return The corresponding setMethod.
   */
  private static String createSetMethod(String fieldName) {
    String methodName = ReflectionUtils.SET_PREFIX + StringUtils.capitalise(fieldName);
    return methodName;
  }
  
}
