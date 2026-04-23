package com.sky.mapper;

import com.sky.entity.SetmealDish;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    //根据菜id查套餐

    List<Long> getSetmealIdsByDishesId(List<Long> dishId);
    //根据套餐id更新套餐状态
    @Update("update setmeal set status=#{status} where id in (#{setmealIds})")
    void updateSetmealStatus(Integer status, List<Long> setmealIds);

    void insertBatch(List<SetmealDish> setmealDishes);

    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
    void deleteBySetmealId(Long setmealId);
    //根据套餐id查询套餐和菜品的关联关系
    @Select("select * from setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> getBySetmealId(Long id);
}
