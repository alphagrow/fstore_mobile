package com.growit.posapp.fstore.ui.fragments.Setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.utils.ApiConstants;


public class ConfirmOrderFragment extends Fragment {

    // TODO: Rename and change types and number of parameters
    String purchase_order;
    TextView order_no_Txt;
    public static ConfirmOrderFragment newInstance() {
        ConfirmOrderFragment fragment = new ConfirmOrderFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.confirm_order, container, false);
        TextView confirmed=view.findViewById(R.id.confirmTxt);
        order_no_Txt =view.findViewById(R.id.order_no_Txt);
        if (getArguments() != null) {
            purchase_order = getArguments().getString("purchase_order");
            order_no_Txt.setText("Order Number : " +purchase_order);
        }
    //    confirmed.setOnClickListener(v -> Utility.whatsupSharing(getActivity(),"https://www.adobe.com/support/products/enterprise/knowledgecenter/media/c4611_sample_explain.pdf"));
        sendToSomeActivity();
        return view;
    }

    private void sendToSomeActivity(){
        Intent intent = new Intent();
        intent.setAction(ApiConstants.ACTION);
        intent.putExtra("dataToPass", "2");
        getActivity().sendBroadcast(intent);
    }

}