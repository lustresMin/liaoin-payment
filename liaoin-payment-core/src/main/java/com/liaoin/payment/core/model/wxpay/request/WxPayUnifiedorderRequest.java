package com.liaoin.payment.core.model.wxpay.request;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Builder;
import lombok.Data;

/**
 * @author mc
 * @version 1.0
 * @date 2018/9/7 16:46
 * @description
 */
@XStreamAlias("xml")
@Data
@Builder
public class WxPayUnifiedorderRequest {

	private String appid;

	@XStreamAlias("mch_id")
	private String mchId;

	@XStreamAlias("nonce_str")
	private String nonceStr;

	private String sign;

	private String attach;

	private String body;

	private String detail;

	@XStreamAlias("notify_url")
	private String notifyUrl;

	private String openid;

	@XStreamAlias("out_trade_no")
	private String outTradeNo;

	@XStreamAlias("spbill_create_ip")
	private String spbillCreateIp;

	@XStreamAlias("total_fee")
	private Integer totalFee;

	@XStreamAlias("trade_type")
	private String tradeType;
}
