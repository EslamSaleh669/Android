package intalio.cts.mobile.android.data.model.viewer;

import com.google.gson.annotations.Expose;

public class UpdateSignatureTemplateModel {

    @Expose
    public String name;

    @Expose
    public String description;

    public UpdateSignatureTemplateModel(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
