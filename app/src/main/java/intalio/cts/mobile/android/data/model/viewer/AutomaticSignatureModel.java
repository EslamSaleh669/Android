package intalio.cts.mobile.android.data.model.viewer;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class AutomaticSignatureModel implements Serializable, SignatureModel {
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

    @Expose
    public int posZ = 1;


    @Expose
    public int signatureTemplateId;


    @Expose
    public int ownedByCurrentUser = 1;
}
