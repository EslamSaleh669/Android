package intalio.cts.mobile.android.ui.fragment.attachments.treeview;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cts.mobile.android.R;

import tellh.com.recyclertreeview_lib.TreeNode;
import tellh.com.recyclertreeview_lib.TreeViewBinder;

/**
 * Created by tlh on 2016/10/1 :)
 */

public class DirectoryNodeBinder extends TreeViewBinder<DirectoryNodeBinder.ViewHolder> {




    @Override
    public ViewHolder provideViewHolder(View itemView) {
        return new ViewHolder(itemView);
    }

    @Override
    public void bindView(ViewHolder holder, int position, TreeNode node) {
        holder.ivArrow.setRotation(0);
        holder.ivArrow.setImageResource(R.drawable.ic_keyboard_arrow_right_black_18dp);
        int rotateDegree = node.isExpand() ? 90 : 0;
        holder.ivArrow.setRotation(rotateDegree);
        Dir dirNode = (Dir) node.getContent();
        holder.tvName.setText(dirNode.dirName);
        holder.nodeID.setText(dirNode.id);
        if (node.isLeaf())
            holder.ivArrow.setVisibility(View.INVISIBLE);
        else holder.ivArrow.setVisibility(View.VISIBLE);

    }

    @Override
    public int getLayoutId() {
        return R.layout.item_dir;
    }

    public static class ViewHolder extends TreeViewBinder.ViewHolder {
        private ImageView ivArrow;
        private TextView tvName;
        private LinearLayout linearLayout ;
        private TextView nodeID ;

        public ViewHolder(View rootView) {
            super(rootView);
            this.ivArrow = (ImageView) rootView.findViewById(R.id.iv_arrow);
            this.tvName = (TextView) rootView.findViewById(R.id.tv_name);
            this.nodeID = (TextView) rootView.findViewById(R.id.nodeid);
            this.linearLayout = (LinearLayout) rootView.findViewById(R.id.roorlin);
        }

        public ImageView getIvArrow() {
            return ivArrow;
        }

        public LinearLayout getLinearLayout() {
            return linearLayout;
        }

        public String getTvName() {

            return tvName.getText().toString();
        }

        public String getType (){
            return "folder" ;
        }

        public String getId () {
            return nodeID.getText().toString();
        }
    }
   public interface InterfacePositionCard {
        void getPosition (int position , int status) ;
     }

}
