package com.iao.saydaliyati.ui.pharmacies;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.iao.saydaliyati.MainActivity;
import com.iao.saydaliyati.R;
import com.iao.saydaliyati.entity.Pharmacy;
import com.iao.saydaliyati.ui.profile.ProfileActivity;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<Pharmacy> pharmacies;
    private Context context;

    public MyAdapter(Context context, List<Pharmacy> pharmacies) {
        this.pharmacies = pharmacies;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_pharmacy, parent, false);



        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pharmacy pharmacy = pharmacies.get(position);

        holder.getTv_list_item_name().setText(pharmacy.getName());
        holder.getTv_list_item_city().setText(pharmacy.getCity()+ ", "+ pharmacy.getArrondissement());

        holder.getIb_list_item_map().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("pharmacy", pharmacy);
                context.startActivity(intent);
            }
        });

        holder.getIb_list_item_afficher().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("pharmacy", pharmacy);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pharmacies.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tv_list_item_name;
        private final TextView tv_list_item_city;
        private final ImageButton ib_list_item_map;
        private final ImageButton ib_list_item_afficher;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_list_item_name = itemView.findViewById(R.id.tv_list_item_name);
            tv_list_item_city = itemView.findViewById(R.id.tv_list_item_city);
            ib_list_item_map = itemView.findViewById(R.id.ib_list_item_map);
            ib_list_item_afficher = itemView.findViewById(R.id.ib_list_item_afficher);
        }

        public TextView getTv_list_item_name() {
            return tv_list_item_name;
        }

        public TextView getTv_list_item_city() {
            return tv_list_item_city;
        }

        public ImageButton getIb_list_item_map() {
            return ib_list_item_map;
        }

        public ImageButton getIb_list_item_afficher() {
            return ib_list_item_afficher;
        }
    }
}
