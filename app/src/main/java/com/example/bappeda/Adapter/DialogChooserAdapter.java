package com.example.bappeda.Adapter;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bappeda.Model.CategoryModel;
import com.example.bappeda.R;

import java.util.List;

public class DialogChooserAdapter extends RecyclerView.Adapter<DialogChooserAdapter.DialogChooserViewHolder> {

    private Context context;
    private List<CategoryModel> listKategori;
    private ChooserListener listener;

    public DialogChooserAdapter(Context context, List<CategoryModel> listKategori, ChooserListener listener){
         this.context = context;
         this.listKategori = listKategori;
         this.listener = listener;
    }

    @NonNull
    @Override
    public DialogChooserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DialogChooserViewHolder(LayoutInflater.from(context).inflate(R.layout.item_chooser, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DialogChooserViewHolder holder, int position) {
        holder.bind(listKategori.get(position));
    }

    @Override
    public int getItemCount() {
        return listKategori.size();
    }

    class DialogChooserViewHolder extends RecyclerView.ViewHolder{

        TextView txt_item;

        DialogChooserViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_item = itemView.findViewById(R.id.txt_item);
        }

        void bind(final CategoryModel c){
            txt_item.setText(c.getNama());
            txt_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onSelected(c.getIdKategori(), c.getNama());
                }
            });
        }
    }

    public interface ChooserListener{
        void onSelected(String id, String value);
    }
}
