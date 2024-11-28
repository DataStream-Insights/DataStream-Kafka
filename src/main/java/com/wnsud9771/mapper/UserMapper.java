package com.wnsud9771.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {
	@Insert("INSERT INTO filter_record (format_id, filter_id) VALUES (#{formatId}, #{filterId})")
	void save(String formatId, String filterId);
	 	
	@Update("UPDATE campaign_topic ct " +
            "JOIN pipelines p ON ct.pipelines_id = p.id " +
            "SET ct.consumer = #{consumer,jdbcType=BOOLEAN}, " +
            "    ct.topic_name = #{topicName} " +
            "WHERE p.pipeline_id = #{pipelineId} " +
            "AND ct.campaign_id = #{campaignId}")
	    int updateCampaignTopicConsumerAndName(@Param("pipelineId") String pipelineId,
	                                         @Param("campaignId") String campaignId,
	                                         @Param("consumer") Boolean consumer,
	                                         @Param("topicName") String topicName);
	 
	@Update("UPDATE format_topic fmt " +
            "JOIN campaign_topic ct ON fmt.campaign_topic_id = ct.id " +
            "JOIN pipelines p ON ct.pipelines_id = p.id " +
            "SET fmt.consumer = #{consumer,jdbcType=BOOLEAN}, " +
            "    fmt.topic_name = #{topicName} " +
            "WHERE p.pipeline_id = #{pipelineId} " +
            "AND ct.campaign_id = #{campaignId} " +
            "AND fmt.format_id = #{formatId}")
	    int updateFormatTopicConsumerAndName(@Param("pipelineId") String pipelineId,
	                                         @Param("campaignId") String campaignId,
	                                         @Param("formatId") String formatId,
	                                         @Param("consumer") Boolean consumer,
	                                         @Param("topicName") String topicName);
	 
	 @Update("UPDATE filter_topic ft " +
	            "JOIN format_topic fmt ON ft.format_topic_id = fmt.id " +
	            "JOIN campaign_topic ct ON fmt.campaign_topic_id = ct.id " +
	            "JOIN pipelines p ON ct.pipelines_id = p.id " +
	            "SET ft.consumer = #{consumer,jdbcType=BOOLEAN}, " +
	            "    ft.topic_name = #{topicName} " +
	            "WHERE p.pipeline_id = #{pipelineId} " +
	            "AND fmt.format_id = #{formatId} " +
	            "AND ft.filter_id = #{filterId}")
	    int updateFilterTopicConsumerAndName(@Param("pipelineId") String pipelineId,
	                                         @Param("formatId") String formatId,
	                                         @Param("filterId") String filterId,
	                                         @Param("consumer") Boolean consumer,
	                                         @Param("topicName") String topicName);
	 
	 //----------------------------------------------------------------------------------------------------------------------------------------//
	 
	 
	 @Update("UPDATE campaign_topic ct " +
	            "JOIN pipelines p ON ct.pipelines_id = p.id " +
	            "SET ct.consumer = #{consumer,jdbcType=BOOLEAN} " +
	            "WHERE p.pipeline_id = #{pipelineId} " +
	            "AND ct.campaign_id = #{campaignId}")
		    int updateCampaignTopicConsumer(@Param("pipelineId") String pipelineId,
		                                         @Param("campaignId") String campaignId,
		                                         @Param("consumer") Boolean consumer);
		 
		@Update("UPDATE format_topic fmt " +
	            "JOIN campaign_topic ct ON fmt.campaign_topic_id = ct.id " +
	            "JOIN pipelines p ON ct.pipelines_id = p.id " +
	            "SET fmt.consumer = #{consumer,jdbcType=BOOLEAN} " +
	            "WHERE p.pipeline_id = #{pipelineId} " +
	            "AND ct.campaign_id = #{campaignId} " +
	            "AND fmt.format_id = #{formatId}")
		    int updateFormatTopicConsumer(@Param("pipelineId") String pipelineId,
		                                         @Param("campaignId") String campaignId,
		                                         @Param("formatId") String formatId,
		                                         @Param("consumer") Boolean consumer);
		 
		 @Update("UPDATE filter_topic ft " +
		            "JOIN format_topic fmt ON ft.format_topic_id = fmt.id " +
		            "JOIN campaign_topic ct ON fmt.campaign_topic_id = ct.id " +
		            "JOIN pipelines p ON ct.pipelines_id = p.id " +
		            "SET ft.consumer = #{consumer,jdbcType=BOOLEAN} " +
		            "WHERE p.pipeline_id = #{pipelineId} " +
		            "AND fmt.format_id = #{formatId} " +
		            "AND ft.filter_id = #{filterId}")
		    int updateFilterTopicConsumer(@Param("pipelineId") String pipelineId,
		                                         @Param("formatId") String formatId,
		                                         @Param("filterId") String filterId,
		                                         @Param("consumer") Boolean consumer);
}
