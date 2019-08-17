package org.order.service;

import org.order.vo.Order;

/**
 * @Description：订单服务
 * @author 0
 * @date 2019年8月15日 下午6:49:01
 */
public interface OrderService {

	int insert(Order record);

	/**
	 * @Title： insertSelective
	 * 
	 * @Description：添加订单
	 * @param ：@param Order record 订单
	 * @return ：int 插入状态
	 */
	int insertSelective(Order record);
}
