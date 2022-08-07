package intalio.cts.mobile.android.data.model.viewer;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class HandwrittenSignatureModel implements Serializable, SignatureModel {
    @Expose
    public int pageNumber;

    @Expose
    public String language;

    @Expose
    public String type;

    @Expose
    public String imageSource;

    @Expose
    public float width;

    @Expose
    public float height;

    @Expose
    public float posX;

    @Expose
    public float posY;

}
