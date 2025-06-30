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
import com.example.glowguide.adapters.CategoryAdapter;
import com.example.glowguide.adapters.ProductAdapter;
import com.example.glowguide.adapters.ProductListAdapter;
import com.example.glowguide.models.Category;
import com.example.glowguide.models.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ShopFragment extends Fragment {

    private RecyclerView recyclerViewShop;
    private TextView tvShopTitle;
    private Button btnCategories;

    private List<Category> categoryList;
    private List<Product> productList;
    private boolean showingProducts = false;

    public ShopFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shop, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerViewShop = view.findViewById(R.id.recyclerViewShop);
        tvShopTitle = view.findViewById(R.id.tvShopTitle);
        btnCategories = view.findViewById(R.id.btnCategories);

        recyclerViewShop.setLayoutManager(new LinearLayoutManager(getContext()));

        categoryList = getDummyCategories();
        productList = getDummyProducts();

        showCategories();

        btnCategories.setOnClickListener(v -> showCategories());

        //icon cart
        FloatingActionButton fabCart = view.findViewById(R.id.fabCart);
        fabCart.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.home_container, new CartFragment())
                    .addToBackStack(null)
                    .commit();
        });

    }

    private void showCategories() {
        showingProducts = false;
        tvShopTitle.setText("Categories");
        btnCategories.setVisibility(View.GONE);

        CategoryAdapter adapter = new CategoryAdapter(categoryList, category -> {
            showProductsByCategory(category.getId());
        });
        recyclerViewShop.setAdapter(adapter);
    }

    private void showProductsByCategory(int categoryId) {
        showingProducts = true;
        tvShopTitle.setText("Products");
        btnCategories.setVisibility(View.VISIBLE);

        List<Product> filtered = new ArrayList<>();
        for (Product product : productList) {
            if (product.getCategoryId() == categoryId) {
                filtered.add(product);
            }
        }

        ProductAdapter adapter = new ProductAdapter(filtered, getContext());
        recyclerViewShop.setAdapter(adapter);
    }

    private List<Category> getDummyCategories() {
        List<Category> list = new ArrayList<>();
        list.add(new Category(1, "Facial Washes", "Refresh your skin by removing dirt, oil, and makeup—your clean canvas starts here.", R.drawable.cleanser_img));
        list.add(new Category(2, "Toners", "Balance your skin and tighten pores while prepping for the next steps.", R.drawable.toner_img));
        list.add(new Category(3, "Serums", "Target skin concerns with powerful actives for visible, radiant results.", R.drawable.serum_img));
        list.add(new Category(4, "Moisturizers", "Hydrate, soften, and strengthen your skin with lasting moisture.", R.drawable.moisturizer_img));
        list.add(new Category(5, "Sunscreens", "Lightweight SPF protection to prevent sun damage and early aging.", R.drawable.suncare_img));
        list.add(new Category(6, "Skincare Sets", "Complete skincare routines tailored for different skin types.", R.drawable.setcombo));
        return list;
    }

    private List<Product> getDummyProducts() {
        List<Product> list = new ArrayList<>();
        list.add(new Product(1, "Daily Glow Facial Wash", 29.90, "Gently cleanses without stripping natural oils", 1, R.drawable.facial1));
        list.add(new Product(2, "Balance Refresh Toner", 24.90, "Restores skin pH and preps for hydration", 2, R.drawable.toner1));
        list.add(new Product(3, "Vital Radiance Serum", 54.90, "Boosts skin brightness and health", 3, R.drawable.serum1));
        list.add(new Product(4, "Lightweight Hydrating Cream", 39.90, "Nourishes and maintains skin elasticity", 4, R.drawable.moisturizer1));
        list.add(new Product(5, "UV Protect Sunscreen SPF50", 32.90, "Protects with lightweight, invisible coverage", 5, R.drawable.sunscreen1));
        list.add(new Product(6, "Hydrating Cream Cleanser", 33.90, "Creamy formula that hydrates while cleansing", 1, R.drawable.facial2));
        list.add(new Product(7, "Deep Moisture Toner", 26.90, "Rich in glycerin and hyaluronic acid", 2, R.drawable.toner2));
        list.add(new Product(8, "Hyaluronic Boost Serum", 59.90, "Delivers long-lasting hydration to deep skin layers", 3, R.drawable.serum2));
        list.add(new Product(9, "Intensive Repair Cream", 44.90, "Thick, barrier-repairing moisturizer", 4, R.drawable.moisturizer2));
        list.add(new Product(10, "Moisture Shield Sunscreen SPF50", 36.90, "Sunscreen enriched with ceramides for dry skin", 5, R.drawable.sunscreen2));
        list.add(new Product(11, "Oil Control Facial Wash", 28.90, "Removes excess sebum and prevents breakouts", 1, R.drawable.facial3));
        list.add(new Product(12, "Pore Minimizing Toner", 25.90, "Shrinks pores and reduces shine", 2, R.drawable.toner3));
        list.add(new Product(13, "Sebum Balancing Serum", 52.90, "Regulates oil production and clears blemishes", 3, R.drawable.serum3));
        list.add(new Product(14, "Oil-free Gel Moisturizer", 37.90, "Lightweight hydration without clogging pores", 4, R.drawable.moisturizer3));
        list.add(new Product(15, "Matte Finish Sunscreen SPF50", 33.90, "Non-greasy sun protection for oily skin", 5, R.drawable.sunscreen3));
        list.add(new Product(16, "Dual-zone Cleanser", 30.90, "Targets oily T-zone and dry cheeks simultaneously", 1, R.drawable.facial4));
        list.add(new Product(17, "Balancing Toner", 27.90, "Normalize oil and hydrates dry areas", 2, R.drawable.toner4));
        list.add(new Product(18, "Multi-zone Serum", 56.90, "Treats both dry and oily areas with adaptive formula", 3, R.drawable.serum4));
        list.add(new Product(19, "Adaptive Moisture Gel-cream", 42.90, "Gel-cream hydrates evenly across combination skin", 4, R.drawable.moisturizer4));
        list.add(new Product(20, "Smart Balance Sunscreen SPF50", 35.90, "Lightweight, adaptive sunscreen with matte finish", 5, R.drawable.sunscreen4));
        list.add(new Product(21, "Soothing Gentle Cleanser", 32.90, "Fragrance-free, calming face wash", 1, R.drawable.facial5));
        list.add(new Product(22, "Calming Herbal Toner", 24.90, "Reduces redness and soothes irritation", 2, R.drawable.toner5));
        list.add(new Product(23, "Barrier Repair Serum", 58.90, "Strengthens skin barrier with minimal ingredients", 3, R.drawable.serum5));
        list.add(new Product(24, "Sensitive Relief Cream", 41.90, "Anti-inflammatory moisturizer with oat extract", 4, R.drawable.moisturizer5));
        list.add(new Product(25, "Hypoallergenic Sunscreen SPF50", 34.90, "Safe, non-irritating sun protection", 5, R.drawable.sunscreen5));
        list.add(new Product(26, "GlowBalance Set", 150, "Maintains skin’s natural balance for a healthy, everyday glow.", 6, R.drawable.setnormal));
        list.add(new Product(27, "HydraCare Set", 150, "Deeply hydrates and restores moisture for smooth, soft skin.", 6, R.drawable.setdry));
        list.add(new Product(28, "OilControl Set", 150, "Controls excess oil and shine while keeping skin fresh and clear.", 6, R.drawable.setoily));
        list.add(new Product(29, "DualGlow Set", 150, "Balances oily and dry areas for a radiant, even complexion.", 6, R.drawable.setcombination));
        list.add(new Product(30, "CalmShield Set", 150, "Soothes irritation and protects sensitive, reactive skin.", 6, R.drawable.setsensitive));

        return list;
    }
}
