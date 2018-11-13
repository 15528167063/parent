package com.hzkc.parent.solider.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.hzkc.parent.Bean.TeamResult;
import com.hzkc.parent.Bean.TeamRoot;
import com.hzkc.parent.R;
import com.hzkc.parent.activity.BaseActivity;
import com.hzkc.parent.adapter.ControllDetalAdapter;
import com.hzkc.parent.greendao.entity.ChildTable;
import com.hzkc.parent.mina.Constants;
import com.jaeger.library.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/6/8.
 */

public class ControllDetailActivity extends  BaseActivity implements View.OnClickListener {
    private TextView  tv_head;
    private ImageView iv_finish;
    private ListView listView;
    private ControllDetalAdapter adpter;
    private List<ChildTable> planlist;
    private TeamRoot teamRoot;
    public int type=-1;//1 详情  2托管   "" 0不收管   1团控
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_controll);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.blue));
        initView();
        initData();
    }



    private void initView() {
        tv_head = (TextView) findViewById(R.id.tv_top_title);
        iv_finish = (ImageView) findViewById(R.id.iv_finish);
        listView = (ListView) findViewById(R.id.listview);

        type=getIntent().getIntExtra("tytpe",-1);
        iv_finish.setVisibility(View.VISIBLE);
        tv_head.setText("团控详情");
        iv_finish.setOnClickListener(this);

    }
    private void initData() {
        planlist=new ArrayList<>();
        getControllChilid();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_finish:
                finish();
                break;
        }

    }

    /**
     * 获取数据
     */
    private void getControllChilid() {
        String parentUUID1 = sp.getString("parentUUID", "");
        String url = Constants.FIND_URL+"Ardpolice/public/index.php/GetSoldierData/"+parentUUID1+".php";
        Log.e("-------------url2",url);
        StringRequest stringRequest1 = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("-------------response",response);
                        teamRoot = new Gson().fromJson(response, TeamRoot.class);
                        if(teamRoot.getCode().equals("201")){
                            Toast.makeText(ControllDetailActivity.this, "参数不完整", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(teamRoot.getCode().equals("202")){
                            return;
                        }
                        initNewBarDatas(teamRoot);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        stringRequest1.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(ControllDetailActivity.this).add(stringRequest1);
    }
    /**
     * 处理数据
     */
    private void initNewBarDatas(TeamRoot teamRoot) {
        List<TeamResult> result = teamRoot.getResult();
        for (int i = 0; i < result.size(); i++) {
            planlist.add(new ChildTable(null,result.get(i).getName(),result.get(i).getSex(),"","",result.get(i).getState(),result.get(i).getRegtime(),result.get(i).getLoginname(),""));
        }
        adpter=new ControllDetalAdapter(planlist,this);
        listView.setAdapter(adpter);

    }

}
