package intalio.cts.mobile.android.ui.fragment.main.nodetreeview;

import com.cts.mobile.android.R;

import intalio.cts.mobile.android.ui.fragment.main.MainViewModel;
import intalio.cts.mobile.android.util.AutoDispose;
import tellh.com.recyclertreeview_lib.LayoutItemType;

public class NodeChild implements LayoutItemType {
    public String fileName;
    public String inherit;
    public String location;
    public int padding;
    public int id ;
    public MainViewModel viewModel ;
    public AutoDispose autoDispose ;
    public boolean enableTodayCount ;
    public boolean enableTotalCount ;

    public NodeChild(String fileName, String inherit , String location,
                     int padding, int id ,MainViewModel viewModel,
                     AutoDispose autoDispose,boolean enableTodayCount , boolean enableTotalCount) {
        this.fileName = fileName;
        this.inherit = inherit;
        this.location = location;
        this.padding = padding ;
        this.id = id ;
        this.viewModel = viewModel;
        this.autoDispose = autoDispose ;
        this.enableTodayCount = enableTodayCount ;
        this.enableTotalCount = enableTotalCount ;


    }

    @Override
    public int getLayoutId() {
        return R.layout.child_dir;
    }
}
