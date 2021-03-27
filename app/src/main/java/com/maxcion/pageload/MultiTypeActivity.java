package com.maxcion.pageload;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.maxcion.pageload.multi.MultiData;
import com.maxcion.pageload.multi.MultiPageLoadAdapter;
import com.maxcion.pageloadadapter.IOnLoadMoreListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MultiTypeActivity extends BaseActivity<MultiData> implements IOnLoadMoreListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_type);
        RecyclerView recyclerView = findViewById(R.id.rv);
        mAdapter = new MultiPageLoadAdapter(null);
        mAdapter.setLoadMoreView(new CommonLoadMoreView());
        mAdapter.setOnLoadMoreListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);
        request();
    }

    @Override
    protected List<MultiData> convertRequestData(List<String> originData) {
        Random random = new Random();

        List<MultiData> result = new ArrayList<>();
        for (String item : originData){
            int i = random.nextInt(3);
            result.add(new MultiData(i, item));
        }
        return result;
    }

    @Override
    public void onLoadMoreRequested() {
        request();
    }
}