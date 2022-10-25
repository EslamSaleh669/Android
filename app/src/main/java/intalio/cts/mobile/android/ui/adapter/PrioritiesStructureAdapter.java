package intalio.cts.mobile.android.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.cts.mobile.android.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import intalio.cts.mobile.android.data.network.response.PrioritiesResponseItem;
import intalio.cts.mobile.android.data.network.response.PurposesResponseItem;

public class PrioritiesStructureAdapter extends ArrayAdapter {

    private List<PrioritiesResponseItem> dataList;
    private Context mContext;
    private int itemLayout;

    private final ListFilter listFilter = new ListFilter();
    private List<PrioritiesResponseItem> dataListAllItems;



    public PrioritiesStructureAdapter(Context context, int resource, List<PrioritiesResponseItem> storeDataLst) {
        super(context, resource, storeDataLst);
        dataList = storeDataLst;
        mContext = context;
        itemLayout = resource;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public PrioritiesResponseItem getItem(int position) {

        return dataList.get(position) ;
    }

    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {


        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.category_item_viewshape, parent, false);
        }

        TextView strName = (TextView) view.findViewById(R.id.categoryValue);
        strName.setText(dataList.get(position).getText());
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
                    dataListAllItems = new ArrayList<PrioritiesResponseItem>(dataList);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                synchronized (lock) {
                    results.values = dataListAllItems;
                    results.count = dataListAllItems.size();
                }
            } else {
                final String searchStrLowerCase = prefix.toString().toLowerCase();

                ArrayList<PrioritiesResponseItem> matchValues = new ArrayList<PrioritiesResponseItem>();

                for (PrioritiesResponseItem dataItem : dataListAllItems) {
                    if (Objects.requireNonNull(dataItem.getText()).toLowerCase().startsWith(searchStrLowerCase)) {
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
                dataList = (ArrayList<PrioritiesResponseItem>) results.values;
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