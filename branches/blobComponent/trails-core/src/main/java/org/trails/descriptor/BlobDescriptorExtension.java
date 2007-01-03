package org.trails.descriptor;

import org.trails.component.blob.ITrailsBlob;
import org.trails.TrailsRuntimeException;

public class BlobDescriptorExtension implements IDescriptorExtension
{

    public enum RenderType
    {
        IMAGE, LINK, IFRAME
    }

    private enum BlobType
    {
        BYTES, ITRAILSBLOB
    }

    private BlobType blobType = BlobType.BYTES;
    private String fileName = "";
    private String contentType = "";
    private RenderType renderType = RenderType.LINK;


    public BlobDescriptorExtension(Class type)
    {
        if (ITrailsBlob.class.isAssignableFrom(type))
        {
            blobType = BlobType.ITRAILSBLOB;
        } else if (type.isArray())
        {
            blobType = BlobType.BYTES;
        } else {
            throw new TrailsRuntimeException("type: " + type + " - Not supported");
        }
    }

    public boolean isBytes()
    {
        return blobType == BlobType.BYTES;
    }

    public boolean isITrailsBlob()
    {
        return blobType == BlobType.ITRAILSBLOB;
    }


    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public String getContentType()
    {
        return contentType;
    }

    public void setContentType(String contentType)
    {
        this.contentType = contentType;
    }


    public RenderType getRenderType()
    {
        return renderType;
    }

    public void setRenderType(RenderType renderType)
    {
        this.renderType = renderType;
    }
}
