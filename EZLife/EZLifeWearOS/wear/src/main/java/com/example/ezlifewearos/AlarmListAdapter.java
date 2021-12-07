package com.example.ezlifewearos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AlarmListAdapter extends RecyclerView.Adapter<AlarmListAdapter.Holder> {

    private final Context menuContext;
    private final LayoutInflater menuInflater;
    private List<AlarmItem> menuItems;

    public AlarmListAdapter(Context input_context, List<AlarmItem> input_items){
        this.menuContext = input_context;
        this.menuItems = input_items;
        menuInflater = LayoutInflater.from(input_context);
    }

    @Override
    public AlarmListAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AlarmListAdapter.Holder(menuInflater.inflate(R.layout.alarm_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(AlarmListAdapter.Holder holder, int position) {
        if(menuItems.isEmpty()){
            return;
        }
        final AlarmItem item = menuItems.get(position);
        holder.bind(item);

        final int pos = position;
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                menuItems.get(pos).launchActivity(menuContext);
            }
        });
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        TextView itemTextView;
        ImageView itemImageView;

        public Holder(final View itemView){
            super(itemView);
            itemTextView = itemView.findViewById(R.id.text_view);
            itemImageView = itemView.findViewById(R.id.image_view);
        }

        public void bind(AlarmItem item){
            itemTextView.setText(item.getName());
            itemImageView.setImageResource(item.getImageId());
        }
    }
}
