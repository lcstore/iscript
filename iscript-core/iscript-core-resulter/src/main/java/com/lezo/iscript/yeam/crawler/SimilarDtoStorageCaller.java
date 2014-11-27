package com.lezo.iscript.yeam.crawler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lezo.iscript.service.crawler.dto.SimilarDto;
import com.lezo.iscript.service.crawler.service.SimilarService;
import com.lezo.iscript.utils.JSONUtils;

@Service
public class SimilarDtoStorageCaller {
	private static Logger log = LoggerFactory.getLogger(SimilarDtoStorageCaller.class);
	@Autowired
	private SimilarService similarService;

	public void handleDtos(List<SimilarDto> similarDtos) {
		List<SimilarDto> insertDtos = new ArrayList<SimilarDto>();
		List<SimilarDto> updateDtos = new ArrayList<SimilarDto>();
		doAssort(similarDtos, insertDtos, updateDtos);
		similarService.batchInsertSimilarDtos(insertDtos);
		similarService.batchUpdateSimilarDtos(updateDtos);
		log.info("save simialer.insert:" + insertDtos.size() + ",update:" + updateDtos.size());
	}

	public void doAssort(List<SimilarDto> similarDtos, List<SimilarDto> insertDtos, List<SimilarDto> updateDtos) {
		Map<Integer, Set<String>> shopMap = new HashMap<Integer, Set<String>>();
		Map<String, SimilarDto> dtoMap = new HashMap<String, SimilarDto>();
		Map<Long, Set<String>> similarCodeKeyMap = new HashMap<Long, Set<String>>();
		for (SimilarDto dto : similarDtos) {
			String key = dto.getSiteId() + "-" + dto.getProductCode();
			dtoMap.put(key, dto);
			Set<String> codeSet = shopMap.get(dto.getSiteId());
			if (codeSet == null) {
				codeSet = new HashSet<String>();
				shopMap.put(dto.getSiteId(), codeSet);
			}
			Set<String> keySet = similarCodeKeyMap.get(dto.getSimilarCode());
			if (keySet == null) {
				keySet = new HashSet<String>();
				similarCodeKeyMap.put(dto.getSimilarCode(), keySet);
			}
			keySet.add(key);
			codeSet.add(dto.getProductCode());
		}
		for (Entry<Integer, Set<String>> entry : shopMap.entrySet()) {
			List<SimilarDto> hasDtos = similarService.getSimilarDtos(new ArrayList<String>(entry.getValue()),
					entry.getKey());
			Set<String> hasCodeSet = new HashSet<String>();
			Map<Long, Long> similarCodeMap = new HashMap<Long, Long>();
			for (SimilarDto oldDto : hasDtos) {
				String key = oldDto.getSiteId() + "-" + oldDto.getProductCode();
				SimilarDto newDto = dtoMap.get(key);
				hasCodeSet.add(oldDto.getProductCode());
				newDto.setId(oldDto.getId());
				// add similar map
				if (oldDto.getSimilarCode() != null && !newDto.getSimilarCode().equals(oldDto.getSimilarCode())) {
					similarCodeMap.put(newDto.getSimilarCode(), oldDto.getSimilarCode());
					newDto.setSimilarCode(oldDto.getSimilarCode());
				}
				handleEmptyFileds(newDto, oldDto);
				handleSource(newDto, oldDto);
				updateDtos.add(newDto);
			}
			for (String code : entry.getValue()) {
				if (hasCodeSet.contains(code)) {
					continue;
				}
				String key = entry.getKey() + "-" + code;
				SimilarDto newDto = dtoMap.get(key);
				insertDtos.add(newDto);
			}
			// update dto similarCode
			for (Entry<Long, Long> sEntry : similarCodeMap.entrySet()) {
				Set<String> keySet = similarCodeKeyMap.get(sEntry.getKey());
				if (keySet == null) {
					continue;
				}
				for (String key : keySet) {
					SimilarDto dto = dtoMap.get(key);
					dto.setSimilarCode(sEntry.getValue());
				}
			}

		}

	}

	private void handleSource(SimilarDto newDto, SimilarDto oldDto) {
		JSONObject srcObject = null;
		String content = oldDto.getSource();
		if (StringUtils.isEmpty(content)) {
			srcObject = new JSONObject();
		} else {
			if (content.indexOf(":") < 0) {
				srcObject = new JSONObject();
				JSONUtils.put(srcObject, content, 1);
			} else {
				if (!content.startsWith("{")) {
					content = "{" + content;
				}
				if (!content.endsWith("}")) {
					content += "}";
				}
				srcObject = JSONUtils.getJSONObject(content);
			}
		}
		Integer count = JSONUtils.getInteger(srcObject, newDto.getSource());
		if (count == null) {
			count = 0;
		}
		JSONUtils.put(srcObject, newDto.getSource(), count + 1);
		newDto.setSource(srcObject.toString());
	}

	private void handleEmptyFileds(SimilarDto newDto, SimilarDto oldDto) {
		if (StringUtils.isEmpty(newDto.getBarCode())) {
			newDto.setBarCode(oldDto.getBarCode());
		}
		if (StringUtils.isEmpty(newDto.getImgUrl())) {
			newDto.setImgUrl(oldDto.getImgUrl());
		}
		if (StringUtils.isEmpty(newDto.getProductName())) {
			newDto.setProductName(oldDto.getProductName());
		}
		if (newDto.getProductPrice() == null) {
			newDto.setProductPrice(oldDto.getProductPrice());
		}
	}

	public void setSimilarService(SimilarService similarService) {
		this.similarService = similarService;
	}

}
