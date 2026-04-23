package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
//向口味表批量插入多条数据

    void inserBatch(List<DishFlavor> flavors);
    //根据菜品id批量删除口味数据
    void deleteByDishId(List<Long> dishids);
    //根据id删除口味数据
    @Delete("delete from dish_flavor where dish_id = #{id}")
    void deleteByDishId2(Long id);

    //根据菜品id查询口味数据
    @Select("select * from dish_flavor where dish_id = #{dishid}")
    List<DishFlavor> getByDishId(Long dishid);
}
