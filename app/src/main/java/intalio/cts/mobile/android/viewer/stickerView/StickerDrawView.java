package intalio.cts.mobile.android.viewer.stickerView;

import android.content.Context;
import android.view.View;

import intalio.cts.mobile.android.data.model.viewer.ViewerAnnotationModel;


/**
 * Created by aem on 10/16/2017.
 */

public class StickerDrawView extends StickerView {

    private AnnotationDrawView _view;

    public StickerDrawView(Context context, ViewerAnnotationModel annotation) {
        super(context, annotation);
    }

    @Override
    protected View getMainView() {
        _view = new AnnotationDrawView(getContext(),this.getAnnotation());
        return _view;
    }

    public void drawAnnotation(ViewerAnnotationModel annotation){
        _view.reDraw(annotation);
    }

}
