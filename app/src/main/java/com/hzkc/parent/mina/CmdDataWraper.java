package com.hzkc.parent.mina;

import java.io.UnsupportedEncodingException;


/**
 * Created by lenovo-s on 2016/11/23.
 */

public class CmdDataWraper {

    public static byte[] getHeartBeatData() throws UnsupportedEncodingException {
        //String childUUID = "";//UTDevice.getUtdid(mContext);
        //cmdJson.setSRC(CmdCommon.SRC_KID );
        //String parentUUID = ShareUtils.getString(context, ShareUtils.SHARE_FUNCTION, ShareUtils.SHARE_FUNCTION_PARENT_UUID, "");
        //cmdJson.setParentUUID(parentUUID);
        //cmdJson.setChildUUID(childUUID);
        String str = new String(("t"+11+";").getBytes("utf-8"), "UTF-8");
        return str.getBytes("UTF-8");
    }
}
