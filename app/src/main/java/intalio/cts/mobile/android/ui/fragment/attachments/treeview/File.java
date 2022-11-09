package intalio.cts.mobile.android.ui.fragment.attachments.treeview;

import com.cts.mobile.android.R;

import tellh.com.recyclertreeview_lib.LayoutItemType;

public class File implements LayoutItemType {
    public String fileName;
    public String id;
    public String parentId;

    public File(String fileName, String id, String parentId) {
        this.fileName = fileName;
        this.id = id;
        this.parentId = parentId ;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_file;
    }
}
