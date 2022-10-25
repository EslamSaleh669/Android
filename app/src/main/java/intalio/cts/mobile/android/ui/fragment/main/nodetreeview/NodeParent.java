package intalio.cts.mobile.android.ui.fragment.main.nodetreeview;

import com.cts.mobile.android.R;

import tellh.com.recyclertreeview_lib.LayoutItemType;

public class NodeParent implements LayoutItemType {
    public String dirName;
    public String id;
    public String location;
    public int padding;

    public NodeParent(String dirName, String id, String location, int padding) {
        this.dirName = dirName;
        this.id = id;
        this.location = location;
        this.padding = padding;

    }

    @Override
    public int getLayoutId() {
        return R.layout.parent_dir;
    }
}
