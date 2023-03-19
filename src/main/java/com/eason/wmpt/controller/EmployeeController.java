package com.eason.wmpt.controller;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eason.wmpt.common.R;
import com.eason.wmpt.entity.Employee;
import com.eason.wmpt.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登陆
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        // 1. 将密码加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 2. 查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<Employee>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        // 3. 用户是否存在
        if(emp == null){
            return R.error("登陆失败");
        }

        // 4. 密码对比
        if(!password.equals(emp.getPassword())){
            return R.error("登陆失败");
        }
        
        // 5. 查看用户状态
        if(emp.getStatus() == 0){
            return R.error("登陆失败");
        }
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    /**
     * 员工退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        // 清理登陆员工id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
      * @param employee
     * @return
     */
    @PostMapping
    public R<String> addEmployee(HttpServletRequest request,@RequestBody Employee employee){
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        boolean save = employeeService.save(employee);
        if(save == false){
            return R.error("新增员工失败");
        }
        return R.success("新增员工成功");
    }


    @GetMapping("/page")
    public R<Page<Employee>> page(@RequestParam int page, @RequestParam int pageSize, String name){
        // 1.构造分页构造器
        Page<Employee> iPage = new Page<Employee>(page, pageSize);
        // 2. 构建条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<Employee>();
        // 3. 添加过滤条件
        queryWrapper.like(!StringUtils.isEmpty(name),Employee::getName, name);
        // 4. 添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        // 5. 执行查询
        employeeService.page(iPage, queryWrapper);
        return R.success(iPage);
    }

    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee){
        boolean b = employeeService.updateById(employee);
        if(!b){
            return R.error("修改失败");
        }
        return  R.success("修改成功");
    }


    @GetMapping("/{id}")
    public R<Employee> modifyEmployee(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        if(employee == null){
            return R.error("没有查询到用户信息");
        }
        return R.success(employee);
    }
}
