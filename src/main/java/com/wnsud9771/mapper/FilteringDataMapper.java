package com.wnsud9771.mapper;

import java.time.LocalDateTime;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface FilteringDataMapper {
	@Select("SELECT id FROM pipelines WHERE pipeline_id = #{pipelineId}")
	Long findPipelineIdByPipelineId(String pipelineId);

	@Insert("INSERT INTO filtering_data (timestamp, data, pipelines_id) "
			+ "VALUES (#{timestamp}, #{data}, #{pipelinesId})")
	void insertFilteringData(@Param("timestamp") LocalDateTime localtime, @Param("data") String data,
			@Param("pipelinesId") Long pipelinesId);

	@Insert("INSERT INTO distinct_data (olpp_code, timestamp, pipelines_id, data) "
			+ "VALUES (#{olppCode}, #{timestamp}, #{pipelinesId}, #{data})")
	void insertDistinctData(@Param("olppCode") Long olppCode, @Param("timestamp") LocalDateTime localtime,
			@Param("data") String data, @Param("pipelinesId") String pipelinesId);

	@Update("WITH ranked_data AS (" + "    SELECT id, pipelines_id, data, timestamp, "
			+ "           ROW_NUMBER() OVER (PARTITION BY pipelines_id, data ORDER BY timestamp DESC) as rn "
			+ "    FROM distinct_data" + ") " + "DELETE FROM distinct_data "
			+ "WHERE id IN (SELECT id FROM ranked_data WHERE rn > 1)")
	int removeDuplicates();

	
	@Insert("INSERT INTO fail_filtering_data (timestamp, data, fail_reason, pipelines_id) "
			+ "VALUES (#{timestamp}, #{data}, #{failReason}, "
			+ "(SELECT id FROM pipelines WHERE pipeline_id = #{pipelineId}))")
	void insertFailFilteringData(LocalDateTime timestamp, String data, String failReason, String pipelineId);
}
