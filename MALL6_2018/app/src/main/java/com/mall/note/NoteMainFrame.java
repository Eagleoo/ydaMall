package com.mall.note;

import android.annotation.SuppressLint;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.easier.ui.CalendarView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.util.Util;
import com.mall.view.R;
import com.way.note.NoteActivity;

/**
 * @author Administrator
 */
public class NoteMainFrame extends TabActivity {

//	public static NoteMainFrame newInstance = null;

    TabHost tabHost;
    @ViewInject(R.id.jishi)
    private TextView jishi;
    @ViewInject(R.id.rili)
    private TextView rili;
    @ViewInject(R.id.tixing)
    private TextView tixing;
    @ViewInject(R.id.jishi_L)
    private LinearLayout jishi_L;
    @ViewInject(R.id.rili_L)
    private LinearLayout rili_L;
    @ViewInject(R.id.tixing_L)
    private LinearLayout tixing_L;
    private TabWidget tabWidget;
    private int state = 0;

    private LinearLayout[] allLinearLayouts;

    private TextView[] allTextViews;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_frame);
        ViewUtils.inject(this);
//		newInstance = this;
        allTextViews = new TextView[]{jishi, tixing, rili,};
        allLinearLayouts = new LinearLayout[]{jishi_L, tixing_L, rili_L};
        initTab();


    }

    @OnClick({R.id.add_new_js, R.id.tv_search})
    public void addNewJs(View v) {
        switch (v.getId()) {
            case R.id.add_new_js:

                Util.showIntent(this, AddOneNote.class);

                break;
            case R.id.tv_search:
                Util.showIntent(this, NoteSearchActivity.class);
                break;

            default:
                break;
        }
    }

    @OnClick(R.id.top_back)
    public void topBack(View v) {
        finish();
//		Util.showIntent(NoteMainFrame.this, Lin_MainFrame.class);
    }

    @SuppressLint("NewApi")
    @OnClick({R.id.fi_jishi, R.id.fi_tixing, R.id.fi_rili})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.fi_jishi:
                state = 0;
                change(0);
                tabHost.setCurrentTabByTag("jishi");
                break;

            case R.id.fi_tixing:
                change(1);
                tabHost.setCurrentTabByTag("tixing");
                state = 1;
                break;
            case R.id.fi_rili:
                change(2);
                state = 2;

                tabHost.setCurrentTabByTag("rili");
                break;
        }
    }

    public void change(int state) {
        for (int i = 0; i < 3; i++) {
            allTextViews[i].setBackgroundColor(getResources().getColor(R.color.transparent));
            allLinearLayouts[i].setVisibility(View.INVISIBLE);
            allTextViews[i].setTextColor(getResources().getColor(R.color.white));
        }
        allTextViews[state].setTextColor(getResources().getColor(R.color.maincolor));
        allTextViews[state].setBackgroundColor(getResources().getColor(R.color.white));
        allLinearLayouts[state].setVisibility(View.VISIBLE);


    }

    public void initTab() {
        if (!Util.list.contains(this))
            Util.list.add(this);
        tabHost = getTabHost();
        tabWidget = tabHost.getTabWidget();
        tabHost.addTab(tabHost.newTabSpec("jishi").setIndicator("jishi")
                .setContent(new Intent(this, ImportantNoteList.class)));

        Intent fujinIntent = new Intent(this, NoteActivity.class);
        tabHost.addTab(tabHost.newTabSpec("tixing")
                .setIndicator("tixing").setContent(fujinIntent));

        Intent userCenter = new Intent(this, CalendarView.class);
        tabHost.addTab(tabHost.newTabSpec("rili").setIndicator("rili")
                .setContent(userCenter));
    }

}
