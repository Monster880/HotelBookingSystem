package com.example.hotelBookingSystem.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

	// 用户id
	private String userId;
	// 用户名
	private String nickName;
	// 用户手机号
	private String phoneNumber;
	// 用户身份(admin,user)
	private String type;
	
}
