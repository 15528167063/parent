package com.hzkc.parent.mina;

import android.content.Context;
import android.os.Handler;

import com.hzkc.parent.service.MianService;
import com.hzkc.parent.utils.AdddataUtils;
import com.hzkc.parent.utils.LogUtil;
import com.hzkc.parent.utils.MyUtils;


import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.stream.StreamWriteFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public class MinaThread extends Thread {

    private static final String TAG = "MinaThread";


    private IoSession session = null;
    private IoConnector connector = null;

    private Context mContext;

    private Handler myHandler;

    //记录连接的session列表
    //public static List<IoSession> ioSessionsList;

    public MinaThread(Context context, Handler xxHandler) {
        mContext = context;
        myHandler = xxHandler;
    }


    public IoSession getSession() {
        return session;
    }

    public Boolean isConnected() {
        if (session != null) {
            if (session.isConnected()) {
                {
                    return true;
                }
            }
        }
        return false;
    }

    public Boolean sMessage(String sss) {
        if (isConnected()) {
            try {
                sMessage(getSession(), sss.getBytes("utf-8"));
                return true;
            } catch (Exception eee) {
                LogUtil.e(TAG, "sMessage err:" + eee.toString());
            }
        }
        return false;
    }


    public byte[] msg2Bytes(Object message) {
        IoBuffer buffer = (IoBuffer) message;
        ByteBuffer bf = buffer.buf();
        byte[] msgByte = new byte[bf.limit()];
        bf.get(msgByte);
        buffer.clear();
        buffer.free();
        return msgByte;
    }

    public void sMessage(IoSession session, byte[] msg) throws Exception {
//        String str = new String(msg, "UTF-8");
//        String str1 = String.format("%05d", str.getBytes("utf-8").length);
//        str = str1 + str;
//        LogUtil.e(TAG, str);
//        IoBuffer buffers = IoBuffer.allocate(str.getBytes("utf-8").length);
//        buffers.put(str.getBytes(), 0, str.getBytes("utf-8").length);
//        buffers.free();
//        buffers.flip();
//        session.write(buffers);

        String str = new String(msg, "UTF-8");
        String getmidata = AdddataUtils.getmidata(str);
        IoBuffer buffers = IoBuffer.allocate(getmidata.getBytes("utf-8").length);
        buffers.put(getmidata.getBytes(), 0, getmidata.getBytes("utf-8").length);
        buffers.free();
        buffers.flip();
        session.write(buffers);
    }

    public void disConnect() {
        if (session != null) {
            if (session.isConnected()) {
                try {
                    session.closeNow();
                } catch (Exception EEEE) {

                }
            }
            try {
                session = null;
            } catch (Exception eee) {

            }
        }
        if(connector!=null){
            try {
                connector.dispose(true);//彻底释放Session,退出程序时调用不需要重连的可以调用这句话，也就是短连接不需要重连。长连接不要调用这句话，注释掉就OK。
                connector = null;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void connectLoop() {
        LogUtil.e(TAG,"connectLoop");
        while (true) {
            try {
                if (!MyUtils.isNetworkAvailable(mContext)) {
                    Thread.sleep(2 * 1000);
                    continue;
                }
                LogUtil.e(TAG, "try to reconnect server："+Constants.IP);
                connector = new NioSocketConnector();
                // 设置链接超时时间
                connector.setConnectTimeoutMillis(10 * 1000);
                //设置5秒没有读取数据为空闲状态
                connector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 4);
                connector.getSessionConfig().setReadBufferSize(16*1024);
                if (Constants.IsUseMyDecoder) {
                    connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MessageCodecFactory()));
                } else {
                    connector.getFilterChain().addLast("codec", new StreamWriteFilter());
                }
                connector.setHandler(new MinaClientHandler(mContext, myHandler));

                connector.setDefaultRemoteAddress(new InetSocketAddress(Constants.IP, Constants.PORT));
                // 监听客户端是否断线
                connector.addListener(new IoListener() {

                    @Override
                    public void sessionClosed(IoSession arg0) throws Exception {
                        super.sessionClosed(arg0);
                        LogUtil.e(TAG, "sessionClosed");
                    }
                    @Override
                    public void sessionDestroyed(IoSession arg0) throws Exception {
                        // TODO Auto-generated method stub
                        super.sessionDestroyed(arg0);
                        MianService.connectTime=0;

                    }
                });
                ConnectFuture future = connector.connect();
                future.awaitUninterruptibly();// 等待连接创建完成
                session = future.getSession();// 获得session
                //判断是否连接服务器成功
                if (session != null && session.isConnected()) {
                    LogUtil.e(TAG, "connected  server");
                    LogUtil.e(TAG, "session.isConnected 等待连接断开---->");
                    if(listener!=null){    //判断是不是链接成功
                        listener.loopSuccess();
                    }
                    session.getCloseFuture().awaitUninterruptibly();// 等待连接断开
                    LogUtil.e(TAG, "awaitUninterruptibly 等待连接断开成功---->");
                }
            } catch (Exception e) {
                System.out.println("客户端链接异常...");
            }
            disConnect();
            try {
                Thread.sleep(4 * 1000);
            } catch (Exception eeee) {

            }
        }
    }


    @Override
    public void run() {
        super.run();
        if(true){
            connectLoop();
            return;
        }

//        connector = new NioSocketConnector();
//        // 设置链接超时时间
//        connector.setConnectTimeoutMillis(10 * 1000);
//        //设置5秒没有读取数据为空闲状态
//        connector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 5);
//        connector.getSessionConfig().setReadBufferSize(65536);
//
//        //connector.
//
//        if (Constants.IsUseMyDecoder) {
//
//            connector.getFilterChain().addLast("codec",
//                    new ProtocolCodecFilter(new MessageCodecFactory()));
//        } else {
//            connector.getFilterChain().addLast("codec",
//                    new StreamWriteFilter());
//        }
//        connector.setHandler(new MinaClientHandler(mContext, myHandler));
//        connector.setDefaultRemoteAddress(new InetSocketAddress(Constants.IP, Constants.PORT));
//         //监听客户端是否断线
//        connector.addListener(new IoListener() {
//            @Override
//            public void sessionDestroyed(IoSession arg0) throws Exception {
//                // TODO Auto-generated method stub
//                super.sessionDestroyed(arg0);
//                LogUtil.e("sessionDestroyed","size:"+ioSessionsList.size());
//                ioSessionsList.remove(arg0);
//                if(ioSessionsList.size()>=1){
//                    return;
//                }
//                try {
//                    arg0.close(true);
//                } catch (Exception eeee) {
//
//                }
//                LogUtil.e(TAG, "sessionDestroyed");
//                int failCount = 0;
//                while (true) {
//                    try {
//                        LogUtil.e(TAG, "try to reconnect server");
//                        if (MyUtils.isNetworkAvailable(mContext)) {
//                            Thread.sleep(2 * 1000);
//                        } else {
//                            Thread.sleep(5 * 1000);
//                            LogUtil.e(TAG, "Network is not available");
//                            continue;
//                        }
//                        //Thread.sleep(5000);
//                        ConnectFuture future = connector.connect();
//                        future.awaitUninterruptibly();// 等待连接创建完成
//                        session = future.getSession();// 获得session
//                        if (session != null && session.isConnected()) {
//                            //session.write("start");
//                            //LogUtil.e(TAG, "断线重连中---->1111111");
//                            //session.getCloseFuture().awaitUninterruptibly();// 等待连接断开
//                            //LogUtil.e(TAG, "断线重连中---->2222222");
//                            break;
//                        } else {
//                            LogUtil.e(TAG, "断线重连失败---->" + failCount + "次");
//                            //System.out.println("断线重连失败---->" + failCount + "次");
//                        }
//                        Thread.sleep(2 * 1000);
//                    } catch (Exception e) {
//                        // TODO: handle exception
//                        LogUtil.e(TAG, "session detroyed error1111111:" + e.toString());
//                    }
//                }
//
//
//            }
//        });
//        //开始连接
//        Boolean isOk = false;
//        while (true) {
//            try {
//                LogUtil.e(TAG, "try to reconnect server");
//                isOk = false;
//                if (MyUtils.isNetworkAvailable(mContext)) {
//                    Thread.sleep(2 * 1000);
//                } else {
//                    Thread.sleep(5 * 1000);
//                    LogUtil.e(TAG, "Network is not available");
//                    continue;
//                }
//                ConnectFuture future = connector.connect();
//                future.awaitUninterruptibly();// 等待连接创建完成
//                session = future.getSession();// 获得session
//
//                //判断是否连接服务器成功
//                if (session != null && session.isConnected()) {
//                    LogUtil.e(TAG, "connected  server");
//                    isOk = true;
//                    //break;
//                } else {
//                    System.out.println("写数据失败");
//                }
//
//                System.out.println(11);
//            } catch (Exception e) {
//                isOk = false;
//                System.out.println("客户端链接异常...");
//            }
//            System.out.println(118);
//            if (session != null && session.isConnected()) {
//
//                session.getCloseFuture().awaitUninterruptibly();// 等待连接断开
//                System.out.println("客户端断开111111...");
//                // connector.dispose();//彻底释放Session,退出程序时调用不需要重连的可以调用这句话，也就是短连接不需要重连。长连接不要调用这句话，注释掉就OK。
//            }
//            if (isOk) {
//                break;
//            }
//            try {
//                Thread.sleep(4 * 1000);
//            } catch (Exception eeee) {
//
//            }
//
//        }
    }
    public interface  LoopSuccess{
       public  void loopSuccess();
    };
    private LoopSuccess listener;
    public void setLoopSuccess(LoopSuccess listener){
       this.listener=listener;
    }

}