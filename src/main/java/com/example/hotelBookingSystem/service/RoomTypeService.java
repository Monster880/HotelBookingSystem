package com.example.hotelBookingSystem.service;

import com.example.hotelBookingSystem.entity.RoomType;
import com.example.hotelBookingSystem.util.Result;
import org.springframework.web.multipart.MultipartFile;

public interface RoomTypeService {

	public Result addRoomType(RoomType roomType, MultipartFile image) throws Exception;
	public Result findAllRoomType() throws Exception;
	public Result findAllRoomTypeWithRoom() throws Exception;
	public Result findRoomTypeById(Integer id) throws Exception;
	public Result findRoomTypeWithRoomById(Integer id) throws Exception;
	public Result deleteRoomType(Integer id) throws Exception;
	public Result updateRoomTypePicture(Integer id, MultipartFile file) throws Exception;
	public Result updateRoomType(RoomType roomType) throws Exception;
	public void refreshRedis() throws Exception;
	
}
