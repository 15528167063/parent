package com.hzkc.parent.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hzkc.parent.R;
import com.hzkc.parent.adapter.PhoneQinQingControlAdapter;
import com.hzkc.parent.event.RequestQingQinEvent;
import com.hzkc.parent.event.WhiteAppBackEvent;
import com.hzkc.parent.event.WhitePhoneListEvent;
import com.hzkc.parent.greendao.GreenDaoManager;
import com.hzkc.parent.greendao.entity.PhoneData;
import com.hzkc.parent.greendao.gen.PhoneDataDao;
import com.hzkc.parent.utils.DialogAddPhone;
import com.hzkc.parent.utils.DialogChangeWord;
import com.hzkc.parent.utils.LogUtil;
import com.hzkc.parent.view.AffirmDialog;
import com.jaeger.library.StatusBarUtil;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 管控设置
 */
public class PhoneQinQingActivity extends BaseActivity implements View.OnClickListener, DialogChangeWord.onDialogChangeNcListener {
    private TextView tvTopTitle,tv_add;
    private ImageView tvFinishNetControl,iv_add_plan;
    private EditText etNetName;
    private String childuuid,parentUUID;
    private ListView listView;
    private LinearLayout flKong;
    private AffirmDialog affirmDialog;
    private DialogAddPhone addPhoneDialog = null;
    private boolean regFlag;
    private PhoneDataDao dao;
    private Handler hd = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 106:
                    dissloading();
                    if (!regFlag) { //网络不成功从本地
                        initAppList();
                    }
                    break;
                case 107://处理获取的applist
                    dissloading();
                    initAppList();
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qq_phone_new);
        StatusBarUtil.setColorNoTranslucent(this, getResources().getColor(R.color.newcolor));
        EventBus.getDefault().register(this);
        initView();
        initData();
    }
    private Runnable runnable;
    private void initView() {
        regFlag=false;
        addPhoneDialog = new DialogAddPhone(this);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tv_add = (TextView) findViewById(R.id.tv_add);
        iv_add_plan = (ImageView) findViewById(R.id.iv_txl);
        tvFinishNetControl = (ImageView) findViewById(R.id.iv_finish_back);
        etNetName = (EditText) findViewById(R.id.et_net_name);
        flKong = (LinearLayout) findViewById(R.id.fl_kongbai);
        listView = (ListView) findViewById(R.id.lv_net_list);
        childuuid = getIntent().getStringExtra("ChildUUID");
        parentUUID = sp.getString("parentUUID", "");
        tvTopTitle.setText("亲情号码");
        iv_add_plan.setVisibility(View.VISIBLE);
        iv_add_plan.setOnClickListener(this);
        tvFinishNetControl.setVisibility(View.VISIBLE);
        runnable = new Runnable() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                msg.what = 106;
                hd.sendMessage(msg);
            }
        };
        affirmDialog = new AffirmDialog(this);
        affirmDialog.setTitleText("您是否确定移除此号码？");
        affirmDialog.setAffirmClickListener(this);
        tvFinishNetControl.setOnClickListener(this);
        tv_add.setOnClickListener(this);
        addPhoneDialog.setImgListener(this);
    }

    private List<PhoneData> list;
    private PhoneQinQingControlAdapter adapter;
    private void initData() {
        showLoading();
        list = new ArrayList<>();
        dao = GreenDaoManager.getInstance().getSession().getPhoneDataDao();
        EventBus.getDefault().post(new RequestQingQinEvent(childuuid, parentUUID,null), "requestWhitePhone");
        hd.postDelayed(runnable, 5000);
    }

    private int positions = -1;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_finish_back:
                finish();
                break;
            case R.id.tv_add:
                if(list.size()>=5){
                    Toast.makeText(this, "亲情号码不能超过5个", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (addPhoneDialog != null) {
                    addPhoneDialog.show();
                }
                break;
            case R.id.iv_txl:
                if(list.size()>=5){
                    Toast.makeText(this, "亲情号码不能超过5个", Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), 0);
                break;
            case R.id.affirm_cancel:
                if (affirmDialog != null) {
                    affirmDialog.dismiss();
                }
                break;
            case R.id.affirm_confirm:
                if (affirmDialog != null) {
                    affirmDialog.dismiss();
                }
                list.remove(positions);
                EventBus.getDefault().post(new RequestQingQinEvent(childuuid, parentUUID,list), "requestWhitePhone");
                adapter.notifyDataSetChanged();

            default:
                break;
        }
    }


    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    /**
     * 请求返回
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "whitePhoneData")
    private void OrderChildAppList(WhitePhoneListEvent whiteAppList) {
        regFlag=true;
        hd.removeCallbacks(runnable);
        Message msg = Message.obtain();
        Log.e("WhitePhoneFragment-107:","----");
        msg.what = 107;
        hd.sendMessage(msg);
    }

    /**
     * (展示数据)
     * 不管是不是从网络获取到时候都从本地取    因为网络获取成功后直接跟新本地数据库
     */
    public PhoneData phonedata=null;
    private void initAppList() {
        List<PhoneData> mlist = dao.queryBuilder().where(PhoneDataDao.Properties.Childuuid.eq(childuuid)).build().list();
        if (mlist.size() > 0) {
            flKong.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            list.clear();
            for (int i = 0; i < mlist.size(); i++) {
                LogUtil.e(TAG, "initData: "+mlist.get(i).getName()+mlist.get(i).getPhone());
                list.add(mlist.get(i));
            }
        } else {//没有app数据
            flKong.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            return;
        }
        if (list.size() < 1) {//白名单长度为0
            flKong.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            return;
        }
        adapter = new PhoneQinQingControlAdapter(this.list, this);
        adapter.setListener(new PhoneQinQingControlAdapter.ForbitCilckListener() {
            @Override
            public void onFbClick(int position, int type ) {
                positions = position;
                affirmDialog.show();
            }
        });

        listView.setAdapter(adapter);
    }

    /**
     * 手动添加号码
     */
    @Override
    public void ChangeNcClick() {
        String et_nc = addPhoneDialog.getEt_nc();
        String et_phone= addPhoneDialog.getEt_phone();
        if(TextUtils.isEmpty(et_nc)){
            Toast.makeText(this, "昵称不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!TextUtils.isEmpty(et_phone)) {
            //定义需要匹配的正则表达式的规则
            String REGEX_MOBILE_SIMPLE = "[1][23456789]\\d{9}";
            //把正则表达式的规则编译成模板
            Pattern pattern = Pattern.compile(REGEX_MOBILE_SIMPLE);
            //把需要匹配的字符给模板匹配，获得匹配器
            Matcher matcher = pattern.matcher(et_phone);
            // 通过匹配器查找是否有该字符，不可重复调用重复调用matcher.find()
            if (!matcher.find()) {//匹配手机号是否存在
                Toast.makeText(this, "手机号格式错误", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            Toast.makeText(this, "手机号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if(list!=null && list.size()>0){
            List<String>datas=new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                datas.add(list.get(i).getPhone());

            }
            if(datas.contains(et_phone)){
                Toast.makeText(this, "不能重复添加此号码", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        addPhoneDialog.et_phone.getText().clear();
        addPhoneDialog.et_nc.getText().clear();
        addPhoneDialog.dismiss();
        showLoading();
        list.add(new PhoneData(null,childuuid,et_nc,et_phone,"1"));
        EventBus.getDefault().post(new RequestQingQinEvent(childuuid, parentUUID,list), "requestWhitePhone");
    }

    /**
     * 保存成功
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "whitephoness")
    private void OrderChildAppListBack (WhiteAppBackEvent returndata) {
        dissloading();
        initData();
    }
    /**
     * 从通讯录获取数据处理
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            ContentResolver reContentResolverol = getContentResolver();
            Uri contactData = data.getData();
            @SuppressWarnings("deprecation")
            Cursor cursor = managedQuery(contactData, null, null, null, null);
            cursor.moveToFirst();
            String username = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phone = reContentResolverol.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                    null,
                    null);
            while (phone.moveToNext()) {
                String usernumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                if (addPhoneDialog != null) {
                    addPhoneDialog.show();
                    addPhoneDialog.et_nc.setText(username);
                    addPhoneDialog.et_phone.setText(usernumber);
                }
            }

        }
    }
}