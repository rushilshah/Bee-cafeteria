package com.cmpe272.beecafeteria.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import com.cmpe272.beecafeteria.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Rushil on 11/26/2015.
 */
public class NumberPickerDialogFragment extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_POS = "position";

    private String title;

    private int position;

    @Bind(R.id.number_picker)
    NumberPicker numberPicker;

    //Numberpicker to change quantity
    NumberChangeCalllbackInterface mCallback;

    public NumberPickerDialogFragment(){

    }

    public static NumberPickerDialogFragment newInstance(String title,int position){
        NumberPickerDialogFragment fragment = new NumberPickerDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, title);
        args.putInt(ARG_POS, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mCallback = (NumberChangeCalllbackInterface) getTargetFragment();
        }catch (ClassCastException e){
            throw new ClassCastException("Fragment must implement NumberPickerCallback.");
        }

        title = getArguments().getString(ARG_PARAM1);
        position = getArguments().getInt(ARG_POS);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialgo_fragment_numberpicker, null);
        ButterKnife.bind(this, view);

        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(10);

        Dialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setView(view)
                .setNegativeButton("Close",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                .setPositiveButton("Set",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mCallback.onNumberChanged(position,numberPicker.getValue());
                            }
                        })
                .create();
        return alertDialog;

    }

    public interface NumberChangeCalllbackInterface{
        void onNumberChanged(int position,int quantity);
    }
}
