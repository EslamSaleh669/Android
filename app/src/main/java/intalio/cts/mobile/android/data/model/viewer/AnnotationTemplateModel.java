package intalio.cts.mobile.android.data.model.viewer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AnnotationTemplateModel implements Serializable {

    @Expose
    @SerializedName(value = "id")
    private int templateId;

    @Expose
    @SerializedName(value = "name")
    private String name;

    @Expose
    @SerializedName(value = "text")
    private String text;

    @Expose
    @SerializedName(value = "type")
    private String type;


    public AnnotationTemplateModel(int templateId, String name, String text, String type) {
        this.templateId = templateId;
        this.name = name;
        this.text = text;
        this.type = type;
    }

    public AnnotationTemplateModel(String name, String text, String type) {
        this(-1, name, text, type);
    }

    public AnnotationTemplateModel(int id, String name, String text) {
        this(id, name, text, "text");
    }

    public AnnotationTemplateModel(String name, String text) {
        this(-1, name, text, "text");
    }

    public int getTemplateId() {
        return templateId;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
