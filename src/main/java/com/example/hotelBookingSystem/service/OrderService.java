package com.example.hotelBookingSystem.service;


import com.example.hotelBookingSystem.util.Result;

import java.util.ArrayList;

public interface OrderService {

	// 预定房间
	public Result scheduled(int roomTypeId, ArrayList<Long> time, int roomCount, String userId) throws Exception;
	// 取消订单
	public Result cancelScheduledOrder(int id) throws Exception;
		
}
