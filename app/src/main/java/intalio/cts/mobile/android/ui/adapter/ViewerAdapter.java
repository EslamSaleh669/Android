package intalio.cts.mobile.android.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cts.mobile.android.R;
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import intalio.cts.mobile.android.data.model.viewer.ViewerAnnotationModel;
import intalio.cts.mobile.android.ui.fragment.correspondencedetails.CorrespondenceDetailsFragment;
import intalio.cts.mobile.android.viewer.viewer.ViewerPageHolder;

/**
 * Created by aem on 9/18/2017.
 */

public class ViewerAdapter extends RecyclerView.Adapter<ViewerPageHolder> implements SectionTitleProvider {

     private final ArrayList<String> images;
     private final ArrayList<ViewerAnnotationModel> _annotations = new ArrayList<>();
     private CorrespondenceDetailsFragment _correspondenceDetailsFragment ;

    public ViewerAdapter(List<ViewerAnnotationModel> annotations, boolean readOnly,CorrespondenceDetailsFragment correspondenceDetailsFragment) {
        this.images = new ArrayList<>();
       this._annotations.addAll(annotations);
       this._correspondenceDetailsFragment = correspondenceDetailsFragment ;

    }
     @Override
    public ViewerPageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewer_page_layout, parent, false);
        return new ViewerPageHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewerPageHolder holder, int position) {
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                DisplayMetrics displayMetrics = new DisplayMetrics();
                ((Activity) holder.itemView.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int scaledHeight = (int) (displayMetrics.widthPixels * (height / (width * 1.0)));
                bitmap = Bitmap.createScaledBitmap(bitmap, displayMetrics.widthPixels, scaledHeight, false);
                holder.ivPage.setImageBitmap(bitmap);

                holder.annotationsView.setPageIndex(position);
                holder.annotationsView.setAnnotations(generatePageAnnotations(position), _correspondenceDetailsFragment);
                new Handler().postDelayed(() -> holder.annotationsView.loadAnnotations(), 0);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                e.printStackTrace();
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        holder.ivPage.setTag(target);
        try {
            Picasso.get().load(images.get(position)).into(target);
            holder.annotationsView.setAnnotations(generatePageAnnotations(position),_correspondenceDetailsFragment);
            holder.annotationsView.setPageIndex(position);
        } catch (Exception e) {
            Log.e("kkkkkkkaaaaaaaaaaa", e.getMessage());
        }
    }

    @Override
    public void onViewRecycled(ViewerPageHolder holder) {
        super.onViewRecycled(holder);
        holder.annotationsView.removeAllViews();
        holder.ivPage.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.ic_men_advanceds));
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    @Override
    public String getSectionTitle(int position) {
        return String.valueOf(position + 1);
    }

    public void addImages(Context context, String documentId, String version, int size, String token) {
        for (int i = 0; i < size; i++) {
            String url = "https://dmsp.intalio.com/VIEWER/"
                + context.getString(R.string.viewer_page_image_path, documentId, version, String.valueOf(i + 1),
                    token)
                + "&_=" + new Date().getTime();

            this.images.add(url);

         }

        notifyDataSetChanged();
    }


    public ArrayList<ViewerAnnotationModel> generatePageAnnotations(int index) {
        ArrayList<ViewerAnnotationModel> pageAnnotations = new ArrayList<>();
        for (ViewerAnnotationModel annotation : _annotations) {
            if (annotation.pageNumber == index + 1)
                pageAnnotations.add(annotation);
        }
        return pageAnnotations;
    }

    public ArrayList<ViewerAnnotationModel> getAnnotations() {
        return this._annotations;
    }

    public List<String> getImages() {
        return this.images;
    }
}
