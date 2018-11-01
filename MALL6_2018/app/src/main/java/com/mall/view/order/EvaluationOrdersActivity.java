package com.mall.view.order;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

import com.Base.BaseActivity;
import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.lin.component.CustomProgressDialog;
import com.mall.adapter.BaseRecycleAdapter;
import com.mall.adapter.UploadPicHelp.UploadPicAdapter;
import com.mall.model.OrderTwo;
import com.mall.net.WebRequestCallBack;
import com.mall.serving.community.net.NewWebAPI;
import com.mall.util.GetPictureHelp;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.NewProductCommentListActivity;
import com.mall.view.R;
import com.mall.view.ShowPopWindow;
import com.mall.view.databinding.ActivityEvaluationOrdersBinding;
import com.mall.yyrg.YYRGSelectBaskImage;
import com.mall.yyrg.adapter.BitmapUtil;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yatoooon.screenadaptation.ScreenAdapterTools;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

public class EvaluationOrdersActivity extends BaseActivity<ActivityEvaluationOrdersBinding> {

    private int EvaluationState = 0; //0 好评 1一般 2差评

    UploadPicAdapter uploadPicAdapter;

    List<String> imgelist = new ArrayList<String>() {

    };

    @Override
    public int getContentViewId() {

        return R.layout.activity_evaluation_orders;
    }

    @Override
    public View getattachedview() {
        return null;
    }


    private String descript;
    private String server;
    private String deliver;
    private String content;
    private String p_img = "";


    public void submit(View view) {
        Log.e("点击", "发布");
        if (orderTwo == null) {
            return;
        }
        content = mBinding.message.getText().toString();
        if (Util.isNull(content)) {
            Util.show("请输入评论内容...");
            return;
        }
        if (imgelist.size() == 0) {
//            Util.show("请添加商品图片...");
            getData();
            return;
        }

        final CustomProgressDialog dialogimge = Util.showProgress("图片上传中...", this);
        GetPictureHelp.uploadImageFromLocalFiles(imgelist, new GetPictureHelp.Loadimge() {
            @Override
            public void upimge(String s) {

                Log.e("图片上传地址1", "地址" + s);
                s = s.replace("success:", "");
                s = s.replace("*|-_-|*", ",");
                Log.e("图片上传地址2", "地址" + s);
                p_img = s;
                dialogimge.dismiss();
                getData();
            }
        });
    }

    public void setEvaluationState(View view) {
        switch (view.getId()) {
            case R.id.praisesline:
                EvaluationState = 0;
                break;
            case R.id.averageunesline:
                EvaluationState = 1;
                break;
            case R.id.badreviewunsline:
                EvaluationState = 2;
                break;
        }
        setEvaluationUi(EvaluationState);
        setScore(EvaluationState);

    }

    private void setScore(int EvaluationState) {
        switch (EvaluationState) {
            case 0:
                descript = "5";
                server = "5";
                deliver = "5";
                break;
            case 1:
                descript = "2.5";
                server = "2.5";
                deliver = "2.5";
                break;
            case 2:
                descript = "1";
                server = "1";
                deliver = "1";
                break;
        }

    }

    private void setEvaluationUi(int EvaluationState) {
        mBinding.praisesiv.setImageResource(R.drawable.praiunseselected);
        mBinding.praisestv.setTextColor(Color.parseColor("#ACACAC"));
        mBinding.averageunesiv.setImageResource(R.drawable.averageunselected);
        mBinding.averageunestv.setTextColor(Color.parseColor("#ACACAC"));
        mBinding.badreviewunsiv.setImageResource(R.drawable.badreviewunselected);
        mBinding.badreviewunstv.setTextColor(Color.parseColor("#ACACAC"));
        if (EvaluationState == 0) {
            mBinding.praisesiv.setImageResource(R.drawable.praiseselected);
            mBinding.praisestv.setTextColor(Color.parseColor("#FE4462"));
        } else if (EvaluationState == 1) {
            mBinding.averageunesiv.setImageResource(R.drawable.averageselected);
            mBinding.averageunestv.setTextColor(Color.parseColor("#FE4462"));
        } else if (EvaluationState == 2) {
            mBinding.badreviewunsiv.setImageResource(R.drawable.badreviewselected);
            mBinding.badreviewunstv.setTextColor(Color.parseColor("#FE4462"));
        }
    }


    private void getData() {
        final CustomProgressDialog dialog = Util.showProgress("数据加载中...", this);
        NewWebAPI.getNewInstance().getWebRequest1("/Product.aspx?call=add_comments_product" +
                        "&orderid=" + orderTwo.secondOrderId + "&pid=" + orderTwo.productId + "&pname=" + orderTwo.productName.replace("/", "").replace(" ", "%20")
                        + "&descript=" + descript + "&server=" + server
                        + "&deliver=" + deliver + "&content=" + content
                        + "&p_img=" + p_img + "&userId="
                        + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd()
                , null,
                new WebRequestCallBack() {
                    @Override
                    public void success(Object result) {
                        com.alibaba.fastjson.JSONObject obj = JSON.parseObject(result.toString());
                        if (200 != obj.getInteger("code").intValue()) {
                            Util.show(obj.getString("message"),
                                    context);
                            return;
                        }
                        Util.show(obj.getString("message"),
                                context);
                        Intent intent = new Intent(context,
//                ProductCommentList.class);
                                NewProductCommentListActivity.class);
                        intent.putExtra("pid", orderTwo.productId + "");
                       startActivity(intent);
                        finish();

                    }

                    @Override
                    public void requestEnd() {
                        dialog.cancel();
                        dialog.dismiss();
                    }
                });
    }

    OrderTwo orderTwo;

    @Override
    protected void initView(ActivityEvaluationOrdersBinding mBinding) {
        Intent intent = getIntent();
        if (intent.hasExtra("shopinfo")) {
            orderTwo = (OrderTwo) intent.getSerializableExtra("shopinfo");
            Glide.with(context).load(orderTwo.productThumb).into(mBinding.shopimage);
            mBinding.shopname.setText(orderTwo.productName);
            mBinding.shopprice.setText("￥" + orderTwo.unitCost);
        }

        mBinding.setActivity(this);
        setEvaluationState(mBinding.praisesline);
        ScreenAdapterTools.getInstance().loadView((ViewGroup) getWindow().getDecorView());
        GridLayoutManager layoutManager = new GridLayoutManager(context, 4);
        mBinding.orderpictures.setLayoutManager(layoutManager);
        uploadPicAdapter = new UploadPicAdapter(context, imgelist, R.drawable.camera1);
        mBinding.orderpictures.setAdapter(uploadPicAdapter);
        uploadPicAdapter.setmOnRecyclerviewItemClickListener(new BaseRecycleAdapter.OnRecyclerviewItemClickListener() {
            @Override
            public void onItemClickListener(final View view, final int position) {
                final int[] num = {0};
                final String[] requestPermissionstr = {
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                };
                RxPermissions rxPermissions = new RxPermissions((Activity) context);
                rxPermissions.requestEach(requestPermissionstr)
                        .subscribe(new Consumer<Permission>() {
                            @Override
                            public void accept(Permission permission) throws Exception {
                                if (permission.granted) {
                                    num[0]++;
                                    if (num[0] == requestPermissionstr.length) {
                                        if (position == 0) {
                                            if (imgelist.size() <= 9) {
                                                startPopupWindow(view);
                                            } else {

                                            }


                                        } else {
                                            GetPictureHelp.PicturePreviewShow(context, imgelist, new GetPictureHelp.StateResh() {
                                                @Override
                                                public void callback() {
                                                    if (uploadPicAdapter != null) {
                                                        uploadPicAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            }, 0);
                                        }

                                    }
                                }
                            }
                        });

            }
        });

    }

    @Override
    protected void initData(ActivityEvaluationOrdersBinding mBinding) {

    }

    private Uri photoUri;
    private int FROM_CAMERA = 0;

    private void startPopupWindow(View view) {

        View pview = getLayoutInflater().inflate(R.layout.select_pic_layout,
                null, false);
        Button btn_take_photo = (Button) pview
                .findViewById(R.id.btn_take_photo);
        Button btn_pick_photo = (Button) pview
                .findViewById(R.id.btn_pick_photo);
        Button btn_cancel = (Button) pview.findViewById(R.id.btn_cancel);
        final PopupWindow distancePopup = initpoputwindow(pview);
        btn_take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoUri = GetPictureHelp.takePhoto(context);
                distancePopup.dismiss();
            }
        });
        btn_pick_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, YYRGSelectBaskImage.class);
                intent.putExtra("maxCount", 10);
                intent.putExtra("count", imgelist.size());
                startActivityForResult(intent, 1000);
                distancePopup.dismiss();

            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                distancePopup.dismiss();
            }
        });

        ShowPopWindow.darkenBackground(0.45f, context);
        distancePopup.showAtLocation(((Activity) context).getWindow().getDecorView(), Gravity.CENTER, 0, 0);

    }

    private PopupWindow initpoputwindow(View view) {
        PopupWindow distancePopup = new PopupWindow(view,
                android.view.WindowManager.LayoutParams.MATCH_PARENT,
                android.view.WindowManager.LayoutParams.WRAP_CONTENT, true);
        if (distancePopup != null) {
            distancePopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    ShowPopWindow.darkenBackground(1f, context);
                }
            });
        }
        distancePopup.setOutsideTouchable(true);
        distancePopup.setFocusable(true);
        distancePopup.setBackgroundDrawable(new BitmapDrawable());
        distancePopup.setAnimationStyle(R.style.popupanimation);
        return distancePopup;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == 1001) {
            Log.e("文件长度", "BitmapUtil.getPhotoInfos" + BitmapUtil.getPhotoInfos.size());
            for (int i = 0; i < BitmapUtil.getPhotoInfos.size(); i++) {
                Log.e("文件路径", "BitmapUtil.path" + BitmapUtil.getPhotoInfos.get(i).toString());
                imgelist.add(BitmapUtil.getPhotoInfos.get(i).getPath_file());
            }


        } else if (photoUri != null && requestCode == FROM_CAMERA) {
            Log.e("图片路径2", "photoUri" + GetPictureHelp.getFilePathByUri(context, photoUri));
            imgelist.add("file://" + GetPictureHelp.getFilePathByUri(context, photoUri));
        }
        uploadPicAdapter.notifyDataSetChanged();
    }


}
