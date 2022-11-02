package com.example.hotelBookingSystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Room {

	// 房间id
	@TableId(type = IdType.AUTO)
	private Integer id;
	// 房间类型id，外键到roomType的id
	private Integer roomTypeId;
	// 房间号
	private Integer number;

}
