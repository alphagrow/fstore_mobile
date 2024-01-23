package com.growit.posapp.fstore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.model.AttributeModel;
import com.growit.posapp.fstore.model.Value;

import java.util.ArrayList;
import java.util.List;

public class AttributeListSpinnerAdapter extends BaseAdapter {
    Context context;
    List<AttributeModel> list;
    LayoutInflater inflter;

    public AttributeListSpinnerAdapter(Context applicationContext, List<AttributeModel> stateNames) {
        this.context = applicationContext;
        this.list = stateNames;
        inflter = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
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
        names.setText(list.get(i).getName());
        return view;
    }
}