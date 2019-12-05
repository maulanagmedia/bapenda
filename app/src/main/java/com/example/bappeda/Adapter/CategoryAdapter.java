package com.example.bappeda.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bappeda.MenuMerchants.MerchantSekitarActivity;
import com.example.bappeda.Model.CategoryModel;
import com.example.bappeda.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context context;
    private List<CategoryModel> listCategory;
    private String idCategory;

    private int position_aktif = 0; //clicked true

    public CategoryAdapter (Context context, List<CategoryModel> listCategory){
        this.context = context;
        this.listCategory = listCategory;
    }

    @NonNull
    @Override
    public CategoryAdapter.CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryViewHolder(LayoutInflater.from(context).inflate(R.layout.item_kategori_merchant, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, final int position) {

        //when clicked
        if (position == position_aktif){
            //if yes
            holder.cardKategori.setCardBackgroundColor(Color.parseColor("#F1F1F1"));
        } else {
            //if not
            holder.cardKategori.setCardBackgroundColor(Color.WHITE);
        }

        holder.bind(listCategory.get(position));
        holder.cardKategori.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (context instanceof MerchantSekitarActivity){
                    ((MerchantSekitarActivity) context).selectedKategori = listCategory.get(position).getIdKategori();
                    ((MerchantSekitarActivity) context).loadListMerchant();
                    Log.d("_log", "id: " + listCategory.get(position).getIdKategori());
                }

                int position_temp = position_aktif;
                position_aktif = position;

                notifyItemChanged(position_temp);
                notifyItemChanged(position_aktif);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listCategory.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {

        CardView cardKategori;
        TextView txtKategori;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            cardKategori = itemView.findViewById(R.id.parent_layout);
            txtKategori = itemView.findViewById(R.id.txt_Kategori);

            cardKategori.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }

        void bind (final CategoryModel c) {
            idCategory = c.getIdKategori();
            txtKategori.setText(c.getNama());
        }
    }
}
