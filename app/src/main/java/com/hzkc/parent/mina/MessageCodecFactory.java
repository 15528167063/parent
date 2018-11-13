package com.hzkc.parent.mina;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * Created by lenovo on 2017/2/21.
 */

public class MessageCodecFactory implements ProtocolCodecFactory {

    private final MyDataDecoder decoder;

    private final MyDataEncoder encoder;

    public MessageCodecFactory() {

        decoder = new MyDataDecoder();
        encoder = new MyDataEncoder();

    }

    @Override
    public ProtocolEncoder getEncoder(IoSession ioSession) throws Exception {
        return encoder;
    }

    @Override
    public ProtocolDecoder getDecoder(IoSession ioSession) throws Exception {
        return decoder;
    }
}
