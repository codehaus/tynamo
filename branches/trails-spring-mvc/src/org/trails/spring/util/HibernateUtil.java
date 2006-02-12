package org.trails.spring.util;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.trails.descriptor.IClassDescriptor;
import org.trails.descriptor.TrailsPropertyDescriptor;

/**
 * Class for HibernateUtil methods.
 * @author Lars Vonk
 *
 */
public class HibernateUtil {
  /** Logger. */
  private static Log log = LogFactory.getLog(HibernateUtil.class); 
  /**
   * Private, no instances needed.
   */
  private HibernateUtil() {
    
  }
  /**
   * Creates a DetachedCriteria for the given <code>example</code>. This method doesnot yet support primitive types
   * and booleans.
   * @param example The search by example object.
   * @param classDescriptor The {@link IClassDescriptor} describing the example Object.
   * @return A <code>DetachedCriteria</code> that can be used in e.g.
   *      {@link org.trails.persistence.PersistenceService#getInstances(org.hibernate.criterion.DetachedCriteria)}.
   */
  public static DetachedCriteria createDetachedCriteriaForObject(final Object example, IClassDescriptor classDescriptor) {
    DetachedCriteria criteria = DetachedCriteria.forClass(classDescriptor.getType());
    List propertyDescriptors = classDescriptor.getPropertyDescriptors();
    // loop over all properties.
    for (Iterator iter = propertyDescriptors.iterator(); iter.hasNext();) {
      TrailsPropertyDescriptor trailsPropertyDescriptor = (TrailsPropertyDescriptor) iter.next();
      
      if (trailsPropertyDescriptor.isSearchable()) {
        Object value = ReflectionUtils.getFieldValueByGetMethod(example, trailsPropertyDescriptor.getName());
        if (value != null) {
          log.debug("Adding property: " + trailsPropertyDescriptor.getName() + " with value: " + value + " to search criteria.");
          if (trailsPropertyDescriptor.isString()) {
            criteria.add(Restrictions.ilike(trailsPropertyDescriptor.getName(), (String) value, MatchMode.ANYWHERE));
          } else {
            // ignore booleans and primitives... not supported yet.
            if (!trailsPropertyDescriptor.isBoolean() && !trailsPropertyDescriptor.getType().isPrimitive()) {
              criteria.add(Restrictions.eq(trailsPropertyDescriptor.getName(), value));
            }
          }
        }
      }
      
    }
    return criteria;
  }
}
