package com.wnsud9771.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface PipelineStatusMapper {

	@Select("SELECT CASE "
			+ "   WHEN COUNT(ft.id) > 0 AND COUNT(ft.id) = SUM(CASE WHEN ft.consumer = true THEN 1 ELSE 0 END) "
			+ "   THEN true " + "   ELSE false " + "END AS allFiltersConsumersTrue " + "FROM pipelines p "
			+ "JOIN campaign_topic ct ON p.id = ct.pipelines_id " + // 수정된 조인 조건
			"JOIN format_topic fmt ON ct.id = fmt.campaign_topic_id " + // 수정된 조인 조건
			"JOIN filter_topic ft ON fmt.id = ft.format_topic_id " + // 수정된 조인 조건
			"WHERE p.pipeline_id = #{pipelineId}")
	boolean areAllFilterConsumersTrue(String pipelineId);

	@Update("UPDATE pipelines SET status = #{status} WHERE pipeline_id = #{pipelineId}")
	int updatePipelineStatus(String pipelineId, boolean status);
	
	

	@Update("UPDATE pipelines SET status = false WHERE status = true")
	int resetAllPipelineStatus();

	@Update("UPDATE campaign_topic SET consumer = false WHERE consumer = true")
	int resetCampaignTopicStatus();

	@Update("UPDATE format_topic SET consumer = false WHERE consumer = true")
	int resetFormatTopicStatus();

	@Update("UPDATE filter_topic SET consumer = false WHERE consumer = true")
	int resetFilterTopicStatus();
}
