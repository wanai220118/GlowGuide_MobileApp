package com.example.glowguide.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Date;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.glowguide.R;
import com.example.glowguide.activities.MainActivity;
import com.example.glowguide.models.CartItem;
import com.example.glowguide.models.CartManager;
import com.example.glowguide.models.Order;
import com.example.glowguide.models.OrderItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CheckoutFragment extends Fragment {
    private RadioGroup paymentOptions;
    private EditText addressInput;
    private Button confirmButton;
    private TextView summaryItems, summaryPayment, summaryAddress, summaryTotal, summaryTitle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checkout, container, false);

        paymentOptions = view.findViewById(R.id.paymentOptions);
        addressInput = view.findViewById(R.id.addressInput);
        confirmButton = view.findViewById(R.id.btnConfirm);

        summaryTitle = view.findViewById(R.id.summaryTitle);
        summaryItems = view.findViewById(R.id.summaryItems);
        summaryPayment = view.findViewById(R.id.summaryPayment);
        summaryAddress = view.findViewById(R.id.summaryAddress);
        summaryTotal = view.findViewById(R.id.summaryTotal);

        updateSummary(); // Initial display

        addressInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                updateSummary();
            }
        });

        paymentOptions.setOnCheckedChangeListener((group, checkedId) -> {
            addressInput.setVisibility(checkedId == R.id.optionCod ? View.VISIBLE : View.GONE);
            updateSummary();
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                new AlertDialog.Builder(getContext())
                        .setTitle("Cancel Checkout?")
                        .setMessage("Are you sure you want to cancel this order?")
                        .setPositiveButton("Yes", (dialog, which) -> requireActivity().getSupportFragmentManager().popBackStack())
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        confirmButton.setOnClickListener(v -> {
            int selectedId = paymentOptions.getCheckedRadioButtonId();

            if (selectedId == -1) {
                Toast.makeText(getContext(), "Please select a payment method.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedId == R.id.optionCod && addressInput.getText().toString().trim().isEmpty()) {
                Toast.makeText(getContext(), "Please enter address for COD.", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean hasSelection = false;
            for (CartItem item : CartManager.getInstance().getCartItems()) {
                if (item.isSelected()) {
                    hasSelection = true;
                    break;
                }
            }

            if (!hasSelection) {
                Toast.makeText(getContext(), "Please select at least one item to checkout.", Toast.LENGTH_SHORT).show();
                return;
            }

            ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Placing your order...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            new Handler().postDelayed(() -> {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                if (user != null) {
                    List<OrderItem> orderedItems = new ArrayList<>();
                    double total = 0;

                    for (CartItem item : CartManager.getInstance().getCartItems()) {
                        if (item.isSelected()) {
                            OrderItem orderItem = new OrderItem(
                                    item.getProduct().getName(),
                                    item.getQuantity(),
                                    item.getProduct().getPrice()
                            );
                            orderedItems.add(orderItem);
                            total += item.getTotalPrice();
                        }
                    }

                    String status = selectedId == R.id.optionPickup ? "TO PICKUP" : "TO SHIP";

                    String paymentMethod = selectedId == R.id.optionCod ? "Cash on Delivery" : "Pay at Counter";

                    Order newOrder = new Order(orderedItems, status, user.getUid(), total, paymentMethod, new Date());

                    db.collection("orders").add(newOrder)
                            .addOnSuccessListener(documentReference -> Log.d("ORDER_SAVE", "Order saved with ID: " + documentReference.getId()))
                            .addOnFailureListener(e -> Log.e("ORDER_SAVE", "Error saving order", e));
                }

                progressDialog.dismiss();
                CartManager.getInstance().clearCart();
                Toast.makeText(getContext(), "Order placed successfully!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(requireContext(), MainActivity.class);
                intent.putExtra("open_home", true);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                requireActivity().finish();
            }, 2000);
        });

        return view;
    }

    private void updateSummary() {
        StringBuilder itemSummary = new StringBuilder();
        double total = 0;
        int itemCount = 0;

        for (CartItem item : CartManager.getInstance().getCartItems()) {
            if (item.isSelected()) {
                itemSummary.append(item.getProduct().getName())
                        .append(" x")
                        .append(item.getQuantity())
                        .append(" - RM ")
                        .append(String.format("%.2f", item.getTotalPrice()))
                        .append("\n");
                total += item.getTotalPrice();
                itemCount += item.getQuantity();
            }
        }

        summaryTitle.setText("Order Summary (" + itemCount + " items)");
        summaryItems.setText(itemSummary.toString());
        summaryTotal.setText("Total: RM " + String.format("%.2f", total));

        int selectedId = paymentOptions.getCheckedRadioButtonId();
        if (selectedId == R.id.optionCod) {
            summaryPayment.setText("Payment Method: Cash on Delivery");
            summaryAddress.setText("Address: " + addressInput.getText().toString() + "\nEstimated Delivery: 2–3 days");
        } else if (selectedId == R.id.optionPickup) {
            summaryPayment.setText("Payment Method: Pickup");
            summaryAddress.setText("Address: -");
        } else {
            summaryPayment.setText("Payment Method: (Not Selected)");
            summaryAddress.setText("Address: -");
        }
    }
}
