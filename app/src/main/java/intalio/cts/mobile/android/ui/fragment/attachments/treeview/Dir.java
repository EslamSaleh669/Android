package intalio.cts.mobile.android.ui.fragment.attachments.treeview;

import com.cts.mobile.android.R;

import tellh.com.recyclertreeview_lib.LayoutItemType;

public class Dir implements LayoutItemType {
    public String dirName;
    public String id;

    public Dir(String dirName, String id) {
        this.dirName = dirName;
        this.id = id;

    }

    @Override
    public int getLayoutId() {
        return R.layout.item_dir;
    }
}
