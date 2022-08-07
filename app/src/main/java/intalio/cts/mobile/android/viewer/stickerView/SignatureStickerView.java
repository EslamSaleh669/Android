package intalio.cts.mobile.android.viewer.stickerView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import intalio.cts.mobile.android.data.model.viewer.ViewerAnnotationModel;
import intalio.cts.mobile.android.util.Constants;
import intalio.cts.mobile.android.viewer.Utility;
import intalio.cts.mobile.android.viewer.stickerView.util.AutoResizeTextView;

/**
 * Created by aem on 10/2/2017.
 */

public class SignatureStickerView extends StickerView {

    private String owner_id;
    private ImageView iv_main;
    private AutoResizeTextView tv_metadata;

    private boolean isText;
    private String text;

    public SignatureStickerView(Context context) {
        super(context);
    }

    public SignatureStickerView(Context context, ViewerAnnotationModel annotation) {
        super(context, annotation);
    }

    public SignatureStickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SignatureStickerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SignatureStickerView(Context context, ViewerAnnotationModel annotation, String text, boolean isText) {
        super(context, annotation);
        this.text = text;
        this.isText = isText;
    }

    public void setOwnerId(String owner_id) {
        this.owner_id = owner_id;
    }

    public String getOwnerId() {
        return this.owner_id;
    }

    public ImageView getImageView() {
        return this.iv_main;
    }

    public TextView getTextView() {
        return this.tv_metadata;
    }

    public void setMetaData(String text) {
        this.tv_metadata.setText(text);
    }

    @Override
    public View getMainView() {
        View viewRet = null;
        RelativeLayout relativeLayout = new RelativeLayout(getContext());
        LayoutParams relativeParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        relativeLayout.setLayoutParams(relativeParams);
        if (this.iv_main == null) {
            RelativeLayout.LayoutParams ivImageLayout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            this.iv_main = new ImageView(getContext());
            this.iv_main.setLayoutParams(ivImageLayout);
//            this.iv_main.setScaleType(ImageView.ScaleType.CENTER);
        }
        if (tv_metadata == null) {
            tv_metadata = new AutoResizeTextView(getContext());
            RelativeLayout.LayoutParams tvLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            tv_metadata.setGravity(Gravity.BOTTOM);
            tv_metadata.setLayoutParams(tvLayoutParams);
            tv_metadata.setTypeface(Utility.getCustomFont(getContext(), Constants.MAIN_FONT_APP));

            relativeLayout.addView(tv_metadata);
        }
        RelativeLayout.LayoutParams ivLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ivLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        iv_main.setLayoutParams(ivLayoutParams);
        relativeLayout.addView(iv_main);
        viewRet = relativeLayout;
//        }
        return viewRet;
    }

    public void setImageBitmap(Bitmap bmp) {
        this.iv_main.setImageBitmap(bmp);
    }

    public void setImageResource(int res_id) {
        this.iv_main.setImageResource(res_id);
    }

    public void setImageDrawable(Drawable drawable) {
        this.iv_main.setImageDrawable(drawable);
    }

    public Bitmap getImageBitmap() {
        return ((BitmapDrawable) this.iv_main.getDrawable()).getBitmap();
    }

}
