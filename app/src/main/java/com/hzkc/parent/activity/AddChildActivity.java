package com.hzkc.parent.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hzkc.parent.R;
import com.hzkc.parent.utils.LogUtil;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import org.simple.eventbus.EventBus;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 添加孩子的界面
 */
public class AddChildActivity extends BaseActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {
    private TextView tvTopTitle;
    private ImageView ivFinish;
    private TextView tvContextChild;
    private TextView tvCreatZxing;
    private TextView tvLoadZxing;
    private static final String TAG = "AddChildActivity";

    /**
     * 扫描跳转Activity RequestCode
     */
    public static final int REQUEST_CODE = 111;
    /**
     * 请求CAMERA权限码
     */
    public static final int REQUEST_CAMERA_PERM = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_child);
        //初始化二维码扫描
        ZXingLibrary.initDisplayOpinion(this);
        initView();
    }

    private void initView() {
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        ivFinish = (ImageView) findViewById(R.id.iv_finish);
        tvContextChild = (TextView) findViewById(R.id.tv_context_child);
        tvCreatZxing = (TextView) findViewById(R.id.tv_creat_zxing);
        tvLoadZxing = (TextView) findViewById(R.id.tv_load_zxing);
        tvTopTitle.setText("添加孩子");
        ivFinish.setVisibility(View.VISIBLE);
        ivFinish.setOnClickListener(this);
        tvContextChild.setOnClickListener(this);
        tvCreatZxing.setOnClickListener(this);
        tvLoadZxing.setOnClickListener(this);
//        String childUUID = sp.getString("ChildUUID", "");
//        if(TextUtils.isEmpty(childUUID)){
//            sp.edit().putString("ChildUUID", "123").commit();
//        }else{
//            sp.edit().putString("ChildUUID", "").commit();
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_context_child://关联孩子设备,扫描二维码
                cameraTask();
                break;
            case R.id.tv_creat_zxing://生成父母端二维码
                Intent intent1 = new Intent(AddChildActivity.this, ParentZxingActivity.class);
                //使用uuid生成一个特定的字符串
                String uuid = sp.getString("parentUUID", "");
                LogUtil.e(TAG, "parentUUID"+uuid);
                intent1.putExtra("parentZxing",uuid);
                startActivity(intent1);
                break;
            case R.id.iv_finish://返回界面
                finish();
                break;
            case R.id.tv_load_zxing://生成下载孩子端的url
                Intent intent2 = new Intent(AddChildActivity.this, ChildLoadActivity.class);
                intent2.putExtra("childZxing","大师傅好傻的");
                startActivity(intent2);
                break;
            default:
                break;
        }
    }

    /**
     * 处理二维码扫描结果
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    Toast.makeText(this, "解析结果:" + result, Toast.LENGTH_LONG).show();
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(this, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        } else if (requestCode == REQUEST_CAMERA_PERM) {
            Toast.makeText(this, "从设置页面返回", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    /**
     * EsayPermissions接管权限处理逻辑1
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /**
     * 二维码实现逻辑
     */
    @AfterPermissionGranted(REQUEST_CAMERA_PERM)
    public void cameraTask() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
            // Have permission, do the thing!
            Intent intent = new Intent(getApplication(), CaptureActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, "需要请求camera权限",
                    REQUEST_CAMERA_PERM, Manifest.permission.CAMERA);
        }
    }

    /**
     * EsayPermissions接管权限处理逻辑2
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Toast.makeText(this, "执行onPermissionsGranted()", Toast.LENGTH_SHORT).show();
    }

    /**
     * EsayPermissions接管权限处理逻辑3
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
//        Toast.makeText(this, "执行onPermissionsDenied()...", Toast.LENGTH_SHORT).show();
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this, "当前App需要申请camera权限,需要打开设置页面么?")
                    .setTitle("权限申请")
                    .setPositiveButton("确认")
                    .setNegativeButton("取消", null /* click listener */)
                    .setRequestCode(REQUEST_CAMERA_PERM)
                    .build()
                    .show();
        }
    }
    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

}
