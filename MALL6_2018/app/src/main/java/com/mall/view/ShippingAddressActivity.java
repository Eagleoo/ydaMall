package com.mall.view;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.model.ShopAddress;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


@ContentView(R.layout.activity_shipping_address)
public class ShippingAddressActivity extends Activity {

    private Context context;

    private String addId = "";

    private TextView add_address;
    LinearLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewUtils.inject(this);
        context = this;
        Util.initTitle(this, "收货地址", new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra("addId", addId);
                setResult(101, data);
                finish();
            }
        });
        init();
    }

    private void init() {
        // TODO Auto-generated method stub
        Intent intent = getIntent();
        addId = intent.getStringExtra("addId");
        selAddress();

        add_address = (TextView) findViewById(R.id.add_address);
        add_address.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

//				Util.showIntent(this, ShopAddressFrame.class, keys, values);
                Intent intent = new Intent(context, ShopAddressFrame.class);
                startActivityForResult(intent, 123);
//				startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        root.removeAllViews();
        switch (requestCode) {
            case 123:
                selAddress();
                break;

            default:
                break;
        }

    }


    public void selAddress() {


        Util.asynTask(this, "正在获取更多收货地址...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                HashMap<String, List<ShopAddress>> map = (HashMap<String, List<ShopAddress>>) runData;
                List<ShopAddress> list = map.get("list");
                if (null == list) {
                    Util.show("网络错误，请重试！", context);
                    return;
                }
                if (0 == list.size()) {
                    Util.show("没有获取到收货地址！", context);
                    return;
                }

                root = (LinearLayout)
                        findViewById(R.id.sel_address_container1);
                for (final ShopAddress sa : list) {
                    View itemView = LayoutInflater.from(context)
                            .inflate(R.layout.sel_all_address_item, null);
                    TextView item = (TextView) itemView
                            .findViewById(R.id.sel_item_address);
                    ImageView image = (ImageView) itemView.findViewById(R.id.bianjiiv);
                    item.setText(sa.getName() + " - " + sa.getMobilePhone()
                            + " - " + sa.getRegion() + " - " + sa.getAddress());
                    item.setTag(sa.getShoppingAddId());
                    item.setTag(-7, sa.getZone());
                    if (sa.getShoppingAddId().equals(addId)) {
                        Resources res = context.getResources();
                        Drawable checked = res
                                .getDrawable(R.drawable.pay_item_checked);
                        checked.setBounds(0, 0, checked.getMinimumWidth(),
                                checked.getMinimumHeight());
                        item.setCompoundDrawables(checked, null, null, null);
                    }

                    image.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            // TODO Auto-generated method stub

                            Intent intent = new Intent(context, ShopAddressFrame.class);
                            intent.putExtra("id", sa.getShoppingAddId());
                            Log.e("*******编辑的appid*******",sa.getShoppingAddId());
                            startActivityForResult(intent, 123);
//							Util.showIntent(context, ShopAddressFrame.class, new String[] { "id" },
//									new String[] {sa.getShoppingAddId() });
                        }
                    });

                    item.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            addId = v.getTag() + "";
                            Log.e("*******收货地址*******",addId);


                            Intent data = new Intent();
                            data.putExtra("addId", addId);
                            setResult(101, data);
                            finish();
                        }
                    });
                    root.addView(itemView);
                }

            }

            @Override
            public Serializable run() {
                User user = UserData.getUser();
                Web web = new Web(Web.getShopAddress, "userId="
                        + user.getUserId() + "&md5Pwd=" + user.getMd5Pwd()
                        + "&size=1024");
                List<ShopAddress> list = web.getList(ShopAddress.class);
                HashMap<String, List<ShopAddress>> map = new HashMap<String, List<ShopAddress>>();
                map.put("list", list);
                return map;
            }
        });
    }

}
