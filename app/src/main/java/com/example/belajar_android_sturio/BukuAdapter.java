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

public class BukuAdapter extends RecyclerView.Adapter<BukuAdapter.BukuVieHolder> {
    private Context context;
    private ArrayList<Buku> listBuku;
    private DatabaseHelper databaseHelper;

    public BukuAdapter(Context context, ArrayList<Buku> listBuku) {
        this.context = context;
        this.listBuku = listBuku;
        this.databaseHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public BukuVieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_buku, parent, false);
        return new BukuVieHolder(view);
    }

    public void onBindViewHolder(@NonNull BukuVieHolder holder, int position) {
        Buku buku = listBuku.get(position);
        holder.tvJudulBuku.setText(buku.getNama());
        holder.tvStokBuku.setText(buku.getStok() + " stok");

        holder.btnCheckout.setOnClickListener(new View.OnClickListener() {
            private Handler handler = new Handler();
            private Runnable runnable;

            @Override
            public void onClick(View v) {
                if (buku.getStok() > 0) {
                    databaseHelper.addToCheckout(buku.getId(), 1);
                    buku.setStok(buku.getStok() - 1);
                    holder.tvStokBuku.setText(buku.getStok() + " stok");

                    if (runnable != null) {
                        handler.removeCallbacks(runnable);
                    }

                    runnable = new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Berhasil menambahkan " + buku.getNama() + " ke checkout",
                                    Toast.LENGTH_SHORT).show();
                        }
                    };

                    handler.postDelayed(runnable, 500); // Menunda eksekusi toast selama 500ms
                } else {
                    Toast.makeText(context, "Stok " + buku.getNama() + " habis", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listBuku.size();
    }

    public static class BukuVieHolder extends RecyclerView.ViewHolder {
        TextView tvJudulBuku, tvStokBuku;
        ImageButton btnCheckout;

        public BukuVieHolder(@NonNull View itemView) {
            super(itemView);
            tvJudulBuku = itemView.findViewById(R.id.tv_judul_buku);
            tvStokBuku = itemView.findViewById(R.id.tv_stok_buku);
            btnCheckout = itemView.findViewById(R.id.btn_checkout);
        }
    }
}