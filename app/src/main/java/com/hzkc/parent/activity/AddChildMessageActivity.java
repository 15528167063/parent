package com.hzkc.parent.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hzkc.parent.R;
import com.wx.wheelview.adapter.ArrayWheelAdapter;
import com.wx.wheelview.widget.WheelView;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class AddChildMessageActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rlChildClassSelecte;
    String[] mChildClass = {"幼儿园小班", "幼儿园中班", "幼儿园大班", "一年级", "二年级",
            "三年级", "四年级", "五年级", "六年级", "七年级", "八年级", "九年级"};
    private List<String> list;
    private View view;
    private PopupWindow mPopupWindow;
    private WheelView wheelView;
    private ImageView ivFinish;
    private Button btWheelCancel;
    private Button btWheelConfirm;
    private TextView tvChildClass;
    private TextView tvTopTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_child_message);
        initView();
        initData();
    }

    private void initView() {
        rlChildClassSelecte = (RelativeLayout) findViewById(R.id.rl_child_class_selecte);
        ivFinish = (ImageView) findViewById(R.id.iv_finish);
        tvChildClass = (TextView) findViewById(R.id.tv_child_class);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);

        view = View.inflate(AddChildMessageActivity.this, R.layout.dialogue_wheel_class, null);
        wheelView = (WheelView) view.findViewById(R.id.wheelview);
        btWheelCancel = (Button) view.findViewById(R.id.bt_wheel_cancel);
        btWheelConfirm = (Button) view.findViewById(R.id.bt_wheel_confirm);
        ivFinish.setVisibility(View.VISIBLE);
        tvChildClass.setText(mChildClass[0]);
        tvTopTitle.setText("添加孩子");

        list = new ArrayList<>();
        for (int i = 0; i < mChildClass.length; i++) {
            list.add(mChildClass[i]);
        }
    }

    private void initData() {
        //初始化滚轮控件
        wheelView.setWheelAdapter(new ArrayWheelAdapter(AddChildMessageActivity.this)); // 文本数据源
        wheelView.setWheelSize(5);
        wheelView.setSelection(0);
        wheelView.setWheelClickable(true);
        wheelView.setSkin(WheelView.Skin.Holo); // common皮肤
        wheelView.setWheelData(list);  // 数据集合
        //设置PopupWindow
        mPopupWindow = new PopupWindow(view,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        mPopupWindow.getContentView().setFocusableInTouchMode(true);
        mPopupWindow.getContentView().setFocusable(true);
        mPopupWindow.setAnimationStyle(R.style.anim_menu_bottombar);
        //设置点击事件
        ivFinish.setOnClickListener(this);
        rlChildClassSelecte.setOnClickListener(this);
        btWheelCancel.setOnClickListener(this);
        btWheelConfirm.setOnClickListener(this);
        //设置滑轮的点击事件
        wheelView.setOnWheelItemClickListener(new WheelView.OnWheelItemClickListener() {
            @Override
            public void onItemClick(int position, Object o) {
                tvChildClass.setText(mChildClass[position]);
                mPopupWindow.dismiss();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_finish:
                finish();
                break;
            case R.id.rl_child_class_selecte:
                mPopupWindow.showAtLocation(findViewById(R.id.ll_add_child), Gravity.BOTTOM, 0, 0);
                break;
            case R.id.bt_wheel_cancel:
                mPopupWindow.dismiss();
                break;
            case R.id.bt_wheel_confirm:
                //获取当前滚轮位置的数据
                String selectedClass = (String) wheelView.getSelectionItem();
                tvChildClass.setText(selectedClass);
                mPopupWindow.dismiss();
                break;
            default:

                break;
        }
    }
    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
