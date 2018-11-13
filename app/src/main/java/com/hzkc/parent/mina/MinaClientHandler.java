package com.hzkc.parent.mina;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import com.hzkc.parent.jsondata.CmdCommon;
import com.hzkc.parent.service.MianService;
import com.hzkc.parent.utils.AdddataUtils;
import com.hzkc.parent.utils.LogUtil;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public class MinaClientHandler extends IoHandlerAdapter {

    private static final String TAG = "MinaClientHandler";
    private Context mContext;
    private Handler myHandler;
    //请求所有孩子的上线下线状态
    private boolean heartFlag = true;
    private String lastmsg = "";
    private String currentmsg = "";
    private int currentlenth = 0;
    private long startTime;

    public static long t1;
    private long lastMyserviceTime;

    public MinaClientHandler(Context context, Handler myHandler) {
        mContext = context;
        this.myHandler = myHandler;
    }

    /**
     * 服务端与客户端连接打开
     */
    @Override
    public void sessionOpened(IoSession session) throws Exception {
        super.sessionOpened(session);

        //MinaThread.ioSessionsList.add(session);
        //LogUtil.e("sessionOpened","size:"+MinaThread.ioSessionsList.size());
        //        if(MinaThread.ioSessionsList.size()>1){
        //            session.close(true);
        //            return;
        //        }
        int port = ((InetSocketAddress) session.getLocalAddress()).getPort();
        LogUtil.e(TAG, "port... sessionOpened:"+port);
        LogUtil.i(TAG, "服务端与客户端连接打开... ");
        lastmsg="";
        currentmsg="";
        currentlenth=0;
        lastMyserviceTime= SystemClock.elapsedRealtime();
        t1 = 0;
        MianService.connectTime=0;
        Message obtain = Message.obtain();
        obtain.what = 102;//初始化数据
        myHandler.sendMessage(obtain);
        startTime = System.currentTimeMillis();
    }

    /**
     * 服务端进入空闲状态
     */
    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        super.sessionIdle(session, status);
        int port = ((InetSocketAddress) session.getLocalAddress()).getPort();
//        LogUtil.e(TAG, "。。。port... sessionIdle:"+port);
        LogUtil.i(TAG, "服务端进入空闲状态... ");
        if(MianService.connectTime==0){
            return;
        }
        t1=MianService.connectTime+System.currentTimeMillis();
        //发送心跳包给服务器
        if (heartFlag){
            //String str = new String(CmdCommon.HEARTBEAT_ID.getBytes("utf-8"), "UTF-8");
//            if(SystemClock.elapsedRealtime()-lastMyserviceTime>60*1000){
//                MianService.minaThread.disConnect();
//                return;
//            }
            String str = new String(("t"+t1+";").getBytes("utf-8"), "UTF-8");
            IoBuffer buffers = IoBuffer.allocate(str.getBytes().length);
            buffers.put(str.getBytes(), 0, str.getBytes().length);
            buffers.free();
            buffers.flip();
            session.write(buffers);
//            Log.e(TAG, "sessionIdle: "+str);
        }
    }

    /**
     * 服务端接收到的数据3000   30000   300000000
     */
    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        super.messageReceived(session, message);

        lastMyserviceTime= SystemClock.elapsedRealtime();

//        LogUtil.e(TAG, "port... sessionIdle:"+message.toString());
        heartFlag=true;
        String msg1 = "";
        String msg =" ";
        //检测获取数据的完整性
        if(Constants.IsUseMyDecoder){
            msg1=(String)message;
            msg = AdddataUtils.getdata(msg1);
            LogUtil.e("服务端接收到的数据解密:","msg解密:"+msg);
            if (msg.equals(CmdCommon.HEARTBEAT_ID)) {
                LogUtil.e(TAG, "msg:"+msg);
                return;
            }
        }else{
            String tempMsg = new String(msg2Bytes(message), "UTF-8");
            if (tempMsg.equals(CmdCommon.HEARTBEAT_ID)) {
                LogUtil.e(TAG, "msg:"+tempMsg);
                return;
            }
            //lastmsg="";
            String rec = lastmsg + tempMsg;
            LogUtil.e(TAG, "messageReceived==>" + tempMsg);
            if (rec.length() < 5) {

                lastmsg = rec;
                return;
            }
            String head = rec.substring(0, 5);
            String tail = rec.substring(5, rec.length());
            try {
                currentlenth = Integer.parseInt(head);
                currentmsg = tail;
                lastmsg = "";
            } catch (NumberFormatException e) {
                lastmsg = ""+rec;
                return;
            }
            int currentmsglen = currentmsg.getBytes().length;
            if (currentlenth < currentmsglen) //
            {
                currentmsg = tail.substring(0, currentlenth);
                lastmsg = tail.substring(currentlenth, currentmsglen);
            } else if (currentlenth > currentmsglen)//
            {
                lastmsg = rec;
                return;
            }
            if (currentmsglen > 2) {
                msg = currentmsg;
            } else {
                msg = currentmsg;
            }
        }
        //lastmsg = "";
        Message obtain = Message.obtain();
        obtain.what = 101;
        obtain.obj = msg;
        myHandler.sendMessage(obtain);
    }
    /**
     * 服务端发送异常
     */
    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        super.exceptionCaught(session, cause);
        LogUtil.e(TAG, "服务端发送异常... "+cause.toString());
    }
    /**
     * 服务端发送信息成功
     */
    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        super.messageSent(session, message);
        int port = ((InetSocketAddress) session.getLocalAddress()).getPort();
//        LogUtil.e(TAG, "port... messageSent:"+port);
        LogUtil.i(TAG, "客户端发送信息成功... ");
    }

    /**
     * msg转成字节数组
     */
    public byte[] msg2Bytes(Object message) {
        IoBuffer buffer = (IoBuffer) message;
        ByteBuffer bf = buffer.buf();
        byte[] msgByte = new byte[bf.limit()];
        bf.get(msgByte);
        buffer.clear();
        buffer.free();
        return msgByte;
    }
    /**
     * String转成IoBuffer
     */
    public static IoBuffer stringToIoBuffer(String str) {

        byte bt[] = str.getBytes();
        IoBuffer ioBuffer = IoBuffer.allocate(bt.length);
        ioBuffer.put(bt, 0, bt.length);
        ioBuffer.flip();
        return ioBuffer;
    }

    /**
     * 向服务端发送数据
     */
    public void sMessage(IoSession session, byte[] msg) throws Exception {
        String str = new String(msg, "UTF-8");
        String str1 = String.format("%05d", str.getBytes().length);
        str = str1 + str;
        IoBuffer buffers = IoBuffer.allocate(str.getBytes().length);
        buffers.put(str.getBytes(), 0, str.getBytes().length);
        buffers.free();
        buffers.flip();
        session.write(buffers);
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        super.sessionClosed(session);
//        try {
//            session.close(true);
//        }catch (Exception e){
//
//        }
        long connetTime = System.currentTimeMillis() - startTime;
        LogUtil.e("sessionClosed","connetTime:"+connetTime);
    }
}
