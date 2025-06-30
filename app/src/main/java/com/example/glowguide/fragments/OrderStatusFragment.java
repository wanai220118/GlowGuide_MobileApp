package com.example.glowguide.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glowguide.R;
import com.example.glowguide.adapters.OrderAdapter;
import com.example.glowguide.models.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class OrderStatusFragment extends Fragment {

    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private List<Order> orderList = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseUser user;
    private String statusFilter = "TO SHIP"; // default

    public OrderStatusFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_status, container, false);

        recyclerView = view.findViewById(R.id.recyclerOrder);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        statusFilter = getArguments() != null ? getArguments().getString("status", "TO SHIP") : "TO SHIP";

        adapter = new OrderAdapter(requireContext(), orderList, this::showCancelDialog);
        recyclerView.setAdapter(adapter);

        loadOrders();

        return view;
    }

    private void loadOrders() {
        if (user == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            Log.e("ORDER_TRACK", "FirebaseUser is null");
            return;
        }

        Log.d("ORDER_TRACK", "Current UID: " + user.getUid());
        Log.d("ORDER_TRACK", "Filtering by status: " + statusFilter);

        Query query = db.collection("orders").whereEqualTo("userId", user.getUid());

        if (!"ALL".equalsIgnoreCase(statusFilter)) {
            if ("TO PICKUP".equalsIgnoreCase(statusFilter)) {
                query = query.whereEqualTo("status", "TO PICKUP");
            } else {
                query = query.whereEqualTo("status", statusFilter);
            }
        }

        query.get()
                .addOnSuccessListener(querySnapshot -> {
                    orderList.clear();
                    if (querySnapshot.isEmpty()) {
                        Log.d("ORDER_TRACK", "No orders found.");
                        Toast.makeText(getContext(), "No orders found.", Toast.LENGTH_SHORT).show();
                    }

                    for (DocumentSnapshot doc : querySnapshot) {
                        try {
                            Order order = doc.toObject(Order.class);
                            if (order != null && order.getItems() != null && !order.getItems().isEmpty()) {
                                order.setId(doc.getId());
                                orderList.add(order);
                            } else {
                                Log.w("ORDER_TRACK", "Skipped order with null/empty items");
                            }
                        } catch (Exception e) {
                            Log.e("ORDER_TRACK", "Error converting to Order", e);
                        }
                    }

                    orderList.sort((o1, o2) -> {
                        if (o1.getOrderDate() == null || o2.getOrderDate() == null) return 0;
                        return o2.getOrderDate().compareTo(o1.getOrderDate());
                    });

                    adapter.notifyDataSetChanged();

                    Log.d("ORDER_TRACK", "Orders loaded: " + orderList.size());
                })
                .addOnFailureListener(e -> {
                    Log.e("ORDER_TRACK", "Error fetching orders", e);
                    Toast.makeText(getContext(), "Failed to load orders.", Toast.LENGTH_SHORT).show();
                });
    }

    private void showCancelDialog(Order order) {
        String[] reasons = {
                "Changed my mind",
                "Found a better price elsewhere",
                "Ordered by mistake",
                "Shipping is too slow",
                "Other"
        };

        new AlertDialog.Builder(getContext())
                .setTitle("Cancel Order")
                .setSingleChoiceItems(reasons, -1, null)
                .setPositiveButton("Confirm", (dialog, which) -> {
                    int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                    if (selectedPosition != -1) {
                        String reason = reasons[selectedPosition];

                        db.collection("orders")
                                .document(order.getId())
                                .update("status", "CANCELLED", "cancelReason", reason)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Order cancelled.", Toast.LENGTH_SHORT).show();
                                    loadOrders();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Failed to cancel order.", Toast.LENGTH_SHORT).show();
                                    Log.e("CANCEL_ORDER", "Error:", e);
                                });
                    } else {
                        Toast.makeText(getContext(), "Please select a reason.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Back", null)
                .show();
    }

    public static OrderStatusFragment newInstance(String status) {
        OrderStatusFragment fragment = new OrderStatusFragment();
        Bundle args = new Bundle();
        args.putString("status", status);
        fragment.setArguments(args);
        return fragment;
    }
}
