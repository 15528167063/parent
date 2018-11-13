package com.hzkc.parent.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.hzkc.parent.Bean.HeadRoot;
import com.hzkc.parent.R;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.greendao.GreenDaoManager;
import com.hzkc.parent.greendao.gen.AppDataBeanDao;
import com.hzkc.parent.greendao.gen.AppUseBeanDao;
import com.hzkc.parent.greendao.gen.ChildContrlFlagDao;
import com.hzkc.parent.greendao.gen.ChildsTableDao;
import com.hzkc.parent.greendao.gen.LoveTrailDataDao;
import com.hzkc.parent.greendao.gen.NetPlanDataBeanDao;
import com.hzkc.parent.greendao.gen.PhoneDataDao;
import com.hzkc.parent.mina.Constants;
import com.hzkc.parent.utils.ActivityCollector;
import com.hzkc.parent.utils.AppUtils;
import com.hzkc.parent.utils.CompressPicture;
import com.hzkc.parent.utils.DialogChangeWord;
import com.hzkc.parent.utils.DialogSelectImage;
import com.hzkc.parent.utils.FileStorage;
import com.hzkc.parent.utils.GlideCircleTransform;
import com.hzkc.parent.utils.LogUtil;
import com.hzkc.parent.utils.MultipartRequest;
import com.hzkc.parent.utils.MyUtils;
import com.hzkc.parent.utils.ToastUtils;
import com.jaeger.library.StatusBarUtil;

import org.simple.eventbus.EventBus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class AccountManagerActivity extends BaseActivity implements View.OnClickListener, DialogSelectImage.onDialogSelectImgListener, DialogChangeWord.onDialogChangeNcListener {

    private TextView tvTopTitle;
    private TextView bt_comfirm,tv_name;
    private ImageView ivFinish;
    private ImageView iv_heads;
    private String isClickCamera,parentuuid;
    private TextView tvAccount;
    private LinearLayout llChangePassword,ll_head;
    private LinearLayout llChangeNc;
    private DialogSelectImage selectImageDialog = null;
    private DialogChangeWord changeNcDialog = null;
    private static final int CROP_PHOTO = 2;
    private static final int REQUEST_CODE_PICK_IMAGE=3;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 6;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE2 = 7;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_manager);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.newcolor));
        EventBus.getDefault().register(this);
        initView();
    }


    private void initView() {
        isClickCamera="0";
        parentuuid= sp.getString("parentUUID", "");
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        bt_comfirm = (TextView) findViewById(R.id.bt_comfirm);
        ivFinish = (ImageView) findViewById(R.id.iv_finish_back);
        tvAccount = (TextView) findViewById(R.id.tv_account);
        tv_name = (TextView) findViewById(R.id.tv_name);
        iv_heads = (ImageView) findViewById(R.id.iv_heads);
        llChangePassword = (LinearLayout) findViewById(R.id.ll_change_password);
        ll_head = (LinearLayout) findViewById(R.id.ll_head);
        llChangeNc = (LinearLayout) findViewById(R.id.ll_change_name);
        tvTopTitle.setText("账号管理");
        String phoneNum = sp.getString("phoneNum", "");
        tvAccount.setText(phoneNum);
        ivFinish.setVisibility(View.VISIBLE);
        selectImageDialog = new DialogSelectImage(this);
        selectImageDialog.setImgListener(this);
        changeNcDialog = new DialogChangeWord(this);
        String headimage = sp.getString("headimage", null);
        String nc = sp.getString("nc", null);
        if(headimage!=null){
            Glide.with(this).load(headimage).transform(new GlideCircleTransform(this)).diskCacheStrategy(DiskCacheStrategy.ALL).error(R.drawable.mine_head_02).into(iv_heads);
        }
        if(nc!=null){
            changeNcDialog.setTv_my_nc(nc);
            tv_name.setText(nc);
        }
        changeNcDialog.setImgListener(this);
        ivFinish.setOnClickListener(this);
        llChangePassword.setOnClickListener(this);
        llChangeNc.setOnClickListener(this);
        ll_head.setOnClickListener(this);
        bt_comfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_comfirm:
//                affirmDialog.show();
                showDialog();
                break;
            case R.id.ll_head:
//                if (selectImageDialog != null) {
//                    selectImageDialog.show();
//                }
                showChoosePhoto();
                break;
            case R.id.iv_finish_back:
                finish();
                break;
            case R.id.ll_change_password:
                Intent intent1 = new Intent(AccountManagerActivity.this, ChangePasswordActivity.class);
                startActivity(intent1);
                break;
            case R.id.ll_change_name:
                if (changeNcDialog != null) {
                    changeNcDialog.show();
                }
                break;
            default:
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if(pd!=null&&pd.isShowing()){
            pd.dismiss();
            pd=null;
        }

    }

    /**
     * 退出登陆
     */
    private void logout() {
        tuichu();
    }
    private void tuichu() {
        sp.edit().putString("childName", "")
                .putString("ChildUUID", "")
                .putString("parentUUID", "")
                .putString("childSex", "")
                .putString("childztl", "")
                .putString("childImage", "")
                .putString("token", "")
                .putString("tokens", "")
                .putBoolean("devicetoken", false)
                .putString("loginImFlag", "")
                .putString("childVersion", "")
                .putString("loginImFlag","")
                .putString("headimage","")    //清空本地头像，昵称，版本，推送id值，加密token,
                .putString("nc", "").commit();
        ChildsTableDao childDao = GreenDaoManager.getInstance().getSession().getChildsTableDao();
        ChildContrlFlagDao childCopntrlDao = GreenDaoManager.getInstance().getSession().getChildContrlFlagDao();
        AppDataBeanDao appdao = GreenDaoManager.getInstance().getSession().getAppDataBeanDao();
        NetPlanDataBeanDao netPlanDao = GreenDaoManager.getInstance().getSession().getNetPlanDataBeanDao();
        LoveTrailDataDao lovePlanDao = GreenDaoManager.getInstance().getSession().getLoveTrailDataDao();
        AppUseBeanDao appUseBeanDao = GreenDaoManager.getInstance().getSession().getAppUseBeanDao();
        PhoneDataDao phoneDataDao = GreenDaoManager.getInstance().getSession().getPhoneDataDao();
        phoneDataDao.deleteAll();
        appUseBeanDao.deleteAll();
        lovePlanDao.deleteAll();
        netPlanDao.deleteAll();
        childDao.deleteAll();
        childCopntrlDao.deleteAll();
        appdao.deleteAll();

        Intent intent5 = new Intent(AccountManagerActivity.this, LoginActivity.class);
        startActivity(intent5);
        ActivityCollector.finishAll();
    }
    @Override
    public void ChangeNcClick() {
        try {
            ChangeNc();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void ChangeNc() throws UnsupportedEncodingException {

        final String nc = changeNcDialog.getEt_nc().toString().trim();
        if(TextUtils.isEmpty(nc)){
            ToastUtils.showToast(AccountManagerActivity.this,"输入的昵称为空");
            return;
        }
        changeNcDialog.setTv_my_nc("");
        String url=Constants.PHP_URL+"v"+ AppUtils.getVerName()+"/saveNickname";
        Log.e("昵称修改", "url:" + url);
        StringRequest postsr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ToastUtils.showToast(AccountManagerActivity.this,"昵称修改成功");
                sp.edit().putString("nc",nc).commit();
                tv_name.setText(nc);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtil.e("昵称修改", "error:" + volleyError);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //创建一个集合，放的是keyvalue的key是参数名与value是参数值
                Map<String, String> map = new HashMap<String, String>();
                map.put("user_id", parentuuid);
                map.put("nickname", nc);
                return map;
            }
        };
        postsr.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getRequestQueue().add(postsr);
    }
    @Override
    public void imgClick(int index) {
        if (index == 1) {
            takePhone();
        } else if (index == 2){
            choosePhone();
        }else if (index == 3){
            selectImageDialog.dismiss();
        }
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
    private Uri imageUri;
    private Uri imagePath;
    private File takePhonefile;
    public void takePhoto(){
        //适配7.0
        takePhonefile = new FileStorage().createIconFile();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(AccountManagerActivity.this, "com.hzkc.parent.fileprovider", takePhonefile);//通过FileProvider创建一个content类型的Uri
        } else {
            imageUri = Uri.fromFile(takePhonefile);
        }
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
        }
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照$ git config --global user.name "qiuqilin123"
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
            case CROP_PHOTO:
                if (res==RESULT_OK) {
                    try {
                        isClickCamera="2";
                        Glide.with(this).load(imageUri).transform(new GlideCircleTransform(this)).diskCacheStrategy(DiskCacheStrategy.ALL).error(R.drawable.mine_head_02).into(iv_heads);
                        changeIcon();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this,"程序崩溃",Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Log.e("tag", "失败");
                }

                break;
            /**
             * 从相册中选取图片的请求标志
             */
            case REQUEST_CODE_PICK_IMAGE:
                if (res == RESULT_OK) {
                    try {
                        Uri uri = data.getData();
                        LogUtil.e("-------------222","onActivityResult:uri"+uri);
                        Glide.with(this).load(uri).transform(new GlideCircleTransform(this)).diskCacheStrategy(DiskCacheStrategy.ALL).error(R.drawable.mine_head_02).into(iv_heads);
                        isClickCamera="1";
                        imagePath=uri;
                        changeIcon();
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
                Toast.makeText(AccountManagerActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE2)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                choosePhoto();
            } else
            {
                Toast.makeText(AccountManagerActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 修改头像
     */
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
            f = MyUtils.getFileByUri(AccountManagerActivity.this,imagePath);
        }else if(isClickCamera.equals("2")){//使用相机
            f=takePhonefile;
        }else{//没有修改图片
            return;
        }
        //测试上传图片
        final String url =Constants.PHP_URL+"v"+ AppUtils.getVerName()+"/saveHeadImg";

        final Map<String, String> params = new HashMap<String, String>();
        params.put("save_src", "parent");
        params.put("id", parentuuid);
        String path = CompressPicture.dealPicture(f.getAbsolutePath(), 800, 480, "jpg");
        File file =  new File(path);
        Log.e("url","url:"+url);
        Log.e("upload->params: " ,params.toString());
        MultipartRequest request=new MultipartRequest(url, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(pd!=null&&pd.isShowing()){
                    pd.dismiss();
                }
                ToastUtils.showToast(MyApplication.getContext(),"修改失败");
            }
        }, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("onResponse","onResponse222："+response);
                if(pd!=null&&pd.isShowing()){
                    pd.dismiss();
                }
                HeadRoot headRoot = new Gson().fromJson(response, HeadRoot.class);
                String headImg = headRoot.data.headImg;
                sp.edit().putString("headimage",headImg).commit();   //保存头像本地
                sp.edit().putBoolean("headrefresh",true).commit();//修改头像出去刷新我的界面
                ToastUtils.showToast(MyApplication.getContext(),"修改成功");

            }
        },"uploadfile",file,params);
        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getRequestQueue().add(request);
    }
    /**
     * 退出
     */
    private TextView tvAddChlid;
    private TextView tvFinish;
    private ImageView ivAddFinish;
    private AlertDialog showStartTime;
    public void showDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(AccountManagerActivity.this,R.style.MyAlertDialog);
        View dialogView =  View.inflate(AccountManagerActivity.this, R.layout.login_out, null);
        tvAddChlid = (TextView) dialogView.findViewById(R.id.tv_add_chlid);
        tvFinish = (TextView) dialogView.findViewById(R.id.tv_finish);
        ivAddFinish = (ImageView) dialogView.findViewById(R.id.iv_add_fish);
        tvAddChlid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStartTime.dismiss();
                logout();
            }
        });
        tvFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStartTime.dismiss();
            }
        });
        ivAddFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStartTime.dismiss();
            }
        });
        builder.setView(dialogView);
        showStartTime = builder.show();
    }

    public void showChoosePhoto(){
        android.support.v7.app.AlertDialog.Builder builder=new android.support.v7.app.AlertDialog.Builder(this,R.style.MyAlertDialog);
        View dialogView =  View.inflate(this, R.layout.dialog_select_img, null);
        TextView  camera = (TextView) dialogView.findViewById(R.id.dialog_select_img_camera);
        TextView  photograph = (TextView) dialogView.findViewById(R.id.dialog_select_img_photograph);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStartTime.dismiss();
                takePhone();
            }
        });
        photograph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStartTime.dismiss();
                choosePhone();
            }
        });
        builder.setView(dialogView);
        showStartTime =  builder.show();
    }
}
