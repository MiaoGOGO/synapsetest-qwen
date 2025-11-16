package com.synapsetest.testmanagement.mapper;

import com.synapsetest.testmanagement.model.TestCase;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * TestCase MyBatis Mapper
 */
@Mapper
public interface TestCaseMapper {

    @Select("SELECT * FROM test_cases WHERE id = #{id}")
    TestCase selectById(String id);

    @Select("SELECT * FROM test_cases")
    List<TestCase> selectAll();

    @Select("SELECT * FROM test_cases WHERE type = #{type}")
    List<TestCase> selectByType(String type);

    @Select("SELECT * FROM test_cases WHERE status = #{status}")
    List<TestCase> selectByStatus(String status);

    @Select("SELECT * FROM test_cases WHERE related_requirement = #{relatedRequirement}")
    List<TestCase> selectByRelatedRequirement(String relatedRequirement);

    @Select("SELECT * FROM test_cases WHERE created_by = #{createdBy}")
    List<TestCase> selectByCreatedBy(String createdBy);

    @Select("SELECT * FROM test_cases WHERE status = #{status} ORDER BY priority DESC")
    List<TestCase> selectByStatusOrderByPriorityDesc(String status);

    @Insert("INSERT INTO test_cases(id, title, description, type, status, priority, expected_result, " +
            "related_requirement, steps, tags, created_at, updated_at, created_by) " +
            "VALUES(#{id}, #{title}, #{description}, #{type}, #{status}, #{priority}, #{expectedResult}, " +
            "#{relatedRequirement}, #{steps, typeHandler=com.synapsetest.testmanagement.config.JsonTypeHandler}, " +
            "#{tags, typeHandler=com.synapsetest.testmanagement.config.JsonTypeHandler}, #{createdAt}, #{updatedAt}, #{createdBy})")
    int insert(TestCase testCase);

    @Update("UPDATE test_cases SET title=#{title}, description=#{description}, type=#{type}, " +
            "status=#{status}, priority=#{priority}, expected_result=#{expectedResult}, " +
            "related_requirement=#{relatedRequirement}, " +
            "steps=#{steps, typeHandler=com.synapsetest.testmanagement.config.JsonTypeHandler}, " +
            "tags=#{tags, typeHandler=com.synapsetest.testmanagement.config.JsonTypeHandler}, " +
            "updated_at=#{updatedAt} WHERE id=#{id}")
    int update(TestCase testCase);

    @Delete("DELETE FROM test_cases WHERE id = #{id}")
    int deleteById(String id);

    @Select("SELECT COUNT(*) FROM test_cases")
    long count();
}

