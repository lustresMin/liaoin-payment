package com.liaoin.payment.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;

/**
 * @author mc
 * @version 1.0
 * @date 2018/9/7 16:15
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayResponse {

	private String prePayParams;

	private URI payUri;

	/**
	 * 以下字段仅在微信h5支付返回
	 */
	private String appId;

	private String timeStamp;

	private String nonceStr;

	@JsonProperty("package")
	private String packAge;

	private String signType;

	private String paySign;

	/**
	 * 以下字段在微信异步通知下返回
	 */
	private Double orderAmount;

	private String orderId;

	/**
	 * 第三方支付的流水号
	 */
	private String outTradeNo;
}
