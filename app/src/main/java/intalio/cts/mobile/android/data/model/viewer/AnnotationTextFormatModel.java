package intalio.cts.mobile.android.data.model.viewer;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class AnnotationTextFormatModel implements Serializable {

    @Expose
    public String font;

    @Expose
    public int fontSize;

    @Expose
    public String fontColor;

    public AnnotationTextFormatModel(String font, int size, String color) {
        this.font = font;
        this.fontSize = size;
        this.fontColor = color;
    }

    public AnnotationTextFormatModel(int size, String color) {
        this("Arial", size, color);
    }

    public AnnotationTextFormatModel(int size) {
        this("Arial", size, "#000000");
    }

    public AnnotationTextFormatModel() {
        this("Arial", 30, "#000000");
    }

    public AnnotationTextFormatModel copy() {
        return new AnnotationTextFormatModel(this.font, this.fontSize, this.fontColor);
    }

    @NonNull
    @Override
    public String toString() {
        return "{\"fontSize\": " + fontSize + ", \"font\": \"" + font + "\", \"fontColor\": \"" + fontColor + "\"}";
    }
}
