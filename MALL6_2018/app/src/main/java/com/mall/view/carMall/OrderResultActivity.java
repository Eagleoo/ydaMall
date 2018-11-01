package com.mall.view.carMall;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mall.util.Util;
import com.mall.view.*;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderResultActivity extends AppCompatActivity {

    int result;//0成功,1失败

    @BindView(R.id.done)
    TextView done;
    @BindView(R.id.fail)
    MoreTextView fail;
    @BindView(R.id.success)
    MoreTextView success;
    @BindView(R.id.number_order)
    TextView number_order;
    @BindView(R.id.type_order)
    TextView type_order;
    @BindView(R.id.date_order)
    TextView date_order;
    @BindView(R.id.mButton)
    Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);
        result = Integer.parseInt(getIntent().getStringExtra("result"));
        type_order.setText(Util.payType);
        number_order.setText(getIntent().getStringExtra("number_order"));
        date_order.setText(getIntent().getStringExtra("date_order"));
        if (result == 0) {
            fail.setVisibility(View.GONE);
            success.setVisibility(View.VISIBLE);
            mButton.setText("完成");
            done.setVisibility(View.VISIBLE);
        } else if (result == 1) {
            fail.setVisibility(View.VISIBLE);
            success.setVisibility(View.GONE);
            mButton.setText("重新支付");
            done.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.done, R.id.mButton})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.done:
                Util.showIntent(OrderResultActivity.this, Lin_MainFrame.class, new String[]{"toTab"}, new String[]{"usercenter"});
                finish();
                break;
            case R.id.mButton:
                if (result == 0) {
                    Util.showIntent(OrderResultActivity.this, Lin_MainFrame.class, new String[]{"toTab"}, new String[]{"usercenter"});
//
                } else if (result == 1) {
                    finish();
                }
                finish();
                break;
        }
    }
}
