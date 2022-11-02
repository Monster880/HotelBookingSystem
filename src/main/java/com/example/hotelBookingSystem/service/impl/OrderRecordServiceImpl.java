package com.example.hotelBookingSystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.hotelBookingSystem.dao.RoomDao;
import com.example.hotelBookingSystem.dao.OrderRecordDao;
import com.example.hotelBookingSystem.dao.RoomTypeDao;
import com.example.hotelBookingSystem.entity.Room;
import com.example.hotelBookingSystem.entity.OrderRecord;
import com.example.hotelBookingSystem.entity.RoomType;
import com.example.hotelBookingSystem.service.OrderRecordService;
import com.example.hotelBookingSystem.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderRecordServiceImpl implements OrderRecordService {

	@Autowired
	private RoomDao roomDao;
	@Autowired
	private OrderRecordDao orderRecordDao;
	@Autowired 
	private RoomTypeDao roomTypeDao;

	// 查找空闲房间
	public Result findIdleRoomByTime(int roomTypeId, ArrayList<Long> time) throws Exception {
		ArrayList<String> timeList = new ArrayList<String>();
		LocalDate startDate = Instant.ofEpochMilli(time.get(0)).atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate endDate = Instant.ofEpochMilli(time.get(1)).atZone(ZoneId.systemDefault()).toLocalDate();
		long days = ChronoUnit.DAYS.between(startDate, endDate);
		for (int i = 0; i < days; i++) {
			timeList.add(startDate.toString());
			startDate = startDate.plusDays(1);
		}
		List<Room> roomList = roomDao.selectList(new QueryWrapper<Room>().eq("room_type_id", roomTypeId));
		ArrayList<Room> idleRooms = new ArrayList<>();
		for (Room room : roomList) {
			boolean isIdle = true;
			for (String tempTime : timeList) {
				// 查找预定记录
				QueryWrapper<OrderRecord> queryWrapper = new QueryWrapper<OrderRecord>();
				queryWrapper.eq("room_id", room.getId());
				queryWrapper.eq("time", tempTime);
				queryWrapper.eq("operation", "预定");
				List<OrderRecord> orderRecordList = orderRecordDao.selectList(queryWrapper);
				// 查找取消预定记录
				queryWrapper = new QueryWrapper<OrderRecord>();
				queryWrapper.eq("room_id", room.getId());
				queryWrapper.eq("time", tempTime);
				queryWrapper.eq("operation", "取消预定");
				List<OrderRecord> orderRecordList1 = orderRecordDao.selectList(queryWrapper);
				if(orderRecordList.size() > 0 && orderRecordList.size() > orderRecordList1.size()) {
					isIdle = false;
					break;
				}
			}
			if(isIdle) {
				idleRooms.add(room);
			}
		}
		RoomType roomType = roomTypeDao.selectOne(new QueryWrapper<RoomType>().eq("id", roomTypeId));
		if(idleRooms.size() < 1) {
			return Result.result(false, "当前选择的时间已经没有空余的" + roomType.getName() + "了", idleRooms);
		}
		return Result.success(idleRooms);
	}

}
