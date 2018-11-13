package com.hzkc.parent.mina;

import com.hzkc.parent.jsondata.CmdCommon;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 * Created by lenovo on 2017/2/21.
 */

public class MyDataDecoder extends CumulativeProtocolDecoder {
    @Override
    protected boolean doDecode(IoSession ioSession, IoBuffer ioBuffer, ProtocolDecoderOutput protocolDecoderOutput) throws Exception {


        /////////////////////////////////////
//        if (ioBuffer.remaining() < 5) {
//            return false;
//        }
//        ioBuffer.mark();
//        byte[] lenBytes = new byte[5];
//        ioBuffer.get(lenBytes, 0, 5);
//        String lenString = new String(lenBytes, "utf-8");
//        int len = Integer.parseInt(lenString);
//        if (len > ioBuffer.remaining()) {
//            ioBuffer.reset();
//            return false;
//        }
//        if (len == 0) {
//            protocolDecoderOutput.write(CmdCommon.HEARTBEAT_ID);
//        } else {
//
//            byte[] realBytes = new byte[len];
//            ioBuffer.get(realBytes, 0, len);
//            String jsonData = new String(realBytes, "utf-8");
//            if (jsonData != null && jsonData.length() > 0) {
//                protocolDecoderOutput.write(jsonData);
//            }
//        }
//        if (ioBuffer.remaining() > 0) {
//            return true;
//        }
//        return false;
        ////////////////////////////////////

        if (ioBuffer.remaining() < 16) {
            return false;
        }
        ioBuffer.mark();
        byte[] lenBytes = new byte[12];
        ioBuffer.get(lenBytes, 0, 12);
        byte[] lenBytes1 = new byte[8];

        System.arraycopy(lenBytes, 4, lenBytes1, 0, 8);
        String lenString = new String(lenBytes1, "utf-8");

        int len = Integer.parseInt(lenString);
        if (len > ioBuffer.remaining()) {
            ioBuffer.reset();
            return false;
        }
        if (len == 0) {
            protocolDecoderOutput.write(CmdCommon.HEARTBEAT_ID);
        } else {

            byte[] realBytes = new byte[len+4];
            ioBuffer.get(realBytes, 0, len+4);
            String jsonData = new String(realBytes, "utf-8");
            if (jsonData != null && jsonData.length() > 0) {
                protocolDecoderOutput.write(jsonData);

            }
        }
        if (ioBuffer.remaining() > 0) {
            return true;
        }
        return false;
    }
}
