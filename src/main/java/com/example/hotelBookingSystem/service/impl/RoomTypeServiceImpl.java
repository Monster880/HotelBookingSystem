package com.example.hotelBookingSystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.example.hotelBookingSystem.dao.RoomDao;
import com.example.hotelBookingSystem.dao.RoomTypeDao;
import com.example.hotelBookingSystem.entity.Room;
import com.example.hotelBookingSystem.entity.RoomType;
import com.example.hotelBookingSystem.service.OrderRecordService;
import com.example.hotelBookingSystem.service.RoomTypeService;
import com.example.hotelBookingSystem.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class RoomTypeServiceImpl implements RoomTypeService {

	@Autowired
	private RoomTypeDao roomTypeDao;
	@Autowired
	private RoomDao roomDao;
	@Autowired
	private OrderRecordService orderRecordService;
	@Value("${fileUploadPath.picture}")
	private String pictureUploadPath;
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	// 添加房间类型
	public Result addRoomType(RoomType roomType, MultipartFile image) throws Exception {
		String imageName = UUID.randomUUID() + ".jpg";
		String filePath = pictureUploadPath + imageName;
		image.transferTo(new File(filePath));
		roomType.setPicture(imageName);
		roomTypeDao.insert(roomType);
		refreshRedis();
		return Result.success();
	}

	// 查找所有房间类型，不携带房间
	public Result findAllRoomType() throws Exception {
		List<Object> valueList = redisTemplate.opsForHash().values("roomType");
		List<RoomType> roomTypeList = new ArrayList<RoomType>();
		if(valueList.size() > 0) {
			for (Object object : valueList) {
				roomTypeList.add((RoomType) object);
				Collections.sort(roomTypeList);
			}
		} else {
			roomTypeList = roomTypeDao.selectList(null);
			for (RoomType roomType : roomTypeList) {
				redisTemplate.opsForHash().put("roomType", Integer.toString(roomType.getId()), roomType);
			}
			Collections.sort(roomTypeList);
		}
		return Result.success(roomTypeList);
	}
	
	// 查找所有房间类型，携带所有房间
	public Result findAllRoomTypeWithRoom() throws Exception {
		List<RoomType> roomTypeList = roomTypeDao.selectList(null);
		for (RoomType roomType : roomTypeList) {
			QueryWrapper<Room> queryWrapper = new QueryWrapper<Room>().eq("room_type_id", roomType.getId());
			roomType.setRoomList(roomDao.selectList(queryWrapper));
		}
		return Result.success(roomTypeList);
	}

	public Result findRoomTypeById(Integer id) throws Exception {
		RoomType roomType = (RoomType) redisTemplate.opsForHash().get("roomType", Integer.toString(id));
		return Result.success(roomType);
	}
	
	public Result findRoomTypeWithRoomById(Integer id) throws Exception {
		RoomType roomType = (RoomType) redisTemplate.opsForHash().get("roomType", Integer.toString(id));
		QueryWrapper<Room> queryWrapper = new QueryWrapper<Room>();
		queryWrapper.eq("room_type_id", id);
		roomType.setRoomList(roomDao.selectList(queryWrapper));
		return Result.success(roomType);
	}

	public Result deleteRoomType(Integer id) throws Exception {
		RoomType roomType = roomTypeDao.selectById(id);
		roomType.setRoomList(roomDao.selectList(new QueryWrapper<Room>().eq("room_type_id", roomType.getId())));
		ArrayList<Long> time = new ArrayList<Long>();
		time.add(System.currentTimeMillis());
		time.add(LocalDateTime.now().plusDays(30).toInstant(ZoneOffset.of("+8")).toEpochMilli());
		ArrayList<Room> idleRoomList = (ArrayList<Room>) orderRecordService.findIdleRoomByTime(roomType.getId(), time).getData();
		if(idleRoomList.size() < roomType.getRoomList().size()) {
			return Result.fail("修改失败，目前有该类型房间被预定，不可删除该房间类型");
		}
		roomTypeDao.deleteById(id);
		roomDao.delete(new QueryWrapper<Room>().eq("room_type_id", id));
		File file = new File(pictureUploadPath + roomType.getPicture());
		file.delete();
		refreshRedis();
		return Result.success();
	}

	public Result updateRoomTypePicture(Integer id, MultipartFile file) throws Exception {
		RoomType oldRoomType = roomTypeDao.selectById(id);
		String imageName = UUID.randomUUID() + ".jpg";
		String filePath = pictureUploadPath + imageName;
		file.transferTo(new File(filePath));
		RoomType roomType = new RoomType();
		roomType.setId(id);
		roomType.setPicture(imageName);
		roomTypeDao.updateById(roomType);
		File oldFile = new File(pictureUploadPath + oldRoomType.getPicture());
		oldFile.delete();
		refreshRedis();
		return Result.success();
	}

	public Result updateRoomType(RoomType roomType) throws Exception {
		RoomType oldRoomType = roomTypeDao.selectById(roomType.getId());
		oldRoomType.setRoomList(roomDao.selectList(new QueryWrapper<Room>().eq("room_type_id", roomType.getId())));
		if(!oldRoomType.getName().equals(roomType.getName())) {
			ArrayList<Long> time = new ArrayList<Long>();
			time.add(System.currentTimeMillis());
			time.add(LocalDateTime.now().plusDays(30).toInstant(ZoneOffset.of("+8")).toEpochMilli());
			ArrayList<Room> idleRoomList = (ArrayList<Room>) orderRecordService.findIdleRoomByTime(roomType.getId(), time).getData();
			if(idleRoomList.size() < oldRoomType.getRoomList().size()) {
				return Result.fail("修改失败，目前有该类型房间被预定，不可修改房间类型");
			}
		}
		roomTypeDao.updateById(roomType);
		refreshRedis();
		return Result.success();
	}

	// 刷新redis缓存
	public void refreshRedis() throws Exception {
		List<RoomType> roomTypeList = roomTypeDao.selectList(null);
		redisTemplate.delete("roomType");
		for (RoomType roomType : roomTypeList) {
			redisTemplate.opsForHash().put("roomType", Integer.toString(roomType.getId()), roomType);
		}
	}

}
