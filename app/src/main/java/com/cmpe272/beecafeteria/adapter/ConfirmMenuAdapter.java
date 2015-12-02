package com.cmpe272.beecafeteria.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cmpe272.beecafeteria.R;
import com.cmpe272.beecafeteria.modelResponse.MenuItem;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Rushil on 11/20/2015.
 */
public class ConfirmMenuAdapter extends RecyclerView.Adapter<ConfirmMenuAdapter.MyViewHolder> {
    List<MenuItem> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;
    private NumberPickerCallback mCallback;

    public ConfirmMenuAdapter(Context context, List<MenuItem> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;

    }

    public void delete(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.adapter_confirm_order, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final MenuItem menuItem = data.get(position);
        holder.itemName.setText(menuItem.getItemName());
        holder.itemQty.setText("x" + String.valueOf(menuItem.getQuantity()));
        double total = Float.parseFloat(menuItem.getPrice())*Float.parseFloat(menuItem.getQuantity());
        holder.itemPrice.setText(String.valueOf(Math.round(total*100)/100D)+"$");

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.txtItemName)
        TextView itemName;

        @Bind(R.id.txtItemQty)
        TextView itemQty;

        @Bind(R.id.txtPrice)
        TextView itemPrice;


        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public interface NumberPickerCallback{
        void onNumberpickerOpener(String title, int position);
    }
}
