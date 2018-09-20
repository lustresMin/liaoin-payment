package com.liaoin.payment.core;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.*;
import com.alipay.api.request.*;
import com.alipay.api.response.*;

import java.math.BigDecimal;

/**
 * 支付宝支付工具类
 *
 * @author 张权立
 * @date 2018/06/07
 */
public class AlipayUtils {

    /**
     * app支付
     *
     * @param serverUrl       支付宝网关
     * @param appId           应用编号
     * @param privateKey      应用私钥
     * @param alipayPublicKey 支付宝公钥
     * @param notifyUrl       回调地址
     * @param subject         交易标题
     * @param outTradeNo      商户网站唯一订单号
     * @param totalAmount     订单总金额，单位为元，精确到小数点后两位
     * @return 返回支付订单信息的字符串
     * @throws AlipayApiException 支付出错抛出异常
     */
    public static String payByApp(String serverUrl, String appId, String privateKey, String alipayPublicKey, String notifyUrl, String subject, String outTradeNo, String totalAmount) throws AlipayApiException {
        //实例化客户端
        AlipayClient alipayClient = new DefaultAlipayClient(serverUrl, appId, privateKey, "json", "utf-8", alipayPublicKey, "RSA2");
        //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setSubject(subject);
        model.setOutTradeNo(outTradeNo);
        model.setTimeoutExpress("30m");
        model.setTotalAmount(totalAmount);
        model.setProductCode("QUICK_MSECURITY_PAY");
        request.setBizModel(model);
        request.setNotifyUrl(notifyUrl);
        AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
        return response.getBody();
    }

    /**
     * @param appid           应用编号
     * @param appPrivateKey   应用私钥
     * @param alipayPublicKey 支付宝公钥
     * @param barcode         用戶手機app的條形碼
     * @param outTradeNo      商户网站唯一订单号
     * @param subject         交易标题
     * @param totalAmount     订单总金额，单位为元，精确到小数点后两位
     * @return 返回生成的订单号
     */
    public static String payByBarCode(String appid,
                                      String appPrivateKey,
                                      String alipayPublicKey,
                                      String barcode,
                                      String outTradeNo,
                                      String subject,
                                      BigDecimal totalAmount) {
        try {
            //获得初始化的AlipayClient
            AlipayClient alipayClient = new DefaultAlipayClient(
                    "https://openapi.alipay.com/gateway.do",
                    appid,
                    appPrivateKey,
                    "json",
                    "UTF-8",
                    alipayPublicKey,
                    "RSA2");
            //创建API对应的request类
            AlipayTradePayRequest request = new AlipayTradePayRequest();
            AlipayTradePayModel alipayTradePayModel = new AlipayTradePayModel();
            //商户订单号，需要保证不重复
            alipayTradePayModel.setOutTradeNo(outTradeNo);
            //当面支付固定传入bar_code
            alipayTradePayModel.setScene("bar_code");
            //用户付款码
            alipayTradePayModel.setAuthCode(barcode);
            //订单标题
            alipayTradePayModel.setSubject(subject);
            //金額
            alipayTradePayModel.setTotalAmount(totalAmount.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            request.setBizModel(alipayTradePayModel);
            //通过alipayClient调用API，获得对应的response类
            AlipayTradePayResponse alipayTradePayResponse = alipayClient.execute(request);
            String code = alipayTradePayResponse.getCode();
            String msg = alipayTradePayResponse.getMsg();
            String tradeNo = alipayTradePayResponse.getTradeNo();
            // 校验参数失败
            if (!"10003".equals(code) && !"10000".equals(code)) {
                //String subCode = alipayTradePayResponse.getSubCode();
                String subMsg = alipayTradePayResponse.getSubMsg();
                //"code:" + code + "msg:" + msg + "/r/n" +"sub_code:"+subCode+"sub_msg:"+
                throw new PayFailException(subMsg);
            }
            //等待用户付款 轮询查询订单 30秒后 未支付成功 直接取消本单
            if ("10003".equals(code)) {
                return checkOrder(appid, appPrivateKey, alipayPublicKey, outTradeNo, tradeNo);
            }
            // 支付失败或者正在支付
            return tradeNo;
        } catch (AlipayApiException e) {
            e.printStackTrace();
            throw new PayFailException("系统异常");
        }

    }

    /**
     * @param appid           应用编号
     * @param appPrivateKey   应用私钥
     * @param alipayPublicKey 支付宝公钥
     * @param outTradeNo      商户网站唯一订单号
     * @param tradeNo         支付宝单号
     * @return
     */
    private static String checkOrder(String appid,
                                     String appPrivateKey,
                                     String alipayPublicKey,
                                     String outTradeNo,
                                     String tradeNo) {
        // 封装支付配置
        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(
                "https://openapi.alipay.com/gateway.do",
                appid,
                appPrivateKey,
                "json",
                "UTF-8",
                alipayPublicKey,
                "RSA2");
        //创建API对应的request类
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        AlipayTradeQueryModel alipayTradeQueryModel = new AlipayTradeQueryModel();
        alipayTradeQueryModel.setOutTradeNo(outTradeNo);
        alipayTradeQueryModel.setTradeNo(tradeNo);
        request.setBizModel(alipayTradeQueryModel);
        //通过alipayClient调用API，获得对应的response类
        // 循环查询订单支付状态
        for (int i = 0; i < 6; i++) {
            // 等待5秒后调用 查询订单API
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                AlipayTradeQueryResponse alipayTradePayResponse = alipayClient.execute(request);
                String code = alipayTradePayResponse.getCode();
                String msg = alipayTradePayResponse.getMsg();
                String tradeStatus = alipayTradePayResponse.getTradeStatus();
                //等待用户付款 轮询查询订单 30秒后 未支付成功 直接取消本单
                if (!("10000".equals(code))) {
                    throw new PayFailException(msg);
                }
                if (!("TRADE_SUCCESS".equals(tradeStatus))) {
                    continue;
                }
                // 交易成功，返回微信订单号
                return alipayTradePayResponse.getTradeNo();
            } catch (AlipayApiException e) {
                throw new PayFailException("查询订单失败");
            }

        }

        // 支付超时，订单撤销
        AlipayClient alipayClientRollBack = new DefaultAlipayClient(
                "https://openapi.alipay.com/gateway.do",
                appid,
                appPrivateKey,
                "json",
                "GBK",
                alipayPublicKey,
                "RSA2");
        //创建API对应的request类
        AlipayTradeCancelRequest requestRollBack = new AlipayTradeCancelRequest();
        AlipayTradeCancelModel alipayTradeCancelModel = new AlipayTradeCancelModel();
        alipayTradeCancelModel.setOutTradeNo(outTradeNo);
        alipayTradeCancelModel.setTradeNo(tradeNo);
        requestRollBack.setBizModel(alipayTradeCancelModel);
        try {
            AlipayTradeCancelResponse alipayTradePayResponseRollBack = alipayClientRollBack.execute(requestRollBack);
            String codeRollBack = alipayTradePayResponseRollBack.getCode();
            String msgRollBack = alipayTradePayResponseRollBack.getMsg();
            if (!("10000".equals(codeRollBack) && "Success".equals(msgRollBack))) {
                throw new PayFailException("撤销订单失败");
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
            throw new PayFailException("撤销订单失败");
        }
        throw new PayFailException("支付超时，请重新提交订单");
    }

    /**
     * @param appid
     * @param appPrivateKey   私钥
     * @param alipayPublicKey 公钥
     * @param outTradeNo      本系统交易单号
     * @param tradeNo         支付宝单号
     * @param refundAmount    退款金额
     */

    public static void refund(String appid, String appPrivateKey, String alipayPublicKey, String outTradeNo, String tradeNo, BigDecimal refundAmount, String outRequestNo) {

        AlipayClient alipayClient = new DefaultAlipayClient(
                "https://openapi.alipay.com/gateway.do",
                appid,
                appPrivateKey,
                "json",
                "UTF-8",
                alipayPublicKey,
                "RSA2");
        AlipayTradeRefundModel alipayTradeRefundModel = new AlipayTradeRefundModel();
        alipayTradeRefundModel.setOutTradeNo(outTradeNo);
        alipayTradeRefundModel.setTradeNo(tradeNo);
        alipayTradeRefundModel.setRefundAmount(refundAmount.toString());
        alipayTradeRefundModel.setOutRequestNo(outRequestNo);
        AlipayTradeRefundRequest alipayTradeRefundRequest = new AlipayTradeRefundRequest();
        alipayTradeRefundRequest.setBizModel(alipayTradeRefundModel);
        try {
            AlipayTradeRefundResponse refundResponse = alipayClient.execute(alipayTradeRefundRequest);
            String code = refundResponse.getCode();
            String msg = refundResponse.getMsg();
            if (!("10000".equals(code) && "Success".equals(msg))) {
                throw new PayFailException("退款失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new PayFailException("退款失败");
        }
    }

}
