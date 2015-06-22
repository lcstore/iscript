package com.lezo.iscript.yeam.resultmgr.directory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;

import com.lezo.iscript.service.crawler.dto.DataTransferDto;
import com.lezo.iscript.service.crawler.service.DataTransferService;
import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.resultmgr.ExecutorUtils;

public class DirSummaryCacher {
	private static final Object LOCKER = new Object();
	private ConcurrentHashMap<String, DirSummary> summaryMap = new ConcurrentHashMap<String, DirSummary>();
	private static DirSummaryCacher instance = new DirSummaryCacher();

	private DirSummaryCacher() {

	}

	public static DirSummaryCacher getInstance() {
		return instance;
	}

	public DirSummary getDirStream(String dirKey) {
		return summaryMap.get(dirKey);
	}

	public void addDirStream(String dirKey, DirSummary dirStream) {
		summaryMap.put(dirKey, dirStream);
	}

	public DirSummary remove(String dirKey) {
		if (dirKey == null) {
			return null;
		}
		return summaryMap.remove(dirKey);
	}

	public Iterator<Entry<String, DirSummary>> iterator() {
		return summaryMap.entrySet().iterator();
	}

	public void fireEvent(DirMeta dirBean) {
		String key = dirBean.toDirKey();
		DirSummary hasStream = summaryMap.get(key);
		if (hasStream == null) {
			synchronized (LOCKER) {
				hasStream = summaryMap.get(key);
				if (hasStream == null) {
					hasStream = new DirSummary();
					hasStream.setDirBean(dirBean);
					hasStream.setFromStamp(dirBean.getCreateTime().getTime());
					// hasStream.setToStamp(toSuitStamp(hasStream.getFromStamp()));
					hasStream.setToStamp(0);
					addParams(hasStream);
					addDirStream(key, hasStream);
				}
			}
		}
		ExecutorUtils.getFileProduceExecutor().execute(new DirFileScanner(hasStream));
	}

	private void addParams(DirSummary hasStream) {
		String dataCode = toDataCode(hasStream.getDirBean().toDirKey());
		DataTransferService transferService = SpringBeanUtils.getBean(DataTransferService.class);
		List<String> codeList = new ArrayList<String>(1);
		codeList.add(dataCode);
		List<DataTransferDto> hasList = transferService.getDtoByCodeList(codeList);
		if (!hasList.isEmpty()) {
			DataTransferDto dto = hasList.get(0);
			JSONObject pObject = JSONUtils.getJSONObject(dto.getParams());
			if (pObject != null) {
				Iterator<?> it = pObject.keys();
				while (it.hasNext()) {
					String keyName = it.next().toString();
					Object value = JSONUtils.get(pObject, keyName);
					hasStream.getParamMap().put(keyName, value.toString());
				}
			}
		}
	}

	private String toDataCode(String key) {
		String code = "" + key.hashCode();
		return code.replace("-", "H");
	}

	private long toSuitStamp(long currentMills) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(currentMills);
		if (0 == c.get(Calendar.HOUR_OF_DAY) && c.get(Calendar.MINUTE) < 30) {
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
		} else {
			c.add(Calendar.MINUTE, -15);
			c.set(Calendar.SECOND, 0);
		}
		return c.getTimeInMillis();
	}

}
