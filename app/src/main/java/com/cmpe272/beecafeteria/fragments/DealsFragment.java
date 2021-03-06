package com.cmpe272.beecafeteria.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cmpe272.beecafeteria.R;
import com.cmpe272.beecafeteria.adapter.DailyDealsAdapter;
import com.cmpe272.beecafeteria.base.App;
import com.cmpe272.beecafeteria.modelResponse.DailyDeals;
import com.cmpe272.beecafeteria.modelResponse.PostResponse;
import com.cmpe272.beecafeteria.network.DailyDealsApiRequests;
import com.cmpe272.beecafeteria.network.GsonGetRequest;
import com.cmpe272.beecafeteria.network.GsonPostRequest;
import com.cmpe272.beecafeteria.network.OutletApiRequests;
import com.cmpe272.beecafeteria.others.SessionManager;
import com.cmpe272.beecafeteria.others.Utils;

import org.lucasr.twowayview.ItemClickSupport;
import org.lucasr.twowayview.widget.TwoWayView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DealsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DealsFragment extends Fragment {

    public static String TAG="DealsFragment";

    @Bind(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    @Bind(R.id.dealsList)
    TwoWayView dailyDealsList;

    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @Bind(R.id.no_item_layout)
    LinearLayout layoutNoItem;

    //Context of application
    private Activity activity;

    //Adapter object to set in RecyclerList of outlets
    private DailyDealsAdapter dailyDealsAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;

    //List that wil contain all daily list
    ArrayList<DailyDeals> arrListDailyDeals;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment DealsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DealsFragment newInstance(String param1) {
        DealsFragment fragment = new DealsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public DealsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
        arrListDailyDeals = new ArrayList<>();
        getData();
    }

    @Override
    public void onAttach(Context context) {
        this.activity = (Activity)context;
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_deals, container, false);
        ButterKnife.bind(this, view);

        dailyDealsAdapter = new DailyDealsAdapter(activity,arrListDailyDeals);

        dailyDealsList.setAdapter(dailyDealsAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });

        final ItemClickSupport itemClick = ItemClickSupport.addTo(dailyDealsList);

        itemClick.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View view, int i, long l) {
                //Toast.makeText(activity, "Deal Selected: " + i, Toast.LENGTH_LONG).show();
                onOrderConfirmationAlert(i);
            }
        });

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        App.cancelAllRequests(TAG);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    private void onOrderConfirmationAlert(final int position) {

        DialogInterface.OnClickListener positiveClickListner = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onOrderPlaced(arrListDailyDeals.get(position));
            }
        };

        DialogInterface.OnClickListener negativeClickListner = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        };

        Utils.alertDialogCreator(activity, "Place order", "Are you sure you want to place order for selected daily deal?", positiveClickListner, negativeClickListner);
    }

    /**
     * Return data for navigation drawer
     * @return list of navigation drawer items
     */
    public void getData() {

        final ProgressDialog progressDialog = new ProgressDialog(activity,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Fetching Deals...");
        progressDialog.show();


        final GsonGetRequest gsonPostRequest =
                DailyDealsApiRequests.getDailyDealsRequest(
                        new Response.Listener<ArrayList<DailyDeals>>() {
                            @Override
                            public void onResponse(ArrayList<DailyDeals> arrDailyDealses) {
                                arrListDailyDeals.clear();
                                arrListDailyDeals.addAll(arrDailyDealses);
                                dailyDealsAdapter.notifyDataSetChanged();

                                if(arrListDailyDeals.isEmpty()){
                                    layoutNoItem.setVisibility(View.VISIBLE);
                                    dailyDealsList.setVisibility(View.GONE);
                                }else{
                                    layoutNoItem.setVisibility(View.GONE);
                                    dailyDealsList.setVisibility(View.VISIBLE);
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

    public void onOrderPlaced(final DailyDeals dailyDeals) {
        final ProgressDialog progressDialog = new ProgressDialog(activity,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Placing Order...");
        progressDialog.show();

        final String strCounterId = dailyDeals.getProviderName();
        final String strUsername = SessionManager.getUserDetails(activity).get(SessionManager.KEY_EMAIL);
        final String dailyDealFlag = activity.getString(R.string.DailyDealOn);
        final String order = dailyDeals.getDailyDealTitle();
        final String orderPrice = String.valueOf(dailyDeals.getPrice());

        final GsonPostRequest gsonPostRequest =
                OutletApiRequests.postPlaceOrder
                        (
                                new Response.Listener<PostResponse>() {
                                    @Override
                                    public void onResponse(PostResponse dummyObject) {
                                        // Deal with the DummyObject here
                                        //mProgressBar.setVisibility(View.GONE);
                                        //mContent.setVisibility(View.VISIBLE);
                                        ///setData(dummyObject);
                                        progressDialog.dismiss();
                                        Toast.makeText(activity, "Order Placed Successfully!", Toast.LENGTH_LONG).show();

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
                                                        onOrderPlaced(dailyDeals);
                                                    }
                                                }).show();
                                    }
                                },
                                strCounterId, strUsername, dailyDealFlag, order, orderPrice
                        );

        App.addRequest(gsonPostRequest, TAG);
    }

}
