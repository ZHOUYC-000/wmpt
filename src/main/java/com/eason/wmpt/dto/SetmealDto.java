package com.eason.wmpt.dto;


import com.eason.wmpt.entity.Setmeal;
import com.eason.wmpt.entity.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
