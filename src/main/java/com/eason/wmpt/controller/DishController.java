package com.eason.wmpt.controller;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eason.wmpt.common.R;
import com.eason.wmpt.dto.DishDto;
import com.eason.wmpt.entity.Category;
import com.eason.wmpt.entity.Dish;
import com.eason.wmpt.entity.DishFlavor;
import com.eason.wmpt.service.CategoryService;
import com.eason.wmpt.service.DishFlavorService;
import com.eason.wmpt.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 新增菜品
     * @param request
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> add(HttpServletRequest request, @RequestBody DishDto dishDto){
        String keys = "dish_" + dishDto.getCategoryId() + "_" + dishDto.getStatus();
        redisTemplate.delete(keys);
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    @GetMapping("/page")
    public R<Page> page(@RequestParam int page, @RequestParam int pageSize, String name){
        // 1.构造分页构造器
        Page<Dish> iPage = new Page<Dish>(page, pageSize);
        Page<DishDto> dtoPage = new Page<DishDto>(page, pageSize);
        // 2. 构建条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<Dish>();
        // 3. 添加过滤条件
        queryWrapper.like(!StringUtils.isEmpty(name),Dish::getName, name);
        // 4. 添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        // 5. 执行查询
        dishService.page(iPage, queryWrapper);
        // 6. 对象拷贝
        BeanUtils.copyProperties(iPage, dtoPage, "records");
        List<Dish> records = iPage.getRecords();
        List<DishDto> lists = null;
        lists = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(lists);
        return R.success(dtoPage);
    }

    /**
     * 根据id查询菜品信息，口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> search(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody DishDto dishDto){
        String keys = "dish_" + dishDto.getCategoryId() + "_" + dishDto.getStatus();
        redisTemplate.delete(keys);
        dishService.updateWithFlavor(dishDto);
        return R.success("更新菜品成功");
    }

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {
        String keys = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();
        List<DishDto> dishDtos = (List<DishDto>) redisTemplate.opsForValue().get(keys);
        if(dishDtos != null){
            return R.success(dishDtos);
        }
        // 缓存中没有数据，查询数据库
        //条件构造器
        //List<DishDto> dishDtos = null;
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(!StringUtils.isEmpty(dish.getName()), Dish::getName, dish.getName());
        queryWrapper.eq(null != dish.getCategoryId(), Dish::getCategoryId, dish.getCategoryId());
        //添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        List<Dish> dishs = dishService.list(queryWrapper);

        dishDtos = dishs.stream().map(item -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Category category = categoryService.getById(item.getCategoryId());
            if (category != null) {
                dishDto.setCategoryName(category.getName());
            }
            LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DishFlavor::getDishId, item.getId());

            dishDto.setFlavors(dishFlavorService.list(wrapper));
            return dishDto;
        }).collect(Collectors.toList());
        redisTemplate.opsForValue().set(keys, dishDtos, 60, TimeUnit.MINUTES);

        return R.success(dishDtos);
    }

    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        List<Dish> dishes = dishService.listByIds(ids);
        Set<String> set = new HashSet<>();
        for(Dish dish : dishes){
            String keys = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();
            set.add(keys);
        }
        redisTemplate.delete(set);
        dishService.removeWithFlavor(ids);
        return R.success("删除成功");
    }

    @PostMapping("/status/{theStatus}")
    public R<String> modify(@PathVariable int theStatus, @RequestParam List<Long> ids) {
        dishService.updateSatusWithFlavor(ids, theStatus);
        return R.success("修改状态成功");
    }
}
