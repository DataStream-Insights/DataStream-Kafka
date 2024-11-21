package com.wnsud9771.dto.format.parsing;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LogValueDTO {
	private String path;
    private String value;
}
