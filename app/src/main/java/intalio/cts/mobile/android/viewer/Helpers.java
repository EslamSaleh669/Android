package intalio.cts.mobile.android.viewer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import intalio.cts.mobile.android.data.model.viewer.ViewerAnnotationModel;
import intalio.cts.mobile.android.viewer.support.PDFConfig;

public class Helpers {

    public enum FileType {
        PDF,
        Document,
        PowerPoint,
        Image,
        Excel,
        Autocad,
        Unknown
    }

    public static FileType getFileType(String extension) {
        switch (extension.toLowerCase()) {
            case "pdf":
                return FileType.PDF;
            case "jpeg":
            case "jpg":
            case "png":
            case "bmp":
            case "gif":
                return FileType.Image;
            case "doc":
            case "docx":
            case "rtf":
                return FileType.Document;
            case "xls":
            case "xlsx":

            case "xlsm":
                return FileType.Excel;
            case "ppt":
            case "pptx":
                return FileType.PowerPoint;
            case "dwg":
                return FileType.Autocad;
            default:
                return FileType.Unknown;
        }
    }

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public static int borderColorToColor(String borderColor) {
        if (borderColor == null || borderColor.isEmpty())
            return 0;
        if (borderColor.startsWith("#"))
            return Color.parseColor(borderColor);
        String[] rgb = borderColor.replace("rgb(", "").replace(")", "").split(",");
        return Color.argb(255,
                Integer.parseInt(rgb[0].trim()),
                Integer.parseInt(rgb[1].trim()),
                Integer.parseInt(rgb[2].trim()));
    }

    public static int fontColorToColor(String fontColor) {
        return Color.parseColor(fontColor);
    }

    public static String now() {
        return dateFormat.format(new Date());
    }

    public static boolean annotationIsSignature(ViewerAnnotationModel annotation) {
        return annotation.type.equals(PDFConfig.AnnotationType.signature.name())
                || annotation.type.equals(PDFConfig.AnnotationType.MANUAL_SIGNATURE.name())
                || annotation.type.equals(PDFConfig.AnnotationType.AUTOMATIC_SIGNATURE.name())
                || annotation.type.equals(PDFConfig.AnnotationType.INITIAL.name());
    }

    public static boolean removeSignatures(ArrayList<ViewerAnnotationModel> annotations) {
        boolean included = false;
        Iterator<ViewerAnnotationModel> iterator = annotations.iterator();
        while (iterator.hasNext()) {
            ViewerAnnotationModel annotation = iterator.next();
            if (annotationIsSignature(annotation)) {
                iterator.remove();
                included = true;
            }
        }
        return included;
    }

    public static ArrayList<ViewerAnnotationModel> generateFilteredSignatures(ArrayList<ViewerAnnotationModel> annotations) {
        ArrayList<ViewerAnnotationModel> _annotations = new ArrayList<>();
        for (ViewerAnnotationModel annotation : annotations) {
            if (annotation.type.equals(PDFConfig.AnnotationType.line.name()))
                annotation.height = 0.007000000000000001f;

            if (!annotationIsSignature(annotation)) {
                if (annotation.textFormat != null)
                    annotation.annotationTextFormat = annotation.textFormat.toString();
                _annotations.add(annotation);
            }
        }
        return _annotations;
    }

    public static ArrayList<String> generateDeletedAnnotations(ArrayList<ViewerAnnotationModel> annotations) {
        ArrayList<String> deleted = new ArrayList<>();
        for (ViewerAnnotationModel annotation : annotations) {
            if (annotation.isDeleted && !annotation.New && !annotationIsSignature(annotation))
                deleted.add(annotation.guid);
        }
        return deleted;
    }

    public static Bitmap decodeFile(File f, int width, int height) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // The new size we want to scale to

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= width &&
                    o.outHeight / scale / 2 >= width) {
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
            Log.e(Helpers.class.getSimpleName(), e.getLocalizedMessage());
        }
        return null;
    }

    public static void removeDeletedAnnotations(ArrayList<ViewerAnnotationModel> annotations) {
        Iterator<ViewerAnnotationModel> iterator = annotations.iterator();
        while (iterator.hasNext()) {
            ViewerAnnotationModel annotation = iterator.next();
            if (annotation.isDeleted) {
                iterator.remove();
            }
        }
    }

    public static byte[] readBytes(InputStream inputStream) throws IOException {
        byte[] b = new byte[1024];
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        int c;
        while ((c = inputStream.read(b)) != -1) {
            os.write(b, 0, c);
        }
        return os.toByteArray();
    }
}
