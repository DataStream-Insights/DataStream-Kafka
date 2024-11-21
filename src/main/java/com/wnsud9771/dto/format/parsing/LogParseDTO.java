package com.wnsud9771.dto.format.parsing;

import lombok.Data;

@Data
public class LogParseDTO { // log 파싱 형식맞추기위한 dto
	private String log;
	private int startdepth;
	private int enddepth;
}
