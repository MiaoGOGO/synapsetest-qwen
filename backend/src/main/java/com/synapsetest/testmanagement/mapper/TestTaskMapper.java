package com.synapsetest.testmanagement.mapper;

import com.synapsetest.testmanagement.model.TestTask;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * TestTask MyBatis Mapper
 */
@Mapper
public interface TestTaskMapper {

    @Select("SELECT * FROM test_tasks WHERE id = #{id}")
    TestTask selectById(String id);

    @Select("SELECT * FROM test_tasks")
    List<TestTask> selectAll();

    @Select("SELECT * FROM test_tasks WHERE status = #{status}")
    List<TestTask> selectByStatus(String status);

    @Select("SELECT * FROM test_tasks WHERE environment = #{environment}")
    List<TestTask> selectByEnvironment(String environment);

    @Select("SELECT * FROM test_tasks WHERE created_by = #{createdBy}")
    List<TestTask> selectByCreatedBy(String createdBy);

    @Select("SELECT * FROM test_tasks WHERE status = #{status} ORDER BY priority DESC")
    List<TestTask> selectByStatusOrderByPriorityDesc(String status);

    @Insert("INSERT INTO test_tasks(id, name, description, environment, version, test_scope, status, " +
            "priority, created_at, updated_at, created_by) " +
            "VALUES(#{id}, #{name}, #{description}, #{environment}, #{version}, #{testScope}, #{status}, " +
            "#{priority}, #{createdAt}, #{updatedAt}, #{createdBy})")
    int insert(TestTask testTask);

    @Update("UPDATE test_tasks SET name=#{name}, description=#{description}, environment=#{environment}, " +
            "version=#{version}, test_scope=#{testScope}, status=#{status}, priority=#{priority}, " +
            "updated_at=#{updatedAt} WHERE id=#{id}")
    int update(TestTask testTask);

    @Delete("DELETE FROM test_tasks WHERE id = #{id}")
    int deleteById(String id);

    @Select("SELECT COUNT(*) FROM test_tasks")
    long count();
}

