package com.wnsud9771.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.wnsud9771.dto.filter.filtering.FilterConditionDTO;

@Mapper
public interface FilterMapper {
	@Select("SELECT DISTINCT fs.andor, o.operation, fi.path, fv.value " +
	        "FROM filter_set fs " +
	        "INNER JOIN filter_set_list fsl ON fs.filter_set_list_id = fsl.id " +
	        "INNER JOIN filter_management fm ON fsl.filtermanagement_id = fm.id " +
	        "INNER JOIN operation o ON fs.operation_id = o.id " +
	        "INNER JOIN format_item fi ON fs.format_item_id = fi.id " +
	        "INNER JOIN filtervalue fv ON fs.filtervalue_id = fv.id " +
	        "WHERE fm.filter_manage_id = #{filterManageId}")
	List<FilterConditionDTO> getFilterConditions(String filterManageId);
}
