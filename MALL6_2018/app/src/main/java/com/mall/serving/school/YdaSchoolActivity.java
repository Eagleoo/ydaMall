package com.mall.serving.school;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.voicerecognition.android.ui.DialogRecognitionListener;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.model.YdNewsModel;
import com.mall.net.Web;
import com.mall.serving.community.activity.BaseActivity;
import com.mall.serving.community.util.AnimeUtil;
import com.mall.serving.community.util.IAsynTask;
import com.mall.serving.community.util.Util;
import com.mall.serving.community.view.pulltorefresh.PullToRefreshBase;
import com.mall.serving.community.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.mall.serving.community.view.pulltorefresh.PullToRefreshListView;
import com.mall.view.R;

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

@ContentView(R.layout.ydaschool_activity)
public class YdaSchoolActivity extends BaseActivity {
    @ViewInject(R.id.ydnews_search)
    private TextView ydnews_search;

    @ViewInject(R.id.refreshListView)
    private PullToRefreshListView refreshListView;
    private ListView lv;
    private List list;
    private YdaSchoolAdapter adapter;

    private int page = 1;

    private boolean isNews = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);
        list = new ArrayList();
        setListener();
        lv = refreshListView.getRefreshableView();
        lv.setDividerHeight(1);
        refreshListView.setPullRefreshEnabled(true);
        refreshListView.setPullLoadEnabled(true);

        setView();
        adapter = new YdaSchoolAdapter(context, list);
        lv.setAdapter(adapter);
        page();

    }

    private void setView() {

        Intent intent = getIntent();
        if (intent.hasExtra("isNews")) {
            isNews = true;
        }
//		top_center.setText("远大学堂");
//		top_left.setVisibility(View.VISIBLE);
//		top_right.setVisibility(View.VISIBLE);
//		top_right.setCompoundDrawablesWithIntrinsicBounds(0,
//				R.drawable.ic_arrow, 0, 0);
//		top_right.setText("创业课程");
//		top_right.setTextSize(10);
//		top_right.setPadding(10, 10, 10, 10);
        if (isNews) {
            com.mall.util.Util.initTitle(this, "远大新闻", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    @OnClick(R.id.ydnews_audio)
    public void speak(final View v) {
        Util.startVoiceRecognition(this, new DialogRecognitionListener() {
            @Override
            public void onResults(Bundle results) {
                ArrayList<String> rs = results != null ? results
                        .getStringArrayList(RESULTS_RECOGNITION) : null;
                if (rs != null && rs.size() > 0) {
                    String str = rs.get(0).replace("。", "").replace("，", "");
                    ydnews_search.setText(str);
                    page(str);
                }
            }
        });
    }

    @OnClick(R.id.ydnews_s)
    public void searchClick(final View v) {
        page = 1;
        page(ydnews_search.getText().toString());
    }

    private void setListener() {
        OnRefreshListener refreshListener = new OnRefreshListener() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase refreshView) {

                page = 1;
                page();

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase refreshView) {

                page++;
                page();
            }
        };
        refreshListView.setOnRefreshListener(refreshListener);
    }

    private void page() {
        page(null);
    }

    private void page(final String search) {
        if (page == 1) {
            list.clear();
        }
        AnimeUtil.setAnimationEmptyView(this, lv, true);
        Util.asynTask(new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                adapter.notifyDataSetChanged();
                refreshListView.onPullDownRefreshComplete();
                refreshListView.onPullUpRefreshComplete();
                refreshListView.setHasMoreData(true);
                AnimeUtil.setNoDataEmptyView("加载失败...",
                        R.drawable.community_dynamic_empty, context, lv, true,
                        null);
                Log.e("get_news","get_news"+runData.toString());
                if (runData == null) {
                    Util.show("网络请求失败，请稍后再再试");
                } else {
                    @SuppressWarnings("unchecked")
                    HashMap<Integer, List> map = (HashMap<Integer, List>) runData;
                    @SuppressWarnings("unchecked")
                    List<YdNewsModel> mlist = (List<YdNewsModel>) map.get(0);

                    Log.e("get_news1","mlist"+(mlist==null));
                    if (mlist!=null){
                        list.addAll(mlist);
                        adapter.setSearch(search);
                        adapter.notifyDataSetChanged();
                    }


                }
            }

            @SuppressLint("UseSparseArrays")
            @Override
            public Serializable run() {

                String school = "&typeid=7&ntypeid=56";
                if (isNews) {
                    school = "";
                }
                Web web = new Web(Web.ydnews_url, Web.get_news + school
                        + "&pagesize_=10&curpage=" + (page) + (Util.isNull(search) ? "" : "&keywords=" + Util.get(search)), "");
                Log.e("*************",web.getString());
                InputStream in = web.getHtml();
                HashMap<Integer, List<YdNewsModel>> map = new HashMap<Integer, List<YdNewsModel>>();
                ListHandler handler = null;
                SAXParser parser = null;
                try {
                    SAXParserFactory factory = SAXParserFactory.newInstance(); // 取得SAXParserFactory实例
                    parser = factory.newSAXParser();
                    handler = new ListHandler();
                    parser.parse(in, handler);
                    map.put(0, handler.getList());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return map;
            }
        });
    }


    public class ListHandler extends DefaultHandler {
        private List<YdNewsModel> list = new ArrayList<YdNewsModel>();
        private YdNewsModel y;
        private StringBuilder builder;

        // 返回解析后得到的Book对象集合
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
            if (qName.equals("list"))
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
                s = s.replace("\t", "");
                s = s.replace("\n", "");
                s = s.replace("\r", "");
                s = s.replace("", "");
                s = s.replace("<br/>", "");
                s = com.mall.util.Util.Html2Text(s);
                y.setContent(" " + s.replace("\0", ""));
            } else if (qName.equals("title")) {
                y.setTitle(s);
            } else if (qName.equals("picurl")) {
                y.setPicurl(s);
            } else if (qName.equals("newdate")) {
                y.setNewdate(s);
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
}
