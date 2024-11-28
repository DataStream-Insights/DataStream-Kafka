package com.wnsud9771.service.filtering;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wnsud9771.mapper.FilteringDataMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilteringSubmitService {
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final FilteringDataMapper filteringDataMapper;
	
	public void filtersubmitsuccessorfail(String pipelineId, String filteringdata, Long distinctCode) throws JsonMappingException, JsonProcessingException {
		
		//캠페인당
		if(distinctCode == 3) {
			
			//pipelineId로 pipelines의 id 찾고
			//데이터 하나 status 확인하고 적재
			//==3 이니까 중복제거 까지
			if(isSuccessOrFail(filteringdata).equals("SUCCESS")) {
				filteringSuccessTabledistinct(pipelineId, filteringdata, distinctCode);
				
				
			}else if(isSuccessOrFail(filteringdata).equals("FAIL") ) {
				
			}else {
				log.info("Status not found in log data");
			}
			
			
			
		}else if(distinctCode == 0L) {
			//pipelineId로 pipelines의 id 찾고
			//데이터 하나 status 확인하고 적재
			
			
			//status == SUCCESS 일떄 시간 먼저 변경하고 저장
			if(isSuccessOrFail(filteringdata).equals("SUCCESS")) {
				filteringSuccessTable(pipelineId, filteringdata);
				
				
			}else if(isSuccessOrFail(filteringdata).equals("FAIL") ) {
				
			}else {
				log.info("Status not found in log data");
			}
			
			
			//status == FAIL 일때 시간 먼저 변경하고 저장 
			
		}
		
		
		
	}
	
	//status 성공인지 실패인지 확인
	private String isSuccessOrFail(String logData) {
		Map<String, Object> logEntry;

        try {
            logEntry = objectMapper.readValue(logData, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            return "ERROR";
        }

        // status 키 처리
        if (logEntry.containsKey("status")) {
            String status = (String) logEntry.get("status");
            return status;
        } else {
            return "STATUSERROR";
        }
	}
	
	//중복제거 x
	private void filteringSuccessTable(String pipelineId, String filteringdata) throws JsonMappingException, JsonProcessingException{
		String filterdvalue = new String();
		
		JsonNode rootNode = objectMapper.readTree(filteringdata);
		
		Iterator<Entry<String, JsonNode>> fields = rootNode.fields();
        while (fields.hasNext()) {
            Entry<String, JsonNode> field = fields.next();
            String fieldName = field.getKey();
            if (!fieldName.equals("timestamp") && !fieldName.equals("status")) {
            	filterdvalue = field.getValue().asText();
            }
        }
		
        LocalDateTime localtime = changeKoreanTime(findtimestamp(filteringdata));
        
        Long id = filteringDataMapper.findPipelineIdByPipelineId(pipelineId);
        
        filteringDataMapper.insertFilteringData(localtime, filterdvalue, id);
	}
	
	//중복제거
	private void filteringSuccessTabledistinct(String pipelineId, String filteringdata, Long distinctCode) throws JsonMappingException, JsonProcessingException{
		String filterdvalue = new String();
		
		JsonNode rootNode = objectMapper.readTree(filteringdata);
		
		Iterator<Entry<String, JsonNode>> fields = rootNode.fields();
        while (fields.hasNext()) {
            Entry<String, JsonNode> field = fields.next();
            String fieldName = field.getKey();
            if (!fieldName.equals("timestamp") && !fieldName.equals("status")) {
            	filterdvalue = field.getValue().asText();
            }
        }
		
        LocalDateTime localtime = changeKoreanTime(findtimestamp(filteringdata));
        
        Long id = filteringDataMapper.findPipelineIdByPipelineId(pipelineId);
        
        filteringDataMapper.insertFilteringData(localtime, filterdvalue, id);
        filteringDataMapper.insertDistinctData(distinctCode, localtime, filterdvalue, pipelineId);
        filteringDataMapper.removeDuplicates();
        
	}
	
	//실패시 테이블 저장 중복제거x
	private void filteringFailTable() {
		
	}
	
	
	//데이트타임 바꾸는것.
	private LocalDateTime changeKoreanTime(String timestamp) {	
		ZonedDateTime utcDateTime = ZonedDateTime.parse(timestamp);
		ZonedDateTime seoulDateTime = utcDateTime.withZoneSameInstant(ZoneId.of("Asia/Seoul"));
		LocalDateTime localDateTime = seoulDateTime.toLocalDateTime();
		return localDateTime;
	}
	
	//----------------------------------------
	
	
	//tiemstamp찾기
	private String findtimestamp(String filteringdata) throws JsonMappingException, JsonProcessingException{
		JsonNode rootNode = objectMapper.readTree(filteringdata);
		String timestampValue = rootNode.get("timestamp").asText();
		return timestampValue;
	}
	
}
