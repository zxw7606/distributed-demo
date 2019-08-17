package org.web.controller;

import java.io.IOException;
import java.math.BigDecimal;

import javax.servlet.http.HttpServletResponse;

import org.order.service.OrderService;
import org.order.vo.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.web.config.AlipayConfig;
import org.web.service.GoodService;
import org.web.util.PayUtils;
import org.web.vo.Good;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.request.AlipayTradeWapPayRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("goods")
public class GoosController {

	@Autowired
	private GoodService goodService;
	@Reference
	private OrderService orderservice;
	@Autowired
	private AlipayConfig alipayConfig;

	@RequestMapping("list")
	public String goodsList() {
		return "list";
	}

	/**
	 * 
	 * @Title：goodsBuy
	 * @Description：模拟支付宝接口 买商品 生成支付页面
	 */

	@RequestMapping(value = "buy/{id}", produces = "text/html")
	public void goodsBuy(@PathVariable("id") Integer id, HttpServletResponse response) {

		Good good = goodService.selectByPrimaryKey(id);
		Order order = new Order();
		order.setSubjectNum(1);
		order.setPayment(new BigDecimal(500));
		order.setOrderId(PayUtils.GeneOutTradeNo());
		order.setStatus("待支付");
		order.setSubjectName(good.getSubjectName());
		order.setSubjectDescription(good.getSubjectDesc());

		// 创建商家订单
		orderservice.insertSelective(order);

		AlipayClient client = new DefaultAlipayClient(alipayConfig.gateway_url, alipayConfig.appid,
				alipayConfig.rsa_priavte_key, alipayConfig.format, alipayConfig.charset, alipayConfig.public_key,
				alipayConfig.signType);

		// 封装请求支付信息
		AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();

		model.setOutTradeNo(order.getOrderId());
		model.setSubject(order.getSubjectName());
		model.setTotalAmount(String.valueOf(order.getSubjectNum()));
		model.setBody(order.getSubjectDescription());
		model.setTimeoutExpress("15m");
		model.setProductCode(order.getSubjectCode());

		AlipayTradeWapPayRequest alipay_request = new AlipayTradeWapPayRequest();
		alipay_request.setBizModel(model);
		// 设置异步通知地址
		alipay_request.setNotifyUrl(alipayConfig.notify_url);
		// 设置同步地址
		alipay_request.setReturnUrl(alipayConfig.return_url);

		try {
			String body = client.pageExecute(alipay_request).getBody();
			response.setContentType("text/html;charset=utf-8");
			response.getWriter().write(body);
			response.getWriter().flush();
		} catch (AlipayApiException e) {
			log.error("订单页面创建失败 code:{}, msg:{}", e.getErrCode(), e.getErrMsg());
			e.printStackTrace();
		} catch (IOException e) {
			log.error("页面 IO 渲染失败");
			e.printStackTrace();
		} finally {
			try {
				response.getWriter().close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

}
