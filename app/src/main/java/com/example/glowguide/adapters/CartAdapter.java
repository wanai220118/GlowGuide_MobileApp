package com.example.glowguide.adapters;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import com.example.glowguide.R;
import com.example.glowguide.models.CartItem;
import com.example.glowguide.models.CartManager;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private final List<CartItem> cartItems;
    private final CartChangeListener listener;

    public interface CartChangeListener {
        void onCartUpdated();
    }

    public CartAdapter(List<CartItem> cartItems, CartChangeListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.name.setText(item.getProduct().getName());
        holder.price.setText("RM " + String.format("%.2f", item.getProduct().getPrice()));
        holder.description.setText(item.getProduct().getDescription());
        holder.checkbox.setChecked(item.isSelected());
        holder.quantity.setText(String.valueOf(item.getQuantity()));

        holder.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setSelected(isChecked);
            listener.onCartUpdated();
        });

        holder.btnIncrease.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            notifyItemChanged(position);
            listener.onCartUpdated();
        });

        holder.btnDecrease.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                notifyItemChanged(holder.getAdapterPosition());
                listener.onCartUpdated();
            } else {
                Context context = holder.itemView.getContext();

                new AlertDialog.Builder(context)
                        .setTitle("Remove Item")
                        .setMessage("Are you sure you want to delete this product?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            int currentPos = holder.getAdapterPosition();
                            if (currentPos != RecyclerView.NO_POSITION && currentPos < cartItems.size()) {
                                cartItems.remove(currentPos); // only remove from adapter list
                                notifyItemRemoved(currentPos);
                                listener.onCartUpdated();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(holder.itemView.getContext())
                    .setTitle("Delete Item")
                    .setMessage("Are you sure you want to delete this product?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        int currentPos = holder.getAdapterPosition();
                        if (currentPos != RecyclerView.NO_POSITION && currentPos < cartItems.size()) {
                            cartItems.remove(currentPos); // only remove from adapter list
                            notifyItemRemoved(currentPos);
                            listener.onCartUpdated();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, description, quantity;
        CheckBox checkbox;
        Button btnIncrease, btnDecrease, btnDelete;


        CartViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.cartItemName);
            price = itemView.findViewById(R.id.cartItemPrice);
            description = itemView.findViewById(R.id.cartItemDesc);
            checkbox = itemView.findViewById(R.id.cartItemCheckbox);
            quantity = itemView.findViewById(R.id.tvQuantity);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
