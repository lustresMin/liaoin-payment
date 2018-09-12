package com.liaoin.payment.core.service.impl;

import com.liaoin.payment.core.common.WxPaySignature;
import com.liaoin.payment.core.constants.WxPayConstants;
import com.liaoin.payment.core.model.PayRequest;
import com.liaoin.payment.core.model.PayResponse;
import com.liaoin.payment.core.model.wxpay.WxPayApi;
import com.liaoin.payment.core.model.wxpay.request.WxPayUnifiedorderRequest;
import com.liaoin.payment.core.model.wxpay.response.WxPaySyncResponse;
import com.liaoin.payment.core.properties.PaymentProperties;
import com.liaoin.payment.core.service.WxPayService;
import com.liaoin.payment.core.util.MoneyUtil;
import com.liaoin.payment.core.util.RandomUtil;
import com.liaoin.payment.core.util.XmlUtil;
import okhttp3.MediaType;
import okhttp3.RequestBody;
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

	@Autowired
	private PaymentProperties paymentProperties;

	@Override
	public PayResponse pay(PayRequest request) {

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
		build.setSign(WxPaySignature.sign(buildMap(build), paymentProperties.getPay().getMchKey()));

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

	/**
	 * 构造map
	 * @param request
	 * @return
	 */
	private Map<String, String> buildMap(WxPayUnifiedorderRequest request) {
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
	private PayResponse buildPayResponse(WxPaySyncResponse response) {
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

		return PayResponse.builder()
				.appId(response.getAppid())
				.timeStamp(timeStamp)
				.nonceStr(nonceStr)
				.packAge(packAge)
				.signType(signType)
				.paySign(WxPaySignature.sign(map, paymentProperties.getPay().getMchKey()))
				.build();
	}

}
