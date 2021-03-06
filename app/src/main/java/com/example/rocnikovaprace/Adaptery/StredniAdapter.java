package com.example.rocnikovaprace.Adaptery;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rocnikovaprace.R;
import com.example.rocnikovaprace.Slovicka;

import java.util.List;


public class StredniAdapter extends RecyclerView.Adapter<Adapter.MyView> {


    private List<Slovicka> list;


    public static class MyView
            extends RecyclerView.ViewHolder {


        TextView textView;
        ImageView obrazek;
        CardView cardView;

        // Konstruktor s paramentrem View
        public MyView(View view) {
            super(view);


            textView = (TextView) view
                    .findViewById(R.id.textview);

            obrazek = (ImageView) view
                    .findViewById(R.id.obrazek);

            cardView = (CardView) view
                    .findViewById(R.id.cardview);


        }
    }

    //Další konstruktor
    public StredniAdapter(List<Slovicka> horizontalList) {
        this.list = horizontalList;
    }

    // Metoda, která se stará o rozložení a vzhled jednotlivých položek v seznamu
    @Override
    public Adapter.MyView onCreateViewHolder(ViewGroup parent,
                                             int viewType) {

        // Inflate item.xml using LayoutInflator
        View itemView
                = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.stredniitem,
                        parent,
                        false);

        // return itemView
        return new Adapter.MyView(itemView);
    }


    @Override
    public void onBindViewHolder(final Adapter.MyView holder,
                                 final int position) {

        holder.textView.setText(list.get(position).slovo);
        holder.obrazek.setImageBitmap(list.get(position).bitmapa);


    }

    // Vrátí délku seznamu
    @Override
    public int getItemCount() {
        return list.size();
    }


}









