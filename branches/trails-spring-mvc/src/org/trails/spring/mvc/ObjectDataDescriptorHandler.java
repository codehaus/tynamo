package org.trails.spring.mvc;

import java.util.List;

import org.trails.descriptor.IClassDescriptor;

/**
 * An ObjectDataDescriptorHandler creates {@link org.trails.spring.mvc.ObjectDataDescriptorList}
 * that can de used for rendering instances of objects in a view.
 * 
 * @author Lars Vonk
 *
 */
public interface ObjectDataDescriptorHandler {
  /**
   * Method creates an {@link ObjectDataDescriptorList} for the given instance with
   * its corresponding {@link IClassDescriptor}.
   * 
   * @param instance The instance for which the {@link ObjectDataDescriptorList} is created.
   * @param classDescriptor The {@link IClassDescriptor} describing the given <code>instance</code>.
   * @return An {@link ObjectDataDescriptorList}.
   */
  public ObjectDataDescriptorList create(Object instance, IClassDescriptor classDescriptor);
  /**
   * Method creates an {@link ObjectDataDescriptorList} for the given instance with
   * its corresponding {@link IClassDescriptor}. This method should also resolve the child object instances.
   * 
   * @param instances The instances for which the {@link ObjectDataDescriptorList} is created.
   * @param classDescriptor The {@link IClassDescriptor} describing the given <code>instance</code>.
   * @param resolveAllChildern indicator whether or not all the childern of an instance shoudl be resolved
   *      in case of an object reference.
   * @param pageNumber The current page number.
   * @param totalNumberOfPages The total number of pages.
   * @return An {@link ObjectDataDescriptorList}.
   */  
  public ObjectDataDescriptorList create(List instances, IClassDescriptor classDescriptor, boolean resolveAllChildern, int pageNumber, int totalNumberOfPages);
}
