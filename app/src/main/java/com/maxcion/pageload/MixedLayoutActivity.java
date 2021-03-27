package com.maxcion.pageload;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.maxcion.pageload.adapter.SimpleAdapter;
import com.maxcion.pageloadadapter.IOnLoadMoreListener;

import java.util.List;

public class MixedLayoutActivity extends BaseActivity<String> implements IOnLoadMoreListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mixedlayout);
        RecyclerView rv = findViewById(R.id.rv);
        mAdapter = new SimpleAdapter(null);
        mAdapter.setLoadMoreView(new CommonLoadMoreView());
        mAdapter.setOnLoadMoreListener(this);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position % 3 == 0 ? 2 : 1 ;
            }
        });
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(mAdapter);
        request();
    }

    @Override
    protected List<String> convertRequestData(List<String> originData) {
        return originData;
    }

    @Override
    public void onLoadMoreRequested() {
        request();
    }
}