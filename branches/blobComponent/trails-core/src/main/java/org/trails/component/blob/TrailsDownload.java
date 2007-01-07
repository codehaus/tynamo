package org.trails.component.blob;

import ognl.Ognl;
import ognl.OgnlException;
import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IAsset;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.Parameter;
import org.trails.descriptor.BlobDescriptorExtension;
import org.trails.descriptor.IClassDescriptor;
import org.trails.descriptor.IPropertyDescriptor;

public abstract class TrailsDownload extends BaseComponent
{
    @InjectObject("service:trails.BinaryOutPutService")
    public abstract BlobDownloadService getBinOutService();

    @Parameter(required = false, defaultValue = "page.model")
    public abstract Object getModel();

    @Parameter(required = false, defaultValue = "page.classDescriptor")
    public abstract IClassDescriptor getClassDescriptor();

    @Parameter(required = true)
    public abstract IPropertyDescriptor getPropertyDescriptor();

    public IPropertyDescriptor getIdentifierDescriptor()
    {
        return getClassDescriptor().getIdentifierDescriptor();
    }

    public BlobDescriptorExtension getBlobDescriptorExtension()
    {
        return getPropertyDescriptor().getExtension(BlobDescriptorExtension.class);
    }

    public IAsset getByteArrayAsset()
    {
        String id = "";
        try
        {
            id = Ognl.getValue(getIdentifierDescriptor().getName(), getModel()).toString();
        } catch (OgnlException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NullPointerException npe)
        {
            id = "";
        }
        return new TrailsBlobAsset(getBinOutService(), getClassDescriptor().getType().getName(), id, getPropertyDescriptor().getName());
    }

    public abstract void setModel(Object model);

    public abstract void setClassDescriptor(IClassDescriptor ClassDescriptor);

    public abstract void setPropertyDescriptor(IPropertyDescriptor iPropertyDescriptor);
}
