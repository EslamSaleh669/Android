package intalio.cts.mobile.android.data.model.viewer;

import java.io.Serializable;
import java.util.List;

public class ViewerDocumentDetailsModel implements Serializable {

    private List<ViewerAnnotationModel> annotations;
    private String versionCode;
    private String filename;
    private int pagesCount;
    private String fileType;
    private Boolean isCheckedOut;

    public List<ViewerAnnotationModel> getAnnotations() {
        return annotations;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public String getFilename() {
        return filename;
    }

    public int getPagesCount() {
        return pagesCount;
    }

    public String getFileType() {
        return fileType;
    }

    public boolean getCheckedOut() {
        return isCheckedOut != null && isCheckedOut;
    }
}
