package intalio.cts.mobile.android.viewer;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.exifinterface.media.ExifInterface;

import com.cts.mobile.android.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
 

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import intalio.cts.mobile.android.util.Constants;

/**
 * Created by aem on 8/19/2016.
 */
public class Utility {
    public static boolean isDemoUser = false;
    public static int Scrolled = 0;
    public static String dateForDialog;
    public static int lastCheckedDelegate = 0;
    public static boolean isThereAnyDelegate = false;
//    public static ArrayList<Correspondence> correspondences = new ArrayList<>();
//    public static List<PurposeModel> purposes = new ArrayList<>();
//    public static List<PrivacyModel> privacies = new ArrayList<>();
//    public static List<PriorityModel> priorities = new ArrayList<>();
//    public static List<ImportanceModel> importances = new ArrayList<>();
//    public static List<CategoryModel> categories = new ArrayList<>();
//    public static List<StatusModel> statuses = new ArrayList<>();
    public static boolean isLandScape = false;

    public static final Gson gson = new GsonBuilder().create();

    //Change value to change from arrows to swipe or vice versa
//    public static boolean mainFragmentInboxDesignWithArrows =false;
    public static void reset() {
        lastCheckedDelegate = 0;
        isThereAnyDelegate = false;
        Scrolled = 0;
    }


    public static void showAlertDialog(Context context, int titleId, int messageId, int positiveButtonId, final DialogInterface.OnClickListener onClickListener, int negativeButtonId, final DialogInterface.OnClickListener negativeOnClickListener) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setMessage(messageId)
            .setTitle(titleId)
            .setPositiveButton(positiveButtonId, onClickListener)
            .setNegativeButton(negativeButtonId, negativeOnClickListener);
        try {
            builder.show();
        } catch (Exception e) {
            Log.e(Utility.class.getSimpleName(), e.getMessage());
        }
    }

    public static void showAlertDialog(Context context, int titleId, int messageId, int positiveButtonId, final DialogInterface.OnClickListener onClickListener) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setMessage(messageId)
            .setTitle(titleId)
            .setPositiveButton(positiveButtonId, onClickListener);
        try {
            builder.show();
        } catch (Exception e) {
            Log.e(Utility.class.getSimpleName(), e.getMessage());
        }
    }

    public static void showAlertDialog(Context context, int titleId, int messageId, int positiveButtonId) {
        showAlertDialog(context, titleId, messageId, positiveButtonId, (dialog, which) -> {
        });
    }

    public static void updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }

    public static Typeface getCustomFont(Context context, String fontName) {
        return Typeface.createFromAsset(context.getAssets(), "fonts/" + fontName);
    }

//    public static void saveLocalData(Context context, String key, String value) {
//        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.SHAREDPREFFNAME, Context.MODE_PRIVATE).edit();
//        editor.putString(key, value);
//        editor.apply();
//    }

    public static String getLocalData(Context context, String key) {
        return getLocalData(context, key, "en");
    }

    public static String getLocalData(Context context, String key, String defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_NAME, Context.MODE_PRIVATE);
        return prefs.getString(key, defaultValue);
    }

    public static String getStringFromXML(Context context, String name) {
        try {
            int id = context.getResources().getIdentifier(name, "string", context.getPackageName());
            return context.getResources().getString(id);
        } catch (Exception e) {
            return name;
        }
    }

    public static String getStringFromXmlById(Context context, int stringId) {
        String retString = "";
        try {
            retString = context.getResources().getString(stringId);
        } catch (Exception e) {
            Log.e("Translation Error", stringId + " is not translated");
        }
        return retString;
    }

    public static int convertToDp(Context context, int dps) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dps * scale + 0.5f);
    }

//    public static Integer[] getIconDetail(int searchIndex, int searchInteger) {
//        Integer[] iconDetailRet = null;
//        for (int i = 0; i < Constants.instructionArray.size(); i++) {
//            Integer[] iconDetail = Constants.instructionArray.get(i);
//            if (searchInteger == iconDetail[searchIndex]) {
//                iconDetailRet = iconDetail;
//            }
//        }
//        return iconDetailRet;
//    }

//    public static String searchValueInArrayByKey(String key, ArrayList<KeyValueObject> arrayList) {
//        String retValue = "";
//        for (int i = 0; i < arrayList.size(); i++) {
//            if (arrayList.get(i).getKey().equals(key)) {
//                retValue = arrayList.get(i).getValue();
//                break;
//            }
//        }
//        return retValue;
//    }

    public static List<View> getAllChildrenOfView(View v) {
        if (!(v instanceof ViewGroup)) {
            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            return viewArrayList;
        }

        ArrayList<View> result = new ArrayList<View>();

        ViewGroup viewGroup = (ViewGroup) v;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            result.addAll(getAllChildrenOfView(child));
        }
        return result;
    }

    public static void showMainAlertDialog(Context context, int title, int message, int button) {
        AppCompatActivity app = (AppCompatActivity) context;
        String titleString = context.getResources().getString(title);
        String messageString = context.getResources().getString(message);
//        if (context instanceof DelegateDialog || context instanceof MainActivity) {
//            if (dateForDialog != null && !dateForDialog.equals("null"))
//                messageString += " " + dateForDialog;
//            /// ....
//        }
        String btnString = context.getResources().getString(button);
        Intent alertIntent = new Intent(app, AlertDialog.class);
        alertIntent.putExtra("title", titleString);
        alertIntent.putExtra("message", messageString);
        alertIntent.putExtra("btnString", btnString);
        context.startActivity(alertIntent);
    }

    public static void showMainAlertServer(Context context, int title, String message, int button) {
        AppCompatActivity app = (AppCompatActivity) context;
        String titleString = context.getResources().getString(title);
        String btnString = context.getResources().getString(button);
        Intent alertIntent = new Intent(app, AlertDialog.class);
        alertIntent.putExtra("title", titleString);
        alertIntent.putExtra("message", message);
        alertIntent.putExtra("btnString", btnString);
        context.startActivity(alertIntent);
    }

//    public static void showMainPromptDialog(Context context, int title, int message, int positiveButton, int negativeButton, int resultCode, int data) {
////        AppCompatActivity app = (AppCompatActivity) context;
////        String titleString = context.getResources().getString(title);
////        String messageString = context.getResources().getString(message);
////        String btnPositive = context.getResources().getString(positiveButton);
////        String btnNegative = context.getResources().getString(negativeButton);
////        Intent alertIntent = new Intent(app, PromptDialog.class);
////        alertIntent.putExtra("title", titleString);
////        alertIntent.putExtra("message", messageString);
////        alertIntent.putExtra("btnPositive", btnPositive);
////        alertIntent.putExtra("btnNegative", btnNegative);
////        alertIntent.putExtra("data", data);
////        ((AppCompatActivity) context).startActivityForResult(alertIntent, resultCode);
//    }

    public static void hideKeyboard(final Context context, final View view) {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            try {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            } catch (Exception ignored) {
            }
        }, 100);
    }

//    public static String getUrlString(Context context) {
//        return Utility.getLocalData(context, "url", context.getString(R.string.cts_root_api_url));
//    }

    public static String changeDateFormat(String input, String output, String dateString) throws Exception {
        DateFormat inputDate = new SimpleDateFormat(input);
        DateFormat outputDate = new SimpleDateFormat(output, Locale.ENGLISH);
        Date date = inputDate.parse(dateString);
        return outputDate.format(date);
    }

//    public static void changeTheme(Context context) {
//        if (Utility.getLocalData(context, "Offline").equals("On")) {
//            context.setTheme(R.style.AppThemeOffline);
//        } else {
//            context.setTheme(R.style.AppThemeGreen);
//        }
//    }

//    public static void changeDialogTheme(Context context, Boolean isClose) {
//        if (Utility.getLocalData(context, "Offline").equals("On")) {
//            if (isClose) {
//                context.setTheme(R.style.dialogThemeOffline);
//            } else {
//                context.setTheme(R.style.dialogThemeOfflineNoClose);
//            }
//        } else {
//            if (isClose) {
//                context.setTheme(R.style.dialogThemeGreen);
//            } else {
//                context.setTheme(R.style.dialogThemeGreenNoClose);
//            }
//        }
//    }


    public static void disableTouchActivity(AppCompatActivity app) {
        app.setFinishOnTouchOutside(false);
        app.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public static void enableTouchActivity(AppCompatActivity app) {
        app.setFinishOnTouchOutside(true);
        app.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public static Bitmap modifyOrientation(Bitmap bitmap, String image_absolute_path, int imageViewWidth, int imageViewHeight) {
        try {
            if (image_absolute_path != null) {
                ExifInterface ei = new ExifInterface(image_absolute_path);
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        bitmap = rotateBitmap(bitmap, 90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        bitmap = rotateBitmap(bitmap, 180);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        bitmap = rotateBitmap(bitmap, 270);
                        break;
                    case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                        bitmap = flip(bitmap, true, false);
                        break;
                    case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                        bitmap = flip(bitmap, false, true);
                        break;
                    default:
                        break;
                }
            }
            bitmap = Bitmap.createScaledBitmap(bitmap, imageViewWidth, imageViewHeight, true);
        } catch (Exception e) {
            Log.e("ModifyOrientation", e.getMessage());
        }
        return bitmap;
    }

    public static String EncodeToBase64(Bitmap bitmap) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }


    private static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }


    public static int getScreenWidth(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.heightPixels;
    }

//////////////////////////////////////////////////For Mac Address Ip Address

//    public static String GetDelegateGctIdOrZero() {
//        try {
//            if (Utility.delegateData != null && Utility.lastCheckedDelegate != 0) {
//                return Utility.delegateData.getDelegateGctId();
//            } else {
//                return "0";
//            }
//        } catch (Exception e) {
//            Log.e(Utility.class.getSimpleName(), e.getMessage());
//        }
//        return "0";
//    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public static boolean isNumeric(String text) {
        boolean bRet = false;
        if (Pattern.matches("^\\d*$", text)) {
            bRet = true;
        }
        return bRet;
    }

    public static String getViewerURL(Context context) {
        String viewerUrl = getLocalData(context, Constants.ViewerURL, context.getString(R.string.viewer_root_api_url));
        if (viewerUrl.charAt(viewerUrl.length() - 1) != '/')
            viewerUrl += "/";
        return viewerUrl;
    }

    public static String getViewerAppName(Context context) {
        String viewerAppName = "VIEWER" ;
//        String viewerAppName = getLocalData(context, Constants.ViewerAppName);
//        if (viewerAppName != null)
//            return viewerAppName;
//        String viewerUrl = getViewerURL(context);
//        viewerAppName = viewerUrl.split("/")[viewerUrl.split("/").length - 1];
//        saveLocalData(context, Constants.ViewerAppName, viewerAppName);
        return viewerAppName;
    }

    public static String getExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index > 0) {
            return fileName.substring(index + 1);
        }
        return null;
    }

    public static boolean checkStoragePermission(Activity activity, int requestCode) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                requestCode);
            return false;
        }

        return true;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int degree) {
        if (degree == 0)
            return bitmap;
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        mtx.postRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    public static String getStringResourceByName(Context context, String aString) {
        String packageName = context.getPackageName();
        int resId = context.getResources().getIdentifier(aString, "string", packageName);
        return context.getString(resId);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String toCamelCase(String string) {
        return Arrays.stream(string.split("_")).map(s -> String.valueOf(s.charAt(0)).toUpperCase() + s.substring(1)).collect(Collectors.joining());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String toFormattedCase(String string) {
        return Arrays.stream(string.split("_")).map(s -> String.valueOf(s.charAt(0)).toUpperCase() + s.substring(1)).collect(Collectors.joining(" "));
    }

    public static void showTopSnackBar(View parent, int resText, int length) {
        Snackbar snack = Snackbar.make(parent, resText, length);
        snack.setAction(R.string.cancel, v -> snack.dismiss());
        snack.show();
    }
}
