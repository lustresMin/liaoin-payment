package com.liaoin.payment.core.service;

import com.liaoin.payment.core.model.PayRequest;
import com.liaoin.payment.core.model.PayResponse;

/**
 * @author mc
 * @version 1.0
 * @date 2018/9/7 15:11
 * @description
 */
public interface WxPayService {
	/**
	 * 发起支付
	 * @param request
	 * @return
	 */
	PayResponse pay(PayRequest request);

	/**
	 * 异步回调
	 * @param notifyData
	 * @return
	 */
	PayResponse asyncNotify(String notifyData);
}
