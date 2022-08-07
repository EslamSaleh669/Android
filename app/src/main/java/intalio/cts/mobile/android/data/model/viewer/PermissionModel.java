package intalio.cts.mobile.android.data.model.viewer;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.cts.mobile.android.R;
import com.google.gson.annotations.Expose;

/**
 * Created by aem on 10/11/2017.
 */

public class PermissionModel implements Parcelable {

    @Expose
    private boolean isBlackOut = false;
    @Expose
    private String fullName;
    @Expose
    private String id;
    @Expose
    private boolean isForced;
    @Expose
    private String annotationPermissionId;

    public PermissionModel(String fullName, String id, boolean isForced) {
        this.fullName = fullName;
        this.isForced = isForced;
        this.id = id;
    }

    public void setIsBlackOutAnnotation(boolean b) {
        isBlackOut = b;
    }

    public boolean isBlackOutAnnotation() {
        return isBlackOut;
    }

    public String getSplittedUser() {
        if (fullName == null)
            fullName = "Everyone";
        String name = "";
        if (fullName.contains("|")) {
            name = fullName.split("\\|")[1];
        } else {
            name = fullName;
        }
        return name;

    }

    public String getFullName() { // get splitted user
        return fullName;
    }

    public String getId() { // get splitted user
        return id;
    }

    public void setFullName(String fullName) { // set user Not splitted
        this.fullName = fullName;
    }

    public void setIsForced(boolean t) {
        isForced = t;
    }


    public PermissionModel(Parcel in) {
        String[] data = new String[3];
        in.readStringArray(data);
        this.fullName = data[0];
        this.id = data[1];
        this.isForced = data[2].equals("true");
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
            this.fullName,
            this.id,
            this.isForced ? "true" : "false"});
    }

    public String getStringType(Context context) {
        return isForced ? context.getString(R.string.force) :
            context.getString(R.string.view);
    }

    public boolean getPermissionType() {
        return isForced;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator CREATOR = new Creator() {
        public PermissionModel createFromParcel(Parcel in) {
            return new PermissionModel(in);
        }

        public PermissionModel[] newArray(int size) {
            return new PermissionModel[size];
        }
    };
}
