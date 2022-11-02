package com.example.hotelBookingSystem.service.impl;

import com.example.hotelBookingSystem.dao.RoomDao;
import com.example.hotelBookingSystem.entity.Room;
import com.example.hotelBookingSystem.service.OrderRecordService;
import com.example.hotelBookingSystem.service.RoomService;
import com.example.hotelBookingSystem.service.RoomTypeService;
import com.example.hotelBookingSystem.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class RoomServiceImpl implements RoomService {

	@Autowired
	private RoomDao roomDao;
	@Autowired
	private OrderRecordService orderRecordService;
	@Autowired
	private RoomTypeService roomTypeService;
	
	public Result addRoom(ArrayList<Room> roomList) throws Exception {
		List<Room> allRoomList = roomDao.selectList(null);
		for (int i = 0; i < roomList.size(); i++) {
			for (int j = 0; j < roomList.size(); j++) {
				if(i == j) {
					continue;
				}
				if(roomList.get(i).getNumber().equals(roomList.get(j).getNumber())) {
					return Result.fail("不能增加重复的房间号");
				}
			}
		}
		for (Room room : roomList) {
			if(room.getId() == null) {
				boolean flag = true;
				for (Room room2 : allRoomList) {
					if(room.getNumber().equals(room2.getNumber())) {
						flag = false;
						break;
					}
				}
				if(flag) {
					roomDao.insert(room);
				} else {
					throw new RuntimeException("添加失败，房间号" + room.getNumber() + "已经存在");
				}
			}
		}
		roomTypeService.refreshRedis();
		return Result.success();
	}

	public Result deleteRoom(Integer id) throws Exception {
		Room room = roomDao.selectById(id);
		ArrayList<Long> time = new ArrayList<Long>();
		time.add(System.currentTimeMillis());
		time.add(LocalDateTime.now().plusDays(30).toInstant(ZoneOffset.of("+8")).toEpochMilli());
		ArrayList<Room> idleRoomList = (ArrayList<Room>) orderRecordService.findIdleRoomByTime(room.getRoomTypeId(), time).getData();
		boolean flag = false;
		for (Room room2 : idleRoomList) {
			if(room2.getId().equals(id)) {
				flag = true;
				break;
			}
		}
		if(!flag) {
			return Result.fail("删除失败，当前房间已被预定，不可删除");
		}
		roomDao.deleteById(id);
		roomTypeService.refreshRedis();
		return Result.success();
	}

	public Result updateRoom(Room room) throws Exception {
		ArrayList<Long> time = new ArrayList<Long>();
		time.add(System.currentTimeMillis());
		time.add(LocalDateTime.now().plusDays(30).toInstant(ZoneOffset.of("+8")).toEpochMilli());
		ArrayList<Room> idleRoomList = (ArrayList<Room>) orderRecordService.findIdleRoomByTime(room.getRoomTypeId(), time).getData();
		boolean flag = false;
		for (Room room2 : idleRoomList) {
			if(room.getId().equals(room2.getId())) {
				flag = true;
				break;
			}
		}
		if(!flag) {
			return Result.fail("修改失败，当前房间已被预定，不可修改");
		}
		List<Room> allRoomList = roomDao.selectList(null);
		for (Room room2 : allRoomList) {
			if(room.getNumber().equals(room2.getNumber())) {
				return Result.fail("修改失败，房间号" + room.getNumber() + "已经存在");
			}
		}
		roomDao.updateById(room);
		roomTypeService.refreshRedis();
		return Result.success();
	}

}
