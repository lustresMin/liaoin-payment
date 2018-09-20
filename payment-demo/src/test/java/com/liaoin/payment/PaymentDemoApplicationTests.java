package com.liaoin.payment;

import com.liaoin.payment.core.enums.BestPayTypeEnum;
import com.liaoin.payment.core.model.PayH5Request;
import com.liaoin.payment.core.model.PayH5Response;
import com.liaoin.payment.core.model.PayMicropayRequest;
import com.liaoin.payment.core.service.WxPayService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PaymentDemoApplicationTests {
	@Autowired
	private WxPayService wxPayService;
	@Test
	public void contextLoads() {
		PayH5Response pay = wxPayService.payMicropay(PayMicropayRequest.builder()
						//订单金额
						.orderAmount(0.1)
						//订单号
						.outTradeNo("4545445465")
						//授权码
						.authCode("134898489094140380")
						//商品描述
						.body("测试")
						.build());
		System.out.println(pay);
	}

}
