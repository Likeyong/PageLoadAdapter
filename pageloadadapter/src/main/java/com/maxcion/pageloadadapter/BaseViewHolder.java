package com.maxcion.pageloadadapter;

import android.content.Context;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


public class BaseViewHolder extends RecyclerView.ViewHolder {
    private View mContentView;
    private final SparseArray<View> mViews;

    public BaseViewHolder(View itemView) {
        super(itemView);
        mContentView = itemView;
        mViews = new SparseArray<View>();
    }

    public static BaseViewHolder getHolder(Context context, ViewGroup parent, int layoutId) {
        View itemView = LayoutInflater.from(context).inflate(layoutId, parent, false);

        return new BaseViewHolder(itemView);
    }

    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mContentView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public BaseViewHolder setGone(int viewId, boolean gone) {
        getView(viewId).setVisibility(gone ? View.GONE : View.VISIBLE);
        return this;

    }

    public BaseViewHolder setText(int viewId, String content) {
        TextView textView = getView(viewId);
        textView.setText(content);
        return this;
    }

    public BaseViewHolder setImageResource(int viewId, int imageResId) {
        ImageView view = getView(viewId);
        view.setImageResource(imageResId);
        return this;
    }

    public void setItemClickListener(View.OnClickListener listener) {
        mContentView.setOnClickListener(listener);
    }

    public void setViewClickListener(int viewId, View.OnClickListener clickListener) {
        getView(viewId).setOnClickListener(clickListener);
    }


}
