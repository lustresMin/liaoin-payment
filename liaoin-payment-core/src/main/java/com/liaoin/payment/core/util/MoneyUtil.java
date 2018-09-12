package com.liaoin.payment.core.util;

import java.math.BigDecimal;

/**
 * @author mc
 * @version 1.0
 * @date 2018/9/7 17:08
 * @description
 */
public class MoneyUtil {
	/**
	 * 元转分
	 * @param yuan
	 * @return
	 */
	public static Integer Yuan2Fen(Double yuan) {
		return new BigDecimal(yuan).movePointRight(2).intValue();
	}

	/**
	 * 分转元
	 * @param fen
	 * @return
	 */
	public static Double Fen2Yuan(Integer fen) {
		return new BigDecimal(fen).movePointLeft(2).doubleValue();
	}
}
