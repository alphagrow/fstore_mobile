package com.growit.posapp.gstore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.growit.posapp.gstore.R;
import com.growit.posapp.gstore.model.Value;
import com.growit.posapp.gstore.tables.Customer;

import java.util.List;

public class VariantAdapter extends BaseAdapter {
    Context context;
    List<Value> stateModelList;
    LayoutInflater inflter;

    public VariantAdapter(Context applicationContext, List<Value> stateNames) {
        this.context = applicationContext;
        this.stateModelList = stateNames;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return stateModelList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.spinner_item_row, null);
        TextView names = (TextView) view.findViewById(R.id.name);
        names.setText(stateModelList.get(i).getValueName());
        return view;
    }
}
