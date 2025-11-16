package com.synapsetest.testmanagement.mapper;

import com.synapsetest.testmanagement.model.TestVersion;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * TestVersion MyBatis Mapper
 */
@Mapper
public interface TestVersionMapper {

    @Select("SELECT * FROM test_versions WHERE id = #{id}")
    TestVersion selectById(String id);

    @Select("SELECT * FROM test_versions")
    List<TestVersion> selectAll();

    @Select("SELECT * FROM test_versions WHERE name = #{name}")
    TestVersion selectByName(String name);

    @Select("SELECT * FROM test_versions WHERE product_version = #{productVersion}")
    List<TestVersion> selectByProductVersion(String productVersion);

    @Select("SELECT * FROM test_versions WHERE product_version = #{productVersion} AND name = #{name}")
    TestVersion selectByProductVersionAndName(@Param("productVersion") String productVersion, 
                                              @Param("name") String name);

    @Insert("INSERT INTO test_versions(id, name, product_version, description, release_date, config, " +
            "created_at, updated_at) " +
            "VALUES(#{id}, #{name}, #{productVersion}, #{description}, #{releaseDate}, " +
            "#{config, typeHandler=com.synapsetest.testmanagement.config.JsonTypeHandler}, #{createdAt}, #{updatedAt})")
    int insert(TestVersion testVersion);

    @Update("UPDATE test_versions SET name=#{name}, product_version=#{productVersion}, " +
            "description=#{description}, release_date=#{releaseDate}, " +
            "config=#{config, typeHandler=com.synapsetest.testmanagement.config.JsonTypeHandler}, " +
            "updated_at=#{updatedAt} WHERE id=#{id}")
    int update(TestVersion testVersion);

    @Delete("DELETE FROM test_versions WHERE id = #{id}")
    int deleteById(String id);

    @Select("SELECT COUNT(*) FROM test_versions")
    long count();
}

