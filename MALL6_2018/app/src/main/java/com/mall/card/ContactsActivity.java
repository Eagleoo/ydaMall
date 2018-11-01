package com.mall.card;

import java.io.InputStream;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Organization;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.card.adapter.CnToSpell;
import com.mall.card.adapter.GetContactsInfo;
import com.mall.card.adapter.MyLetterListView;
import com.mall.card.adapter.MyLetterListView.OnTouchingLetterChangedListener;
import com.mall.view.R;


public class ContactsActivity extends Activity {

	Context mContext = null;

	private ProgressDialog dialog;

	private ImageButton contact_list_back_btn;// 返回
	private RelativeLayout contact_list_back_btnll;// 扩大接触面
	
	private static final int PROGRESSDIALOGID = 1;

	/** 获取库Phon表字段 **/
	private static final String[] PHONES_PROJECTION = new String[] {
			Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID, Phone.CONTACT_ID,Organization.COMPANY };

	/** 联系人显示名称 **/
	private static final int PHONES_DISPLAY_NAME_INDEX = 0;

	/** 电话号码 **/
	private static final int PHONES_NUMBER_INDEX = 1;

	/** 头像ID **/
	private static final int PHONES_PHOTO_ID_INDEX = 2;

	/** 联系人的ID **/
	private static final int PHONES_CONTACT_ID_INDEX = 3;
	/**
	 单位
	 */
	private static final int PHONE_COMPANY=4;
	private ArrayList<Map<String, Object>> items = new ArrayList<Map<String, Object>>();

	/** 联系人名称 **/
	private ArrayList<String> mContactsName = new ArrayList<String>();

	/** 联系人号码 **/
	private ArrayList<String> mContactsNumber = new ArrayList<String>();

	/** 联系人头像 **/
	private ArrayList<Bitmap> mContactsPhonto = new ArrayList<Bitmap>();

	private Handler handler;

	private BaseAdapter adapter;

	private OverlayThread overlayThread;
	private TextView overlay;
	private String[] sections;// 存放存在的汉语拼音首字母
	MyLetterListView letterListView = null;
	ListView mListView = null;
	private HashMap<String, Integer> alphaIndexer;// 存放存在的汉语拼音首字母和与之对应的列表位置

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mContext = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.contact_list);
		ViewUtils.inject(this);
		GetContactsInfo getContactsInfo=new GetContactsInfo(this);
		try {
			Log.v("lianxi", getContactsInfo.getContactInfo());
			System.out.println(getContactsInfo.getContactInfo());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		initView();
		
		mListView = (ListView) findViewById(R.id.contact_list);
		letterListView = (MyLetterListView) findViewById(R.id.ContactLetterListView);
		/** 得到手机通讯录联系人信息 **/
		getPhoneContacts();
		getSIMContacts();
		Comparator comp = new Mycomparator();
		Collections.sort(items, comp);

		letterListView
				.setOnTouchingLetterChangedListener(new LetterListViewListener());
		alphaIndexer = new HashMap<String, Integer>();
		handler = new Handler();
		overlayThread = new OverlayThread();
		initOverlay();

		setAdapter(items);

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				// 调用系统方法拨打电话
				// Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri
				// .parse("tel:" + mContactsNumber.get(position)));
				// startActivity(dialIntent);
				Map<String, Object> map = items.get(position);
				Intent intent = new Intent(ContactsActivity.this,CardAddNewCard.class);
				intent.putExtra("Name", map.get("Name").toString());
				intent.putExtra("phoneNumber", map.get("phoneNumber")
						.toString());
				startActivity(intent);
				finish();
			}
		});

		super.onCreate(savedInstanceState);
	}
	@OnClick(R.id.top_back)
public void onclick(View view){
	finish();
}
	private void initView(){
		/*contact_list_back_btn = (ImageButton) findViewById(R.id.contact_list_back_btn);
		contact_list_back_btnll = (RelativeLayout) findViewById(R.id.contact_list_back_btnll);
		contact_list_back_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		contact_list_back_btnll.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});*/
	}
	
	/**
	 * 为ListView设置适配器
	 * 
	 * @param list
	 */
	private void setAdapter(List<Map<String, Object>> list) {
		if (list != null) {
			adapter = new ListAdapter(this, list);
			mListView.setAdapter(adapter);
		}

	}

	/**
	 * ListViewAdapter
	 * 
	 * @author sy
	 * 
	 */
	private class ListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List<Map<String, Object>> list;

		public ListAdapter(Context context, List<Map<String, Object>> list) {

			this.inflater = LayoutInflater.from(context);
			this.list = list;
			alphaIndexer = new HashMap<String, Integer>();
			sections = new String[list.size()];

			for (int i = 0; i < list.size(); i++) {
				// 当前汉语拼音首字母
				// getAlpha(list.get(i));
				String currentStr = list.get(i).get("Sort").toString();
				// 上一个汉语拼音首字母，如果不存在为“ ”
				String previewStr = (i - 1) >= 0 ? list.get(i - 1).get("Sort")
						.toString() : " ";
				if (!previewStr.equals(currentStr)) {
					String name = list.get(i).get("Sort").toString();
					alphaIndexer.put(name, i);
					sections[i] = name;
				}
			}

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
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.clist_item, null);
				holder = new ViewHolder();
				holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
				holder.iamge = (ImageView) convertView.findViewById(R.id.image);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.phone = (TextView) convertView.findViewById(R.id.phone);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.iamge.setImageBitmap((Bitmap) (list.get(position)
					.get("contactPhoto")));
			holder.name.setText(list.get(position).get("Name").toString());
			holder.phone.setText(list.get(position).get("phoneNumber")
					.toString());
			String currentStr = list.get(position).get("Sort").toString();
			String previewStr = (position - 1) >= 0 ? list.get(position - 1)
					.get("Sort").toString() : " ";
			if (!previewStr.equals(currentStr)) {
				holder.alpha.setVisibility(View.VISIBLE);
				holder.alpha.setText(currentStr);
			} else {
				holder.alpha.setVisibility(View.GONE);
			}
			return convertView;
		}

		private class ViewHolder {
			TextView alpha;
			ImageView iamge;
			TextView name;
			TextView phone;
		}

	}

	// 初始化汉语拼音首字母弹出提示框
	private void initOverlay() {
		LayoutInflater inflater = LayoutInflater.from(this);
		overlay = (TextView) inflater.inflate(R.layout.overlay, null);
		overlay.setVisibility(View.INVISIBLE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
				PixelFormat.TRANSLUCENT);
		WindowManager windowManager = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.addView(overlay, lp);
	}

	private class LetterListViewListener implements
			OnTouchingLetterChangedListener {

		@Override
		public void onTouchingLetterChanged(final String s) {
			if (alphaIndexer.get(s) != null) {
				int position = alphaIndexer.get(s);
				mListView.setSelection(position);
				overlay.setText(sections[position]);
				overlay.setVisibility(View.VISIBLE);
				handler.removeCallbacks(overlayThread);
				// 延迟一秒后执行，让overlay为不可见
				handler.postDelayed(overlayThread, 1500);
			}
		}

	}

	// 设置overlay不可见
	private class OverlayThread implements Runnable {

		@Override
		public void run() {
			overlay.setVisibility(View.GONE);
		}

	}

	/** 得到手机通讯录联系人信息 **/
	private void getPhoneContacts() {
		this.showDialog(PROGRESSDIALOGID);

		ContentResolver resolver = mContext.getContentResolver();

		// 获取手机联系人
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,
				PHONES_PROJECTION, null, null, null);

		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {
				Map<String, Object> map = new HashMap<String, Object>();

				// 得到联系人名称
				String contactName = phoneCursor
						.getString(PHONES_DISPLAY_NAME_INDEX);

				String contactSort = CnToSpell.getFullSpell(contactName)
						.toUpperCase().substring(0, 1);
				String company=phoneCursor
						.getString(PHONE_COMPANY);
			
				if (contactSort.equals("0") || contactSort.equals("1")
						|| contactSort.equals("2") || contactSort.equals("3")
						|| contactSort.equals("4") || contactSort.equals("5")
						|| contactSort.equals("6") || contactSort.equals("7")
						|| contactSort.equals("8") || contactSort.equals("9")) {
					contactSort="#";
				}

				// 得到手机号码
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				// 当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber))
					continue;

				// 得到联系人ID
				Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);

				// 得到联系人头像ID
				Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);

				// 得到联系人头像Bitamp
				Bitmap contactPhoto = null;

				// photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
				if (photoid > 0) {
					Uri uri = ContentUris.withAppendedId(
							ContactsContract.Contacts.CONTENT_URI, contactid);
					InputStream input = ContactsContract.Contacts
							.openContactPhotoInputStream(resolver, uri);
					contactPhoto = BitmapFactory.decodeStream(input);
				} else {
					contactPhoto = BitmapFactory.decodeResource(getResources(),
							R.drawable.contact_photo);
				}

				map.put("Name", contactName);
				map.put("phoneNumber", phoneNumber);
				map.put("contactPhoto", contactPhoto);
				map.put("Sort", contactSort);

				// mContactsName.add(contactName);
				// mContactsNumber.add(phoneNumber);
				// mContactsPhonto.add(contactPhoto);
				items.add(map);
			}

			dismissDialog(PROGRESSDIALOGID);
			phoneCursor.close();
		}
	}

	// 通讯社按中文拼音排序
	public class Mycomparator implements Comparator {
		public int compare(Object o1, Object o2) {
			Map<String, Object> c1 = (Map<String, Object>) o1;
			Map<String, Object> c2 = (Map<String, Object>) o2;
			Comparator cmp = Collator.getInstance(java.util.Locale.ENGLISH);
			return cmp.compare(c1.get("Sort"), c2.get("Sort"));
		}
	}

	/** 得到手机SIM卡联系人人信息 **/
	private void getSIMContacts() {
		ContentResolver resolver = mContext.getContentResolver();
		// 获取Sims卡联系人
		Uri uri = Uri.parse("content://icc/adn");
		Cursor phoneCursor = resolver.query(uri, PHONES_PROJECTION, null, null,
				null);

		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {
				Map<String, Object> map = new HashMap<String, Object>();
				// 得到手机号码
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				// 当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber))
					continue;
				// 得到联系人名称
				String contactName = phoneCursor
						.getString(PHONES_DISPLAY_NAME_INDEX);
				String contactSort = CnToSpell.getFullSpell(contactName)
						.toUpperCase().substring(0, 1);
				if (contactSort.equals("0") || contactSort.equals("1")
						|| contactSort.equals("2") || contactSort.equals("3")
						|| contactSort.equals("4") || contactSort.equals("5")
						|| contactSort.equals("6") || contactSort.equals("7")
						|| contactSort.equals("8") || contactSort.equals("9")) {
					contactSort="#";
				}

				Bitmap contactPhoto = null;
				// Sim卡中没有联系人头像,使用默认的
				contactPhoto = BitmapFactory.decodeResource(getResources(),
						R.drawable.contact_photo);

				map.put("Name", contactName);
				map.put("phoneNumber", phoneNumber);
				map.put("contactPhoto", contactPhoto);
				map.put("Sort", contactSort);
				items.add(map);
			}

			phoneCursor.close();
		}
	}

	protected android.app.Dialog onCreateDialog(int id) {
		switch (id) {
		case PROGRESSDIALOGID:
			dialog = ProgressDialog.show(ContactsActivity.this, null,
					"通讯录加载中，请稍后...");
			break;
		default:
			return null;
		}
		return dialog;
	};

}
