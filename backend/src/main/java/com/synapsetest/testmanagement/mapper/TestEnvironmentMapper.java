package com.synapsetest.testmanagement.mapper;

import com.synapsetest.testmanagement.model.TestEnvironment;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * TestEnvironment MyBatis Mapper
 */
@Mapper
public interface TestEnvironmentMapper {

    @Select("SELECT * FROM test_environments WHERE id = #{id}")
    TestEnvironment selectById(String id);

    @Select("SELECT * FROM test_environments")
    List<TestEnvironment> selectAll();

    @Select("SELECT * FROM test_environments WHERE name = #{name}")
    TestEnvironment selectByName(String name);

    @Select("SELECT * FROM test_environments WHERE status = #{status}")
    List<TestEnvironment> selectByStatus(String status);

    @Insert("INSERT INTO test_environments(id, name, description, url, status, config, created_at, updated_at) " +
            "VALUES(#{id}, #{name}, #{description}, #{url}, #{status}, " +
            "#{config, typeHandler=com.synapsetest.testmanagement.config.JsonTypeHandler}, #{createdAt}, #{updatedAt})")
    int insert(TestEnvironment testEnvironment);

    @Update("UPDATE test_environments SET name=#{name}, description=#{description}, url=#{url}, " +
            "status=#{status}, config=#{config, typeHandler=com.synapsetest.testmanagement.config.JsonTypeHandler}, " +
            "updated_at=#{updatedAt} WHERE id=#{id}")
    int update(TestEnvironment testEnvironment);

    @Delete("DELETE FROM test_environments WHERE id = #{id}")
    int deleteById(String id);

    @Select("SELECT COUNT(*) FROM test_environments")
    long count();
}

