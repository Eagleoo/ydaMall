package com.mall.serving.school;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.model.LocationModel;
import com.mall.model.User;
import com.mall.model.YdNewsCommentModel;
import com.mall.model.YdNewsModel;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.R;
import com.mall.view.messageboard.FaceConversionUtil;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;

public class YDNewsDetailFrame extends Activity {

    private WebView web;
    private YdNewsModel y;
    private LinearLayout container;
    @ViewInject(R.id.title)
    private TextView title;
    @ViewInject(R.id.detail_info)
    private TextView detail_info;
    @ViewInject(R.id.short_info)
    private TextView short_info;
    @ViewInject(R.id.et_sendmessage1)
    private EditText et_sendmessage;
    private BitmapUtils bmUtils;
    @ViewInject(R.id.more)
    private TextView more;
    private float screenwidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.message_detail_frame);
        ViewUtils.inject(this);
        bmUtils = new BitmapUtils(this);
        init();
    }

    @OnClick(R.id.more)
    public void More(View v) {
        Intent intent = new Intent(this, YdNewsCommentList.class);
        intent.putExtra("newsid", y.getId());

        this.startActivity(intent);
    }

    @OnClick(R.id.btn_send)
    public void AddComment(final View v) {
        if (Util.isNull(et_sendmessage.getText().toString())) {
            Util.show("请输入评论内容", this);
            return;
        }
        final User user = UserData.getUser();
        String userNo = "";
        if (user == null) {
            userNo = "";
        } else {
            userNo = user.getUserNo();
        }
        final String userNos = userNo;
        String add = "";
        LocationModel locationModel = LocationModel.getLocationModel();
        if (Util.isNull(locationModel.getCity())) {
            add = "";
        } else {
            add = locationModel.getProvince() + locationModel.getCity();
        }
        final String addre = add;
        Util.asynTask(this, "", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (runData == null) {
                    Toast.makeText(YDNewsDetailFrame.this, "评论失败",
                            Toast.LENGTH_LONG).show();
                    return;
                } else {
                    Toast.makeText(YDNewsDetailFrame.this, "评论成功", Toast.LENGTH_LONG).show();
                    getYdNewsCommentList(y.getId());
                    et_sendmessage.setText("");
                }
            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.ydnews_url, Web.add_comment + "&userid="
                        + user.getUserId() + "&newsid=" + y.getId() + "&info="
                        + et_sendmessage.getText().toString() + "&userno="
                        + userNos + "&source=" + addre, "");
                String result = web.getPlan();
                return result;
            }
        });
    }

    private void share(final String url, String imgurl, String title, String brief) {
        OnekeyShare oks = new OnekeyShare();
        oks.setTitle(title);
        oks.setTitleUrl(url);
        oks.setUrl(url);
        oks.setAddress("10086");
        oks.setSite("远大云商");
        oks.setSiteUrl(url);
        oks.setImageUrl(imgurl);
        oks.setText(brief);
        oks.setComment(brief);
        oks.setSilent(false);
        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
            @Override
            public void onShare(Platform platform, ShareParams paramsToShare) {
                if ("ShortMessage".equals(platform.getName())) {
                    paramsToShare.setImageUrl(null);
                    paramsToShare.setText(paramsToShare.getText() + "\n" + url.toString());
                }
            }
        });
        oks.show(this);
    }

    private void init() {
        Util.initTitle(this, "新闻详情", new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (y != null) {
                    String url = "www.yda360.cn/News_show.asp?id=" + y.getId() + "&typeid=1&ntypeid=23";
                    System.out.println("----------url===" + url);
                    share(url, y.getPicurl(), y.getTitle(), y.getContent());
                }
            }
        }, R.drawable.office_share);
        web = this.findViewById(R.id.message_deatil_web);
        container = this.findViewById(R.id.container);
        y = (YdNewsModel) this.getIntent().getSerializableExtra("ydnews");
        initView();
        Log.e("新闻详情", "跳转到这");
        if (!Util.isNull(y.getId())) {
            getNewsObject(y.getId());
            getYdNewsCommentList(y.getId());
        }
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    private void initView() {
        if (Util.isNull(y)) {
            return;
        }
        if (!Util.isNull(y.getTitle())) {
            title.setText(y.getTitle());
        }
        if (!Util.isNull(y.getNewdate())) {
            String[] times = y.getNewdate().split(" ");
            String date = y.getNewdate();
            if (2 == times.length)
                date = times[0];
            detail_info.setText("远大新闻     " + date + "  "
                    + y.getComment() + "评论");
        }
        if (!Util.isNull(y.getContent())) {
            short_info.setText("             " + y.getContent());
        }
        WebSettings settings = web.getSettings();
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);//正常显示不做任何渲染
        settings.setDomStorageEnabled(true);
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setJavaScriptEnabled(true); // 启用JS脚本
        settings.setAllowUniversalAccessFromFileURLs(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        // 点击后退按钮,让WebView后退一页(也可以覆写Activity的onKeyDown方法)
        web.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && web.canGoBack()) {
                        web.goBack(); // 后退
                        return true; // 已处理
                    }
                }
                return false;
            }
        });
        web.addJavascriptInterface(this, "videolistner");
    }

    private void getYdNewsCommentList(final String newsid) {
        Util.asynTask(this, "", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (runData == null) {
                    Toast.makeText(YDNewsDetailFrame.this, "网络请求失败，请稍后再再试",
                            Toast.LENGTH_LONG).show();
                } else {
                    @SuppressWarnings("unchecked")
                    HashMap<Integer, List<YdNewsCommentModel>> map = (HashMap<Integer, List<YdNewsCommentModel>>) runData;
                    List<YdNewsCommentModel> list = map.get(1);
                    if (list != null && list.size() > 0) {
                        YdNewsCommentContainer(list, container);
                        more.setVisibility(View.VISIBLE);
                    } else {
                        more.setVisibility(View.GONE);
                    }
                }
            }

            @SuppressLint("UseSparseArrays")
            @Override
            public Serializable run() {
                Web web = new Web(Web.ydnews_url, Web.get_comment_xml
                        + "&pagesize_=10&curpage=1&newsid=" + newsid, "");
                InputStream in = web.getHtml();
                HashMap<Integer, List<YdNewsCommentModel>> map = new HashMap<Integer, List<YdNewsCommentModel>>();
                ListHandler handler = null;
                SAXParser parser = null;
                try {
                    SAXParserFactory factory = SAXParserFactory.newInstance(); // 取得SAXParserFactory实例
                    parser = factory.newSAXParser();
                    handler = new ListHandler();
                    parser.parse(in, handler);
                    map.put(1, handler.getList());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return map;
            }
        });
    }

    private void getNewsObject(final String newsid) {
        Util.asynTask(this, "获取新闻详情...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (runData == null) {
                    Toast.makeText(YDNewsDetailFrame.this, "获取数据失败",
                            Toast.LENGTH_LONG).show();
                    return;
                } else {
                    YdNewsModel y = (YdNewsModel) runData;
                    StringBuilder sb = new StringBuilder();
                    DisplayMetrics metrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    screenwidth = metrics.widthPixels;
                    int _20dp = Util.pxToDp(YDNewsDetailFrame.this, 60);
                    int px_width = Util.pxToDp(YDNewsDetailFrame.this, screenwidth) - _20dp;
                    initView();

                    Log.e("新闻详情", "px_width" + px_width);
                    sb.append("<script type=\"text/javascript\">");
                    sb.append("var objs=document.getElementsByTagName(\"img\");");
                    sb.append("var videos=document.getElementsByTagName(\"video\");");
                    sb.append("for(var i=0;i<videos.length;i++) {");
                    sb.append("videos[i].style.height=\"auto\";");
                    sb.append("videos[i].style.width=\"" + (px_width)
                            + "px\";");
                    sb.append("}");
                    sb.append("for(var i=0;i<objs.length;i++) {");
                    sb.append("objs[i].style.height=\"auto\";");
                    sb.append("objs[i].style.width=\"" + (px_width)
                            + "px\";");
                    sb.append("if(objs[i].src.indexOf(\"http://\") == -1){");
                    sb.append("objs[i].src = \"http://www.yda360.cn\"+objs[i].src.replace(\"file://\",\"\");");
                    sb.append("}");
                    sb.append("}");
                    sb.append("</script>");
                    String content = y.content;
                    if (Util.isNull(content))
                        content = "";
                    content = content.replaceAll("src=\"/", "src=\"http://www.yda360.cn/");
                    content = content.replaceAll("src='/", "src='http://www.yda360.cn/");
                    initWebViewOfNews(content.replace("!important", "")
                            + sb.toString(), web);
                }
            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.ydnews_url, Web.get_news_info
                        + "&newsid=" + newsid, "");
                InputStream in = web.getHtml();
                YdNewsModel yy = null;
                ObjectHandler handler = null;
                SAXParser parser = null;
                try {
                    SAXParserFactory factory = SAXParserFactory.newInstance(); // 取得SAXParserFactory实例
                    parser = factory.newSAXParser();
                    handler = new ObjectHandler();
                    parser.parse(in, handler);
                    List<YdNewsModel> listt = handler.getList();
                    if (listt != null && listt.size() > 0) {
                        yy = listt.get(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return yy;
            }
        });
    }

    private void addVoidClickListner(WebView view) {


        view.loadUrl("javascript:(function(){" +
                "var videos = document.getElementsByTagName(\"video\"); " +
                "var imgs = document.getElementsByTagName(\"img\"); " +
                "for(var i=0;i<imgs.length;i++)  " +
                "{"
                + "    imgs[i].onclick=function()  " +
                "    {  "
                + "        window.videolistner.openImage1(this.src);  " +
                "    }  " +
                "}" +
                "for(var i=0;i<videos.length;i++)  " +
                "{"
                + "    videos[i].onclick=function()  " +
                "    {  "
                + "        window.videolistner.openImage(this.src);  " +
                "    }  " +
                "}" +
                "})()");
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebViewOfNews(String htmlNews, final WebView web) {


        web.loadDataWithBaseURL(null, htmlNews, "text/html", "utf-8", null);

        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {

                view.getSettings().setJavaScriptEnabled(true);

                super.onPageFinished(view, url);
                // html加载完成之后，添加监听图片的点击js函数
                Log.e("加载完毕", "img" + url);
                addVoidClickListner(web);

            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                view.getSettings().setJavaScriptEnabled(true);

                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

                super.onReceivedError(view, errorCode, description, failingUrl);

            }
        });
    }

    private void YdNewsCommentContainer(List<YdNewsCommentModel> list,
                                        LinearLayout container) {
        container.removeAllViews();
        for (int i = 0; i < list.size(); i++) {
            final YdNewsCommentModel y = list.get(i);
            FrameLayout lay = (FrameLayout) getLayoutInflater().inflate(
                    R.layout.ydnews_comment_item, null);
            final ImageView img = (ImageView) lay.findViewById(R.id.img);
            TextView user_name = (TextView) lay.findViewById(R.id.user_name);
            TextView time_and_role = (TextView) lay
                    .findViewById(R.id.time_and_role);
            final TextView favours = (TextView) lay.findViewById(R.id.favours);
            TextView content = (TextView) lay.findViewById(R.id.content);
            if (Util.isNull(y.getSource())) {
                time_and_role.setText("远大网友   "
                        + Util.friendly_time(y.getAdddate()));
            } else {
                time_and_role.setText(y.getSource() + "  " + Util.friendly_time(y.getAdddate()));
            }
            user_name.setText(Util.getNo_pUserId(y.getUserid()));
            favours.setText(y.getPraise());
            check_comment_Praise(y, favours);

            favours.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Favours(y, favours);
                }
            });
            SpannableString spannableString = FaceConversionUtil.getInstace()
                    .getExpressionString(YDNewsDetailFrame.this, y.getInfo());
            content.setText(spannableString);
            String url = "http://" + Web.webImage + "/userFace/" + y.getUserno() + "_97_97.jpg";
            bmUtils.display(img, url, new BitmapLoadCallBack<View>() {
                @Override
                public void onLoadCompleted(View arg0, String arg1,
                                            Bitmap arg2, BitmapDisplayConfig arg3,
                                            BitmapLoadFrom arg4) {
                    img.setImageBitmap(Util.getRoundedCornerBitmap(arg2));
                }

                @Override
                public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
                    Resources res = YDNewsDetailFrame.this.getResources();
                    Bitmap bit = BitmapFactory.decodeResource(res, R.drawable.ic_launcher_black_white);
                    img.setImageBitmap(Util.getRoundedCornerBitmap(bit));
                }
            });
            // lay.setOnClickListener(new OnClickListener() {
            // @Override
            // public void onClick(View arg0) {
            // et_sendmessage.setText("回复："+yy.getUserid());
            // commentId=yy.getId();
            // }
            // });
            container.addView(lay);
        }
    }


    private void check_comment_Praise(final YdNewsCommentModel yy, final TextView tv) {
        final User user = UserData.getUser();
        if (user == null) {
            return;
        }
        Util.asynTask(new IAsynTask() {

            @Override
            public void updateUI(Serializable runData) {
                if (runData == null) {

                } else {
                    System.out.println(runData.toString() + "check_comment_Praise");
                    if ("0".equals(runData.toString())) {

                        tv.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                                R.drawable.community_dynamic_praise_pink, 0);
                    } else {

                        tv.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                                R.drawable.community_dynamic_praise, 0);
                    }

                }

            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.ydnews_url, Web.check_comment_Praise
                        + "&userid=" + user.getUserId() + "&userno="
                        + user.getUserNo() + "&id=" + yy.getId()
                        + "&md5Pwd=" + user.getMd5Pwd(), "");
                return web.getPlan();
            }
        });
    }

    private void Favours(final YdNewsCommentModel yy, final TextView tv) {
        final User user = UserData.getUser();
        if (user == null) {

        }
        Util.asynTask(this, "", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (runData == null) {

                } else {

                    System.out.println(runData.toString() + "update_comment_Praise");
                    if ("1".equals(runData.toString())) {
                        int parise = Integer.parseInt(yy.getPraise());
                        yy.setPraise((parise + 1) + "");
                        tv.setText((parise + 1) + "");
                        tv.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                                R.drawable.community_dynamic_praise_pink, 0);
                    } else {

                        int parise = Integer.parseInt(yy.getPraise());
                        yy.setPraise((parise - 1) + "");
                        tv.setText((parise - 1) + "");
                        tv.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                                R.drawable.community_dynamic_praise, 0);
                    }

                }
            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.ydnews_url, Web.update_comment_Praise
                        + "&userid=" + user.getUserId() + "&userno="
                        + user.getUserNo() + "&id=" + yy.getId(), "");
                return web.getPlan();
            }
        });
    }

    public class ObjectHandler extends DefaultHandler {
        private List<YdNewsModel> list = new ArrayList<YdNewsModel>();
        private YdNewsModel y;
        private StringBuilder builder;

        public List<YdNewsModel> getList() {
            return list;
        }

        @Override
        public void startDocument() throws SAXException {
            list.clear();
            builder = new StringBuilder();
            super.startDocument();
        }

        @Override
        public void startElement(String uri, String localName, String qName,
                                 Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            if (qName.equals("news"))
                y = new YdNewsModel();
            builder.setLength(0);
        }

        @Override
        public void characters(char[] ch, int start, int length)
                throws SAXException {
            super.characters(ch, start, length);
            builder.append(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName)
                throws SAXException {
            String s = builder.toString();
            if (qName.equals("id")) {
                y.setId(s);
            } else if (qName.equals("tid")) {
                y.setTid(s);
            } else if (qName.equals("nid")) {
                y.setNid(s);
            } else if (qName.equals("Praise")) {
                y.setPraise(s);
            } else if (qName.equals("Comment")) {
                y.setComment(s);
            } else if (qName.equals("click_sum")) {
                y.setClick_sum(s);
            } else if (qName.equals("new_from")) {
                y.setNew_from(s);
            } else if (qName.equals("content")) {
                y.setContent(s);
            } else if (qName.equals("title")) {
                y.setTitle(s);
            } else if (qName.equals("picurl")) {
                y.setPicurl(s);
            } else if (qName.equals("newdate")) {
                y.setNewdate(s);
            } else if (qName.equals("news")) {
                list.add(y);
            }
            super.endElement(uri, localName, qName);
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
        }
    }

    public class ListHandler extends DefaultHandler {
        private List<YdNewsCommentModel> list = new ArrayList<YdNewsCommentModel>();
        private YdNewsCommentModel y;
        private StringBuilder builder;

        public List<YdNewsCommentModel> getList() {
            return list;
        }

        @Override
        public void startDocument() throws SAXException {
            list.clear();
            builder = new StringBuilder();
            super.startDocument();
        }

        @Override
        public void startElement(String uri, String localName, String qName,
                                 Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            if (qName.equals("list"))
                y = new YdNewsCommentModel();
            builder.setLength(0);
        }

        @Override
        public void characters(char[] ch, int start, int length)
                throws SAXException {
            super.characters(ch, start, length);
            builder.append(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName)
                throws SAXException {
            String s = builder.toString();
            if (qName.equals("id")) {
                y.setId(s);
            } else if (qName.equals("newsid")) {
                y.setNewsid(s);
            } else if (qName.equals("Praise")) {
                y.setPraise(s);
            } else if (qName.equals("userid")) {
                y.setUserid(s);
            } else if (qName.equals("adddate")) {
                y.setAdddate(s);
            } else if (qName.equals("info")) {
                y.setInfo(s);
            } else if (qName.equals("Source")) {
                y.setSource(s);
            } else if (qName.equals("userno")) {
                y.setUserno(s);
            } else if (qName.equals("list")) {
                list.add(y);
            }
            super.endElement(uri, localName, qName);
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    // js通信接口
//    @SuppressLint("JavascriptInterface")

    @JavascriptInterface
    public void openImage(String img) {
        System.out.println("KK" + img);
        Log.e("地址", "img" + img);
        //

    }

    @JavascriptInterface
    public void openImage1(String img) {
        System.out.println("KK1" + img);
        Log.e("地址1", "img" + img);
        //

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (web != null) {
            web.onPause();
        }

    }
}
