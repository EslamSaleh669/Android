package intalio.cts.mobile.android.data.model.viewer;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.UUID;

import intalio.cts.mobile.android.viewer.Helpers;

/**
 * Created by aem on 9/13/2017.
 */

public class ViewerAnnotationModel implements Serializable {

    @Expose
    public String id;

    @Expose
    public String guid;

    @Expose
    public String dateAdded;

    @Expose
    public int pageNumber;

    @Expose
    public float posX;

    @Expose
    public float posY;

    @Expose
    public int posZ;

    @Expose
    public String type;

    @Expose
    public float width;

    @Expose
    public float height;

    @Expose
    public AnnotationTextFormatModel textFormat;

    @Expose
    public String backgroundColor;

    @Expose
    public String borderColor;

    @Expose
    public float borderWidth;

    @Expose
    public int signatureTemplateId;

    @Expose
    public double opacity = 1;

    @Expose
    public String imageSource;

    @Expose
    public String text;

    @Expose
    public int UserId;

    @Expose
    public String annotationTextFormat;

    public float parentWidth;
    public float parentHeight;
    public String ImageByte;
    public boolean New;
    public boolean hasPermission = true;
    public boolean isSelected = false;
    public boolean isDeleted = false;

    public ViewerAnnotationModel() {

    }

    public ViewerAnnotationModel copy(int pageNumber) {
        ViewerAnnotationModel annotation = new ViewerAnnotationModel();
        annotation.id = id.split("_")[0] + "_" + (Integer.parseInt(id.split("_")[1]) + 1);
        annotation.type = type;
        annotation.pageNumber = pageNumber;
        annotation.hasPermission = hasPermission;
        annotation.height = height;
        annotation.width = width;
        annotation.posY = posY;
        annotation.posX = posX;
        annotation.posZ = posZ;
        annotation.parentHeight = parentHeight;
        annotation.parentWidth = parentWidth;
        annotation.ImageByte = ImageByte;
        annotation.signatureTemplateId = signatureTemplateId;
        annotation.UserId = UserId;
        annotation.imageSource = imageSource;
        annotation.New = New;
        annotation.opacity = opacity;
        annotation.backgroundColor = backgroundColor;
        annotation.borderWidth = borderWidth;
        annotation.borderColor = borderColor;
        annotation.dateAdded = Helpers.now();
        annotation.text = text;
        if (annotation.textFormat != null)
            annotation.textFormat = textFormat.copy();
        annotation.guid = UUID.randomUUID().toString();
        annotation.isSelected = isSelected;
        return annotation;
    }

    public void updateAnnotation(ViewerAnnotationModel from) {
        id = from.id;
        type = from.type;
        pageNumber = from.pageNumber;
        hasPermission = from.hasPermission;
        height = from.height;
        width = from.width;
        posY = from.posY;
        posX = from.posX;
        posZ = from.posZ;
        parentHeight = from.parentHeight;
        parentWidth = from.parentWidth;
        ImageByte = from.ImageByte;
        signatureTemplateId = from.signatureTemplateId;
        UserId = from.UserId;
        imageSource = from.imageSource;
        New = from.New;
        opacity = from.opacity;
        backgroundColor = from.backgroundColor;
        borderWidth = from.borderWidth;
        borderColor = from.borderColor;
        dateAdded = from.dateAdded;
        text = from.text;
        textFormat = from.textFormat.copy();
        guid = from.guid;
        isSelected = from.isSelected;
    }
}
