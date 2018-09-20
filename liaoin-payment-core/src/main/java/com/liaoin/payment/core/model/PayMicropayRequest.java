package com.liaoin.payment.core.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author mc
 * @version 1.0
 * @date 2018/9/20 11:51
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayMicropayRequest {
	/**
	 * 订单金额.
	 */
	private Double orderAmount;
	/**
	 * 商户订单号
	 */
	private String outTradeNo;

	/**
	 * 授权码
	 */
	private String authCode;
	/**
	 * 商品描述
	 */
	private String body;
}
