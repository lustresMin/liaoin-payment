package com.liaoin.payment.core.exception.wx;

import com.liaoin.payment.core.enums.BestPayResultEnum;

/**
 * @author mc
 * @version 1.0
 * @date 2018/9/7 14:14
 * @description
 */
public class BestPayException extends RuntimeException{
	private Integer code;

	public BestPayException(BestPayResultEnum resultEnum) {
		super(resultEnum.getMsg());
		code = resultEnum.getCode();
	}

	public Integer getCode() {
		return code;
	}
}
