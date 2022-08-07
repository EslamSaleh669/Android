package intalio.cts.mobile.android.data.model.viewer;

import com.google.gson.annotations.Expose;

public interface SignatureModel {

    @Expose
    public int ownedByCurrentUser = 1;

    @Expose
    public int annotationNumber = 1;

    @Expose
    public int id = 1;
}
