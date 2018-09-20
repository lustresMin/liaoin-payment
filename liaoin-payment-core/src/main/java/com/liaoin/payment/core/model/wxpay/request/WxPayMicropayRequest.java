package com.liaoin.payment.core.model.wxpay.request;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Builder;
import lombok.Data;

/**
 * @author mc
 * @version 1.0
 * @date 2018/9/20 11:41
 * @description 刷卡支付请求封装实体
 */
@XStreamAlias("xml")
@Data
@Builder
public class WxPayMicropayRequest {
	/**
	 * 公众账号ID
	 */
	private String appid;
	/**
	 * 商户号
	 */
	@XStreamAlias("mch_id")
	private String mchId;
	/**
	 * 随机字符串
	 */
	@XStreamAlias("nonce_str")
	private String nonceStr;
	/**
	 * 签名
 	 */
	private String sign;
	/**
	 * 商品描述
	 */
	private String body;
	/**
	 * 商户订单号
	 */
	@XStreamAlias("out_trade_no")
	private String outTradeNo;

	/**
	 * 订单金额
	 */
	@XStreamAlias("total_fee")
	private Integer totalFee;

	/**
	 * 终端IP
	 */
	@XStreamAlias("spbill_create_ip")
	private String spbillCreateIp;

	/**
	 * 授权码
	 */
	@XStreamAlias("auth_code")
	private String authCode;

}
