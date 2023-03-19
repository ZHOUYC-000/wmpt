package com.eason.wmpt.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eason.wmpt.common.R;
import com.eason.wmpt.entity.Category;
import com.eason.wmpt.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> addCategory(HttpServletRequest request, @RequestBody Category category){
        boolean save = categoryService.save(category);
        if(save){
            return R.success("添加成功");
        }
        return R.error("添加失败");
    }

    @GetMapping("/page")
    public R<Page> page(@RequestParam int page, @RequestParam int pageSize){
        // 分页构造器
        Page<Category> pageInfo = new Page<>(page, pageSize);
        // 条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        // 添加条件
        queryWrapper.orderByAsc(Category::getSort);
        // 分页查询
        categoryService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);

    }

    @DeleteMapping
    public R<String> delete(@RequestParam Long ids){
        categoryService.remove(ids);
        return R.success("删除成功");
    }

    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Category category){
        boolean update = categoryService.updateById(category);
        if(!update){
            return R.error("修改失败");
        }
        return  R.success("修改成功");
    }

    /**
     * 根据条件查询分类数据
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(category.getType() != null, Category::getType, category.getType());
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }


}
