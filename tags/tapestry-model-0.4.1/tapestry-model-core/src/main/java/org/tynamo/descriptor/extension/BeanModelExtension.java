package org.tynamo.descriptor.extension;

import org.tynamo.PageType;
import org.tynamo.descriptor.Descriptor;
import org.tynamo.util.BeanModelUtils;

import java.util.HashMap;
import java.util.Map;

public class BeanModelExtension implements DescriptorExtension
{

	private final static String defaultKey = PageType.DEFAULT.getContextKey();

	private Map<String, String> reorderMap = new HashMap<String, String>();
	private Map<String, String> includeMap = new HashMap<String, String>();
	private Map<String, String> excludeMap = new HashMap<String, String>();

	private boolean applyDefaultExclusions = true;

	private BeanModelExtension(){}

	public String getReorderPropertyNames(String contextKey)
	{
		return reorderMap.containsKey(contextKey) ? reorderMap.get(contextKey) : getReorderPropertyNames();
	}

	public String getIncludePropertyNames(String contextKey)
	{
		return includeMap.containsKey(contextKey) ? includeMap.get(contextKey) : getIncludePropertyNames();
	}

	public String getExcludePropertyNames(String contextKey)
	{
		return excludeMap.containsKey(contextKey) ? excludeMap.get(contextKey) : getExcludePropertyNames();
	}

	public void setReorderPropertyNames(String contextKey, String properties)
	{
		reorderMap.put(contextKey, properties);
	}

	public void setIncludePropertyNames(String contextKey, String properties)
	{
		includeMap.put(contextKey, properties);
	}

	public void setExcludePropertyNames(String contextKey, String properties)
	{
		excludeMap.put(contextKey, properties);
	}


	public String getReorderPropertyNames()
	{
		return reorderMap.get(defaultKey);
	}

	public String getIncludePropertyNames()
	{
		return includeMap.get(defaultKey);
	}

	public String getExcludePropertyNames()
	{
		return excludeMap.get(defaultKey);
	}

	public void setReorderPropertyNames(String properties)
	{
		reorderMap.put(defaultKey, properties);
	}


	public void addToIncludeMap(String contextKey, String newProperty)
	{
		String includePropertyNames = includeMap.get(contextKey);
		includePropertyNames = includePropertyNames == null ? newProperty : BeanModelUtils.join(includePropertyNames, newProperty);
		setIncludePropertyNames(contextKey, includePropertyNames);
	}

	public void addToExcludeMap(String contextKey, String newProperty)
	{
		String excludePropertyNames = excludeMap.get(contextKey);
		excludePropertyNames = excludePropertyNames == null ? newProperty : BeanModelUtils.join(excludePropertyNames, newProperty);
		setExcludePropertyNames(contextKey, excludePropertyNames);
	}

	public boolean isApplyDefaultExclusions()
	{
		return applyDefaultExclusions;
	}

	public void setApplyDefaultExclusions(boolean applyDefaultExclusions)
	{
		this.applyDefaultExclusions = applyDefaultExclusions;
	}

	public static BeanModelExtension obtainBeanModelExtension(Descriptor descriptor)
	{
		BeanModelExtension beanModelExtension = descriptor.getExtension(BeanModelExtension.class);
		if (beanModelExtension == null)
		{
			beanModelExtension = new BeanModelExtension();
			descriptor.addExtension(BeanModelExtension.class, beanModelExtension);
		}

		return beanModelExtension;
	}
}