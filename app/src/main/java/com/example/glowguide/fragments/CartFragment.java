package com.example.glowguide.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.glowguide.R;
import com.example.glowguide.adapters.CartAdapter;
import com.example.glowguide.models.CartManager;

public class CartFragment extends Fragment implements CartAdapter.CartChangeListener {

    private RecyclerView recyclerView;
    private TextView totalText;
    private Button checkoutButton;
    private CartAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        recyclerView = view.findViewById(R.id.cartRecycler);
        totalText = view.findViewById(R.id.totalAmount);
        checkoutButton = view.findViewById(R.id.btnCheckout);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        CartAdapter adapter = new CartAdapter(CartManager.getInstance().getCartItems(), this);
        recyclerView.setAdapter(adapter);

        updateTotal(); // Initial total

        checkoutButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.home_container, new CheckoutFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    @Override
    public void onCartUpdated() {
        updateTotal();
    }

    private void updateTotal() {
        double total = CartManager.getInstance().getTotalPrice();
        totalText.setText("Total: RM " + String.format("%.2f", total));
    }
}
