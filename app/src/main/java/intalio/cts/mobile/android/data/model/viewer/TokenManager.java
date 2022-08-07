package intalio.cts.mobile.android.data.model.viewer;

import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Date;

public class TokenManager {

    public static String accessToken;

    public static JSONObject decodeToken(String _token) throws JSONException {
        return new JSONObject(new String(Base64.decode(_token.split("\\.")[1], Base64.URL_SAFE), StandardCharsets.UTF_8));
    }

    public static String getUserId() {
        try {
            return decodeToken(accessToken).getString("Id");
        } catch (Exception e) {
            return "";
        }
    }

    public static String getStructureId() {
        try {
            return decodeToken(accessToken).getString("StructureId");
        } catch (Exception e) {
            return "0";
        }
    }

    public static String getUserDisplayName() {
        try {
            return decodeToken(accessToken).getString("DisplayName");
        } catch (Exception e) {
            return "";
        }
    }

    public static boolean isTokenValid(String _token) {
        if (_token == null)
            return false;

        try {
            JSONObject jsonObject = decodeToken(_token);
            if (!jsonObject.has("exp")) return false;
            jsonObject.get("exp");
            return jsonObject.getInt("exp") - new Date().getTime() / 1000 > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
