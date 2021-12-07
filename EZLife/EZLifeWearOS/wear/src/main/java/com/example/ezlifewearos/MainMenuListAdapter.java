package com.example.ezlifewearos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainMenuListAdapter extends RecyclerView.Adapter<MainMenuListAdapter.Holder>{

    private final Context menuContext;
    private final LayoutInflater menuInflater;
    private List<AppFuncItem> menuItems;

    public MainMenuListAdapter(Context input_context, List<AppFuncItem> input_items){
        this.menuContext = input_context;
        this.menuItems = input_items;
        menuInflater = LayoutInflater.from(input_context);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(menuInflater.inflate(R.layout.main_menu_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        if(menuItems.isEmpty()){
            return;
        }
        final AppFuncItem item = menuItems.get(position);
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

        public void bind(AppFuncItem item){
            itemTextView.setText(item.getName());
            itemImageView.setImageResource(item.getImageId());
        }
    }
}
