package com.wnsud9771.service.format.parsing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wnsud9771.dto.format.parsing.LogValueDTO;
import com.wnsud9771.dto.format.parsing.ParsedLogDTO;
import com.wnsud9771.mapper.FormatMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FormatingService {//해당 포맷의 포맷팅할거 가져오고, 로그 파싱해서 다시 보내주는
	private final FormatMapper formatMapper;
	private final LogParsingService logParsingService;
	
	public ParsedLogDTO Formatinglog(String log,String formatId) {
		List<String> paths = formatMapper.findPathsByFormatId(formatId);
		
		//String parsinglog = new String();
		ParsedLogDTO parsedto = new ParsedLogDTO();
		parsedto = parsingLog(log,paths);
		
		return parsedto;
	}
	
	public ParsedLogDTO parsingLog(String logs,List<String> paths) {
		ParsedLogDTO dto = new ParsedLogDTO();
		List<String>path = paths;
		
		log.info("받은 logs: {}", logs);
		log.info("받은 path:{} ", paths);
		
		 ObjectMapper mapper = new ObjectMapper();
		 Map<String, String> resultMap = new HashMap<>();
		
		if(!path.isEmpty()) {
			List<LogValueDTO> values = logParsingService.extractValuesByPaths(logs,path);
		
		 for (LogValueDTO valueDto : values) {
	            resultMap.put(valueDto.getPath(), valueDto.getValue());
	        }
	        try {
	            // Map을 JSON 문자열로 변환
	            String jsonResult = mapper.writeValueAsString(resultMap);
	            dto.setParsedLog(jsonResult);
	            log.info("****포매팅 한 로그 {}", jsonResult);
	        } catch (JsonProcessingException e) {
	            throw new RuntimeException("포매팅 JSON 변환 중 오류가 발생했습니다.", e);
	        }
	    }
	
		return dto; 
	}
}
