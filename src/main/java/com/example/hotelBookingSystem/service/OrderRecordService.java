package com.example.hotelBookingSystem.service;

import com.example.hotelBookingSystem.util.Result;

import java.util.ArrayList;

public interface OrderRecordService {
	
	// 查找空闲房间
	public Result findIdleRoomByTime(int roomTypeId, ArrayList<Long> time) throws Exception;
	
}
