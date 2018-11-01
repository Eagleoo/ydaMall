package com.mall.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.MD5;
import com.mall.util.MyPopWindow;
import com.mall.util.UserData;
import com.mall.util.Util;

import java.io.Serializable;

/**
 * 功能： 修改二级密码<br>
 * 时间： 2013-8-23<br>
 * 备注： <br>
 *
 * @author Lin.~
 */
public class UpdateTwoPwdFrame extends Activity {

    private EditText old1;

    private EditText new1;

    private EditText new2;

    private TextView submit;

    private TextView clear;
    private TextView click;

    public void submit(View v) {
        if (Util.isNull(old1.getText().toString())) {
            Util.show("请输入原交易密码。", this);
        } else if (Util.isNull(new1.getText().toString()))
            Util.show("请输入新的交易密码。", this);
        else if (Util.isNull(new2.getText().toString()))
            Util.show("请输入新的确认交易密码。", this);
        else if (!new1.getText().toString().equals(new2.getText().toString()))
            Util.show("新的交易密码不相同", this);
        else {
            Util.asynTask(this, "正在修改，请稍等...", new IAsynTask() {
                @Override
                public void updateUI(Serializable runData) {
                    if ("success".equals(runData + "")) {
                        String newPwd = new1.getText().toString();
                        newPwd = new MD5().getMD5ofStr(newPwd);
                        UserData.getUser().setSecondPwd(newPwd);
                        new MyPopWindow.MyBuilder(UpdateTwoPwdFrame.this,
                                "修改成功", "确定", new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent();
                                intent.setClass(UpdateTwoPwdFrame.this, AccountManagerFrame.class);
                                intent.putExtra("className", UpdateTwoPwdFrame.this.getClass().toString());
                                startActivity(intent);
                                finish();
                            }
                        }
                        ).setTitle("提示").build().showCenter();

                    } else
                        Util.show(runData + "", UpdateTwoPwdFrame.this);
                }

                @Override
                public Serializable run() {
                    String oldPwd = old1.getText().toString();
                    oldPwd = new MD5().getMD5ofStr(oldPwd);
                    String newPwd = new1.getText().toString();
                    newPwd = new MD5().getMD5ofStr(newPwd);
                    Web web = new Web(Web.updateTwoPwd2, "userId="
                            + UserData.getUser().getUserId() + "&md5Pwd="
                            + UserData.getUser().getMd5Pwd() + "&twoPwd="
                            + oldPwd + "&newPwd=" + newPwd);
                    return web.getPlan();
                }
            });
        }
    }

    public void clear(View v) {
        old1.setText("");
        new1.setText("");
        new2.setText("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.updatetwopwd_frame);
        initComponent();


    }

    private void initComponent() {
        Util.initTop(this, "修改交易密码", Integer.MIN_VALUE, null);
        old1 = Util.getEditText(R.id.uptwo_oldPwd, this);
        new1 = Util.getEditText(R.id.uptwo_newPwd1, this);
        new2 = Util.getEditText(R.id.uptwo_newPwd2, this);
        submit = Util.getTextView(R.id.uptwo_submit, this);
        clear = Util.getTextView(R.id.uptwo_clear, this);
        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                submit(v);
            }
        });

        clear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clear(v);
            }
        });
        click = Util.getTextView(R.id.click, this);
        click.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UpdateTwoPwdFrame.this, ForgetTradePasswordFrame.class);
                startActivity(intent);
                finish();
            }
        });
    }

}
