package com.liaoin.payment.core;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LiaoinPaymentCoreApplicationTests {

	@Test
	public void contextLoads() {
		String APP_PRIVATE_KEY="MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQC2vINrIDl68oda7h96fW9zOtIqe1cyR3kOew951D9GH6hJ4WT+vm5eosS3JiklimsL+atpfmMfvSyeDf2Lm/C8TQRhCVWArQBOxCup/+Y2iwS3NKMJu9rNsAdGiQH1Cplqw62kPx//MSwVbcxMeEM977xQvxbIcV+kRboTah/yFkbwjgMwPfEmeHIUVwQXbe2RbFf3JAgof8oGDKqw9O+VHM5M08x/V9wL1sPtcrkXhEbpgoIiwSpbg2HMvUI0wCzXZt1mkG9c37X9NstnRu/jlqh2rz6ll6zLk/tizfH4VFgZgxp6tPSgv24DG+ed/kmSBkOpA6u8CxxBY4wStSnHAgMBAAECggEBAIJn210qnPqJc2DHyD0QoOVZl7vrU1m1OcXEulGVzyXK170JlZpZ6cetXrbZC8oXkrb0EIhzQmfXB60vjL4BpfWq/LIHCDaQv0R2qDZkQTSxVscrUCJVJhtdQIPsqDHnw342cIdi6QGjZLNSLNZxiIL9v6TcCXU3uyou/FB4jp2iZXsf+AjpaT6iwdC8n/br0Mi6S2dESG8gPjpq7SUQg2wSjDR3QouehF9U8UpUkCqh+iUFZ0Fz3KCV87rI3wPh2fi60LB5NyolG7rT4YbLJOuh0XQovJtt6FkbpSQ8ROEUaBUx7Fi65YvXZKWeGPm+p67kj9nXlyWcqCLiFNnIZTkCgYEA2qNzw/C4QzPpq6MUTdXSv2pDNdUP2QzxCUxpyYVOqciCGlk/U/tJq1Acoo1eBng8UhImKfc5QCgtlGGbPLXxwpA9aqtvJc50vcLdndwP6ReA5w2UAQZARbMjYNyEkCuQJts6Yd2iqszNSNIbDdPkZhBR47/qKbseJ9ZLxIwR/pMCgYEA1fZ9UgM+RMJI+NB0+/BpDFVaV14LiYUp6KFe1TGu84gYcvCtJWzx8MpNRXIRefqp9X4dRPJ46TUT/uhicVLFYmMeCH7gPV6vgxt2WzfeWTi5IP+EeV34ttYe0zM0VFrCI4oP5UUeVwmT39V+xpPdOKF9NN5Yy4DlhdiuTWyyNH0CgYEAmnOnfz7ZXJgataZx3H5178UIj8ng9SsgR7pUbQkgRe6ggi6T/ybgpwXkBK0rRBTCQBQUI8dDCWCYul6thJjfndu5l1ZeIW4UqE01s6PXFYoGBLNh38tjshlXn8CIYTR1FA4A00v38wLU7mGGvSD4E2XqA6L6r9pA9lF7mvB8ccMCgYBAUbHIlqsAVntDOL8CAXpCt+HJg77qHzrC8MemRSI3mUDoRIt4RbDRVq814h140q6G2tuHn3BTgp45MbhuIpitg+hCW4mjSTRvsDC7KSdRIOkeD1HOfj5HEEUB7lGbzhCwSE+Q918wgPOQBQjwPwDwhjMyUmjU/DGDM+jp43QKrQKBgQC0Qr8IC09LpvRLhHrGIYdhVn5ciA2zt2xO0bbdmVxEVCvCmkIGX0q8RrlV0JLxxI8aLD4TXgffPFBgDCbBJSCE0k0Ds5JU8KzJl6RWTFvaDxV91OCN1BFciWKxieRiV/Kz+bxK2E5K3EMKb93rqlEO91LXlfVO1AmXsoK6xRyTFQ==";
		String ALIPAY_PUBLIC_KEY="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtryDayA5evKHWu4fen1vczrSKntXMkd5DnsPedQ/Rh+oSeFk/r5uXqLEtyYpJYprC/mraX5jH70sng39i5vwvE0EYQlVgK0ATsQrqf/mNosEtzSjCbvazbAHRokB9QqZasOtpD8f/zEsFW3MTHhDPe+8UL8WyHFfpEW6E2of8hZG8I4DMD3xJnhyFFcEF23tkWxX9yQIKH/KBgyqsPTvlRzOTNPMf1fcC9bD7XK5F4RG6YKCIsEqW4NhzL1CNMAs12bdZpBvXN+1/TbLZ0bv45aodq8+pZesy5P7Ys3x+FRYGYMaerT0oL9uAxvnnf5JkgZDqQOrvAscQWOMErUpxwIDAQAB";
		String outtradeno = "2018092009420011";

		//实例化客户端
		AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", "2016092100564328", APP_PRIVATE_KEY, "json", "UTF-8", ALIPAY_PUBLIC_KEY, "RSA2");
		//实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
		AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();


		//SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
		AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
		//对一笔交易的具体描述信息。如果是多种商品，请将商品描述字符串累加传给body。
		model.setBody("我是测试数据");
		//商品的标题/交易标题/订单标题/订单关键字等。
		model.setSubject("App支付测试Java");
		//商户网站唯一订单号
		model.setOutTradeNo(outtradeno);
		//该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点， 如 1.5h，可转换为 90m。
		model.setTimeoutExpress("30m");
		//订单总金额，单位为元，精确到小数点后两位，取值范围[0.01,100000000]
		model.setTotalAmount("0.01");
		//销售产品码，商家和支付宝签约的产品码
		model.setProductCode("QUICK_MSECURITY_PAY");


		request.setBizModel(model);
		request.setNotifyUrl("www.baidu.com");
		try {
			//这里和普通的接口调用不同，使用的是sdkExecute
			AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
			System.out.println("orderString:"+response.getBody());//就是orderString 可以直接给客户端请求，无需再做处理。
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}

	}


}
