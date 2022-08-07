package intalio.cts.mobile.android.viewer.viewer_menu;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.cts.mobile.android.R;

import org.jetbrains.annotations.NotNull;

import java.util.Vector;

import intalio.cts.mobile.android.ui.EditSignatureActivity;
import intalio.cts.mobile.android.ui.fragment.correspondencedetails.CorrespondenceDetailsFragment;
import intalio.cts.mobile.android.util.Constants;
import intalio.cts.mobile.android.viewer.Utility;
import intalio.cts.mobile.android.viewer.interfaces.MenuInterface;
import intalio.cts.mobile.android.viewer.support.PDFConfig;

/**
 * Created by aem on 9/15/2017.
 */

public class MenuAdapter extends RecyclerView.Adapter<MenuItemViewHolder> implements MenuInterface {

    public Vector<MenuItem> menuItems;
    private final Context _context;
    CorrespondenceDetailsFragment _correspondenceDetailsFragment;

    public MenuAdapter(Context context, Vector<MenuItem> menuItems, CorrespondenceDetailsFragment correspondenceDetailsFragment) {
        PDFConfig.currentMenuItems = menuItems;
        this.menuItems = menuItems;
        this._context = context;
        this._correspondenceDetailsFragment = correspondenceDetailsFragment;

    }

    @Override
    public MenuItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item_layout, parent, false);
        return new MenuItemViewHolder(_context, itemView);
    }

    @Override
    public void onBindViewHolder(MenuItemViewHolder holder, int position) {
        MenuItem menuItem = menuItems.get(position);
        //  Typeface font = Utility.getCustomFont(_context, Constants.MAIN_FONT_APP);

        holder.ivMenuImage.setImageDrawable(menuItem.getMenuImage());
        holder.tvMenuItem.setText(menuItem.getMenuText());
        //  holder.tvMenuItem.setTypeface(font);
        holder.position = holder.getAdapterPosition();
        holder.menuInterface = this;
        if (menuItem.isSelected()) {
            holder.rootView.setBackgroundColor(ContextCompat.getColor(_context, android.R.color.white));
            holder.tvMenuItem.setTextColor(ContextCompat.getColor(_context, R.color.colorPrimary));
            Drawable drawable = holder.ivMenuImage.getDrawable();
            drawable.setColorFilter(ContextCompat.getColor(_context, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        } else {
            holder.rootView.setBackgroundColor(ContextCompat.getColor(_context, R.color.colorPrimary));
            holder.tvMenuItem.setTextColor(ContextCompat.getColor(_context, android.R.color.white));
            Drawable drawable = holder.ivMenuImage.getDrawable();
            drawable.setColorFilter(ContextCompat.getColor(_context, android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        }

        if (menuItem.isDisabled()) {
            holder.ivMenuImage.setAlpha(0.5f);
            holder.tvMenuItem.setAlpha(0.5f);
        } else {
            holder.ivMenuImage.setAlpha(1f);
            holder.tvMenuItem.setAlpha(1f);
        }
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    @Override
    public void menuItemClicked(int position) {
        try {
            MenuItem menuItem = menuItems.get(position);
//            ViewerActivity viewerActivity = (ViewerActivity) _context;
            CorrespondenceDetailsFragment correspondenceDetailsFragment = new CorrespondenceDetailsFragment();

            if (!menuItem.isDisabled()) {
                PDFConfig.setAllPagesAnnotation(menuItem.isAllPage());
                if (menuItem.getMenuItems() != null) {
                    if (menuItem.getMenuItems().get(0).getType() != PDFConfig.AnnotationType.Back) {
                        Drawable backDrawable = null;
                        String language = Utility.getLocalData(_context, Constants.LANG_KEY);

                        if (language.equals("ar")) {
                            backDrawable = ContextCompat.getDrawable(_context, R.drawable.back_ar);
                        } else {
                            backDrawable = ContextCompat.getDrawable(_context, R.drawable.back_en);
                        }

                        MenuItem backItem = new MenuItem(backDrawable, PDFConfig.AnnotationType.Back, Utility.getStringFromXmlById(_context, R.string.back));
                        menuItem.getMenuItems().add(0, backItem);
                        backItem.setBackItems(menuItems);
                    }
                    menuItems = menuItem.getMenuItems();
                    PDFConfig.currentMenuItems = menuItems;
                    this.notifyDataSetChanged();
                }
                if (menuItem.getType() != null) {
                    switch (menuItem.getType()) {
                        case Back:
                            PDFConfig.currentMenuItems = menuItem.getBackItems();
                            menuItems = menuItem.getBackItems();
                            PDFConfig.resetDrawType();
                            this.notifyDataSetChanged();
                            break;
                        case stamps:
                            PDFConfig.imageToDraw = menuItem.getImage();
                            PDFConfig.imageToDrawName = menuItem.imageName;
                        case handwriting:
                        case AUTOMATIC_SIGNATURE:
                        case MANUAL_SIGNATURE:
                        case INITIAL:
                        case text:
                        case highlight:
                        case line:
                        case rectangle:
                        case ellipse:
                        case blackout:
                            if (menuItem.getType() == PDFConfig.AnnotationType.handwriting) {
                                Log.d("iamhere", "handwriting");

                                Intent intent = new Intent(_context, EditSignatureActivity.class);
                                intent.putExtra("save_action", "HAND_SIGN");
                                intent.putExtra("title", R.string.note);
                                intent.putExtra("btnString", R.string.addNote);
                                intent.putExtra("validateMessage", R.string.addNoteWarning);
                                // correspondenceDetailsFragment.handSign(intent);
                                // correspondenceDetailsFragment.requireActivity().startActivityForResult(intent, Constants.HAND_NOTE_SIGN_RESULT);
                            } else if (menuItem.getType() == PDFConfig.AnnotationType.MANUAL_SIGNATURE) {
                                Log.d("iamhere", "MANUAL_SIGNATURE");

                                Intent intent = new Intent(_context, EditSignatureActivity.class);
                                intent.putExtra("save_action", "HAND_TEXT");
                                intent.putExtra("title", R.string.signature);
                                intent.putExtra("btnString", R.string.save);
                                intent.putExtra("validateMessage", R.string.addSignatureWarning);
                                //    correspondenceDetailsFragment.handSign();

                                _correspondenceDetailsFragment.getActivity().startActivityForResult(intent, Constants.HAND_NOTE_SIGN_RESULT);
                            }

                            if (!menuItem.isSelected()) {
                                menuItem.setSelected(true);
                                PDFConfig.setDrawType(menuItem.getType());
                            } else {
                                menuItem.setSelected(false);
                                PDFConfig.resetDrawType();
                            }
                            for (MenuItem item : menuItems) {
                                if (!item.equals(menuItem)) {
                                    if (item.isSelected()) {
                                        item.setSelected(false);
                                        this.notifyItemChanged(menuItems.indexOf(item));
                                        break;
                                    }
                                }
                            }
                            this.notifyItemChanged(position);
                            break;
//                        case PRINT:
//                            viewerActivity.downloadFile(false, true);
//                            break;
//                        case PRINT_WITH_ANNOTATIONS:
//                            viewerActivity.downloadFile(true, true);
//                            break;
//                        case DOWNLOAD:
//                            viewerActivity.downloadFile(false, false);
//                            break;
//                        case DOWNLOAD_WITH_ANNOTATIONS:
//                            viewerActivity.downloadFile(true, false);
//                            break;
//                        case AUDIT_ACTIONS:
//                            break;
//                        case NOTE_TEMPLATE:
//                            viewerActivity.openAnnotationTemplate();
//                            break;
                        case SIGNATURE_TEMPLATE:
                            _correspondenceDetailsFragment.openSignatureTemplate();
                            break;
//                        case UNSIGN:
//                            Utility.showMainPromptDialog(viewerActivity, R.string.unsign, R.string.unsignDocumentMessage, R.string.yes, R.string.no, Constants.UNSIGN_DOCUMENT_RESULT, -1);
//                            break;
//                        case MULTIPLE_PREPARE_FOR_SIGNATURE:
//                            viewerActivity.startGenerateActivity();
//                            break;
//                        case RESTORE_DOCUMENT:
//                            Utility.showMainPromptDialog(viewerActivity, R.string.restoreDocument, R.string.restoreDocumentMessage, R.string.yes, R.string.no, Constants.RESTORE_DOCUMENT_RESULT, -1);
//                            break;
//                        case CHECKIN:
//                            ((ViewerActivity) _context).doCheckIn();
//                            break;
//                        case CHECKOUT:
//                            ((ViewerActivity) _context).doCheckout();
//                            break;
//                        case DISCARD_CHECKOUT:
//                            ((ViewerActivity) _context).doDiscardCheckout();
//                            break;
                        default:
                            break;
                    }

                }
            }
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), e.getMessage());
        }

    }

    public void unselectItems() {
        for (MenuItem menuItem : this.menuItems) {
            menuItem.setSelected(false);
        }
        this.notifyDataSetChanged();
    }

}
