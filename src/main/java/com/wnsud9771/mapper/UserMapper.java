package com.wnsud9771.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {
	@Insert("INSERT INTO filter_record (format_id, filter_id) VALUES (#{formatId}, #{filterId})")
	void save(String formatId, String filterId);
	 
	@Update("UPDATE campaign_topic SET consumer = #{consumer,jdbcType=BOOLEAN}, topic_name = #{topicName} WHERE campaign_id = #{campaignId}")
	int updateConsumerByCampaignId(
	    @Param("campaignId") String campaignId, 
	    @Param("consumer") boolean consumer,
	    @Param("topicName") String topicName
	);

	@Update("UPDATE format_topic SET consumer = #{consumer,jdbcType=BOOLEAN}, topic_name = #{topicName} WHERE campaign_id = #{campaignId} AND format_id = #{formatId}")
	int updateConsumerByCampaignIdAndFormatId(
	    @Param("campaignId") String campaignId,
	    @Param("formatId") String formatId,
	    @Param("consumer") boolean consumer,
	    @Param("topicName") String topicName
	);

	@Update("UPDATE filter_topic SET consumer = #{consumer,jdbcType=BOOLEAN}, topic_name = #{topicName} WHERE format_id = #{formatId} AND filter_id = #{filterId}")
	int updateConsumerByFormatIdAndFilterId(
	    @Param("formatId") String formatId,
	    @Param("filterId") String filterId,
	    @Param("consumer") boolean consumer,
	    @Param("topicName") String topicName
	);
}
