package com.maxcion.pageload;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.maxcion.pageload.adapter.SimpleAdapter;
import com.maxcion.pageloadadapter.IOnLoadMoreListener;

import java.util.List;

public class SingleColumnActivity extends BaseActivity<String> implements IOnLoadMoreListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_column);
        RecyclerView rv = findViewById(R.id.rv);
        mAdapter = new SimpleAdapter(null);
        mAdapter.setLoadMoreView(new CommonLoadMoreView());
        mAdapter.setOnLoadMoreListener(this);
        rv.setAdapter(mAdapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
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