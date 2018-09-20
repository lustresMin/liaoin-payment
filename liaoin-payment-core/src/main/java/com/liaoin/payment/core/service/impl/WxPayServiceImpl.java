package com.liaoin.payment.core.service.impl;

import com.liaoin.payment.core.common.WxPaySignature;
import com.liaoin.payment.core.constants.WxPayConstants;
import com.liaoin.payment.core.model.PayH5Request;
import com.liaoin.payment.core.model.PayH5Response;
import com.liaoin.payment.core.model.PayMicropayRequest;
import com.liaoin.payment.core.model.wxpay.WxPayApi;
import com.liaoin.payment.core.model.wxpay.request.WxPayMicropayRequest;
import com.liaoin.payment.core.model.wxpay.request.WxPayUnifiedorderRequest;
import com.liaoin.payment.core.model.wxpay.response.WxPayAsyncResponse;
import com.liaoin.payment.core.model.wxpay.response.WxPaySyncResponse;
import com.liaoin.payment.core.properties.PaymentProperties;
import com.liaoin.payment.core.service.WxPayService;
import com.liaoin.payment.core.util.MoneyUtil;
import com.liaoin.payment.core.util.RandomUtil;
import com.liaoin.payment.core.util.XmlUtil;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author mc
 * @version 1.0
 * @date 2018/9/7 15:11
 * @description
 */
@Component("wxPayServiceImpl")
public class WxPayServiceImpl implements WxPayService {
	Logger log = LoggerFactory.getLogger(getClass());
	@Autowired
	private PaymentProperties paymentProperties;

	@Override
	public PayH5Response payH5(PayH5Request request) {

		WxPayUnifiedorderRequest build = WxPayUnifiedorderRequest.builder()
				.outTradeNo(request.getOrderId())
				.totalFee(MoneyUtil.Yuan2Fen(request.getOrderAmount()))
				.body(request.getOrderName())
				.openid(request.getOpenid())
				.nonceStr(RandomUtil.getRandomStr())
				.appid(paymentProperties.getPay().getAppId())
				.mchId(paymentProperties.getPay().getMchId())
				.tradeType(paymentProperties.getPay().getTradeType())
				.notifyUrl(paymentProperties.getPay().getNotifyUrl())
				.spbillCreateIp(paymentProperties.getPay().getSpbillCreateIp())
				.build();
		build.setSign(WxPaySignature.sign(buildMapH5(build), paymentProperties.getPay().getMchKey()));

		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(WxPayConstants.WXPAY_GATEWAY)
				.addConverterFactory(SimpleXmlConverterFactory.create())
				.build();
		String xml = XmlUtil.toXMl(build);
		RequestBody body = RequestBody.create(MediaType.parse("application/xml; charset=utf-8"),xml);
		Call<WxPaySyncResponse> call = retrofit.create(WxPayApi.class).unifiedorder(body);
		Response<WxPaySyncResponse> retrofitResponse  = null;
		try{
			retrofitResponse = call.execute();
		}catch (IOException e) {
			e.printStackTrace();
		}
		if (!retrofitResponse.isSuccessful()) {
			throw new RuntimeException("【微信统一支付】发起支付, 网络异常");
		}
		WxPaySyncResponse response = retrofitResponse.body();

		if(!response.getReturnCode().equals(WxPayConstants.SUCCESS)) {
			throw new RuntimeException("【微信统一支付】发起支付, returnCode != SUCCESS, returnMsg = " + response.getReturnMsg());
		}
		if (!response.getResultCode().equals(WxPayConstants.SUCCESS)) {
			throw new RuntimeException("【微信统一支付】发起支付, resultCode != SUCCESS, err_code = " + response.getErrCode() + " err_code_des=" + response.getErrCodeDes());
		}
		return buildPayResponse(response);
	}

	@Override
	public PayH5Response asyncNotifyH5(String notifyData) {
		//xml解析为对象
		WxPayAsyncResponse asyncResponse = (WxPayAsyncResponse) XmlUtil.fromXML(notifyData, WxPayAsyncResponse.class);

		//签名校验
		if (!WxPaySignature.verify(buildMapH5(asyncResponse), paymentProperties.getPay().getMchKey())) {
			log.error("【微信支付异步通知】签名验证失败, response={}", asyncResponse);
			throw new RuntimeException("【微信支付异步通知】签名验证失败");
		}

		if(!asyncResponse.getReturnCode().equals(WxPayConstants.SUCCESS)) {
			throw new RuntimeException("【微信支付异步通知】发起支付, returnCode != SUCCESS, returnMsg = " + asyncResponse.getReturnMsg());
		}
		//该订单已支付直接返回
		if (!asyncResponse.getResultCode().equals(WxPayConstants.SUCCESS)
				&& asyncResponse.getErrCode().equals("ORDERPAID")) {
			return buildPayResponse(asyncResponse);
		}

		if (!asyncResponse.getResultCode().equals(WxPayConstants.SUCCESS)) {
			throw new RuntimeException("【微信支付异步通知】发起支付, resultCode != SUCCESS, err_code = " + asyncResponse.getErrCode() + " err_code_des=" + asyncResponse.getErrCodeDes());
		}

		return buildPayResponse(asyncResponse);
	}

	/**
	 * 刷卡支付
	 * @param request
	 * @return
	 */
	@Override
	public PayH5Response payMicropay(PayMicropayRequest request) {
		WxPayMicropayRequest build = WxPayMicropayRequest.builder()
				.appid(paymentProperties.getPay().getAppId())
				.mchId(paymentProperties.getPay().getMchId())
				.nonceStr(RandomUtil.getRandomStr())
				.spbillCreateIp(paymentProperties.getPay().getSpbillCreateIp())
				.totalFee(MoneyUtil.Yuan2Fen(request.getOrderAmount()))
				.outTradeNo(request.getOutTradeNo())
				.authCode(request.getAuthCode())
				.body(request.getBody())
				.build();
		build.setSign(WxPaySignature.sign(buildMapMicropay(build), paymentProperties.getPay().getMchKey()));

		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(WxPayConstants.WXPAY_GATEWAY)
				.addConverterFactory(SimpleXmlConverterFactory.create())
				.build();
		String xml = XmlUtil.toXMl(build);
		RequestBody body = RequestBody.create(MediaType.parse("application/xml; charset=utf-8"),xml);
		Call<WxPaySyncResponse> call = retrofit.create(WxPayApi.class).micropay(body);
		Response<WxPaySyncResponse> retrofitResponse  = null;
		try{
			retrofitResponse = call.execute();
		}catch (IOException e) {
			e.printStackTrace();
		}
		if (!retrofitResponse.isSuccessful()) {
			throw new RuntimeException("【微信统一支付】发起支付, 网络异常");
		}
		WxPaySyncResponse response = retrofitResponse.body();

		if(!response.getReturnCode().equals(WxPayConstants.SUCCESS)) {
			throw new RuntimeException("【微信统一支付】发起支付, returnCode != SUCCESS, returnMsg = " + response.getReturnMsg());
		}
		if (!response.getResultCode().equals(WxPayConstants.SUCCESS)) {
			throw new RuntimeException("【微信统一支付】发起支付, resultCode != SUCCESS, err_code = " + response.getErrCode() + " err_code_des=" + response.getErrCodeDes());
		}
		return buildPayResponse(response);
	}

	private Map<String,String> buildMapMicropay(WxPayMicropayRequest request) {
		Map<String, String> map = new HashMap<>();
		map.put("appid", request.getAppid());
		map.put("mch_id", request.getMchId());
		map.put("nonce_str", request.getNonceStr());
		map.put("sign", request.getSign());
		map.put("body", request.getBody());
		map.put("out_trade_no", request.getOutTradeNo());
		map.put("total_fee", String.valueOf(request.getTotalFee()));
		map.put("spbill_create_ip", request.getSpbillCreateIp());
		map.put("auth_code", request.getAuthCode());
		return map;
	}

	private Map<String, String> buildMapH5(WxPayAsyncResponse response) {
		Map<String, String> map = new HashMap<>();
		map.put("return_code", response.getReturnCode());
		map.put("return_msg", response.getReturnMsg());
		map.put("appid", response.getAppid());
		map.put("mch_id", response.getMchId());
		map.put("device_info", response.getDeviceInfo());
		map.put("nonce_str", response.getNonceStr());
		map.put("sign", response.getSign());
		map.put("result_code", response.getResultCode());
		map.put("err_code", response.getErrCode());
		map.put("err_code_des", response.getErrCodeDes());
		map.put("openid", response.getOpenid());
		map.put("is_subscribe", response.getIsSubscribe());
		map.put("trade_type", response.getTradeType());
		map.put("bank_type", response.getBankType());
		map.put("total_fee", String.valueOf(response.getTotalFee()));
		map.put("fee_type", response.getFeeType());
		map.put("cash_fee", response.getCashFee());
		map.put("cash_fee_type", response.getCashFeeType());
		map.put("coupon_fee", response.getCouponFee());
		map.put("coupon_count", response.getCouponCount());
		map.put("transaction_id", response.getTransactionId());
		map.put("out_trade_no", response.getOutTradeNo());
		map.put("attach", response.getAttach());
		map.put("time_end", response.getTimeEnd());
		return map;
	}



	/**
	 * 构造map
	 * @param request
	 * @return
	 */
	private Map<String, String> buildMapH5(WxPayUnifiedorderRequest request) {
		Map<String, String> map = new HashMap<>();
		map.put("appid", request.getAppid());
		map.put("mch_id", request.getMchId());
		map.put("nonce_str", request.getNonceStr());
		map.put("sign", request.getSign());
		map.put("attach", request.getAttach());
		map.put("body", request.getBody());
		map.put("detail", request.getDetail());
		map.put("notify_url", request.getNotifyUrl());
		map.put("openid", request.getOpenid());
		map.put("out_trade_no", request.getOutTradeNo());
		map.put("spbill_create_ip", request.getSpbillCreateIp());
		map.put("total_fee", String.valueOf(request.getTotalFee()));
		map.put("trade_type", request.getTradeType());
		return map;
	}
	/**
	 * 返回给h5的参数
	 * @param response
	 * @return
	 */
	private PayH5Response buildPayResponse(WxPaySyncResponse response) {
		String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
		String nonceStr = RandomUtil.getRandomStr();
		String packAge = "prepay_id=" + response.getPrepayId();
		String signType = "MD5";

		//先构造要签名的map
		Map<String, String> map = new HashMap<>();
		map.put("appId", response.getAppid());
		map.put("timeStamp", timeStamp);
		map.put("nonceStr", nonceStr);
		map.put("package", packAge);
		map.put("signType", signType);

		return PayH5Response.builder()
				.appId(response.getAppid())
				.timeStamp(timeStamp)
				.nonceStr(nonceStr)
				.packAge(packAge)
				.signType(signType)
				.paySign(WxPaySignature.sign(map, paymentProperties.getPay().getMchKey()))
				.build();
	}

	private PayH5Response buildPayResponse(WxPayAsyncResponse response) {
		return PayH5Response.builder()
					.orderAmount(MoneyUtil.Fen2Yuan(response.getTotalFee()))
					.orderId(response.getOutTradeNo())
					.outTradeNo(response.getTransactionId())
					.build();
	}

}
