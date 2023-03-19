package com.eason.wmpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eason.wmpt.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
