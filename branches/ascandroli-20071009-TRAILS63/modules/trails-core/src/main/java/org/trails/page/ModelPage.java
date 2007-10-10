package org.trails.page;

/**
 * A page which has a model object.
 *
 * @author Chris Nelson
 */
public abstract class ModelPage extends TrailsPage
{

	public abstract Object getModel();

	public abstract void setModel(Object model);

	public abstract boolean isModelNew();

	public abstract void setModelNew(boolean modelNew);

}
