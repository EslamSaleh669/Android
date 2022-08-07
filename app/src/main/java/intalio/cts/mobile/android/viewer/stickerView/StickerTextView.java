package intalio.cts.mobile.android.viewer.stickerView;


import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;

import com.cts.mobile.android.R;

import intalio.cts.mobile.android.data.model.viewer.ViewerAnnotationModel;
import intalio.cts.mobile.android.viewer.Helpers;
import intalio.cts.mobile.android.viewer.stickerView.util.AutoResizeTextView;
import intalio.cts.mobile.android.viewer.support.PDFConfig;

/**
 * Created by cheungchingai on 6/15/15.
 */
public class StickerTextView extends StickerView {
    private AutoResizeTextView tv_main;

    public StickerTextView(Context context) {
        super(context);
    }

    public StickerTextView(Context context, ViewerAnnotationModel annotation) {
        super(context, annotation);
    }

    public StickerTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StickerTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public View getMainView() {
        if (tv_main != null)
            return tv_main;

        tv_main = new AutoResizeTextView(getContext());
        tv_main.setTextColor(Color.BLACK);
        tv_main.setGravity(Gravity.START | Gravity.CENTER);
        if (getAnnotation().backgroundColor != null && !getAnnotation().backgroundColor.isEmpty()) {
            try {
                tv_main.setBackgroundColor(Helpers.borderColorToColor(getAnnotation().backgroundColor));
            } catch (Exception e) {
                tv_main.setBackgroundColor(Color.parseColor(getAnnotation().backgroundColor));
            }
        } else if (getAnnotation().type.equals(PDFConfig.AnnotationType.text.name())) {
            tv_main.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.note_background_color));
        }
        if (getAnnotation().textFormat.fontColor != null) {
            try {
                tv_main.setTextColor(Helpers.borderColorToColor(getAnnotation().textFormat.fontColor));
            } catch (Exception e) {
                tv_main.setTextColor(Helpers.fontColorToColor(getAnnotation().textFormat.fontColor));
            }
        } else {
            tv_main.setTextColor(Color.BLACK);
        }
        if (getAnnotation().textFormat != null) {
            tv_main.setTextSize(getAnnotation().textFormat.fontSize);
        } else {
            tv_main.setTextSize(60);
        }
        tv_main.setSingleLine(false);
        tv_main.setMaxLines(5);
        tv_main.setShadowLayer(4, 0, 0, Color.WHITE);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        params.gravity = Gravity.CENTER;
        tv_main.setLayoutParams(params);
        if (getImageViewFlip() != null)
            getImageViewFlip().setVisibility(View.GONE);
        return tv_main;
    }

    public void setText(String text) {
        if (tv_main != null)
            tv_main.setText(text);
    }

    public void setTextColor(int color) {
        if (tv_main != null) {
            tv_main.setTextColor(color);
        }
    }

    @Override
    public void setBackgroundColor(@ColorInt int color) {
        if (tv_main != null) {
            tv_main.setBackgroundColor(color);
        }
    }

    public String getText() {
        if (tv_main != null)
            return tv_main.getText().toString();

        return null;
    }

    public void setTextSize(float textSize) {
        if (tv_main != null) {
            tv_main.setTextSize(textSize);
        }
    }

    public float getTextSize() {
        if (tv_main != null) {
            return tv_main.getTextSize();
        }
        return 0;
    }

    public static float pixelsToSp(Context context, float px) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return px / scaledDensity;
    }

    public void setMaxLines(int maxLines) {
        tv_main.setMaxLines(maxLines);
    }

    @Override
    protected void onScaling(boolean scaleUp) {
        super.onScaling(scaleUp);
    }


}
