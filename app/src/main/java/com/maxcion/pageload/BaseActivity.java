package com.maxcion.pageload;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.maxcion.pageloadadapter.PageLoadRecyclerVewAdapter;

import java.util.List;

public abstract class BaseActivity<T> extends AppCompatActivity {
    protected PageLoadRecyclerVewAdapter mAdapter;
    protected int mFailCount ;
    protected final int PAGE_SIZE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void request() {
        NetWorkRequest.request(mAdapter.getDataSize() / PAGE_SIZE + 1, mFailCount, new NetWorkRequest.Callback() {
            @Override
            public void onSuccess(List<String> result) {
                List<T> finalResult = convertRequestData(result);
                if(result.size() >= PAGE_SIZE){
                    if (mAdapter.getDataSize() == 0){

                        mAdapter.setNewData(finalResult);
                    }else {
                        mAdapter.addDataList(finalResult);
                    }
                    mAdapter.loadMoreComplete(true);
                }else {
                    mAdapter.loadMoreEnd(true);
                }
            }

            @Override
            public void onFail() {
                mFailCount++;
                mAdapter.loadMoreFail(true);
            }
        });
    }

    protected abstract List<T> convertRequestData(List<String> originData);
}