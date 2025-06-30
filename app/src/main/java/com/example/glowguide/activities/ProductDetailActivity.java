package com.example.glowguide.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.glowguide.R;
import com.example.glowguide.models.CartManager;
import com.example.glowguide.models.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ProductDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        Product product = (Product) getIntent().getSerializableExtra("product");
//        if (product != null) {
//            // set values to UI
//        }

        //Views
        TextView name = findViewById(R.id.detailProductName);
        TextView price = findViewById(R.id.detailProductPrice);
        TextView desc = findViewById(R.id.detailProductDesc);
        Button btnAddToCart = findViewById(R.id.btnAddToCart);
        ImageView productImage = findViewById(R.id.productImage);

        Glide.with(this)
                .load(product.getImageResId())
                .placeholder(R.drawable.placeholder_image)
                .into(productImage);


        if (product != null) {
            name.setText(product.getName());
            price.setText("RM " + String.format("%.2f", product.getPrice()));
            desc.setText(product.getDescription());

            btnAddToCart.setOnClickListener(v -> {
                CartManager.getInstance().addToCart(product);
                Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(this, "No product received!", Toast.LENGTH_SHORT).show();
        }

        //cart
        FloatingActionButton fabCart = findViewById(R.id.fabCart);
        fabCart.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class); // Assuming CartFragment is shown from MainActivity
            intent.putExtra("open_cart", true);
            startActivity(intent);
        });

        int[] quantity = {1};
        TextView tvQuantity = findViewById(R.id.tvQuantity);
        Button btnIncrease = findViewById(R.id.btnIncrease);
        Button btnDecrease = findViewById(R.id.btnDecrease);

        btnIncrease.setOnClickListener(v -> {
            quantity[0]++;
            tvQuantity.setText(String.valueOf(quantity[0]));
        });

        btnDecrease.setOnClickListener(v -> {
            if (quantity[0] > 1) {
                quantity[0]--;
                tvQuantity.setText(String.valueOf(quantity[0]));
            }
        });

        btnAddToCart.setOnClickListener(v -> {
            for (int i = 0; i < quantity[0]; i++) {
                CartManager.getInstance().addToCart(product);
            }
            Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show();
        });

    }
}