package com.cmpe272.beecafeteria.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cmpe272.beecafeteria.R;
import com.cmpe272.beecafeteria.adapter.OutletsAdapter;
import com.cmpe272.beecafeteria.base.App;
import com.cmpe272.beecafeteria.modelResponse.Outlet;
import com.cmpe272.beecafeteria.network.GsonGetRequest;
import com.cmpe272.beecafeteria.network.OutletApiRequests;

import org.lucasr.twowayview.ItemClickSupport;
import org.lucasr.twowayview.widget.TwoWayView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OutletsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OutletsListFragment extends Fragment {

    public static String TAG="OutletsListFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;

    @Bind(R.id.outletList)
    TwoWayView outletList;

    @Bind(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @Bind(R.id.no_item_layout)
    LinearLayout layoutNoItem;

    //interface callback to open menu of outlet
    private OutletsCallback mCallbacks;

    //List of all outlets
    private List<Outlet> outletsArrLIst;


    //Context of application
    private Context context;

    //Adapter object to set in RecyclerList of outlets
    private OutletsAdapter outletsAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment OutletsListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OutletsListFragment newInstance(String param1) {
        OutletsListFragment fragment = new OutletsListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public OutletsListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
        outletsArrLIst = new ArrayList<>();
        getData();
    }

    @Override
    public void onAttach(Context context) {
        this.context = context;
        try {
            mCallbacks = (OutletsCallback) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_outlets_list, container, false);
        ButterKnife.bind(this,view);

        outletsAdapter = new OutletsAdapter(context,outletsArrLIst);

        outletList.setAdapter(outletsAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });

        final ItemClickSupport itemClick = ItemClickSupport.addTo(outletList);

        itemClick.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View view, int i, long l) {
                mCallbacks.onOutletSelected(outletsArrLIst.get(i));
            }
        });

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        App.cancelAllRequests(TAG);
    }

    /**
     * Return data for navigation drawer
     * @return list of navigation drawer items
     */
    public void getData() {


        final ProgressDialog progressDialog = new ProgressDialog(context,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Fetching Outlets...");
        progressDialog.show();


        final GsonGetRequest gsonPostRequest =
                OutletApiRequests.getOutletRequest(
                        new Response.Listener<ArrayList<Outlet>>() {
                            @Override
                            public void onResponse(ArrayList<Outlet> arrDailyDealses) {
                                outletsArrLIst.clear();
                                outletsArrLIst.addAll(arrDailyDealses);
                                outletsAdapter.notifyDataSetChanged();

                                if (outletsArrLIst.isEmpty()) {
                                    layoutNoItem.setVisibility(View.VISIBLE);
                                    outletList.setVisibility(View.GONE);
                                } else {
                                    layoutNoItem.setVisibility(View.GONE);
                                    outletList.setVisibility(View.VISIBLE);
                                }
                                progressDialog.dismiss();
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        }
                        ,
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // Deal with the error here
                                //mProgressBar.setVisibility(View.GONE);
                                //mErrorView.setVisibility(View.VISIBLE);
                                progressDialog.dismiss();
                                Snackbar.make(coordinatorLayout, "Something went wrong!", Snackbar.LENGTH_LONG)
                                        .setAction("Retry", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                getData();
                                            }
                                        }).show();
                            }
                        }
                );

        App.addRequest(gsonPostRequest, TAG);

    }

    public interface OutletsCallback{

        void onOutletSelected(Outlet outlet);
    }


}
