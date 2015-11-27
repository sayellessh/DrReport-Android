package net.swaas.drinfo.beans;

import java.io.Serializable;

/**
 * Created by vinoth on 10/26/15.
 */
public class FileUpload implements Serializable {
    private String filename;
    private String url;
    private String type;
    private String newFileName;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNewFileName() {
        return newFileName;
    }

    public void setNewFileName(String newFileName) {
        this.newFileName = newFileName;
    }
}
