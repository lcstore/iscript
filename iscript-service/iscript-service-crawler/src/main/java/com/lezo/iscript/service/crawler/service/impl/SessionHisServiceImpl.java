package com.lezo.iscript.service.crawler.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lezo.iscript.service.crawler.dao.SessionHisDao;
import com.lezo.iscript.service.crawler.dto.SessionHisDto;
import com.lezo.iscript.service.crawler.service.SessionHisService;
import com.lezo.iscript.utils.BatchIterator;

@Service
public class SessionHisServiceImpl implements SessionHisService {
	private static Logger logger = LoggerFactory.getLogger(SessionHisServiceImpl.class);
	@Autowired
	private SessionHisDao sessionHisDao;

	@Override
	public void batchInsertSessionHisDtos(List<SessionHisDto> dtoList) {
		BatchIterator<SessionHisDto> it = new BatchIterator<SessionHisDto>(dtoList);
		while (it.hasNext()) {
			sessionHisDao.batchInsert(it.next());
		}
	}

	@Override
	public void batchUpdateSessionHisDtos(List<SessionHisDto> dtoList) {
		BatchIterator<SessionHisDto> it = new BatchIterator<SessionHisDto>(dtoList);
		while (it.hasNext()) {
			sessionHisDao.batchUpdate(it.next());
		}
	}

	@Override
	public List<SessionHisDto> getSessionHisDtos(List<String> sessionIds) {
		List<SessionHisDto> dtoList = new ArrayList<SessionHisDto>();
		BatchIterator<String> it = new BatchIterator<String>(sessionIds);
		while (it.hasNext()) {
			List<SessionHisDto> subList = sessionHisDao.getSessionHisDtos(it.next());
			if (CollectionUtils.isNotEmpty(subList)) {
				dtoList.addAll(subList);
			}
		}
		return dtoList;
	}

	public void setSessionHisDao(SessionHisDao sessionHisDao) {
		this.sessionHisDao = sessionHisDao;
	}

	@Override
	public void batchSaveSessionHisDtos(List<SessionHisDto> dtoList) {
		if (CollectionUtils.isEmpty(dtoList)) {
			return;
		}
		List<SessionHisDto> insertDtos = new ArrayList<SessionHisDto>();
		List<SessionHisDto> updateDtos = new ArrayList<SessionHisDto>();
		doAssort(dtoList, insertDtos, updateDtos);
		batchInsertSessionHisDtos(insertDtos);
		batchUpdateSessionHisDtos(updateDtos);
		logger.info("save SessionHisDto.insert:" + insertDtos.size() + ",update:" + updateDtos.size());
	}

	private void doAssort(List<SessionHisDto> SessionHisDtos, List<SessionHisDto> insertDtos,
			List<SessionHisDto> updateDtos) {
		Map<String, SessionHisDto> dtoMap = new HashMap<String, SessionHisDto>();
		for (SessionHisDto dto : SessionHisDtos) {
			String key = dto.getSessionId();
			SessionHisDto hasDto = dtoMap.get(key);
			if (hasDto == null) {
				dtoMap.put(key, dto);
			} else if (dto.getUpdateTime().after(hasDto.getUpdateTime())) {
				dtoMap.put(key, dto);
			}
		}
		List<String> keyList = new ArrayList<String>(dtoMap.keySet());

		List<SessionHisDto> hasDtos = getSessionHisDtos(keyList);
		Set<String> hasCodeSet = new HashSet<String>();
		for (SessionHisDto oldDto : hasDtos) {
			String key = oldDto.getSessionId();
			hasCodeSet.add(key);
			SessionHisDto newDto = dtoMap.get(key);
			if (newDto != null) {
				newDto.setId(oldDto.getId());
				updateDtos.add(newDto);
			}
		}
		for (Entry<String, SessionHisDto> entry : dtoMap.entrySet()) {
			if (hasCodeSet.contains(entry.getKey())) {
				continue;
			}
			SessionHisDto newDto = entry.getValue();
			newDto.setStatus(SessionHisDto.STATUS_UP);
			insertDtos.add(newDto);
		}
	}

	@Override
	public void updateUpSessionToInterrupt() {
		sessionHisDao.updateUpSessionToInterrupt();
	}

	@Override
	public List<SessionHisDto> getSessionHisDtosByUpdateTime(Date afterUpdateTime) {
		return sessionHisDao.getSessionHisDtosByUpdateTime(afterUpdateTime);
	}

}
