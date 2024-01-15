package com.growit.posapp.fstore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.model.StateModel;
import com.growit.posapp.fstore.model.UomCategoryModel;

import java.util.List;

public class UOMSpinnerAdapter extends BaseAdapter {
    Context context;
    List<UomCategoryModel> stateModelList;
    LayoutInflater inflter;

    public UOMSpinnerAdapter(Context applicationContext, List<UomCategoryModel> stateNames) {
        this.context = applicationContext;
        this.stateModelList = stateNames;
        inflter = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        names.setText(stateModelList.get(i).getName());
        return view;
    }
}
