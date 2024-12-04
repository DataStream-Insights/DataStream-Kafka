package com.wnsud9771.service.be;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.wnsud9771.dto.pipeline.PipelineIdDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BesendService {
	private final RestTemplate restTemplate;
	
	@Value("${beaddress}")
	private String beaddress;
	
	public void sendbeanddb(String pipelineid) {
		PipelineIdDTO dto = new PipelineIdDTO();
		dto.setPipelineid(pipelineid);
		
		try {
	 		restTemplate.postForObject(beaddress+"/sendkafka/getsuc", dto, PipelineIdDTO.class);	 		
	 	}catch (Exception e){
	 		log.info("필터링후 각자 테이블에 저장할려고 파이프라인id:{} be에 보내지 못함 ",dto);
	 	}
	}
}
