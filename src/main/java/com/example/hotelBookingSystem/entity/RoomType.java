package com.example.hotelBookingSystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomType implements Comparable<RoomType> {

	// 主键id
	@TableId(type = IdType.AUTO)
	private Integer id;
	// 房间类型名称
	private String name;
	// 房间类型价格
	private Double price;
	// 房间类型大小
	private Integer size;
	// 房间类型图片
	private String picture;
	// 所有这种房间类型的房间集合，@TableField用来声明该属性是不是数据表里面的字段
	@TableField(exist = false)
	private List<Room> roomList;
	
	@Override
	public int compareTo(RoomType o) { // 排序规则
		if(this.price > o.getPrice()) {
			return 1;
		} else if (this.price < o.getPrice()) {
			return -1;
		} else {
			return 0;
		}
	}
	
}
