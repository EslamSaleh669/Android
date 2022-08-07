package intalio.cts.mobile.android.data.model.viewer;

import java.io.Serializable;

public class HandwrittenAnnotationRequestModel implements Serializable {
    private HandwritingDataModel handwritingData;

    public HandwrittenAnnotationRequestModel() {
    }

    public HandwrittenAnnotationRequestModel(ViewerAnnotationModel annotation) {
        this.handwritingData = new HandwritingDataModel();
        handwritingData.height = annotation.height * annotation.parentHeight;
        handwritingData.width = annotation.width * annotation.parentHeight;
        handwritingData.posX = annotation.posX * annotation.width;
        handwritingData.posY = annotation.posY * annotation.parentHeight;
        handwritingData.imageSource = "data:image/png;base64," + annotation.ImageByte;
    }
}
