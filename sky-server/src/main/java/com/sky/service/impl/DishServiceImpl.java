package com.sky.service.impl;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;
@Slf4j
@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    //新增菜品和对应口味
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO){
//向菜品表插入一条数据
        Dish dish=new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.insert(dish);
        //设置口味表的dish_id
        Long dishId=dish.getId();
        //向口味表插入多条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors!=null&&flavors.size()>0){
            flavors.forEach(flavor -> {
                flavor.setDishId(dishId);
            });

            dishFlavorMapper.inserBatch(flavors);


        }
    }
     //菜品分页查询
     public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO){
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        PageResult pageResult=new PageResult(page.getTotal(),page.getResult());
        return pageResult;
    }
     //批量删除菜品
     @Transactional
     public void deleteBatch(List<Long> ids){
        //判断是否存在菜品
        ids.forEach(id -> {
            Dish dish = dishMapper.selectById(id);
            if (dish.getStatus()== StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        });

         //判断是否关联了套餐
         List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishesId(ids);
         if (setmealIds!=null&&setmealIds.size()>0){
             //当前菜品下有套餐，不能删除
             throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
         }
         //删除菜品表中的数据
             dishMapper.deleteById(ids);
             //删除口味表中的数据
             dishFlavorMapper.deleteByDishId(ids);

     }
     //根据id查询菜品和对应口味
     public DishVO getByIdWithFlavor(Long id){
        //根据id查询菜品
        Dish dish = dishMapper.selectById(id);
        //根据id查询口味
        List<DishFlavor> flavors = dishFlavorMapper.getByDishId(id);
        //将查询到的菜品和口味封装到DishVO中
        DishVO dishVO=new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(flavors);
        return dishVO;
     }
     //根据id修改菜品
     @Transactional
     public void updateWithFlavor(DishDTO dishDTO){
        //向菜品表更新一条数据
        Dish dish=new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.update(dish);
        //删除口味表中的数据
        dishFlavorMapper.deleteByDishId2(dishDTO.getId());
        //向口味表插入多条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors!=null&&flavors.size()>0){
            flavors.forEach(flavor -> {
                flavor.setDishId(dishDTO.getId());
            });

            dishFlavorMapper.inserBatch(flavors);


        }
     }
     //菜品起售停售
     @Transactional
     public void startOrStop(Integer status, Long id){
        //判断是否关联了套餐

        //如果关联了套餐，同样起售或停售套餐
         if (status == StatusConstant.DISABLE) {
             // 如果是停售操作，还需要将包含当前菜品的套餐也停售
             List<Long> dishIds = new ArrayList<>();
             dishIds.add(id);
             List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishesId(dishIds);
             if (setmealIds != null && setmealIds.size() > 0) {
                 for (Long setmealId : setmealIds) {
                     Setmeal setmeal = Setmeal.builder()
                             .id(setmealId)
                             .status(StatusConstant.DISABLE)
                             .build();
                     setmealMapper.update(setmeal);
                 }
             }
         }
        //更新菜品状态
        //根据id查询菜品
        Dish dish = dishMapper.selectById(id);
        dish.setStatus(status);
        dishMapper.update(dish);
     }
      //根据分类id查询菜品
      public List<Dish> list(Long categoryId) {
          Dish dish = Dish.builder()
                  .categoryId(categoryId)
                  .status(StatusConstant.ENABLE)
                  .build();
          return dishMapper.list(dish);
      }
    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }

}
