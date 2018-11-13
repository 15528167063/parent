package com.hzkc.parent.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.hzkc.parent.mina.Constants;
import com.hzkc.parent.service.MianService;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by lenovo on 2017/4/12.
 */

public class SocketThread extends Thread {

    public static final String TAG = SocketThread.class.getSimpleName();
    Socket socket = null;
    InputStream inputStream = null;
    OutputStream outputStream = null;
    Context mContext = null;

    private Boolean isConnected = false;

    private Handler myHandler;

    public static long t1;

    public SocketThread(Context context, Handler handler) {
        mContext = context;
        myHandler = handler;
    }

    Timer timer = new Timer();
    TimerTask timerTask = new TimerTask() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (socket != null && outputStream != null && inputStream != null && isConnected) {
                //发送心跳命令
                Boolean bb = sendHearBeatToServer();
                Log.e(TAG, "向服务器发送心跳命令:" + bb);
                //d("sendHearBeatToServer:" + bb);
                if (!bb) {
                    // disConnect();
                }
            }
        }
    };

    public synchronized Boolean sendHearBeatToServer() {
        if (socket == null || inputStream == null || outputStream == null) {
            return false;
        }
        if(MianService.connectTime==0){
            return false;
        }
        t1=MianService.connectTime+System.currentTimeMillis();
        //发送心跳包给服务器
        byte[] data = new byte[0];
        try {
            String str = new String(("t"+t1+";").getBytes("utf-8"), "UTF-8");
            data = str.getBytes("UTF-8");
            LogUtil.e("发送心跳包给服务器", "sessionIdle: "+str);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (data != null) {
            return sendDataHeartBeat(data);
        }
        return false;
    }


    /**
     * 发送数据
     *
     * @param msg
     * @return
     */
    private Boolean sendData(String msg) {
        try {
            if (socket == null || inputStream == null || outputStream == null) {
                return false;
            }
            String str;
            String str1 = String.format("%05d", msg.getBytes("utf-8").length);
            str = str1 + msg;
            LogUtil.e(TAG, "sendData=>" + str);
            byte[] datas = str.getBytes("utf-8");
            if (datas != null) {
                outputStream.write(datas);
                outputStream.flush();
                return true;
            }
        } catch (Exception eeee) {
            LogUtil.e(TAG, "eee:" + eeee.toString());
        }
        return false;
    }


    /**
     * 发送消息 字符形式
     *
     * @param sss
     * @return
     */
    public Boolean sMessage(String sss) {
        //String str = new String(msg, "UTF-8");
        //MyLog.e(TAG, "sendMessage:" + sss);
        final String sendmsgString = sss;
        myHandler.post(new Runnable() {
            @Override
            public void run() {
                Boolean bb = sendData(sendmsgString);
                LogUtil.e(TAG, "sendData:" + bb);
            }
        });
        //return sendData(sss);
        return true;
    }

    /**
     * 发送消息 byte数组
     *
     * @param msg
     * @return
     */
    // public Boolean sMessage(byte[] msg) {
    //     return sendData(msg);
    // }

    /**
     * 发送数据
     *
     * @param
     * @return
     */
    public Boolean sendDataHeartBeat(byte[] datas) {
        try {
            if (socket == null || inputStream == null || outputStream == null) {
                return false;
            }
            // byte[] datas = msg.getBytes("utf-8");
            if (datas != null) {
                outputStream.write(datas);
                outputStream.flush();
                return true;
            }
        } catch (Exception eeee) {
            //disConnect();
        }
        return false;
    }

    /**
     * 发送数据
     *
     * @param
     * @return
     */
    private Boolean sendData(byte[] datas) {
        try {
            if (socket == null || inputStream == null || outputStream == null) {
                return false;
            }
            // byte[] datas = msg.getBytes("utf-8");
            if (datas != null) {
                outputStream.write(datas);
                outputStream.flush();
                return true;
            }
        } catch (Exception eeee) {
            //disConnect();
        }
        return false;
    }

    public OutputStream getsession() {
        return outputStream;
    }

    /**
     * 读取数据长度
     *
     * @return
     * @throws Exception
     */
    public int readDataLen() throws Exception {

        LogUtil.e(TAG, "readDataLen");
        byte[] lenBytes = new byte[5];
        int tototalLen = 0;
        int index = 0;
        int dataLen = 0;
        Boolean isValid = false;
        int needReadLen = 5;
        while (true) {
            //byte[] tempBytes = new byte[5];
            LogUtil.e(TAG, "read len start");
            int len = inputStream.read(lenBytes, index, needReadLen);
            LogUtil.e(TAG, "read len:" + len);
            if (len < 0) {
                if (tototalLen == 5) {
                    // isValid = true;
                    // break;
                }
                break;
            }
            if (len > 0) {
                index = tototalLen;
                if (index > 0) {
                    //  index = tototalLen - 1;
                }
                //System.arraycopy(tempBytes, 0, lenBytes, index, len);
                tototalLen += len;
                needReadLen = 5 - tototalLen;
            }
            if (tototalLen == 5) {
                isValid = true;
                break;
            }
        }
        if (isValid) {
            String lenString = new String(lenBytes, 0, 5, "utf-8");
            try {
                dataLen = Integer.parseInt(lenString);
            } catch (Exception eeee) {
                dataLen = 0;
            }
        } else {
            lenBytes = null;
        }
        return dataLen;
    }

    /**
     * 读取json数据部分
     *
     * @param readLen
     * @return
     * @throws Exception
     */
    public byte[] readDataJson1(int readLen) throws Exception {
        LogUtil.e(TAG, "readDataJson:" + readLen);
        byte[] dataBytes = new byte[readLen];
        int tototalLen = 0;
        int index = 0;
        Boolean isValid = false;
        int needReadLen = readLen;
        while (true) {
            //byte[] tempBytes = new byte[1 * 1024];
            int len = inputStream.read(dataBytes, index, needReadLen);
            if (len < 0) {
                if (tototalLen == readLen) {
                    // isValid = true;
                    // break;
                }
                break;
            }
            if (len > 0) {
                index = tototalLen;
                if (index > 0) {
                    // index = index - 1;
                }
                //System.arraycopy(tempBytes, 0, dataBytes, index, len);
                tototalLen += len;
                needReadLen = readLen - tototalLen;
            }
            if (tototalLen == readLen) {
                isValid = true;
                break;
            }
        }
        if (isValid) {
            LogUtil.e(TAG, "readDataJsonlength:" + dataBytes.length);
            return dataBytes;
        }
        dataBytes = null;
        return null;

    }

    /**
     * 读取json数据部分
     *
     * @param readLen
     * @return
     * @throws Exception
     */
    public byte[] readDataJson(int readLen) throws Exception {
        LogUtil.e(TAG, "readDataJson:" + readLen);
        byte[] dataBytes = new byte[readLen];
        int tototalLen = 0;
        int index = 0;
        Boolean isValid = false;
        int needReadLen = readLen;
        while (true) {
            byte[] tempBytes = new byte[1 * 1024];
            int mylen = needReadLen;
            if (needReadLen > 1 * 1024) {
                mylen = 1 * 1024;
            }
            int len = inputStream.read(tempBytes, 0, mylen);
            if (len < 0) {
                if (tototalLen == readLen) {
                    // isValid = true;
                    // break;
                }
                break;
            }
            if (len > 0) {
                index = tototalLen;
                if (index > 0) {
                    // index = index - 1;
                }
                System.arraycopy(tempBytes, 0, dataBytes, index, len);
                tototalLen += len;
                needReadLen = readLen - tototalLen;
            }
            if (tototalLen == readLen) {
                isValid = true;
                break;
            }
        }
        if (isValid) {
            return dataBytes;
        }
        dataBytes = null;

        return null;

    }


    /**
     *
     */
    public void disConnect() {
        isConnected = false;
        if (socket != null) {
            try {
                socket.shutdownInput();
                socket.shutdownOutput();
                socket.close();
            } catch (Exception eeeeee) {

            }
            socket = null;
        }
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (Exception eeeeee) {

            }
            inputStream = null;
        }
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (Exception eeeeee) {

            }
            outputStream = null;
        }

    }


    /**
     * 开启socket监听
     * @return
     */
    public boolean startSocket() {
        LogUtil.e(TAG, "startSocket");
        timer.schedule(timerTask, 1000, 2000);
        isConnected = false;
        long lastStartTime = System.currentTimeMillis();
        while (true) {
            try {
                Boolean isNetworkAvailable = MyUtils.isNetworkAvailable(mContext);
                LogUtil.e(TAG, "new socket before isNetworkAvailable:" + isNetworkAvailable);
                if (!isNetworkAvailable) {
                    disConnect();
                    Thread.sleep(2000);
                    continue;
                }
                if (socket == null || inputStream == null || outputStream == null) {
                    LogUtil.e(TAG, "new socket");
                    lastStartTime = System.currentTimeMillis();
                    socket = new Socket();
                    socket.setKeepAlive(true);
                    socket.setReuseAddress(true);
                    InetSocketAddress socketAddress = new InetSocketAddress(Constants.IP, Constants.PORT);
                    socket.connect(socketAddress, 10000);
                    inputStream = socket.getInputStream();
                    outputStream = socket.getOutputStream();
                    //连接打开 open
                    t1 = 0;
                    Message obtain = Message.obtain();
                    obtain.what = 102;//初始化数据
                    myHandler.sendMessage(obtain);
                }
                //MyUtils.saveMinaConnectState(mContext, true);
                isConnected = true;

                int dataLen = readDataLen();
                LogUtil.e(TAG, "readDataLen:" + dataLen);
                if (dataLen > 0) {
                    byte[] data = readDataJson(dataLen);
                    if (data != null) {
                        //接受数据
                        String jsonData = new String(data, "utf-8").trim();
                        LogUtil.e(TAG, "recvData:" + jsonData);
                        Message msghandler = Message.obtain();
                        msghandler.what = 101;
                        msghandler.obj = jsonData;
                        myHandler.sendMessage(msghandler);
                    }
                } else {
                    disConnect();
                }
            } catch (Exception eeee) {

                long subTime = System.currentTimeMillis() - lastStartTime;
                LogUtil.e(TAG, "after time disconnect:" + subTime / 1000 + "秒");
                //MyUtils.saveMinaConnectState(mContext, false);
                isConnected = false;
                LogUtil.e(TAG, "exception=>" + eeee.toString());
                disConnect();
            }
            if (false) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Boolean isConnected() {
        if (socket == null || inputStream == null || outputStream == null) {
            return false;
        }
        return true;
    }

    @Override
    public void run() {
        super.run();
        startSocket();
    }
}
