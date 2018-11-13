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
import android.os.Handler;
import android.os.Message;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.hzkc.parent.Bean.HeadRoot;
import com.hzkc.parent.R;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.event.ChangeChildDataEvent;
import com.hzkc.parent.event.DeleteChildEvent;
import com.hzkc.parent.event.InitEvent;
import com.hzkc.parent.event.OrderChildDataEvent;
import com.hzkc.parent.event.OrderDeleteChildEvent;
import com.hzkc.parent.greendao.GreenDaoManager;
import com.hzkc.parent.greendao.entity.AppDataBean;
import com.hzkc.parent.greendao.entity.AppUseBean;
import com.hzkc.parent.greendao.entity.ChildContrlFlag;
import com.hzkc.parent.greendao.entity.ChildsTable;
import com.hzkc.parent.greendao.entity.LoveTrailData;
import com.hzkc.parent.greendao.entity.NetPlanDataBean;
import com.hzkc.parent.greendao.gen.AppDataBeanDao;
import com.hzkc.parent.greendao.gen.AppUseBeanDao;
import com.hzkc.parent.greendao.gen.ChildContrlFlagDao;
import com.hzkc.parent.greendao.gen.ChildsTableDao;
import com.hzkc.parent.greendao.gen.LoveTrailDataDao;
import com.hzkc.parent.greendao.gen.NetPlanDataBeanDao;
import com.hzkc.parent.jsondata.CmdCommon;
import com.hzkc.parent.mina.Constants;
import com.hzkc.parent.utils.AppUtils;
import com.hzkc.parent.utils.CompressPicture;
import com.hzkc.parent.utils.DialogSelectImage;
import com.hzkc.parent.utils.FileStorage;
import com.hzkc.parent.utils.GlideCircleTransform;
import com.hzkc.parent.utils.LogUtil;
import com.hzkc.parent.utils.MultipartRequest;
import com.hzkc.parent.utils.MyUtils;
import com.hzkc.parent.utils.ToastUtils;
import com.hzkc.parent.view.AffirmDialog;
import com.jaeger.library.StatusBarUtil;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChildNewDetailActivity extends BaseActivity implements View.OnClickListener, DialogSelectImage.onDialogSelectImgListener {
    private TextView tvTopTitle;
    private TextView tvSave;
    private ImageView iv_state_on, iv_state_off;
    private ImageView ivFinish;
    private EditText etChildName;
    private RadioGroup rgChildSex;
    private RadioButton rbSexNan;
    private RadioButton rbSexNv;
    private ImageView ivChildIcon;
    private TextView tvChildName;
    private TextView tvChildClass;
    private RelativeLayout rlChildUnbindcode;
    private RelativeLayout rlChildJzsw;
    private RelativeLayout rlChildSlbh;
    private TextView btChildDelete;
    private boolean deleteChildFlag;
    private String childuuid;
    private String childname;
    private String childnianji, childbanji;
    private String childsex;
    private String parentUUID;
    private ChildsTableDao childDao;
    private ChildContrlFlagDao childCopntrlDao;
    private String childName;
    private ProgressDialog pd;
    private String state = "2";
    private ImageView iv_icon, iv_child_icon;
    private String childFrom;
    private Runnable runnable;
    private String phonePsw;
    public String viplist;
    private AffirmDialog affirmDialog;
    private String statetype;//是不是可以使用状态栏
    private DialogSelectImage selectImageDialog = null;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 101:
                    if (!deleteChildFlag) {
                        ToastUtils.showToast(MyApplication.getContext(), "当前连接不正常，请稍后再试");
                    }
                    if (pd != null && pd.isShowing()) {
                        pd.dismiss();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_detail_news);
        StatusBarUtil.setColorNoTranslucent(this, getResources().getColor(R.color.newcolor));
        EventBus.getDefault().register(this);
        initView();
        initDatas();
        initDao();

        runnable = new Runnable() {
            public void run() {
                Message msg = Message.obtain();
                msg.what = 101;
                handler.sendMessage(msg);
            }
        };
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    public String type;

    private void initView() {
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvSave = (TextView) findViewById(R.id.tv_save1);
        ivFinish = (ImageView) findViewById(R.id.iv_finish_back);
        iv_state_off = (ImageView) findViewById(R.id.iv_state_off);
        iv_state_on = (ImageView) findViewById(R.id.iv_state_on);
        iv_icon = (ImageView) findViewById(R.id.iv_icon);
        iv_child_icon = (ImageView) findViewById(R.id.iv_child_icon);
        iv_state_on = (ImageView) findViewById(R.id.iv_state_on);
        etChildName = (EditText) findViewById(R.id.et_child_name);
        rgChildSex = (RadioGroup) findViewById(R.id.rg_child_sex);
        rbSexNan = (RadioButton) findViewById(R.id.rb_01);
        rbSexNv = (RadioButton) findViewById(R.id.rb_02);
        tvChildClass = (TextView) findViewById(R.id.tv_child_class);
        rlChildUnbindcode = (RelativeLayout) findViewById(R.id.rl_child_unbindcode);
        rlChildJzsw = (RelativeLayout) findViewById(R.id.rl_child_jzsw);
        rlChildSlbh = (RelativeLayout) findViewById(R.id.rl_child_slbh);
        btChildDelete = (TextView) findViewById(R.id.bt_child_delete);
        ivChildIcon = (ImageView) findViewById(R.id.iv_child_icon);
        tvChildName = (TextView) findViewById(R.id.tv_child_name);
        selectImageDialog = new DialogSelectImage(this);
        selectImageDialog.setImgListener(this);
        affirmDialog = new AffirmDialog(this);
        affirmDialog.setTitleText("您还不是VIP会员，不能使用VIP功能，是否立即充值?");
        affirmDialog.setAffirmClickListener(this);
        type = getIntent().getStringExtra("type");
        if (type != null) {
            childuuid = getIntent().getStringExtra("ChildUUID");
        } else {
            childuuid = sp.getString("ChildUUID", "");
        }
        tvTopTitle.setText("孩子资料");
        ivFinish.setVisibility(View.VISIBLE);
        tvSave.setVisibility(View.VISIBLE);
        rlChildUnbindcode.setVisibility(View.GONE);
        tvSave.setOnClickListener(this);
        ivFinish.setOnClickListener(this);
        iv_state_on.setOnClickListener(this);
        iv_state_off.setOnClickListener(this);
        rlChildJzsw.setOnClickListener(this);
        rlChildSlbh.setOnClickListener(this);
        btChildDelete.setOnClickListener(this);
        iv_icon.setOnClickListener(this);
        iv_child_icon.setOnClickListener(this);
        rlChildUnbindcode.setOnClickListener(this);
        deleteChildFlag = false;
    }

    private void initDatas() {
        viplist = sp.getString("viplist", "");
        childFrom = getIntent().getStringExtra("childFrom");    //来源
        parentUUID = sp.getString("parentUUID", "");
        phonePsw = sp.getString("phonePsw", "");
    }

    public String childimage, childztl;

    /**
     * 初始化数据库
     */


    private void initDao() {
        childDao = GreenDaoManager.getInstance().getSession().getChildsTableDao();
        childCopntrlDao = GreenDaoManager.getInstance().getSession().getChildContrlFlagDao();
        ChildsTable findChild = childDao.queryBuilder().where(ChildsTableDao.Properties.Childuuid.eq(childuuid)).build().unique();
        ChildContrlFlag findChildFlag = childCopntrlDao.queryBuilder().where(ChildContrlFlagDao.Properties.Childuuid.eq(childuuid)).build().unique();
        if (findChild != null) {
            childbanji = findChild.getSchool();
            childname = findChild.getName();
            childnianji = findChild.getNianji();
            childsex = findChild.getSex();
            childimage = findChild.getImageurl();
            childztl = findChild.getChildztl();
        } else {
            EventBus.getDefault().post(new InitEvent(parentUUID), "init");
        }
        if (childName == null && childsex == null && childnianji == null && childnianji == null) {
            Toast.makeText(this, "请前往选择孩子界面选中孩子", Toast.LENGTH_SHORT).show();
            String childNames = sp.getString("childNames", "");
            String ChildUUID = sp.getString("ChildUUIDs", "");
            String childSexs = sp.getString("childSexs", "");
            sp.edit().putString("ChildUUID", ChildUUID).putString("childName", childNames).putString("childSex", childSexs).commit();
            return;
        }
        etChildName.setText(childname);
        tvChildName.setText(childname);
        lastName=childname;
        if (childsex.equals(CmdCommon.FLAG_BOY)) {
            rbSexNan.setChecked(true);
        } else if (childsex.equals(CmdCommon.FLAG_GIRL)) {
            rbSexNv.setChecked(true);
        }
        if (childimage != null) {        //头像初始化
            Glide.with(this).load(childimage).transform(new GlideCircleTransform(this)).diskCacheStrategy(DiskCacheStrategy.ALL).error(R.drawable.mine_head__01).into(iv_icon);
        }
        if (TextUtils.isEmpty(childztl) ||  childztl.equals("0")) {   //是不是使用状态蓝初始化
            iv_state_off.setVisibility(View.VISIBLE);
            iv_state_on.setVisibility(View.GONE);
        } else {
            iv_state_off.setVisibility(View.GONE);
            iv_state_on.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.affirm_cancel:
                if (affirmDialog != null) {
                    affirmDialog.hide();
                }
                break;
            case R.id.affirm_confirm:
                if (affirmDialog != null) {
                    affirmDialog.hide();
                }
                Intent intents = new Intent(this, MyMemeberActivity.class);
                startActivity(intents);
                break;
            case R.id.iv_finish_back:
                if (!state.equals("2")) {
                    showDialog();
                    return;
                }
                if (!lastName.equals(etChildName.getText().toString())) {
                    showDialog();
                    return;
                }
                finish();
                break;
            case R.id.tv_save1://保存
                saveChild();
                break;
            case R.id.iv_state_on://关闭状蓝
                state="0";
                iv_state_off.setVisibility(View.VISIBLE);
                iv_state_on.setVisibility(View.GONE);
                break;
            case R.id.iv_state_off://开启状蓝
                state="1";
                iv_state_off.setVisibility(View.GONE);
                iv_state_on.setVisibility(View.VISIBLE);
                break;
            case R.id.bt_child_delete://删除孩子
                showDialogIsDeleteChild();
                break;
            case R.id.iv_child_icon:
//                if (selectImageDialog != null) {
//                    selectImageDialog.show();
//                }
                showChoosePhoto();
                break;
            case R.id.iv_icon:
                if (selectImageDialog != null) {
                    selectImageDialog.show();
                }
                break;
            default:
                break;
        }
    }


    /**
     * 删除孩子弹出对话框
     */
    public String lastName = "" ;
    public boolean ischeckable = false;
    private void showDialogIsDeleteChild() {
        AlertDialog.Builder builer = new AlertDialog.Builder(this);
        builer.setCancelable(false);
        builer.setTitle("优成长");
        builer.setIcon(R.drawable.icon);
        View dialogView = View.inflate(this, R.layout.dialog_child_delete, null);
        final EditText EtPsw = (EditText) dialogView.findViewById(R.id.et_psw);
        builer.setView(dialogView);
        builer.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                    dialog.dismiss();
                }
                return false;
            }
        });
        builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "确定");
                String psw = EtPsw.getText().toString().trim();
                if (TextUtils.isEmpty(psw)) {
                    ToastUtils.showToast(MyApplication.getContext(), "输入密码为空");
                } else if (psw.equals(phonePsw)) {//执行删除孩子操作
                    dialog.dismiss();
                    deleteChild();
                } else {
                    ToastUtils.showToast(MyApplication.getContext(), "输入密码错误");
                    dialog.dismiss();
                }
            }
        });
        builer.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "取消");
                dialog.dismiss();
            }
        });
        builer.show();
    }


    /**
     * 删除孩子数据
     */
    private void deleteChild() {
        state = "-1";
        lastName =etChildName.getText().toString();
        deleteChildFlag = false;
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
        pd = new ProgressDialog(ChildNewDetailActivity.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("正在删除孩子，请稍候");
        setResult(-1);
        pd.show();
        EventBus.getDefault().post(new DeleteChildEvent(childuuid, parentUUID), "deleteChild");
        handler.postDelayed(runnable, 10000);
    }

    /**
     * 保存孩子数据
     */
    private void saveChild() {

        lastName =etChildName.getText().toString();
        childName = etChildName.getText().toString().trim();
        if (TextUtils.isEmpty(childName)) {
            ToastUtils.showToast(MyApplication.getContext(), "输入孩子姓名");
            return;
        }
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
        if(iv_state_on.getVisibility()==View.VISIBLE){
            state="1";
        }else {
            state="0";
        }
        pd = new ProgressDialog(ChildNewDetailActivity.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("正在修改孩子，请稍候");
        pd.show();
        if (rbSexNan.isChecked()) {
            childsex = "1";
        } else {
            childsex = "0";
        }
        EventBus.getDefault().post(new ChangeChildDataEvent(childuuid, childName, childsex, "", "", state), "changeChildData");
        handler.postDelayed(runnable, 10000);
    }


    @Override
    protected void onDestroy() {
        deleteChildFlag = true;
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * 修改孩子信息返回结果
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "orederChildData")
    public void RequestChangeChildEvent(OrderChildDataEvent data) {
        deleteChildFlag = true;
        handler.removeCallbacks(runnable);
        String isChangePsw = data.isRegistered;
        LogUtil.e(TAG, "RequestChangeChildEvent: 修改孩子信息返回结果");
        if (isChangePsw.equals("0")) {   //修改成功

            ChildsTableDao childDao = GreenDaoManager.getInstance().getSession().getChildsTableDao();
            ChildsTable findChild = childDao.queryBuilder().where(ChildsTableDao.Properties.Childuuid.eq(childuuid)).build().unique();
            if (findChild != null) {
                findChild.setSex(childsex);
                findChild.setNianji("");
                findChild.setName(childName);
                findChild.setSchool("");
                findChild.setChildztl(state);
                childDao.update(findChild);
                sp.edit().putString("childName", childName).putString("childSex", childsex).putString("childztl", childztl).commit();
            }
            Toast.makeText(this, "孩子信息修改成功", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "孩子信息修改失败,请稍后修改", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * 删除孩子信息返回结果
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "orederdeleteChild")
    public void RequestDeleteChildEvent(OrderDeleteChildEvent data) {
        deleteChildFlag = true;
        handler.removeCallbacks(runnable);
        if (data.isRegistered.equals("0")) {
            NetPlanDataBeanDao netPlanDataBeanDao = GreenDaoManager.getInstance().getSession().getNetPlanDataBeanDao();
            AppDataBeanDao appDao = GreenDaoManager.getInstance().getSession().getAppDataBeanDao();
            LoveTrailDataDao lovePlanDao = GreenDaoManager.getInstance().getSession().getLoveTrailDataDao();
            AppUseBeanDao appuseDao = GreenDaoManager.getInstance().getSession().getAppUseBeanDao();
            //删除本地运动轨迹
            List<LoveTrailData> list1 = lovePlanDao.queryBuilder().where(LoveTrailDataDao.Properties.Childuuid.eq(childuuid)).build().list();
            if (list1 != null && list1.size() > 0) {
                lovePlanDao.deleteAll();
            }
            //删除本地应用追踪
            List<AppUseBean> list2 = appuseDao.queryBuilder().where(AppUseBeanDao.Properties.Childuuid.eq(childuuid)).build().list();
            if (list2 != null && list2.size() > 0) {
                appuseDao.deleteAll();
            }

            List<NetPlanDataBean> list = netPlanDataBeanDao.queryBuilder()
                    .where(NetPlanDataBeanDao.Properties.Childuuid.eq(childuuid))
                    .build().list();
            List<AppDataBean> appList = appDao.queryBuilder()
                    .where(AppDataBeanDao.Properties.Childuuid.eq(childuuid))
                    .build().list();
            ChildsTable findChild = childDao.queryBuilder().where(ChildsTableDao.Properties.Childuuid.eq(childuuid)).build().unique();
            ChildContrlFlag findChildFlag = childCopntrlDao.queryBuilder().where(ChildContrlFlagDao.Properties.Childuuid.eq(childuuid)).build().unique();
            sp.edit().putBoolean("yjgk" + childuuid, false).commit();
            if (findChild != null) {
                childDao.delete(findChild);
            }
            if (findChildFlag != null) {
                childCopntrlDao.delete(findChildFlag);
            }
            if (list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    netPlanDataBeanDao.delete(list.get(i));
                }
            }
            if (appList.size() > 0) {
                for (int i = 0; i < appList.size(); i++) {
                    appDao.delete(appList.get(i));
                }
            }
            // todo:
            if (childuuid.equals(childuuid)) {
                List<ChildsTable> childlist = childDao.queryBuilder().build().list();
                if (childlist.size() > 0) {
                    sp.edit().putString("ChildUUID", childlist.get(0).getChilduuid())
                            .putString("childName", childlist.get(0).getName())
                            .putString("childSex", childlist.get(0).getSex())
                            .putString("childztl", childlist.get(0).getChildztl())
                            .putString("childImage", childlist.get(0).getImageurl())
                            .commit();
                    LogUtil.i(TAG, "deleteChild: " + childlist.get(0).getChilduuid() + childlist.get(0).getName());
                } else {
                    sp.edit()
                            .putString("ChildUUID", "")
                            .putString("childName", "")
                            .putString("childSex", "")
                            .putString("childztl", "")
                            .putString("childImage", "")
                            .commit();
                }
            }
            sp.edit().putString(childuuid + "first", "").commit();
            //删除孩子的childVersion
            String childVersion = sp.getString("childVersion", "");
            String[] split = childVersion.split(";");
            for (int i = 0; i < split.length; i++) {
                if (split[i].contains(childuuid)) {
                    split[i] = "";
                }
            }
            String childInfos = "";
            for (int i = 0; i < split.length; i++) {
                childInfos += split[i];
            }
            sp.edit().putString("childVersion", childInfos).commit();
            Toast.makeText(this, "删除孩子成功", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            pd.dismiss();
            finish();
        } else {
            Toast.makeText(this, "删除孩子失败", Toast.LENGTH_SHORT).show();
            pd.dismiss();
            finish();
        }
    }

    @Override
    public void imgClick(int index) {
        if (index == 1) {
            takePhone();
        } else if (index == 2) {
            choosePhone();
        } else if (index == 3) {
            selectImageDialog.dismiss();
        }
    }

    /**
     * 调用照相机
     */
    public void takePhone() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE2);

        } else {
            takePhoto();
        }
    }

    /**
     * 在相册中选取
     */
    private static final int CROP_PHOTO = 2;
    private static final int REQUEST_CODE_PICK_IMAGE = 3;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 6;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE2 = 7;

    public void choosePhone() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE2);

        } else {
            choosePhoto();
        }
    }

    /**
     * 拍照
     */
    private Uri imageUri;
    private Uri imagePath;
    private File takePhonefile;
    private String isClickCamera;

    public void takePhoto() {
        //适配7.0
        takePhonefile = new FileStorage().createIconFile();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(ChildNewDetailActivity.this, "com.hzkc.parent.fileprovider", takePhonefile);//通过FileProvider创建一个content类型的Uri
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
    public void choosePhoto() {
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
                if (res == RESULT_OK) {
                    try {
                        isClickCamera = "2";
                        Glide.with(this).load(imageUri).transform(new GlideCircleTransform(this)).diskCacheStrategy(DiskCacheStrategy.ALL).error(R.drawable.mine_head_02).into(iv_icon);
                        changeIcon();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "程序崩溃", Toast.LENGTH_SHORT).show();
                    }
                } else {
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
                        Glide.with(this).load(uri).transform(new GlideCircleTransform(this)).diskCacheStrategy(DiskCacheStrategy.ALL).error(R.drawable.mine_head_02).into(iv_icon);
                        isClickCamera = "1";
                        imagePath = uri;
                        changeIcon();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "程序崩溃", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.i("liang", "失败");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto();
            } else {
                Toast.makeText(ChildNewDetailActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                choosePhoto();
            } else {
                Toast.makeText(ChildNewDetailActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
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
        LogUtil.e("changeIcon", "imagePath:" + imagePath);
        if (isClickCamera.equals("1")) {//选择照片
            f = MyUtils.getFileByUri(ChildNewDetailActivity.this, imagePath);
        } else if (isClickCamera.equals("2")) {//使用相机
            f = takePhonefile;
        } else {//没有修改图片
            return;
        }
        //测试上传图片
        final String url = Constants.PHP_URL + "v" + AppUtils.getVerName() + "/saveHeadImg";

        final Map<String, String> params = new HashMap<String, String>();
        params.put("save_src", "child");
        params.put("id", childuuid);
        String path = CompressPicture.dealPicture(f.getAbsolutePath(), 800, 480, "jpg");
        File file = new File(path);
        Log.e("url", "url:" + url);
        Log.e("upload->params: ", params.toString());
        MultipartRequest request = new MultipartRequest(url, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
                ToastUtils.showToast(MyApplication.getContext(), "修改失败");
            }
        }, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("onResponse", "onResponse222：" + response);
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
                HeadRoot headRoot =null;
                try {
                    headRoot = new Gson().fromJson(response, HeadRoot.class);
                }catch (Exception e){
                    ToastUtils.showToast(MyApplication.getContext(), "头像修改失败");
                }

                String headImg = headRoot.data.headImg;
                sp.edit().putString("childImage", headImg).commit();   //保存头像本地
                ChildsTable findChild = childDao.queryBuilder().where(ChildsTableDao.Properties.Childuuid.eq(childuuid)).build().unique();
                if (findChild != null) {
                    findChild.setImageurl(headImg);
                    childDao.update(findChild);
                }
            }
        }, "uploadfile", file, params);
        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getRequestQueue().add(request);
    }

    /**
     * 退出
     */
    private TextView tvAddChlid;
    private TextView tvFinish, tv_content;
    private ImageView ivAddFinish;
    private android.support.v7.app.AlertDialog showStartTime;

    public void showDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ChildNewDetailActivity.this, R.style.MyAlertDialog);
        View dialogView = View.inflate(ChildNewDetailActivity.this, R.layout.login_out, null);
        tv_content = (TextView) dialogView.findViewById(R.id.tv_content);
        tvAddChlid = (TextView) dialogView.findViewById(R.id.tv_add_chlid);
        tvFinish = (TextView) dialogView.findViewById(R.id.tv_finish);
        ivAddFinish = (ImageView) dialogView.findViewById(R.id.iv_add_fish);
        tv_content.setText("您还未保存数据，是否退出？");
        tvAddChlid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStartTime.dismiss();
                saveChild();
            }
        });
        tvFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStartTime.dismiss();
                finish();
            }
        });
        ivAddFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStartTime.dismiss();
                finish();
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