package intalio.cts.mobile.android.data.model.viewer;

/**
 * Created by aem on 8/26/2016.
 */
public class SignatureInfo {
    private String Token;
    private String SignatureId;
    private String signature;

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

    public String getSignatureId() {
        return SignatureId;
    }

    public void setSignatureId(String signatureId) {
        SignatureId = signatureId;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
