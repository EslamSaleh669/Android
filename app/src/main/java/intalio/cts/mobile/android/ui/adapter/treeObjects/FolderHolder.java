package intalio.cts.mobile.android.ui.adapter.treeObjects;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cts.mobile.android.R;
import com.unnamed.b.atv.model.TreeNode;

/**
 * Created by aem on 9/4/2016.
 */
public class FolderHolder extends TreeNode.BaseNodeViewHolder<FolderHolder.IconTreeItemm> {

    private Context context;

    public FolderHolder(Context context) {
        super(context);
        this.context = context;
    }




    @Override
    public View createNodeView(TreeNode node, IconTreeItemm value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.layout_folder_node,null,false);
        TextView marginText = (TextView) view.findViewById(R.id.matgintxt);
        TextView folderMarginText = (TextView) view.findViewById(R.id.foldermartxt);
        TextView tvValue = (TextView) view.findViewById(R.id.node_value);
        ImageView imageView = (ImageView) view.findViewById(R.id.icon);
        RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.folderrel);


        if (!value.hasParent){

           marginText.setVisibility(View.GONE);
        }else {
            marginText.setVisibility(View.VISIBLE);

        }


        if (value.hasFolderParent){
            folderMarginText.setVisibility(View.VISIBLE);
        }else {
            folderMarginText.setVisibility(View.GONE);

        }

        tvValue.setText(value.text);
        imageView.setImageResource(value.icon);


        return view;
    }

    public static class IconTreeItemm {
        public int icon;
        public String text;
        public boolean hasParent ;
        public boolean hasFolderParent ;

    }
}
