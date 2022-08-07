package intalio.cts.mobile.android.viewer.views;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.cts.mobile.android.R;

import java.util.ArrayList;
import java.util.List;

import intalio.cts.mobile.android.data.model.viewer.SignatureTemplateModel;
import intalio.cts.mobile.android.util.Constants;
import intalio.cts.mobile.android.viewer.Utility;
import intalio.cts.mobile.android.viewer.adapters.SignatureTemplateListAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignatureTemplateDialog extends AppCompatActivity {
    public ArrayList<SignatureTemplateModel> signatureTemplates;
    RecyclerView signatureTemplateListView;
    SignatureTemplateListAdapter signatureTemplateListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_signature_templates_dialog);
        signatureTemplates = new ArrayList<>();
        getSignatureTemplates();
    }

    private void getSignatureTemplates() {
        SignatureTemplateModel signatureTemplateModel = new SignatureTemplateModel();
        SignatureTemplateModel signatureTemplateModel2 = new SignatureTemplateModel();
        SignatureTemplateModel signatureTemplateModel3 = new SignatureTemplateModel();

        signatureTemplateModel.setId(4);
        signatureTemplateModel.setName("CEO");
        signatureTemplateModel.setDescription("");
        signatureTemplateModel.setLastModified("1632085200000");
        signatureTemplateModel.setType("Image");
        signatureTemplateModel.setDateAdded("1632085200000");

        signatureTemplateModel2.setId(14);
        signatureTemplateModel2.setName("eslam");
        signatureTemplateModel2.setDescription("");
        signatureTemplateModel2.setLastModified("1653858000000");
        signatureTemplateModel2.setType("Image");
        signatureTemplateModel2.setDateAdded("1653858000000");

        signatureTemplateModel3.setId(15);
        signatureTemplateModel3.setName("private");
        signatureTemplateModel3.setDescription("private");
        signatureTemplateModel3.setLastModified("1654376400000");
        signatureTemplateModel3.setType("Image");
        signatureTemplateModel3.setDateAdded("1654376400000");

        signatureTemplates.add(signatureTemplateModel);
        signatureTemplates.add(signatureTemplateModel2);
        signatureTemplates.add(signatureTemplateModel3);
        signatureTemplateListView = findViewById(R.id.lvAddedTemplate);
        signatureTemplateListAdapter = new SignatureTemplateListAdapter(signatureTemplates, getIntent().getBooleanExtra("settings", false));
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        signatureTemplateListView.setLayoutManager(layoutManager);
        signatureTemplateListView.setAdapter(signatureTemplateListAdapter);


//        signatureTemplateListAdapter.templates.clear();
//        signatureTemplateListAdapter.templates.addAll(signatureTemplates);
//        signatureTemplateListAdapter.notifyDataSetChanged();

//        RetrofitManager.getViewerService().getSignatureTemplates().enqueue(new Callback<List<SignatureTemplateModel>>() {
//            @Override
//            public void onResponse(Call<List<SignatureTemplateModel>> call, Response<List<SignatureTemplateModel>> response) {
//                if (response.body() != null) {
//                    signatureTemplateListAdapter.templates.clear();
//                    signatureTemplateListAdapter.templates.addAll(response.body());
//                    signatureTemplateListAdapter.notifyDataSetChanged();
//                } else
//                    Utility.showMainAlertServer(SignatureTemplateDialog.this, R.string.warning, getString(R.string.serverError), R.string.ok);
//            }
//
//            @Override
//            public void onFailure(Call<List<SignatureTemplateModel>> call, Throwable t) {
//                Utility.showMainAlertServer(SignatureTemplateDialog.this, R.string.warning, getString(R.string.serverError), R.string.ok);
//            }
//        });
    }

    @Override
    public void onStart() {
        super.onStart();

        initListView();
        if (Utility.getLocalData(this, Constants.LANG_KEY).equals("ar")) {
            this.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }

    private void initListView() {
        signatureTemplateListView = findViewById(R.id.lvAddedTemplate);
        signatureTemplateListAdapter = new SignatureTemplateListAdapter(signatureTemplates, getIntent().getBooleanExtra("settings", false));
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        signatureTemplateListView.setLayoutManager(layoutManager);
        signatureTemplateListView.setAdapter(signatureTemplateListAdapter);
    }
}
