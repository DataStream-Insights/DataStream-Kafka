package com.wnsud9771.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FormatMapper {
	@Select("SELECT fi.path " +
		       "FROM format_item fi " +
		       "JOIN format_set fs ON fs.formatitem_id = fi.id " +
		       "JOIN format_management fm ON fs.format_management_id = fm.id " +
		       "WHERE fm.formatid = #{formatId}")
    List<String> findPathsByFormatId(String formatId);
}
