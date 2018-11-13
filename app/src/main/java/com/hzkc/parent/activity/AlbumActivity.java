package com.hzkc.parent.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hzkc.parent.Bean.AlbumFolderInfo;
import com.hzkc.parent.Bean.ImageInf;
import com.hzkc.parent.R;
import com.hzkc.parent.adapter.GridViewAdapter;
import com.hzkc.parent.utils.ImageScan;
import com.hzkc.parent.view.ImageFolderPopWindow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/23.
 */

public class AlbumActivity extends BaseActivity implements ImageFolderPopWindow.OnFolderClickListener, GridViewAdapter.OnSelectedImgListener, View.OnClickListener {
    //取消
    private TextView cancal;
    //完成
    private TextView next;

    private LinearLayout folder;

    private GridView gridview;

    private RelativeLayout toolbarLayout;

    private TextView foldername;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            setAlreadySelectedImg();
            initGridView();
        }
    };
    private Context mContext;
    private ArrayList<AlbumFolderInfo> mAlbumFolderInfos;
    private ImageFolderPopWindow imageFolderPopWindow;
    private ArrayList<ImageInf> mSelectedImages;
    private int mCurrentFolder = 0;
    GridViewAdapter gridViewAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_select);
        initView();
    }

    private void initView() {
        cancal= (TextView) findViewById(R.id.cancal);
        next= (TextView) findViewById(R.id.next);
        folder= (LinearLayout) findViewById(R.id.folder);
        gridview= (GridView) findViewById(R.id.gridview);
        folder= (LinearLayout) findViewById(R.id.folder);
        toolbarLayout= (RelativeLayout) findViewById(R.id.toolbar_layout);
        foldername= (TextView) findViewById(R.id.foldername);

        mSelectedImages = getIntent().getParcelableArrayListExtra("selectedImgList");
        mContext = this;
        initImageScan();
        changeNextButtonState();

        cancal.setOnClickListener(this);
        next.setOnClickListener(this);
        folder.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancal:
                finish();
                break;
            case R.id.next:
                //ToastUtils.showToast(mContext, "选择完成");
                Intent i1 = new Intent();
                i1.putParcelableArrayListExtra("selectImgList", mSelectedImages);
                setResult(0, i1);
                finish();
                break;
            case R.id.folder:
                initImageFolderpopWindow();
                break;
            default:
            break;
        }
    }

    private void setAlreadySelectedImg() {
        if (mSelectedImages == null || mSelectedImages.size() == 0) {
            return;
        }
        List<ImageInf> allImagelist = mAlbumFolderInfos.get(0).getImageInfoList();
        for (ImageInf imageInf : mSelectedImages) {
            String selectPath = imageInf.getImageFile().getAbsolutePath();
            for (int i = 0; i < allImagelist.size(); i++) {
                if (allImagelist.get(i).getImageFile().getAbsolutePath().equals(selectPath)) {
                    allImagelist.get(i).setSecleted(true);
                }
            }
        }
    }

    private void initImageFolderpopWindow() {
        imageFolderPopWindow = new ImageFolderPopWindow(mContext, mAlbumFolderInfos);
        imageFolderPopWindow.showAsDropDown(toolbarLayout);
        imageFolderPopWindow.setOnFolderClickListener(this);

    }

    private void initImageScan() {
        new ImageScan(mContext, getSupportLoaderManager()) {
            @Override
            public void scanFinish(ArrayList<AlbumFolderInfo> folderLists) {
                //ToastUtils.showToast(mContext, "加载完毕" + folderLists.size());
                mAlbumFolderInfos = folderLists;
                mHandler.sendEmptyMessage(1);
            }
        };
    }

    private void initGridView() {
        gridViewAdapter = new GridViewAdapter(mContext, mAlbumFolderInfos.get(mCurrentFolder).getImageInfoList(), mSelectedImages);
        gridview.setAdapter(gridViewAdapter);
        gridViewAdapter.setOnSelectedImgListener(this);

    }

    /**
     * Folder点击的回调
     */
    @Override
    public void onFolderClick(int position) {
        //ToastUtils.showToast(AlbumActivity.this,mAlbumFolderInfos.get(position).getFolderName() + "回调");
        mCurrentFolder = position;
        setCurrentFolderName();
        initGridView();
        imageFolderPopWindow.dismiss();

    }

    private void setCurrentFolderName() {
        foldername.setText(mAlbumFolderInfos.get(mCurrentFolder).getFolderName());
    }

    @Override
    public void selectedImg(ArrayList<ImageInf> imageInfs) {
        mSelectedImages = imageInfs;
        changeNextButtonState();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)//api需要大于16，当前最小minsdk为15
    private void changeNextButtonState() {
        if (mSelectedImages == null) {
            return;
        }
        if (mSelectedImages.size() > 0) {
            //next.setBackground(getResources().getDrawable(R.drawable.compose_send_corners_highlight_bg));
            next.setText("下一步(" + mSelectedImages.size() + ")");
            next.setClickable(true);
            next.setTextColor(getResources().getColor(R.color.white));
        } else {
            //next.setBackground(getResources().getDrawable(R.drawable.compose_send_corners_bg));
            next.setText("下一步");
            next.setClickable(false);
            next.setTextColor(getResources().getColor(R.color.white));
        }
    }

    @Override
    public void disSelectedImg(ArrayList<ImageInf> imageInfs) {
        mSelectedImages = imageInfs;
        changeNextButtonState();
    }
}
