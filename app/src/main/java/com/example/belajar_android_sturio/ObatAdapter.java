package com.example.belajar_android_sturio;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class ObatAdapter extends RecyclerView.Adapter<ObatAdapter.ObatViewHolder> {
    private Context context;
    private ArrayList<Obat> listObat;
    private DatabaseHelper databaseHelper;

    public ObatAdapter(Context context, ArrayList<Obat> listObat) {
        this.context = context;
        this.listObat = listObat;
        this.databaseHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public ObatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_obat, parent, false);
        return new ObatViewHolder(view);
    }

    public void onBindViewHolder(@NonNull ObatViewHolder holder, int position) {
        Obat obat = listObat.get(position);
        holder.tvNamaObat.setText(obat.getNama());
        holder.tvHargaObat.setText("Rp. " + obat.getHarga());
        holder.tvStokObat.setText(obat.getStok() + " stok");

        holder.btnCheckout.setOnClickListener(new View.OnClickListener() {
            private Handler handler = new Handler();
            private Runnable runnable;

            @Override
            public void onClick(View v) {
                if (obat.getStok() > 0) {
                    databaseHelper.addToCheckout(obat.getId(), 1);
                    obat.setStok(obat.getStok() - 1);
                    holder.tvStokObat.setText(obat.getStok() + " stok");

                    if (runnable != null) {
                        handler.removeCallbacks(runnable);
                    }

                    runnable = new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Berhasil menambahkan " + obat.getNama() + " ke checkout",
                                    Toast.LENGTH_SHORT).show();
                        }
                    };

                    handler.postDelayed(runnable, 500); // Menunda eksekusi toast selama 500ms
                } else {
                    Toast.makeText(context, "Stok " + obat.getNama() + " habis", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listObat.size();
    }

    public static class ObatViewHolder extends RecyclerView.ViewHolder {
        TextView tvNamaObat, tvHargaObat, tvStokObat;
        ImageButton btnCheckout;

        public ObatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNamaObat = itemView.findViewById(R.id.tv_nama_obat);
            tvHargaObat = itemView.findViewById(R.id.tv_harga_obat);
            tvStokObat = itemView.findViewById(R.id.tv_stok_obat);
            btnCheckout = itemView.findViewById(R.id.btn_checkout);
        }
    }
}