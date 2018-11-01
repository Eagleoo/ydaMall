package com.mall.yyrg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.Util;
import com.mall.view.R;
import com.mall.yyrg.adapter.YYRGComputationResultAdapter;
import com.mall.yyrg.model.TopHundredRecord;

/**
 * 计算结果
 * 
 * @author Administrator
 * 
 */
public class YYRGComputationResult extends Activity {
	private String yppid = "";
	@ViewInject(R.id.listView1)
	private ListView listView1;
	private String time;
	private String renshu;
	private String yungouma;
	@ViewInject(R.id.title)
	private TextView title;
	private String huojiang;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yyrg_computation_result);
		ViewUtils.inject(this);
		yppid = getIntent().getStringExtra("yppid");
		time = getIntent().getStringExtra("time");
		renshu = getIntent().getStringExtra("renshu");
		yungouma=getIntent().getStringExtra("yungou");
		huojiang=getIntent().getStringExtra("huojiang");
		if (TextUtils.isEmpty(yppid)) {

		} else {
			getTopHundredRecord();
		}
	}
	@OnClick(R.id.top_back)
	public void returnBack(View view){
		finish();
	}
	private void getTopHundredRecord() {
		Util.asynTask(this, "载入中……", new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				if (runData != null) {
					List<TopHundredRecord> list = new ArrayList<TopHundredRecord>();
					list = ((HashMap<Integer, List<TopHundredRecord>>) runData)
							.get(1);
					if (list.size() > 0) {
						title.setText("截止该商品最后购买时间〔"+list.get(0).getBuyTime()+"〕最后100条全站购买时间记录");
						
						String message = "";
						long allcount = 0l;
						int temp=0;
						for (int i = 0; i < list.size(); i++) {
							if (time.indexOf(list.get(i).getBuyTime())!=-1&&huojiang.equals(list.get(i).getUserid())) {
								temp=i;
								break;
							}
						}
						for (int i = temp; i < list.size(); i++) {
							String time = list.get(i).getBuyTime().split(" ")[1]
									.replace(":", "");
							time = time.replace(".", "");
							if (i<104) {
								allcount += Long.parseLong(time);
							}
							if (i ==99) {
								list.get(i).setState("2");
							} else {
								list.get(i).setState("1");
							}
						}
					long yushu= allcount% Integer.parseInt(renshu)+10000001;
							if (yushu>=Long.parseLong(yungouma)) {
								allcount=allcount-(yushu-Long.parseLong(yungouma));
							}else {
								allcount=allcount-(Long.parseLong(yungouma)-yushu);
							}
						message = "取以上数值结果得：\n"
								+ "1、求和："
								+ allcount
								+ "(上面100条云购记录时间取值相加之和)\n"
								+ "2、取余："
								+ allcount
								+ "(100条记录之和)%"
								+ renshu
								+ "(本商品总参与人次)="
								+ (Integer.parseInt(yungouma)-10000001)
								+ "(余数)\n3、结果："
								+  (Integer.parseInt(yungouma)-10000001)
								+ "(余数)+10000001="
								+ yungouma;
						listView1.setAdapter(new YYRGComputationResultAdapter(
								YYRGComputationResult.this, list, message));
					} else {

					}

				} else {
					Toast.makeText(YYRGComputationResult.this,
							"操作失败，请检查网络是否连接后，再试一下", Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				Web web = new Web(Web.yyrgAddress, Web.getTopHundredRecord,
						"yppid=" + yppid);
				List<TopHundredRecord> list = web
						.getList(TopHundredRecord.class);
				HashMap<Integer, List<TopHundredRecord>> map = new HashMap<Integer, List<TopHundredRecord>>();
				map.put(1, list);
				return map;
			}

		});
	}
}
