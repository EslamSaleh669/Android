package intalio.cts.mobile.android.ui.fragment.main.nodetreeview;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.cts.mobile.android.R;

import tellh.com.recyclertreeview_lib.TreeNode;
import tellh.com.recyclertreeview_lib.TreeViewBinder;

/**
 * Created by tlh on 2016/10/1 :)
 */

public class ParentNodeBinder extends TreeViewBinder<ParentNodeBinder.ParentViewHolder> {



    @Override
    public ParentViewHolder provideViewHolder(View itemView) {
        return new ParentViewHolder(itemView);
    }

    @Override
    public void bindView(ParentViewHolder holder, int position, TreeNode node) {


        holder.ivArrow.setRotation(0);
        holder.ivArrow.setImageResource(R.drawable.ic_nodednownarrow);
        int rotateDegree = node.isExpand() ? 0 : 0;
        holder.ivArrow.setRotation(rotateDegree);
        NodeParent dirNode = (NodeParent) node.getContent();

        RelativeLayout.LayoutParams childIconLayout =
                new RelativeLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);

        childIconLayout.setMarginStart(dirNode.padding);
        childIconLayout.setMarginEnd(20);
        childIconLayout.width = 50 ;
        childIconLayout.height = 50 ;
        childIconLayout.addRule(RelativeLayout.CENTER_VERTICAL);


        holder.tvName.setText(dirNode.dirName);
        holder.nodeID.setText(dirNode.id);
        if (node.isLeaf())
            holder.ivArrow.setVisibility(View.INVISIBLE);
        else holder.ivArrow.setVisibility(View.VISIBLE);

        if (dirNode.location.equals("insideParent")){
            holder.parentnodeImage.setLayoutParams(childIconLayout);

        }

    }

    @Override
    public int getLayoutId() {
        return R.layout.parent_dir;
    }

    public static class ParentViewHolder extends TreeViewBinder.ViewHolder {
        private ImageView ivArrow;
        private TextView tvName;
        private RelativeLayout linearLayout ;
        private TextView nodeID ;
        private CardView parentCard ;
        private ImageView parentnodeImage ;

        public ParentViewHolder(View rootView) {
            super(rootView);
            this.ivArrow = (ImageView) rootView.findViewById(R.id.nodeNext);
            this.tvName = (TextView) rootView.findViewById(R.id.nodetitle);
            this.nodeID = (TextView) rootView.findViewById(R.id.nodeinherit);
            this.linearLayout = (RelativeLayout) rootView.findViewById(R.id.nodelin);
            this.parentCard = (CardView) rootView.findViewById(R.id.parentCard);
            this.parentnodeImage = (ImageView) rootView.findViewById(R.id.parentnodeImage);
        }

        public ImageView getIvArrow() {
            return ivArrow;
        }

        public RelativeLayout getLinearLayout() {
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
