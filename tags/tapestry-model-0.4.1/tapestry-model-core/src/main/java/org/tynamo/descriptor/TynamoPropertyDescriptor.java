package org.tynamo.descriptor;

public interface TynamoPropertyDescriptor extends Descriptor
{
	public static final int DEFAULT_LENGTH = 255;

	/**
	 * @return
	 */
	public Class getPropertyType();

	/**
	 * @return
	 */
	public boolean isNumeric();

	public boolean isBoolean();

	/**
	 * @return
	 */
	public boolean isDate();

	/**
	 * @return
	 */
	public boolean isString();

	/**
	 * @return
	 */
	public boolean isObjectReference();

	/**
	 * @return is transient
	 * 
	 * @since 0.4.0
	 */
	// Briefly introduced in 0.4.0, to be removed in 0.5.0. It's not needed in the common descriptor
	@Deprecated
	public boolean isTransient();

	/**
	 * @param transient The transietn to set.
	 */
	public void setTransient(boolean value);

	/**
	 * @return Returns the required.
	 */
	public boolean isRequired();

	/**
	 * @param required The required to set.
	 */
	public void setRequired(boolean required);

	/**
	 * @return
	 */
	public boolean isReadOnly();

	/**
	 * @param readOnly The readOnly to set.
	 */
	public void setReadOnly(boolean readOnly);

	/**
	 * @return
	 */
	public String getName();

	public void setName(String name);

	public int getLength();

	public void setLength(int length);

	public abstract boolean isLarge();

	public abstract void setLarge(boolean Large);

	public String getFormat();

	public void setFormat(String format);

	public boolean isSearchable();

	public void setSearchable(boolean searchable);

	public boolean isCollection();

	public boolean isEmbedded();

	public boolean isRichText();

	public boolean isIdentifier();

	public void setRichText(boolean richText);

	public Class getBeanType();

	public void setBeanType(Class beanType);

}