package com.example.belajar_android_sturio;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private EditText inputName, inputStock;
    private Button btnSave;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        // Setup bottom navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.menu_add);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_home:
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    overridePendingTransition(0,0);
                    finish();
                    return true;
                case R.id.menu_add:
                    return true;
                case R.id.menu_checkout:
                    startActivity(new Intent(getApplicationContext(), PinjamActivity.class));
                    overridePendingTransition(0,0);
                    finish();
                    return true;
            }
            return false;
        });

        inputName = findViewById(R.id.input_name);
        inputStock = findViewById(R.id.input_stok);
        btnSave = findViewById(R.id.btn_save);

        databaseHelper = new DatabaseHelper(this);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = inputName.getText().toString().trim();
                String qtyStr = inputStock.getText().toString().trim();

                if (name.isEmpty() || qtyStr.isEmpty()) {
                    Toast.makeText(AddActivity.this, "Mohon isi semua field!", Toast.LENGTH_SHORT).show();
                    return;
                }

                int qty = Integer.parseInt(qtyStr);

                databaseHelper.saveOrUpdateProduct(name, qty);

                Toast.makeText(AddActivity.this, "Buku Tersimpan", Toast.LENGTH_SHORT).show();

                // Clear input fields
                inputName.setText("");
                inputStock.setText("");
            }
        });
    }
}