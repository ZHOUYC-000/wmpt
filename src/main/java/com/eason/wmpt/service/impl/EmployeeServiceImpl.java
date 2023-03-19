package com.eason.wmpt.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eason.wmpt.entity.Employee;
import com.eason.wmpt.mapper.EmployeeMapper;
import com.eason.wmpt.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
