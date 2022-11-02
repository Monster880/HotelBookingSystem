package com.example.hotelBookingSystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.hotelBookingSystem.dao.RoomDao;
import com.example.hotelBookingSystem.dao.OrderRecordDao;
import com.example.hotelBookingSystem.dao.RoomTypeDao;
import com.example.hotelBookingSystem.dao.OrderDao;
import com.example.hotelBookingSystem.entity.Room;
import com.example.hotelBookingSystem.entity.OrderRecord;
import com.example.hotelBookingSystem.entity.RoomType;
import com.example.hotelBookingSystem.entity.Order;
import com.example.hotelBookingSystem.service.OrderRecordService;
import com.example.hotelBookingSystem.service.OrderService;
import com.example.hotelBookingSystem.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

	@Autowired
	private RoomDao roomDao;
	@Autowired
	private OrderRecordDao orderRecordDao;
	@Autowired
	private OrderDao orderDao;
	@Autowired
	private RoomTypeDao roomTypeDao;
	@Autowired
	private OrderRecordService orderRecordService;

	// 预定房间
	public Result scheduled(int roomTypeId, ArrayList<Long> time, int roomCount, String userId) throws Exception {
		// 先查询还有没有足够的空闲房间
		ArrayList<Room> idleRoomList = (ArrayList<Room>) orderRecordService.findIdleRoomByTime(roomTypeId, time).getData();
		if (idleRoomList.size() < roomCount) {
			return Result.fail("对不起，剩余房间数量不足");
		}
		ArrayList<String> timeList = new ArrayList<String>();
		LocalDate startDate = Instant.ofEpochMilli(time.get(0)).atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate endDate = Instant.ofEpochMilli(time.get(1)).atZone(ZoneId.systemDefault()).toLocalDate();
		long days = ChronoUnit.DAYS.between(startDate, endDate);
		for (int i = 0; i < days; i++) {
			timeList.add(startDate.toString());
			startDate = startDate.plusDays(1);
		}
		// 添加房间日志
		// 设置订单房间号
		String roomNumber = "";
		for (int i = 0; i < roomCount; i++) {
			for (String tempTime : timeList) {
				OrderRecord orderRecord = new OrderRecord();
				orderRecord.setUserId(userId);
				orderRecord.setRoomId(idleRoomList.get(i).getId());
				orderRecord.setTime(tempTime);
				orderRecord.setOperation("预定");
				orderRecordDao.insert(orderRecord);
			}
			roomNumber += idleRoomList.get(i).getNumber();
			roomNumber += "，";
		}
		// 订单入住时间
		String scheduledTime = timeList.get(0) + "~" + endDate.toString();
		RoomType roomType = roomTypeDao.selectOne(new QueryWrapper<RoomType>().eq("id", roomTypeId));
		Double orderAmount = roomCount * roomType.getPrice() * days;
		// 去掉房间id结尾的逗号
		roomNumber = roomNumber.substring(0, roomNumber.length() - 1);
		// 创建订单
		Order order = new Order();
		order.setUserId(userId);
		order.setOrderNumber(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
		order.setRoomType(roomType.getName());
		order.setRoomNumber(roomNumber);
		order.setScheduledTime(scheduledTime);
		order.setOrderAmount(orderAmount);
		order.setOrderStatus("待付款");
		order.setCreateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		orderDao.insert(order);
		return Result.success(order);
	}

	// 取消订单
	public Result cancelScheduledOrder(int id) throws Exception {
		Order order = orderDao.selectOne(new QueryWrapper<Order>().eq("id", id));
		// 取消订单
		order.setOrderStatus("已取消");
		orderDao.updateById(order);
		// 添加房间日志
		String[] roomNumberList = order.getRoomNumber().split("，");
		LocalDate startDate = LocalDate.parse(order.getScheduledTime().split("~")[0]);
		LocalDate endDate = LocalDate.parse(order.getScheduledTime().split("~")[1]);
		ArrayList<String> timeList = new ArrayList<String>();
		long days = ChronoUnit.DAYS.between(startDate, endDate);
		for (int i = 0; i < days; i++) {
			timeList.add(startDate.toString());
			startDate = startDate.plusDays(1);
		}
		for (String roomNumber : roomNumberList) {
			for (String time : timeList) {
				Room room = roomDao.selectOne(new QueryWrapper<Room>().eq("number", roomNumber));
				OrderRecord orderRecord = new OrderRecord();
				orderRecord.setUserId(order.getUserId());
				orderRecord.setRoomId(room.getId());
				orderRecord.setTime(time);
				orderRecord.setOperation("取消预定");
				orderRecordDao.insert(orderRecord);
			}
		}
		return Result.success();
	}

}
