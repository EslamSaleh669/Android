package intalio.cts.mobile.android.data.model;

import java.io.Serializable;

public class TreeNodeStateModel implements Serializable {
    private Boolean opened;
    private Boolean disabled;
    private Boolean selected;

    public Boolean getOpened() {
        return opened != null && opened;
    }

    public Boolean getDisabled() {
        return disabled != null && disabled;
    }

    public Boolean getSelected() {
        return selected != null && selected;
    }
}
