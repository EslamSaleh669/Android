package intalio.cts.mobile.android.viewer.stickerView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

import intalio.cts.mobile.android.data.model.viewer.ViewerAnnotationModel;
import intalio.cts.mobile.android.viewer.Helpers;
import intalio.cts.mobile.android.viewer.support.PDFConfig;


/**
 * Created by aem on 10/16/2017.
 */

public class AnnotationDrawView extends View {

    private ViewerAnnotationModel _annotation;
    private final Paint _paint;
    private Rect rect;
    private RectF rectf;

    public AnnotationDrawView(Context context) {
        super(context);
        _paint = new Paint();
    }

    public AnnotationDrawView(Context context, ViewerAnnotationModel annotation) {
        this(context);
        this._annotation = annotation;
        this.rect = new Rect(0, 0, (int) annotation.width, (int) annotation.height);
        this.rectf = new RectF(0, 0, (int) annotation.width, (int) annotation.height);
    }

    public void reDraw(ViewerAnnotationModel annotation) {
        if (annotation != null)
            _annotation = annotation;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        PDFConfig.AnnotationType annotationType = PDFConfig.generateAnnotationType(_annotation.type);
        _paint.setStyle(Paint.Style.FILL);
        _paint.setColor(Color.parseColor(_annotation.backgroundColor));

        if (annotationType == PDFConfig.AnnotationType.rectangle || annotationType == PDFConfig.AnnotationType.line) {
            canvas.drawRect(rect, _paint);
        } else if (annotationType == PDFConfig.AnnotationType.ellipse) {
            canvas.drawOval(rectf, _paint);
        }

        _paint.setStyle(Paint.Style.STROKE);
        _paint.setColor(Helpers.borderColorToColor(_annotation.borderColor));
        _paint.setStrokeWidth(_annotation.borderWidth);

        if (annotationType == PDFConfig.AnnotationType.rectangle) {
            canvas.drawRect(rect, _paint);
        } else if (annotationType == PDFConfig.AnnotationType.ellipse) {
            canvas.drawOval(rectf, _paint);
        }
    }
}
