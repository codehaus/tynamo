package org.trails.component.blob;

import java.io.Serializable;

public interface ITrailsBlob extends Serializable {

    String getFileName();

    void setFileName(String fileName);

    String getFilePath();

    void setFilePath(String filePath);

    byte[] getBytes();

    void setBytes(byte[] bytes);

    String getContentType();

    void setContentType(String contentType);

}
