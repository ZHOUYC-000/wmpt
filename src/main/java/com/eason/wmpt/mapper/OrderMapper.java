package com.eason.wmpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eason.wmpt.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Orders> {

}