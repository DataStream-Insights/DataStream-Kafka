package com.wnsud9771.dto.format.parsing;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LogItemDTO { // 파싱한 로그의 필드와 밸류 반환하는 dto
	private String name; // format field의 필드명
	private String value; // 아이템 컨텐츠 예시 == formatfiled의 아이템 컨텐츠 예시
	private String path; // 현재 노드의 전체 경로
	private boolean hasChild; // 하위 노드 존재 여부
}
