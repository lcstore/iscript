package com.lezo.iscript.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

public interface TableSchemaDao {

	@Select("DESC ${tableName}")
	@ResultType(value = TableSchemaDto.class)
	List<TableSchemaDto> getTableSchemas(@Param("tableName") String tableName);
}
