package com.mall.view.WithdrawalBusiness;


import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.Base.BaseActivity;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.view.VideoAudioDialog;
import com.google.gson.Gson;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.lin.component.CustomProgressDialog;
import com.mall.model.ReviewModle;
import com.mall.net.NewWebAPI;
import com.mall.net.WebRequestCallBack;
import com.mall.util.MD5;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.R;
import com.mall.view.ShowPopWindow;
import com.mall.view.databinding.WithdrawalBusinessBinding;
import com.yatoooon.screenadaptation.ScreenAdapterTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WithdrawalBusinessApplicationActivity extends BaseActivity<WithdrawalBusinessBinding> {
    interface Adaptercall {
        void call(ReviewModle.ListBean listBean);
    }

    WithdrawalBusinessAdapter withdrawalBusinessAdapter;
    List<ReviewModle.ListBean> reviews = new ArrayList<ReviewModle.ListBean>();


    @Override
    public int getContentViewId() {
        return R.layout.withdrawal_business;
    }

    @Override
    public View getattachedview() {
        return null;
    }

    @Override
    protected void initView(WithdrawalBusinessBinding mBinding) {
        ScreenAdapterTools.getInstance().loadView((ViewGroup) getWindow().getDecorView());
        mBinding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        initrecycle();
    }

    private void initrecycle() {
        withdrawalBusinessAdapter = new WithdrawalBusinessAdapter(context, reviews, new Adaptercall() {
            @Override
            public void call(final ReviewModle.ListBean listBean) {
                View mContentView = LayoutInflater.from(context).inflate(R.layout.popwindmenu, null);
                final TextView okTv = mContentView.findViewById(R.id.okTv);
                TextView onTv = mContentView.findViewById(R.id.onTv);
                TextView clearTv = mContentView.findViewById(R.id.clearTv);
                final PopupWindow mPopUpWindow = ShowPopWindow.showSharebottomWindow(mContentView, context, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, R.style.Animation);
                View.OnClickListener onClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPopUpWindow.dismiss();
                        String state = "";
                        switch (view.getId()) {
                            case R.id.okTv:
                                state = "1";
                                break;
                            case R.id.onTv:
                                state = "2";
                                break;
                            case R.id.clearTv:
                                state = "";
                                return;
                        }
                        final VideoAudioDialog dialog1 = new VideoAudioDialog(context);
                        dialog1.showdialogtag7(View.VISIBLE);
                        dialog1.setTitle("请输入支付密码");
                        dialog1.showContent(View.GONE);
                        dialog1.setRight("确认");
                        EditText editText = dialog1.getInput();
                        editText.setHint("请输入支付密码");
                        editText.setInputType(0x81);
                        dialog1.showcancel(View.GONE);
                        final String finalState = state;
                        dialog1.setLeft(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (Util.isNull(dialog1.getRemark())) {
                                    Util.show("密码不能为空");
                                    return;
                                }
                                toReview(dialog1.getRemark(), listBean.getCASHAPPLYID(), finalState, "");

                            }
                        });
                        dialog1.show();
                    }
                };

                okTv.setOnClickListener(onClickListener);
                onTv.setOnClickListener(onClickListener);
                clearTv.setOnClickListener(onClickListener);
            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.recyclerview.setLayoutManager(manager);
        mBinding.recyclerview.setAdapter(withdrawalBusinessAdapter);
        mBinding.recyclerview.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                getReviewList(true);
            }

            @Override
            public void onLoadMore() {
                getReviewList(false);
            }
        });

    }

    int page = 0;

    @Override
    protected void initData(WithdrawalBusinessBinding mBinding) {
        mBinding.recyclerview.refresh();
    }

    private void getReviewList(final boolean isrefresh) {
        page++;
        if (isrefresh) {
            page = 1;
        }
        Map<String, String> param = new HashMap<String, String>();
        String md5Pwd = UserData.getUser().getMd5Pwd();
        String userId = UserData.getUser().getUserId();
        param.put("userId", userId);
        param.put("md5Pwd", md5Pwd);
        param.put("pageSize", "10");
        param.put("page", page + "");
        param.put("state", "");
        param.put("type_", "");
        NewWebAPI.getNewInstance().getWebRequest("/User.aspx?call=getcity_ceo_tx_list", param,
                new WebRequestCallBack() {
                    @Override
                    public void success(Object result) {
                        super.success(result);
                        if (null == result) {
                            Util.show("网络异常，请重试！", context);
                            return;
                        }

                        JSONObject json = JSON.parseObject(result.toString());
                        int code = json.getIntValue("code");
                        String message = json.getString("message");
                        if (code != 200) {
                            Util.show(message);
                            return;
                        }
                        Gson gson = new Gson();
                        ReviewModle reviewModle = gson.fromJson(result.toString(), ReviewModle.class);
                        if (isrefresh) {
                            reviews.clear();
                        }
                        reviews.addAll(reviewModle.getList());
                        withdrawalBusinessAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void requestEnd() {
                        super.requestEnd();
                        if (isrefresh) {
                            mBinding.recyclerview.refreshComplete();
                        } else {
                            mBinding.recyclerview.loadMoreComplete();
                        }
                    }
                });
    }

    private void toReview(String towpwd, String id, String state, String remark) {
        final CustomProgressDialog cpd = CustomProgressDialog.showProgressDialog(this, "请求中...");
        Map<String, String> param = new HashMap<String, String>();
        String md5Pwd = UserData.getUser().getMd5Pwd();
        String userId = UserData.getUser().getUserId();
        param.put("userId", userId);
        param.put("md5Pwd", md5Pwd);
        param.put("towpwd", new MD5().getMD5ofStr(towpwd));
        param.put("id", id);
        param.put("state", state);//1为通过，2为驳回
        param.put("remark", "");
        NewWebAPI.getNewInstance().getWebRequest("/User.aspx?call=audit_tixian", param,
                new WebRequestCallBack() {
                    @Override
                    public void success(Object result) {
                        super.success(result);
                        if (null == result) {
                            Util.show("网络异常，请重试！", context);
                            return;
                        }

                        JSONObject json = JSON.parseObject(result.toString());
                        int code = json.getIntValue("code");
                        String message = json.getString("message");
                        if (code != 200) {
                            Util.show(message);
                            return;
                        }
                        Util.show(message);

                        mBinding.recyclerview.refresh();


                    }

                    @Override
                    public void requestEnd() {
                        super.requestEnd();
                        cpd.dismiss();

                    }
                });
    }
}
