package intalio.cts.mobile.android.viewer.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;


import com.cts.mobile.android.R;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.UUID;

import intalio.cts.mobile.android.data.model.viewer.AnnotationTextFormatModel;
import intalio.cts.mobile.android.data.model.viewer.ViewerAnnotationModel;
import intalio.cts.mobile.android.data.network.response.TokenResponse;
import intalio.cts.mobile.android.ui.fragment.correspondencedetails.CorrespondenceDetailsFragment;
import intalio.cts.mobile.android.util.Constants;
import intalio.cts.mobile.android.viewer.Helpers;
import intalio.cts.mobile.android.viewer.Utility;
import intalio.cts.mobile.android.viewer.interfaces.AnnotationInterface;
import intalio.cts.mobile.android.viewer.stickerView.StickerImageView;
import intalio.cts.mobile.android.viewer.stickerView.StickerTextView;
import intalio.cts.mobile.android.viewer.stickerView.StickerView;
import intalio.cts.mobile.android.viewer.support.PDFConfig;

/**
 * Created by aem on 9/11/2017.
 */

public class AnnotationsView extends RelativeLayout implements AnnotationInterface {
    public ArrayList<ViewerAnnotationModel> _annotations;
    private Context _context;
    private AnnotationsView _self;
    private int _pageIndex;
    private CorrespondenceDetailsFragment _correspondenceDetailsFragment ;

    public AnnotationsView(Context context) {
        super(context);
        constructAnnotationsView(context);
    }

    public AnnotationsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        constructAnnotationsView(context);
    }

    public AnnotationsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        constructAnnotationsView(context);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    public void constructAnnotationsView(Context context) {
        _context = context;
        _self = this;
        this.setClipChildren(false);
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                performClick();
                try {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        try {
                            for (int i = 0; i < _self.getChildCount(); i++) {
                                if (!(_self.getChildAt(i) instanceof TextView)) {
                                    StickerView stickerView = (StickerView) _self.getChildAt(i);
                                    stickerView.setAnnotationSelected(false);
                                    stickerView.setControlsVisibility(false);
                                }
                            }
                        } catch (Exception e) {
                            Log.e(e.getClass().getSimpleName(), e.getMessage());
                        }
                        if (PDFConfig.getDrawType() != null) {
                            createAndDrawAnnotation(PDFConfig.getDrawType(), event.getX(), event.getY());
                        }
                    }
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), e.getMessage());
                    e.printStackTrace();
                }
                return true;
            }
        });
    }

    private void createAndDrawAnnotation(PDFConfig.AnnotationType type, float x, float y) {
        ViewerAnnotationModel viewerAnnotation = createNewAnnotation(type, x, y);
        _annotations.add(viewerAnnotation);
        drawAnnotation(viewerAnnotation);

        PDFConfig.resetDrawType();
        PDFConfig.resetMenuItems();
    }

    public void setAnnotations(ArrayList<ViewerAnnotationModel> annotations, CorrespondenceDetailsFragment _correspondenceDetailsFragment) {
        this._annotations = annotations;
        this._correspondenceDetailsFragment = _correspondenceDetailsFragment;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        if (oldH != 0) {
            PDFConfig.setPageWidth(w);
            PDFConfig.setPageHeight(h);
            new Handler().postDelayed(this::loadAnnotations, 100);
        }
    }

    public void loadAnnotations() {
        try {
            removeAllViews();
            for (ViewerAnnotationModel viewerAnnotation : _annotations) {
                if (viewerAnnotation.isDeleted)
                    continue;
                PDFConfig.annotationCounter++;
                drawAnnotation(viewerAnnotation);
            }
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), e.getMessage());
            e.printStackTrace();
        }
    }

    private ViewerAnnotationModel createNewAnnotation(PDFConfig.AnnotationType type, float x, float y) {
        SharedPreferences sharedpreferences = _context.getSharedPreferences(Constants.SHARED_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedpreferences.getString(Constants.USER_TOKEN_KEY, "");
        TokenResponse tokenResponse  = gson.fromJson(json, TokenResponse.class);


        final ViewerAnnotationModel viewerAnnotation = new ViewerAnnotationModel();
        viewerAnnotation.type = PDFConfig.setAnnotationType(type);
        viewerAnnotation.id = viewerAnnotation.type.replace("_", "") + "_" + PDFConfig.annotationCounter;
        viewerAnnotation.dateAdded = Helpers.now();
        viewerAnnotation.guid = UUID.randomUUID().toString();
        viewerAnnotation.pageNumber = _pageIndex + 1;
        viewerAnnotation.posX = (x - PDFConfig.ANNOTATION_WIDTH / 2) / this.getWidth();
        viewerAnnotation.posY = (y - PDFConfig.ANNOTATION_HEIGHT / 2) / this.getHeight();
        viewerAnnotation.posZ = PDFConfig.annotationCounter;
        viewerAnnotation.width = PDFConfig.ANNOTATION_WIDTH / this.getWidth();
        viewerAnnotation.height = PDFConfig.ANNOTATION_HEIGHT / this.getHeight();
        viewerAnnotation.parentWidth = this.getWidth();
        viewerAnnotation.parentHeight = this.getHeight();
        viewerAnnotation.hasPermission = true;
        viewerAnnotation.New = true;
        viewerAnnotation.opacity = 1;

       // CorrespondenceDetailsFragment correspondenceDetailsFragment = new CorrespondenceDetailsFragment();

        viewerAnnotation.signatureTemplateId = PDFConfig.signatureTemplateId;

        if (PDFConfig.base64ToDraw != null) {
            viewerAnnotation.ImageByte = PDFConfig.base64ToDraw;
            PDFConfig.base64ToDraw = null;
        }

        if (PDFConfig.imageToDrawName != null && !PDFConfig.imageToDrawName.equals("")) {
            viewerAnnotation.imageSource = PDFConfig.imageToDrawName;
            PDFConfig.imageToDraw = null;
            PDFConfig.imageToDrawName = "";
        }

        switch (type) {
            case highlight:
                viewerAnnotation.backgroundColor = "#fffb00";
                viewerAnnotation.opacity = 0.5f;
                break;

            case ellipse:
                viewerAnnotation.backgroundColor = "#f32d13";
                viewerAnnotation.borderColor = "#000000";
                viewerAnnotation.borderWidth = 0.016937631875516158f;
                break;

            case blackout:
                viewerAnnotation.backgroundColor = "#000000";
                break;

            case line:
                viewerAnnotation.backgroundColor = "#f32d13";
                break;

            case rectangle:
                viewerAnnotation.backgroundColor = "#9f512d";
                viewerAnnotation.borderColor = "#dda0dd";
                viewerAnnotation.borderWidth = 0.016937631875516158f;
                break;

            case stamps:
                String language = Utility.getLocalData(_context, Constants.LANG_KEY);

                String imageName = viewerAnnotation.imageSource.split("/stamp/")[1].split("/")[0];
                viewerAnnotation.imageSource = "/" + Utility.getViewerAppName(_context) + _context.getString(R.string.viewer_stamps_image_path, imageName,
                        language, tokenResponse.getAccessToken());
                break;

            case text:
                viewerAnnotation.text = PDFConfig.noteText;
                viewerAnnotation.textFormat = new AnnotationTextFormatModel(15, "#000000");
                viewerAnnotation.backgroundColor = String.format("#%02x%02x%02x", 255, 255, 240);
            case sticky_note:
                viewerAnnotation.text = PDFConfig.noteText;
                viewerAnnotation.textFormat = new AnnotationTextFormatModel();
                break;
            case signature:
            case MANUAL_SIGNATURE:
            case AUTOMATIC_SIGNATURE:
                break;
            case handwriting:
                _correspondenceDetailsFragment.saveHandwrittenAnnotation(viewerAnnotation);
                 break;
        }

        _correspondenceDetailsFragment.saveAnnotationToPdf(viewerAnnotation, PDFConfig.isAllPagesAnnotation());
        PDFConfig.annotationCounter++;

        Log.d("annotationsresult",type.toString());

        return viewerAnnotation;
    }

    private void drawAnnotation(ViewerAnnotationModel viewerAnnotation) {

        SharedPreferences sharedpreferences = _context.getSharedPreferences(Constants.SHARED_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedpreferences.getString(Constants.USER_TOKEN_KEY, "");
        TokenResponse tokenResponse  = gson.fromJson(json, TokenResponse.class);

        try {
            LayoutParams params = new LayoutParams((int) (viewerAnnotation.width * getWidth()), (int) (viewerAnnotation.height * getHeight()));
            StickerImageView stickerImage;
            StickerTextView stickerText;

            byte[] decodedString;
            Bitmap image;
            int resourceId;
            GradientDrawable drawable;

            switch (PDFConfig.generateAnnotationType(viewerAnnotation.type)) {
                case stamps:
                    stickerImage = new StickerImageView(_context, viewerAnnotation);
                    stickerImage.setLayoutParams(params);
                    stickerImage.setX(viewerAnnotation.posX * getWidth());
                    stickerImage.setY(viewerAnnotation.posY * getHeight());
                    String language = Utility.getLocalData(_context, Constants.LANG_KEY);
                    String imageName = viewerAnnotation.imageSource.split("/stamp/")[1].split("/")[0];
                    String viewerURL = Utility.getViewerURL(_context);
                    String viewerAppName = Utility.getViewerAppName(_context);
                    viewerAnnotation.imageSource = "/" + viewerAppName + "/" + _context.getString(R.string.viewer_stamps_image_path, imageName,
                            language, tokenResponse.getAccessToken());
                    Picasso.get().load(viewerURL + viewerAnnotation.imageSource.replaceAll("^/" + viewerAppName, "")).into(stickerImage.getImageView());
                    stickerImage.setAnnotationInterface(this);
                    this.addView(stickerImage);
                    break;
                case highlight:
                    stickerImage = new StickerImageView(_context, viewerAnnotation);
                    stickerImage.setLayoutParams(params);
                    stickerImage.setX(viewerAnnotation.posX * getWidth());
                    stickerImage.setY(viewerAnnotation.posY * getHeight());
                    stickerImage.setImageDrawable(ContextCompat.getDrawable(_context, R.drawable.highlight_drawable));
                    stickerImage.setAnnotationInterface(this);
                    this.addView(stickerImage);
                    break;
                case line:
                    stickerImage = new StickerImageView(_context, viewerAnnotation);
                    params = new LayoutParams((int) (viewerAnnotation.width * getWidth()), (int) (0.1 * getHeight()));
                    stickerImage.setLayoutParams(params);
                    stickerImage.setX(viewerAnnotation.posX * getWidth());
                    stickerImage.setY(viewerAnnotation.posY * getHeight());
                    if (!viewerAnnotation.New)
                        stickerImage.setY((float) (stickerImage.getY() - (params.height / 2)));
                    drawable = (GradientDrawable) ContextCompat.getDrawable(_context, R.drawable.line_drawable);
                    drawable.setStroke(5, Helpers.borderColorToColor(viewerAnnotation.backgroundColor));
                    stickerImage.setImageDrawable(drawable);
                    stickerImage.setAnnotationInterface(this);
                    this.addView(stickerImage);
                    break;

                case ellipse:
                    stickerImage = new StickerImageView(_context, viewerAnnotation);
                    stickerImage.setLayoutParams(params);
                    stickerImage.setX(viewerAnnotation.posX * getWidth());
                    stickerImage.setY(viewerAnnotation.posY * getHeight());
                    drawable = (GradientDrawable) ContextCompat.getDrawable(_context, R.drawable.ellipse_drawable);
                    drawable.setColor(Helpers.borderColorToColor(viewerAnnotation.backgroundColor));
                    drawable.setStroke((int) (viewerAnnotation.borderWidth * getWidth() * 0.25),
                            Helpers.borderColorToColor(viewerAnnotation.borderColor));
                    stickerImage.setImageDrawable(drawable);
                    stickerImage.setAnnotationInterface(this);
                    this.addView(stickerImage);
                    break;

                case rectangle:
                    stickerImage = new StickerImageView(_context, viewerAnnotation);
                    stickerImage.setLayoutParams(params);
                    stickerImage.setX(viewerAnnotation.posX * getWidth());
                    stickerImage.setY(viewerAnnotation.posY * getHeight());
                    drawable = (GradientDrawable) ContextCompat.getDrawable(_context, R.drawable.rectangle_drawable);
                    drawable.setColor(Helpers.borderColorToColor(viewerAnnotation.backgroundColor));
                    drawable.setStroke((int) (viewerAnnotation.borderWidth * getWidth() * 0.25),
                            Helpers.borderColorToColor(viewerAnnotation.borderColor));
                    stickerImage.setImageDrawable(drawable);
                    stickerImage.setAnnotationInterface(this);
                    this.addView(stickerImage);
                    break;
                case blackout:
                    stickerImage = new StickerImageView(_context, viewerAnnotation);
                    stickerImage.setLayoutParams(params);
                    stickerImage.setX(viewerAnnotation.posX * getWidth());
                    stickerImage.setY(viewerAnnotation.posY * getHeight());
                    stickerImage.setBackgroundColor(ContextCompat.getColor(_context, android.R.color.black));
                    stickerImage.setAnnotationInterface(this);
                    this.addView(stickerImage);
                    break;
                case sticky_note:
                case text:
                    stickerText = new StickerTextView(_context, viewerAnnotation);
                    stickerText.setLayoutParams(params);
                    stickerText.setX(viewerAnnotation.posX * getWidth());
                    stickerText.setY(viewerAnnotation.posY * getHeight());
                    stickerText.setAnnotationInterface(this);

                    if (PDFConfig.generateAnnotationType(viewerAnnotation.type) == PDFConfig.AnnotationType.sticky_note) {
                        String backgroundImage = viewerAnnotation.imageSource.split("textAnnotationImage/")[1].toLowerCase();
//                        resourceId = _context.getResources().getIdentifier(backgroundImage, "drawable", _context.getPackageName());
//                        stickerText.setBackground(Objects.requireNonNull(ContextCompat.getDrawable(_context, resourceId)).getConstantState().newDrawable().mutate());
                    }

                    if (viewerAnnotation.text != null && !viewerAnnotation.text.trim().equals("")) {
                        stickerText.setText(URLDecoder.decode(viewerAnnotation.text, "utf-8"));
                    }
                    if (PDFConfig.getDrawType() == PDFConfig.AnnotationType.text) {
                        stickerText.setControlsVisibility(true);
                        if (PDFConfig.noteText == null) {
                            stickerText.openEditNoteDialog();
                        } else {
                            PDFConfig.noteText = null;
                        }
                    }
                    this.addView(stickerText);

                    break;
                case AUTOMATIC_SIGNATURE:
                case MANUAL_SIGNATURE:
                    if (viewerAnnotation.id.equals("-1") || viewerAnnotation.id.equals("-2")) {
                        viewerAnnotation.hasPermission = false;
                    }
                    stickerImage = new StickerImageView(_context, viewerAnnotation);
                    stickerImage.setLayoutParams(params);
                    stickerImage.setX(viewerAnnotation.posX * getWidth());
                    stickerImage.setY(viewerAnnotation.posY * getHeight());
                    if (viewerAnnotation.ImageByte != null && viewerAnnotation.ImageByte.startsWith("http"))
                        Picasso.get().load(viewerAnnotation.ImageByte).into(stickerImage.getImageView());
                    else {
                        if (viewerAnnotation.ImageByte != null && viewerAnnotation.ImageByte.startsWith("data:image/png;base64,"))
                            viewerAnnotation.ImageByte = viewerAnnotation.ImageByte.split("data:image/png;base64,")[1];
                        decodedString = Base64.decode(viewerAnnotation.ImageByte, Base64.DEFAULT);
                        image = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        stickerImage.setImageBitmap(image);
                    }
                    stickerImage.setAnnotationInterface(this);
                    stickerImage.getImageView().setScaleType(ImageView.ScaleType.FIT_CENTER);
                    this.addView(stickerImage);
//                }
                    break;
                case barcode:
                case handwriting:
                    stickerImage = new StickerImageView(_context, viewerAnnotation);
                    stickerImage.setLayoutParams(params);
                    stickerImage.setX(viewerAnnotation.posX * getWidth());
                    stickerImage.setY(viewerAnnotation.posY * getHeight());
                    if (viewerAnnotation.New) {
                        if (PDFConfig.base64ToDraw == null) {
                            decodedString = Base64.decode(viewerAnnotation.ImageByte, Base64.DEFAULT);
                        } else {
                            decodedString = Base64.decode(PDFConfig.base64ToDraw, Base64.DEFAULT);
                            PDFConfig.base64ToDraw = null;
                        }
                        image = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        stickerImage.setImageBitmap(image);
                    } else {
                        viewerURL = Utility.getViewerURL(_context);
                        viewerAppName = Utility.getViewerAppName(_context);
                        String imageURL = viewerAnnotation.imageSource.startsWith(viewerURL) ?
                                viewerAnnotation.imageSource :
                                viewerURL.replaceAll("/" + viewerAppName + "/$", "") + viewerAnnotation.imageSource;
                        if (!imageURL.contains("?token="))
                            imageURL += "?token=" + tokenResponse.getAccessToken();
                        Picasso.get().load(imageURL).into(stickerImage.getImageView());
                    }
                    stickerImage.setAnnotationInterface(this);
                    this.addView(stickerImage);
                    break;
                case INITIAL:
                    stickerImage = new StickerImageView(_context, viewerAnnotation);
                    stickerImage.setLayoutParams(params);
                    stickerImage.setX(viewerAnnotation.posX * getWidth());
                    stickerImage.setY(viewerAnnotation.posY * getHeight());
                    stickerImage.setAnnotationInterface(this);
                    stickerImage.getImageView().setScaleType(ImageView.ScaleType.FIT_CENTER);
                    this.addView(stickerImage);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onDeleteAnnotation(ViewerAnnotationModel v) {
//
//        CorrespondenceDetailsFragment correspondenceDetailsFragment = new CorrespondenceDetailsFragment();
//        //correspondenceDetailsFragment.getContext();
//        correspondenceDetailsFragment.getContext() = _context;
//        _context = correspondenceDetailsFragment.getContext();
//        viewerActivity.deleteAnnotationPDF(v);
        CorrespondenceDetailsFragment correspondenceDetailsFragment = new CorrespondenceDetailsFragment();
        correspondenceDetailsFragment.deleteAnnotationPDF(v);
    }

    @Override
    public void onMoveAnnotation(ViewerAnnotationModel v) {

        CorrespondenceDetailsFragment correspondenceDetailsFragment = new CorrespondenceDetailsFragment();
        correspondenceDetailsFragment.editAnnotationPDF(v, _pageIndex);


    }

    @Override
    public void onScaleAnnotation(ViewerAnnotationModel v) {
//        ViewerActivity viewerActivity = (ViewerActivity) _context;
//        viewerActivity.editAnnotationPDF(v, _pageIndex);
        CorrespondenceDetailsFragment correspondenceDetailsFragment = new CorrespondenceDetailsFragment();
        correspondenceDetailsFragment.editAnnotationPDF(v, _pageIndex);
    }

    @Override
    public void onSetTextAnnotation(ViewerAnnotationModel v) {

        CorrespondenceDetailsFragment correspondenceDetailsFragment = new CorrespondenceDetailsFragment();
        correspondenceDetailsFragment.editAnnotationPDF(v, _pageIndex);
    }

    public void setPageIndex(int pageIndex) {
        _pageIndex = pageIndex;
    }

    public ArrayList<ViewerAnnotationModel> getAnnotations() {
        return _annotations;
    }
}

