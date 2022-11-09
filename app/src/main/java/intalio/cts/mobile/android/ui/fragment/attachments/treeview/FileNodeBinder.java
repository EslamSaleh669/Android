package intalio.cts.mobile.android.ui.fragment.attachments.treeview;

import android.view.View;
import android.widget.TextView;

import com.cts.mobile.android.R;

import tellh.com.recyclertreeview_lib.TreeNode;
import tellh.com.recyclertreeview_lib.TreeViewBinder;

/**
 * Created by tlh on 2016/10/1 :)
 */

public class FileNodeBinder extends TreeViewBinder<FileNodeBinder.ViewHolder> {
    @Override
    public ViewHolder provideViewHolder(View itemView) {
        return new ViewHolder(itemView);
    }

    @Override
    public void bindView(ViewHolder holder, int position, TreeNode node) {
        File fileNode = (File) node.getContent();
        holder.tvName.setText(fileNode.fileName);
        holder.fileID.setText(fileNode.id);
        holder.fileParentId.setText(fileNode.parentId);

    }

    @Override
    public int getLayoutId() {
        return R.layout.item_file;
    }

    public class ViewHolder extends TreeViewBinder.ViewHolder {
        public TextView tvName;
        public TextView fileID, fileParentId;

        public ViewHolder(View rootView) {
            super(rootView);
            this.tvName = (TextView) rootView.findViewById(R.id.tv_name);
            this.fileID = (TextView) rootView.findViewById(R.id.fileid);
            this.fileParentId = (TextView) rootView.findViewById(R.id.fileparentid);

        }
        public String getId () {
            return fileID.getText().toString();
        }
        public String getParentId () {
            return fileParentId.getText().toString();
        }
    }


}
