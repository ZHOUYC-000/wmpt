package com.eason.wmpt.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eason.wmpt.entity.ShoppingCart;
import com.eason.wmpt.mapper.ShoppingCartMapper;
import com.eason.wmpt.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

}
