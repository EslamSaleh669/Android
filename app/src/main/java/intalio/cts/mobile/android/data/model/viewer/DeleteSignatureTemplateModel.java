package intalio.cts.mobile.android.data.model.viewer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class DeleteSignatureTemplateModel implements Serializable {
    @Expose
    @SerializedName(value = "selectedIds")
    private Templates selectedIds;

    public DeleteSignatureTemplateModel(List<Integer> ids) {
        this.selectedIds = new Templates(ids);
    }

}

class Templates implements Serializable {
    @Expose
    @SerializedName(value = "templates")
    private List<Integer> templates;

    public Templates(List<Integer> ids) {
        this.templates = ids;
    }
}
