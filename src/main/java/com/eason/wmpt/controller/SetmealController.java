package com.eason.wmpt.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eason.wmpt.common.R;
import com.eason.wmpt.dto.DishDto;
import com.eason.wmpt.dto.SetmealDto;
import com.eason.wmpt.entity.Category;
import com.eason.wmpt.entity.Setmeal;
import com.eason.wmpt.entity.SetmealDish;
import com.eason.wmpt.service.CategoryService;
import com.eason.wmpt.service.DishService;
import com.eason.wmpt.service.SetmealDishService;
import com.eason.wmpt.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private DishService dishService;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> save(HttpServletRequest request, @RequestBody SetmealDto setmealDto){
        log.info("套餐信息：{}",setmealDto);
        setmealService.saveWithDish(setmealDto);

        return R.success("新增套餐成功");
    }

    /**
     * 套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //分页构造器对象
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        Page<SetmealDto> dtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据name进行like模糊查询
        queryWrapper.like(name != null,Setmeal::getName,name);
        //添加排序条件，根据更新时间降序排列
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo,queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            //对象拷贝
            BeanUtils.copyProperties(item,setmealDto);
            //分类id
            Long categoryId = item.getCategoryId();
            //根据分类id查询分类对象
            Category category = categoryService.getById(categoryId);
            if(category != null){
                //分类名称
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(list);
        return R.success(dtoPage);
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("ids:{}",ids);

        setmealService.removeWithDish(ids);

        return R.success("套餐数据删除成功");
    }

    @GetMapping("/list")
    @Cacheable(value = "setmealCache", key = "#setmeal.categoryId + '_' + #setmeal.status", unless = "#result == null")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        log.info("setmeal:{}", setmeal);
        //条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(setmeal.getName()), Setmeal::getName, setmeal.getName());
        queryWrapper.eq(null != setmeal.getCategoryId(), Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(null != setmeal.getStatus(), Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        return R.success(setmealService.list(queryWrapper));
    }

    @GetMapping("/{id}")
    public R<SetmealDto> search(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);
        return R.success(setmealDto);
    }

    @PutMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> update(HttpServletRequest request, @RequestBody SetmealDto setmealDto){
        log.info("套餐信息：{}",setmealDto);
        setmealService.updateWithDish(setmealDto);

        return R.success("更新套餐成功");
    }

    @PostMapping("/status/{theStatus}")
    public R<String> modify(@PathVariable int theStatus, @RequestParam List<Long> ids) {
        setmealService.updateSatusWithDish(ids, theStatus);
        return R.success("修改状态成功");
    }

    @GetMapping("/dish/{id}")
    public R<List<DishDto>> dish(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        List<DishDto> collect = setmealDishes.stream().map(item -> {
            Long dishId = item.getDishId();
            DishDto dishDto = dishService.getByIdWithFlavor(dishId);
            dishDto.setCopies(item.getCopies());
            return dishDto;
        }).collect(Collectors.toList());
        return R.success(collect);
    }

}
