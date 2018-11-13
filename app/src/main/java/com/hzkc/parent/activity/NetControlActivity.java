package com.hzkc.parent.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hzkc.parent.R;
import com.hzkc.parent.adapter.MyNetControlAdapter;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.event.RequestNetControlEvent;
import com.hzkc.parent.event.WWWControlBackEvent;
import com.hzkc.parent.utils.LogUtil;
import com.hzkc.parent.utils.NetworkUtil;
import com.hzkc.parent.utils.ToastUtils;
import com.jaeger.library.StatusBarUtil;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NetControlActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvSelectAll;
    private TextView tvRemoveApp;
    private TextView tvTopTitle;
    private ImageView ivFinish;
    private ImageView ivAddPlan;
    private LinearLayout flKong;
    private ListView lvNetList;
    private String childUUID,childFrom="ycz";
    private MyNetControlAdapter adapter;
    private List<String> list;
    boolean allFlag = false;
    private ProgressDialog pd;
    private boolean stopFlag;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 106:
                    if (!stopFlag) {
                        ToastUtils.showToast(MyApplication.getContext(),"网络连接不通畅,请稍后再试");
                    }
                    if (pd!=null&&pd.isShowing()){
                        pd.dismiss();
                    }
                    break;
                case 107:
                    if(pd!=null&&pd.isShowing()){
                        pd.dismiss();
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_control);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.blue));
        EventBus.getDefault().register(this);
        stopFlag=false;
        initView();
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
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvRemoveApp = (TextView) findViewById(R.id.tv_remove_app);
        tvSelectAll = (TextView) findViewById(R.id.tv_select_all);
        ivFinish = (ImageView) findViewById(R.id.iv_finish);
        ivAddPlan = (ImageView) findViewById(R.id.iv_add_plan);
        flKong = (LinearLayout) findViewById(R.id.fl_kongbai);
        lvNetList = (ListView) findViewById(R.id.lv_net_list);
        TextView  tv_net_list = (TextView) findViewById(R.id.tv_net_list);
        childUUID = getIntent().getStringExtra("ChildUUID");
        String netData = sp.getString(childUUID + "netList", "1");
        if(netData.equals("1")){
            sp.edit().putString(childUUID + "netList", "").commit();
        }

        if(childFrom.equals("ycz")){
            tvTopTitle.setText("网址管理");
            tv_net_list.setText("下列网站，将不允许孩子访问");
        }else {
            tvTopTitle.setText("网址管理");
            tv_net_list.setText("下列网站，将允许孩子访问");
        }
        ivFinish.setVisibility(View.VISIBLE);
        ivAddPlan.setVisibility(View.VISIBLE);
        ivFinish.setOnClickListener(this);
        ivAddPlan.setOnClickListener(this);
        tvRemoveApp.setOnClickListener(this);
        tvSelectAll.setOnClickListener(this);
        list = new ArrayList<>();
    }

    private void initData() {
        LogUtil.i(TAG, "initData net数据: "+sp.getString(childUUID + "netList", ""));
        String[] mlist = sp.getString(childUUID + "netList", "").split("#,#");
        if (mlist.length > 0) {
            flKong.setVisibility(View.INVISIBLE);
            for (int i = 0; i < mlist.length; i++) {
                LogUtil.i(TAG, "initData: "+mlist[i]);
                if(!TextUtils.isEmpty(mlist[i])){
                    list.add(mlist[i]);
                }
            }
        } else {//没有网址数据
            LogUtil.i(TAG, "initData: 没有网址数据");
            flKong.setVisibility(View.VISIBLE);
            return;
        }
        if (list.size() < 1) {//网址数据为0
            LogUtil.i(TAG, "initData: 网址数据长度为0");
            flKong.setVisibility(View.VISIBLE);
            return;
        }
        adapter = new MyNetControlAdapter(this.list, NetControlActivity.this);
        lvNetList.setAdapter(adapter);
        lvNetList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 取得ViewHolder对象，这样就省去了通过层层的findViewById去实例化我们需要的cb实例的步骤
                MyNetControlAdapter.ViewHolder holder = (MyNetControlAdapter.ViewHolder) view.getTag();
                // 改变CheckBox的状态
                Boolean flag = MyNetControlAdapter.getIsSelected().get(position);
                LogUtil.i(TAG, "onItemClick:11111 " + flag);
                // 将CheckBox的选中状况记录下来
                MyNetControlAdapter.getIsSelected().put(position, !flag);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View v) {
        boolean isNet = NetworkUtil.isConnected();
        switch (v.getId()) {
            case R.id.iv_finish:
                finish();
                break;
            case R.id.iv_add_plan:
//                Intent intent = new Intent(NetControlActivity.this, AddNetActivity.class);
//                intent.putExtra("ChildUUID", childUUID);
//                startActivityForResult(intent, 0);
                showNologDialog();
                break;
            case R.id.tv_remove_app://移除网站
                if (!isNet) {
                    ToastUtils.showToast(MyApplication.getContext(),"网络不通，请检查网络再试");
                    return;
                }
                removeApp();
                handler.postDelayed(runnable, 10000);
                break;
            case R.id.tv_select_all://全选
                selectAll();
                break;
            default:
                break;
        }
    }

    /**
     * 全选
     */
    private void selectAll() {
        if (list.size() > 0) {
            allFlag = !allFlag;
            if (allFlag) {
                // 遍历list的长度，将MyAdapter中的map值全部设为true
                for (int i = 0; i < list.size(); i++) {
                    MyNetControlAdapter.getIsSelected().put(i, true);
                }
            } else {
                // 遍历list的长度，将MyAdapter中的map值全部设为false
                for (int i = 0; i < list.size(); i++) {
                    MyNetControlAdapter.getIsSelected().put(i, false);
                }
            }
            adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "过滤网址为空，请添加", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 移除App
     */
    private void removeApp() {
        HashMap<Integer, Boolean> map = MyNetControlAdapter.getIsSelected();
        LogUtil.i(TAG, "removeApp: " + map);
        if(!(map+"").contains("true")){
            ToastUtils.showToast(MyApplication.getContext(),"请选择要移除的网址");
            return;
        }
        stopFlag=false;
        if(pd!=null&&pd.isShowing()){
            pd.dismiss();
        }
        pd = new ProgressDialog(NetControlActivity.this);
        pd.setMessage("正在执行操作，请稍候");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(false);
        pd.show();
        if (list.size() > 0) {
            List<String> mylist = new ArrayList<>();

            for (int i = 0; i < list.size(); i++) {
                if (map.get(i)) {
                    String netInfo = (String) adapter.getItem(i);
                    LogUtil.i(TAG, "removeApp: "+netInfo);
                    mylist.add(netInfo);
                }
            }
            for (int i = 0; i < mylist.size(); i++) {
                LogUtil.i(TAG, "removeApp: "+mylist.get(i));
                list.remove(mylist.get(i));
            }
            if(list.size()>0){
                String netIndex="";
                for (int i = 0; i < list.size(); i++) {
                    if(i==0){
                        netIndex=list.get(i);
                    }else{
                        netIndex=netIndex+"#,#"+list.get(i);
                    }
                }
                sp.edit().putString(childUUID + "netList", netIndex).commit();
            }else{
                sp.edit().putString(childUUID + "netList", "").commit();
            }
            list.clear();
            initData();
            adapter.notifyDataSetChanged();
            EventBus.getDefault().post(new RequestNetControlEvent(childUUID), "requestNetControl");
        } else {
            Toast.makeText(this, "上网过滤为空，请添加需要过滤的网站", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtil.i(TAG, "onActivityResult: " + adapter);
        LogUtil.i(TAG, "onActivityResult: " + data);
        if(data!=null){
            switch (resultCode) { //resultCode为回传的标记
                case RESULT_OK:
                    list.clear();
                    initData();
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        if(pd!=null&&pd.isShowing()){
            pd.dismiss();
        }
        stopFlag=true;
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * 请求孩子的管控计划
     * */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "wwwcontrolback")
    private void OrderChildStopPlan (WWWControlBackEvent requestData) {
        LogUtil.i(TAG, "OrderChildStopPlan: 请求孩子的管控计划");
        stopFlag=true;
        handler.removeCallbacks(runnable);
        Message msg = Message.obtain();
        msg.what = 107;
        handler.sendMessage(msg);
    }
    /**
     * 显示添加网址
     * */
    private AlertDialog dialog;
    public void showNologDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this,R.style.MyAlertDialog);
        View dialogView =  View.inflate(this, R.layout.dialog_add_net, null);
        TextView  bt_comfirm = (TextView) dialogView.findViewById(R.id.bt_comfirm);
        TextView  bt_cancle = (TextView) dialogView.findViewById(R.id.bt_cancle);
        final EditText et_content = (EditText) dialogView.findViewById(R.id.et_my_nc);
        bt_comfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                save(et_content.getText().toString());
            }
        });
        bt_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        builder.setView(dialogView);
        dialog = builder.show();
    }

    private void save( String netName) {
        String netData = sp.getString(childUUID + "netList", "");
        if(netData.contains(netName)){
            Toast.makeText(this,"您输入的网址已经存在，请输入其他网址",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(netData)){
            sp.edit().putString(childUUID + "netList", netName).commit();
        }else{
            sp.edit().putString(childUUID + "netList", netData+"#,#"+netName).commit();
        }
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        EventBus.getDefault().post(new RequestNetControlEvent(childUUID), "requestNetControl");
        list.clear();
        initData();
        adapter.notifyDataSetChanged();
    }
}
