package com.wnsud9771.service.format.parsing;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PreLogProcessService {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public String preprocessJson(String json) {
        try {
            // 입력값이 JSON 형식인지 기본 검증
            if (!json.trim().startsWith("{") || !json.trim().endsWith("}")) {
                log.warn("Invalid JSON format: Not a JSON object");
                return "{}";
            }

            StringBuilder processedJson = new StringBuilder("{");
            boolean firstPair = true;

            // JSON의 키-값 쌍을 찾는 패턴
            // value가 { }로 시작하는 객체이거나 " "로 감싸진 문자열인 경우만 매칭
            Pattern pattern = Pattern.compile("\"([^\"]+)\"\\s*:\\s*(\\{[^}]+\\}|\"[^\"]*\")(?=\\s*,|\\s*})");
            Matcher matcher = pattern.matcher(json);

            while (matcher.find()) {
                String key = matcher.group(1);
                String value = matcher.group(2);

                // value 처리
                String processedValue = processValue(value);
                if (processedValue != null) {
                    if (!firstPair) {
                        processedJson.append(",");
                    }
                    processedJson.append("\"").append(key).append("\":").append(processedValue);
                    firstPair = false;
                }
            }

            processedJson.append("}");
            String result = processedJson.toString();

            // 최종 결과가 유효한 JSON인지 확인
            try {
                objectMapper.readTree(result);
                return result;
            } catch (Exception e) {
                log.error("Final JSON validation failed: {}", e.getMessage());
                return "{}";
            }

        } catch (Exception e) {
            log.error("JSON preprocessing failed: {}", e.getMessage());
            return "{}";
        }
    }

    private String processValue(String value) {
        try {
            if (value.startsWith("{")) {
                // 중첩된 JSON 객체인 경우 재귀적으로 처리
                return preprocessJson(value);
            } else if (value.startsWith("\"") && value.endsWith("\"")) {
                // 문자열 값인 경우 그대로 반환
                return value;
            } else {
                // 그 외의 경우는 무시
                return null;
            }
        } catch (Exception e) {
            log.error("Value processing failed: {}", e.getMessage());
            return null;
        }
    }
}