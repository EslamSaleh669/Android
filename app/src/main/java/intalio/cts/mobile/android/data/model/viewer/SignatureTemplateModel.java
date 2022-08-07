package intalio.cts.mobile.android.data.model.viewer;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class SignatureTemplateModel implements Serializable {

    @Expose
    public int id;

    @Expose
    public String name;

    @Expose
    public String type;

    @Expose
    public String lastModified;

    @Expose
    public String dateAdded;

    @Expose
    public String description;

    @Expose
    public String imageSource;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageSource() {
        return imageSource;
    }

    public void setImageSource(String imageSource) {
        this.imageSource = imageSource;
    }
}
