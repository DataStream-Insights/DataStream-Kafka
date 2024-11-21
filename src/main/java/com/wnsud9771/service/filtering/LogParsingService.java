//package com.wnsud9771.service.filtering;
//
//import java.util.Map;
//
//import org.springframework.stereotype.Service;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//@Service
//public class LogParsingService {
//	
//	ObjectMapper mapper = new ObjectMapper();
//	Map<String, String> map = mapper.readValue(jsonString, Map.class);
//	
//	for (Map.Entry<String, String> entry : map.entrySet()) {
//	    String key = entry.getKey();
//	    String value = entry.getValue();
//	    String data = key + "=" + value;
//	    // data 처리 로직
//	}
//}
