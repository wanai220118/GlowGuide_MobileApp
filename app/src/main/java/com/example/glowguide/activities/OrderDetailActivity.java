package com.example.glowguide.activities;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.glowguide.R;
import com.example.glowguide.models.Order;
import com.example.glowguide.models.OrderItem;
import android.view.View;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrderDetailActivity extends AppCompatActivity {

    TextView tvStatus, tvPayment, tvTotal, tvItems, tvReason, tvOrderId, tvOrderDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        tvStatus = findViewById(R.id.tvStatus);
        tvPayment = findViewById(R.id.tvPaymentMethod);
        tvTotal = findViewById(R.id.tvTotal);
        tvItems = findViewById(R.id.tvItemList);
        tvReason = findViewById(R.id.tvCancelReason);
        tvOrderId = findViewById(R.id.tvOrderId);
        tvOrderDate = findViewById(R.id.tvOrderDate);

        Order order = (Order) getIntent().getSerializableExtra("order");

        if (order != null) {
            tvStatus.setText("Status: " + order.getStatus());
            tvPayment.setText("Payment Method: " + order.getPaymentMethod());
            tvTotal.setText("Total: RM " + String.format("%.2f", order.getTotal()));
            tvOrderId.setText("Order ID: " + (order.getId() != null ? order.getId() : "-"));
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
            tvOrderDate.setText("Order Date: " + (order.getOrderDate() != null ? sdf.format(order.getOrderDate()) : "-"));

            StringBuilder itemsText = new StringBuilder();
            List<OrderItem> items = order.getItems();
            if (items != null) {
                for (OrderItem item : items) {
                    itemsText.append(item.getName()).append(" x").append(item.getQuantity()).append("\n");
                }
            }
            tvItems.setText(itemsText.toString().trim());

            if ("CANCELLED".equals(order.getStatus())) {
                tvReason.setText("Reason: " + order.getCancelReason());
                tvReason.setVisibility(View.VISIBLE);
            } else {
                tvReason.setVisibility(View.GONE);
            }
        }
    }

}