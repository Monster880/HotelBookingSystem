package com.example.hotelBookingSystem.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.hotelBookingSystem.entity.Order;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDao extends BaseMapper<Order> {

}
