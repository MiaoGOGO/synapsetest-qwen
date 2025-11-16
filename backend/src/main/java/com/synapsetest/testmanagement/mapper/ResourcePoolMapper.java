package com.synapsetest.testmanagement.mapper;

import com.synapsetest.testmanagement.model.ResourcePool;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * ResourcePool MyBatis Mapper
 */
@Mapper
public interface ResourcePoolMapper {

    @Select("SELECT * FROM resource_pools WHERE id = #{id}")
    ResourcePool selectById(String id);

    @Select("SELECT * FROM resource_pools")
    List<ResourcePool> selectAll();

    @Select("SELECT * FROM resource_pools WHERE name = #{name}")
    ResourcePool selectByName(String name);

    @Select("SELECT * FROM resource_pools WHERE status = #{status}")
    List<ResourcePool> selectByStatus(String status);

    @Select("SELECT * FROM resource_pools WHERE type = #{type}")
    List<ResourcePool> selectByType(String type);

    @Select("SELECT * FROM resource_pools WHERE status = #{status} AND type = #{type}")
    List<ResourcePool> selectByStatusAndType(@Param("status") String status, @Param("type") String type);

    @Insert("INSERT INTO resource_pools(id, name, type, status, capacity, allocated, location, " +
            "config, created_at, updated_at) " +
            "VALUES(#{id}, #{name}, #{type}, #{status}, #{capacity}, #{allocated}, #{location}, " +
            "#{config, typeHandler=com.synapsetest.testmanagement.config.JsonTypeHandler}, #{createdAt}, #{updatedAt})")
    int insert(ResourcePool resourcePool);

    @Update("UPDATE resource_pools SET name=#{name}, type=#{type}, status=#{status}, capacity=#{capacity}, " +
            "allocated=#{allocated}, location=#{location}, " +
            "config=#{config, typeHandler=com.synapsetest.testmanagement.config.JsonTypeHandler}, " +
            "updated_at=#{updatedAt} WHERE id=#{id}")
    int update(ResourcePool resourcePool);

    @Delete("DELETE FROM resource_pools WHERE id = #{id}")
    int deleteById(String id);

    @Select("SELECT COUNT(*) FROM resource_pools")
    long count();
}

