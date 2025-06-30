package com.example.glowguide.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glowguide.R;
import com.example.glowguide.activities.OrderDetailActivity;
import com.example.glowguide.models.Order;
import com.example.glowguide.models.OrderItem;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Order> orders;
    private OnOrderActionListener listener;

    private Context context;

    public OrderAdapter(Context context, List<Order> orders, OnOrderActionListener listener) {
        this.context = context;
        this.orders = orders;
        this.listener = listener;
    }


    public interface OnOrderActionListener {
        void onCancelClicked(Order order);
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView productName, orderStatus, orderPrice, orderQuantity;
        Button btnCancelOrder;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            orderStatus = itemView.findViewById(R.id.orderStatus);
            orderPrice = itemView.findViewById(R.id.orderPrice);
            orderQuantity = itemView.findViewById(R.id.orderQuantity);
            btnCancelOrder = itemView.findViewById(R.id.btnCancelOrder);
        }
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);

        StringBuilder productDetails = new StringBuilder();
        int totalQty = 0;

        if (order.getItems() == null || order.getItems().isEmpty()) {
            holder.productName.setText("No items");
            holder.orderQuantity.setText("Total Items: 0");
            holder.orderPrice.setText("Total: RM 0.00");
            holder.orderStatus.setText("Status: " + order.getStatus());
            holder.btnCancelOrder.setVisibility(View.GONE);
            return;
        }

        // ✅ Correctly loop through and format product details
        for (OrderItem item : order.getItems()) {
            productDetails.append(item.getName())
                    .append(" x")
                    .append(item.getQuantity())
                    .append("\n");
            totalQty += item.getQuantity();
        }

        holder.productName.setText(productDetails.toString().trim());
        holder.orderStatus.setText("Status: " + order.getStatus());
        holder.orderQuantity.setText("Total Items: " + totalQty);
        holder.orderPrice.setText("Total: RM " + String.format("%.2f", order.getTotal()));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderDetailActivity.class);
            intent.putExtra("order", order);
            context.startActivity(intent);
        });

        if ("TO SHIP".equals(order.getStatus()) || "TO PICKUP".equals(order.getStatus())) {
            holder.btnCancelOrder.setVisibility(View.VISIBLE);
            holder.btnCancelOrder.setOnClickListener(v -> {
                if (listener != null) listener.onCancelClicked(order);
            });
        } else {
            holder.btnCancelOrder.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }
}