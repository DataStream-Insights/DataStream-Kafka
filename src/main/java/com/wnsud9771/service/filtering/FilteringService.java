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

	public JsonDTO filterSuccessOrFail(String formatLog, String filterManageId) {
		Map<String, Object> logEntry;
		try {
			logEntry = objectMapper.readValue(formatLog, new TypeReference<Map<String, Object>>() {
			});
		} catch (JsonMappingException e) {
			log.error("JSON mapping error: {}", e.getMessage());
			return new JsonDTO();
		} catch (JsonProcessingException e) {
			log.error("JSON processing error: {}", e.getMessage());
			return new JsonDTO();
		}

		List<FilterListDTO> conditions = getFilterConditions(filterManageId);

		FilterListDTO firstCondition = conditions.get(0);
		log.info("첫번째 필터링 조건: {}", firstCondition);

		// 실패한 필터 조건을 저장할 Map
		Map<String, Map<String, Object>> failedConditions = new HashMap<>();
		Map<String, Object> resultMap = new HashMap<>(logEntry); // 원본 로그 전체 복사

		// 첫 번째 조건 체크
		boolean result = matchCondition(logEntry, firstCondition.getPath(), firstCondition.getOperation(),
				firstCondition.getValue());

		if (!result) {
			addFailedCondition(failedConditions, firstCondition, logEntry);
		}

		// 나머지 조건들 체크
		for (int i = 1; i < conditions.size(); i++) {
			FilterListDTO condition = conditions.get(i);
			boolean matches = matchCondition(logEntry, condition.getPath(), condition.getOperation(),
					condition.getValue());

			if (!matches) {
				addFailedCondition(failedConditions, condition, logEntry);
			}

			result = switch (condition.getAndOr().toLowerCase()) {
			case "and" -> result && matches;
			case "or" -> result || matches;
			default -> throw new IllegalArgumentException("Invalid logical operator: " + condition.getAndOr());
			};

			if (!result && condition.getAndOr().equalsIgnoreCase("and")) {
				return createResponse(resultMap, false, failedConditions);
			}
		}

		return createResponse(resultMap, result, failedConditions);
	}

	private void addFailedCondition(Map<String, Map<String, Object>> failedConditions, FilterListDTO condition,
			Map<String, Object> logEntry) {
		Map<String, Object> detail = new HashMap<>();
		detail.put("actual", logEntry.getOrDefault(condition.getPath(), null));
		detail.put("filter", condition.getValue());
		detail.put("operation", condition.getOperation());
		String description = switch (condition.getOperation().toLowerCase()) {
		case "equals" -> "같지가 않음";
		case "not_equals" -> "같지 않아야 하는데 같음";
		case "greater_than" -> "초과가 아님";
		case "less_than" -> "미만이 아님";
		case "greater_equals" -> "이상이 아님";
		case "less_equals" -> "이하가 아님";
		default -> condition.getOperation();
		};
		detail.put("operation_description", description);

		failedConditions.put(condition.getPath(), detail);
	}

	private JsonDTO createResponse(Map<String, Object> logEntry, boolean success,
			Map<String, Map<String, Object>> failedConditions) {
		JsonDTO jsonDTO = new JsonDTO();
		try {
			Map<String, Object> resultMap = new HashMap<>(logEntry);
			resultMap.put("status", success ? "SUCCESS" : "FAIL");

			if (!success) {
				resultMap.put("failure_details", failedConditions);
			}

			jsonDTO.setJsonlog(objectMapper.writeValueAsString(resultMap));
		} catch (JsonProcessingException e) {
			log.error("Error creating response: {}", e.getMessage());
			return new JsonDTO();
		}
		return jsonDTO;
	}

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
			if (!logEntry.containsKey(key))
				return false;
			String logValue = String.valueOf(logEntry.get(key));

			return switch (operation.toLowerCase()) {
			case "equals" -> logValue.equals(value);
			case "not_equals" -> !logValue.equals(value);
			case "greater_than" -> Double.parseDouble(logValue) > Double.parseDouble(value);
			case "less_than" -> Double.parseDouble(logValue) < Double.parseDouble(value);
			case "greater_equals" -> Double.parseDouble(logValue) >= Double.parseDouble(value);
			case "less_equals" -> Double.parseDouble(logValue) <= Double.parseDouble(value);
			default -> throw new IllegalArgumentException("Unsupported operation: " + operation);
			};
		} catch (Exception e) {
			log.error("Error matching condition: key={}, operation={}, value={}, error={}", key, operation, value,
					e.getMessage());
			return false;
		}
	}
}