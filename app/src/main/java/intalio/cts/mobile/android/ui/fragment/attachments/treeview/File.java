package intalio.cts.mobile.android.ui.fragment.attachments.treeview;

import com.cts.mobile.android.R;

import tellh.com.recyclertreeview_lib.LayoutItemType;

public class File implements LayoutItemType {
    public String fileName;
    public String id;

    public File(String fileName, String id) {
        this.fileName = fileName;
        this.id = id;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_file;
    }
}
