package org.trails.component.blob;

import ognl.Ognl;
import ognl.OgnlException;
import org.apache.commons.io.IOUtils;
import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IAsset;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.Parameter;
import org.apache.tapestry.request.IUploadFile;
import org.trails.descriptor.BlobDescriptorExtension;
import org.trails.descriptor.IClassDescriptor;
import org.trails.descriptor.IPropertyDescriptor;
import org.trails.persistence.PersistenceService;
import org.hibernate.LazyInitializationException;

import java.io.IOException;
import java.io.InputStream;

public abstract class BlobComponent extends BaseComponent {//extends AbstractFormComponent {

    @InjectObject("service:trails.BinaryOutPutService")
    public abstract BlobDownloadService getBinOutService();

    public IUploadFile getFile() {
        return null;
    }

    public void setFile(IUploadFile file) {


        if (file != null) {

            if (getPropertyDescriptor().getExtension(BlobDescriptorExtension.class).isBytes()) {

                InputStream inputStream = file.getStream();
                try {
                    byte[] data = IOUtils.toByteArray(inputStream);
                    if (data.length > 1) {
                        setBytes(data);
                    }
                } catch (IOException e) {
                    // nuthin' ?
                }
            } else if (getPropertyDescriptor().getExtension(BlobDescriptorExtension.class).isITrailsBlob()) {
                ITrailsBlob trailsBlob = (ITrailsBlob) getBytes();

                try {

                    trailsBlob.setFileName(file.getFileName());
                    trailsBlob.setFilePath(file.getFilePath());
                    trailsBlob.setContentType(file.getContentType());

                } catch (LazyInitializationException e) {

                    getPersistenceService().reattach(trailsBlob);

                    trailsBlob.setFileName(file.getFileName());
                    trailsBlob.setFilePath(file.getFilePath());
                    trailsBlob.setContentType(file.getContentType());
                }

                InputStream inputStream = file.getStream();
                try {
                    byte[] data = IOUtils.toByteArray(inputStream);
                    if (data.length > 1) {
                        trailsBlob.setBytes(data);
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
    }

    @Parameter(required = false, defaultValue = "page.model")
    public abstract Object getModel();

    public abstract void setModel(Object model);

    @Parameter(required = false, defaultValue = "page.classDescriptor")
    public abstract IClassDescriptor getClassDescriptor();

    public abstract void setClassDescriptor(IClassDescriptor ClassDescriptor);

    @Parameter(required = true)
    public abstract IPropertyDescriptor getPropertyDescriptor();

    public abstract void setPropertyDescriptor(IPropertyDescriptor iPropertyDescriptor);

    @Parameter(required = true)
    public abstract Object getBytes();

    public abstract void setBytes(Object bytes);

    @InjectObject("spring:persistenceService")
    public abstract PersistenceService getPersistenceService();


    public IPropertyDescriptor getIdentifierDescriptor() {
        return getClassDescriptor().getIdentifierDescriptor();
    }

    public IAsset getByteArrayAsset() {
        String id = "";
        try {
            id = Ognl.getValue(getIdentifierDescriptor().getName(), getModel()).toString();
        } catch (OgnlException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NullPointerException npe) {
            id = "";
        }
        return new TrailsBlobAsset(getBinOutService(), getClassDescriptor().getType().getName(), id, getPropertyDescriptor().getName(), "image/jpeg");
    }
}
