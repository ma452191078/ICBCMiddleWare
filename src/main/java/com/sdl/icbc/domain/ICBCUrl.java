package com.sdl.icbc.domain;

import lombok.Data;

/**
 * 工行url参数
 * Created by majingyuan on 2017/8/22.
 */
@Data
public class ICBCUrl {

//    请求序列号ID
    private String packageId;
//    证书ID
    private String userId;
//    请求业务编码
    private String transCode;
//    集团CIS
    private String cis;
//    所属银行编号，工行默认102
    private String bankCode;
//    请求action
    private String action;
//    结算账户账号
    private String acctNo;
//    起始日期
    private String startDate;
//    截止日期
    private String endDate;
//    查询分页标识
    private String nextTag;
//    结算账户账号序号
    private String acctSeq;
//    管家卡卡号
    private String cardNo;

    private String version;


}