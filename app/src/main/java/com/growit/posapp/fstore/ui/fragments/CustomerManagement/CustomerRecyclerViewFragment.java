package com.growit.posapp.fstore.ui.fragments.CustomerManagement;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.growit.posapp.fstore.MainActivity;
import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.adapters.CustomerRecyclerViewAdapter;
import com.growit.posapp.fstore.db.DatabaseClient;
import com.growit.posapp.fstore.tables.Customer;
import com.growit.posapp.fstore.utils.RecyclerItemClickListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CustomerRecyclerViewFragment extends Fragment {
    private List<Customer> customerDataList = new ArrayList<>();
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    CustomerRecyclerViewAdapter customAdapter;
    EditText searchEditTxt;
    private TextView total_customer_text;
    private boolean isSearch = false;
    private String[] cstTpeArray = {"All","Farmer", "Franchisee", "Dealer"};
    private int[] cstTpeIDArray = {0,1,2,3};
    int cusTomerType=0;
    private List<Customer> searchCustomerDataList=new ArrayList<>();
    Spinner cstTypeSpinner;
    ImageView backBtn;
    public static CustomerRecyclerViewFragment newInstance() {
        return new CustomerRecyclerViewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.customer_recyclerview, parent, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        cstTypeSpinner= view.findViewById(R.id.cstTypeSpinner);
        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, cstTpeArray);
        cstTypeSpinner.setAdapter(spinnerArrayAdapter);
        searchEditTxt = view.findViewById(R.id.seacrEditTxt);
        LinearLayout lay_add_customer = view.findViewById(R.id.lay_add_customer);
        total_customer_text = view.findViewById(R.id.total_customer_text);
        backBtn = view.findViewById(R.id.backBtn);
        TextView add_text = view.findViewById(R.id.add_text);
        lay_add_customer.setVisibility(View.VISIBLE);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL); // set Horizontal Orientation
        customAdapter = new CustomerRecyclerViewAdapter(getActivity(), null);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);


        cstTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cusTomerType = cstTpeIDArray[position];
                performSearch();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        searchEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
                if(s.length()>0){
                    isSearch=true;
                }else{
                    isSearch=false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchEditTxt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                isSearch = true;
                performSearch();

                return true;
            }
            return false;
        });
        add_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = AddCustomerFragment.newInstance();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainActivity.class));

            }
        });
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // do whatever
                        Bundle bundle = new Bundle();
                        if(isSearch) {
                            bundle.putSerializable("CustomerOBJ", (Serializable) searchCustomerDataList);
                            bundle.putInt("position", position);
                        }else{
                            bundle.putSerializable("CustomerOBJ", (Serializable) customerDataList);
                            bundle.putInt("position", position);
                        }
                        UpdateCustomerFragment UCFragment = UpdateCustomerFragment.newInstance();
                        UCFragment.setArguments(bundle);
                        // Insert the fragment by replacing any existing fragment
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.flContent, UCFragment).commit();
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
        getTasks();
        return view;
    }

    private void filter(String text) {
        // creating a new array list to filter our data.
        List<Customer> filteredList = new ArrayList<>();

        // running a for loop to compare elements.
        for (Customer item : customerDataList) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.getName().toLowerCase().contains(text.toLowerCase()) || item.getMobile().toLowerCase().contains(text.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredList.add(item);
            }
        }
        if (filteredList.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            Toast.makeText(getActivity(), R.string.NO_DATA, Toast.LENGTH_SHORT).show();
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            searchCustomerDataList=filteredList;
            customAdapter.filterList(filteredList);
        }
    }

    private void performSearch() {
        getTasks();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    }

    private void getTasks() {
        class GetTasks extends AsyncTask<Void, Void, List<Customer>> {

            @Override
            protected List<Customer> doInBackground(Void... voids) {
                customerDataList = DatabaseClient
                        .getInstance(getActivity())
                        .getAppDatabase()
                        .customerDao()
                        .getAll();
                if (isSearch) {
                    isSearch = false;
                    customerDataList = DatabaseClient
                            .getInstance(getActivity())
                            .getAppDatabase()
                            .customerDao()
                            .searchCustomer(searchEditTxt.getText().toString());
                } else if(cusTomerType!=0){
                    customerDataList = DatabaseClient
                            .getInstance(getActivity())
                            .getAppDatabase()
                            .customerDao()
                            .getAllCustomerByType(cusTomerType);
                }else if(cusTomerType==0) {
                    customerDataList = DatabaseClient
                            .getInstance(getActivity())
                            .getAppDatabase()
                            .customerDao()
                            .getAll();
                }

                return customerDataList;
            }

            @Override
            protected void onPostExecute(List<Customer> tasks) {
                super.onPostExecute(tasks);
                customAdapter = new CustomerRecyclerViewAdapter(getActivity(), tasks);
                recyclerView.setAdapter(customAdapter);
                recyclerView.setLayoutManager(linearLayoutManager);
                total_customer_text.setText("Total: " + tasks.size() + " " + "Customers");
            }
        }

        GetTasks gt = new GetTasks();
        gt.execute();
    }


}
