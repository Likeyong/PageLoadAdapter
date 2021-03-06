package com.maxcion.pageloadadapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.ViewGroup;


import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * LinearLayoutManager 和GridLayoutManager 两种使用情况下,继承这个adapter, 然后 只用实现convert(BaseViewHolder holder, T item)和 getItemLayoutId() 两个方法
 * getItemLayoutId()  返回 item 的布局就好了
 * convert(BaseViewHolder holder, T item)  里面进行数据和view 的绑定就好了, 获取view 通过 holder.getView(viewId)
 * <p>
 * 只要给该adapter 设置了 ALoadMoreView 和 IonLoadMoreListener 就代表 当前recyclerView 支持 上拉加载更多
 * 即  setLoadMoreView(ALoadMoreView loadMoreView)  和  setOnLoadMoreListener(IonLoadMoreListener loadMoreListener)
 * <p>
 * 当前一页数据加载成功后 调用 loadMoreComplete()
 * 如果当前分页加载数据失败了 调用 loadMoreFail()
 * 如果已经确定数据全部加载了,调用loadMoreEnd()  代表了之后再滑动到底部  不会在进行加载更多了
 * <p>
 * 下拉刷新成功  通过setNewData(List<T> newDataList) 进行数据源刷新, 并且会自动判断 是否开启上拉加载更多
 * (如果ALoadMoreView 和 IonLoadMoreListener 都不为空,且数据超过了一个屏幕)
 *

 * @Date: 2020/6/3 11:01
 */


public abstract class PageLoadRecyclerVewAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {

    protected static final int LOADING_VIEW = 0x00000111;
    protected Context mContext;
    protected List<T> mDataList;

    private ALoadMoreView mLoadMoreView;
    private IOnLoadMoreListener mLoadMoreListener;

    private boolean mStopLoadMore;
    private RecyclerView mRecyclerView;
    private boolean mEnableLoadMore;

    public PageLoadRecyclerVewAdapter(List<T> dataList) {
        mDataList = dataList == null ? new ArrayList<>() : dataList;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        BaseViewHolder holder = null;
        switch (viewType) {
            case LOADING_VIEW:
                holder = getLoadingView(parent);
                break;
            default:
                holder = getBaseViewHolderByType(parent, viewType);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = holder.getItemViewType();
        autoLoadMore(position);
        switch (viewType) {
            default:
                convert(holder, getItem(position));
                break;

            case LOADING_VIEW:
                mLoadMoreView.convert(holder);
                break;
        }
        if (position >= getItemCount() - 1 && mLoadMoreView != null && mLoadMoreListener != null && mLoadMoreView.getState() != ALoadMoreView.STATE_LOAD_MORE_END) {
            mLoadMoreView.setState(ALoadMoreView.STATE_LOAD_MORE_DEFAULT);
        }

    }

    protected abstract void convert(BaseViewHolder holder, T item);

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : getLoadMoreViewCount() + mDataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position >= mDataList.size() ? LOADING_VIEW : getDefItemViewType(position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            GridLayoutManager.SpanSizeLookup spanSizeLookup = gridLayoutManager.getSpanSizeLookup();
            int spanCount = gridLayoutManager.getSpanCount();
            if (spanSizeLookup != null) {
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return getItemViewType(position) == LOADING_VIEW ? spanCount : spanSizeLookup.getSpanSize(position);
                    }
                });
            }
        }

        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                autoSetLoadMoreEnable();
            }
        });
    }

    /* start********************************************对外接口*********************************************/

    public void setNewData(List<T> newDataList) {
        setNewData(newDataList, false);
    }

    public void setNewData(List<T> newDataList, boolean usePost) {
        List<T> datas = newDataList == null ? new ArrayList<>() : newDataList;
        mDataList.clear();
        mDataList.addAll(datas);
        notifyDataSetChanged();
        // 这里取消在 post 内部调用autoSetLoadMoreEnable()的原因是因为
        //在 加载第一页数据时 ,返回的数据不足 一页PAGE_SIZE 时 通过 setNewData()进行数据源设置
        //然后紧接着就会调用 loadMoreEnd() 函数 停止分页加载, 如果这里post 执行autoSetLoadMoreEnable(),
        //autoSetLoadMoreEnable() 函数就会在 loadMoreEnd() 执行后执行,导致分页加载状态错误

        if (usePost) {
            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {

                    autoSetLoadMoreEnable();
                }
            });
        } else {
            autoSetLoadMoreEnable();
        }
    }

    public void addDataList(List<T> dataList) {
        if (dataList != null && dataList.size() > 0) {
            int dataSize = getDataSize();
            mDataList.addAll(dataList);
            notifyItemRangeInserted(dataSize, dataList.size());
        }
    }

    public List<T> getDataList() {
        return mDataList;
    }

    public void closeLoadMore() {
        if (mLoadMoreView != null && mLoadMoreListener != null && getLoadMoreViewCount() != 0) {
            notifyItemRemoved(getItemCount());
            mLoadMoreView = null;
        }
    }


    public T getItem(int position) {
        if (position >= 0 && position < mDataList.size())
            return mDataList.get(position);
        else
            return null;
    }


    public void setOnLoadMoreListener(IOnLoadMoreListener loadMoreListener) {
        mLoadMoreListener = loadMoreListener;
    }

    public void setLoadMoreView(ALoadMoreView loadMoreView) {
        mLoadMoreView = loadMoreView;
    }

    public int getDataSize() {
        return mDataList.size();
    }

    /**
     * 如果用到分页加载 最好使用带有参数的 函数 并且 传参 传 true,不然  可能出现 footerView 显示异常问题
     */
    @Deprecated
    public void loadMoreComplete() {
        loadMoreComplete(false);
    }

    public void loadMoreComplete(boolean usePost) {
        if (usePost && mRecyclerView != null) {
            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    setLoadMoreComplete();
                }
            });
        } else {
            setLoadMoreComplete();
        }

    }

    /**
     * 如果用到分页加载 最好使用带有参数的 函数 并且 传参 传 true,不然  可能出现 footerView 显示异常问题
     */
    @Deprecated
    public void loadMoreFail() {
        loadMoreFail(false);
    }

    public void loadMoreFail(boolean usePost) {
        if (usePost && mRecyclerView != null) {
            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    setLoadMoreFail();
                }
            });
        } else {
            setLoadMoreFail();
        }

    }

    /**
     * 如果用到分页加载 最好使用带有参数的 函数 并且 传参 传 true,不然  可能出现 footerView 显示异常问题
     */
    @Deprecated
    public void loadMoreEnd() {
        loadMoreEnd(false);
    }

    public void loadMoreEnd(boolean withPost) {
        if (withPost && mRecyclerView != null) {
            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    setLoadMoreEnd();
                }
            });

        } else {
            setLoadMoreEnd();
        }
    }

    //根据现有数据源获取下一页的页码
    public int getPageNo(int pageSize) {
        if (getDataSize() == 0) {
            return 1;
        } else if (getDataSize() < pageSize) {
            return 2;
        }
        return getDataSize() / pageSize + 1;
    }

    /* end********************************************对外接口*********************************************/

    /* start********************************************内部使用函数*********************************************/

    private void setLoadMoreFail() {
        mLoadMoreView.setState(ALoadMoreView.STATE_LOAD_MORE_FAIL);
        mStopLoadMore = false;
        notifyItemChanged(getDataSize());
    }

    private void setLoadMoreComplete() {
        mStopLoadMore = false;
        mLoadMoreView.setState(ALoadMoreView.STATE_LOAD_MORE_DEFAULT);
        notifyItemChanged(getDataSize());
    }

    private void setLoadMoreEnd() {
        mStopLoadMore = true;
        mLoadMoreView.setState(ALoadMoreView.STATE_LOAD_MORE_END);
        notifyItemChanged(getDataSize());
    }

    private void autoSetLoadMoreEnable() {
        if (mLoadMoreView != null && mLoadMoreListener != null) {
            mStopLoadMore = false;
            mLoadMoreView.setState(ALoadMoreView.STATE_LOAD_MORE_DEFAULT);
            setLoadMoreEnable(isFullScreen());
        }
    }

    private void setLoadMoreEnable(boolean enable) {
        int oldLoadMoreViewCount = getLoadMoreViewCount();
        mEnableLoadMore = enable;
        int newLoadMoreViewCount = getLoadMoreViewCount();

        if (oldLoadMoreViewCount == 1) {
            if (newLoadMoreViewCount == 0) {
                notifyItemRemoved(getItemCount());
            }
        } else {
            if (newLoadMoreViewCount == 1) {
                mLoadMoreView.setState(ALoadMoreView.STATE_LOAD_MORE_DEFAULT);
                notifyItemInserted(getItemCount());
            }
        }
    }

    /**
     * 判断当前数据是否超过一屏幕
     *

     * @Date: 2020/6/3 11:19
     */

    public boolean isFullScreen() {
        boolean result = true;
        if (mRecyclerView != null) {
            RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                int lastCompletelyVisibleItemPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                int firstCompletelyVisibleItemPosition = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                int itemCount = getItemCount();
                result = firstCompletelyVisibleItemPosition != 0 || lastCompletelyVisibleItemPosition + 1 < itemCount;

            }
        }
        return result;
    }

    /**
     * 通过position 判断是否回调加载更多
     *

     * @Date: 2020/6/3 11:14
     */

    private void autoLoadMore(int position) {
        //如果loadMoreViewCount 为0 直接不回调
        if (getLoadMoreViewCount() == 0) {
            return;
        }
        //如果数据没到最后一条数据 直接不回调
        if (position < getItemCount() - 1) {
            return;
        }

        //如果没有设置加载更多相关属性 直接不回调
        if (mLoadMoreView == null || mLoadMoreListener == null) {
            return;
        }

        // mLoadMoreView.getState() 在onBindViewHolder 绑定最后一条数据的时候设置为STATE_LOAD_MORE_DEFAULT
        //如果mStopLoadMore = true (调用了loadMoreEnd()) 或者mLoadMoreView.getState() != ALoadMoreView.STATE_LOAD_MORE_DEFAULT
        //就不走分页逻辑
        if (mStopLoadMore || mLoadMoreView.getState() != ALoadMoreView.STATE_LOAD_MORE_DEFAULT) {

            return;
        }
        //将当前状态设置为 加载中状态
        mLoadMoreView.setState(ALoadMoreView.STATE_LOAD_MORE_LOADING);

        if (!mStopLoadMore && mLoadMoreListener != null) {
            //标记当前正在加载更多, 之后不要重复回调, 会在loadMoreFail和loadMoreComplete 的时候设置为false 代表可以继续加载更多
            mStopLoadMore = true;
            if (getRecyclerView() != null) {
                getRecyclerView().post(new Runnable() {
                    @Override
                    public void run() {
                        mLoadMoreListener.onLoadMoreRequested();
                    }
                });
            } else {
                mLoadMoreListener.onLoadMoreRequested();
            }
        }
    }

    private RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    /**
     * 判断当前是否有上拉加载更多
     *

     * @Date: 2020/6/3 11:19
     */

    private int getLoadMoreViewCount() {

        int count = 0;
        if (mLoadMoreView != null && mLoadMoreListener != null && mEnableLoadMore) {
            count = 1;
        }

        return getDataSize() == 0 ? 0 : count;
    }

    private BaseViewHolder getLoadingView(ViewGroup parent) {

        return new BaseViewHolder(LayoutInflater.from(parent.getContext()).inflate(mLoadMoreView.getLayoutId(), parent, false));
    }


    /* end********************************************内部使用函数*********************************************/


    protected int getDefItemViewType(int position) {
        return super.getItemViewType(position);
    }

    protected abstract int getItemLayoutId();

    protected BaseViewHolder getBaseViewHolderByType(ViewGroup parent, int viewType) {
        BaseViewHolder holder;
        holder = BaseViewHolder.getHolder(parent.getContext(), parent, getItemLayoutId());
        return holder;
    }
}
