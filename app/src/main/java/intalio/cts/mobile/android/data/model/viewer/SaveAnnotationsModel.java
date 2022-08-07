package intalio.cts.mobile.android.data.model.viewer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SaveAnnotationsModel {


    @Expose
    @SerializedName(value = "annotations")
    private List<ViewerAnnotationModel> annotations;

    @Expose
    @SerializedName(value = "deletedAnnotations")
    private List<String> deletedAnnotations;

    public SaveAnnotationsModel(List<ViewerAnnotationModel> annotations, List<String> deletedAnnotations) {
        this.annotations = annotations;
        this.deletedAnnotations = deletedAnnotations;
    }
}
