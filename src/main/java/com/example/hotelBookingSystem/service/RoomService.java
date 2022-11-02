package com.example.hotelBookingSystem.service;

import com.example.hotelBookingSystem.entity.Room;
import com.example.hotelBookingSystem.util.Result;


import java.util.ArrayList;

public interface RoomService {

	public Result addRoom(ArrayList<Room> roomList) throws Exception;
	public Result deleteRoom(Integer id) throws Exception;
	public Result updateRoom(Room room) throws Exception;
	
}
