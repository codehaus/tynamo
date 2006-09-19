package org.trails.descriptor;

import java.util.List;

public interface DescriptorFactory
{
    // Because the interface was incomplete with regard to methods needed by the conversion of the EmbbededDescriptor
    // to a delegate, the contract was made more specific.  The missing methods could be moved to the interface, but are
    // all subclasses implementing the methods of TrailsClassDescriptor?  Potentially, and if so, that was an oversight
    // in the evolution of TrailsClassDescriptor that propagated this change.  Which came first, the chicken or the egg? :)
    public TrailsClassDescriptor buildClassDescriptor(Class type);
	
	public void setMethodExcludes(List methodExcludes);
	
	public void setPropertyExcludes(List propertyExcludes);
}
