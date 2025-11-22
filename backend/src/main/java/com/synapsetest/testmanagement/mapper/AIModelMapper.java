package com.synapsetest.testmanagement.mapper;

import com.synapsetest.testmanagement.model.AIModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * AIModel MyBatis Mapper
 * SQL statements are defined in AIModelMapper.xml
 */
@Mapper
public interface AIModelMapper {

    AIModel selectById(String id);

    List<AIModel> selectAll();

    List<AIModel> selectBySecurityStatus(String securityStatus);

    List<AIModel> selectByComplianceStatus(String complianceStatus);

    List<AIModel> selectByName(String name);

    List<AIModel> selectByVersion(String version);

    int insert(AIModel aiModel);

    int update(AIModel aiModel);

    int deleteById(String id);

    int count();
}

