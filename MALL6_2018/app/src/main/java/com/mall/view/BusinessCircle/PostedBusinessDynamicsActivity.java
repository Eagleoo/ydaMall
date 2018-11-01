package com.mall.view.BusinessCircle;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.mall.BasicActivityFragment.BasicActivity;
import com.mall.MessageEvent;
import com.mall.view.PicturePreviewActivity;
import com.mall.view.R;
import com.mall.yyrg.adapter.NewSelcctPhotoAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import cn.finalteam.rxgalleryfinal.RxGalleryFinal;
import cn.finalteam.rxgalleryfinal.bean.MediaBean;
import cn.finalteam.rxgalleryfinal.imageloader.ImageLoaderType;
import cn.finalteam.rxgalleryfinal.rxbus.RxBusResultDisposable;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageMultipleResultEvent;
import cn.finalteam.rxgalleryfinal.ui.RxGalleryListener;
import cn.finalteam.rxgalleryfinal.ui.base.IMultiImageCheckedListener;

public class PostedBusinessDynamicsActivity extends BasicActivity {


    @BindView(R.id.add_bask_image)
    public com.mall.yyrg.adapter.MyGridView myaddimage;

    NewSelcctPhotoAdapter newSelcctPhotoAdapter;

    private List<MediaBean> listlast = new ArrayList<>();

    RxGalleryFinal rxGalleryFinal;

    int maxpiclength=9;



    @Override
    public int getContentViewId() {
        return R.layout.activity_posted_business_dynamics;
    }

    @Override
    public void initAllMembersView(Bundle savedInstanceState) {
        init();

    }

    private void init() {
        setPicselect();
        initAdapter();
    }

    private void initAdapter() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        MediaBean mediaBean = new MediaBean();
        listlast.add(mediaBean);
        newSelcctPhotoAdapter= new NewSelcctPhotoAdapter(context, listlast, width * 2 / 9);
        myaddimage.setAdapter(newSelcctPhotoAdapter);
    }

    @Override
    public void EventCallBack(MessageEvent messageEvent) {

    }

    @OnClick({R.id.top_back,R.id.topright})
    public void  click(View view){
        switch (view.getId()){
            case R.id.top_back:
                finish();
                break;
            case R.id.topright:
                break;
        }
    }

    @OnItemClick({R.id.add_bask_image})
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

        Log.e("点击预览1","position"+position);
        if (position==0){
            if (listlast.size()==maxpiclength+1){
                Toast.makeText(getBaseContext(), "你最多只能选择" + maxpiclength + "张图片", Toast.LENGTH_SHORT).show();
                return;
            }
            openMulti();
        }else{
            Log.e("点击预览2","position"+position);
            Intent intent=new Intent(context, PicturePreviewActivity.class);
            Log.e("点击预览3","position"+position);
            intent.putExtra(PicturePreviewActivity.POSITION,position);
            Log.e("点击预览4","position"+position);
                listlast.remove(0);
            Log.e("点击预览5","position"+position);
//            getParcelableArrayListExtra
            intent.putParcelableArrayListExtra(PicturePreviewActivity.MEDIABEANLIST,(ArrayList<? extends Parcelable>) listlast);
            Log.e("点击预览6","position"+position);

            startActivityForResult(intent,111);
//            startActivity(intent);
            Log.e("点击预览7","position"+position);
        }

    }

//    int resultCode, Intent data
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==111){

            if (resultCode==123){
                listlast.clear();
                listlast.add(0,new MediaBean());
                ArrayList<MediaBean> list=data.getParcelableArrayListExtra(PicturePreviewActivity.MEDIABEANLIST);
                listlast.addAll(list);
                newSelcctPhotoAdapter.notifyDataSetChanged();
            }

        }
    }

    private void setPicselect(){
         rxGalleryFinal = RxGalleryFinal
                .with(context)
                .image()
                .multiple();
        //多选事件的回调
        RxGalleryListener
                .getInstance()
                .setMultiImageCheckedListener(
                        new IMultiImageCheckedListener() {
                            @Override
                            public void selectedImg(Object t, boolean isChecked) {
//                                Toast.makeText(getBaseContext(), isChecked ? "选中" : "取消选中", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void selectedImgMax(Object t, boolean isChecked, int maxSize) {
//                                Toast.makeText(getBaseContext(), "你最多只能选择" + maxSize + "张图片", Toast.LENGTH_SHORT).show();
                            }
                        });
    }

    private void openMulti() {

        if (listlast != null && !listlast.isEmpty()) {
            listlast.remove(0);
            rxGalleryFinal
                    .selected(listlast);
        }
        rxGalleryFinal.maxSize(maxpiclength)
                .imageLoader(ImageLoaderType.PICASSO)
                .subscribe(new RxBusResultDisposable<ImageMultipleResultEvent>() {

                    @Override
                    protected void onEvent(ImageMultipleResultEvent imageMultipleResultEvent) throws Exception {
                        List<MediaBean> list=imageMultipleResultEvent.getResult();
                        Log.e("长度","listlast10"+list.size());
                        for (int i=0;i<list.size();i++){
                            MediaBean bean=list.get(i);
                            Log.e("数据："+i,bean.toString()+"KL");
                        }

                        listlast.clear();
                        if (list.size()!=maxpiclength){
                            Log.e("长度","listlast11"+list.size());
                            listlast.add(new MediaBean());
                        }
                        listlast.addAll(list);
                        Log.e("最后长度","listlast13"+listlast.size());
                        newSelcctPhotoAdapter.notifyDataSetChanged();
//                        Toast.makeText(getBaseContext(), "已选择" + imageMultipleResultEvent.getResult().size() + "张图片", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        Toast.makeText(getBaseContext(), "OVER", Toast.LENGTH_SHORT).show();
                    }
                })
                .openGallery();
    }
}
