package com.lezo.iscript.service.crawler.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lombok.extern.log4j.Log4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lezo.iscript.common.UnifyValueUtils;
import com.lezo.iscript.service.crawler.dao.SimilarDao;
import com.lezo.iscript.service.crawler.dto.SimilarDto;
import com.lezo.iscript.service.crawler.service.SimilarService;
import com.lezo.iscript.utils.BatchIterator;
import com.lezo.iscript.utils.JSONUtils;

@Log4j
@Service
public class SimilarServiceImpl implements SimilarService {
	@Autowired
	private SimilarDao similarDao;

	@Override
	public void batchInsertSimilarDtos(List<SimilarDto> dtoList) {
		BatchIterator<SimilarDto> it = new BatchIterator<SimilarDto>(dtoList);
		while (it.hasNext()) {
			similarDao.batchInsert(it.next());
		}
	}

	@Override
	public void batchUpdateSimilarDtos(List<SimilarDto> dtoList) {
		BatchIterator<SimilarDto> it = new BatchIterator<SimilarDto>(dtoList);
		while (it.hasNext()) {
			similarDao.batchUpdate(it.next());
		}
	}

	@Override
	public List<SimilarDto> getSimilarDtoByProductCodes(Integer siteId, List<String> codeList) {
		if (siteId == null || siteId <= 0 || CollectionUtils.isEmpty(codeList)) {
			return Collections.emptyList();
		}
		List<SimilarDto> dtoList = new ArrayList<SimilarDto>();
		BatchIterator<String> it = new BatchIterator<String>(codeList);
		while (it.hasNext()) {
			List<SimilarDto> subList = similarDao.getSimilarDtoByProductCodes(siteId, it.next());
			if (CollectionUtils.isNotEmpty(subList)) {
				dtoList.addAll(subList);
			}
		}
		return dtoList;
	}

	public void setSimilarDao(SimilarDao similarDao) {
		this.similarDao = similarDao;
	}

	@Override
	public List<SimilarDto> getSimilarDtoBySimilarCodes(List<Long> codeList) {
		if (CollectionUtils.isEmpty(codeList)) {
			return Collections.emptyList();
		}
		List<SimilarDto> dtoList = new ArrayList<SimilarDto>();
		BatchIterator<Long> it = new BatchIterator<Long>(codeList);
		while (it.hasNext()) {
			List<SimilarDto> subList = similarDao.getSimilarDtoBySimilarCodes(it.next());
			if (CollectionUtils.isNotEmpty(subList)) {
				dtoList.addAll(subList);
			}
		}
		return dtoList;
	}

	@Override
	public void batchSaveSimilarDtos(List<SimilarDto> dtoList) {
		if (CollectionUtils.isEmpty(dtoList)) {
			return;
		}
		UnifyValueUtils.unifyQuietly(dtoList);
		long start = System.currentTimeMillis();
		List<SimilarDto> insertList = new ArrayList<SimilarDto>();
		List<SimilarDto> updateList = new ArrayList<SimilarDto>();
		doAssort(dtoList, insertList, updateList);
		batchInsertSimilarDtos(insertList);
		batchUpdateSimilarDtos(updateList);
		long cost = System.currentTimeMillis() - start;
		log.info("save similarDto:" + dtoList.size() + ",insert:" + insertList.size() + ",update:" + updateList.size()
				+ ",cost:" + cost);
	}

	private void doAssort(List<SimilarDto> dtoList, List<SimilarDto> insertList, List<SimilarDto> updateList) {
		Map<String, SimilarDto> keyDtoMap = new HashMap<String, SimilarDto>();
		Map<Integer, Set<String>> siteCodeMap = new HashMap<Integer, Set<String>>();
		for (SimilarDto dto : dtoList) {
			Set<String> codeSet = siteCodeMap.get(dto.getSiteId());
			if (codeSet == null) {
				codeSet = new HashSet<String>();
				siteCodeMap.put(dto.getSiteId(), codeSet);
			}
			codeSet.add(dto.getProductCode());
			String key = getDtoKey(dto);
			SimilarDto hasDto = keyDtoMap.get(key);
			if (hasDto == null || (hasDto.getWareType() < dto.getWareType())
					|| (hasDto.getConfirmModel() < dto.getConfirmModel())) {
				keyDtoMap.put(key, dto);
			}
		}
		for (Entry<Integer, Set<String>> entry : siteCodeMap.entrySet()) {
			Integer siteId = entry.getKey();
			List<String> codeList = new ArrayList<String>(entry.getValue());
			List<SimilarDto> hasList = getSimilarDtoByProductCodes(siteId, codeList);
			Map<Long, Long> new2OldSimilarMap = new HashMap<Long, Long>();
			Set<String> hasSet = new HashSet<String>();
			for (SimilarDto oldDto : hasList) {
				String key = getDtoKey(oldDto);
				hasSet.add(key);
				SimilarDto newDto = keyDtoMap.get(key);
				keepOrCoverValue(oldDto, newDto);
				if (oldDto.getSimilarCode() != null) {
					newDto.setSimilarCode(oldDto.getSimilarCode());
					new2OldSimilarMap.put(newDto.getSimilarCode(), oldDto.getSimilarCode());
				}
				updateList.add(newDto);
			}
			for (String code : codeList) {
				String key = siteId + "-" + code;
				if (hasSet.contains(key)) {
					continue;
				}
				SimilarDto newDto = keyDtoMap.get(key);
				Long oldSimilarCode = new2OldSimilarMap.get(newDto.getSimilarCode());
				if (oldSimilarCode != null) {
					newDto.setSimilarCode(oldSimilarCode);
				}
				insertList.add(newDto);
			}
		}

	}

	private void keepOrCoverValue(SimilarDto oldDto, SimilarDto newDto) {
		newDto.setId(oldDto.getId());
		if (StringUtils.isEmpty(newDto.getBarCode())) {
			newDto.setBarCode(oldDto.getBarCode());
		}
		if (oldDto.getWareCode() == null || oldDto.getWareType() > newDto.getWareType()) {
			newDto.setWareType(oldDto.getWareType());
			newDto.setWareCode(oldDto.getWareCode());
		}
		if (oldDto.getConfirmModel() > newDto.getConfirmModel()) {
			newDto.setConfirmModel(oldDto.getConfirmModel());
		}
		if (newDto.getIsStandard() == null || oldDto.getIsStandard() > newDto.getIsStandard()) {
			newDto.setIsStandard(oldDto.getIsStandard());
		}
		if (StringUtils.isNotBlank(oldDto.getCaption()) && StringUtils.isBlank(newDto.getCaption())) {
			newDto.setCaption(oldDto.getCaption());
		}
		if (StringUtils.isNotBlank(oldDto.getDeciderKvs())) {
			JSONObject kvObject = JSONUtils.getJSONObject(oldDto.getDeciderKvs());
			JSONObject newKvObject = JSONUtils.getJSONObject(newDto.getDeciderKvs());
			Iterator<?> it = newKvObject.keys();
			while (it.hasNext()) {
				String key = it.next().toString();
				Object value = JSONUtils.get(newKvObject, key);
				JSONUtils.put(kvObject, key, value);
			}
			newDto.setDeciderKvs(kvObject.toString());
		}
	}

	private String getDtoKey(SimilarDto dto) {
		return dto.getSiteId() + "-" + dto.getProductCode();
	}
}
