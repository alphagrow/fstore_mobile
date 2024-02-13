package com.growit.posapp.fstore.ui.fragments.Setting;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.growit.posapp.fstore.MainActivity;
import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.adapters.ConfigurationAdapter;
import com.growit.posapp.fstore.databinding.FragmentAddShopAndShopListBinding;
import com.growit.posapp.fstore.databinding.FragmentUserCreateBinding;
import com.growit.posapp.fstore.model.ConfigurationModel;
import com.growit.posapp.fstore.ui.fragments.AddProduct.UserAdapter;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.growit.posapp.fstore.utils.RecyclerItemClickListener;
import com.growit.posapp.fstore.utils.SessionManagement;
import com.growit.posapp.fstore.utils.Utility;
import com.growit.posapp.fstore.volley.VolleyCallback;
import com.growit.posapp.fstore.volley.VolleyRequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UserCreateFragment extends Fragment {


    FragmentUserCreateBinding binding;
    List<ConfigurationModel> list;
    Activity contexts;
    UserAdapter adapter;

    boolean isAllFieldsChecked = false;
    EditText et_username,ed_login,ed_password;
    public UserCreateFragment() {
        // Required empty public constructor
    }

    public static UserCreateFragment newInstance() {
        return new UserCreateFragment();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUserCreateBinding.inflate(inflater, container, false);
        init();
        return binding.getRoot();
    }
        private void init () {
            list = new ArrayList<>();
            GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1, LinearLayoutManager.VERTICAL, false);
            binding.recyclerVendor.setLayoutManager(layoutManager);
            if (Utility.isNetworkAvailable(getContext())) {
                getUserList();

            } else {
                Toast.makeText(getContext(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();

            }

            binding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    binding.refreshLayout.setRefreshing(false);
                    if (Utility.isNetworkAvailable(getContext())) {

                        getUserList();


                    } else {
                        Toast.makeText(getContext(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();

                    }

                }
            });
            binding.backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), MainActivity.class));
                    getActivity().finish();
                }
            });
            binding.addText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (Utility.isNetworkAvailable(getContext())) {
                        showDialogeReceiveProduct();
                    } else {
                        Toast.makeText(getContext(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();

                    }
                }
            });
            binding.seacrEditTxt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filterList(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });


        }
        @Override
        public void onAttach (@NonNull Context context){
            super.onAttach(context);
            contexts = getActivity();

        }

        private void showDialogeReceiveProduct () {

            Dialog dialog = new Dialog(getActivity());
            dialog.setContentView(R.layout.add_user_dialoge);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(false);
            dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
            et_username = dialog.findViewById(R.id.et_username);
            ed_login = dialog.findViewById(R.id.login_ed);
            ed_password = dialog.findViewById(R.id.et_password);
            TextView okay_text = dialog.findViewById(R.id.ok_text);
            TextView cancel_text = dialog.findViewById(R.id.cancel_text);


            okay_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String str_shop_name = et_username.getText().toString();
                    String str_login = ed_login.getText().toString();
                    String str_password = ed_password.getText().toString();
                    isAllFieldsChecked = CheckAllFields();
                    if (!Utility.isNetworkAvailable(getActivity())) {
                        Toast.makeText(getContext(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (isAllFieldsChecked) {
                        addUser(str_shop_name,str_login,str_password);
                    }
                    dialog.dismiss();

                }
            });

            cancel_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        }


        private boolean CheckAllFields () {
            if (et_username.length() == 0) {
                et_username.setError("This field is required");
                Toast.makeText(getActivity(), "Enter the User Name", Toast.LENGTH_SHORT).show();

                return false;
            }
            if (ed_login.length() == 0) {
                ed_login.setError("This field is required");
                Toast.makeText(getActivity(), "Enter the login", Toast.LENGTH_SHORT).show();

                return false;
            }
            if (ed_password.length() == 0) {
                ed_password.setError("This field is required");
                Toast.makeText(getActivity(), "Enter the password", Toast.LENGTH_SHORT).show();

                return false;
            }
            return true;
        }

        private void addUser (String str_name,String login,String password){
            SessionManagement sm = new SessionManagement(getActivity());
            Map<String, String> params = new HashMap<>();
            params.put("user_id", sm.getUserID() + "");
            params.put("token", sm.getJWTToken());
            params.put("name", str_name);
            params.put("login", login);
            params.put("password", password);

            Utility.showDialoge("Please wait while a moment...", getActivity());
            Log.v("add", String.valueOf(params));
            new VolleyRequestHandler(getActivity(), params).createRequest(ApiConstants.POST_CREATE_User, new VolleyCallback() {
                private String message = " failed!!";

                @Override
                public void onSuccess(Object result) throws JSONException {
                    Log.v("Response", result.toString());
                    JSONObject obj = new JSONObject(result.toString());
                    int statusCode = obj.optInt("statuscode");
                    String status = obj.optString("status");
                    String message = obj.optString("message");
                    String error_message = obj.optString("error_message");
                    if (statusCode == 200 && status.equalsIgnoreCase("success")) {
                        Utility.dismissDialoge();
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        et_username.setText("");
                        ed_login.setText("");
                        ed_password.setText("");
                        getUserList();
                    } else {
                        Utility.dismissDialoge();
                        Toast.makeText(getActivity(), error_message, Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onError(String result) throws Exception {
                    Utility.dismissDialoge();
                    Toast.makeText(getActivity(), R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show();
                }
            });

        }


        private void getUserList () {
            SessionManagement sm = new SessionManagement(contexts);
            RequestQueue queue = Volley.newRequestQueue(contexts);
            //  String url = ApiConstants.BASE_URL + ApiConstants.GET_CUSTOMER_DISCOUNT_LIST;

            String url = ApiConstants.BASE_URL + ApiConstants.GET_LIST_USER + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken();
            //    Utility.showDialoge("Please wait while a moment...", getActivity());
            Log.d("ALL_CROPS_url", url);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.v("Response", response.toString());
                    JSONObject obj = null;
                    try {
                        obj = new JSONObject(response.toString());
                        int statusCode = obj.optInt("statuscode");
                        String status = obj.optString("status");

                        if (status.equalsIgnoreCase("success")) {
                            // Utility.dismissDialoge();
                            JSONArray jsonArray = obj.getJSONArray("users");
                            list.clear();
                            if (jsonArray.length() > 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    ConfigurationModel model = new ConfigurationModel();
                                    JSONObject data = jsonArray.getJSONObject(i);
                                    Integer id = data.optInt("id");
                                    String name = data.optString("name");
                                    String login = data.optString("login");
                                    model.setId(id);
                                    model.setName(name);
                                    model.setLogin(login);
                                    list.add(model);
                                }
                                if (list == null || list.size() == 0) {
                                    binding.noDataFound.setVisibility(View.VISIBLE);
                                } else {
                                    binding.totalCustomerText.setText("Total : " + list.size() + " User ");
                                    binding.noDataFound.setVisibility(View.GONE);
                                    adapter = new UserAdapter(getActivity(), list);
                                    binding.recyclerVendor.setAdapter(adapter);

                                }

                            }
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }, error -> Toast.makeText(contexts, R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show());
            queue.add(jsonObjectRequest);
        }
        private void filterList (String text){

            ArrayList<ConfigurationModel> model = new ArrayList<>();
            for (ConfigurationModel detail : list) {
                if (detail.getName().toLowerCase().contains(text.toLowerCase())) {
                    model.add(detail);
                }
            }

            adapter.updateList(model);
        }

    }
