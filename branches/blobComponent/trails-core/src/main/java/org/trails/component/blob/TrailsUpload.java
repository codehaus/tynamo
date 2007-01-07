package org.trails.component.blob;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.request.IUploadFile;
import org.apache.tapestry.annotations.Parameter;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.commons.io.IOUtils;
import org.trails.descriptor.IPropertyDescriptor;
import org.trails.descriptor.BlobDescriptorExtension;
import org.trails.persistence.PersistenceService;
import org.hibernate.LazyInitializationException;

import java.io.InputStream;
import java.io.IOException;

public abstract class TrailsUpload extends BaseComponent
{

    @Parameter(required = true)
    public abstract IPropertyDescriptor getPropertyDescriptor();

    @Parameter(required = true)
    public abstract Object getBytes();

    public abstract void setBytes(Object bytes);

    @InjectObject("spring:persistenceService")
    public abstract PersistenceService getPersistenceService();


    public BlobDescriptorExtension getBlobDescriptorExtension()
    {
        return getPropertyDescriptor().getExtension(BlobDescriptorExtension.class);
    }


    public IUploadFile getFile()
    {
        return null;
    }

    public void setFile(IUploadFile file)
    {


        if (file != null)
        {

            if (getBlobDescriptorExtension().isBytes())
            {

                InputStream inputStream = file.getStream();
                try
                {
                    byte[] data = IOUtils.toByteArray(inputStream);
                    if (data.length > 1)
                    {
                        setBytes(data);
                    }
                } catch (IOException e)
                {
                    // nuthin' ?
                }
            } else if (getBlobDescriptorExtension().isITrailsBlob())
            {
                ITrailsBlob trailsBlob = (ITrailsBlob) getBytes();

                try
                {

                    trailsBlob.setFileName(file.getFileName());
                    trailsBlob.setFilePath(file.getFilePath());
                    trailsBlob.setContentType(file.getContentType());

                } catch (LazyInitializationException e)
                {

                    getPersistenceService().reattach(trailsBlob);

                    trailsBlob.setFileName(file.getFileName());
                    trailsBlob.setFilePath(file.getFilePath());
                    trailsBlob.setContentType(file.getContentType());
                }

                InputStream inputStream = file.getStream();
                try
                {
                    byte[] data = IOUtils.toByteArray(inputStream);
                    if (data.length > 1)
                    {
                        trailsBlob.setBytes(data);
                    }
                } catch (IOException ioe)
                {
                    ioe.printStackTrace();
                }
            }
        }
    }
}
