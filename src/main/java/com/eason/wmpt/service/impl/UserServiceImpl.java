package com.eason.wmpt.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eason.wmpt.entity.User;
import com.eason.wmpt.mapper.UserMapper;
import com.eason.wmpt.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
