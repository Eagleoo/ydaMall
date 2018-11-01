package com.mall.officeonline;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bpj.lazyfragment.MainActivity;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mall.model.AlbumClassifyModel;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.LoginFrame;
import com.mall.view.R;

public class AlbumList extends Activity {
    @ViewInject(R.id.listview)
    private ListView listview;
    private ItemAdapter adapter;
    @ViewInject(R.id.topright)
    private ImageView topright;
    private String officeid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_list);
        ViewUtils.inject(this);
        officeid = this.getIntent().getStringExtra("offid");
        init();
    }

    private void init() {
        if (UserData.getUser() != null && UserData.getOfficeInfo() != null) {// 未登录
            if (!UserData.getUser().getUserIdNoEncodByUTF8().equals(officeid)) {
                initTopone();
            } else {
                initTopTwo();
            }
        } else {
            initTopone();
        }
        getClassifyId();
    }

    private void initTopone() {
        Util.initTitle(this, "空间相册", new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AlbumList.this, MainActivity.class);
                AlbumList.this.setResult(2, intent);
                AlbumList.this.finish();
            }
        });
    }

    private void initTopTwo() {
        Util.initTitle(this, "空间相册", new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AlbumList.this, MainActivity.class);
                AlbumList.this.setResult(2, intent);
                AlbumList.this.finish();
            }
        }, new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserData.getUser() != null
                        && UserData.getOfficeInfo() != null) {
                    if (UserData.getUser().getUserIdNoEncodByUTF8()
                            .equals(UserData.getOfficeInfo().getUserid())) {
                        addclass();
                    }
                }

            }
        }, R.drawable.note_add);
    }

    private void addclass() {
        LayoutInflater infl = LayoutInflater.from(AlbumList.this);
        View view = infl.inflate(R.layout.add_album_classify, null);
        final EditText pwd = (EditText) view.findViewById(R.id.second_pwd);
        pwd.setFocusable(true);
        Button submit = (Button) view.findViewById(R.id.submit);
        Button cancel = (Button) view.findViewById(R.id.cancel);
        final Dialog ad = new Dialog(AlbumList.this, R.style.CustomDialogStyle);
        ad.show();
        Window window = ad.getWindow();
        WindowManager.LayoutParams pa = window.getAttributes();
        pa.width = Util.dpToPx(AlbumList.this, 250);
        window.setBackgroundDrawable(new ColorDrawable(0));
        window.setContentView(view, pa);
        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Util.isNull(pwd.getText().toString())) {
                    Toast.makeText(AlbumList.this, "请输入相册名称", Toast.LENGTH_LONG)
                            .show();
                }
                addAlbumClassify(UserData.getOfficeInfo().getOffice_id(), pwd
                        .getText().toString());
                ad.dismiss();
            }
        });
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.dismiss();
            }
        });
    }

    private void addAlbumClassify(final String officeid, final String typename) {
        if (UserData.getUser() == null) {
            Util.showIntent(this, LoginFrame.class);
        }
        if (UserData.getOfficeInfo() != null) {
            Util.asynTask(this, "", new IAsynTask() {
                @Override
                public void updateUI(Serializable runData) {
                    if (runData != null) {
                        if ("ok".equals(runData + "")) {
                            Toast.makeText(AlbumList.this, "增加分类成功",
                                    Toast.LENGTH_LONG).show();
                            adapter.list.clear();
                            getClassifyId();
                        }
                    } else {
                        Toast.makeText(AlbumList.this, "增加分类失败",
                                Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public Serializable run() {
                    Web web = new Web(Web.officeUrl, Web.AddOfficePhotoClass,
                            "officeid=" + officeid + "&typename=" + typename
                                    + "&userID="
                                    + UserData.getUser().getUserId()
                                    + "&userPaw="
                                    + UserData.getUser().getMd5Pwd());
                    return web.getPlan();
                }
            });
        }
    }

    private void getClassifyId() {
        if (UserData.getOfficeInfo() != null) {
            Util.asynTask(this, "", new IAsynTask() {
                @Override
                public void updateUI(Serializable runData) {
                    if (runData != null) {
                        HashMap<Integer, List<AlbumClassifyModel>> map = (HashMap<Integer, List<AlbumClassifyModel>>) runData;
                        List<AlbumClassifyModel> list = map.get(1);
                        if (list != null && list.size() > 0) {
                            if (adapter == null) {
                                adapter = new ItemAdapter(AlbumList.this);
                                listview.setAdapter(adapter);
                            }
                            for (int i = 0; i < list.size(); i++) {
                                Log.e("getClassifyId", list.get(i).getPhotoTypeid());
                            }
                            adapter.setList(list);
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(AlbumList.this, "没有获取到相册分类",
                                Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public Serializable run() {
                    Web web = new Web(Web.officeUrl, Web.GetOfficePhotoClass,
                            "officeid="
                                    + UserData.getOfficeInfo().getOffice_id());
                    List<AlbumClassifyModel> list = web
                            .getList(AlbumClassifyModel.class);
                    HashMap<Integer, List<AlbumClassifyModel>> map = new HashMap<Integer, List<AlbumClassifyModel>>();
                    map.put(1, list);
                    return map;
                }
            });
        }
    }

    public class ItemAdapter extends BaseAdapter {
        private Context c;
        private LayoutInflater inflater;
        private List<AlbumClassifyModel> list = new ArrayList<AlbumClassifyModel>();

        public ItemAdapter(Context c) {
            this.c = c;
            inflater = LayoutInflater.from(c);
        }

        public void notifyClassData() {
            this.list.clear();
            this.notifyDataSetChanged();
        }

        public void setList(List<AlbumClassifyModel> list) {
            this.list = list;
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return this.list.size();
        }

        @Override
        public Object getItem(int position) {
            return this.list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            AlbumClassifyModel al = this.list.get(position);
            ViewHolder h = null;
            if (convertView == null) {
                h = new ViewHolder();
                convertView = inflater.inflate(R.layout.album_list_item, null);
                h.name = (TextView) convertView.findViewById(R.id.albumbnane);
                convertView.setTag(h);
            } else {
                h = (ViewHolder) convertView.getTag();
            }
            h.name.setText(al.getPhotoTypeName());
            final AlbumClassifyModel acm = al;
            h.name.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(c, AlbumFrame.class);
                    intent.putExtra("classID", acm.getPhotoTypeid());
                    intent.putExtra("officeuseid", officeid);
                    c.startActivity(intent);
                }
            });
            h.name.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    System.out.println("--------------setOnLongClickListener-----------");
                    if (UserData.getUser() != null && UserData.getOfficeInfo() != null) {
                        if (UserData.getUser().getUserIdNoEncodByUTF8().equals(UserData.getOfficeInfo().getUserid())) {
                            Util.showChoosedDialog(AlbumList.this, "是否要删除该分类？",
                                    "点错了", "确定删除", new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            deleteAlbumClass(position, acm.getPhotoTypeid());
                                        }
                                    });
                        }
                    }
                    return true;
                }
            });
            return convertView;
        }

    }

    private void deleteAlbumClass(final int position, final String cateId) {
        Util.asynTask(AlbumList.this, "", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (runData != null) {
                    if ("ok".equals(runData + "")) {
                        Toast.makeText(AlbumList.this, "删除成功", Toast.LENGTH_LONG).show();
                        adapter.list.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(AlbumList.this, "删除失败", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.officeUrl, Web.DelOfficeUserPhotoClass,
                        "userID=" + UserData.getUser().getUserId() + "&userPaw=" + new UserData().getUser().getMd5Pwd()
                                + "&officeid=" + UserData.getOfficeInfo().getOffice_id() + "&cateId=" + cateId);
                return web.getPlan();
            }
        });
    }

    public class ViewHolder {
        TextView name;
    }
}
