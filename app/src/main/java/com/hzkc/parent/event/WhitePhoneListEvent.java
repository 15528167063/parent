package com.hzkc.parent.event;

import com.hzkc.parent.jsondata.PhoneDatas;

import java.util.List;

/**
 * Created by lenovo-s on 2016/11/7.
 */

public class WhitePhoneListEvent {

    public List<PhoneDatas> appDataList;

    public WhitePhoneListEvent(List<PhoneDatas> appDataList) {
        this.appDataList = appDataList;
    }
}
