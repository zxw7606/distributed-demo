package org.order.mapper;

import org.apache.ibatis.annotations.Param;
import org.order.vo.Order;

public interface OrderMapper {
	/**
	 * This method was generated by MyBatis Generator. This method corresponds to
	 * the database table tb_order
	 *
	 * @mbg.generated Fri Aug 16 19:13:17 CST 2019
	 */
	int insert(Order record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to
	 * the database table tb_order
	 *
	 * @mbg.generated Fri Aug 16 19:13:17 CST 2019
	 */
	int insertSelective(Order record);

	Order selectByPrimaryKey(@Param("orderId") String orderId);

	int updateByPrimaryKeySelective(Order record);
}