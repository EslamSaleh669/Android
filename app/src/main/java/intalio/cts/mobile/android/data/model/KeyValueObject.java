package intalio.cts.mobile.android.data.model;

import com.google.gson.annotations.Expose;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by aem on 8/19/2016.
 */
public class KeyValueObject implements Serializable {

    @Expose
    private String key;

    @Expose
    private String value;

    private String type;

    public KeyValueObject(JSONObject data){
        try{
            this.key = data.getString("Label");
            this.value = data.getString("Value");
        }catch(Exception e){

        }
    }

    public KeyValueObject(String key, String value) {
        this.key = key;
        this.value = value;
        type = "param";
        if(key.equalsIgnoreCase("service")){
            type = "service";
        }else if(key.equalsIgnoreCase("requesttype")){
            type = "requestType";
        }
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }
}
