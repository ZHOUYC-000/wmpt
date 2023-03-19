package com.eason.wmpt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.eason.wmpt.dto.SetmealDto;
import com.eason.wmpt.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐，同时需要删除套餐和菜品的关联数据
     * @param ids
     */
    public void removeWithDish(List<Long> ids);

    public SetmealDto getByIdWithDish(Long id);

    public void updateWithDish(SetmealDto setmealDto);

    void updateSatusWithDish(List<Long> ids, int theStatus);
}
