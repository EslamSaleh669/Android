package intalio.cts.mobile.android.viewer.viewer_menu;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.cts.mobile.android.R;

import intalio.cts.mobile.android.util.Constants;
import intalio.cts.mobile.android.viewer.Utility;
import intalio.cts.mobile.android.viewer.interfaces.MenuInterface;


/**
 * Created by aem on 9/15/2017.
 */

public class MenuItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public View rootView;
    public ImageView ivMenuImage;
    public TextView tvMenuItem;
    public int position;
    public MenuInterface menuInterface;
    private Context _context;

    public MenuItemViewHolder(Context _context, View itemView) {
        super(itemView);
        rootView = itemView;
        ivMenuImage = (ImageView) rootView.findViewById(R.id.ivMenuItem);
        tvMenuItem = (TextView) rootView.findViewById(R.id.tvMenuItem);
    //    tvMenuItem.setTypeface(Utility.getCustomFont(_context, Constants.MAIN_FONT_APP));
        rootView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        menuInterface.menuItemClicked(position);
    }
}
