package com.wnsud9771.dto.filter.filtering;

import lombok.Data;

@Data
public class FilterConditionDTO {
	private String andOr;
	private String operation;
	private String path;
	private String value;
}
