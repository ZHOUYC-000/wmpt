package com.eason.wmpt.dto;


import com.eason.wmpt.entity.OrderDetail;
import com.eason.wmpt.entity.Orders;
import lombok.Data;

import java.util.List;

@Data
public class OrdersDto extends Orders {

    //private String userName;

    //private String phone;

    //private String address;

    //private String consignee;

    private List<OrderDetail> orderDetails;
	
}
