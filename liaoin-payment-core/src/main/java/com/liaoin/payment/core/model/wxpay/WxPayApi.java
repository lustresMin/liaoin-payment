package com.liaoin.payment.core.model.wxpay;

import com.liaoin.payment.core.model.wxpay.response.WxPaySyncResponse;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * @author mc
 * @version 1.0
 * @date 2018/9/7 17:46
 * @description
 */
public interface WxPayApi {

	/**
	 * 统一下单
	 * @param body
	 * @return
	 */
	@POST("/pay/unifiedorder")
	Call<WxPaySyncResponse> unifiedorder(@Body RequestBody body);

	/**
	 * 申请退款
	 * @param body
	 * @return
	 */
//	@POST("/secapi/pay/refund")
//	Call<WxPayRefundResponse> refund(@Body RequestBody body);
}
