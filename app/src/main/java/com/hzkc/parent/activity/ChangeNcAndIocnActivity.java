package com.hzkc.parent.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hzkc.parent.R;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.event.ChangeNCEvent;
import com.hzkc.parent.event.UpdateNcEvent;
import com.hzkc.parent.mina.Constants;
import com.hzkc.parent.utils.CompressPicture;
import com.hzkc.parent.utils.FileStorage;
import com.hzkc.parent.utils.GlideCircleTransform;
import com.hzkc.parent.utils.LogUtil;
import com.hzkc.parent.utils.MultipartRequest;
import com.hzkc.parent.utils.MyUtils;
import com.hzkc.parent.utils.ToastUtils;
import com.jaeger.library.StatusBarUtil;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ChangeNcAndIocnActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvTopTitle;
    private TextView tvMyNc;
    private ImageView ivFinish;
    private EditText etMyNc;
    private TextView btComfirm;
    private ImageView ivMineChildIcon;
    private String userid;
    private String isClickCamera;

    private static final int CROP_PHOTO = 2;
    private static final int REQUEST_CODE_PICK_IMAGE=3;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 6;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE2 = 7;

    private Uri imageUri;
    private Uri imagePath;
    private ProgressDialog pd;
    private File takePhonefile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_nc_and_iocn);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.blue));
        EventBus.getDefault().register(this);
        isClickCamera="0";
        initView();
    }
    private void initView() {
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvMyNc = (TextView) findViewById(R.id.tv_my_nc);
        ivFinish = (ImageView) findViewById(R.id.iv_finish);
        etMyNc = (EditText) findViewById(R.id.et_my_nc);
        btComfirm = (TextView) findViewById(R.id.bt_comfirm);
        ivMineChildIcon = (ImageView) findViewById(R.id.iv_mine_child_icon);
        ivFinish.setVisibility(View.VISIBLE);

        String nc = sp.getString("nc", "");
        String txid = sp.getString("txid", "");
        userid = sp.getString("parentUUID", "");

        Glide.with(this).load(Constants.FIND_URL_TX+txid+".jpg").transform(new GlideCircleTransform(this)).diskCacheStrategy(DiskCacheStrategy.ALL).into(ivMineChildIcon);

        String extra = getIntent().getStringExtra("join");

        tvMyNc.setText("当前昵称为： "+nc);
        tvTopTitle.setText("修改个人资料");
        ivFinish.setOnClickListener(this);
        btComfirm.setOnClickListener(this);
        //修改头像
        ivMineChildIcon.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_finish:
                finish();
                break;
            case R.id.bt_comfirm:
                try {
                    changeNc();
                    //修改头像
//                    changeIcon();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.iv_mine_child_icon:
                clickMyIcon();
                break;
            default:
                break;
        }
    }

    /**
     * 点击头像
     * */
    private void clickMyIcon() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_select_photo, null);
        TextView tv_select_gallery = (TextView) view.findViewById(R.id.tv_select_gallery);
        TextView tv_select_camera = (TextView) view.findViewById(R.id.tv_select_camera);
        tv_select_gallery.setOnClickListener(new View.OnClickListener() {// 在相册中选取
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                choosePhone();
            }
        });
        tv_select_camera.setOnClickListener(new View.OnClickListener() {// 调用照相机
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                takePhone();
            }
        });
        dialog.setView(view);
        dialog.show();
    }

    /**
     * 修改昵称
     * */
    public  String nc;
    private void changeNc() throws UnsupportedEncodingException {
        nc = etMyNc.getText().toString().trim();
        if(TextUtils.isEmpty(nc)){
            ToastUtils.showToast(MyApplication.getContext(),"昵称不能为空");
            return;
        }

        EventBus.getDefault().post(new ChangeNCEvent(nc), "changeNc");
    }
    /**
     * 修改头像
     * */
    private void changeIcon() throws FileNotFoundException {
        pd = new ProgressDialog(this);
        pd.setMessage("正在修改数据中，请稍候");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(false);
        pd.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                    pd.dismiss();
                }
                return false;
            }
        });
        File f;
        LogUtil.e("changeIcon","imagePath:"+imagePath);
        if(isClickCamera.equals("1")){//选择照片
            f = MyUtils.getFileByUri(ChangeNcAndIocnActivity.this,imagePath);
        }else if(isClickCamera.equals("2")){//使用相机
            f=takePhonefile;
        }else{//没有修改图片
            ToastUtils.showToast(MyApplication.getContext(),"修改成功");
            finish();
            return;
        }
        final Map<String, String> params = new HashMap<String, String>();
        String path = CompressPicture.dealPicture(f.getAbsolutePath(), 800, 480, "jpg");
        File file =  new File(path);
        //测试上传图片
        final String url = Constants.FIND_URL_SEND_IMG+"id="+131; //换成自己的测试url地址
        LogUtil.e("url","url:"+url);
        MultipartRequest request=new MultipartRequest(url, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtil.e("onResponse","onResponse111："+error);
                if(pd!=null&&pd.isShowing()){
                    pd.dismiss();
                }
                ToastUtils.showToast(MyApplication.getContext(),"修改失败");
                finish();
            }
        }, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.e("onResponse","onResponse222："+response);
                if(pd!=null&&pd.isShowing()){
                    pd.dismiss();
                }
                ToastUtils.showToast(MyApplication.getContext(),"修改成功");
                finish();
            }
        },"upfile",file,params);
        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getRequestQueue().add(request);
    }
    /**
     * 调用照相机
     * */
    public void takePhone(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE2);

        }else {
            takePhoto();
        }

    }
    /**
     * 在相册中选取
     * */
    public void choosePhone(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE2);

        }else {
            choosePhoto();
        }
    }

    /**
     * 拍照
     */
    public void takePhoto(){
//        /**
//         * 最后一个参数是文件夹的名称，可以随便起
//         */
//        File file=new File(Environment.getExternalStorageDirectory(),"拍照");
//        if(!file.exists()){
//            file.mkdir();
//        }
//        /**
//         * 这里将时间作为不同照片的名称
//         */
//        output=new File(file,System.currentTimeMillis()+".jpg");
//
//        /**
//         * 如果该文件夹已经存在，则删除它，否则创建一个
//         */
//        try {
//            if (output.exists()) {
//                output.delete();
//            }
//            output.createNewFile();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        /**
//         * 隐式打开拍照的Activity，并且传入CROP_PHOTO常量作为拍照结束后回调的标志
//         */
//        //imageUri = Uri.fromFile(output);
//        imageUri = MyUtils.getImageContentUri(MyApplication.getContext(),output);
//        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//        startActivityForResult(intent, CROP_PHOTO);
        //适配7.0
        takePhonefile = new FileStorage().createIconFile();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(ChangeNcAndIocnActivity.this, "com.hzkc.parent.fileprovider", takePhonefile);//通过FileProvider创建一个content类型的Uri
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

    /**
     * 从相册选取图片
     */
    public void choosePhoto(){
        /**
         * 打开选择图片的界面
         */
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);

    }

    public void onActivityResult(int req, int res, Intent data) {
        switch (req) {
            /**
             * 拍照的请求标志
             */
            case CROP_PHOTO:
                if (res==RESULT_OK) {
                    try {
                        /**
                         * 该uri就是照片文件夹对应的uri
                         */
                        Log.e("-------------111","onActivityResult:imageUri"+imageUri);
                        Glide.with(this).load(imageUri).transform(new GlideCircleTransform(this)).diskCacheStrategy(DiskCacheStrategy.ALL).into(ivMineChildIcon);
                        isClickCamera="2";
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this,"程序崩溃",Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Log.i("tag", "失败");
                }

                break;
            /**
             * 从相册中选取图片的请求标志
             */

            case REQUEST_CODE_PICK_IMAGE:
                if (res == RESULT_OK) {
                    try {
                        /**
                         * 该uri是上一个Activity返回的
                         */
                        Uri uri = data.getData();
                        Log.e("------------222","onActivityResult:uri"+uri);
                        Glide.with(this).load(uri).transform(new GlideCircleTransform(this)).diskCacheStrategy(DiskCacheStrategy.ALL).into(ivMineChildIcon);
                        isClickCamera="1";
                        imagePath=uri;
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this,"程序崩溃",Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Log.i("liang", "失败");
                }

                break;

            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {

        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                takePhoto();
            } else
            {
                // Permission Denied
                Toast.makeText(ChangeNcAndIocnActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }


        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE2)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                choosePhoto();
            } else
            {
                // Permission Denied
                Toast.makeText(ChangeNcAndIocnActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    /**
     * 修改昵称
     * */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "UpdateNc")
    private void ChangeNc(UpdateNcEvent name) {
        if(name.State.equals("0")){
            ToastUtils.showToast(MyApplication.getContext(),"昵称修改成功");
            sp.edit().putString("nc",nc).commit();
            finish();
        }else {
            Toast.makeText(this, "昵称修改失败", Toast.LENGTH_SHORT).show();
        }
    }
}
