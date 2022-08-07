package intalio.cts.mobile.android.viewer.support;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cts.mobile.android.R;

import java.util.Vector;

import intalio.cts.mobile.android.data.model.AttachmentModel;
import intalio.cts.mobile.android.data.network.response.AttachmentsResponseItem;
import intalio.cts.mobile.android.viewer.Utility;
import intalio.cts.mobile.android.viewer.stickerView.StickerView;
import intalio.cts.mobile.android.viewer.viewer_menu.MenuAdapter;
import intalio.cts.mobile.android.viewer.viewer_menu.MenuItem;

/**
 * Created by aem on 9/8/2017.
 */

public class PDFConfig {

    public static final int ADD_NOTE_TO_VIEWER = 11;

    public static final float ANNOTATION_WIDTH = 300;
    public static final float ANNOTATION_HEIGHT = 200;

    public static final float ANNOTATION_MIN_SIZE = ANNOTATION_WIDTH / 3;
    public static AttachmentModel attachment;

    private static int _pageWidth;
    private static int _pageHeight;
    private static RecyclerView pdfRecyclerView;
    private static AnnotationType drawType;
    private static boolean isAllPages;
    public static int annotationCounter = 0;
    public static boolean isOffline;

    public static Vector<MenuItem> currentMenuItems;
    public static MenuAdapter menuAdapter;

    public static Drawable imageToDraw;
    public static String imageToDrawName;
    public static String base64ToDraw;
    public static String noteText;
    public static int signatureTemplateId;

    public static StickerView stickerView;
    private static boolean editable = false;


    public enum AnnotationType {
        stamps,
        highlight,
        text,
        line,
        ellipse,
        rectangle,
        blackout,
        signature,
        AUTOMATIC_SIGNATURE,
        MANUAL_SIGNATURE,
        barcode,
        handwriting,
        sticky_note,

        INITIAL,
        Back,
        PRINT,
        PRINT_WITH_ANNOTATIONS,
        DOWNLOAD,
        DOWNLOAD_WITH_ANNOTATIONS,
        PREPARE_FOR_SIGNATURE,
        MULTIPLE_PREPARE_FOR_SIGNATURE,
        RESTORE_DOCUMENT,
        AUDIT_ACTIONS,
        NOTE_TEMPLATE,
        SIGNATURE_TEMPLATE,

        UNSIGN,
        CHECKIN,
        CHECKOUT,
        DISCARD_CHECKOUT
    }


    public static void setAllPagesAnnotation(boolean all) {
        isAllPages = all;
    }

    public static boolean isAllPagesAnnotation() {
        return isAllPages;
    }

    public static void setPageWidth(int pageWidth) {
        _pageWidth = pageWidth;
    }

    public static void setPageHeight(int pageHeight) {
        _pageHeight = pageHeight;
    }

    public static int getPageWidth() {
        return _pageWidth;
    }

    public static int getPageHeight() {
        return _pageHeight;
    }

    public static AnnotationType generateAnnotationType(String type) {
        AnnotationType _type = AnnotationType.valueOf(type);
        return AnnotationType.values()[_type.ordinal()];
    }

    public static String setAnnotationType(AnnotationType type) {
        return AnnotationType.values()[(type.ordinal())].name();
    }

    public static RecyclerView getPdfRecyclerView() {
        return pdfRecyclerView;
    }


    //Menu Configuration
    public static Vector<MenuItem> initMenu(Context context, AttachmentModel attachment , String state , String language) {
//        editable = !State.selectedCorrespondence.getIsViewMode();
        if (state.equals("node"))
            editable = true;

        MenuItem fileMenu = null;
        MenuItem editMenu = null;
        MenuItem signatureMenu;

        //Add Permissions
        Vector<MenuItem> menuItems = new Vector<>();

        //FileButtonItem

        fileMenu = new MenuItem(ContextCompat.getDrawable(context, R.drawable.viewer_file_icon), Utility.getStringFromXmlById(context, R.string.file));
        menuItems.add(fileMenu);

// && attachment.isCheckout()
        if (editable) {
            //EditButtonItem
            editMenu = new MenuItem(ContextCompat.getDrawable(context, R.drawable.edit_icon), Utility.getStringFromXmlById(context, R.string.edit));
            menuItems.add(editMenu);

            //SignatureButtonItem
            signatureMenu = new MenuItem(ContextCompat.getDrawable(context, R.drawable.signature_icon), Utility.getStringFromXmlById(context, R.string.menu_signature));
            menuItems.add(signatureMenu);

            MenuItem autoSignItem = new MenuItem(ContextCompat.getDrawable(context, R.drawable.signature_icon), AnnotationType.SIGNATURE_TEMPLATE, Utility.getStringFromXmlById(context, R.string.sign));
            signatureMenu.addItem(autoSignItem);

            MenuItem autoSignAllItem = new MenuItem(ContextCompat.getDrawable(context, R.drawable.signature_icon), AnnotationType.SIGNATURE_TEMPLATE, Utility.getStringFromXmlById(context, R.string.signall));
            autoSignAllItem.setAllPage(true);
            signatureMenu.addItem(autoSignAllItem);

            MenuItem handSignItem = new MenuItem(ContextCompat.getDrawable(context, R.drawable.signature_icon), AnnotationType.MANUAL_SIGNATURE, Utility.getStringFromXmlById(context, R.string.handsign));
            signatureMenu.addItem(handSignItem);

            MenuItem handSignAllItem = new MenuItem(ContextCompat.getDrawable(context, R.drawable.signature_icon), AnnotationType.MANUAL_SIGNATURE, Utility.getStringFromXmlById(context, R.string.handsignall));
            handSignAllItem.setAllPage(true);
            signatureMenu.addItem(handSignAllItem);

//                MenuItem unsignItem = new MenuItem(ContextCompat.getDrawable(context, R.drawable.unsign_icon), AnnotationType.UNSIGN, Utility.getStringFromXmlById(context, R.string.unsign));
//                signatureMenu.addItem(unsignItem);

        }


        if (fileMenu != null) {
            // Check in/out menu
            if (attachment.isCheckout()) {
                fileMenu.addItem(new MenuItem(ContextCompat.getDrawable(context, R.drawable.ic_checkin), AnnotationType.CHECKIN, Utility.getStringFromXmlById(context, R.string.checkin)));
                fileMenu.addItem(new MenuItem(ContextCompat.getDrawable(context, R.drawable.ic_uncheckout), AnnotationType.DISCARD_CHECKOUT, Utility.getStringFromXmlById(context, R.string.discard_checkout)));
            } else if (editable)
                fileMenu.addItem(new MenuItem(ContextCompat.getDrawable(context, R.drawable.ic_checkout), AnnotationType.CHECKOUT, Utility.getStringFromXmlById(context, R.string.checkout)));


            MenuItem downloadMenu = new MenuItem(ContextCompat.getDrawable(context, R.drawable.ic_download), Utility.getStringFromXmlById(context, R.string.download));
            fileMenu.addItem(downloadMenu);
            MenuItem downloadItem = new MenuItem(ContextCompat.getDrawable(context, R.drawable.ic_download), AnnotationType.DOWNLOAD, Utility.getStringFromXmlById(context, R.string.download));
            downloadMenu.addItem(downloadItem);
            MenuItem downloadWithAnnotations = new MenuItem(ContextCompat.getDrawable(context, R.drawable.ic_download), AnnotationType.DOWNLOAD_WITH_ANNOTATIONS, Utility.getStringFromXmlById(context, R.string.download_with_annotations));
            downloadMenu.addItem(downloadWithAnnotations);

            MenuItem printMenu = new MenuItem(ContextCompat.getDrawable(context, R.drawable.print_icon), Utility.getStringFromXmlById(context, R.string.print));
            fileMenu.addItem(printMenu);
            MenuItem printItem = new MenuItem(ContextCompat.getDrawable(context, R.drawable.print_icon), AnnotationType.PRINT, Utility.getStringFromXmlById(context, R.string.print));
            printMenu.addItem(printItem);
            MenuItem printWithAnnotations = new MenuItem(ContextCompat.getDrawable(context, R.drawable.print_icon), AnnotationType.PRINT_WITH_ANNOTATIONS, Utility.getStringFromXmlById(context, R.string.print_with_annotations));
            printMenu.addItem(printWithAnnotations);


//            if (attachment.getStatus() == AttachmentStatus.ISDOCX.ordinal()) {
//                signatureMenu = new MenuItem(ContextCompat.getDrawable(context, R.drawable.signature_icon), Utility.getStringFromXmlById(context, R.string.menu_signature));
//                menuItems.add(signatureMenu);
//
//                //PrepareForSignatureItem/
//                MenuItem prepareSignItem = new MenuItem(ContextCompat.getDrawable(context, R.drawable.prepare_sign_icon), AnnotationType.PREPARE_FOR_SIGNATURE, Utility.getStringFromXmlById(context, R.string.prepareforsignature));
//                signatureMenu.addItem(prepareSignItem);
//
//                //PrepareForMultipleSignatureItem
//                MenuItem multiPrepareSignItem = new MenuItem(ContextCompat.getDrawable(context, R.drawable.prepare_sign_icon), AnnotationType.MULTIPLE_PREPARE_FOR_SIGNATURE, Utility.getStringFromXmlById(context, R.string.preparemultipleforsignature));
//                signatureMenu.addItem(multiPrepareSignItem);
//            }

//            if (attachment.canBeRestored()) {
//                MenuItem restoreItem = new MenuItem(ContextCompat.getDrawable(context, R.drawable.file_restore_icon), AnnotationType.RESTORE_DOCUMENT, Utility.getStringFromXmlById(context, R.string.restore));
//                fileMenu.addItem(restoreItem);
//            }

//            if(!isLocked) {
////                //ActionsLogButtonItem
//                MenuItem actionsLogItem = new MenuItem(ContextCompat.getDrawable(context, R.drawable.gear_icon), AnnotationType.AUDIT_ACTIONS, Utility.getStringFromXmlById(context, R.string.ActionsLog));
//                fileMenu.addItem(actionsLogItem);
//            }
        }


        if (editMenu != null) {
            //HighlightButtonItem
            MenuItem highlightAnnotation = new MenuItem(ContextCompat.getDrawable(context, R.drawable.highlighter_icon), AnnotationType.highlight, Utility.getStringFromXmlById(context, R.string.highlight));
            editMenu.addItem(highlightAnnotation);

            MenuItem rectangleAnnotation = new MenuItem(ContextCompat.getDrawable(context, R.drawable.rectangle_icon), AnnotationType.rectangle, Utility.getStringFromXmlById(context, R.string.rectangle));
            editMenu.addItem(rectangleAnnotation);

            MenuItem ellipseAnnotation = new MenuItem(ContextCompat.getDrawable(context, R.drawable.ellipse_icon), AnnotationType.ellipse, Utility.getStringFromXmlById(context, R.string.ellipse));
            editMenu.addItem(ellipseAnnotation);

            MenuItem lineAnnotation = new MenuItem(ContextCompat.getDrawable(context, R.drawable.line_icon), AnnotationType.line, Utility.getStringFromXmlById(context, R.string.line));
            editMenu.addItem(lineAnnotation);

            MenuItem blackoutAnnotation = new MenuItem(ContextCompat.getDrawable(context, R.drawable.blackout_icon), AnnotationType.blackout, Utility.getStringFromXmlById(context, R.string.blackout));
            editMenu.addItem(blackoutAnnotation);

            //NoteButtonItem
            MenuItem noteAnnotation = new MenuItem(ContextCompat.getDrawable(context, R.drawable.note_icon), AnnotationType.text, Utility.getStringFromXmlById(context, R.string.menu_note));
            editMenu.addItem(noteAnnotation);

            //NoteTemplateItem
            MenuItem noteTemplateMenu = new MenuItem(ContextCompat.getDrawable(context, R.drawable.note_template_icon), AnnotationType.NOTE_TEMPLATE, Utility.getStringFromXmlById(context, R.string.notetemplate));
            editMenu.addItem(noteTemplateMenu);

            //HandwriteButtonItem
            MenuItem handWriteAnnotation = new MenuItem(ContextCompat.getDrawable(context, R.drawable.handwrite_note), AnnotationType.handwriting, Utility.getStringFromXmlById(context, R.string.handwrite));
            editMenu.addItem(handWriteAnnotation);

            //StampButtonItem
            MenuItem stampButtonItem = new MenuItem(ContextCompat.getDrawable(context, R.drawable.annotation_icon), Utility.getStringFromXmlById(context, R.string.stamp));
            editMenu.addItem(stampButtonItem);

            //Approved Stamp
            Drawable approvedDrawable = null;
            MenuItem approvedAnnotation = new MenuItem(approvedDrawable, AnnotationType.stamps, Utility.getStringFromXmlById(context, R.string.approved));
            if (language.equals("ar")) {
                approvedDrawable = ContextCompat.getDrawable(context, R.drawable.approved_tab_ar);
                approvedDrawable.setColorFilter(ContextCompat.getColor(context, android.R.color.white), PorterDuff.Mode.SRC_ATOP);
                approvedAnnotation.setMenuImage(approvedDrawable);
            } else {
                approvedDrawable = ContextCompat.getDrawable(context, R.drawable.approved_tab);
                approvedDrawable.setColorFilter(ContextCompat.getColor(context, android.R.color.white), PorterDuff.Mode.SRC_ATOP);
                approvedAnnotation.setMenuImage(approvedDrawable);
            }
            approvedAnnotation.imageName = "/" + Utility.getViewerAppName(context) + context.getString(R.string.viewer_stamps_image_prefix) + "Approved.png";
            stampButtonItem.addItem(approvedAnnotation);

            //Confidiential Stamp
            Drawable confidentialDrawable = null;
            MenuItem confidentialAnnotation = new MenuItem(confidentialDrawable, AnnotationType.stamps, Utility.getStringFromXmlById(context, R.string.confidential));
            if (language.equals("ar")) {
                confidentialDrawable = ContextCompat.getDrawable(context, R.drawable.confidential_tab_ar);
                confidentialDrawable.setColorFilter(ContextCompat.getColor(context, android.R.color.white), PorterDuff.Mode.SRC_ATOP);
                confidentialAnnotation.setMenuImage(confidentialDrawable);
            } else {
                confidentialDrawable = ContextCompat.getDrawable(context, R.drawable.confidential_tab);
                confidentialDrawable.setColorFilter(ContextCompat.getColor(context, android.R.color.white), PorterDuff.Mode.SRC_ATOP);
                confidentialAnnotation.setMenuImage(confidentialDrawable);
            }
            confidentialAnnotation.imageName = "/" + Utility.getViewerAppName(context) + context.getString(R.string.viewer_stamps_image_prefix) + "Confidential.png";
            stampButtonItem.addItem(confidentialAnnotation);

            //finalVersion Stamp
            Drawable fsDrawable = null;
            MenuItem fsAnnotation = new MenuItem(fsDrawable, AnnotationType.stamps, Utility.getStringFromXmlById(context, R.string.menu_final));
            if (language.equals("ar")) {
                fsDrawable = ContextCompat.getDrawable(context, R.drawable.finalversion_tab_ar);
                fsDrawable.setColorFilter(ContextCompat.getColor(context, android.R.color.white), PorterDuff.Mode.SRC_ATOP);
                fsAnnotation.setMenuImage(fsDrawable);
            } else {
                fsDrawable = ContextCompat.getDrawable(context, R.drawable.finalversion_tab);
                fsDrawable.setColorFilter(ContextCompat.getColor(context, android.R.color.white), PorterDuff.Mode.SRC_ATOP);
                fsAnnotation.setMenuImage(fsDrawable);
            }
            fsAnnotation.imageName = "/" + Utility.getViewerAppName(context) + context.getString(R.string.viewer_stamps_image_prefix) + "FinalVersion.png";
            stampButtonItem.addItem(fsAnnotation);

            //Reviewed Stamp
            Drawable revDrawable = null;
            MenuItem revAnnotation = new MenuItem(revDrawable, AnnotationType.stamps, Utility.getStringFromXmlById(context, R.string.reviewed));
            if (language.equals("ar")) {
                revDrawable = ContextCompat.getDrawable(context, R.drawable.reviewed_tab_ar);
                revDrawable.setColorFilter(ContextCompat.getColor(context, android.R.color.white), PorterDuff.Mode.SRC_ATOP);
                revAnnotation.setMenuImage(revDrawable);
            } else {
                revDrawable = ContextCompat.getDrawable(context, R.drawable.reviewed_tab);
                revDrawable.setColorFilter(ContextCompat.getColor(context, android.R.color.white), PorterDuff.Mode.SRC_ATOP);
                revAnnotation.setMenuImage(revDrawable);
            }
            revAnnotation.imageName = "/" + Utility.getViewerAppName(context) + context.getString(R.string.viewer_stamps_image_prefix) + "Reviewed.png";
            stampButtonItem.addItem(revAnnotation);

            //Revised Stamp
            Drawable revisDrawable = null;
            MenuItem revisAnnotation = new MenuItem(revisDrawable, AnnotationType.stamps, Utility.getStringFromXmlById(context, R.string.revised));
            if (language.equals("ar")) {
                revisDrawable = ContextCompat.getDrawable(context, R.drawable.revised_tab_ar);
                revisDrawable.setColorFilter(ContextCompat.getColor(context, android.R.color.white), PorterDuff.Mode.SRC_ATOP);
                revisAnnotation.setMenuImage(revisDrawable);
            } else {
                revisDrawable = ContextCompat.getDrawable(context, R.drawable.revised_tab);
                revisDrawable.setColorFilter(ContextCompat.getColor(context, android.R.color.white), PorterDuff.Mode.SRC_ATOP);
                revisAnnotation.setMenuImage(revisDrawable);
            }
            revisAnnotation.imageName = "/" + Utility.getViewerAppName(context) + context.getString(R.string.viewer_stamps_image_prefix) + "Revised.png";
            stampButtonItem.addItem(revisAnnotation);

            //draft Stamp
            Drawable draftDrawable = null;
            MenuItem draftAnnotation = new MenuItem(draftDrawable, AnnotationType.stamps, Utility.getStringFromXmlById(context, R.string.draft));
            if (language.equals("ar")) {
                draftDrawable = ContextCompat.getDrawable(context, R.drawable.draft_tab_ar);
                draftDrawable.setColorFilter(ContextCompat.getColor(context, android.R.color.white), PorterDuff.Mode.SRC_ATOP);
                draftAnnotation.setMenuImage(draftDrawable);
            } else {
                draftDrawable = ContextCompat.getDrawable(context, R.drawable.draft_tab);
                draftDrawable.setColorFilter(ContextCompat.getColor(context, android.R.color.white), PorterDuff.Mode.SRC_ATOP);
                draftAnnotation.setMenuImage(draftDrawable);
            }
            draftAnnotation.imageName = "/" + Utility.getViewerAppName(context) + context.getString(R.string.viewer_stamps_image_prefix) + "Draft.png";
            stampButtonItem.addItem(draftAnnotation);
        }

        return menuItems;
    }

    public static void resetMenuItems() {
        for (MenuItem menuItem : currentMenuItems) {
            menuItem.setSelected(false);
        }
        PDFConfig.menuAdapter.notifyDataSetChanged();
    }

    public static void setDrawType(AnnotationType type) {
        drawType = type;
    }

    public static void resetDrawType() {
        drawType = null;
    }

    public static AnnotationType getDrawType() {
        return drawType;
    }

}