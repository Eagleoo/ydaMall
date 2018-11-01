package com.mall.view;

import java.io.Serializable;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;

public class StoreMainFrame extends TabActivity {

    public static StoreMainFrame newInstance = null;

    TabHost tabHost;
    private static int size = 0;
    @ViewInject(R.id.homeButton)
    private TextView homeButton;
    @ViewInject(R.id.store_page)
    private TextView store_page;
    @ViewInject(R.id.goods_fenlei)
    private TextView goods_fenlei;
    @ViewInject(R.id.shopping_cart)
    private TextView shopping_cart;
    @ViewInject(R.id.my_card)
    private TextView my_card;
    private int state = 0;
    private String from = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shangc_main_frame);
        ViewUtils.inject(this);
        newInstance = this;
        Intent intent = getIntent();
        from = intent.getStringExtra("from");
        initTab();
        if (0 == size) {
            size++;
        }

        if ("page".equals(intent.getStringExtra("goto"))) {
            String page = intent.getStringExtra("page");

            if (Util.isInt(page)) {
                Util.asynTask(new IAsynTask() {
                    @Override
                    public void updateUI(Serializable runData) {
                        tabHost.setCurrentTabByTag("home");
                        Lin_MallActivity ma = (Lin_MallActivity) tabHost
                                .getCurrentView().getContext();
                    }

                    @Override
                    public Serializable run() {
                        try {
                            Thread.currentThread().sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                });
            }
        }
        if ("phone".equals(from)) {
            state = 1;
            chanegeDrawable(R.drawable.new_shangc_shouye, homeButton,
                    R.color.new_shangc_buqian);
            chanegeDrawable(R.drawable.new_shangcshouye, store_page,
                    R.color.new_shangc_buqian);
            chanegeDrawable(R.drawable.new_shangc_fenlei_1, goods_fenlei,
                    R.color.new_shangc_buhou);
            chanegeDrawable(R.drawable.new_shangc_cart, shopping_cart,
                    R.color.new_shangc_buqian);
            chanegeDrawable(R.drawable.new_shangc_yuanda, my_card,
                    R.color.new_shangc_buqian);
            tabHost.setCurrentTabByTag("fenlei");
            this.sendBroadcast(new Intent("com.lin.actions.cate"));
        }
        if ("shopping_cart".equals(getIntent().getStringExtra("toTab"))) {
            state = 1;
            chanegeDrawable(R.drawable.new_shangc_shouye, homeButton,
                    R.color.new_shangc_buqian);
            chanegeDrawable(R.drawable.new_shangcshouye, store_page,
                    R.color.new_shangc_buqian);
            chanegeDrawable(R.drawable.new_shangc_fenlei, goods_fenlei,
                    R.color.new_shangc_buqian);
            chanegeDrawable(R.drawable.new_shangc_cart_1, shopping_cart,
                    R.color.new_shangc_buhou);
            chanegeDrawable(R.drawable.new_shangc_yuanda, my_card,
                    R.color.new_shangc_buqian);
            tabHost.setCurrentTabByTag("shopping_cart");
        }
    }

    @OnClick({R.id.homeButton, R.id.store_page, R.id.goods_fenlei,
            R.id.shopping_cart, R.id.my_card})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.homeButton:
                chanegeDrawable(R.drawable.new_shangc_shouye_1, homeButton,
                        R.color.new_shangc_buhou);
                chanegeDrawable(R.drawable.new_shangcshouye, store_page,
                        R.color.new_shangc_buqian);
                chanegeDrawable(R.drawable.new_shangc_fenlei, goods_fenlei,
                        R.color.new_shangc_buqian);
                chanegeDrawable(R.drawable.new_shangc_cart, shopping_cart,
                        R.color.new_shangc_buqian);
                chanegeDrawable(R.drawable.new_shangc_yuanda, my_card,
                        R.color.new_shangc_buqian);
                if ("phone".equals(from)) {
                    Intent intent = new Intent(StoreMainFrame.this,
                            Lin_MainFrame.class);
                    intent.putExtra("from", "phone");
                    startActivity(intent);
                    return;
                }
                Util.showIntent(this, Lin_MainFrame.class);
                break;

            case R.id.store_page:
                state = 0;
                chanegeDrawable(R.drawable.new_shangc_shouye, homeButton,
                        R.color.new_shangc_buqian);
                chanegeDrawable(R.drawable.new_shangcshouye_1, store_page,
                        R.color.new_shangc_buhou);
                chanegeDrawable(R.drawable.new_shangc_fenlei, goods_fenlei,
                        R.color.new_shangc_buqian);
                chanegeDrawable(R.drawable.new_shangc_cart, shopping_cart,
                        R.color.new_shangc_buqian);
                chanegeDrawable(R.drawable.new_shangc_yuanda, my_card,
                        R.color.new_shangc_buqian);
                tabHost.setCurrentTabByTag("store_page");
                break;
            case R.id.goods_fenlei:
                state = 1;
                chanegeDrawable(R.drawable.new_shangc_shouye, homeButton,
                        R.color.new_shangc_buqian);
                chanegeDrawable(R.drawable.new_shangcshouye, store_page,
                        R.color.new_shangc_buqian);
                chanegeDrawable(R.drawable.new_shangc_fenlei_1, goods_fenlei,
                        R.color.new_shangc_buhou);
                chanegeDrawable(R.drawable.new_shangc_cart, shopping_cart,
                        R.color.new_shangc_buqian);
                chanegeDrawable(R.drawable.new_shangc_yuanda, my_card,
                        R.color.new_shangc_buqian);
                tabHost.setCurrentTabByTag("fenlei");
                this.sendBroadcast(new Intent("com.lin.actions.cate1"));
                break;
            case R.id.shopping_cart:
                if (null == UserData.getUser()) {
                    Util.show("您还没登录，请先登录！", this);
                    Util.showIntent(StoreMainFrame.this, LoginFrame.class, new String[]{"finish"}, new String[]{"store"});
                    return;
                }
                state = 1;
                chanegeDrawable(R.drawable.new_shangc_shouye, homeButton,
                        R.color.new_shangc_buqian);
                chanegeDrawable(R.drawable.new_shangcshouye, store_page,
                        R.color.new_shangc_buqian);
                chanegeDrawable(R.drawable.new_shangc_fenlei, goods_fenlei,
                        R.color.new_shangc_buqian);
                chanegeDrawable(R.drawable.new_shangc_cart_1, shopping_cart,
                        R.color.new_shangc_buhou);
                chanegeDrawable(R.drawable.new_shangc_yuanda, my_card,
                        R.color.new_shangc_buqian);
                tabHost.setCurrentTabByTag("shopping_cart");
                break;
            case R.id.my_card:
                state = 1;
                chanegeDrawable(R.drawable.new_shangc_shouye, homeButton,
                        R.color.new_shangc_buqian);
                chanegeDrawable(R.drawable.new_shangcshouye, store_page,
                        R.color.new_shangc_buqian);
                chanegeDrawable(R.drawable.new_shangc_fenlei, goods_fenlei,
                        R.color.new_shangc_buqian);
                chanegeDrawable(R.drawable.new_shangc_cart, shopping_cart,
                        R.color.new_shangc_buqian);
                chanegeDrawable(R.drawable.new_shangc_yuanda_1, my_card,
                        R.color.new_shangc_buhou);
                tabHost.setCurrentTabByTag("my_card");
                break;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ("phone".equals(from)) {

                finish();

                return true;
            }
            Activity activity = this.getCurrentActivity();

            if (state != 0) {
                state = 0;
                chanegeDrawable(R.drawable.new_shangc_shouye, homeButton,
                        R.color.new_shangc_buqian);
                chanegeDrawable(R.drawable.new_shangcshouye_1, store_page,
                        R.color.new_shangc_buhou);
                chanegeDrawable(R.drawable.new_shangc_fenlei, goods_fenlei,
                        R.color.new_shangc_buqian);
                chanegeDrawable(R.drawable.new_shangc_cart, shopping_cart,
                        R.color.new_shangc_buqian);
                chanegeDrawable(R.drawable.new_shangc_yuanda, my_card,
                        R.color.new_shangc_buqian);
                tabHost.setCurrentTabByTag("store_page");
                return true;
            }
            if (activity instanceof ProductListFrame) {
                if (((ProductListFrame) activity).isShowCate()) {
                    ((ProductListFrame) activity).hideCate();
                } else {
                    ((ProductListFrame) activity).initGoodsTypeView();
                }
                return true;
            }

        }
        return super.dispatchKeyEvent(event);
    }

    public void initTab() {
        if (!Util.list.contains(this))
            Util.list.add(this);
        tabHost = getTabHost();
        tabHost.addTab(tabHost.newTabSpec("store_page")
                .setIndicator("store_page")
                .setContent(new Intent(this, StoreFrame.class)));
        tabHost.addTab(tabHost.newTabSpec("my_card").setIndicator("my_card")
                .setContent(new Intent(this, UserCenterFrame.class)));
        tabHost.addTab(tabHost.newTabSpec("shopping_cart")
                .setIndicator("shopping_cart")
                .setContent(new Intent(this, ShopCarFrame.class)));
        Intent intent = new Intent(this, ProductListFrame.class);
//		if ("phone".equals(from)) {
        intent.putExtra("catName", "catName");
        intent.putExtra("catId", "-2");
        intent.putExtra("type", "-2");

//		}
        tabHost.addTab(tabHost.newTabSpec("fenlei").setIndicator("fenlei")
                .setContent(intent));

    }

    /**
     * 返回首页
     *
     * @return
     */
    public TextView getMain_tab_home() {
        return homeButton;
    }

    /**
     * 附近
     *
     * @return
     */
    public TextView getMain_tab_lmsj() {
        return goods_fenlei;
    }

    /**
     * 会员中心
     *
     * @return
     */
    public TextView getMain_tab_catagory() {
        return my_card;
    }

    /**
     * 活动
     *
     * @return
     */
    public TextView getMain_tab_car() {
        return store_page;
    }

    /**
     * 心情
     *
     * @return
     */
    public TextView getMain_tab_buy() {
        return shopping_cart;
    }

    private void chanegeDrawable(int imageid, TextView view, int textcolorid) {
        view.setTextColor(getResources().getColor(textcolorid));
        Drawable icon = this.getResources().getDrawable(imageid);
        icon.setBounds(0, 0, icon.getMinimumWidth(), icon.getMinimumHeight());
        view.setCompoundDrawables(null, icon, null, null);
    }

    public void fujinFind(View v) {
        v.setEnabled(false);
        tabHost.setCurrentTabByTag("fujin");
        v.setEnabled(true);
    }

}
