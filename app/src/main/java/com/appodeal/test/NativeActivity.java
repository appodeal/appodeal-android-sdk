package com.appodeal.test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class NativeActivity extends FragmentActivity {

    private static final String INTENT_NATIVE_TYPE = "intent_native_type";

    private static final int DEFAULT_PACK_SIZE = 20;

    private SwipeRefreshLayout srlUpdateNative;
    private RecyclerView rvNative;

    private int nativeTemplateType;
    private NativeAdapter nativeAdapter;
    private AppodealWrapperAdapter appodealWrapperAdapter;
    private LinearLayoutManager linearLayoutManager;
    private final Runnable addPackRunnable = new Runnable() {
        @Override
        public void run() {
            addPackToAdapter();
        }
    };


    public static Intent newIntent(Context context, int nativeType) {
        Intent intent = new Intent(context, NativeActivity.class);
        intent.putExtra(INTENT_NATIVE_TYPE, nativeType);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native);

        nativeTemplateType = getIntent().getIntExtra(INTENT_NATIVE_TYPE, 0);

        initViews();
        setupViews();
    }

    private void initViews() {
        srlUpdateNative = findViewById(R.id.srl_update_native);
        rvNative = findViewById(R.id.rv_native);
    }

    private void setupViews() {
        srlUpdateNative.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (nativeAdapter != null) {
                    if (appodealWrapperAdapter != null) {
                        appodealWrapperAdapter.destroyNativeAds();
                    }

                    nativeAdapter.clearContent();

                    addPackToAdapter();
                }

                if (srlUpdateNative != null) {
                    srlUpdateNative.setRefreshing(false);
                }
            }
        });

        nativeAdapter = new NativeAdapter(new ArrayList<>());
        appodealWrapperAdapter = new AppodealWrapperAdapter(nativeAdapter, 2, nativeTemplateType);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setSmoothScrollbarEnabled(true);

        rvNative.setAdapter(appodealWrapperAdapter);
        rvNative.setItemAnimator(new DefaultItemAnimator());
        rvNative.setLayoutManager(linearLayoutManager);
        rvNative.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    int visibleItemCount = linearLayoutManager.getChildCount();
                    int totalItemCount = linearLayoutManager.getItemCount();
                    int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount) {
                        recyclerView.post(addPackRunnable);
                    }
                }
            }
        });

        addPackToAdapter();
    }

    @Override
    protected void onDestroy() {
        if (appodealWrapperAdapter != null) {
            appodealWrapperAdapter.destroyNativeAds();
        }

        super.onDestroy();
    }

    private void addPackToAdapter() {
        List<Integer> publisherList = new ArrayList<>();

        int lastValue = nativeAdapter.getLastValue();
        for (int i = 1; i <= DEFAULT_PACK_SIZE; i++) {
            publisherList.add(i + lastValue);
        }

        nativeAdapter.addPack(publisherList);
    }

}