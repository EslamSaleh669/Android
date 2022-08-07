package intalio.cts.mobile.android.data.model.viewer;

import java.io.Serializable;

public class CheckinRequestModel implements Serializable {
    private String message;

    public CheckinRequestModel() {
    }

    public CheckinRequestModel(String message) {
        this.message = message;
    }
}
