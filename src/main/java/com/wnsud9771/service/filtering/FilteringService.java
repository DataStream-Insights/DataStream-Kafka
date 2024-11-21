package com.wnsud9771.service.filtering;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wnsud9771.dto.filter.JsonDTO;
import com.wnsud9771.dto.filter.filtering.FilterConditionDTO;
import com.wnsud9771.dto.filter.filtering.FilterListDTO;
import com.wnsud9771.mapper.FilterMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilteringService {
	private final FilterMapper filterMapper;
	private final ObjectMapper objectMapper = new ObjectMapper();

	// ---------------------------------(최종 필터링로그)----------------------------------------
	public JsonDTO filterSuccessOrFail(String formatLog, String filterManageId) {
		//Map<String, Object> logEntry = objectMapper.readValue(formatLog, new TypeReference<Map<String, Object>>() {});
		
		Map<String, Object> logEntry;
        try {
            logEntry = objectMapper.readValue(formatLog, new TypeReference<Map<String, Object>>() {});
        } catch (JsonMappingException e) {
            log.error("JSON mapping error: {}", e.getMessage());
            return new JsonDTO();
        } catch (JsonProcessingException e) {
            log.error("JSON processing error: {}", e.getMessage());
            return new JsonDTO();
        }
        
        Map<String, Object> resultMap = new HashMap<>();
        
        //필터관리 id 넣어서 필터링목록들 조회
        List<FilterListDTO> conditions = getFilterConditions(filterManageId);
        
        FilterListDTO firstConditionis = conditions.get(0);
        log.info("첫번째 필터링 조건1!!!!!!!!!!!!!!!!!!!!: {}", firstConditionis);
        
        //첫번째 핕터링 의 andor빼고 로그랑 매칭시켜 값들 반환
        FilterListDTO firstCondition = conditions.get(0);
        boolean result = matchCondition(logEntry, firstCondition.getPath(), 
                                     firstCondition.getOperation(), 
                                     firstCondition.getValue());
        
        if(result) {
            resultMap.put(firstCondition.getPath(), logEntry.get(firstCondition.getPath()));
        }
        
        for(int i = 1; i < conditions.size(); i++) {
            FilterListDTO condition = conditions.get(i);
            boolean matches = matchCondition(logEntry, condition.getPath(),  // path를 key로 사용
                                          condition.getOperation(), 
                                          condition.getValue());
            
         // 조건 만족시 해당 key-value 저장
            if(matches) {
                resultMap.put(condition.getPath(), logEntry.get(condition.getPath()));
            }
            
            // AND/OR 연산
            result = switch(condition.getAndOr().toLowerCase()) {
                case "and" -> result && matches;
                case "or" -> result || matches;
                default -> throw new IllegalArgumentException("Invalid logical operator: " + condition.getAndOr());
            };
            if(!result && condition.getAndOr().equalsIgnoreCase("and")) {
                return new JsonDTO();
            }
        }
        
        
        JsonDTO jsonDTO = new JsonDTO();
        if(result && !resultMap.isEmpty()) {
            try {
                jsonDTO.setJsonlog(objectMapper.writeValueAsString(resultMap));
            } catch (JsonProcessingException e) {
                log.error("Error converting result to JSON string: {}", e.getMessage());
                return new JsonDTO();
            }
        }
        
        
        return jsonDTO;
        
	}

	// ------------------------------------------------------------------------------------------------------------------------

	public List<FilterListDTO> getFilterConditions(String filterManageId) {
		List<FilterConditionDTO> conditions = filterMapper.getFilterConditions(filterManageId);
		return conditions.stream().map(this::convertToDto).collect(Collectors.toList());
	}
	

	private FilterListDTO convertToDto(FilterConditionDTO dbdto) {
		FilterListDTO dto = new FilterListDTO();

		dto.setAndOr(dbdto.getAndOr());
		dto.setOperation(dbdto.getOperation());
		dto.setPath(dbdto.getPath());
		dto.setValue(dbdto.getValue());
		return dto;
	}
	
	private boolean matchCondition(Map<String, Object> logEntry, String key, String operation, String value) {
	       try {
	           if(!logEntry.containsKey(key)) return false;
	           String logValue = String.valueOf(logEntry.get(key));
	           
	           return switch(operation.toLowerCase()) {
	               case "equals" -> logValue.equals(value);
	               case "not_equals" -> !logValue.equals(value);
	               case "greater_than" -> Double.parseDouble(logValue) > Double.parseDouble(value);
	               case "less_than" -> Double.parseDouble(logValue) < Double.parseDouble(value);
	               case "greater_equals" -> Double.parseDouble(logValue) >= Double.parseDouble(value);
	               case "less_equals" -> Double.parseDouble(logValue) <= Double.parseDouble(value);
	               default -> throw new IllegalArgumentException("Unsupported operation: " + operation);
	           };
	       } catch(Exception e) {
	           log.error("Error matching condition: key={}, operation={}, value={}, error={}", 
	                     key, operation, value, e.getMessage());
	           return false;
	       }
	   }
	
	 // 결과를 JSON 문자열로 변환하는 메서드 (필요한 경우)
	   public String convertResultToJson(Map<String, Object> result) {
	       try {
	           return objectMapper.writeValueAsString(result);
	       } catch (Exception e) {
	           log.error("Error converting result to JSON: {}", e.getMessage());
	           return "{}";
	       }
	   }
}
