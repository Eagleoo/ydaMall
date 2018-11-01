package com.mall.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.BaseMallAdapter;
import com.lin.component.CustomProgressDialog;
import com.mall.net.NewWebAPI;
import com.mall.net.WebRequestCallBack;
import com.mall.util.Util;

/**
 * 商品搜索结果页面
 *
 * @author ke
 */
public class ProductSreachFrame extends Activity {

    private Intent intent = null;
    private BitmapUtils bmUtil;
    @ViewInject(R.id.product_list_xinpin)
    private TextView product_list_xinpin;
    @ViewInject(R.id.product_list_xiaoliang)
    private TextView product_list_xiaoliang;
    @ViewInject(R.id.product_list_jiage)
    private TextView product_list_jiage;
    @ViewInject(R.id.product_list_pingjia)
    private TextView product_list_pingjia;
    @ViewInject(R.id.product_list_fragment_gridView)
    private GridView gridView;
    @ViewInject(R.id.goods_type_layout)
    private FrameLayout goods_type_layout;
    @ViewInject(R.id.topCenter)
    private TextView topCenter;
    private BaseMallAdapter<JSONObject> adapter = null;
    private int page = 1;
    private int size = 12;
    private int getLastVisiblePosition = 0, lastVisiblePositionY = 0;
    private String params = "";
    private String ascOrDesc = "desc";
    private String sreachValue;

    @ViewInject(R.id.topback1)
    private ImageView topback1;

    @ViewInject(R.id.search1)
    private TextView search1;

    @ViewInject(R.id.ll1)
    private View ll1;


    @ViewInject(R.id.searchtitle)
    private TextView searchtitle;

    private Context context;
    int numce = 0;

    String type = "默认";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.product_list_frame);
        ViewUtils.inject(this);
        context = this;
        init();
        topback1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                finish();
            }
        });

        search1.setVisibility(View.INVISIBLE);
        searchtitle.setVisibility(View.VISIBLE);

    }

    private void init() {
        intent = getIntent();
        bmUtil = new BitmapUtils(this);
        bmUtil.configDefaultLoadFailedImage(R.drawable.zw174);
        sreachValue = intent.getStringExtra("sValue");
        gridView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                    // 滚动到底部
                    if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                        View v = (View) view.getChildAt(view.getChildCount() - 1);
                        int[] location = new int[2];
                        v.getLocationOnScreen(location);// 获取在整个屏幕内的绝对坐标
                        int y = location[1];
                        Log.e("view.getlastvisibleposition", view.getLastVisiblePosition() + "");
                        if (view.getLastVisiblePosition() != getLastVisiblePosition
                                && lastVisiblePositionY != y) {
                            loadData();
                            return;
                        }
                    }
                    getLastVisiblePosition = 0;
                    lastVisiblePositionY = 0;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });

        params = "type=默认&ascOrDesc=" + ascOrDesc + "&value=" + sreachValue;
        loadData();
    }

    // @Override
    // protected void onActivityResult(int requestCode, int resultCode, Intent
    // data) {
    // // TODO Auto-generated method stub
    // super.onActivityResult(requestCode, resultCode, data);
    // if (requestCode == 1000) {
    // sreachValue = data.getStringExtra("searchValue");
    //
    // page = 1;
    // params = "type=默认&ascOrDesc=" + ascOrDesc + "&value=" + sreachValue;
    // adapter = null;
    // loadData();
    // }
    // }

    @SuppressLint("NewApi")
    @OnClick({R.id.product_list_xinpin, R.id.product_list_xiaoliang,
            R.id.product_list_jiage, R.id.product_list_pingjia})
    public void titleClick(View view) {
        product_list_xinpin.setBackground(null);
        product_list_xiaoliang.setBackground(null);
        product_list_jiage.setBackground(null);
        product_list_pingjia.setBackground(null);
        product_list_xinpin.setTextColor(Color.parseColor("#535353"));
        product_list_xiaoliang.setTextColor(Color.parseColor("#535353"));
        product_list_jiage.setTextColor(Color.parseColor("#535353"));
        product_list_pingjia.setTextColor(Color.parseColor("#535353"));
//		view.setBackgroundResource(R.drawable.base_tabpager_indicator_selected);
        ((TextView) view).setTextColor(Color.parseColor("#cf0000"));
        if (null != adapter) {
            adapter.clear();
            adapter = null;
            page = 1;
            if ("asc".equals(ascOrDesc))
                ascOrDesc = "desc";
            else
                ascOrDesc = "asc";
        }
        params = "type=新品&ascOrDesc=" + ascOrDesc + "&value=" + sreachValue;
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.sort_reddown);
        Matrix matrix = new Matrix();
        String strall = "";

        if ("asc".equals(ascOrDesc)) {
            matrix.setRotate(180);

        }

        Bitmap bm1 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        Drawable draw = new BitmapDrawable(bm1);
        draw.setBounds(0, 0, bm1.getWidth(), bm1.getHeight());
        if (view.getId() == product_list_xiaoliang.getId()) {
//			params = "type=销量&ascOrDesc=" + ascOrDesc + "&value=" + sreachValue;
            product_list_xiaoliang.setCompoundDrawables(null, null, draw, null);
            strall = "销量";
        } else if (view.getId() == product_list_jiage.getId()) {
//			params = "type=价格&ascOrDesc=" + ascOrDesc + "&value=" + sreachValue;
            product_list_jiage.setCompoundDrawables(null, null, draw, null);
            strall = "价格";
        } else if (view.getId() == product_list_pingjia.getId()) {
//			params = "type=评价&ascOrDesc=" + ascOrDesc + "&value=" + sreachValue;
            product_list_pingjia.setCompoundDrawables(null, null, draw, null);
            strall = "价格";
        } else if (view.getId() == R.id.product_list_xinpin) {

            strall = "类别";
        }

        showPopupWindow(ll1, strall);

    }

    String[] str = null;
    private String cid = "-1";

    private void showPopupWindow(View view, final String titlename) {
        Log.e("分类", titlename + "");
        type = titlename;
        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(context).inflate(
                R.layout.pop_window1, null);
        // 设置按钮的点击事件
        ListView mylist = (ListView) contentView.findViewById(R.id.itemlist);


        final String[] str0 = {"全部", "服饰鞋帽", "箱包饰品", "个护化妆", "居家百货", "生活电器", "电子数码", "食品保健", "远大专属"};
        final String[] cid1 = {"", "419", "589", "498", "526", "1093", "1147", "469", "1079"};

        final String[] str1 = {"以销量从高到低排序", "以销量从低到高排序"};
        final String[] str2 = {"以价格从高到低排序", "以价格从低到高排序"};
        final String[] str3 = {"以评价从高到低排序", "以评价从低到高排序"};
        str = null;
        if (titlename.equals("类别")) {
            str = str0;
        } else if (titlename.equals("销量")) {
            str = str1;
        } else if (titlename.equals("价格")) {
            str = str2;
        } else if (titlename.equals("评价")) {
            str = str3;
        }


        mylist.setAdapter(new BaseAdapter() {

            @Override
            public View getView(int arg0, View arg1, ViewGroup arg2) {
                // TODO Auto-generated method stub
                View view = LayoutInflater.from(context).inflate(R.layout.itemtextleft, arg2, false);
                TextView textView = (TextView) view.findViewById(R.id.tv);
                textView.setText(str[arg0]);

                return view;
            }

            @Override
            public long getItemId(int arg0) {
                // TODO Auto-generated method stub
                return arg0;
            }

            @Override
            public Object getItem(int arg0) {
                // TODO Auto-generated method stub
                return arg0;
            }

            @Override
            public int getCount() {
                // TODO Auto-generated method stub
                return str.length;
            }
        });

        final PopupWindow popupWindow = new PopupWindow(contentView,
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);

        popupWindow.setTouchable(true);

        popupWindow.setTouchInterceptor(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Log.i("mengdd", "onTouch : ");

                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }


        });

        mylist.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {
                // TODO Auto-generated method stub
                popupWindow.dismiss();


                String order = str[arg2];


                if (order.indexOf("从高到低") != -1) {
                    ascOrDesc = "desc";
                } else if (order.indexOf("从低到高") != -1) {
                    ascOrDesc = "asc";
                }
//				numce++;
//				String ascOrDesc="";
//				if (numce%2!=0) {
//					ascOrDesc = "desc";
//				}else{
//					ascOrDesc = "asc";
//				}
//				


                if (titlename.equals("类别")) {
                    type = "默认";
                    Log.e("是否选择类别", "是");
                    String cid11 = "1";
                    try {
                        cid11 = cid1[arg2];

                    } catch (Exception e) {
                        cid11 = "1";

                    }
                    if (!cid11.equals("1") || !cid11.equals("")) {
                        Log.e("cid111", cid11 + "");
                        cid = cid11;
                    }

                }

                Log.e("点击的cid", cid + "");

                Log.e("分类2", titlename + "");

                params = "type=" + type + "&ascOrDesc=" + ascOrDesc + "&cid=" + cid + "&value=" + sreachValue;
                if (str[arg2].equals("远大专属")) {
                    ishowiamge = false;
                } else {
                    ishowiamge = true;
                }


                loadData();
//				loadData(params, R.layout.product_item_fragment_item,"2");

            }
        });

        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        // 设置好参数之后再show
        popupWindow.showAsDropDown(view);

    }

    private boolean ishowiamge = true;

    private void loadData() {
        if (!Util.isNetworkConnected(this)) {
            Util.show("没有检测到网络，请检查您的网络连接...", this);
            return;
        }
        final CustomProgressDialog cpd = Util.showProgress("加载中...", this);
        NewWebAPI.getNewInstance().getWebRequest(
                "/Product.aspx?call=searchProductList&" + params + "&page="
                        + page + "&size=" + size, new WebRequestCallBack() {
                    @Override
                    public void success(Object result) {
                        if (Util.isNull(result)) {
                            Util.show("网络错误，请重试！", ProductSreachFrame.this);
                            return;
                        }
                        JSONObject json = JSON.parseObject(result.toString());
                        if (200 != json.getIntValue("code")) {
                            Util.show(json.getString("message"),
                                    ProductSreachFrame.this);
                            return;
                        }
                        JSONObject[] objs = json.getJSONArray("list").toArray(
                                new JSONObject[]{});
                        if (null == adapter || 1 == page) {
                            adapter = (new BaseMallAdapter<JSONObject>(
                                    R.layout.product_item_fragment_item,
                                    ProductSreachFrame.this, objs) {
                                @Override
                                public View getView(int position,
                                                    View convertView, ViewGroup parent,
                                                    final JSONObject t) {
//AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(Util.aa/2, Util.aa/2);


                                    AbsListView.LayoutParams layoutParams = (AbsListView.LayoutParams) convertView.getLayoutParams();
                                    if (layoutParams == null) {
                                        layoutParams = new AbsListView.LayoutParams(Util.aa / 2, Util.aa / 2);
                                        convertView.setLayoutParams(layoutParams);
                                    } else {
                                        layoutParams.height = Util.aa / 2;
                                        layoutParams.width = Util.aa / 2;
                                    }


                                    ImageView imageView = ((ImageView) convertView.findViewById(R.id.badgeiv));
                                    if (t.getString("jifen").equals("0.00") || t.getString("jifen").equals("0")) {

                                        imageView.setVisibility(View.INVISIBLE);
                                    } else {
                                        imageView.setVisibility(View.VISIBLE);
                                    }

//									convertView.setLayoutParams(layoutParams);
                                    setImage(R.id.product_list_item_img,
                                            t.getString("thumb"));
                                    setText(R.id.product_list_item_name,
                                            t.getString("name"));
                                    setText(R.id.product_list_item_sbj,
                                            t.getString("sbj"));
                                    setText(R.id.product_list_item_yuanda_price,
                                            t.getString("price"));
                                    setText(R.id.product_list_item_yuanda_jifen,
                                            t.getString("jifen"));
                                    OnClickListener click = new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Util.showIntent(context,
                                                    ProductDeatilFream.class,
                                                    new String[]{"url"},
                                                    new String[]{t
                                                            .getString("pid")});
                                        }
                                    };
                                    getCacheView(R.id.product_list_item_item)
                                            .setOnClickListener(click);
                                    return convertView;
                                }
                            });
                            gridView.setAdapter(adapter);
                        } else {
                            adapter.add(objs);
                            adapter.updateUI();
                        }
                        page++;
                    }

                    @Override
                    public void requestEnd() {
                        super.requestEnd();
                        cpd.dismiss();
                    }

                    @Override
                    public void fail(Throwable e) {
                        super.fail(e);
                    }
                });
    }
}
