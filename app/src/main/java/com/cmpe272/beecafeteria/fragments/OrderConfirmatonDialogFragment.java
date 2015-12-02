package com.cmpe272.beecafeteria.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.cmpe272.beecafeteria.R;
import com.cmpe272.beecafeteria.adapter.ConfirmMenuAdapter;
import com.cmpe272.beecafeteria.modelResponse.MenuItem;

import org.lucasr.twowayview.widget.TwoWayView;
import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Rushil on 11/28/2015.
 */
public class OrderConfirmatonDialogFragment extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_POS = "position";

    private ArrayList<MenuItem> confirmMenuList;

    @Bind(R.id.confirmMenuList)
    TwoWayView confirmMenuTwoWayView;

    @Bind(R.id.txt_total)
    TextView txtTotal;

    //Adapter object to set in RecyclerList of outlets
    private ConfirmMenuAdapter menuAdapter;

    //Numberpicker to change quantity
    OrderConfirmationCallback mCallback;

    Activity activity;

    public OrderConfirmatonDialogFragment() {

    }

    public static OrderConfirmatonDialogFragment newInstance(ArrayList<MenuItem> menuItemArrayList) {
        OrderConfirmatonDialogFragment fragment = new OrderConfirmatonDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, Parcels.wrap(menuItemArrayList));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity=activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mCallback = (OrderConfirmationCallback) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Fragment must implement OrderConfirmationCallback.");
        }

        confirmMenuList = Parcels.unwrap(getArguments().getParcelable(ARG_PARAM1));
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_fragment_confirm_order, null);
        ButterKnife.bind(this, view);

        menuAdapter = new ConfirmMenuAdapter(activity,confirmMenuList);

        confirmMenuTwoWayView.setAdapter(menuAdapter);

        txtTotal.setText(getTotal()+" $");

        Dialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle("Order")
                .setView(view)
                .setNegativeButton("Close",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                .setPositiveButton("Place Order",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mCallback.onOrderPlaced(confirmMenuList,getTotal());
                            }
                        })
                .create();
        return alertDialog;

    }

    public interface OrderConfirmationCallback{
            void onOrderPlaced(ArrayList<MenuItem> confirmMenuList,String orderTotal);
    }

    private String getTotal(){
        double total=0;
        for(MenuItem item:confirmMenuList){
            total = total + (Float.parseFloat(item.getQuantity())*Float.parseFloat(item.getPrice()));
        }
        return String.valueOf(Math.round(total*100)/100D);
    }
}
