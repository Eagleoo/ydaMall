package com.mall.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lin.component.BaseMallAdapter;
import com.lin.component.CustomProgressDialog;
import com.mall.model.LocationModel;
import com.mall.model.User;
import com.mall.net.NewWebAPI;
import com.mall.net.WebRequestCallBack;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.UserData;
import com.mall.util.Util;

import java.util.HashMap;
import java.util.Map;

/**
 * 摇一摇历史记录
 *
 * @author Administrator
 */
public class ListOfShock extends Activity {
    @ViewInject(R.id.listView)
    private ListView listView;
    private int page = 1;
    private BaseMallAdapter<JSONObject> adapter;
    private BitmapUtils bmUtils;
    private String title = "";
    private double x = LocationModel.getLocationModel().getLongitude();
    private double y = LocationModel.getLocationModel().getLatitude();
    private boolean isHistory = true;
    private String result = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shock_old_list);
        ViewUtils.inject(this);
        bmUtils = new BitmapUtils(this);
        init();
        if (isHistory) {
            pageOfShakeHistory();
            scrollPage();
        }
    }

    private void init() {
        title = this.getIntent().getStringExtra("title");
        result = this.getIntent().getStringExtra("result");
        if (Util.isNull(title)) {
            title = "摇一摇记录";
        }
        if (title.equals("摇一摇记录")) {
            isHistory = true;
        } else {
            isHistory = false;
        }
        if (!Util.isNull(result)) {
            isHistory = false;
            if (adapter != null) {
                adapter.clear();
            }
            bindData(result);
        }
        Util.initTitle(this, title, new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ListOfShock.this.finish();
            }
        }, new OnClickListener() {
            @Override
            public void onClick(View v) {
                VoipDialog dialog = new VoipDialog("是否要清空摇一摇记录？", ListOfShock.this, "是", "否", new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clearInfo();
                    }
                }, null);
                dialog.show();
            }
        }, R.drawable.ssdk_country_clear_search);
    }

    private void clearInfo() {
        User user = UserData.getUser();
        final CustomProgressDialog cpd = CustomProgressDialog.showProgressDialog(ListOfShock.this, "正在清空摇一摇记录");
        Map<String, String> map = new HashMap<String, String>();
        map.put("userId", user.getUserIdNoEncodByUTF8());
        map.put("md5Pwd", user.getMd5Pwd());
        NewWebAPI.getNewInstance().getWebRequest("/user.aspx?call=DeleteShake_List", map, new WebRequestCallBack() {
            @Override
            public void success(Object result) {
                if (null == result) {
                    Util.show("网络异常！", ListOfShock.this);
                    return;
                }
                JSONObject json = JSON.parseObject(result.toString());
                Util.show(json.getString("message"), ListOfShock.this);
                if (200 == json.getIntValue("code")) {
                    if (null != adapter) {
                        adapter.clear();
                        adapter = null;
                    }
                    pageOfShakeHistory();
                }
            }

            @Override
            public void fail(Throwable e) {
                Util.show("清空摇一摇记录失败，请重试！", ListOfShock.this);
            }

            @Override
            public void requestEnd() {
                super.requestEnd();
                cpd.dismiss();
                cpd.cancel();
            }

        });
    }

    private void pageOfShakeHistory() {
        if (UserData.getUser() == null) {
            Util.showIntent(this, LoginFrame.class);
            return;
        }
        User user = UserData.getUser();
        final CustomProgressDialog dialog = Util.showProgress("数据加载中...", this);
        NewWebAPI.getNewInstance().GetOldShakeList(user.getUserId(), user.getMd5Pwd(), page++, 15, "1", new WebRequestCallBack() {
            @Override
            public void success(Object result) {
                dialog.cancel();
                dialog.dismiss();
                bindData(result.toString());
            }

            @Override
            public void fail(Throwable e) {
                dialog.cancel();
                dialog.dismiss();
            }
        });
    }

    private void bindData(String result) {
        JSONObject obj = null;
        try {
            obj = JSON.parseObject(result.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        if (200 != obj.getInteger("code").intValue()) {
            return;
        }
        JSONArray array = obj.getJSONArray("list");
        JSONObject[] objs = array.toArray(new JSONObject[]{});
        final int _45dp = Util.pxToDp(ListOfShock.this, 45);
        if (null == adapter) {
            adapter = new BaseMallAdapter<JSONObject>(R.layout.shock_old_list_item, ListOfShock.this, objs) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent, JSONObject t) {
                    setText(R.id.name, Util.getNo_pUserId(t.getString("userid")));
                    LatLng start = new LatLng(x, y);
                    LatLng end = new LatLng(Double.parseDouble(t.getString("x")), Double.parseDouble(t.getString("y")));
                    double distance = AMapUtils.calculateLineDistance(start, end) / 1000F;
                    distance = Util.getDouble(distance, 2);
                    if (x != 0 && y != 0) {
                        if (distance == 0) {
                            setText(R.id.distance, "您和他（她）近在咫尺");
                        } else if (distance < 1) {
                            setText(R.id.distance, "相距1000米以内");
                        } else {
                            setText(R.id.distance, "相距" + distance + "里");
                        }
                    }
                    final ImageView img = (ImageView) convertView.findViewById(R.id.face);
                    ListOfShock.this.bmUtils.display(img, t.getString("userimg"), new BitmapLoadCallBack<View>() {
                        @Override
                        public void onLoadCompleted(View container, String uri, Bitmap bitmap, BitmapDisplayConfig config, BitmapLoadFrom from) {
                            Bitmap zoomBm = Util.zoomBitmap(bitmap, _45dp, _45dp);
                            img.setImageBitmap(Util.toRoundCorner(zoomBm, 3));
                        }

                        @Override
                        public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
                            img.setImageResource(R.drawable.ic_launcher_black_white);
                        }
                    });
                    final String userid = t.getString("userid");
                    convertView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            startPopupWindow(userid);
                        }
                    });
                    return convertView;
                }
            };
            listView.setAdapter(adapter);
        } else {
            adapter.add(objs);
        }
        adapter.updateUI();
    }

    private void scrollPage() {
        listView.setOnScrollListener(new OnScrollListener() {
            int lastItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (lastItem >= adapter.getCount()
                        && scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                    pageOfShakeHistory();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                lastItem = firstVisibleItem + visibleItemCount;
            }
        });
    }

    private void startPopupWindow(final String userid) {
        final Dialog dialog = new Dialog(this, R.style.Dialog);
        View popView = getLayoutInflater().inflate(
                R.layout.shock_list_popupwindow_view, null);
        TextView sb_zz = (TextView) popView.findViewById(R.id.sb_zz);
        TextView czzh_zz = (TextView) popView.findViewById(R.id.czzh_zz);
        TextView check_zzjl = (TextView) popView.findViewById(R.id.check_zzjl);
        check_zzjl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                dialog.cancel();
                Intent intent = new Intent(ListOfShock.this, ShockExchangeHistory.class);
                intent.putExtra("searchUser", userid);
                ListOfShock.this.startActivity(intent);
            }
        });
        sb_zz.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                dialog.cancel();
                Intent intent = new Intent(ListOfShock.this, MoneyToMoneyFrame.class);
                intent.putExtra("parentName", "商币");
                intent.putExtra("userKey", "sb");
                intent.putExtra("userid", userid);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ListOfShock.this.startActivity(intent);
            }
        });
        czzh_zz.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                dialog.cancel();
                Intent intent = new Intent(ListOfShock.this, MoneyToMoneyFrame.class);
                intent.putExtra("parentName", "充值账户");
                intent.putExtra("userKey", "rec");
                intent.putExtra("userid", userid);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ListOfShock.this.startActivity(intent);
            }
        });
        dialog.setContentView(popView);
        dialog.show();
    }
}
  