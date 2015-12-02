package com.cmpe272.beecafeteria.fragments;

import android.app.Activity;
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
import com.cmpe272.beecafeteria.adapter.OrderAdapter;
import com.cmpe272.beecafeteria.base.App;
import com.cmpe272.beecafeteria.modelResponse.Order;
import com.cmpe272.beecafeteria.network.GsonPostRequest;
import com.cmpe272.beecafeteria.network.OrderApiRequests;
import com.cmpe272.beecafeteria.others.SessionManager;

import org.lucasr.twowayview.ItemClickSupport;
import org.lucasr.twowayview.widget.TwoWayView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DealsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrdersFragment extends Fragment {

    public static String TAG = "OrdersFragment";

    @Bind(R.id.orderList)
    TwoWayView orderList;

    @Bind(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @Bind(R.id.no_item_layout)
    LinearLayout layoutNoItem;

    //Context of application
    private Activity activity;

    //Adapter object to set in RecyclerList of outlets
    private OrderAdapter orderAdapter;

    //List of all orders
    private List<Order> orderArrList;

    //interface callback to open detail order fragment
    private OrderFragmentCallbacks mCallbacks;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment DealsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrdersFragment newInstance(String param1) {
        OrdersFragment fragment = new OrdersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public OrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
        orderArrList = new ArrayList<>();
        getData();
    }

    @Override
    public void onAttach(Context context) {
        activity = (Activity) context;
        try {
            mCallbacks = (OrderFragmentCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        ButterKnife.bind(this, view);

        orderAdapter = new OrderAdapter(activity, orderArrList);

        orderList.setAdapter(orderAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });

        final ItemClickSupport itemClick = ItemClickSupport.addTo(orderList);

        itemClick.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View view, int i, long l) {
                mCallbacks.onOrderSelected(orderArrList.get(i));
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
     *
     * @return list of navigation drawer items
     */
    public void getData() {
        /*List<Order> data = new ArrayList<>();


        // preparing navigation drawer items
        for (int i = 0; i < 3; i++) {
            Order order = new Order();
            order.setOutlateName("Subway");
            order.setCounter("1");
            order.setOrderDate("11/11/2015");
            order.setOrderDescription("2 Veggies");
            order.setStatus("Delivered");
            order.setTotal("5.99");
            data.add(order);
        }

        orderArrList = data;
        return data;*/

        final ProgressDialog progressDialog = new ProgressDialog(activity,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Fetching previous orders...");
        progressDialog.show();

        final String strUsername = SessionManager.getUserDetails(activity).get(SessionManager.KEY_EMAIL);

        final GsonPostRequest gsonPostRequest =
                OrderApiRequests.getOrdersRequests
                        (
                                new Response.Listener<ArrayList<Order>>() {
                                    @Override
                                    public void onResponse(ArrayList<Order> arrOrders) {
                                        orderArrList.clear();
                                        orderArrList.addAll(arrOrders);
                                        orderAdapter.notifyDataSetChanged();

                                        if (orderArrList.isEmpty()) {
                                            layoutNoItem.setVisibility(View.VISIBLE);
                                            orderList.setVisibility(View.GONE);
                                        } else {
                                            layoutNoItem.setVisibility(View.GONE);
                                            orderList.setVisibility(View.VISIBLE);
                                        }

                                        swipeRefreshLayout.setRefreshing(false);

                                        progressDialog.dismiss();
                                    }
                                }
                                ,
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Snackbar.make(coordinatorLayout, "Something went wrong!", Snackbar.LENGTH_LONG)
                                                .setAction("Retry", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        getData();
                                                    }
                                                }).show();
                                        progressDialog.dismiss();
                                    }
                                },
                                strUsername
                        );

        App.addRequest(gsonPostRequest, TAG);


    }


    public interface OrderFragmentCallbacks{

        void onOrderSelected(Order order);
    }

}
