package com.mall.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.PauseOnScrollListener;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.model.LMSJComment;
import com.mall.model.LMSJScoreAndCommentCount;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.serving.community.view.picviewpager.PicViewpagerPopup;
import com.mall.serving.voip.view.gridview.NoScrollGridView;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.mall.view.R.id.csg_images;
import static com.mall.view.R.id.lmsj_comment_comment_bar;

public class LMSJCommentFrame extends Activity {

	@ViewInject(R.id.lmsj_comment_score)
	private TextView socre;
	@ViewInject(R.id.lmsj_comment_person)
	private TextView person;
	@ViewInject(R.id.lmsj_comment_rating)
	private RatingBar rating;

	@ViewInject(R.id.lmsj_comment_5xing)
	private ProgressBar _5x;
	@ViewInject(R.id.lmsj_comment_5xing_ren)
	private TextView _5r;
	@ViewInject(R.id.lmsj_comment_4xing)
	private ProgressBar _4x;
	@ViewInject(R.id.lmsj_comment_4xing_ren)
	private TextView _4r;
	@ViewInject(R.id.lmsj_comment_3xing)
	private ProgressBar _3x;
	@ViewInject(R.id.lmsj_comment_3xing_ren)
	private TextView _3r;
	@ViewInject(R.id.lmsj_comment_2xing)
	private ProgressBar _2x;
	@ViewInject(R.id.lmsj_comment_2xing_ren)
	private TextView _2r;
	@ViewInject(R.id.lmsj_comment_1xing)
	private ProgressBar _1x;
	@ViewInject(R.id.lmsj_comment_1xing_ren)
	private TextView _1r;
	@ViewInject(R.id.lmsj_comment_list)
	private ListView listView;
	private LMSJCommentAdapter adapter;
	// 评论
	@ViewInject(R.id.lmsj_comment_comment_value)
	private EditText commentMessage;
	@ViewInject(R.id.lmsj_comment_user_rating)
	private RatingBar ratingBar;

	private String lid;
	private int page = 1;
	private String shopid="";
	private boolean isRefreshFoot = false;


    @ViewInject(lmsj_comment_comment_bar)
    public View lmsj_bar;

    List<LMSJComment> listbean=new ArrayList<>();

	private Context context;

	private  int viewhight=0;

	private String parentID="-1";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.lmsj_comment_frame);
		context=this;
		ViewUtils.inject(this);
		lid = this.getIntent().getStringExtra("lid");
		shopid = this.getIntent().getStringExtra("userid1");
		Util.initTop(this, "联盟商家评论", Integer.MIN_VALUE, null);
		commentMessage.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
                 if(hasFocus){
                	 ratingBar.setRating(0);
                 }
			}
		});
		initTop();
		readComment();
        initListen();
	}

    @Override
    protected void onResume() {
        super.onResume();
        readComment();
    }

    private void initListen() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                LMSJComment  model= listbean.get(position);
                User tempUser = UserData.getUser();
				Log.e("1","0");
                if (tempUser==null){
					Log.e("1","1");
                    return;
                }
				Log.e("1","2");

//                if (!shopid.equals(Util.getNo_pUserId(tempUser.getNoUtf8UserId()))){
//                    return;
//                }

                if (!model.getExp4().equals("")){
					Log.e("1","3");
                    return;
                }


                if (true) {
                    Intent intent = new Intent(LMSJCommentFrame.this, ReplyToCommentsActivity.class);
                    intent.putExtra("info", model);
                    startActivity(intent);
                    return;
                }


                lmsj_bar.setVisibility(View.VISIBLE);

				InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

				ViewTreeObserver vto2 = view.getViewTreeObserver();
				vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
						viewhight=view.getHeight();
					}
				});
				parentID=model.getId();
            }
        });

		listView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(commentMessage.getWindowToken(),0);
				lmsj_bar.setVisibility(View.GONE);
				return false;
			}
		});
    }


    private void readComment() {
		Util.asynTask(this, "正在获取网友评论...", new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				HashMap<String, List<LMSJComment>> map = (HashMap<String, List<LMSJComment>>) runData;
				List<LMSJComment> list = map.get("list");
                listbean.clear();
                listbean.addAll(list);
				if (null == adapter) {
					adapter = new LMSJCommentAdapter(LMSJCommentFrame.this,
                            listbean,shopid);
					listView.setAdapter(adapter);
				} else
					adapter.addData(listbean);
				;
			}

			@Override
			public Serializable run() {
				Web web = new Web(Web.getLMSJCommentPage, "page=" + page
						+ "&size=999&id=" + lid);
				HashMap<String, List<LMSJComment>> map = new HashMap<String, List<LMSJComment>>();
				map.put("list", web.getList(LMSJComment.class));
				return map;
			}
		});

	}

	private void initTop() {
		// 获取评论总分，各等级评论人数
		Util.asynTask(new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				HashMap<String, List<LMSJScoreAndCommentCount>> map = (HashMap<String, List<LMSJScoreAndCommentCount>>) runData;
				List<LMSJScoreAndCommentCount> list = map.get("list");
				if (0 == list.size())
					return;
				int countComment = 0;
				float countScore = 0.0F;
				for (LMSJScoreAndCommentCount lacc : list) {
					countComment += Util.getInt(lacc.getPerson());
					countScore += Util.getDouble(lacc.getSumScore());
				}
				double avgScore = countScore / countComment;
				if (0.0 < avgScore) {
					rating.setRating(Float.parseFloat(avgScore + ""));
					// 平均分
					socre.setText(Util.getDouble(avgScore, 1) + "");
				}
				// 总评论人
				person.setText(countComment + "人评论");
				_1x.setMax(countComment);
				_2x.setMax(countComment);
				_3x.setMax(countComment);
				_4x.setMax(countComment);
				_5x.setMax(countComment);
				for (LMSJScoreAndCommentCount lacc : list) {
					if (lacc.getScore().equals("1.0")) {
						initCommentScoreAndPerson(lacc, _1x, _1r);
					} else if (lacc.getScore().equals("2.0")) {
						initCommentScoreAndPerson(lacc, _2x, _2r);
					} else if (lacc.getScore().equals("3.0")) {
						initCommentScoreAndPerson(lacc, _3x, _3r);
					} else if (lacc.getScore().equals("4.0")) {
						initCommentScoreAndPerson(lacc, _4x, _4r);
					} else if (lacc.getScore().equals("5.0")) {
						initCommentScoreAndPerson(lacc, _5x, _5r);
					}
				}
			}

			@Override
			public Serializable run() {
				Web web = new Web(Web.getLMSJ12345ScoreByLmsjId, "id=" + lid);
				HashMap<String, List<LMSJScoreAndCommentCount>> map = new HashMap<String, List<LMSJScoreAndCommentCount>>();
				map.put("list", web.getList(LMSJScoreAndCommentCount.class));
				return map;
			}
		});

		listView.setOnScrollListener(new PauseOnScrollListener(new BitmapUtils(
				this), false, true, new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
						&& isRefreshFoot) {
					if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
//						readComment();
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// 判断是否滑动到底部弄
				isRefreshFoot = (firstVisibleItem + visibleItemCount == totalItemCount);
			}
		}));
	}

	private void initCommentScoreAndPerson(LMSJScoreAndCommentCount lacc,
			ProgressBar pro, TextView text) {
		pro.setProgress(Integer.parseInt(lacc.getPerson()));
		text.setText(lacc.getPerson());
	}


	@OnClick(R.id.lmsj_comment_comment_submit)
	public void submitClick(View v) {
		final float score = ratingBar.getRating();
		if (null == UserData.getUser()) {
			Util.showIntent("您还没登录是否登录？", this, LoginFrame.class);
			return;
		}
		if(Util.isNull(commentMessage.getText().toString())){
			Util.show("请输入要评论的内容", this);
		}
		Util.asynTask(this, "正在发表您的评论...", new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				if(null == runData)
					Util.show("评论超时，请重试", LMSJCommentFrame.this);
				else if("success".equals(runData+"")){
					ratingBar.setRating(0.0F);
					commentMessage.setText("");
					// 刷新数据
					initTop();
					page=1;
					adapter.clear();
					readComment();
				}else
					Util.show(runData+"", LMSJCommentFrame.this);
			}
			
			@Override
			public Serializable run() {
				Web web = new Web(Web.addLMSJComment, "userid="
						+ UserData.getUser().getUserId() + "&md5Pwd="
						+ UserData.getUser().getMd5Pwd() + "&lmsj=" + lid + "&message="
						+ Util.get(commentMessage.getText().toString()) + "&rating="
						+ score+"&parentID="+parentID+"&files="+"");
				return web.getPlan();
			}
		});
	}
}

class LMSJCommentAdapter extends BaseAdapter {

	private List<LMSJComment> list;
	private Context context;
	private LayoutInflater flater;
	private String shopid;
	public LMSJCommentAdapter(Context context, List<LMSJComment> list,String shopid) {
		super();
		this.context = context;
		this.list = list;
		flater = LayoutInflater.from(context);
		this.shopid=shopid;
	}

	public void addData(List<LMSJComment> list) {
		this.list.addAll(list);
		this.notifyDataSetChanged();
	}

	public void clear() {
		this.list.clear();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return list.get(position).hashCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (null == list || 0 == list.size())
			return convertView;
		final LMSJComment model = list.get(position);
		LMSJCommentHolder holder = null;
		if (null == convertView) {
			convertView = flater.inflate(R.layout.lmsj_comment_list_item, null);
			holder = new LMSJCommentHolder();
			holder.rootitem=convertView.findViewById(R.id.rootitem);
//			com.mall.serving.voip.view.gridview.NoScrollGridView
			holder.csg_images= (NoScrollGridView) convertView.findViewById(csg_images);
			holder.user = (TextView) convertView
					.findViewById(R.id.lmsj_comment_list_item_user);
			holder.date = (TextView) convertView
					.findViewById(R.id.lmsj_comment_list_item_date);
			holder.star = (com.hedgehog.ratingbar.RatingBar) convertView
					.findViewById(R.id.lmsj_comment_list_item_star);
			holder.content = (TextView) convertView
					.findViewById(R.id.lmsj_comment_list_item_content);
			holder.callback= (TextView) convertView.findViewById(R.id.callbackmessage);
			convertView.setTag(holder);
		} else
			holder = (LMSJCommentHolder) convertView.getTag();

		holder.user.setText(model.getUser());
		holder.date.setText(model.getDate());
		holder.star.setStar(Float.parseFloat(model.getScore()));
//		holder.star.setIsIndicator(true);
		holder.content.setText(model.getContent());
		holder.callback.setText(model.getExp4());
		String imagefiles=model.getFiles();
		if (!imagefiles.equals("")){
			String[] imags=imagefiles.split("\\*\\|-_-\\|\\*");
			Log.e("图片长度1",imags.length+"LLLL");
			Log.e("打印地址", Arrays.toString(imags)+"LLL");
			final List<String> piclist = Arrays.asList(imags);
			Log.e("图片长度2",piclist.size()+"LLLL");
			holder.csg_images.setVisibility(View.VISIBLE);


			holder.csg_images.setAdapter(new BaseAdapter() {
				@Override
				public int getCount() {
					return piclist.size();
				}

				@Override
				public Object getItem(int position) {
					return position;
				}

				@Override
				public long getItemId(int position) {
					return position;
				}

				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					View view=LayoutInflater.from(context).inflate(R.layout.imageitem,parent,false);
					ImageView imageView= (ImageView) view.findViewById(R.id.upImageView);

					int width =	Util.getScreenSize(context).getWidth()-200;
					Log.e("设置宽度1","width:"+width);
					int itemwidth=(int)(width/5);
////										LinearLayout.LayoutParams
////												para = (LinearLayout.LayoutParams) imageView.getLayoutParams();
					ViewGroup.LayoutParams
							para =  imageView.getLayoutParams();
					para.width = itemwidth;
					para.height = itemwidth;

					view.setLayoutParams(para);
					Picasso.with(context)
							.load("http://img.yda360.com/"+piclist.get(position))
							.error(R.drawable.ic_launcher)
							.placeholder(R.drawable.ic_launcher)
							.into(imageView);
					return view;
				}
			});

			holder.csg_images.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

					ArrayList<String> arrayList = new ArrayList<String>();
					for (int i=0;i<piclist.size();i++){
						arrayList.add("http://img.yda360.com/"+piclist.get(i));
					}

					new PicViewpagerPopup(context, arrayList, position, true, null);
				}
			});

		}else {
			holder.csg_images.setVisibility(View.GONE);
		}


		return convertView;
	}
}

class LMSJCommentHolder {
	public TextView user;
	public TextView date;
	public com.hedgehog.ratingbar.RatingBar star;
	public TextView content,callback;
	public  View rootitem;
	public NoScrollGridView csg_images;
}
