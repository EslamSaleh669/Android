package intalio.cts.mobile.android.ui.adapter.treeObjects;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cts.mobile.android.R;
import com.unnamed.b.atv.model.TreeNode;

/**
 * Created by aem on 9/4/2016.
 */
public class FileHolder extends TreeNode.BaseNodeViewHolder<FileHolder.IconTreeItem> {

    private Context context;

    public FileHolder(Context context) {
        super(context);
        this.context = context;
    }


    @Override
    public View createNodeView(TreeNode node, IconTreeItem value) {
         final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.layout_profile_node,null,false);
        TextView tvValue = (TextView) view.findViewById(R.id.node_value);
        ImageView imageView = (ImageView) view.findViewById(R.id.icon);
        RelativeLayout relativeLayout = view.findViewById(R.id.filerel);


        imageView.setImageResource(value.icon);
        tvValue.setText(value.text);


        return view;
    }

    public static class IconTreeItem {
        public int icon;
        public String text;
    }
}
