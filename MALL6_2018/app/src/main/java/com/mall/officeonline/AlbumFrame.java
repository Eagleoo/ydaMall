package com.mall.officeonline;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mall.model.AlbumModel;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.LoginFrame;
import com.mall.view.R;
import com.mall.view.messageboard.MessageBoardPicShow;

/**
 * 相册
 *
 * @author Administrator
 */
public class AlbumFrame extends Activity {
    private int page = 1;
    private BitmapUtils bmUtils;
    private int firstColumnHeight;
    private int secondColumnHeight;
    private int thirdColumnHeight;
    @ViewInject(R.id.first_column)
    private LinearLayout firstColumn;
    @ViewInject(R.id.second_column)
    private LinearLayout secondColumn;
    @ViewInject(R.id.third_column)
    private LinearLayout thirdColumn;
    private int columnWidth;
    private PopupWindow distancePopup = null;
    private String classID = "";
    @ViewInject(R.id.grid)
    private GridView grid;
    private ItemAdapter adapter;
    @ViewInject(R.id.topright)
    private ImageView topright;
    private String officeuserId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_frame);
        ViewUtils.inject(this);
        bmUtils = new BitmapUtils(this);
        columnWidth = (getWindowManager().getDefaultDisplay().getWidth() - 15) / 3;

    }

    @Override
    protected void onStart() {
        super.onStart();
        page = 1;
        if (adapter != null) {
            adapter.clear();
        }
        init();
    }

    public void firstpage(String classID) {
        getImageList(classID);
    }

    public void scrollpage(final String classID) {
        grid.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // 滚动到底部自动加载(很重要)
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                    if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                        // 加载更多
                        getImageList(classID);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });
    }

    private void init() {
        classID = this.getIntent().getStringExtra("classID");
        officeuserId = this.getIntent().getStringExtra("officeuseid");
        if (UserData.getUser() != null && UserData.getOfficeInfo() != null) {// 未登录
            if (!UserData.getUser().getUserIdNoEncodByUTF8().trim().equals(officeuserId)) {
                initTopOne();
            } else {
                initTopTwo();
            }
        } else {
            initTopOne();
        }
        firstpage(classID);
        scrollpage(classID);
    }

    private void addclass() {
        LayoutInflater infl = LayoutInflater.from(AlbumFrame.this);
        View view = infl.inflate(R.layout.add_album_classify, null);
        final EditText pwd = (EditText) view.findViewById(R.id.second_pwd);
        pwd.setFocusable(true);
        Button submit = (Button) view.findViewById(R.id.submit);
        Button cancel = (Button) view.findViewById(R.id.cancel);
        final Dialog ad = new Dialog(AlbumFrame.this, R.style.CustomDialogStyle);
        ad.show();
        Window window = ad.getWindow();
        WindowManager.LayoutParams pa = window.getAttributes();
        pa.width = Util.dpToPx(AlbumFrame.this, 250);
        window.setBackgroundDrawable(new ColorDrawable(0));
        window.setContentView(view, pa);
        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Util.isNull(pwd.getText().toString())) {
                    Toast.makeText(AlbumFrame.this, "请输入相册名称", Toast.LENGTH_LONG).show();
                }
                addAlbumClassify(UserData.getOfficeInfo().getOffice_id(), pwd.getText().toString());
                ad.dismiss();
            }
        });
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.dismiss();
            }
        });
    }

    private void initTopOne() {
        Util.initTitle(this, "空间相册", new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initTopTwo() {
        Util.initTitle(this, "空间相册",
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getPopupWindow();
                        startPopupWindow();
                        distancePopup.showAsDropDown(v);
                    }
                }, R.drawable.note_add);
    }

    private void addAlbumClassify(final String officeid, final String typename) {
        if (UserData.getUser() == null) {
            Util.showIntent(this, LoginFrame.class);
        }
        if (UserData.getOfficeInfo() != null) {
            Util.asynTask(this, "", new IAsynTask() {
                @Override
                public void updateUI(Serializable runData) {
                    if (runData != null) {
                        if ("ok".equals(runData + "")) {
                            Toast.makeText(AlbumFrame.this, "增加分类成功",
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(AlbumFrame.this, "增加分类失败",
                                Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public Serializable run() {
                    Web web = new Web(Web.officeUrl, Web.AddOfficePhotoClass,
                            "officeid=" + officeid + "&typename=" + typename
                                    + "&userID="
                                    + UserData.getUser().getUserId()
                                    + "&userPaw="
                                    + UserData.getUser().getMd5Pwd());
                    return web.getPlan();
                }
            });
        }
    }

    private void getImageList(final String typeid) {
        if (UserData.getOfficeInfo() == null) {
            Toast.makeText(this, "对不起，空间相册不存在", Toast.LENGTH_LONG).show();
        } else {

            Util.asynTask(this, "123", new IAsynTask() {
                @Override
                public void updateUI(Serializable runData) {
                    if (runData != null) {
                        HashMap<Integer, List<AlbumModel>> map = (HashMap<Integer, List<AlbumModel>>) runData;
                        List<AlbumModel> list = map.get(page);
                        List<String> urls = new ArrayList<String>();
                        // for (int i = 0; i < list.size(); i++) {
                        // urls.add("http://"+Web.webImage+
                        // list.get(i).getPhoto_path());
                        // }
                        // addImage(urls);
                        if (adapter == null) {
                            adapter = new ItemAdapter(AlbumFrame.this);
                            grid.setAdapter(adapter);
                        }
                        if (list != null && list.size() > 0) {
                            adapter.setList(list);
                        }
                    } else {
                        Toast.makeText(AlbumFrame.this, "无更新图片数据",
                                Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public Serializable run() {
                    Web web = new Web(Web.officeUrl, Web.GetPhotoListPage,
                            "officeid="
                                    + UserData.getOfficeInfo().getOffice_id()
                                    + "&cPage=" + (page++) + "&typeid="
                                    + typeid);
                    List<AlbumModel> list = web.getList(AlbumModel.class);
                    HashMap<Integer, List<AlbumModel>> map = new HashMap<Integer, List<AlbumModel>>();
                    map.put(page, list);
                    return map;
                }
            });
        }
    }

    private void addImage(final List<String> mImageUrls) {
        String mImageUrl = "";
        for (int i = 0; i < mImageUrls.size(); i++) {
            mImageUrl = mImageUrls.get(i);
            final ImageView imageView = new ImageView(getContext());
            imageView.setScaleType(ScaleType.FIT_XY);
            imageView.setPadding(5, 5, 5, 5);
            imageView.setTag(R.string.image_url, mImageUrl);
            final int index = i;
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(),
                            ImageDetailsActivity.class);
                    intent.putExtra("image_position", index);
                    intent.putStringArrayListExtra("urls",
                            (ArrayList<String>) mImageUrls);
                    getContext().startActivity(intent);
                }
            });
            bmUtils.display(imageView, mImageUrl.replace("s_", ""),
                    new BitmapLoadCallBack<View>() {
                        @Override
                        public void onLoadCompleted(View arg0, String arg1,
                                                    Bitmap arg2, BitmapDisplayConfig arg3,
                                                    BitmapLoadFrom arg4) {
                            double ratio = arg2.getWidth()
                                    / (columnWidth * 1.0);
                            int scaledHeight = (int) (arg2.getHeight() / ratio);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    columnWidth, scaledHeight);
                            imageView.setLayoutParams(params);
                            imageView.setImageBitmap(arg2);
                            findColumnToAdd(imageView, arg2.getHeight())
                                    .addView(imageView);
                        }

                        @Override
                        public void onLoadFailed(View arg0, String arg1,
                                                 Drawable arg2) {
                            Resources r = AlbumFrame.this.getResources();
                            InputStream is = r
                                    .openRawResource(R.drawable.ic_launcher);
                            BitmapDrawable bmpDraw = new BitmapDrawable(is);
                            imageView.setImageResource(R.drawable.no_get_image);
                            findColumnToAdd(imageView,
                                    bmpDraw.getBitmap().getHeight()).addView(
                                    imageView);
                        }
                    });
        }
    }

    private LinearLayout findColumnToAdd(ImageView imageView, int imageHeight) {
        System.out.println("开始添加图片");
        if (firstColumnHeight <= secondColumnHeight) {
            if (firstColumnHeight <= thirdColumnHeight) {
                imageView.setTag(R.string.border_top, firstColumnHeight);
                firstColumnHeight += imageHeight;
                imageView.setTag(R.string.border_bottom, firstColumnHeight);
                return firstColumn;
            }
            imageView.setTag(R.string.border_top, thirdColumnHeight);
            thirdColumnHeight += imageHeight;
            imageView.setTag(R.string.border_bottom, thirdColumnHeight);
            return thirdColumn;
        } else {
            if (secondColumnHeight <= thirdColumnHeight) {
                imageView.setTag(R.string.border_top, secondColumnHeight);
                secondColumnHeight += imageHeight;
                imageView.setTag(R.string.border_bottom, secondColumnHeight);
                return secondColumn;
            }
            imageView.setTag(R.string.border_top, thirdColumnHeight);
            thirdColumnHeight += imageHeight;
            imageView.setTag(R.string.border_bottom, thirdColumnHeight);
            return thirdColumn;
        }
    }

    public Context getContext() {
        return this;
    }

    private void getPopupWindow() {
        if (distancePopup != null && distancePopup.isShowing()) {
            distancePopup.dismiss();
        }
    }

    /**
     * 新建一个popupwindow实例
     *
     * @param view
     */
    private void initpoputwindow(View view) {
        distancePopup = new PopupWindow(view,
                android.view.WindowManager.LayoutParams.MATCH_PARENT,
                android.view.WindowManager.LayoutParams.WRAP_CONTENT, true);
        distancePopup.setOutsideTouchable(true);
        distancePopup.setFocusable(true);
        distancePopup.setBackgroundDrawable(new BitmapDrawable());
        distancePopup.setAnimationStyle(R.style.popupanimation);
    }

    /**
     * 初始化并弹出popupwindow
     *
     * @param i
     */
    private void startPopupWindow() {
        View pview = getLayoutInflater().inflate(
                R.layout.shop_office_add_class_or_image, null, false);
        TextView add_class = (TextView) pview.findViewById(R.id.add_class);
        TextView add_image = (TextView) pview.findViewById(R.id.add_image);
        add_class.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addclass();
                distancePopup.dismiss();
            }
        });
        add_image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                distancePopup.dismiss();
                Util.showIntent(AlbumFrame.this, UploadOfficeImage.class);
            }
        });
        initpoputwindow(pview);
    }

    public class ItemAdapter extends BaseAdapter {
        public List<AlbumModel> list = new ArrayList<AlbumModel>();
        private List<String> mImageUrls = new ArrayList<String>();
        public Context c;
        public LayoutInflater inflater;
        private StringBuilder sb = new StringBuilder();
        HashMap<Integer, View> map = new HashMap<Integer, View>();

        public ItemAdapter(Context c) {
            this.c = c;
            inflater = LayoutInflater.from(c);
        }

        public void clear() {
            this.list.clear();
            this.map.clear();
            this.notifyDataSetChanged();
        }

        public void setList(List<AlbumModel> list) {
            this.list.addAll(list);
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    String url = Web.imgServer2 + this.list.get(i).getPhoto_path();
                    mImageUrls.add(url);
                    sb.append(url);
                    sb.append("|,|");
                }
            }
            System.out.println("------------sb.toString()========" + sb.toString());
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return this.list.size();
        }

        @Override
        public Object getItem(int position) {
            return this.list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            AlbumModel a = this.list.get(position);
            ViewHolder h = null;
            if (map.get(position) == null) {
                h = new ViewHolder();
                convertView = inflater.inflate(R.layout.album_item, null);
                h.image = (ImageView) convertView.findViewById(R.id.image);
                h.image.setTag(position);
                convertView.setTag(h);
                map.put(position, convertView);
            } else {
                convertView = map.get(position);
                h = (ViewHolder) convertView.getTag();
                h.image.setTag(position);
            }
            String url = Web.imgServer2 + a.getPhoto_path();
            final ImageView imageView = h.image;
            bmUtils.display(h.image, url.replace("s_", ""),
                    new BitmapLoadCallBack<View>() {
                        @Override
                        public void onLoadCompleted(View arg0, String arg1,
                                                    Bitmap arg2, BitmapDisplayConfig arg3,
                                                    BitmapLoadFrom arg4) {
                            imageView.setImageBitmap(Util.zoomBitmap(arg2, columnWidth, Util.dpToPx(AlbumFrame.this, 100)));
                        }

                        @Override
                        public void onLoadFailed(View arg0, String arg1,
                                                 Drawable arg2) {
                            imageView.setImageResource(R.drawable.no_get_image);
                        }
                    });
            final int index = (Integer) h.image.getTag();
            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(c, MessageBoardPicShow.class);
                    intent.putExtra("picFiles", sb.toString());
                    System.out.println("-------------index=====" + index);
                    intent.putExtra("currentPic", index + "");
                    c.startActivity(intent);
                }
            });
            return convertView;
        }

    }

    public class ViewHolder {
        ImageView image;
    }
}
