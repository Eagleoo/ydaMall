package com.mall.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.mall.model.YdNewsModel;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.Util;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class problemDetailsActivity extends AppCompatActivity {
    private float screenwidth;
    private WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_details);
        init();
    }

    private void init() {
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        int newsid = intent.getIntExtra("newsid", 0);
        inittitle(title);
        initWebView();
        getNewsObject(newsid + "");
    }

    @SuppressLint("JavascriptInterface")
    private void initWebView() {
        web = (WebView) findViewById(R.id.message_deatil_web);
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

    private void inittitle(String title) {
        if (Util.isNull(title)) {
            title = "详情";
        }
        Util.initTitle(this, title, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, null, null);
    }

    private void getNewsObject(final String newsid) {
        Util.asynTask(this, "获取详情...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (runData == null) {
                    Toast.makeText(problemDetailsActivity.this, "获取数据失败",
                            Toast.LENGTH_LONG).show();
                    return;
                } else {
                    String y = (String) runData;
                    StringBuilder sb = new StringBuilder();
                    DisplayMetrics metrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    screenwidth = metrics.widthPixels;
                    int _20dp = Util.pxToDp(problemDetailsActivity.this, 60);
                    int px_width = Util.pxToDp(problemDetailsActivity.this, screenwidth) - _20dp;

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
                    String content = y;
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
                Web web = new Web(Web.getMessageById, "&id=" + newsid);
                String in = web.getString().replace("<root>", "").replace("<obj>", "")
                        .replace("</obj>", "").replace("</root>", "").replace("]]>","");
                Log.e("xxxxx", "in" + in);
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
                    Log.e("详情获取异常", "e" + e.toString());
                }
                return in;
            }
        });
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
}
