package com.eason.wmpt.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eason.wmpt.entity.OrderDetail;
import com.eason.wmpt.mapper.OrderDetailMapper;
import com.eason.wmpt.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

}