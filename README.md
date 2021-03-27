# PageLoadAdapter

RecyclerView几乎在每个app里面都有被使用，但凡使用了列表就会采用分页加载进行数据请求和加载。android 官方也推出了分页库，但是感觉只有kotlin一起使用才能体会到酸爽。Java 版本的也有很多很强大的第三方库，BaseRecyclerViewAdapterHelper这个库是我用起来最顺手的分页库，里面也包含了各式各样强大的功能：分组、拖动排序、动画，因为功能强大，代码量也相对比较大。 但是很多时候我们想要的就是分页加载，所以参照BaseRecyclerViewAdapterHelper写下了这个分页加载库，只有分页功能。（可以说照搬，也可以说精简，但是其中也加入个人理解）。
这个库相对BaseRecyclerViewAdapterHelper只有两个优点：
* 代码量小
* BaseRecyclerViewAdapterHelper 在数据不满一屏时仍然显示加载更多以及页面初始化时都会显示loadmoewView（虽然提供了api进行隐藏，但是看了很长时间注释和文档都没了解该怎么使用），而这个库在初次加载和不满一屏数据时不会显示loadmoreView

gradle引用
>    implementation 'com.maxcion:pageloadadapter:1.0.0'

项目地址：https://github.com/Likeyong/PageLoadAdapter


![single.gif](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/86336cc7a75e4c8ba510f8d5b7f0d639~tplv-k3u1fbpfcp-watermark.image)


![mixed.gif](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/f73ba22722d34dd68030fce6806c4cb5~tplv-k3u1fbpfcp-watermark.image)


![mult.gif](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/2a6ce605309345919e496c7051bbff35~tplv-k3u1fbpfcp-watermark.image)

## 单列分页加载
```
//一定要在PageLoadRecyclerVewAdapter<String> 的泛型参数里面指定数据源item格式
public class SimpleAdapter extends PageLoadRecyclerVewAdapter<String> {
    public SimpleAdapter(List<String> dataList) {
        super(dataList);
    }

    //这里进行 数据绑定
    @Override
    protected void convert(BaseViewHolder holder, String item) {
        holder.setText(R.id.text, item);
    }

    //这里返回布局item id
    @Override
    protected int getItemLayoutId() {
        return R.layout.item_simple;
    }
}

```
第一步 adapter实现好了，现在需要打开adapter的分页加载功能

```
public class SingleColumnActivity extends BaseActivity<String> implements IOnLoadMoreListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_column);
        RecyclerView rv = findViewById(R.id.rv);
        //实例化adapter
        mAdapter = new SimpleAdapter(null);
        //给adapter 设置loadmoreview
        mAdapter.setLoadMoreView(new CommonLoadMoreView());
        //设置滑动到底部时进行更多加载的回调
        mAdapter.setOnLoadMoreListener(this);
        rv.setAdapter(mAdapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
        request();
    }



    @Override
    public void onLoadMoreRequested() {

        request();
    }

    //这个函数不用管
    @Override
    protected List<String> convertRequestData(List<String> originData) {
        return originData;
    }


}
```

第二步，RecyclerView也打开了分页加载功能，第三部就是根据接口返回的数据判断到底是 加载失败了、加成成功了还是加载结束（没有更多数据需要加载）

```
protected void request() {
        NetWorkRequest.request(mAdapter.getDataSize() / PAGE_SIZE + 1, mFailCount, new NetWorkRequest.Callback() {
            @Override
            public void onSuccess(List<String> result) {
                List<T> finalResult = convertRequestData(result);
                if(result.size() >= PAGE_SIZE){// 接口返回了满满一页的数据，这里数据加载成功
                    if (mAdapter.getDataSize() == 0){
                        //当前列表里面没有数据，代表是初次请求，所以这里使用setNewData（）

                        mAdapter.setNewData(finalResult);
                    }else {
                        //列表里面已经有数据了，这里使用addDataList（），将数据添加到列表后面
                        mAdapter.addDataList(finalResult);
                    }
                    //这里调用adapter。loadMoreComplete（true） 函数通知列表刷新footview， 这里参数一定要传true
                    mAdapter.loadMoreComplete(true);
                }else {
                    //如果接口返回的数据不足一页，也就代表没有足够的数据了，那么也就没有下一页数据，所以这里
                    //认定分页加载结束
                    //这里的参数也一定要传true
                    mAdapter.loadMoreEnd(true);
                }
            }

            @Override
            public void onFail() {
                mFailCount++;
                //请求失败 通知recyclerview 刷新footview 状态
                mAdapter.loadMoreFail(true);
            }
        });
    }
```
上面是我写的模拟接口请求，不用在意其他代码，只要关注onSuccess 和onFail 两个回调里面的逻辑。

### 混合布局的支持
在电商行业经常能看到商品列表中，同一个列表，有的商品占满整整一行，有的一行显示2-3个商品。这种实现方案就是通过GridLayoutManager 的SpanSizeLookup 来控制每个item占几列的。
```
 RecyclerView rv = findViewById(R.id.rv);
        mAdapter = new SimpleAdapter(null);
        mAdapter.setLoadMoreView(new CommonLoadMoreView());
        mAdapter.setOnLoadMoreListener(this);
      //这里我们将列表设置最多两列
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
              //根据position 设置每个item应该占几列
              //如果当前的position是3的整数倍 我们就让他占满2列，其他的只占1列
                return position % 3 == 0 ? 2 : 1 ;
            }
        });
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(mAdapter);
```

### RecyclerView多Type支持
如果要使用多type, 在写Adapter的时候要继承PageLoadMultiRecyclerViewAdapter<T, BaseViewHolder>，其中T 是数据源item类型，这个类型必须实现  IMultiItem 接口，并在getItemType（）函数中返回当前item对应的type
```
public class MultiPageLoadAdapter extends PageLoadMultiRecyclerViewAdapter<MultiData, BaseViewHolder> {
    public MultiPageLoadAdapter(List<MultiData> dataList) {
        super(dataList);
        //构造函数里面将 每种type 和 type 对应的布局进行绑定
        addItemLayout(MultiData.TYPE_TEXT, R.layout.item_simple);
        addItemLayout(MultiData.TYPE_IMAGE, R.layout.item_multi_image);
        addItemLayout(MultiData.TYPE_VIDEO, R.layout.item_multi_video);
    }

    @Override
    protected void convert(BaseViewHolder holder, MultiData item) {
        //在convert中针对不同的type 进行不同的bind逻辑
        switch (holder.getItemViewType()){
            case MultiData.TYPE_VIDEO:
                holder.setText(R.id.text, item.content);
                break;

            case MultiData.TYPE_IMAGE:
                holder.setText(R.id.text, item.content);
                break;

            case MultiData.TYPE_TEXT:
                holder.setText(R.id.text, item.content);
            default:
                break;
        }
    }
}
```


引入方式也和上面两种方式一样
```
 RecyclerView recyclerView = findViewById(R.id.rv);
        mAdapter = new MultiPageLoadAdapter(null);
        mAdapter.setLoadMoreView(new CommonLoadMoreView());
        mAdapter.setOnLoadMoreListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);
```
