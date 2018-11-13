package com.hzkc.parent.activity;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hzkc.parent.Bean.ImageInf;
import com.hzkc.parent.R;
import com.hzkc.parent.adapter.ImageListAdapter;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.mina.Constants;
import com.hzkc.parent.utils.CompressPicture;
import com.hzkc.parent.utils.DensityUtil;
import com.hzkc.parent.utils.FileStorage;
import com.hzkc.parent.utils.LogUtil;
import com.hzkc.parent.utils.MultipartRequest;
import com.hzkc.parent.utils.ToastUtils;
import com.hzkc.parent.view.RecyclerViewItemSpaces;
import com.jaeger.library.StatusBarUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


/**
 * Created by Administrator on 2016/7/23.
 */

public class HandleAcitivity extends BaseActivity implements ImageListAdapter.OnAddImageClickListener, View.OnClickListener,EasyPermissions.PermissionCallbacks {
    //取消
    private TextView cancal;
    //发送
    private TextView ideaSend;

    private RecyclerView mImgRecyclerView;
    //内容
    private EditText content;

    private ImageView addImg;

    private ArrayList<ImageInf> mSelectedImages = new ArrayList<>();
    //上下文
    private Context mContext;

    public static final int REQUEST_PICTURE = 108;

    private String parentUUID;
    private String nc;
    private String phone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handle);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.blue));
        initView();
        initData();
    }

    private void initData() {
        parentUUID = sp.getString("parentUUID", "");
        phone = sp.getString("phoneNum", "");
        nc = sp.getString("nc", "");
        //nc = phone.substring(0, 3) + "*****" + phone.substring(8);
    }

    private void initView() {
        cancal = (TextView) findViewById(R.id.tv_cancal);
        ideaSend = (TextView) findViewById(R.id.idea_send);
        mImgRecyclerView = (RecyclerView) findViewById(R.id.ImgList);
        content = (EditText) findViewById(R.id.content);
        addImg = (ImageView) findViewById(R.id.picture);
        mContext = this;
        cancal.setOnClickListener(this);
        ideaSend.setOnClickListener(this);
        addImg.setOnClickListener(this);
    }


    public int a=1;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.picture://点击图片
                showDiglog();
                break;
            case R.id.idea_send://发送按钮
                String text = content.getText().toString().trim();
                if (TextUtils.isEmpty(text)) {
                    ToastUtils.showToast(MyApplication.getContext(),"内容不能为空");
                    return;
                }
                //发送图片和文本
                if(a==1){
                    ++a;
                    send();
                    finish();
                }
                break;
            case R.id.tv_cancal://取消按钮
                finish();
                break;
        }
    }

    /**
     * 二维码实现逻辑
     */
    @AfterPermissionGranted(REQUEST_PICTURE)
    private void pictureTask() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent i1 = new Intent(this, AlbumActivity.class);
            i1.putParcelableArrayListExtra("selectedImgList", mSelectedImages);
            startActivityForResult(i1, 0);
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, "需要请求使用图片权限",
                    REQUEST_PICTURE, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (data != null) {
                    mSelectedImages = data.getParcelableArrayListExtra("selectImgList");
                    //ToastUtils.showToast(mContext, "选择了几张照片" + mSelectedImages.size());
                    initImgList();
                }
                break;
            case CROP_PHOTO:
                if(resultCode==RESULT_OK){
                    try {
                        mSelectedImages.add(new ImageInf(takePhonefile,false));
                        initImgList();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this,"程序崩溃",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    LogUtil.e("ChangeNcAndIocnActivity","e取消");
                }
                break;
        }
    }

    /**
     * 初始化照片
     */
    private void initImgList() {
        mImgRecyclerView.setVisibility(View.VISIBLE);
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 3);
        mImgRecyclerView.setLayoutManager(layoutManager);
        ImageListAdapter imageListAdapter = new ImageListAdapter(mSelectedImages, mContext);
        imageListAdapter.setOnAddImageClickListener(this);
        mImgRecyclerView.addItemDecoration(new RecyclerViewItemSpaces(0, DensityUtil.dip2px(HandleAcitivity.this,3), 0, 0));
        mImgRecyclerView.setAdapter(imageListAdapter);
    }

    /**
     * 添加照片
     */
    @Override
    public void OnAddImageClick() {
//        Intent intent = new Intent(this, AlbumActivity.class);
//        intent.putParcelableArrayListExtra("selectedImgList", mSelectedImages);
//        startActivityForResult(intent, 0);
        showDiglog();
    }

    /**
     * 点击发送
     */
    private void send() {
        sp.edit().putString("refresh","refresh").commit();
        if (mSelectedImages != null && mSelectedImages.size() > 0) {
            try {
                sendTextImgContext();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            try {
                sendTextContext();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送文本
     * text：说说内容
     * nc：昵称
     * ncid：发布人id
     * ncpic：发布人头像编号
     * picbz：是否有照片标志
     */
    private void sendTextContext() throws UnsupportedEncodingException {
        String text = content.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            ToastUtils.showToast(MyApplication.getContext(),"内容不能为空");
            return;
        }
        String url = Constants.FIND_URL_SEND_PYQ+"text=" + URLEncoder.encode(text,"UTF-8") + "&nc=" + URLEncoder.encode(nc,"UTF-8") + "&ncid="
                + parentUUID + "&ncpic=" + phone.substring(phone.length()-2) + "&picbz=" + 0;
        //请求朋友圈列表
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ToastUtils.showToast(MyApplication.getContext(),"发送成功");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtils.showToast(MyApplication.getContext(),"发送失败");
            }
        });
        MyApplication.getRequestQueue().add(request);
    }

    /**
     * 发送文本和图片
     */
    private void sendTextImgContext() throws UnsupportedEncodingException {
        String text = content.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            ToastUtils.showToast(MyApplication.getContext(),"内容不能为空");
            return;
        }
        final Map<String, String> params = new HashMap<String, String>();
        final List<File> f = new ArrayList<>();
        for (int i = 0; i < mSelectedImages.size(); i++) {
            LogUtil.e("File","File："+mSelectedImages.get(i).getImageFile());

            String path = CompressPicture.dealPicture(mSelectedImages.get(i).getImageFile().getAbsolutePath(), 800, 480, "jpg");
            File file =  new File(path);
            f.add(file);
            //f.add(mSelectedImages.get(i).getImageFile());
        }
        String url2 = Constants.FIND_URL_SEND_PYQ+"text=" + URLEncoder.encode(text,"UTF-8") + "&nc=" + URLEncoder.encode(nc,"UTF-8") + "&ncid="
                + parentUUID + "&ncpic=" + phone.substring(phone.length()-2) + "&picbz=" + 1;
        Log.e("url2","url2"+url2);
        //请求朋友圈列表
        StringRequest request2 = new StringRequest(Request.Method.GET, url2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, "onResponse: 123123123:"+response);
                        ToastUtils.showToast(MyApplication.getContext(),"发送成功");
                        final String url = Constants.FIND_URL_SEND_IMG+"id="+response; //换成自己的测试url地址
                        LogUtil.e("url","url:"+url);
                        for (int i = 0; i < f.size(); i++) {
                            MultipartRequest request=new MultipartRequest(url, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    LogUtil.e("onResponse","onResponse111："+error);
                                }
                            }, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    LogUtil.e("onResponse","onResponse222："+response);
                                }
                            },"upfile",f.get(i),params);
                            MyApplication.getRequestQueue().add(request);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtils.showToast(MyApplication.getContext(),"发送失败");
            }
        });
        MyApplication.getRequestQueue().add(request2);
    }

    public void uploadImage(final String http_url, final File file){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!file.exists()) {
                        Log.i("错误", "文件不存在");
                    }

                    HttpClient client = new DefaultHttpClient();

                    HttpPost post = new HttpPost(http_url);

                    FileBody fileBody = new FileBody(file, "image/jpeg");
                    MultipartEntity entity = new MultipartEntity();

                    //entity.addPart("uploadedfile", fileBody);//uploadedfile是图片上传的键值名
                    //entity.addPart("key_app", new StringBody("1", Charset.forName("UTF-8")));//设置要传入的参数，key_app是键值名,此外传参时候需要指定编码格式

                    post.setEntity(entity);

                    HttpResponse response = client.execute(post);

                    if (response.getStatusLine().getStatusCode() == 200) {

                        HttpEntity httpEntity = response.getEntity();

                        String result = EntityUtils.toString(httpEntity, "utf-8");

                        Log.e("返回的结果",result);
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 成功
     * */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        //Toast.makeText(this, "执行onPermissionsGranted()...", Toast.LENGTH_SHORT).show();
    }
    /**
     * 失败
     * */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        //Toast.makeText(this, "执行onPermissionsDenied()...", Toast.LENGTH_SHORT).show();
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this, "当前App需要申请查看图片权限,需要打开设置页面么?")
                    .setTitle("权限申请")
                    .setPositiveButton("确认")
                    .setNegativeButton("取消", null /* click listener */)
                    .setRequestCode(REQUEST_PICTURE)
                    .build()
                    .show();
        }
    }

    /**
     * 拍照或是选相册弹出框
     */
    private void showDiglog() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.layout_pop_paizhao, null);
        final Dialog dialog = new Dialog(HandleAcitivity.this, R.style.alert_dialog);
        dialog.setContentView(view);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.FILL_PARENT);
        view.findViewById(R.id.tv_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.tv_paizhao).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhone();
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.tv_choose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pictureTask();
                dialog.dismiss();
            }
        });
        dialog.show();
    }



    /**
     * 调用照相机
     * */
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE2 = 7;
    public void takePhone(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_CALL_PHONE2);

        }else {
            takePhoto();
        }
    }

    /**
     * 拍照
     */
    private File takePhonefile;
    private Uri imageUri;
    private static final int CROP_PHOTO = 2;
    public void takePhoto(){
        //适配7.0
        takePhonefile = new FileStorage().createIconFile();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(HandleAcitivity.this, "com.hzkc.parent.fileprovider", takePhonefile);//通过FileProvider创建一个content类型的Uri
        } else {
            imageUri = Uri.fromFile(takePhonefile);
        }
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
        }
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将拍取的照片保存到指定URI
        startActivityForResult(intent, CROP_PHOTO);
    }
}
