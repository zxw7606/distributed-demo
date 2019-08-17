package org.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.order.service.OrderService;
import org.order.vo.Order;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.web.config.AlipayConfig;
import org.web.util.PayUtils;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeWapPayRequest;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Description：支付的回调接口
 * @author 0
 * @date 2019年8月16日 下午5:19:09
 */
@Slf4j
@Controller
@RequestMapping("order")
public class OrderController {

	@Reference
	private OrderService orderservice;

	private @Autowired RabbitTemplate RabbitTemplate;

	private @Autowired AlipayConfig alipayConfig;

	/**
	 * @Title：buy
	 * @Description：付款后的异步通知接口
	 * @param ：@param order
	 */
	@RequestMapping(value = "notify_url")
	public void notiry(@RequestParam Map<String, String> params) {
		// boolean verify_result = AlipaySignature.rsaCheckV1(params,
		// alipayConfig.public_key, alipayConfig.charset,
		// "RSA2");
		// 商户订单号
		String out_trade_no = params.get("out_trade_no");
		// 支付宝交易号
		String trade_no = params.get("trade_no");
		// 交易状态
		String trade_status = params.get("trade_status");

		// if (!verify_result) {
		// log.warn("订单异步回调通知 验证失败");
		// return;
		// }
		// log.warn("订单异步回调通知 验证成功");

		log.warn("支付成功,,异步处理商户订单信息..");
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("out_trade_no", out_trade_no);
		map.put("trade_no", trade_no);
		map.put("status", "支付成功");
		RabbitTemplate.convertAndSend("ex1", "com.zxw.publish.order", map);
		// 订单支付成功

	}

	//
	// charset: UTF-8
	// out_trade_no: 11971040979
	// method: alipay.trade.wap.pay.return
	// total_amount: 1.00
	// sign:
	// tlWX7X60c3ChRlvptg9iyKBNUdxCUPN0XRtVLoF9w4eeGeiTyGCFdLx8v3kjR3sKO4aXkR2ScZCIz79x96xSiRwz7zfpJyyob0wzfMdBllWq3B4QOfnQ0ETY3IZZjnwfeNbgvOr09lCW4IVN8L4oSmbciV9DWWZTCUMiRaO6lhekJMYuq8YuRpvpjk0nPnZv+SX+7eipZsJZoD8+/0dtgsNJqx4QaJHW/0qp7cqmN56WH6qlu9Ob+SlsZ7AtNYOtf3QLZLS8GjlWBH06sj6fqTmI7mFpoZatOL6+Q6B8b9mErdYrk6CNT4M+ukmwcBEsJWMEXCY+CwpW3cHNskp/4A==
	// trade_no: 2019081722001475931000104814
	// auth_app_id: 2016101100661640
	// version: 1.0
	// app_id: 2016101100661640
	// sign_type: RSA2
	// seller_id: 2088102179097842
	// timestamp: 2019-08-17 10:58:25
	//

	@RequestMapping(value = "return_url")
	public String returnUrl(@RequestParam Map<String, String> params) {
		try {
			boolean verify_result = AlipaySignature.rsaCheckV1(params, alipayConfig.public_key, alipayConfig.charset,
					"RSA2");
			// 商户订单号
			String out_trade_no = params.get("out_trade_no");
			// 支付宝交易号
			String trade_no = params.get("trade_no");
			// 交易状态
			String trade_status = params.get("trade_status");

			notiry(params);

			if (!verify_result) {
				log.warn("订单回调通知 验证失败");
				return "error_order_page";
			}
			log.warn("订单回调通知 验证成功");

			if (trade_status.equals("TRADE_FINISHED")) {
				// 订单是处理过的 比如退款的回调也在这？？？
			} else if (trade_status.equals("TRADE_SUCCESS")) {
				log.warn("支付成功,跳转页面");
				return "success_order_page";
			}

		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
		return null;
	}

	public OrderService getOrderservice() {
		return orderservice;
	}

	public void setOrderservice(OrderService orderservice) {
		this.orderservice = orderservice;
	}

}
