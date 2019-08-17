package org.web.util;

import java.util.Date;

public class PayUtils {

	/**
	 * 
	 * @Title：GeneOutTradeNo
	 * @Description：获取商户订单号
	 */
	public static String GeneOutTradeNo() {
		Date date = new Date();
		StringBuilder builder = new StringBuilder();
		builder.append(date.getYear()).append(date.getMonth()).append(date.getHours()).append(date.getSeconds());
		String milliseconds = of(date.getTime());
		builder.append(milliseconds.substring(milliseconds.length() - 3));
		return builder.toString();
	}

	public static String of(long num) {
		return String.valueOf(num);
	}
}
