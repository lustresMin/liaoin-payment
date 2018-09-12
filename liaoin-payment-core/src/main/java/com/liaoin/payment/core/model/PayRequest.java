package com.liaoin.payment.core.model;

import com.liaoin.payment.core.enums.BestPayTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author mc
 * @version 1.0
 * @date 2018/9/7 16:45
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayRequest {

	/**
	 * 支付方式.
	 */
	private BestPayTypeEnum payTypeEnum;

	/**
	 * 订单号.
	 */
	private String orderId;

	/**
	 * 订单金额.
	 */
	private Double orderAmount;

	/**
	 * 订单名字.
	 */
	private String orderName;

	/**
	 * 微信openid, 仅微信支付时需要
	 */
	private String openid;
}
