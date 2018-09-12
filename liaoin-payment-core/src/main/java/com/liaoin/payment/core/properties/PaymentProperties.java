package com.liaoin.payment.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author mc
 * @version 1.0
 * @date 2018/9/7 14:09
 * @description
 */
@Data
@ConfigurationProperties(prefix = "liaoin.payment")
public class PaymentProperties {
	/**
	 * 支付请求相关配置
	 */
	private WxPayH5Properties pay = new WxPayH5Properties();

}
