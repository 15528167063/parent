package com.hzkc.parent.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hzkc.parent.Bean.InternetItemBean;
import com.hzkc.parent.R;
import com.hzkc.parent.adapter.MoreFunctionAdapter;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.event.CloseYjspDataEvent;
import com.hzkc.parent.event.YjspDataEvent;
import com.hzkc.parent.event.YjspEvent;
import com.hzkc.parent.greendao.GreenDaoManager;
import com.hzkc.parent.greendao.entity.ChildContrlFlag;
import com.hzkc.parent.greendao.gen.ChildContrlFlagDao;
import com.hzkc.parent.jsondata.CmdCommon;
import com.hzkc.parent.mina.Constants;
import com.hzkc.parent.utils.LogUtil;
import com.hzkc.parent.utils.NetworkUtil;
import com.hzkc.parent.utils.ToastUtils;
import com.hzkc.parent.view.AffirmDialog;
import com.jaeger.library.StatusBarUtil;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MoreFunctionActivity extends BaseActivity implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private TextView topTitle;
    private ImageView tvFinish;
    private MoreFunctionAdapter mAdapter;
    private List<InternetItemBean> mDatas;
    private String childUUID,childFrom;
    private String[] moreFunctionName = {"应用追踪", "网址黑名单"};
    private String[] moreFunctionName1 = {"应用追踪", "网址白名单","号码管理"};
    private int[] moreFunctionIcon = {R.drawable.ic_zhuizong, R.drawable.ic_websa};
    private int[] moreFunctionIcon1 = {R.drawable.ic_zhuizong, R.drawable.ic_websa, R.drawable.main_phone};
    private String parentUUID,viplist;
    private ProgressDialog pd;
    private boolean yjspFlag,isVip;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 204://处理一键锁屏反馈
                    ordeyjsp(msg);
                case 106:
                    if (!yjspFlag) {
                        ToastUtils.showToast(MyApplication.getContext(),"网络连接不通畅,请稍后再试");
                    }
                    if(pd!=null&&pd.isShowing()){
                        pd.dismiss();
                    }
                    break;
                case 205://处理关闭一键锁屏
                    ordeCloseyjsp();
                default:
                    break;
            }
        }
    };

    private String yjsp;
    private Runnable runnable;
    private String childLineState;
    private AffirmDialog affirmDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_function);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.blue));
        initView();
        EventBus.getDefault().register(this);
        initData();
        runnable = new Runnable() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                msg.what = 106;
                handler.sendMessage(msg);
            }
        };
    }

    private void initView() {
        mRecyclerView = (RecyclerView)findViewById(R.id.id_recyclerview);
        topTitle=(TextView)findViewById(R.id.tv_top_title);
        tvFinish=(ImageView) findViewById(R.id.iv_finish);
        topTitle.setText("更多功能");
        tvFinish.setVisibility(View.VISIBLE);
        parentUUID = sp.getString("parentUUID", "");
        childUUID = getIntent().getStringExtra("ChildUUID");
        childFrom = getIntent().getStringExtra("childFrom");
        childLineState = sp.getString(childUUID + "online", "");
        tvFinish.setOnClickListener(this);
        yjspFlag=false;

        viplist = sp.getString("viplist", "");
        isVip = sp.getBoolean("isVip", false);
        affirmDialog = new AffirmDialog(this);
        affirmDialog.setTitleText("您还不是VIP会员，不能使用VIP功能，是否立即充值?");
        affirmDialog.setAffirmClickListener(this);
    }

    private void initData() {
        //初始化条目数据
        initItemData();
        mRecyclerView.setLayoutManager(new GridLayoutManager(MoreFunctionActivity.this, 3));
        mAdapter = new MoreFunctionAdapter(this, mDatas);
        mAdapter.setOnItemClickListener(new MoreFunctionAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                boolean isNet = NetworkUtil.isConnected();
                if (!isNet) {
                    Toast.makeText(MoreFunctionActivity.this, "网络不通，请检查网络再试", Toast.LENGTH_SHORT).show();
                    return;
                }
                switch (position) {
                    case 0://应用追踪
                        if(viplist.contains(Constants.VIP_APP_ZHIZHONG)){
                            if(viplist.contains(Constants.VIP_EYE_PROTECT)){
                                if(!isVip){
                                    if (affirmDialog != null) {
                                        affirmDialog.show();
                                    }
                                    return;
                                }
                            }
                        }
                        Intent intent1 = new Intent(MoreFunctionActivity.this, AppUseActivity.class);
                        intent1.putExtra("ChildUUID", childUUID);
                        startActivity(intent1);
                        break;
                    case 1://上网管控
                        if(viplist.contains(Constants.VIP_WEB_CHOOSE)){
                            if(!isVip){
                                if (affirmDialog != null) {
                                    affirmDialog.show();
                                }
                                return;
                            }
                        }
                        //
                        Intent intent2 = new Intent(MoreFunctionActivity.this, NetControlActivity.class);
//                        Intent intent2= new Intent(MoreFunctionActivity.this, PhoneManageActivity.class);
                        intent2.putExtra("ChildUUID", childUUID);
                        intent2.putExtra("childFrom", childFrom);
                        startActivity(intent2);
                        break;
                    case 2://上网管控
                        Toast.makeText(MoreFunctionActivity.this, "此功能正在开发中（号码及短信黑白名单管理）", Toast.LENGTH_SHORT).show();
//                        Intent intent3 = new Intent(MoreFunctionActivity.this, PhoneManageActivity.class);
//                        intent3.putExtra("ChildUUID", childUUID);
//                        startActivity(intent3);
                        break;
                    default:
                        break;
                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_finish:
                finish();
                break;
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
            default:
                break;
        }
    }
    /**
     * 初始化条目数据
     */
    private void initItemData() {
        mDatas = new ArrayList<InternetItemBean>();
        ChildContrlFlagDao childDao = GreenDaoManager.getInstance().getSession().getChildContrlFlagDao();
        ChildContrlFlag findChild = childDao.queryBuilder().where(ChildContrlFlagDao.Properties.Childuuid.eq(childUUID)).build().unique();
        setFindChild(findChild);
    }
    /**
     * 设置找到学生的状态
     */
    public void setFindChild(ChildContrlFlag findChild) {
        if (findChild != null) {
            String yjspfalg = findChild.getYjspfalg();
            if(childFrom.equals("ycz")){
                for (int i = 0; i < moreFunctionName.length; i++) {
                    InternetItemBean itemBean = new InternetItemBean();
                    itemBean.setTvName(moreFunctionName[i]);
                    itemBean.setIvIcon(moreFunctionIcon[i]);
                    mDatas.add(itemBean);
                }
            }else {
                for (int i = 0; i < moreFunctionName1.length; i++) {
                    InternetItemBean itemBean = new InternetItemBean();
                    itemBean.setTvName(moreFunctionName1[i]);
                    itemBean.setIvIcon(moreFunctionIcon1[i]);
                    mDatas.add(itemBean);
                }
            }

        }
    }

    /**
     * 一键锁屏功能
     */
    private void yjsp() {
        yjspFlag=false;
        if(pd!=null&&pd.isShowing()){
            pd.dismiss();
        }
        pd = new ProgressDialog(MoreFunctionActivity.this);
        pd.setMessage("正在执行操作，请稍候");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(false);
        pd.show();
        ChildContrlFlagDao childDao = GreenDaoManager.getInstance().getSession().getChildContrlFlagDao();
        ChildContrlFlag findChild = childDao.queryBuilder().where(ChildContrlFlagDao.Properties.Childuuid.eq(childUUID)).build().unique();
        if (findChild != null) {
            if (findChild.getYjspfalg().equals(CmdCommon.CMD_FLAG_OPEN)) {
                yjsp = CmdCommon.CMD_FLAG_CLOSE;
            } else {
                yjsp = CmdCommon.CMD_FLAG_OPEN;
            }
            childDao.update(findChild);
        } else {
            ChildContrlFlag childFlag = new ChildContrlFlag(null,childUUID, parentUUID, "", ""
                    , CmdCommon.CMD_FLAG_CLOSE, CmdCommon.CMD_FLAG_CLOSE
                    , CmdCommon.CMD_FLAG_CLOSE, "30", "5", CmdCommon.CMD_FLAG_CLOSE, CmdCommon.CMD_FLAG_CLOSE);
            childDao.insert(childFlag);
            yjsp = CmdCommon.CMD_FLAG_CLOSE;
        }
        EventBus.getDefault().post(new YjspEvent(parentUUID, childUUID, yjsp), "yjsp");
        LogUtil.e(TAG, "锁屏功能: " + yjsp);
    }

    /**
     * 一键锁屏功能反馈
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "yjspata")
    public void yjspDataEvent(YjspDataEvent dataEvent) {
        yjspFlag=true;
        handler.removeCallbacks(runnable);
        Message obtain = Message.obtain();
        LogUtil.e(TAG, "一键锁屏功能反馈:yjgkDataEvent");
        obtain.obj = dataEvent.yjspState;
        obtain.what = 204;
        handler.sendMessage(obtain);
    }

    /**
     * 关闭一键锁屏功能
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "closeyjsp")
    public void closeYjspDataEvent(CloseYjspDataEvent dataEvent) {
        yjspFlag=true;
        Message obtain = Message.obtain();
        LogUtil.e(TAG, "关闭一键锁屏功能:closeYjspDataEvent");
        obtain.what = 205;
        handler.sendMessage(obtain);
    }
    /**
     * 处理一键锁屏反馈
     * */
    private void ordeyjsp(Message msg) {
        if(pd!=null&&pd.isShowing()){
            pd.dismiss();
        }
        //String yjgk = (String) msg.obj;
        ChildContrlFlagDao childDao = GreenDaoManager.getInstance().getSession().getChildContrlFlagDao();
        ChildContrlFlag findChild = childDao.queryBuilder().where(ChildContrlFlagDao.Properties.Childuuid.eq(childUUID)).build().unique();
        if (findChild != null) {
            if (yjsp.equals(CmdCommon.CMD_FLAG_CLOSE)) {
                ToastUtils.showToast(MoreFunctionActivity.this,"一键锁屏功能关闭");
                mDatas.get(0).setTv2Name("状态:已关闭");
                findChild.setYjspfalg(CmdCommon.CMD_FLAG_CLOSE);
            } else {
                ToastUtils.showToast(MoreFunctionActivity.this,"一键锁屏功能开启");
                mDatas.get(0).setTv2Name("状态:开启");
                findChild.setYjspfalg(CmdCommon.CMD_FLAG_OPEN);
            }
            childDao.update(findChild);
        } else {
            ChildContrlFlag childFlag = new ChildContrlFlag(null, childUUID, parentUUID, "", ""
                    , CmdCommon.CMD_FLAG_CLOSE, CmdCommon.CMD_FLAG_CLOSE
                    , CmdCommon.CMD_FLAG_CLOSE, "30", "5", CmdCommon.CMD_FLAG_CLOSE, CmdCommon.CMD_FLAG_CLOSE);
            childDao.insert(childFlag);
            mDatas.get(0).setTv2Name("状态:已关闭");
            ToastUtils.showToast(MoreFunctionActivity.this,"一键锁屏功能关闭");
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 处理关闭一键锁屏
     * */
    private void ordeCloseyjsp() {
        yjsp = CmdCommon.CMD_FLAG_CLOSE;
        mDatas.get(0).setTv2Name("状态:已关闭");
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        yjspFlag=true;
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
