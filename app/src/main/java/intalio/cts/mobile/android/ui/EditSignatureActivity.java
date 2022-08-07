package intalio.cts.mobile.android.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


import com.cts.mobile.android.R;

import java.io.ByteArrayOutputStream;

import intalio.cts.mobile.android.data.model.viewer.SignatureInfo;
import intalio.cts.mobile.android.util.Constants;
import intalio.cts.mobile.android.viewer.Utility;
import intalio.cts.mobile.android.viewer.support.PDFConfig;
import intalio.cts.mobile.android.viewer.views.SignatureView;

public class EditSignatureActivity extends AppCompatActivity implements View.OnClickListener {

    private final int DEFAULT_BRUSH_SIZE = 5;

    private SignatureView drawView;
    private ImageView currPaint;
    private String saveAction;
    private String imageName;
    private int validateMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_signature);
      //  Utility.changeDialogTheme(this, true);
        if (Utility.getLocalData(this, Constants.LANG_KEY).equals("ar")) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    //    Typeface font = Utility.getCustomFont(this, Constants.MAIN_FONT_APP);
        drawView = findViewById(R.id.SignView);
        LinearLayout paintLayout = (LinearLayout) findViewById(R.id.paint_colors);
        currPaint = (ImageView) paintLayout.getChildAt(0);
        currPaint.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.paint_pressed));
        drawView.setBrushSize(DEFAULT_BRUSH_SIZE);
        TextView signTextView = findViewById(R.id.SignTextView);
        TextView tvTools = findViewById(R.id.tvTools);
        TextView tvColors = findViewById(R.id.tvColors);
        ImageButton newBtn = findViewById(R.id.ClearButton);
        newBtn.setOnClickListener(this);
        Button saveBtn = findViewById(R.id.SubmitButton);
        saveBtn.setOnClickListener(this);
        Bundle bundle = getIntent().getExtras();
        saveAction = bundle.getString("save_action");
        imageName = bundle.getString("image_name");
        String title = getResources().getString(bundle.getInt("title"));
        String btnString = getResources().getString(bundle.getInt("btnString"));
        validateMessage = bundle.getInt("validateMessage");
        saveBtn.setText(btnString);
        signTextView.setText(title);
//        signTextView.setTypeface(font);
//        saveBtn.setTypeface(font);
//        tvTools.setTypeface(font);
//        tvColors.setTypeface(font);
        drawView.clearBool = !saveAction.equals("UPDATE_SIGNATURE");

        SeekBar thickness = findViewById(R.id.thickness);
        thickness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float size = DEFAULT_BRUSH_SIZE * progress * 0.02f;
                drawView.setBrushSize(size);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    public void paintClicked(View view) {
        float size = drawView.brushSize;
        drawView.setErase(false);
        if (view != currPaint) {
            ImageView imgView = (ImageView) view;
            String color = view.getTag().toString();
            drawView.setColor(color);
            drawView.restoreSize(size);
            imgView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.paint_pressed));
            currPaint.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.paint));
            currPaint = (ImageView) view;
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ClearButton) {
            this.confirmNewDrawing();
        } else if (view.getId() == R.id.SubmitButton) {
            this.confirmSaveDrawing(saveAction, imageName);
        }
    }

    private void confirmNewDrawing() {
        drawView.startNew();

    }

    private void confirmSaveDrawing(String saveActionType, String imageName) {
        String token = "";
        String signatureId = "";
        drawView.setDrawingCacheEnabled(true);
        drawView.setBackgroundColor(Color.TRANSPARENT);
        Bitmap signatureImage = drawView.getDrawingCache();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        signatureImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String signatureData = Base64.encodeToString(byteArray, Base64.DEFAULT);
        SignatureInfo signatureInfo = new SignatureInfo();
        signatureInfo.setToken(token);
        signatureInfo.setSignatureId(signatureId);
        signatureInfo.setSignature(signatureData);
        if (!drawView.isEmpty()) {
            switch (saveActionType) {
                case "HAND_SIGN":
                case "HAND_TEXT":
                    Intent signatureDataIntent = new Intent();
                    signatureDataIntent.putExtra("noteData", signatureData);
                    setResult(RESULT_OK, signatureDataIntent);
                    Log.d("signatureData",signatureData);

                    finish();
                    break;
                default:
                    break;
            }
            finish();
        } else {
            Utility.showMainAlertDialog(this, R.string.warning, validateMessage, R.string.ok);
        }
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.rectangle_white_signature_view);
        drawView.setBackground(drawable);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.SIGNTAURE_RESULT) {
            this.setFinishOnTouchOutside(true);
            this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }
}
