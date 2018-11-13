package com.hzkc.parent.solider.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hzkc.parent.R;
import com.hzkc.parent.activity.BaseActivity;
import com.jaeger.library.StatusBarUtil;

public class SoliderUnbindCodeActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvTopTitle;
    private TextView ivFinish;
    private TextView tvChildUnbindCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solider_unbind_code);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.blue));
        initView();
    }
    private void initView() {
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        ivFinish = (TextView) findViewById(R.id.tv_finish_mine);
        tvChildUnbindCode = (TextView) findViewById(R.id.tv_child_unbindcode);
        tvTopTitle.setText("卸载码");
        ivFinish.setText("士兵资料");
        String childUUID = getIntent().getStringExtra("ChildUUID");
        tvChildUnbindCode.setText(childUUID);
        ivFinish.setVisibility(View.VISIBLE);
        ivFinish.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_finish_mine:
                finish();
                break;
            default:
                break;
        }
    }
}
