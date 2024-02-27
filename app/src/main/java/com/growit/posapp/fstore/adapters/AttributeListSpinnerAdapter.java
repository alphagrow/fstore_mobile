package com.growit.posapp.fstore.adapters;

import static java.security.AccessController.getContext;

import android.app.LauncherActivity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.model.AttributeModel;
import com.growit.posapp.fstore.model.Value;

import java.util.ArrayList;
import java.util.List;

public class AttributeListSpinnerAdapter extends ArrayAdapter<AttributeModel> {

    private final List<AttributeModel> items;
    private final List<AttributeModel> selectedItems;
    private final boolean[] checkedItems;
    private AttributeListSpinnerAdapter.OnItemSelectedListener onItemSelectedListener;

    public interface OnItemSelectedListener {
        void onItemSelected(List<AttributeModel> selectedItems, int pos);
    }

    public AttributeListSpinnerAdapter(Context context, List<AttributeModel> items, List<AttributeModel> selectedItems) {
        super(context, 0, items);
        this.items = items;
        this.selectedItems = selectedItems;
        this.checkedItems = new boolean[items.size()];

        for (int i = 0; i < items.size(); i++) {
            checkedItems[i] = selectedItems.contains(items.get(i));
        }
    }

    public void setOnItemSelectedListener(AttributeListSpinnerAdapter.OnItemSelectedListener listener) {
        this.onItemSelectedListener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createView(position, convertView, parent, false);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createView(position, convertView, parent, true);
    }

    private View createView(int position, View convertView, ViewGroup parent, boolean isDropDown) {
        View view = convertView != null
                ? convertView
                : LayoutInflater.from(getContext()).inflate(
                isDropDown ? R.layout.custom_spinner_dropdown_item : R.layout.custom_non_select_dropdown,
                parent,
                false
        );

        TextView textView = view.findViewById(R.id.spin_txt);
        if (isDropDown) {
            CheckBox checkBox = view.findViewById(R.id.spinnerCheckbox);
            TextView itemName = view.findViewById(R.id.itemName);

            itemName.setText(items.get(position).getName());
            checkBox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                checkedItems[position] = isChecked;
                if (isChecked) {
                    if (!selectedItems.contains(items.get(position))) {
                        selectedItems.add(items.get(position));
                    }
                } else {
                    selectedItems.remove(items.get(position));
                }
                notifyDataSetChanged();
                if (onItemSelectedListener != null) {
                    onItemSelectedListener.onItemSelected(selectedItems, position);
                }
            });
        } else {
            if (selectedItems.isEmpty()) {
                textView.setText("Select");
            } else {
                ArrayList<String> names = new ArrayList<>();
                for (AttributeModel selectedItem : getSelectedItems()) {
                    names.add(selectedItem.getName());
                }
                textView.setText(names.toString().replace("[", "").replace("]", ""));
            }
        }

        return view;
    }

    private List<AttributeModel> getSelectedItems() {
        return selectedItems;
    }
}