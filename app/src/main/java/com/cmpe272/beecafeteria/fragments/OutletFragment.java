package com.cmpe272.beecafeteria.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cmpe272.beecafeteria.R;
import com.cmpe272.beecafeteria.adapter.MenuAdapter;
import com.cmpe272.beecafeteria.base.App;
import com.cmpe272.beecafeteria.modelResponse.MenuItem;
import com.cmpe272.beecafeteria.modelResponse.Outlet;
import com.cmpe272.beecafeteria.modelResponse.PostResponse;
import com.cmpe272.beecafeteria.network.GsonGetRequest;
import com.cmpe272.beecafeteria.network.GsonPostRequest;
import com.cmpe272.beecafeteria.network.MenuApiReqests;
import com.cmpe272.beecafeteria.network.OutletApiRequests;
import com.cmpe272.beecafeteria.others.SessionManager;
import com.cmpe272.beecafeteria.others.Utils;

import org.lucasr.twowayview.ItemClickSupport;
import org.lucasr.twowayview.widget.TwoWayView;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OutletFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OutletFragment extends Fragment implements MenuAdapter.NumberPickerCallback, NumberPickerDialogFragment.NumberChangeCalllbackInterface, OrderConfirmatonDialogFragment.OrderConfirmationCallback {

    public static String TAG = "OutletFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;

    @Bind(R.id.menuList)
    TwoWayView menuLIst;

    @Bind(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @Bind(R.id.no_item_layout)
    LinearLayout layoutNoItem;

    @Bind(R.id.btn_cancel)
    Button btnCancel;

    @Bind(R.id.btn_checkout)
    Button btnCheckout;

    //Context of application
    private Activity activity;

    //Adapter object to set in RecyclerList of outlets
    private MenuAdapter menuAdapter;

    //Selected outlet object
    private Outlet selectedOutlet;

    private List<MenuItem> menuItemList;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param outlet Selected Outlet.
     * @return A new instance of fragment OutletsListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OutletFragment newInstance(Outlet outlet) {
        OutletFragment fragment = new OutletFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, Parcels.wrap(outlet));
        fragment.setArguments(args);
        return fragment;
    }

    public OutletFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedOutlet = Parcels.unwrap(getArguments().getParcelable(ARG_PARAM1));
        }
        menuItemList = new ArrayList<>();
        getData();
    }

    @Override
    public void onAttach(Context context) {
        this.activity = (Activity) context;
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_oulet, container, false);
        ButterKnife.bind(this, view);

        menuAdapter = new MenuAdapter(activity, menuItemList, this);

        menuLIst.setAdapter(menuAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });

        final ItemClickSupport itemClick = ItemClickSupport.addTo(menuLIst);

        itemClick.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View view, int i, long l) {
                //Toast.makeText(activity, "Item clicked: " + i, Toast.LENGTH_LONG).show();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCancelCalled();
            }
        });

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkout();
            }
        });

        return view;
    }

    /**
     * Return menuItemList for navigation drawer
     *
     * @return list of navigation drawer items
     */
    public void getData() {

        final ProgressDialog progressDialog = new ProgressDialog(activity,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Fetching Deals...");
        progressDialog.show();

        String outletName = selectedOutlet.getOutletName();

        outletName = outletName.replace(" ", "%20");

        final GsonGetRequest gsonPostRequest =
                MenuApiReqests.getMenuFromOutletRequest(
                        new Response.Listener<ArrayList<MenuItem>>() {
                            @Override
                            public void onResponse(ArrayList<MenuItem> arrDailyDealses) {
                                menuItemList.clear();
                                menuItemList.addAll(arrDailyDealses);
                                menuAdapter.notifyDataSetChanged();

                                if (menuItemList.isEmpty()) {
                                    layoutNoItem.setVisibility(View.VISIBLE);
                                    menuLIst.setVisibility(View.GONE);
                                } else {
                                    layoutNoItem.setVisibility(View.GONE);
                                    menuLIst.setVisibility(View.VISIBLE);
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
                                Log.d("Error",error.toString());
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
                        outletName
                );

        App.addRequest(gsonPostRequest, TAG);

    }

    @Override
    public void onStop() {
        super.onStop();
        App.cancelAllRequests(TAG);
    }

    @Override
    public void onNumberpickerOpener(String title, int position) {
        DialogFragment newFragment = NumberPickerDialogFragment.newInstance(title, position);
        newFragment.setTargetFragment(this, 0);
        newFragment.show(getFragmentManager(), "NumberPicker");
    }

    @Override
    public void onNumberChanged(int position, int quantity) {
        menuItemList.get(position).setQuantity(String.valueOf(quantity));
        menuAdapter.notifyDataSetChanged();
    }

    private void checkout() {
        ArrayList<MenuItem> menuSelectedItem = new ArrayList<>();
        for (MenuItem item : menuItemList) {
            if (item.isSelected()) {
                menuSelectedItem.add(item);
            }
        }

        if(menuSelectedItem.size() > 0) {
            DialogFragment newFragment = OrderConfirmatonDialogFragment.newInstance(menuSelectedItem);
            newFragment.setTargetFragment(this, 0);
            newFragment.show(getFragmentManager(), "ConfirmOrder");
        }else{
            Utils.alertDialogCreatorWithMessageOnly(activity,"Order","Please select item before placing order.");
        }
    }

    private void onCancelCalled() {

        DialogInterface.OnClickListener positiveClickListner = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                activity.onBackPressed();
            }
        };

        DialogInterface.OnClickListener negativeClickListner = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        };

        Utils.alertDialogCreator(activity, "Cancel Order", "Are you sure you want to cancel the selection?", positiveClickListner, negativeClickListner);
    }

    @Override
    public void onOrderPlaced(ArrayList<MenuItem> confirmMenuList,String orderTotal) {
        final ProgressDialog progressDialog = new ProgressDialog(activity,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Placing Order...");
        progressDialog.show();

        final String strCounterId = selectedOutlet.getOutletId();
        final String strUsername = SessionManager.getUserDetails(activity).get(SessionManager.KEY_EMAIL);
        final String dailyDealFlag = activity.getString(R.string.DailyDealOff);
        final String orderList = generateOrderString(confirmMenuList);

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
                                        activity.onBackPressed();
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
                                                        checkout();
                                                    }
                                                }).show();
                                    }
                                },
                                strCounterId, strUsername, dailyDealFlag, orderList, orderTotal
                        );

        App.addRequest(gsonPostRequest, TAG);
    }

    private String generateOrderString(ArrayList<MenuItem> confirmMenuList){
        StringBuilder generatedOrder = new StringBuilder();
        for(MenuItem item: confirmMenuList){
            generatedOrder.append(item.getItemName()+" "+item.getQuantity()+", ");
        }
        return generatedOrder.toString();
    }
}