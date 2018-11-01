package com.mall.officeonline;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.CustomProgressDialog;
import com.mall.model.OfficeArticleClass;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.LoginFrame;
import com.mall.view.R;
import com.mall.view.SelectPicActivity;

public class OfficeAddVedioOrArticle extends Activity {
    private List<OfficeArticleClass> articleClassList = new ArrayList<OfficeArticleClass>();
    private List<OfficeArticleClass> vedioClassList = new ArrayList<OfficeArticleClass>();
    @ViewInject(R.id.classtex)
    private TextView classtex;
    @ViewInject(R.id.class_name_choosed)
    private TextView class_name_choosed;
    @ViewInject(R.id.sec)
    private TextView sec;
    @ViewInject(R.id.title)
    private EditText title;
    @ViewInject(R.id.url)
    private EditText url;
    @ViewInject(R.id.urllayout)
    private LinearLayout urllayout;
    @ViewInject(R.id.content)
    private EditText content;
    private String[] parentIds = new String[]{"1", "2"};
    private String type = "";
    private String type_sec = "";
    @ViewInject(R.id.add_vedio_image)
    private ImageView addImageView;
    @ViewInject(R.id.vedio_img)
    private ImageView veido_image;
    @ViewInject(R.id.vedio_url)
    private EditText vedio_url;
    @ViewInject(R.id.vedio_yulan)
    private TextView vedio_yulan;
    @ViewInject(R.id.topCenter)
    private TextView topCenter;
    boolean isArticle;

    @OnClick(R.id.vedio_yulan)
    public void YuLan(final View v) {
        if (Util.isNull(vedio_url.getText().toString())) {
            Toast.makeText(this, "视频地址不能为空！", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(this, VedioWeb.class);
        intent.putExtra("src", vedio_url.getText().toString());
        this.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.office_add_vedio_or_article);
        ViewUtils.inject(this);
        init();
    }

    private void showParentChooseView(List<String> list) {
        final Dialog ad = new Dialog(this, R.style.CustomDialogStyle);
        View view = getLayoutInflater().inflate(
                R.layout.office_album_classifys_choose, null);
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.container);
        Resources rs = this.getResources();
        final Drawable down = rs.getDrawable(R.drawable.radiobutton_down);
        final Drawable up = rs.getDrawable(R.drawable.radiobutton_up);
        down.setBounds(0, 0, down.getMinimumWidth(), down.getMinimumHeight());
        up.setBounds(0, 0, up.getMinimumWidth(), up.getMinimumHeight());
        LinearLayout.LayoutParams tParams = new LinearLayout.LayoutParams(
                LayoutParams.FILL_PARENT, Util.dpToPx(this, 40));
        tParams.setMargins(0, Util.dpToPx(this, 1), 0, 0);
        for (int i = 0; i < list.size(); i++) {
            TextView t = new TextView(this);
            t.setLayoutParams(tParams);
            t.setGravity(Gravity.CENTER_VERTICAL);
            t.setPadding(Util.pxToDp(this, 5), 0, Util.pxToDp(this, 5), 0);

            switch (i) {
                case 0:
                    if (type.equals("1")) {
                        t.setCompoundDrawables(null, null, down, null);
                    } else {
                        t.setCompoundDrawables(null, null, up, null);
                    }
                    break;
                case 1:
                    if (type.equals("2")) {
                        t.setCompoundDrawables(null, null, down, null);
                    } else {
                        t.setCompoundDrawables(null, null, up, null);
                    }
                    break;

                default:
                    break;
            }


            t.setBackgroundColor(Color.WHITE);
            t.setText("  " + list.get(i));
            t.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView t = (TextView) v;
                    t.setCompoundDrawables(null, null, down, null);
                    showVedioOrArticle(t.getText().toString().trim());
                    ad.dismiss();
                }
            });
            layout.addView(t);
        }
        ad.show();
        Window window = ad.getWindow();
        WindowManager.LayoutParams pa = window.getAttributes();
        pa.width = Util.dpToPx(this, 250);
        window.setBackgroundDrawable(new ColorDrawable(0));
        window.setContentView(view, pa);
    }


    private void showVedioOrArticle(String str) {
        if (str.equals("视频专区")) {
            class_name_choosed.setText("视频分类");

            topCenter.setText("发布视频");
            urllayout.setVisibility(View.VISIBLE);
            type = "2";// 点击后大分类
            if (vedioClassList != null && vedioClassList.size() > 0) {
                sec.setVisibility(View.VISIBLE);
                type_sec = vedioClassList.get(0).getTypeid();
                sec.setText(vedioClassList.get(0).getTypename());
            } else {
                sec.setVisibility(View.GONE);
                type_sec = "";
            }
            classtex.setText("  视频专区");
        } else {
            class_name_choosed.setText("文章分类");
            type = "1";
            topCenter.setText("发布文章");
            urllayout.setVisibility(View.GONE);
            if (articleClassList != null
                    && articleClassList.size() > 0) {
                sec.setVisibility(View.VISIBLE);
                type_sec = articleClassList.get(0).getTypeid();
                sec.setText(articleClassList.get(0).getTypename());
            } else {
                sec.setVisibility(View.GONE);
                type_sec = "";
            }
            classtex.setText("  店主日志");
        }

    }

    private void showSecClass(final List<OfficeArticleClass> list) {
        if (list == null || list.size() <= 0) {
            return;
        }
        for (OfficeArticleClass o : list) {
        }
        final Dialog ad = new Dialog(this, R.style.CustomDialogStyle);
        View view = getLayoutInflater().inflate(
                R.layout.office_album_classifys_choose, null);
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.container);
        Resources rs = this.getResources();
        final Drawable down = rs.getDrawable(R.drawable.radiobutton_down);
        final Drawable up = rs.getDrawable(R.drawable.radiobutton_up);
        down.setBounds(0, 0, down.getMinimumWidth(), down.getMinimumHeight());
        up.setBounds(0, 0, up.getMinimumWidth(), up.getMinimumHeight());
        LinearLayout.LayoutParams tParams = new LinearLayout.LayoutParams(
                LayoutParams.FILL_PARENT, Util.dpToPx(this, 40));
        tParams.setMargins(0, Util.dpToPx(this, 1), 0, 0);
        for (int i = 0; i < list.size(); i++) {
            TextView t = new TextView(this);
            t.setLayoutParams(tParams);
            t.setGravity(Gravity.CENTER_VERTICAL);
            t.setPadding(Util.pxToDp(this, 5), 0, Util.pxToDp(this, 5), 0);
            if (i == 0) {
                t.setCompoundDrawables(null, null, down, null);
            } else {
                t.setCompoundDrawables(null, null, up, null);
            }
            t.setBackgroundColor(Color.WHITE);
            t.setText("  " + list.get(i).getTypename());
            t.setTag(list.get(i).getTypeid());
            t.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView t = (TextView) v;
                    type_sec = t.getTag().toString();// 得到二级
                    sec.setText(t.getText().toString());
                    ad.dismiss();
                }
            });
            final OfficeArticleClass m = list.get(i);
            t.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Util.showChoosedDialog(OfficeAddVedioOrArticle.this,
                            "是否要删除该分类？", "点错了", "确定删除", new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    deleteArticleOrVedioClass(m.getTypeid());
                                }
                            });
                    return true;
                }
            });
            layout.addView(t);
        }
        ad.show();
        Window window = ad.getWindow();
        WindowManager.LayoutParams pa = window.getAttributes();
        pa.width = Util.dpToPx(this, 250);
        window.setBackgroundDrawable(new ColorDrawable(0));
        window.setContentView(view, pa);
    }

    public void deleteArticleOrVedioClass(final String id) {
        if (UserData.getUser() != null) {
            final User user = UserData.getUser();
            if (UserData.getOfficeInfo() != null) {

                final String officeid = UserData.getOfficeInfo().getOffice_id();
                Util.asynTask(this, "", new IAsynTask() {
                    @Override
                    public void updateUI(Serializable runData) {
                        if (runData != null) {
                            if ("ok".equals(runData + "")) {
                                Toast.makeText(OfficeAddVedioOrArticle.this,
                                        "删除成功", Toast.LENGTH_LONG).show();
                                Util.showIntent(OfficeAddVedioOrArticle.this,
                                        OfficeAddVedioOrArticle.class);
                            }
                        } else {
                            Toast.makeText(OfficeAddVedioOrArticle.this,
                                    "删除失败", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public Serializable run() {
                        Web web = new Web(Web.officeUrl,
                                Web.DelOfficeUserArticleClass, "officeid="
                                + officeid + "&userID="
                                + user.getUserId() + "&userPaw="
                                + user.getMd5Pwd() + "&cateId=" + id);
                        return web.getPlan();
                    }
                });
            }
        } else {

        }

    }

    private void init() {

        Intent intent = getIntent();
        if (intent.hasExtra("article")) {
            isArticle = true;
        }
        String title = "";
        String txt = "";
        if (isArticle) {
            title = "发布文章";
            txt = "店主日志";
            type = "1";
        } else {
            title = "发布视频";
            txt = "视频专区";
            type = "2";
        }

        showVedioOrArticle(txt);


        Util.initTitle(this, title,
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OfficeAddVedioOrArticle.this.finish();
                    }
                }, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addArticleOrVedio();
                    }
                }, R.drawable.note_add);
        for (int i = 0; i < parentIds.length; i++) {
            getArticleClassify(parentIds[i]);
        }

        classtex.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> li = new ArrayList<String>();
                li.add("店主日志");
                li.add("视频专区");
                showParentChooseView(li);
            }
        });

        sec.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equals("1")) {
                    showSecClass(articleClassList);
                } else {
                    showSecClass(vedioClassList);
                }
            }
        });

        addImageView.setOnClickListener(new ImageSelectOnClick(801));
    }

    public void getArticleClassify(final String parentid) {
        if (UserData.getOfficeInfo() != null) {
            Util.asynTask(this, "", new IAsynTask() {
                @Override
                public void updateUI(Serializable runData) {
                    if (runData != null) {
                        HashMap<String, List<OfficeArticleClass>> map = (HashMap<String, List<OfficeArticleClass>>) runData;
                        if (parentid.equals("1")) {
                            articleClassList = map.get(parentid);
                            if (articleClassList != null
                                    && articleClassList.size() > 0) {
                                sec.setVisibility(View.VISIBLE);
                                sec.setText(articleClassList.get(0)
                                        .getTypename());
                                // 初始的时候设置第一个默认分类
                                type = "1";
                                type_sec = articleClassList.get(0).getTypeid();
                            }
                        } else {
                            vedioClassList = map.get(parentid);
                        }
                    }
                }

                @Override
                public Serializable run() {
                    Web web = new Web(Web.officeUrl, Web.GetOfficeArticleClass,
                            "officeid="
                                    + UserData.getOfficeInfo().getOffice_id()
                                    + "&parentid=" + parentid);
                    List<OfficeArticleClass> list = web
                            .getList(OfficeArticleClass.class);
                    HashMap<String, List<OfficeArticleClass>> map = new HashMap<String, List<OfficeArticleClass>>();
                    map.put(parentid, list);
                    return map;
                }
            });
        }
    }

    private void addArticleOrVedio() {
        if (UserData.getUser() == null) {
            Util.showIntent(this, LoginFrame.class);
        }
        final User u = UserData.getUser();
        if (Util.isNull(title.getText().toString())) {
            Toast.makeText(this, "文章标题不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        if (type.equals("2")) {
            if (Util.isNull(url.getText().toString())) {
                Toast.makeText(this, "视频地址不能为空不能为空", Toast.LENGTH_LONG).show();
                return;
            }
        }
        StringBuilder sb = new StringBuilder();
        if (!Util.isNull(vedio_url.getText().toString())) {
            sb.append("<div style=\"text-align:center;\"><embed type=\"application/x-shockwave-flash\" wmode=\"transparent\" class=\"edui-faked-video\" pluginspage=\"http://www.macromedia.com/go/getflashplayer\" ");
            sb.append("src=\"");
            sb.append(vedio_url.getText().toString());
            sb.append("\" width=\"720\" height=\"480\" style=\"float:none;\" play=\"true\" loop=\"false\" menu=\"false\" allowscriptaccess=\"never\" ");
            sb.append("allowfullscreen=\"true\" /><span style=\"line-height:1.5\"></span></div>");
            sb.append(content.getText().toString());
        }
        sb.append("");
        final String vedi = sb.toString();
        Util.asynTask(this, "", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (runData != null) {
                    if ("ok".equals(runData + "")) {
                        Toast.makeText(OfficeAddVedioOrArticle.this,
                                "添加文章成功", Toast.LENGTH_LONG).show();
                        OfficeAddVedioOrArticle.this.finish();
                    }
                } else {
                    Toast.makeText(OfficeAddVedioOrArticle.this, "添加文章失败",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.officeUrl, Web.AddOfficeUserArticle,
                        "userID=" + u.getUserId() + "&userPaw="
                                + u.getMd5Pwd() + "&type=" + type
                                + "&type_sec=" + type_sec + "&title="
                                + title.getText().toString() + "&vdeioUrl="
                                + url.getText().toString()
                                + "&vdeioimgurl="
                                + url.getText().toString() + "&txtRemark="
                                + vedi.replace("=", "|,.|") + "&flag=1");
                return web.getPlan();
            }
        });
    }

    public class ImageSelectOnClick implements OnClickListener {
        private int code = 0;

        public ImageSelectOnClick(int code) {
            this.code = code;
        }

        @Override
        public void onClick(View arg0) {
            Intent intent = new Intent(OfficeAddVedioOrArticle.this,
                    SelectPicActivity.class);
            startActivityForResult(intent, code);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK
                && (requestCode == 800 || requestCode == 801
                || requestCode == 802 || requestCode == 803
                || requestCode == 804 || requestCode == 805
                || requestCode == 806 || requestCode == 807
                || requestCode == 808 || requestCode == 809)) {
            String picPath = "";
            String pictype = "";
            if (!Util.isNull(data
                    .getStringExtra(SelectPicActivity.KEY_PHOTO_PATH))) {
                picPath = data.getStringExtra(SelectPicActivity.KEY_PHOTO_PATH);
                pictype = picPath.substring(picPath.lastIndexOf(".") + 1,
                        picPath.length());
            }
            final int _80dp = Util.dpToPx(this, 80F);
            Bitmap tagBm = null;
            int networkType = Util.getNetworkType(this);
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(picPath, opts);
            if (1 == networkType) {
                if (opts.outWidth > 480 && opts.outHeight > 800)
                    tagBm = Util.getLocationThmub(picPath, 480, 800);
                else
                    tagBm = BitmapFactory.decodeFile(picPath);
            } else if (2 == networkType) {
                if (opts.outWidth > 240 && opts.outHeight > 400)
                    tagBm = Util.getLocationThmub(picPath, 240, 400);
                else
                    tagBm = BitmapFactory.decodeFile(picPath);
            } else if (3 == networkType) {
                if (opts.outWidth > 320 && opts.outHeight > 480)
                    tagBm = Util.getLocationThmub(picPath, 320, 480);
                else
                    tagBm = BitmapFactory.decodeFile(picPath);
            } else {
                Util.show("请检查您的网络!", this);
                return;
            }
            Bitmap bm = Util.zoomBitmap(tagBm, _80dp, _80dp);
            switch (requestCode) {
                case 801:
                    veido_image.setImageBitmap(bm);
                    veido_image.setTag(tagBm);
                    veido_image.setTag(-7, pictype);
                    addImageView.setVisibility(View.GONE);
                    veido_image.setOnClickListener(new ImageSelectOnClick(801));
                    break;
                default:
                    break;
            }
        }
        upload("");
    }

    @SuppressLint("NewApi")
    private void upload(final String id) {
        final CustomProgressDialog dialog = CustomProgressDialog
                .createDialog(this);
        dialog.setMessage(dialog, "视频封面上传中...");
        dialog.setCancelable(false);
        dialog.show();
        Util.asynTask(new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                String result = (String) runData;
                if (result.contains("success:")) {
                    String[] s = result.split(":");
                    if (s.length == 2 && s[1] != null) {
                        url.setText("http://" + Web.webImage + s[1]);
                    }
                }
                dialog.dismiss();
            }

            @Override
            public Serializable run() {
                String result = "";
                String NAMESCROPE = "http://mynameislin.cn/";
                String METHOD_NAME = "UploadOfficeUserPhoto";
                String URL = "http://" + Web.webImage + "/api/MyOffice.asmx";
                String SOAP_ACTION = NAMESCROPE + METHOD_NAME;
                List<Bitmap> imgList = new ArrayList<Bitmap>();
                List<String> picTypes = new ArrayList<String>();
                int counts = 0;
                if (null != veido_image.getTag()) {
                    imgList.add((Bitmap) veido_image.getTag());
                    picTypes.add(veido_image.getTag(-7).toString());
                }
                StringBuilder sb = new StringBuilder();
                for (int k = 0; k < imgList.size(); k++) {
                    Bitmap bm = imgList.get(k);
                    if (bm.isRecycled()) {
                        continue;
                    }
                    int bmByteCount = 0;
                    try {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
                        bm.compress(compressFormat, 80, out);// 将Bitmap压缩到ByteArrayOutputStream中
                        InputStream sbs = new ByteArrayInputStream(out
                                .toByteArray());
                        byte[] buffer = new byte[30 * 1024];
                        int count = 0;
                        int i = 0;
                        if (out.size() % (30 * 1024) == 0) {
                            counts = out.size() / (30 * 1024);
                        } else {
                            counts = out.size() / (30 * 1024) + 1;
                        }// 计算每张图片上传的次数
                        String fileName = System.currentTimeMillis() + ""
                                + new Random().nextDouble() + "."
                                + picTypes.get(k);
                        bmByteCount = out.size();
                        while ((count = sbs.read(buffer)) >= 0) {
                            String uploadBuffer = new String(Base64.encode(
                                    buffer, 0, count, Base64.DEFAULT));
                            SoapObject request = new SoapObject(NAMESCROPE,
                                    METHOD_NAME);
                            Date curDate = new Date(System.currentTimeMillis());
                            request.addProperty("userKey", Util.getUSER_KEY(curDate));
                            request.addProperty("userKeyPwd", Util.getUSER_KEYPWD(curDate));
                            request.addProperty("image", uploadBuffer);
                            request.addProperty("fileName", fileName);
                            request.addProperty("userID", UserData.getUser()
                                    .getUserId());
                            request.addProperty("userPaw", UserData.getUser()
                                    .getMd5Pwd());
                            request.addProperty("end", counts);
                            request.addProperty("tag", i);
                            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                                    SoapEnvelope.VER11);
                            envelope.bodyOut = request;
                            envelope.dotNet = true;
                            envelope.setOutputSoapObject(request);
                            HttpTransportSE ht = new HttpTransportSE(URL);
                            ht.debug = true;
                            try {
                                ht.call(SOAP_ACTION, envelope);
                                Object obj = envelope.bodyIn;
                                String s = obj.toString();
                                System.out
                                        .println("obj.toString()================"
                                                + obj.toString());
                                result = s.substring(s.indexOf("success"),
                                        s.indexOf(";"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            i++;
                        }
                        out.close();
                        sbs.close();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        bm.recycle();
                    }
                    sb.append(result);
                }
                for (Bitmap bm : imgList) {
                    if (!bm.isRecycled())
                        bm.recycle();
                }
                imgList.clear();
                return sb.toString();
            }
        });
    }

}
