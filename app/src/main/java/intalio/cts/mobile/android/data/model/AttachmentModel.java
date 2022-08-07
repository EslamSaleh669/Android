package intalio.cts.mobile.android.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import intalio.cts.mobile.android.data.model.viewer.ViewerAnnotationModel;
import kotlin.jvm.Transient;

public class AttachmentModel implements Serializable {
    public String id;
    public String text;
    public String title;
    public String type;
    public String parentId;
    public String icon;
    public TreeNodeStateModel state;
    public Boolean isLocked;

    public List<AttachmentModel> children;

    @Transient
    private List<ViewerAnnotationModel> annotations;

    @Transient
    public String lockedBy;

    @Transient
    public int pagesCount;

    public AttachmentType getAttachmentType() {
        if (type != null) {
            return type.equals("1") ? AttachmentType.FOLDER : AttachmentType.FILE;
        }
        return AttachmentType.FOLDER;
    }


    @Transient
    private boolean checkout;

    @Transient
    public String version;


    public List<ViewerAnnotationModel> getAnnotations() {
        if (annotations != null)
            return annotations;
        return new ArrayList<>();
    }

    public void setAnnotations(List<ViewerAnnotationModel> annotations) {
        this.annotations = annotations;
    }

    public boolean isCheckout() {
        return checkout;
    }

    public void setCheckout(boolean checkout) {
        this.checkout = checkout;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getLockedBy() {
        return lockedBy;
    }

    public void setLockedBy(String lockedBy) {
        this.lockedBy = lockedBy;
    }

    public void setPagesCount(int pagesCount) {
        this.pagesCount = pagesCount;
    }

    public int getPagesCount() {
        return this.pagesCount;
    }

    public String getDocumentId() {
        return this.id.split("_")[1];
    }
}
