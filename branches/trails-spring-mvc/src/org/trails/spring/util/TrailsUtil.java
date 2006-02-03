package org.trails.spring.util;

import org.trails.descriptor.TrailsPropertyDescriptor;

public class TrailsUtil {
  /**
   * Is the given <code>trailsPropertyDescriptor</code> a normal property, meaning
   * that is it not a Collection, and ObjectReference or an Identifier property.
   *
   * @param trailsPropertyDescriptor The property to check.
   * @return <code>true</code> if norma, <code>false</code> if not.
   */
  public static boolean isNormalProperty(TrailsPropertyDescriptor trailsPropertyDescriptor) {
    return (!trailsPropertyDescriptor.isCollection() && !trailsPropertyDescriptor.isObjectReference() && !trailsPropertyDescriptor.isIdentifier());
    
  }
}
