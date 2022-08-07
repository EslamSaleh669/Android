package intalio.cts.mobile.android.viewer.viewer;

import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.cts.mobile.android.R;

import intalio.cts.mobile.android.viewer.views.AnnotationsView;

/**
 * Created by aem on 9/18/2017.
 */

public class ViewerPageHolder extends RecyclerView.ViewHolder {

    public ImageView ivPage;
    public AnnotationsView annotationsView;

    public ViewerPageHolder(View itemView) {
        super(itemView);
        ivPage = itemView.findViewById(R.id.ivPage);
        annotationsView = itemView.findViewById(R.id.svAnnotations);
    }
}
