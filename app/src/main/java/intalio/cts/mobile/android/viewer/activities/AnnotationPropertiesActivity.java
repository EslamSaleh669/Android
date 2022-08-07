package intalio.cts.mobile.android.viewer.activities;


import static intalio.cts.mobile.android.util.Constants.CANCELED_ANNOTATION_PROPERTIES_RESULT;
import static intalio.cts.mobile.android.util.Constants.STICKY_NOTE_PROPERTIES_RESULT;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.cts.mobile.android.R;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import intalio.cts.mobile.android.data.model.viewer.AnnotationTextFormatModel;
import intalio.cts.mobile.android.data.model.viewer.ViewerAnnotationModel;
import intalio.cts.mobile.android.util.Constants;
import intalio.cts.mobile.android.viewer.Helpers;
import intalio.cts.mobile.android.viewer.Utility;
import intalio.cts.mobile.android.viewer.stickerView.StickerDrawView;
import intalio.cts.mobile.android.viewer.stickerView.StickerTextView;
import intalio.cts.mobile.android.viewer.stickerView.StickerView;
import intalio.cts.mobile.android.viewer.support.PDFConfig;

/**
 * Created by jka on 10/16/2017.
 */

public class AnnotationPropertiesActivity extends AppCompatActivity implements View.OnClickListener, ColorPickerDialogListener {
    private final int BACKGROUND_COLOR_DIALOG = 0;
    private final int LINE_COLOR_DIALOG = 1;

    private EditText mNoteEditTextAnnotationProperties, mFontSizeEditTextAnnotationProperties, mEtBorderWidth;
    private ImageButton mBackgroundColorImageButton, mLineColorImageButton;

    private ViewerAnnotationModel _annotation;

    private StickerView mStickerView;


    public void getBundle() {
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        _annotation = (ViewerAnnotationModel) bundle.getSerializable("Annotation");
        if (PDFConfig.stickerView != null)
            mStickerView = PDFConfig.stickerView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annotation_properties);
//        Utility.changeTheme(this);
        if (Utility.getLocalData(this, "language").equals("ar")) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        setFinishOnTouchOutside(false);
        getBundle();
        init();
    }

    public void init() {
       // Typeface myFont = Utility.getCustomFont(this, Constants.MAIN_FONT_APP);

        TextView mTvProperty = (TextView) findViewById(R.id.tvProperties);
     //   mTvProperty.setTypeface(myFont);

        TextView mTvBckColor = (TextView) findViewById(R.id.tvBckColor);
      //  mTvBckColor.setTypeface(myFont);

        TextView mTvFontSize = (TextView) findViewById(R.id.tvFontSize);
       // mTvFontSize.setTypeface(myFont);

        TextView mTvFontColor = (TextView) findViewById(R.id.tvFontColor);
     //  mTvFontColor.setTypeface(myFont);

        TextView mTvBorderWidth = (TextView) findViewById(R.id.tvBorderWidth);
        //mTvBorderWidth.setTypeface(myFont);

        LinearLayout mNoteLinearLayout = (LinearLayout) findViewById(R.id.text_annotation_properties_linearlayout);
        LinearLayout mFontSizeLinearLayout = (LinearLayout) findViewById(R.id.font_size_linearlayout);
        LinearLayout mBackgroundColorLinearLayout = (LinearLayout) findViewById(R.id.background_color_linearlayout);
        LinearLayout mLineColorLinearLayout = (LinearLayout) findViewById(R.id.line_color_linearlayout);
        LinearLayout mLlBorderWidth = (LinearLayout) findViewById(R.id.llBorderWidth);

        mNoteEditTextAnnotationProperties = (EditText) findViewById(R.id.etText_annotation_properties);
      //  mNoteEditTextAnnotationProperties.setTypeface(myFont);

        mFontSizeEditTextAnnotationProperties = (EditText) findViewById(R.id.etFontSize_annotation_properties);
       // mFontSizeEditTextAnnotationProperties.setTypeface(myFont);

        mEtBorderWidth = (EditText) findViewById(R.id.etBorderWidth);
       // mEtBorderWidth.setTypeface(myFont);

        mBackgroundColorImageButton = (ImageButton) findViewById(R.id.background_color_button_properties);
        mLineColorImageButton = (ImageButton) findViewById(R.id.line_color_button_properties);

        mBackgroundColorImageButton.setOnClickListener(this);
        mLineColorImageButton.setOnClickListener(this);

        PDFConfig.AnnotationType annotationType = PDFConfig.generateAnnotationType(_annotation.type);
        if (annotationType.equals(PDFConfig.AnnotationType.text)) {
            setTextViews();//setting strings to textviews
            setBackgroundColor();//setting background color
            setTextFontColor(); // setting text font color
            mLlBorderWidth.setVisibility(View.GONE);
        } else if (annotationType.equals(PDFConfig.AnnotationType.ellipse)
                || annotationType.equals(PDFConfig.AnnotationType.rectangle)
                || annotationType.equals(PDFConfig.AnnotationType.line)) {
            //removing all textviews from layout
            mNoteLinearLayout.setVisibility(View.GONE);
            mFontSizeLinearLayout.setVisibility(View.GONE);
            mTvFontColor.setText(getString(R.string.border_color));
            setBackgroundColor();
            setBorderColor();
            mEtBorderWidth.setText(String.valueOf((int) _annotation.borderWidth));
        } else if (annotationType.equals(PDFConfig.AnnotationType.sticky_note)) {
            mBackgroundColorLinearLayout.setVisibility(View.GONE);
            mLineColorLinearLayout.setVisibility(View.GONE);
            mLlBorderWidth.setVisibility(View.GONE);
            setTextViews();
        }

        Button mSaveAnnotationProperties = (Button) findViewById(R.id.save_annotation_properties);
        //  mSaveAnnotationProperties.setTypeface(myFont);
        mSaveAnnotationProperties.setOnClickListener(this);

        Button mCancelAnnotationProperties = (Button) findViewById(R.id.cancel_annotation_properties);
      //  mCancelAnnotationProperties.setTypeface(myFont);
        mCancelAnnotationProperties.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        mNoteEditTextAnnotationProperties.clearFocus();
        switch (view.getId()) {
            case R.id.background_color_button_properties:
                ColorPickerDialog.newBuilder().setColor(Color.WHITE).setDialogId(BACKGROUND_COLOR_DIALOG).show(this);
                break;
            case R.id.line_color_button_properties:
                ColorPickerDialog.newBuilder().setColor(Color.WHITE).setDialogId(LINE_COLOR_DIALOG).show(this);
                break;

            case R.id.save_annotation_properties:
                //setResutl
                PDFConfig.AnnotationType annotationType = PDFConfig.generateAnnotationType(_annotation.type);
                boolean isSuccess = false; //

                if (annotationType.equals(PDFConfig.AnnotationType.text)) {
                    if (!mNoteEditTextAnnotationProperties.getText().toString().equals("") && !mFontSizeEditTextAnnotationProperties.getText().toString().equals("")) {
                        isSuccess = true;
                        _annotation.text = mNoteEditTextAnnotationProperties.getText().toString();
                        int backgroundColor = ((ColorDrawable) mBackgroundColorImageButton.getDrawable()).getColor();
                        _annotation.backgroundColor = String.format("#%02x%02x%02x", Color.red(backgroundColor), Color.green(backgroundColor), Color.blue(backgroundColor));
                        _annotation.opacity = Color.alpha(backgroundColor) / 255.0;
                        int borderColor = ((ColorDrawable) mLineColorImageButton.getDrawable()).getColor();
                        _annotation.textFormat = new AnnotationTextFormatModel(Integer.parseInt(mFontSizeEditTextAnnotationProperties.getText().toString()),
                                String.format("#%02x%02x%02x", Color.red(borderColor), Color.green(borderColor), Color.blue(borderColor)));
                        ((StickerTextView) mStickerView).setText(_annotation.text);
                        ((StickerTextView) mStickerView).setTextColor(Helpers.fontColorToColor(_annotation.textFormat.fontColor));
                        mStickerView.setBackgroundColor(Color.parseColor(_annotation.backgroundColor));
                        ((StickerTextView) mStickerView).setTextSize(_annotation.textFormat.fontSize);
                        mStickerView.updateAnnotation(_annotation);
                    }
                } else if (annotationType.equals(PDFConfig.AnnotationType.ellipse)
                        || annotationType.equals(PDFConfig.AnnotationType.rectangle)
                        || annotationType.equals(PDFConfig.AnnotationType.line)) {
                    int backgroundColor = ((ColorDrawable) mBackgroundColorImageButton.getDrawable()).getColor();
                    _annotation.backgroundColor = String.format("#%02x%02x%02x", Color.red(backgroundColor), Color.green(backgroundColor), Color.blue(backgroundColor));
                    int borderColor = ((ColorDrawable) mLineColorImageButton.getDrawable()).getColor();
                    _annotation.borderColor = String.format("#%02x%02x%02x", Color.red(borderColor), Color.green(borderColor), Color.blue(borderColor));
                    if (!mEtBorderWidth.getText().toString().equals("")) {
                        isSuccess = true;
                        _annotation.borderWidth = Integer.parseInt(mEtBorderWidth.getText().toString());
                        ((StickerDrawView) mStickerView).drawAnnotation(_annotation);
                        mStickerView.updateAnnotation(_annotation);
                    }
                } else if (annotationType.equals(PDFConfig.AnnotationType.sticky_note)) {
                    if (!mNoteEditTextAnnotationProperties.getText().toString().equals("") && !mFontSizeEditTextAnnotationProperties.getText().toString().equals("")) {
                        isSuccess = true;
                        _annotation.text = mNoteEditTextAnnotationProperties.getText().toString();
                        _annotation.textFormat = new AnnotationTextFormatModel(Integer.parseInt(mFontSizeEditTextAnnotationProperties.getText().toString()));
                        ((StickerTextView) mStickerView).setText(_annotation.text);
                        ((StickerTextView) mStickerView).setTextSize(_annotation.textFormat.fontSize);
                        mStickerView.updateAnnotation(_annotation);
                    }
                }
                if (isSuccess) {
                    Intent i = getIntent();
                    i.putExtra("Annotation", _annotation);
                    setResult(RESULT_OK, i);
                    finish();
                } else {
                    Utility.showAlertDialog(this, R.string.warning, R.string.requiredField, R.string.ok);
                }


                break;
            case R.id.cancel_annotation_properties:
                //setResutl
                PDFConfig.AnnotationType annotationType1 = PDFConfig.generateAnnotationType(_annotation.type);
                if (annotationType1.equals(PDFConfig.AnnotationType.text) || annotationType1.equals(PDFConfig.AnnotationType.sticky_note)) {
                    Intent intent = getIntent();
                    intent.putExtra("Annotation", _annotation);
                    setResult(STICKY_NOTE_PROPERTIES_RESULT, intent);
                } else {
                    setResult(CANCELED_ANNOTATION_PROPERTIES_RESULT);
                }
                finish();
                break;

            default:

                break;
        }
    }

    @Override
    public void onColorSelected(int dialogId, @ColorInt int color) {
        switch (dialogId) {
            case BACKGROUND_COLOR_DIALOG:
                ColorDrawable backgroundColorDrawable = new ColorDrawable(color);
                mBackgroundColorImageButton.setImageDrawable(backgroundColorDrawable);
                break;
            case LINE_COLOR_DIALOG:
                ColorDrawable lineColorDrawable = new ColorDrawable(color);
                mLineColorImageButton.setImageDrawable(lineColorDrawable);
                break;
            default:
                break;
        }

    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }

    public void setTextViews() {
        if (_annotation.text != null) {
            mNoteEditTextAnnotationProperties.setText(_annotation.text);
        }

        if (_annotation.textFormat != null) {
            mFontSizeEditTextAnnotationProperties.setText(String.valueOf(_annotation.textFormat.fontSize));
        }
    }

    public void setBackgroundColor() {
        if (_annotation.backgroundColor != null) {//if edit mode
            mBackgroundColorImageButton.setImageDrawable(new ColorDrawable(Color.parseColor(_annotation.backgroundColor)));
        } else { //if new annotation
            mBackgroundColorImageButton.setImageDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.note_background_color)));
        }
    }

    public void setTextFontColor() {
        if (_annotation.textFormat.fontColor != null) {//if edit mode
            mLineColorImageButton.setImageDrawable(new ColorDrawable(Helpers.fontColorToColor(_annotation.textFormat.fontColor)));
        } else {//if new annotation
            mLineColorImageButton.setImageDrawable(new ColorDrawable(Color.BLACK));
        }
    }

    public void setBorderColor() {
        if (_annotation.borderColor != null) {
            mLineColorImageButton.setImageDrawable(new ColorDrawable(Helpers.fontColorToColor(_annotation.borderColor)));
        }
    }

    @Override
    public void onBackPressed() {
        PDFConfig.AnnotationType annotationType1 = PDFConfig.generateAnnotationType(_annotation.type);
        if (annotationType1.equals(PDFConfig.AnnotationType.text) || annotationType1.equals(PDFConfig.AnnotationType.sticky_note)) {
            Intent intent = getIntent();
            intent.putExtra("Annotation", _annotation);
            setResult(STICKY_NOTE_PROPERTIES_RESULT, intent);
        } else {
            setResult(CANCELED_ANNOTATION_PROPERTIES_RESULT);
        }
        super.onBackPressed();
    }
}
