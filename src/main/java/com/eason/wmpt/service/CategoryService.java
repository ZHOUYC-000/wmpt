package com.eason.wmpt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.eason.wmpt.entity.Category;

public interface CategoryService extends IService<Category> {

    void remove(Long id);
}
