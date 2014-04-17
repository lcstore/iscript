package com.lezo.iscript.yeam.tasker.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lezo.iscript.yeam.tasker.dto.TypeConfigDto;

public interface TypeConfigDao {
	void batchInsert(List<TypeConfigDto> dtoList);

	void batchStatusUpdate(@Param(value = "idList") List<Long> idList, @Param(value = "status") int status);

	List<TypeConfigDto> getTypeConfigDtos(@Param(value = "tasker") String tasker, @Param(value = "status") int status);
}
