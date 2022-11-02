package com.example.hotelBookingSystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {

	// 订单id
	@TableId(type = IdType.AUTO)
	private Integer id;
	// 用户id
	private String userId;
	// 订单号
	private String orderNumber;
	// 房间类型名称
	private String roomType;
	// 房间号，多个房间号之间用逗号隔开
	private String roomNumber;
	// 预定时间，（入住时间~退房时间）
	private String scheduledTime;
	// 订单金额
	private Double orderAmount;
	// 订单状态，（待付款，预定成功，已取消，已退款，交易结束）
	private String orderStatus;
	// 订单创建时间
	private String createTime;
	
}
