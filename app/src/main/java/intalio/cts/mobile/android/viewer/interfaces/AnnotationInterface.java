package intalio.cts.mobile.android.viewer.interfaces;


import intalio.cts.mobile.android.data.model.viewer.ViewerAnnotationModel;

/**
 * Created by aem on 9/11/2017.
 */

public interface AnnotationInterface {
    void onDeleteAnnotation(ViewerAnnotationModel v);
    void onMoveAnnotation(ViewerAnnotationModel v);
    void onScaleAnnotation(ViewerAnnotationModel v);
    void onSetTextAnnotation(ViewerAnnotationModel v);
}
