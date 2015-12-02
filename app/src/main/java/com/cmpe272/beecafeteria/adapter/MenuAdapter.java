package com.cmpe272.beecafeteria.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MyViewHolder> {
    List<MenuItem> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;
    private NumberPickerCallback mCallback;

    public MenuAdapter(Context context, List<MenuItem> data,Fragment outletFragment) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        try {
            mCallback = (NumberPickerCallback)outletFragment;
        }
        catch (ClassCastException e){
            throw new ClassCastException("Fragment is not of implementing NumberPickerCallback.");
        }
    }

    public void delete(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.adapter_menu, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final MenuItem menuItem = data.get(position);
        holder.itemName.setText(menuItem.getItemName());
        holder.checkBox.setChecked(menuItem.isSelected());
        holder.itemQty.setText(menuItem.getQuantity());
        holder.itemPrice.setText(menuItem.getPrice()+"$");

        holder.checkBox.setTag(menuItem);

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                MenuItem item = (MenuItem) cb.getTag();

                item.setSelected(cb.isChecked());
                data.get(position).setSelected(cb.isChecked());

            }
        });

        holder.itemQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onNumberpickerOpener(menuItem.getItemName()+" Quantity",position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.txtItemName)
        TextView itemName;

        @Bind(R.id.checkBoxItem)
        CheckBox checkBox;

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
        void onNumberpickerOpener(String title,int position);
    }
}
