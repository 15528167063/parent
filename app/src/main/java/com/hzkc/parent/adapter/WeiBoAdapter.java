package com.hzkc.parent.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hzkc.parent.Bean.FriendsListBean;
import com.hzkc.parent.Bean.PhotoInfo;
import com.hzkc.parent.R;
import com.hzkc.parent.activity.FindAddFriendActivity;
import com.hzkc.parent.activity.FindCommentActivity;
import com.hzkc.parent.activity.FindCommentListActivity;
import com.hzkc.parent.activity.ImagePagerActivity;
import com.hzkc.parent.jsondata.LoadingFooter;
import com.hzkc.parent.mina.Constants;
import com.hzkc.parent.utils.LogUtil;
import com.hzkc.parent.utils.MyUtils;
import com.hzkc.parent.utils.NetworkUtil;
import com.hzkc.parent.view.MultiImageView;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;
import static android.widget.Toast.makeText;


/**
 * Created by Administrator on 2016/7/11.
 */

public class WeiBoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<FriendsListBean> mDatas;
    private Context mContext;
    private View mView;
    private final int HEADRLAYOUT = 0;
    private final int NORMALLAYOUT = 1;
    private final int FOOTERLAYOUT = 2;
    public FooterHolder mFooterHolder;
    public HeaderViewHolder mHeadHolder;
    private List<Integer> checkPositionlist;
    public SharedPreferences sp;
    private final String dianzanId;
    private NewMsgListener listener;
    private final String phoneNum;

    public WeiBoAdapter(Context context, ArrayList<FriendsListBean> mDatas) {
        this.mContext = context;
        this.mDatas = mDatas;
        checkPositionlist = new ArrayList<>();
        sp = context.getSharedPreferences("info", context.MODE_PRIVATE);
        dianzanId = sp.getString("dianzanId", "");
        phoneNum = sp.getString("phoneNum", "");
        Log.e("-------------",mDatas.size()+"");
    }

    public void setDatas(ArrayList<FriendsListBean> statuses) {
        this.mDatas = statuses;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == NORMALLAYOUT) {
            view = LayoutInflater.from(mContext).inflate(R.layout.list_item_status, parent, false);
            return new OriginViewHolder(view);
        } else if(viewType ==FOOTERLAYOUT){
            view = LayoutInflater.from(mContext).inflate(R.layout.sample_common_list_footer, parent, false);
            mFooterHolder = new FooterHolder(view);
            return mFooterHolder;
        } else{
            view = LayoutInflater.from(mContext).inflate(R.layout.head_circle, parent, false);
            mHeadHolder = new HeaderViewHolder(view);
            return mHeadHolder;
        }
    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0){
            return HEADRLAYOUT;
        } else if (position == mDatas.size()+1){
            return FOOTERLAYOUT;
        } else{
            return NORMALLAYOUT;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int p) {
        if(getItemViewType(p)==HEADRLAYOUT){
            HeaderViewHolder headholder = (HeaderViewHolder) holder;
            String myNc = sp.getString("nc", "");
            headholder.tv_name.setText(myNc);
            //headholder.tv_new_msg.setVisibility(View.VISIBLE);
            headholder.iv_icon.setImageURI(Constants.FIND_URL_TX+phoneNum.substring(phoneNum.length()-2)+".jpg");
            if(listener!=null){
                LogUtil.i(TAG, "onClick: ");
                listener.SetTheMsgIcon(headholder.tv_new_msg);
            }
        }else if (p != mDatas.size()+1) {
            final int position=p-1;
            final FriendsListBean status = mDatas.get(position);
            if (holder instanceof OriginViewHolder) {
                //设置头像
                //((OriginViewHolder) holder).profile_img.setImageURI("http://img2.utuku.china.com/500x0/news/20170223/5cbd9d33-5edb-4c1b-920f-dcd0e12f5935.jpg");
                ((OriginViewHolder) holder).profile_img.setImageURI(Constants.FIND_URL_TX+status.getNcpic()+".jpg");
                ((OriginViewHolder) holder).profile_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, FindAddFriendActivity.class);
                        intent.putExtra("nc",status.getNc());
                        intent.putExtra("pic",status.getNcpic());
                        intent.putExtra("userid",status.getNcid());
                        mContext.startActivity(intent);

                        sendMessages();
                    }
                });
                //设置姓名
                ((OriginViewHolder) holder).profile_name.setText(status.getNc());
                //设置时间
                ((OriginViewHolder) holder).profile_time.setText(status.getTime());
                //设置微博内容
                ((OriginViewHolder) holder).weibo_content.setText(status.getText());
                //设置微博图片
                String picbz = status.getPicbz();
                String imgFlag = MyUtils.decodeString64(picbz);
                if (imgFlag.equals("1")) {//有图片
                    String picdz="";
                    if(status.getPicdz().substring(0,1).equals(";")){
                        picdz = status.getPicdz().substring(1);
                    }else{
                        picdz = status.getPicdz();
                    }
                    String[] split = picdz.split(";");

                    final List<PhotoInfo> imageDatas = new ArrayList<>();
                    for (int i = 0; i < split.length; i++) {
                        imageDatas.add(new PhotoInfo(Constants.FIND_URL_IMG+split[i]));
                    }
                    LogUtil.e("TAG", "图片的数量为>>>" + imageDatas.size());
                    if (imageDatas != null && imageDatas.size() > 0) {
                        ((OriginViewHolder)holder).multiImagView.setVisibility(View.VISIBLE);
                        ((OriginViewHolder)holder).multiImagView.setList(imageDatas);
                        ((OriginViewHolder)holder).multiImagView.setOnItemClickListener(new MultiImageView.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                //imagesize是作为loading时的图片size
                                ImagePagerActivity.ImageSize imageSize = new ImagePagerActivity.ImageSize(view.getMeasuredWidth(), view.getMeasuredHeight());
                                List<String> photoUrls = new ArrayList<String>();
                                for(PhotoInfo photoInfo : imageDatas){
                                    LogUtil.e(photoInfo.url);
                                    photoUrls.add(photoInfo.url);
                                }
                                ImagePagerActivity.startImagePagerActivity(mContext, photoUrls, position, imageSize);
                            }
                        });
                    }
                } else {
                    ((OriginViewHolder)holder).multiImagView.setVisibility(View.GONE);
                }


                //设置微博内容
                ((OriginViewHolder) holder).weibo_content.setText(status.getText());
                //设置评论点击事件
                final String plbz = status.getPlbz();
                if (plbz.equals("1")) {//有评论
                    ((OriginViewHolder) holder).text_comment.setText(status.getPltj());
                } else {
                    ((OriginViewHolder) holder).text_comment.setText("评论");
                }
                ((OriginViewHolder) holder).linear_comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!NetworkUtil.isConnected()) {
                            Toast.makeText(mContext, "网络不通，请检查网络再试", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (plbz.equals("1")) {//有评论
                            Intent intent = new Intent(mContext, FindCommentListActivity.class);
                            intent.putExtra("msgId", status.getId());
                            intent.putExtra("dztj", status.getDztj());
                            intent.putExtra("comment", status.getPlnr());
                            mContext.startActivity(intent);
                            sendMessages();
                        } else {//没有评论
                            Intent intent = new Intent(mContext, FindCommentActivity.class);
                            intent.putExtra("msgId", status.getId());
                            mContext.startActivity(intent);
                            sendMessages();
                        }
                    }
                });
                //设置点赞点击事件
                final String dzbz = status.getDzlist();
                //((OriginViewHolder) holder).text_favour.setTextColor(mContext.getResources().getColor(R.color.gray));
//                ((OriginViewHolder) holder).iv_favour.setImageResource(R.drawable.timeline_icon_unlike);
                ((OriginViewHolder) holder).text_favour.setTag(new Integer(position));//设置tag 否则划回来时选中消失

                ((OriginViewHolder) holder).iv_favour.setVisibility(View.VISIBLE);

                String phoneNum = sp.getString("phoneNum", "");
                Log.e("---------",dzbz+"---"+phoneNum);
                if (dzbz.contains(phoneNum)) {//有点赞
                    //checkbox  复用问题
                    if (checkPositionlist != null) {
                        //((OriginViewHolder) holder).linear_favour.setEnabled(true);
                        if (checkPositionlist.contains(new Integer(position))) {
                            //((OriginViewHolder) holder).text_favour.setTextColor(mContext.getResources().getColor(R.color.blue));
//                            ((OriginViewHolder) holder).iv_favour.setImageResource(R.drawable.timeline_icon_like);
//                            ((OriginViewHolder) holder).linear_favour.setClickable(false);
                            ((OriginViewHolder) holder).iv_favour.setVisibility(View.INVISIBLE);
                            ((OriginViewHolder) holder).iv_favoured.setVisibility(View.VISIBLE);
//                            Log.e("+++++++++++++++1",(Integer.parseInt(status.getDztj()) ) + "position="+position+status.getText());
                            ((OriginViewHolder) holder).text_favour.setText((Integer.parseInt(status.getDztj())) + "");
                        } else if(dianzanId.contains("#"+status.getId()+"#")){
                            //((OriginViewHolder) holder).text_favour.setTextColor(mContext.getResources().getColor(R.color.blue));
//                            ((OriginViewHolder) holder).iv_favour.setImageResource(R.drawable.timeline_icon_like);
//                            ((OriginViewHolder) holder).linear_favour.setClickable(false);
//                            ((OriginViewHolder) holder).linear_favour.setEnabled(false);
                            ((OriginViewHolder) holder).iv_favour.setVisibility(View.INVISIBLE);
                            ((OriginViewHolder) holder).iv_favoured.setVisibility(View.VISIBLE);

//                            Log.e("+++++++++++++++2",(Integer.parseInt(status.getDztj()) ) + ""+dianzanId+"position="+position+status.getText());
                            ((OriginViewHolder) holder).text_favour.setText((status.getDztj()));
                        }else{
//                            Log.e("+++++++++++++++3",(Integer.parseInt(status.getDztj()) ) +dianzanId+"#"+status.getId());
                            ((OriginViewHolder) holder).text_favour.setText(status.getDztj());

                            ((OriginViewHolder) holder).iv_favour.setVisibility(View.INVISIBLE);
                            ((OriginViewHolder) holder).iv_favoured.setVisibility(View.VISIBLE);

                        }
                    } else {
                        if(dianzanId.contains("#"+status.getId()+"#")){
                            //((OriginViewHolder) holder).text_favour.setTextColor(mContext.getResources().getColor(R.color.blue));
//                            ((OriginViewHolder) holder).iv_favour.setImageResource(R.drawable.timeline_icon_like);
//                            ((OriginViewHolder) holder).linear_favour.setClickable(false);
//                            ((OriginViewHolder) holder).linear_favour.setEnabled(false);
                            ((OriginViewHolder) holder).iv_favour.setVisibility(View.INVISIBLE);
                            ((OriginViewHolder) holder).iv_favoured.setVisibility(View.VISIBLE);

//                            Log.e("+++++++++++++++4",(Integer.parseInt(status.getDztj())) + "position="+position+status.getText());
                            ((OriginViewHolder) holder).text_favour.setText((status.getDztj()));
                        }else{
//                            Log.e("+++++++++++++++5",(Integer.parseInt(status.getDztj()) ) +"position="+position+status.getText());
                            ((OriginViewHolder) holder).text_favour.setText(status.getDztj());
                        }
                    }
                } else {
                    //checkbox  复用问题
                    //((OriginViewHolder) holder).linear_favour.setEnabled(true);
                    if (checkPositionlist != null) {
//                        if (checkPositionlist.contains(new Integer(position))) {
////                            ((OriginViewHolder) holder).linear_favour.setClickable(false);
//                            //((OriginViewHolder) holder).text_favour.setTextColor(mContext.getResources().getColor(R.color.blue));
////                            ((OriginViewHolder) holder).iv_favour.setImageResource(R.drawable.timeline_icon_like);
//                            ((OriginViewHolder) holder).iv_favour.setVisibility(View.INVISIBLE);
//                            ((OriginViewHolder) holder).iv_favoured.setVisibility(View.VISIBLE);
//                            Log.e("+++++++++++++++6", 1 + "position="+position+status.getText());
//                            ((OriginViewHolder) holder).text_favour.setText("1");
//                        } else {
//                        Log.e("+++++++++++++++7",0 +"position="+position+status.getText());

                        ((OriginViewHolder) holder).text_favour.setText(status.getDztj());
                        ((OriginViewHolder) holder).iv_favour.setVisibility(View.VISIBLE);
                        ((OriginViewHolder) holder).iv_favoured.setVisibility(View.INVISIBLE);
//                        }
                    } else {
//                        Log.e("+++++++++++++++8",0 + "position="+position+status.getText());
                        ((OriginViewHolder) holder).text_favour.setText(status.getDztj());
                        ((OriginViewHolder) holder).iv_favour.setVisibility(View.VISIBLE);
                        ((OriginViewHolder) holder).iv_favoured.setVisibility(View.INVISIBLE);
                    }
                }
                ((OriginViewHolder) holder).linear_favour.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!NetworkUtil.isConnected()) {
                            Toast.makeText(mContext, "网络不通，请检查网络再试", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(  ((OriginViewHolder) holder).iv_favoured.getVisibility()==View.VISIBLE){
                            return;
                        }
//                        if (dzbz.equals("1")) {//有点赞
//                            Log.e("+++++++++++++++9",(Integer.parseInt(status.getDztj()) + 1) + "position="+position+status.getText());
//                            ((OriginViewHolder) holder).text_favour.setText((Integer.parseInt(status.getDztj()) + 1) + "");
//                        } else {
//                            Log.e("+++++++++++++++10",1 + "position="+position+status.getText());
//                            ((OriginViewHolder) holder).text_favour.setText("1");
//                        }


                        if (!checkPositionlist.contains(((OriginViewHolder) holder).text_favour.getTag())) {
                            checkPositionlist.add(new Integer(position));
                        }
                        new Thread() {
                            @Override
                            public void run() {
                                String  phoneNum = sp.getString("phoneNum", "");
                                String url = Constants.FIND_URL_DZ+"id=" + status.getId() + "&phone=" + phoneNum;
                                Log.e("_------",url);

                                //点赞
                                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                //点赞成功修改状态
                                                ((OriginViewHolder) holder).text_favour.setText((Integer.parseInt(status.getDztj()) + 1) + "");
                                                ((OriginViewHolder) holder).iv_favour.setVisibility(View.INVISIBLE);
                                                ((OriginViewHolder) holder).iv_favoured.setVisibility(View.VISIBLE);
                                                LogUtil.e("点赞", "点赞:" + response);
                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        makeText(mContext, "服务器忙请稍后再试", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                Volley.newRequestQueue(mContext).add(stringRequest);
                            }
                        }.start();
                        //((OriginViewHolder) holder).text_favour.setTextColor(mContext.getResources().getColor(R.color.blue));
//                        ((OriginViewHolder) holder).iv_favour.setImageResource(R.drawable.timeline_icon_like);
//                        ((OriginViewHolder) holder).linear_favour.setClickable(false);

                        sp.edit().putString("dianzanId",dianzanId+"#"+status.getId()+"#").commit();
                    }
                });
                ((OriginViewHolder) holder).origin_status_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!NetworkUtil.isConnected()) {
                            Toast.makeText(mContext, "网络不通，请检查网络再试", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (plbz.equals("1")) {//有评论
                            Intent intent = new Intent(mContext, FindCommentListActivity.class);
                            intent.putExtra("msgId", status.getId());
                            intent.putExtra("dztj", status.getDztj());
                            intent.putExtra("comment", status.getPlnr());
                            mContext.startActivity(intent);
                            sendMessages();
                        } else {//没有评论
                            Intent intent = new Intent(mContext, FindCommentActivity.class);
                            intent.putExtra("msgId", status.getId());
                            mContext.startActivity(intent);
                            sendMessages();
                        }
                    }
                });
            }
        }
    }


    @Override
    public int getItemCount() {
        return mDatas.size() + 2;
    }


    public static class OriginViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView profile_img;

        ImageView profile_verified;

        ImageView iv_favour,iv_favoured;

        TextView profile_name;

        TextView profile_time;

        TextView weiboComeFrom;

        TextView weibo_content;

        MultiImageView multiImagView;

        LinearLayout linear_favour;

        LinearLayout linear_comment;

        TextView text_comment;

        TextView text_favour;

        LinearLayout origin_status_layout;

        public OriginViewHolder(View itemView) {
            super(itemView);
            profile_img = (SimpleDraweeView) itemView.findViewById(R.id.profile_img);
            profile_verified = (ImageView) itemView.findViewById(R.id.profile_verified);
            iv_favour = (ImageView) itemView.findViewById(R.id.iv_favour);
            iv_favoured = (ImageView) itemView.findViewById(R.id.iv_favoured);
            profile_name = (TextView) itemView.findViewById(R.id.profile_name);
            profile_time = (TextView) itemView.findViewById(R.id.profile_time);
            weiboComeFrom = (TextView) itemView.findViewById(R.id.weiboComeFrom);
            weibo_content = (TextView) itemView.findViewById(R.id.weibo_content);
            multiImagView = (MultiImageView) itemView.findViewById(R.id.multiImagView);
            linear_favour = (LinearLayout) itemView.findViewById(R.id.linear_favour);
            linear_comment = (LinearLayout) itemView.findViewById(R.id.linear_comment);
            text_comment = (TextView) itemView.findViewById(R.id.text_comment);
            text_favour = (TextView) itemView.findViewById(R.id.text_favour);
            origin_status_layout = (LinearLayout) itemView.findViewById(R.id.origin_status_layout);
        }
    }

    public class FooterHolder extends RecyclerView.ViewHolder {
        View mLoadingViewstubstub;
        View mEndViewstub;
        View mNetworkErrorViewstub;

        public FooterHolder(View itemView) {
            super(itemView);
            mLoadingViewstubstub = itemView.findViewById(R.id.loading_viewstub);
            mEndViewstub = itemView.findViewById(R.id.end_viewstub);
            mNetworkErrorViewstub = itemView.findViewById(R.id.network_error_viewstub);
        }

        //根据传过来的status控制哪个状态可见
        public void setData(int status) {
            Log.d("TAG", "reduAdapter" + status + "");
            switch (status) {
                case LoadingFooter.Normal:
                    setAllGone();
                    break;
                case LoadingFooter.Loading://加载中
                    setAllGone();
                    mLoadingViewstubstub.setVisibility(View.VISIBLE);
                    break;
                case LoadingFooter.TheEnd:
                    setAllGone();
                    mEndViewstub.setVisibility(View.VISIBLE);
                    break;
                case LoadingFooter.NetWorkError:
                    setAllGone();
                    mNetworkErrorViewstub.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }

        }

        //全部不可见
        void setAllGone() {
            if (mLoadingViewstubstub != null) {
                mLoadingViewstubstub.setVisibility(View.GONE);
            }
            if (mEndViewstub != null) {
                mEndViewstub.setVisibility(View.GONE);
            }
            if (mNetworkErrorViewstub != null) {
                mNetworkErrorViewstub.setVisibility(View.GONE);
            }
        }

    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder{
        SimpleDraweeView iv_icon;
        TextView tv_name;
        TextView tv_new_msg;
        public HeaderViewHolder(View itemView) {
            super(itemView);
            iv_icon= (SimpleDraweeView) itemView.findViewById(R.id.iv_icon);
            tv_name= (TextView) itemView.findViewById(R.id.tv_name);
            tv_new_msg= (TextView) itemView.findViewById(R.id.tv_new_msg);
        }
    }

    public interface NewMsgListener{
        public void SetTheMsgIcon(TextView tv);
    }

    public void changNewMsgIcon(NewMsgListener listener){
        this.listener=listener;
    }

    public void sendMessages(){
        Intent intent1 = new Intent();
        intent1.setAction("updata");
        mContext.sendBroadcast(intent1);
    }
}
