package com.eason.wmpt.dto;


import com.eason.wmpt.entity.Dish;
import com.eason.wmpt.entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
