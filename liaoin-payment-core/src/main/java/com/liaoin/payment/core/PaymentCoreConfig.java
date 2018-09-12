package com.liaoin.payment.core;

import com.liaoin.payment.core.properties.PaymentProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author mc
 * @version 1.0
 * @date 2018/9/7 14:08
 * @description
 */
@Configuration
@EnableConfigurationProperties(PaymentProperties.class)
public class PaymentCoreConfig {
}
