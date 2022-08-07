package intalio.cts.mobile.android.viewer.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.cts.mobile.android.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;

import intalio.cts.mobile.android.data.model.viewer.SignatureTemplateModel;
import intalio.cts.mobile.android.data.model.viewer.TokenManager;
import intalio.cts.mobile.android.viewer.Utility;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class SignatureTemplateListAdapter extends RecyclerView.Adapter<SignatureTemplateListAdapter.SignatureTemplateHolder> {

    public ArrayList<SignatureTemplateModel> templates;
    private final boolean settings;
    private int editPosition = -1;
    public final int ADD_ITEM = -2;
    public final int EDIT_ITEM = -1;

    public SignatureTemplateListAdapter(ArrayList<SignatureTemplateModel> objects, boolean settings) {
        this.settings = settings;
        this.templates = new ArrayList<>(objects);
    }


    @Override
    public int getItemViewType(int position) {
        if (position == editPosition) {
            return EDIT_ITEM;
        } else {
            return 1;
        }
    }

    @NonNull
    @Override
    public SignatureTemplateHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == EDIT_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.signature_template_edit_list_item, parent, false);
            return new EditSignatureTemplateHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.signature_template_list_item, parent, false);
            return new ViewSignatureTemplateHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull SignatureTemplateHolder holder, int position) {
        SignatureTemplateModel template = this.templates.get(position);

         String url = Utility.getViewerURL(holder.itemView.getContext()) + holder.itemView.getContext().getString(R.string.viewer_signature_image_path, template.getId(),
            "eyJhbGciOiJSUzI1NiIsImtpZCI6IjRBMUI0MkQ5OTdGNDEzNDdGNzhBNEQ0MDEwQTYzMTQyIiwidHlwIjoiYXQrand0In0.eyJuYmYiOjE2NTQ0MzA5ODIsImV4cCI6MTY4NTk4ODU4MiwiaXNzIjoiaHR0cHM6Ly9pYW1wLmludGFsaW8uY29tIiwiYXVkIjpbIklkZW50aXR5U2VydmVyQXBpIiwib2ZmbGluZV9hY2Nlc3MiXSwiY2xpZW50X2lkIjoiNWQyYzhmYTUtOWY1OC00MzBjLWJjZjItNWY0MzY2ZDQyNWRjIiwic3ViIjoiMzAwMDA2IiwiYXV0aF90aW1lIjoxNjU0NDMwOTgyLCJpZHAiOiJsb2NhbCIsIkRpc3BsYXlOYW1lIjoiRmFkZWwgU2FobWFyYW5pIiwiTG9naW5Qcm92aWRlclR5cGUiOjEsIkVtYWlsIjoiZmFkaS5hbW1vdXJ5QGludGFsaW8uY29tcyIsIklkIjozMDAwMDYsIkZpcnN0TmFtZSI6IkZhZGVsIiwiTGFzdE5hbWUiOiJTYWhtYXJhbmkiLCJNaWRkbGVOYW1lIjoiIiwiU3RydWN0dXJlSWQiOiI2NjY1IiwiTWFuYWdlcklkIjoiMzAwMDA4IiwiU3RydWN0dXJlSWRzIjoiNjY2NSIsIkdyb3VwSWRzIjoiIiwiU3RydWN0dXJlU2VuZGVyIjoidHJ1ZSIsIlN0cnVjdHVyZVJlY2VpdmVyIjoidHJ1ZSIsIlByaXZhY3kiOiIzIiwiaHR0cDovL3NjaGVtYXMubWljcm9zb2Z0LmNvbS93cy8yMDA4LzA2L2lkZW50aXR5L2NsYWltcy9yb2xlIjoiQWRtaW5pc3RyYXRvciIsIkFwcGxpY2F0aW9uUm9sZUlkIjoiMSIsIkNsaWVudHMiOlsie1wiUm9sZUlkXCI6MSxcIlJvbGVcIjpcIkFkbWluaXN0cmF0b3JcIixcIkNsaWVudElkXCI6XCI1ZDJjOGZhNS05ZjU4LTQzMGMtYmNmMi01ZjQzNjZkNDI1ZGNcIn0iLCJ7XCJSb2xlSWRcIjoxLFwiUm9sZVwiOlwiQWRtaW5pc3RyYXRvclwiLFwiQ2xpZW50SWRcIjpcImIwMjc5NTExLTAzOGEtNGQzOS1iZTE2LTNjMWMzOWM5ZWRhZVwifSIsIntcIlJvbGVJZFwiOjEsXCJSb2xlXCI6XCJBZG1pbmlzdHJhdG9yXCIsXCJDbGllbnRJZFwiOlwiMjZkYjQwNjEtMzIxNi00NTFlLThkYmUtYWY1NjVhY2MwZGM2XCJ9Iiwie1wiUm9sZUlkXCI6MyxcIlJvbGVcIjpcIkNvbnRyaWJ1dGVcIixcIkNsaWVudElkXCI6XCI5NWM4ZDI3ZS0wNTFkLTQ5MDMtODEzNS1lMzlkNDYyM2RlOTdcIn0iXSwianRpIjoiQkM0NDI2QTY5NTgwMkVGMTE3QkM1OTY1NTkxMzVFQkYiLCJzaWQiOiIwNkQzQjUwQkEwOTUxRUM0MjEzQjNCOTVENzdGRTExQSIsImlhdCI6MTY1NDQzMDk4Miwic2NvcGUiOlsib3BlbmlkIiwiSWRlbnRpdHlTZXJ2ZXJBcGkiLCJvZmZsaW5lX2FjY2VzcyJdLCJhbXIiOlsicHdkIl19.CVNxWswWkMc4tHyLaHjfhFnSp3_DX0jA4eWK6-cPI64-Jp7ROs10kbDhVFUq8IB7T30bJNGXuO0hY3t8nZaPTPDz-c17Xj5FX8cT9freVQdrVtuMYHfUCs5lDU1eLfXOOdVYeyiw2_0dd2wYh0OxcXBJtqotzslwGYiepUQ2pAf4CM32y2RwWNfxnX-mg8mrElh6wftEOWZmuOXZyNRsCg8MJjYQLRYiWzPnyZf-zn4GTLLZzyGL8RkqPNu1AAWcuPrA0vETBeRks0xP1RQTkftZBtPoKn4MlvMI0_jv86ydvsNvboVtHavi5BL186PZSKV9iPmZxL2e1pgBGmAamw");

        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                holder.image.setImageBitmap(bitmap);

                if (settings)
                    return;
                holder.itemView.setOnClickListener(v1 -> {
                    Intent i = new Intent();
                    i.putExtra("id", template.getId());
                    i.putExtra("name", template.getName());
                    i.putExtra("image", url);
                    ((Activity) holder.itemView.getContext()).setResult(Activity.RESULT_OK, i);
                    ((Activity) holder.itemView.getContext()).finish();
                });
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        holder.image.setTag(target);
        Picasso.get().load(url).into(target);


        if (holder instanceof ViewSignatureTemplateHolder) {
//            holder.tvName.setTypeface(font);
//            holder.tvDescription.setTypeface(font);
            holder.tvName.setText(template.getName());
            holder.tvDescription.setText(template.getDescription());
//            holder.btnDelete.setOnClickListener(v1 -> {
//                new AlertDialog.Builder(holder.itemView.getContext())
//                    .setMessage(holder.itemView.getContext().getString(R.string.delete_signature_template, template.getName()))
//                    .setNegativeButton(R.string.cancel, null)
//                    .setPositiveButton(R.string.ok, (dialog, which) -> RetrofitManager.getViewerService().deleteSignatureTemplate(new DeleteSignatureTemplateModel(new ArrayList<>(Collections.singletonList(template.getId())))).enqueue(new retrofit2.Callback<ResponseBody>() {
//                        @Override
//                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                            if (response.code() != 200) {
//                                Utility.showMainAlertServer(holder.itemView.getContext(), R.string.warning, holder.itemView.getContext().getString(R.string.serverError), R.string.ok);
//                                return;
//                            }
//
//                            SignatureTemplateListAdapter.this.templates.remove(position);
//                            notifyDataSetChanged();
//                        }
//
//                        @Override
//                        public void onFailure(Call<ResponseBody> call, Throwable t) {
//                            Utility.showMainAlertServer(holder.itemView.getContext(), R.string.warning, holder.itemView.getContext().getString(R.string.serverError), R.string.ok);
//                        }
//                    }))
//                    .show();
//            });
            holder.btnEdit.findViewById(R.id.btnEditTemplate).setOnClickListener(v1 -> {
                editPosition = position;
                notifyItemChanged(position);
            });
            return;
        }

        holder.etName.setText(template.getName());
        holder.etDescription.setText(template.getDescription());
        holder.btnCancel.setOnClickListener(v1 -> {
            editPosition = -1;
            notifyItemChanged(position);
            Utility.hideKeyboard(holder.itemView.getContext(), holder.btnCancel);
        });

//        holder.btnSave.setOnClickListener(v -> {
//            String name = holder.etName.getText().toString();
//            String description = holder.etDescription.getText().toString();
//            Utility.hideKeyboard(holder.itemView.getContext(), holder.btnSave);
//            RetrofitManager.getViewerService().updateSignatureTemplate(String.valueOf(template.getId()), new UpdateSignatureTemplateModel(name, description)).enqueue(new retrofit2.Callback<ResponseBody>() {
//                @Override
//                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    if (response.code() != 200) {
//                        Utility.showMainAlertServer(holder.itemView.getContext(), R.string.warning, holder.itemView.getContext().getString(R.string.serverError), R.string.ok);
//                        return;
//                    }
//
//                    editPosition = -1;
//                    template.setName(name);
//                    template.setDescription(description);
//                    notifyItemChanged(position);
//                }
//
//                @Override
//                public void onFailure(Call<ResponseBody> call, Throwable t) {
//                    Utility.showMainAlertServer(holder.itemView.getContext(), R.string.warning, holder.itemView.getContext().getString(R.string.serverError), R.string.ok);
//                }
//            });
//        });
    }

    @Override
    public int getItemCount() {
        return this.templates.size();
    }

    static class SignatureTemplateHolder extends RecyclerView.ViewHolder {
        public EditText etName, etDescription;
        public ImageView image;
        public TextView tvName, tvDescription;
        public ImageButton btnSave, btnCancel, btnEdit, btnDelete, btnEditImage;

        public SignatureTemplateHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private class ViewSignatureTemplateHolder extends SignatureTemplateHolder {
        public ViewSignatureTemplateHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDescription = itemView.findViewById(R.id.tvContent);
            image = itemView.findViewById(R.id.signature_template_image);
            btnEdit = itemView.findViewById(R.id.btnEditTemplate);
            btnEditImage = itemView.findViewById(R.id.btn_edit_signature_template_image);
            btnDelete = itemView.findViewById(R.id.btnCancelEditTemplate);
            if (!settings) {
                btnEdit.setVisibility(View.GONE);
                btnDelete.setVisibility(View.GONE);
            }
        }
    }

    private static class EditSignatureTemplateHolder extends SignatureTemplateHolder {
        public EditSignatureTemplateHolder(View itemView) {
            super(itemView);
            etName = itemView.findViewById(R.id.etName);
            etDescription = itemView.findViewById(R.id.etContent);
            image = itemView.findViewById(R.id.signature_template_image);
            btnSave = itemView.findViewById(R.id.btnSaveTemplate);
            btnCancel = itemView.findViewById(R.id.btnCancelEditTemplate);
        }
    }
}
