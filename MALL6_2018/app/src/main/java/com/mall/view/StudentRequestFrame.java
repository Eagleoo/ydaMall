package com.mall.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;

import java.io.Serializable;

@ContentView(R.layout.student_request_frame)
public class StudentRequestFrame extends Activity {

    @ViewInject(R.id.student_name)
    private EditText name;
    @ViewInject(R.id.student_schoolName)
    private EditText schoolName;
    @ViewInject(R.id.student_stuNo)
    private EditText stuNo;
    @ViewInject(R.id.student_idCardImage)
    private ImageView idCardImage;
    @ViewInject(R.id.student_stuImage)
    private ImageView stuImage;
    @ViewInject(R.id.student_stuImage2)
    private ImageView stuImage2;
    private final int idCard_browse_result = 7;
    private final int stuNo_browse_result = 17;
    private final int stuNo_browse_result2 = 117;
    @ViewInject(R.id.cb_agree)
    private CheckBox cb_agree;
    @ViewInject(R.id.tv_agree)
    private TextView tv_agree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);
        Util.initTop(this, "申请〔大学生空间〕", Integer.MIN_VALUE, null);
        User user = UserData.getUser();
        if (null != user) {
            String secondPwd = UserData.getUser().getSecondPwd();
            if (Util.isNull(secondPwd) || "0".equals(secondPwd)) {
                VoipDialog voipDialog = new VoipDialog("对不起，您还未设置交易密码。为保障您的交易安全，请先设置交易密码", this, "确定", "取消",
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Util.showIntent(StudentRequestFrame.this,
                                        SetSencondPwdFrame.class);
                            }
                        }, null);
                voipDialog.show();
                return;
            }
            if (!Util.isNull(user.getName())
                    && Util.isNull(name.getText().toString()))
                name.setText(user.getName());
        } else {
            Util.showIntent("对不起，请先登录！", this, LoginFrame.class);
        }

        Spanned html1 = Html
                .fromHtml("我已阅读并同意<font color=\"#49afef\">《大学生空间合同》"
                        + "</font>");
        tv_agree.setText(html1);
    }

    @OnClick(R.id.tv_agree)
    public void agree(View v) {

        Util.showIntent(this, ProvisionActivity.class,
                new String[]{"StudentRequestFrame"},
                new String[]{"StudentRequestFrame"});

    }

    @OnClick(R.id.student_submite)
    public void submitClick(View v) {
        v.setEnabled(false);
        final User user = UserData.getUser();

        if (!cb_agree.isChecked()) {
            Util.show("请阅读并同意《大学生空间合同》", this);
            return;
        }
        Util.asynTask(this, "正在申请...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if ("success".equals(runData + "")) {
                    Util.showIntent("申请成功，等待系统审核！", StudentRequestFrame.this,
                            UserCenterFrame.class);
                    return;
                } else
                    Util.show(runData + "", StudentRequestFrame.this);
            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.requestStudent_site, "userid="
                        + user.getUserId() + "&md5Pwd=" + user.getMd5Pwd()
                        + "&stuNo=" + Util.get(stuNo.getText().toString())
                        + "&stuName=" + Util.get(name.getText().toString())
                        + "&schoolName="
                        + Util.get(schoolName.getText().toString())
                        + "&stuImage=" + stuImage.getTag() + "&stuImage2="
                        + stuImage2.getTag() + "&idImage="
                        + idCardImage.getTag());
                return web.getPlan();
            }
        });
        v.setEnabled(true);
    }

    @OnClick(R.id.student_clear)
    public void clearClick(View v) {
        name.setText("");
        stuNo.setText("");
        schoolName.setText("");
    }

    @OnClick(R.id.student_idCardImage)
    public void idCardBrowseClick(View v) {
//		MultiImageSelectorActivity.startSelect(this, idCard_browse_result, 1,
//				true);
        // Intent intent = new Intent(this, SelectPicActivity.class);
        // startActivityForResult(intent, idCard_browse_result);
    }

    @OnClick(R.id.student_stuImage)
    public void stuNoBrowseClick(View v) {
//		MultiImageSelectorActivity.startSelect(this, stuNo_browse_result, 1,
//				true);
        // Intent intent = new Intent(this, SelectPicActivity.class);
        // startActivityForResult(intent, stuNo_browse_result);
    }

    @OnClick(R.id.student_stuImage2)
    public void stuNoBrowse2Click(View v) {
//		MultiImageSelectorActivity.startSelect(this, stuNo_browse_result2, 1,
//				true);
        // Intent intent = new Intent(this, SelectPicActivity.class);
        // startActivityForResult(intent, stuNo_browse_result2);
    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(final int requestCode, int resultCode,
                                    Intent data) {
//		if (resultCode == Activity.RESULT_OK) {
////			String[] imgList = data.getStringArrayExtra(MultiImageSelectorActivity.EXTRA_RESULT);
//			List<String> imgList = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
//			for (final String picPath : imgList) {
//				final String imgType = picPath.substring(picPath
//						.lastIndexOf("."));
//				final int _130dp = Util.dpToPx(this, 130F);
//				final int _100dp = Util.dpToPx(this, 100F);
//				final Bitmap bm = Util
//						.getLocationThmub(picPath, _130dp, _100dp);
//				final Bitmap newBm = Util.zoomBitmap(bm, _130dp, _100dp);
//				if (requestCode == idCard_browse_result) {
//					idCardImage.setImageBitmap(newBm);
//				}
//				if (requestCode == stuNo_browse_result) {
//					stuImage.setImageBitmap(newBm);
//				}
//				if (requestCode == stuNo_browse_result2) {
//					stuImage2.setImageBitmap(newBm);
//				}
//				Util.asynTask(this, "资料上传中...", new IAsynTask() {
//					@Override
//					public void updateUI(Serializable runData) {
//						if (!Util.isNull(runData)
//								&& -1 != (runData + "").indexOf(".")) {
//							String data = (runData + "").replace(
//									"/shop/image/shoplogo//", "/");
//							if (requestCode == idCard_browse_result) {
//								idCardImage.setTag(data);
//							}
//							if (requestCode == stuNo_browse_result) {
//								stuImage.setTag(data);
//							}
//							if (requestCode == stuNo_browse_result2) {
//								stuImage2.setTag(data);
//							}
//							Util.show("上传成功！", StudentRequestFrame.this);
//						} else
//							Util.show("上传失败！", StudentRequestFrame.this);
//					}
//
//					@Override
//					public Serializable run() {
//						User user = UserData.getUser();
//						String NAMESCROPE = "http://mynameislin.cn/";
//						String METHOD_NAME = "uploadStudentImage";
//						String URL = "http://" + Web.webImage
//								+ "/getUserInfo.asmx";
//						String SOAP_ACTION = NAMESCROPE + METHOD_NAME;
//						String path = "";
//						try {
//							ByteArrayOutputStream out = new ByteArrayOutputStream();
//							Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
//							if (imgType.equalsIgnoreCase("png"))
//								compressFormat = Bitmap.CompressFormat.PNG;
//							Util.getLocationThmub(picPath, _130dp * 2,
//									_100dp * 2).compress(compressFormat, 80,
//											out);
//
//							InputStream sbs = new ByteArrayInputStream(out
//									.toByteArray());
//
//							byte[] buffer = new byte[30 * 1024];
//							int count = 0;
//							int i = 0;
//							while ((count = sbs.read(buffer)) >= 0) {
//								String uploadBuffer = new String(Base64.encode(
//										buffer, 0, count, Base64.DEFAULT));
//								SoapObject request = new SoapObject(NAMESCROPE,
//										METHOD_NAME);
//								request.addProperty("userid", user.getUserId());
//								request.addProperty("md5Pwd", user.getMd5Pwd());
//								request.addProperty("oneName",
//										picPath.hashCode());
//								Date curDate = new Date(System.currentTimeMillis());
//								request.addProperty("userKey", Util.getUSER_KEY(curDate));
//								request.addProperty("image", uploadBuffer);
//								request.addProperty("imgType", imgType);
//								request.addProperty("userKeyPwd",
//										Util.getUSER_KEYPWD(curDate));
//								request.addProperty("tag", i);
//								SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
//										SoapEnvelope.VER11);
//								envelope.bodyOut = request;
//								envelope.dotNet = true;
//								envelope.setOutputSoapObject(request);
//
//								HttpTransportSE ht = new HttpTransportSE(URL);
//								ht.debug = true;
//								try {
//									ht.call(SOAP_ACTION, envelope);
//									Object obj = envelope.bodyIn;
//									LogUtils.e(obj + "");
//									if (null != obj) {
//										String oo = obj + "";
//										int index = oo.indexOf("success:");
//										String r = oo.substring(index);
//										path = (r.replace("success:", "")
//												.replace("; }", ""));
//									}
//								} catch (Exception e) {
//									e.printStackTrace();
//								}
//								i++;
//							}
//							out.close();
//							sbs.close();
//						} catch (FileNotFoundException e) {
//							e.printStackTrace();
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//						return path;
//					}
//				});
//			}
//		}
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
    }

}
