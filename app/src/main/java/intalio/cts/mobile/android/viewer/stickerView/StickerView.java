package intalio.cts.mobile.android.viewer.stickerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.cts.mobile.android.R;

import java.util.ArrayList;

import intalio.cts.mobile.android.data.model.viewer.AnnotationTextFormatModel;
import intalio.cts.mobile.android.data.model.viewer.PermissionModel;
import intalio.cts.mobile.android.data.model.viewer.ViewerAnnotationModel;
import intalio.cts.mobile.android.ui.fragment.correspondencedetails.CorrespondenceDetailsFragment;
import intalio.cts.mobile.android.viewer.Utility;
import intalio.cts.mobile.android.viewer.activities.AnnotationPropertiesActivity;
import intalio.cts.mobile.android.viewer.interfaces.AnnotationInterface;
import intalio.cts.mobile.android.viewer.support.PDFConfig;
import intalio.cts.mobile.android.viewer.views.AnnotationsView;


public abstract class StickerView extends FrameLayout {
    private BorderView iv_border;
    private ImageView iv_scale;
    private ImageView iv_delete;
    private ImageView iv_flip;
    private ImageView iv_edit;
    private ImageView iv_annotation;
    private FrameLayout _self;
    private AnnotationInterface annotationInterface;
    private ViewerAnnotationModel _annotation;
    private final Rect border = new Rect();
    private final Paint borderPaint = new Paint();

    // For moving
    private float move_orgX = -1, move_orgY = -1;


    private static int BUTTON_SIZE_DP = 20;
    private final static int SELF_SIZE_DP = 100;


    public StickerView(Context context) {
        super(context);
        init(context);
    }

    public StickerView(Context context, ViewerAnnotationModel annotation) {
        super(context);
        _annotation = annotation;
        init(context);
    }

    public StickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StickerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    //region AnnotationInterface Methods
    public void setAnnotationInterface(AnnotationInterface annotationInterface) {
        this.annotationInterface = annotationInterface;
    }
    //endregion

    public void updateAnnotation(ViewerAnnotationModel annotation) {
        this._annotation.updateAnnotation(annotation);
    }

    public ViewerAnnotationModel getAnnotation() {
        return this._annotation;
    }

    private void init(final Context context) {
        _self = this;
        this.iv_border = new BorderView(context);
        this.iv_scale = new ImageView(context);
        this.iv_delete = new ImageView(context);
        this.iv_flip = new ImageView(context);
        this.iv_edit = new ImageView(context);
        this.iv_annotation = new ImageView(context);

        this.iv_scale.setImageResource(R.drawable.zoominout);
        this.iv_delete.setImageResource(R.drawable.remove);
        this.iv_flip.setImageResource(R.drawable.flip);
        this.iv_edit.setImageResource(R.drawable.pen);
        this.iv_annotation.setImageResource(R.drawable.annotation_permissions_edit);


        this.setTag("DraggableViewGroup");
        this.iv_border.setTag("iv_border");
        this.iv_scale.setTag("iv_scale");
        this.iv_delete.setTag("iv_delete");
        this.iv_flip.setTag("iv_flip");
        this.iv_edit.setTag("iv_edit");
        this.iv_annotation.setTag("iv_annotation");

        iv_flip.setVisibility(View.INVISIBLE);
        BUTTON_SIZE_DP = 15;

        int size = convertDpToPixel(SELF_SIZE_DP, getContext());

        LayoutParams this_params = new LayoutParams(size, size);
        this_params.gravity = Gravity.CENTER;

        LayoutParams iv_main_params =
                new LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                );
//        iv_main_params.setMargins(margin, margin, margin, margin);

        LayoutParams iv_border_params =
                new LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                );
//        iv_border_params.setMargins(margin, margin, margin, margin);

        LayoutParams iv_scale_params =
                new LayoutParams(
                        convertDpToPixel(BUTTON_SIZE_DP, getContext()),
                        convertDpToPixel(BUTTON_SIZE_DP, getContext())
                );
        iv_scale_params.gravity = Gravity.BOTTOM | Gravity.END;

        LayoutParams iv_delete_params =
                new LayoutParams(
                        convertDpToPixel(BUTTON_SIZE_DP, getContext()),
                        convertDpToPixel(BUTTON_SIZE_DP, getContext())
                );
        iv_delete_params.gravity = Gravity.TOP | Gravity.START;

        LayoutParams iv_edit_params =
                new LayoutParams(
                        convertDpToPixel(BUTTON_SIZE_DP, getContext()),
                        convertDpToPixel(BUTTON_SIZE_DP, getContext())
                );
        LayoutParams iv_annotation_params =
                new LayoutParams(
                        convertDpToPixel(BUTTON_SIZE_DP, getContext()),
                        convertDpToPixel(BUTTON_SIZE_DP, getContext())
                );

        iv_annotation_params.gravity = Gravity.BOTTOM | Gravity.START;
        iv_edit_params.gravity = Gravity.TOP | Gravity.END;

        this.setLayoutParams(this_params);
        this.addView(getMainView(), iv_main_params);
        this.addView(iv_border, iv_border_params);
        this.addView(iv_scale, iv_scale_params);
        this.addView(iv_delete, iv_delete_params);
        this.addView(iv_flip, iv_edit_params);

        if (this instanceof StickerTextView || this instanceof StickerDrawView) {
            this.addView(iv_edit, iv_edit_params);
        }
        PDFConfig.AnnotationType annotationType = PDFConfig.generateAnnotationType(_annotation.type);
        if (PDFConfig.attachment.isCheckout() && annotationType != PDFConfig.AnnotationType.AUTOMATIC_SIGNATURE && annotationType != PDFConfig.AnnotationType.MANUAL_SIGNATURE && annotationType != PDFConfig.AnnotationType.INITIAL) {
            this.addView(iv_annotation, iv_annotation_params);
        }

        this.setOnTouchListener(mTouchListener);
        this.iv_scale.setOnTouchListener(mTouchListener);
        this.iv_delete.setOnClickListener(view -> {
            if (StickerView.this.getParent() != null) {
                ViewGroup myCanvas = ((ViewGroup) StickerView.this.getParent());
                myCanvas.removeView(StickerView.this);
                annotationInterface.onDeleteAnnotation(_annotation);
            }
        });
        this.requestDisallowInterceptTouchEvent(true);
        this.iv_flip.setOnClickListener(view -> {
            View mainView = getMainView();
            mainView.setRotationY(mainView.getRotationY() == -180f ? 0f : -180f);
            mainView.invalidate();
            requestLayout();
        });
        this.iv_edit.setOnClickListener(v -> openEditNoteDialog());
        if (!_annotation.isSelected) {
            this.setControlsVisibility(false);
        }
      //  this.iv_annotation.setOnClickListener(view -> openAnnotationDialog(context));


    }

    public void setAnnotationSelected(boolean isSelected) {
        this._annotation.isSelected = isSelected;
    }

    public boolean isFlip() {
        return getMainView().getRotationY() == -180f;
    }

    public void openEditNoteDialog() {
        Intent i = new Intent(_self.getContext(), AnnotationPropertiesActivity.class);
        i.putExtra("Annotation", _annotation);
        PDFConfig.stickerView = this;
        ((AppCompatActivity) _self.getContext()).startActivityForResult(i, PDFConfig.ADD_NOTE_TO_VIEWER);
    }

//    public void openAnnotationDialog(Context context) {
//        PDFConfig.AnnotationType annotationType = PDFConfig.generateAnnotationType(_annotation.type);
//        if (PDFConfig.attachment.isCheckout() && annotationType != PDFConfig.AnnotationType.AUTOMATIC_SIGNATURE && annotationType != PDFConfig.AnnotationType.MANUAL_SIGNATURE && annotationType != PDFConfig.AnnotationType.INITIAL) {
//            if (_annotation.New) {
//                Utility.showAlertDialog(context, R.string.warning, R.string.save_annotations_before_managing_permissions, R.string.ok, (DialogInterface.OnClickListener) (dialog, which) -> {
//                    ((CorrespondenceDetailsFragment) context).sendRequestToSaveAnnotations(null);
//                }, R.string.cancel, null);
//                return;
//            }
//            Intent intent = new Intent(context, ViewPermissionsDialog.class);
//            ArrayList<PermissionModel> permissions = new ArrayList<>();
//            intent.putParcelableArrayListExtra("Permissions", permissions);
//            intent.putExtra("Annotation", _annotation);
//
//            ((AppCompatActivity) context).startActivityForResult(intent, Constants.ANNOTATION_PERMISSION_RESULT);
//        }
//    }

    protected abstract View getMainView();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    protected View getImageViewFlip() {
        return iv_flip;
    }

    protected void onScaling(boolean scaleUp) {
    }

    private class BorderView extends View {

        public BorderView(Context context) {
            super(context);
        }

        public BorderView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public BorderView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            // Draw sticker border

            LayoutParams params = (LayoutParams) this.getLayoutParams();
            border.left = this.getLeft() - params.leftMargin;
            border.top = this.getTop() - params.topMargin;
            border.right = this.getRight() - params.rightMargin;
            border.bottom = this.getBottom() - params.bottomMargin;
            borderPaint.setStrokeWidth(6);
            borderPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            borderPaint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(border, borderPaint);
        }
    }

    private static int convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }

    public void setControlsVisibility(boolean isVisible) {
        if (!isVisible) {
            iv_border.setVisibility(View.GONE);
            iv_delete.setVisibility(View.GONE);
            iv_flip.setVisibility(View.GONE);
            iv_scale.setVisibility(View.GONE);
            iv_edit.setVisibility(View.GONE);
            iv_annotation.setVisibility(GONE);
        } else {
            iv_border.setVisibility(View.VISIBLE);
            iv_delete.setVisibility(View.VISIBLE);
            iv_scale.setVisibility(View.VISIBLE);
            if (this instanceof StickerTextView || this instanceof StickerDrawView) {
                iv_edit.setVisibility(View.VISIBLE);
            } else {
                iv_edit.setVisibility(View.GONE);
            }

            PDFConfig.AnnotationType annotationType = PDFConfig.generateAnnotationType(_annotation.type);
            if (PDFConfig.attachment.isCheckout() && annotationType != PDFConfig.AnnotationType.AUTOMATIC_SIGNATURE && annotationType != PDFConfig.AnnotationType.MANUAL_SIGNATURE && annotationType != PDFConfig.AnnotationType.INITIAL) {
                iv_annotation.setVisibility(VISIBLE);
            } else {
                iv_annotation.setVisibility(GONE);
            }

            if (annotationType == PDFConfig.AnnotationType.blackout) {
                iv_scale.setColorFilter(Color.WHITE);
                iv_delete.setColorFilter(Color.WHITE);
                iv_annotation.setColorFilter(Color.WHITE);
            }
        }
    }

    private final OnTouchListener mTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            view.performClick();
            RecyclerView rv = PDFConfig.getPdfRecyclerView();
            if (PDFConfig.attachment.isCheckout()) {
                setControlsVisibility(true);
            }
            if (view.getTag().equals("DraggableViewGroup") && PDFConfig.attachment.isCheckout()) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        _self.requestDisallowInterceptTouchEvent(true);
                        if (rv != null) {
                            rv.requestDisallowInterceptTouchEvent(true);
                        }
                        move_orgX = event.getRawX();
                        move_orgY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (StickerView.this.getX() >= 0 && StickerView.this.getY() >= 0 && StickerView.this.getX() + StickerView.this.getLayoutParams().width <= PDFConfig.getPageWidth() && StickerView.this.getY() + StickerView.this.getLayoutParams().height <= PDFConfig.getPageHeight()) {
                            float offsetX = event.getRawX() - move_orgX;
                            float offsetY = event.getRawY() - move_orgY;
                            if (StickerView.this.getX() + StickerView.this.getWidth() + offsetX <= PDFConfig.getPageWidth() && StickerView.this.getX() + offsetX > 0) {
                                StickerView.this.setX(StickerView.this.getX() + offsetX);
                            }
                            if (StickerView.this.getY() + StickerView.this.getHeight() + offsetY <= PDFConfig.getPageHeight() && StickerView.this.getY() + offsetY > 0) {
                                StickerView.this.setY(StickerView.this.getY() + offsetY);
                            }
                            move_orgX = event.getRawX();
                            move_orgY = event.getRawY();
                        } else {
                            if (StickerView.this.getX() < 0) {
                                StickerView.this.setX(0);
                            }
                            if (StickerView.this.getY() < 0) {
                                StickerView.this.setY(0);
                            }
                            if (StickerView.this.getX() + StickerView.this.getLayoutParams().width > PDFConfig.getPageWidth()) {
                                StickerView.this.setX(PDFConfig.getPageWidth() - StickerView.this.getLayoutParams().width);
                            }
                            if (StickerView.this.getY() + StickerView.this.getLayoutParams().height > PDFConfig.getPageHeight()) {
                                StickerView.this.setY(PDFConfig.getPageHeight() - StickerView.this.getLayoutParams().height);
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        _annotation.posX = StickerView.this.getX() / ((AnnotationsView) annotationInterface).getMeasuredWidth();
                        _annotation.posY = StickerView.this.getY() / ((AnnotationsView) annotationInterface).getMeasuredHeight();
                        annotationInterface.onMoveAnnotation(_annotation);
                        break;
                }
            } else if (view.getTag().equals("iv_scale") && PDFConfig.attachment.isCheckout()) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        _self.requestDisallowInterceptTouchEvent(true);
                        if (rv != null) {
                            rv.requestDisallowInterceptTouchEvent(true);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:

                        _self.requestDisallowInterceptTouchEvent(true);
                        if (rv != null) {
                            rv.requestDisallowInterceptTouchEvent(true);
                        }


                        if (StickerView.this.getX() + StickerView.this.getLayoutParams().width + event.getX() < PDFConfig.getPageWidth() || event.getX() < 0) {
                            StickerView.this.getLayoutParams().width += event.getX();
                        } else {
                            StickerView.this.getLayoutParams().width = PDFConfig.getPageWidth() - (int) StickerView.this.getX();

                        }
                        if (StickerView.this.getY() + StickerView.this.getLayoutParams().height + event.getY() < PDFConfig.getPageHeight() || event.getY() < 0) {
                            StickerView.this.getLayoutParams().height += event.getY();

                        } else {
                            StickerView.this.getLayoutParams().height = PDFConfig.getPageHeight() - (int) StickerView.this.getY();

                        }
                        if (StickerView.this.getLayoutParams().width < PDFConfig.ANNOTATION_MIN_SIZE) {
                            StickerView.this.getLayoutParams().width = (int) PDFConfig.ANNOTATION_MIN_SIZE;
                        }
                        if (StickerView.this.getLayoutParams().height < (int) PDFConfig.ANNOTATION_MIN_SIZE) {
                            StickerView.this.getLayoutParams().height = (int) PDFConfig.ANNOTATION_MIN_SIZE;
                        }
                        onScaling(true);

                        postInvalidate();
                        requestLayout();
                        break;
                    case MotionEvent.ACTION_UP:
                        _annotation.width = getWidth() / (((AnnotationsView) annotationInterface).getMeasuredWidth() * 1.0f);
                        _annotation.height = getHeight() / (((AnnotationsView) annotationInterface).getMeasuredHeight() * 1.0f);
                        if (StickerView.this instanceof StickerTextView) {
                            _annotation.textFormat = new AnnotationTextFormatModel((int) (((StickerTextView) StickerView.this).getTextSize()));
                        }
                        annotationInterface.onScaleAnnotation(_annotation);
                        break;
                }
            }
            return true;
        }
    };
}
