package intalio.cts.mobile.android.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.cts.mobile.android.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import intalio.cts.mobile.android.data.network.response.AllStructuresAttributesItem;
import intalio.cts.mobile.android.data.network.response.AllStructuresItem;
import intalio.cts.mobile.android.data.network.response.PurposesResponseItem;

public class StructuresAdapter extends ArrayAdapter {

    private List<AllStructuresItem> dataList;
    private Context mContext;
    private int itemLayout;
    private String language;

    private final ListFilter listFilter = new ListFilter();
    private List<AllStructuresItem> dataListAllItems;


    public StructuresAdapter(Context context, int resource, List<AllStructuresItem> storeDataLst, String mylanguage
    ) {
        super(context, resource, storeDataLst);
        dataList = storeDataLst;
        mContext = context;
        itemLayout = resource;
        language = mylanguage;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public AllStructuresItem getItem(int position) {

        return dataList.get(position);
    }

    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.key_value_layout, parent, false);
        }

        TextView strName = (TextView) view.findViewById(R.id.autoValue);
        ImageView imageView = (ImageView) view.findViewById(R.id.imgtype);


        if (dataList.get(position).getItemType().equals("user")) {
            imageView.setImageResource(R.drawable.ic_touser_black);
             strName.setText(dataList.get(position).getName());

        } else {
            switch (language) {
                case "en":
                    strName.setText(dataList.get(position).getName());
                    break;

                case "ar":
                    List<AllStructuresAttributesItem> attributes = dataList.get(position).getAttributes();
                    for (int i = 0; i < Objects.requireNonNull(attributes).size(); i++) {
                        if (Objects.equals(attributes.get(i).getText(), "NameAr")) {
                            String structureNameAr = attributes.get(i).getValue();
                            if (structureNameAr.isEmpty()){
                                strName.setText(dataList.get(position).getName());

                            }else {
                                strName.setText(structureNameAr);

                            }

                        }
                    }
                    break;

                case "fr":

                    List<AllStructuresAttributesItem> attributes2 = dataList.get(position).getAttributes();
                    for (int i = 0; i < Objects.requireNonNull(attributes2).size(); i++) {
                        if (Objects.equals(attributes2.get(i).getText(), "NameFr")) {
                            String structureNameFR = attributes2.get(i).getValue();
                            if (structureNameFR.isEmpty()){
                                strName.setText(dataList.get(position).getName());

                            }else {
                                strName.setText(structureNameFR);
                            }

                        }
                    }
                    break;


            }


            imageView.setImageResource(R.drawable.ic_bulding_black);
        }


        return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return listFilter;
    }

    public class ListFilter extends Filter {
        private final Object lock = new Object();

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (dataListAllItems == null) {
                synchronized (lock) {
                    dataListAllItems = new ArrayList<AllStructuresItem>(dataList);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                synchronized (lock) {
                    results.values = dataListAllItems;
                    results.count = dataListAllItems.size();
                }
            } else {
                final String searchStrLowerCase = prefix.toString().toLowerCase();

                ArrayList<AllStructuresItem> matchValues = new ArrayList<AllStructuresItem>();

                for (AllStructuresItem dataItem : dataListAllItems) {
                    if (Objects.requireNonNull(dataItem.getName()).toLowerCase().startsWith(searchStrLowerCase)) {
                        matchValues.add(dataItem);
                    }
                }

                results.values = matchValues;
                results.count = matchValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {
                dataList = (ArrayList<AllStructuresItem>) results.values;
            } else {
                dataList = null;
            }
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

    }
}
