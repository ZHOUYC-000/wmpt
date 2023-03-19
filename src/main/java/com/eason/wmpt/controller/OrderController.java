package com.eason.wmpt.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eason.wmpt.common.R;
import com.eason.wmpt.dto.OrdersDto;
import com.eason.wmpt.entity.OrderDetail;
import com.eason.wmpt.entity.Orders;
import com.eason.wmpt.service.OrderDetailService;
import com.eason.wmpt.service.OrderService;
import com.eason.wmpt.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("订单数据：{}",orders);
        orderService.submit(orders);
        System.out.println(orders);
        return R.success("下单成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, Long number, String beginTime, String endTime){
        System.out.println(beginTime);
        System.out.println(endTime);
        String format_DateTime = "yyyy-MM-dd HH:mm:ss";
        DateTimeFormatter df = DateTimeFormatter.ofPattern(format_DateTime);
        LocalDateTime beginDateTime = null;
        LocalDateTime endDateTime = null;
        if(beginTime != null){
            beginDateTime = LocalDateTime.parse(beginTime, df);
        }
        if(endTime != null){
            endDateTime = LocalDateTime.parse(endTime, df);
        }

        Page<Orders> ordersPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.like(number != null, Orders::getId,number);
        queryWrapper.ge(beginTime != null, Orders::getOrderTime, beginDateTime);
        queryWrapper.le(endTime != null, Orders::getOrderTime, endDateTime);
        queryWrapper.orderByDesc(Orders::getOrderTime);
        orderService.page(ordersPage,queryWrapper);
        return R.success(ordersPage);
    }

    @GetMapping("/userPage")
    public R<Page> userPage(int page, int pageSize){
        Page<OrdersDto> ordersPage = new Page<>(page, pageSize);
        Page<Orders> iPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Orders::getOrderTime);
        orderService.page(iPage, queryWrapper);
        BeanUtils.copyProperties(iPage, ordersPage, "records");
        List<Orders> records = iPage.getRecords();
        List<OrdersDto> lists = null;
        lists = records.stream().map(item -> {
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item, ordersDto);
            Long userId = item.getUserId();
            Long id = item.getId();
            LambdaQueryWrapper<OrderDetail> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(OrderDetail::getOrderId,id);
            List<OrderDetail> list = orderDetailService.list(queryWrapper1);
            ordersDto.setOrderDetails(list);
            return ordersDto;
        }).collect(Collectors.toList());
        ordersPage.setRecords(lists);
        return R.success(ordersPage);
    }
}