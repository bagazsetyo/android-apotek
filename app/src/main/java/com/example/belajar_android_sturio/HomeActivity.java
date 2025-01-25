package com.example.belajar_android_sturio;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private RecyclerView rvBuku;
    private BukuAdapter adapter;
    private ArrayList<Buku> listBuku;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Inisialisasi DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Inisialisasi RecyclerView dan ArrayList
        rvBuku = findViewById(R.id.rv_obat);
        listBuku = new ArrayList<>();
        adapter = new BukuAdapter(this, listBuku);
        rvBuku.setLayoutManager(new LinearLayoutManager(this));
        rvBuku.setAdapter(adapter);

        // Setup bottom navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.menu_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_home:
                    return true;
                case R.id.menu_add:
                    startActivity(new Intent(getApplicationContext(), AddActivity.class));
                    overridePendingTransition(0,0);
                    finish();
                    return true;
                case R.id.menu_checkout: // Menambahkan case untuk menu checkout
                    startActivity(new Intent(getApplicationContext(), CheckoutActivity.class));
                    overridePendingTransition(0,0);
                    finish();
                    return true;
            }
            return false;
        });

        // Setup swipe to delete
        ItemTouchHelper.SimpleCallback swipeToDeleteCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
//            @Override
//            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//            @Override
//            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//                int position = viewHolder.getAdapterPosition();
//                // Hapus dari database
//                Obat obat = listObat.get(position);
//                databaseHelper.deleteProduct(obat.getId());
//                // Hapus dari list
//                listObat.remove(position);
//                adapter.notifyItemRemoved(position);
//
//                showDeleteConfirmationDialog(obat, position);
//            }
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Buku buku = listBuku.get(position);

                showDeleteConfirmationDialog(buku, position);
            }
        };

        new ItemTouchHelper(swipeToDeleteCallback).attachToRecyclerView(rvBuku);

        // Load data awal
        loadObatData();
    }

    private void showDeleteConfirmationDialog(Buku obat, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi Hapus");
        builder.setMessage("Apakah Anda yakin ingin menghapus data ini?");

        // Tombol "Ya" untuk menghapus
        builder.setPositiveButton("Ya", (dialog, which) -> {
            // Hapus data dari database
            databaseHelper.deleteProduct(obat.getId());
            // Hapus dari list dan perbarui adapter
            listBuku.remove(position);
            adapter.notifyItemRemoved(position);
            Toast.makeText(this, "Data berhasil dihapus", Toast.LENGTH_SHORT).show();
        });

        // Tombol "Tidak" untuk membatalkan
        builder.setNegativeButton("Tidak", (dialog, which) -> {
            // Batalkan swipe dan kembalikan posisi item
            adapter.notifyItemChanged(position);
        });

        // Tampilkan dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void loadObatData() {
        listBuku.clear();
        Cursor cursor = databaseHelper.getAllProducts();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    int idIndex = cursor.getColumnIndexOrThrow("id");
                    int nameIndex = cursor.getColumnIndexOrThrow("name");
                    int priceIndex = cursor.getColumnIndexOrThrow("price");
                    int stokIndex = cursor.getColumnIndexOrThrow("qty");

                    long id = cursor.getLong(idIndex);
                    String nama = cursor.getString(nameIndex);
                    double harga = cursor.getDouble(priceIndex);
                    int stok = cursor.getInt(stokIndex);

                    listBuku.add(new Buku(id, nama, harga, stok));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
            cursor.close();
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadObatData();
    }
}