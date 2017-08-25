package com.sdl.icbc.controller;

import com.sdl.icbc.domain.ICBCUrl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 工行收款管家中间件
 * Created by majingyuan on 2017/8/22.
 */
@RestController
@RequestMapping("/icbc")
@Slf4j
public class ICBCController {

    @PostMapping("/getDetail")
    public Map<String,Object> getICBCDetail(ICBCUrl icbcUrl){
        Map<String, Object> result = new HashMap<>();
        log.info("====================================================");
        log.info("-------------------收付款记录查询---------------------");
        Date today = new Date();
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyyMMdd");
        String sendDay = dayFormat.format(today);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmssSSS");
        String sendTime = timeFormat.format(today);

        String url = icbcUrl.getAction() + "userID=" + icbcUrl.getUserId() + "&PackageID=" + icbcUrl.getPackageId() + "&SendTime=" +sendDay + sendTime;

        String param = null;
        try {
            param = "Version=" + URLEncoder.encode(icbcUrl.getVersion(), "GBK") +
                    "&TransCode=" + URLEncoder.encode(icbcUrl.getTransCode(), "GBK") +
                    "&BankCode=" + URLEncoder.encode(icbcUrl.getBankCode(), "GBK") +
                    "&GroupCIS=" + URLEncoder.encode(icbcUrl.getCis(), "GBK") +
                    "&PackageID=" + URLEncoder.encode(icbcUrl.getPackageId(), "GBK") +
                    "&Cert=" +
                    "&ID=" + URLEncoder.encode(icbcUrl.getUserId(), "GBK") +
                    "&reqData=" + URLEncoder.encode("<?xml version=\"1.0\" encoding=\"GBK\"?><CMS><eb><pub>" +
                    "<TransCode>" + icbcUrl.getTransCode() + "</TransCode>" +
                    "<CIS>" + icbcUrl.getCis() + "</CIS>" +
                    "<BankCode>" + icbcUrl.getBankCode() + "</BankCode>" +
                    "<ID>" + icbcUrl.getUserId() + "</ID>" +
                    "<TranDate>" + sendDay + "</TranDate>" +
                    "<TranTime>" + sendTime + "</TranTime>" +
                    "<fSeqno>" + icbcUrl.getPackageId() + "</fSeqno>" +
                    "</pub><in>" +
                    "<AcctNo>" + icbcUrl.getAcctNo() + "</AcctNo>" +
                    "<StartDate>" + icbcUrl.getStartDate() + "</StartDate>" +
                    "<EndDate>" + icbcUrl.getEndDate() + "</EndDate>" +
                    "<NextTag>" + icbcUrl.getNextTag() + "</NextTag>" +
                    "<AcctSeq>" + icbcUrl.getAcctSeq() + "</AcctSeq>" +
                    "<CardNo>" + icbcUrl.getCardNo() + "</CardNo>" +
                    "</in></eb></CMS>", "GBK");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        log.info("actionUrl: {}", url);
        log.info("actionParam: {}", param);
        String sr = sendPost(url, param);
        result.put("resMsg", sr);
        int isError = sr.indexOf("errorCode=");
        if (isError < 0){
            sr = sr.replace("reqData=","");
            result.put("resFlag", "0");
            result.put("resXml", getstrFromBASE64(sr));
            log.info("resXml: {}", getstrFromBASE64(sr));
        }else {
            result.put("resFlag", "1");
            result.put("resXml", sr);
            log.info("resMsg:\n {}", sr);
        }
        log.info("---------------------查询结束------------------------");
        log.info("====================================================");
        return result;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        String requestString = param;

        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            HttpURLConnection httpConn = (HttpURLConnection) realUrl.openConnection();
            // //设置连接属性
            httpConn.setDoOutput(true);// 使用 URL 连接进行输出
            httpConn.setDoInput(true);// 使用 URL 连接进行输入
            httpConn.setUseCaches(false);// 忽略缓存
            httpConn.setRequestMethod("POST");// 设置URL请求方法

            // 设置请求属性
            // 获得数据字节数据，请求数据流的编码，必须和下面服务器端处理请求流的编码一致

//            String requestData = URLEncoder.encode(requestString, "GBK");
            httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=GBK");
            httpConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接

            // 建立输出流，并写入数据
            OutputStream outputStream = httpConn.getOutputStream();
            outputStream.write(requestString.getBytes());
            outputStream.flush();
            // 获得响应状态
            int responseCode = httpConn.getResponseCode();

            if (HttpURLConnection.HTTP_OK == responseCode) {// 连接成功
                // 当正确响应时处理数据
                StringBuffer sb = new StringBuffer();
                String readLine;
                BufferedReader responseReader;
                // 处理响应流，必须与服务器响应流输出的编码一致
                responseReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "GBK"));
                while ((readLine = responseReader.readLine()) != null) {
                    sb.append(readLine).append("\n");
                }
                responseReader.close();
                //tv.setText(sb.toString());
                result = sb.toString();
                //System.out.println(sb.toString());
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }



        return result;
    }

    /**
     * base64解码
     * @param s:需要解码的数据
     * @return 解码后的数据
     */
    public static String getstrFromBASE64(String s) {
        if (s == null)
            return null;
        sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
        try {
            byte[] b = decoder.decodeBuffer(s);
            return new String(b, "GBK");
        } catch (Exception e) {
            return null;
        }
    }
}
