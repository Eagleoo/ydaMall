package com.mall.view.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.IBinder;
import android.util.Base64;
import android.util.Log;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.lin.component.CustomProgressDialog;
import com.mall.model.BusinessMessage;
import com.mall.model.LocationModel;
import com.mall.model.messageboard.UserMessageBoardCache;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.App;
import com.mall.view.messageboard.MyToast;
import com.mall.yyrg.model.PhotoInfo;

import org.greenrobot.eventbus.EventBus;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class UploadService extends Service {
	/**
	 * 用户在OnActivityResult()中获得图片的uri，然后放入临时Map缓存中，返回到心情列表，后台线程开始上传图片和文字
	 * 微信的发送方式：
	 * 第一次点击发送时候，启动服务，将需发送的数据暂时保存到本地以便顺利显示内容，并向服务传递需要发送的数据，数据开始在后台上传，如果网络情况良好
	 * ，则上传成功 如果网络情况不好，则回调上传结果，提示用户再次上传。
	 */
//	public static final String TAG = "com.mall.view.messageboard.UploadService";
	private Context c;
	private List<Bitmap> bitmaps = new ArrayList<Bitmap>();
	private List<PhotoInfo> photoInfoList = new ArrayList<PhotoInfo>();
	private UploadBinder uploadBinder = new UploadBinder();
	private String imageUploadSuccessed="no";
	private String contentUploadSuccessed="no";
	private String content="";
	private DbUtils db;
	private long id=0;

	private String key="";

	private float score;
	private String type_="";

	public String getType_() {
		return type_;
	}

	public void setType_(String type_) {
		this.type_ = type_;
	}

	private String lid;

	@Override
	public void onCreate() {
		super.onCreate();
	}
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public void setImgLocalFiles(String imgLocalFiles) {
		if(!Util.isNull(imgLocalFiles)){
			String[] files=imgLocalFiles.split("\\|,\\|");
			uploadImageFromLocalFiles(files);
		}
	}
	public void setContext(Context c){
		this.c=c;
		db=DbUtils.create(c, "xqcache");
	}
	public String getImageUploadSuccessed(){
		return imageUploadSuccessed;
	}
	public String getcontentUploadSuccessed(){
		return contentUploadSuccessed;
	}
	public void setId(long id){
		this.id=id;
	}
	public void setContent(String content){
		this.content=content;
	}

	public void setSecore(float score){
		this.score=score;
	}

	public  void  setlid(String lid ){
		this.lid=lid;
	}

	public void setBitmaps(List<Bitmap> bitmap){
		this.bitmaps=bitmap;
		this.key=key;
		if(bitmaps!=null&&bitmaps.size()>0){
			Log.e("上传图片","图片");
			uploadImage();
		}else{
			upload(content, "");
		}
	}

	public void setPhotoInfos(List<PhotoInfo> photoInfoList,String key){
		this.photoInfoList=photoInfoList;
		this.key=key;
			uploadImage();
	}


	public void setBitmapStr(List<Bitmap> bitmap,String key){
		this.key=key;
		this.bitmaps=bitmap;
		if(bitmaps!=null&&bitmaps.size()>0){
			uploadImage();
		}else{

			uploadmessage(content, "",score,lid);
		}
	}


	@Override
	public IBinder onBind(Intent intent) {
		return uploadBinder;
	}
	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}
	public class UploadBinder extends Binder{
		public UploadService getService(){
			return UploadService.this;
		}
	}

	public void uploadImageFromLocalFiles(final String[] localFiles){
		Log.e("上传","1");
		Util.asynTask(new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
//				String result = (String) runData;
//
//				if ((runData + "").contains("success:")) {
//					imageUploadSuccessed="yes";
//					upload(content, result.replace("success:", ""));
//				} else {
//					UserMessageBoardCache usb;
//					try {
//						usb = db.findById(UserMessageBoardCache.class, id);
//						usb.setFlag("no");
//						db.saveOrUpdate(usb);
//					} catch (DbException e1) {
//						e1.printStackTrace();
//					}
//				}


				String result = (String) runData;
				Log.e("上传图片返回地址",runData.toString()+"LLL");
				Log.e("上传图片",result);
				if ((runData + "").contains("success:")) {
					imageUploadSuccessed="yes";

					if(key!=null&&key.equals("1")){
						String str=result.replace("success:", "");
						Log.e("图片地址返回",str+"LKK");
						uploadmessage(content, str,score,lid);
					}else{
						upload(content, result.replace("success:", ""));
					}

				} else {
					UserMessageBoardCache usb;
					try {
						usb = db.findById(UserMessageBoardCache.class, id);
						usb.setFlag("no");
						db.saveOrUpdate(usb);
					} catch (DbException e1) {
						Log.e("上床11Exception",e1.toString());
						e1.printStackTrace();
					}
				}
				System.gc();
			}
			@SuppressLint("NewApi")
			@Override
			public Serializable run() {
				String result = "";
//				String NAMESCROPE = "http://lin00123.cn/";
				String NAMESCROPE = "http://mynameislin.cn/";
				String METHOD_NAME = "uploadMoodImage";
				String URL = "http://" + "img.yda360.com"
						+ "/ImageServiceUpLoad.asmx";

				String SOAP_ACTION = NAMESCROPE + METHOD_NAME;
				List<Bitmap> imgList = new ArrayList<Bitmap>();
				StringBuilder sb = new StringBuilder();
				for(int k=0;k<localFiles.length;k++){
					Bitmap bm =Util.getLocationThmub(localFiles[k],480,640);
					int bmByteCount = 0;
					try {
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
						bm.compress(compressFormat, 100, out);// 将Bitmap压缩到ByteArrayOutputStream中
						InputStream sbs = new ByteArrayInputStream(out
								.toByteArray());
						byte[] buffer = new byte[30 * 1024];
						int count = 0;
						int i = 0;
						String fileName = "mood_android"+System.currentTimeMillis() + ""
								+ new Random().nextInt(Integer.MAX_VALUE/2) + ".jpg";
						bmByteCount = out.size();
						while ((count = sbs.read(buffer)) >= 0) {
							String uploadBuffer = new String(Base64.encode(buffer, 0, count, Base64.DEFAULT));
							SoapObject request = new SoapObject(NAMESCROPE,
									METHOD_NAME);
							request.addProperty("id", "");
							Date curDate = new Date(System.currentTimeMillis());
							request.addProperty("userKey", Util.getUSER_KEY(curDate));
							request.addProperty("userKeyPwd",Util.getUSER_KEYPWD(curDate));
							request.addProperty("image", uploadBuffer);
							request.addProperty("fileName", fileName);
							request.addProperty("tag", i);
							request.addProperty("length", bmByteCount);
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
					sb.append("*|-_-|*");
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


	String zoneid="";
	public void uploadImageFromLocalFiles(final String[] localFiles, final String key1,String zoneid){
		this.zoneid=zoneid;
		this.key=key1;
		Log.e("上传","1");
		Util.asynTask(new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
//				String result = (String) runData;
//
//				if ((runData + "").contains("success:")) {
//					imageUploadSuccessed="yes";
//					upload(content, result.replace("success:", ""));
//				} else {
//					UserMessageBoardCache usb;
//					try {
//						usb = db.findById(UserMessageBoardCache.class, id);
//						usb.setFlag("no");
//						db.saveOrUpdate(usb);
//					} catch (DbException e1) {
//						e1.printStackTrace();
//					}
//				}


				String result = (String) runData;
				Log.e("上传图片返回地址",runData.toString()+"LLL");
				Log.e("上传图片",result);
				if ((runData + "").contains("success:")) {
					imageUploadSuccessed="yes";

					if(key1!=null&&key1.equals("1")){
						String str=result.replace("success:", "");
						Log.e("图片地址返回",str+"LKK");
						uploadmessage(content, str,score,lid);
					}else{
						upload(content, result.replace("success:", ""));
					}

				} else {
					UserMessageBoardCache usb;
					try {
						usb = db.findById(UserMessageBoardCache.class, id);
						usb.setFlag("no");
						db.saveOrUpdate(usb);
					} catch (DbException e1) {
						Log.e("上床11Exception",e1.toString());
						e1.printStackTrace();
					}
				}
				System.gc();
			}
			@SuppressLint("NewApi")
			@Override
			public Serializable run() {
				String result = "";
//				String NAMESCROPE = "http://lin00123.cn/";
				String NAMESCROPE = "http://mynameislin.cn/";
				String METHOD_NAME = "uploadMoodImage";
				String URL = "http://" + "img.yda360.com"
						+ "/ImageServiceUpLoad.asmx";

				String SOAP_ACTION = NAMESCROPE + METHOD_NAME;
				List<Bitmap> imgList = new ArrayList<Bitmap>();
				StringBuilder sb = new StringBuilder();
				for(int k=0;k<localFiles.length;k++){
					Bitmap bm =Util.getLocationThmub(localFiles[k],480,640);
					int bmByteCount = 0;
					try {
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
						bm.compress(compressFormat, 100, out);// 将Bitmap压缩到ByteArrayOutputStream中
						InputStream sbs = new ByteArrayInputStream(out
								.toByteArray());
						byte[] buffer = new byte[30 * 1024];
						int count = 0;
						int i = 0;
						String fileName = "mood_android"+System.currentTimeMillis() + ""
								+ new Random().nextInt(Integer.MAX_VALUE/2) + ".jpg";
						bmByteCount = out.size();
						while ((count = sbs.read(buffer)) >= 0) {
							String uploadBuffer = new String(Base64.encode(buffer, 0, count, Base64.DEFAULT));
							SoapObject request = new SoapObject(NAMESCROPE,
									METHOD_NAME);
							request.addProperty("id", "");
							Date curDate = new Date(System.currentTimeMillis());
							request.addProperty("userKey", Util.getUSER_KEY(curDate));
							request.addProperty("userKeyPwd",Util.getUSER_KEYPWD(curDate));
							request.addProperty("image", uploadBuffer);
							request.addProperty("fileName", fileName);
							request.addProperty("tag", i);
							request.addProperty("length", bmByteCount);
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
					sb.append("*|-_-|*");
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



	/**
	 * 上传图片
	 */
	private void uploadImage(/*final String[] fileNams*/) {
		Log.e("上传","上传图片1111"+key);


		Util.asynTask(new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				String result = (String) runData;

				Log.e("上传图片",result);
				if ((runData + "").contains("success:")) {
					imageUploadSuccessed="yes";

					if(key!=null&&key.equals("1")){
						String str=result.replace("success:", "");
						Log.e("图片地址返回",str+"LKK");
						uploadmessage(content, str,score,lid);
					}else{
						upload(content, result.replace("success:", ""));
					}


				} else {
					UserMessageBoardCache usb;
					try {
						usb = db.findById(UserMessageBoardCache.class, id);
						usb.setFlag("no");
						db.saveOrUpdate(usb);
					} catch (DbException e1) {
						Log.e("上床11Exception",e1.toString());
						e1.printStackTrace();
					}
				}
			}
			@SuppressLint("NewApi")
			@Override
			public Serializable run() {
				String result = "";
//				String NAMESCROPE = "http://lin00123.cn/";
				String NAMESCROPE = "http://mynameislin.cn/";
				String METHOD_NAME = "uploadMoodImage";
				String URL = "http://" + "img.yda360.com"
						+ "/ImageServiceUpLoad.asmx";
				String SOAP_ACTION = NAMESCROPE + METHOD_NAME;
				List<Bitmap> imgList = new ArrayList<Bitmap>();
				StringBuilder sb = new StringBuilder();
				for (int k = 0; k < bitmaps.size(); k++) {
					Bitmap bm = bitmaps.get(k);
					if (null == bm || bm.isRecycled()) {
						continue;
					}

					int bmByteCount = 0;
					try {
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.PNG;
						bm.compress(compressFormat, 100, out); // 将Bitmap压缩到ByteArrayOutputStream中
						InputStream sbs = new ByteArrayInputStream(out
								.toByteArray());
						byte[] buffer = new byte[30 * 1024];
						int count = 0;
						int i = 0;
						String fileName = "mood_android"+System.currentTimeMillis() + ""
								+ new Random().nextInt(Integer.MAX_VALUE/2) + ".jpg";
//						String fileName=fileNams[k];
						bmByteCount = out.size();
						while ((count = sbs.read(buffer)) >= 0) {
							String uploadBuffer = new String(Base64.encode(buffer, 0, count, Base64.DEFAULT));
							SoapObject request = new SoapObject(NAMESCROPE,
									METHOD_NAME);
							request.addProperty("id", "");
							Date curDate = new Date(System.currentTimeMillis());
							request.addProperty("userKey", Util.getUSER_KEY(curDate));
							request.addProperty("userKeyPwd",Util.getUSER_KEYPWD(curDate));
							request.addProperty("image", uploadBuffer);
							request.addProperty("fileName", fileName);
							request.addProperty("tag", i);
							request.addProperty("length", bmByteCount);
							SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
									SoapEnvelope.VER11);
							envelope.bodyOut = request;
							envelope.dotNet = true;
							envelope.setOutputSoapObject(request);
							HttpTransportSE ht = new HttpTransportSE(URL);
							Log.e("上传12","2"+URL);
							ht.debug = true;
							try {
								ht.call(SOAP_ACTION, envelope);
								Object obj = envelope.bodyIn;
								String s = obj.toString();
								System.out.println("obj.toString()================"+ obj.toString());
								result = s.substring(s.indexOf("success"),
										s.indexOf(";"));
							} catch (Exception e) {
								Log.e("上床Exception",e.toString());
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
					sb.append("*|-_-|*");
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
	/**
	 * 上传生成的图片地址和文字
	 * @param content
	 * @param imageFiles
	 */
	private void upload(final String content, final String imageFiles) {
		Log.e("上传","3");
		String info="动态";
		if (type_.equals("评价")){
			info="评价";
		}
		final String finalInfo = info;
		Util.asynTask(App.getContext(), "正在发布您的"+info+"...", new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				if (Util.isNull(runData)) {
					contentUploadSuccessed="no";
					return;
				}
				Log.e("正在发布您的动态1",runData.toString()+"LK");
				if ((runData + "").contains("success")) {
					EventBus.getDefault().post(new BusinessMessage("Yes"));
					Util.MyToast(App.getContext(), finalInfo +"发布成功",1);
					contentUploadSuccessed="yes";
					try {
						UserMessageBoardCache usb=db.findById(UserMessageBoardCache.class, id);
						if (usb!=null) {
							usb.setFlag("yes");
							db.saveOrUpdate(usb);
						}

					} catch (DbException e) {
						e.printStackTrace();
						UserMessageBoardCache usb;
						try {
							usb = db.findById(UserMessageBoardCache.class, id);
							usb.setFlag("no");
							db.saveOrUpdate(usb);
						} catch (DbException e1) {
							e1.printStackTrace();
						}
					}
				} else {
					Log.e("正在发布您的动态2",runData.toString()+"LK");
//					Util.MyToast(App.getActivity(),runData.toString(),1);
					MyToast.makeText(App.getActivity(), runData.toString(), 5)
							.show();
					UserMessageBoardCache usb;
					try {
						usb = db.findById(UserMessageBoardCache.class, id);
						usb.setFlag("no");
						db.saveOrUpdate(usb);
					} catch (DbException e1) {
						e1.printStackTrace();
					}
					contentUploadSuccessed="no";
				}
			}

			@Override
			public Serializable run() {
				String city = "";
				String lat = "";
				String lon = "";
				LocationModel locationModel=LocationModel.getLocationModel();
				if(!Util.isNull(locationModel.getLatitude())){
					lat = locationModel.getLatitude()+"";
					lon = locationModel.getLongitude()+"";
					String province = locationModel.getProvince();
					String ci = locationModel.getCity();
					city = Util.get(province + ci);
				}
				Web web = new Web(Web.addUserMessageBoard, "userId="
						+ UserData.getUser().getUserId() + "&message="
						+ content + "&files=" + imageFiles + "&forward=-1"
						+ "&userPaw=" + UserData.getUser().getMd5Pwd()
						+ "&city=" + city + "&lat=" + lat + "&lon=" + lon+"&zoneid="+zoneid);
				return web.getPlan();
			}
		});
	}


	private void uploadmessage(final String content , final String filespath, final float score, final String lid) {

		String info="动态";
		if (type_.equals("评价")){
			info="评价";
		}
		final CustomProgressDialog dialog=Util.showProgress("正在发布您的"+info+"...", this);
		dialog.setCancelable(false);
		Util.asynTask( new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				dialog.cancel();
				dialog.dismiss();
				if (Util.isNull(runData)) {
					Util.show("网络错误，请重试！", App.getContext());
					return;
				}
				if ((runData + "").contains("success")) {
					if (type_.equals("评价")){
						Util.show("评论发布成功", App.getContext());
					}else{
						Util.show("动态发布成功", App.getContext());
					}

				} else {
					Util.show(runData + "", App.getContext());
				}
			}

			@Override
			public Serializable run() {



				Web web = null;

				Log.e("Type",key+"KL");

				if(key.equals("1")){

					web = new Web(Web.addLMSJComment, "userid="
							+ UserData.getUser().getUserId() + "&md5Pwd="
							+ UserData.getUser().getMd5Pwd() + "&lmsj=" + lid + "&message="
							+ Util.get(content) + "&rating="
							+ score+"&parentID=-1"+"&files="+filespath);

				}



				return web.getPlan();
			}
		});
	}


}
