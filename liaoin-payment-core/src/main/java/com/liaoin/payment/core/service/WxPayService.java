package com.liaoin.payment.core.service;

import com.liaoin.payment.core.model.PayH5Request;
import com.liaoin.payment.core.model.PayH5Response;
import com.liaoin.payment.core.model.PayMicropayRequest;

/**
 * @author mc
 * @version 1.0
 * @date 2018/9/7 15:11
 * @description
 */
public interface WxPayService {
	/**
	 * H5发起支付
	 * @param request
	 * @return
	 */
	PayH5Response payH5(PayH5Request request);

	/**
	 * H5异步回调
	 * @param notifyData
	 * @return
	 */
	PayH5Response asyncNotifyH5(String notifyData);

	/**
	 * 刷卡发起支付
	 * @param request
	 * @return
	 */
	PayH5Response payMicropay(PayMicropayRequest request);
}
