package com.example.hotelBookingSystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRecord {
	
	// 房间日志id
	@TableId(type = IdType.AUTO)
	private Integer id;
	// 用户id
	private String userId;
	// 房间id，外键到room的id
	private Integer roomId;
	// 日志时间
	private String time;
	// 日志信息，房间操作（预定，入住，退房，取消预定）
	private String operation;
	
}
