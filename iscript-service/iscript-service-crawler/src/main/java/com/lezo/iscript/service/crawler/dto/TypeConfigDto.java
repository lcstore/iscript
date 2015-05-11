package com.lezo.iscript.service.crawler.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import com.lezo.iscript.utils.JSONUtils;

public class TypeConfigDto {
	public static final String TASKER_COMMON = "common";
	public static final int TYPE_ENABLE = 1;
	public static final int TYPE_DISABLE = 0;
	private static final String KEY_INCLUDE = "include";
	private static final String KEY_EXCLUDE = "exclude";
	/**
	 * range:"1-5,10-20"
	 */
	private static final String KEY_RANGE = "range";
	/**
	 * list:"1,2,3,4,5"
	 */
	private static final String KEY_LIST = "list";
	private Long id;
	private String type;
	private String tasker;
	private int minSize;
	private int maxSize;
	private int status;
	private Date createTime;
	private Date updateTime;

	private String levelLang;
	private List<Integer> levelDescList;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTasker() {
		return tasker;
	}

	public void setTasker(String tasker) {
		this.tasker = tasker;
	}

	public int getMinSize() {
		return minSize;
	}

	public void setMinSize(int minSize) {
		this.minSize = minSize;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getLevelLang() {
		return levelLang;
	}

	public void setLevelLang(String levelLang) {
		this.levelLang = levelLang;
	}

	public List<Integer> getLevelDescList() {
		if (levelDescList != null) {
			return levelDescList;
		}
		JSONObject lObject = StringUtils.isBlank(getLevelLang()) ? null : JSONUtils.getJSONObject(getLevelLang());
		JSONObject inObject = lObject == null ? null : JSONUtils.getJSONObject(lObject, KEY_INCLUDE);
		JSONObject exObject = lObject == null ? null : JSONUtils.getJSONObject(lObject, KEY_EXCLUDE);
		Set<Integer> levelSet = new HashSet<Integer>();
		Set<Integer> includeSet = toLevelSet(inObject);
		Set<Integer> excludeSet = toLevelSet(exObject);
		levelSet.addAll(includeSet);
		levelSet.removeAll(excludeSet);
		if (levelSet.isEmpty()) {
			// LEVEL_MIN = 1
			levelSet.add(1);
		}
		levelDescList = new ArrayList<Integer>(levelSet);
		Collections.sort(levelDescList, new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				return o2.compareTo(o1);
			}
		});
		return levelDescList;
	}

	private Set<Integer> toLevelSet(JSONObject dataObject) {
		if (dataObject == null) {
			return java.util.Collections.emptySet();
		}
		String sRange = JSONUtils.getString(dataObject, KEY_RANGE);
		Set<Integer> rangeSet = toRangeSet(sRange);
		String sList = JSONUtils.getString(dataObject, KEY_LIST);
		Set<Integer> listSet = toListSet(sList);
		rangeSet.addAll(listSet);
		return rangeSet;
	}

	private Set<Integer> toListSet(String sList) {
		if (StringUtils.isBlank(sList)) {
			return Collections.emptySet();
		}
		Set<Integer> dataSet = new HashSet<Integer>();
		String[] sListArr = sList.split(",");
		if (sListArr != null) {
			for (String sValue : sListArr) {
				dataSet.add(Integer.valueOf(sValue.trim()));
			}
		}
		return dataSet;
	}

	private Set<Integer> toRangeSet(String sRange) {
		if (StringUtils.isBlank(sRange)) {
			return Collections.emptySet();
		}
		Set<Integer> dataSet = new HashSet<Integer>();
		String[] sRangeArr = sRange.split(",");
		for (String range : sRangeArr) {
			String[] rArr = range.split("-");
			if (rArr == null || rArr.length != 2) {
				Integer fromValue = Integer.valueOf(rArr[0].trim());
				Integer toValue = Integer.valueOf(rArr[1].trim());
				Integer maxValue = fromValue < toValue ? toValue : fromValue;
				Integer minValue = fromValue < toValue ? fromValue : toValue;
				for (int index = minValue; index <= maxValue; index++) {
					dataSet.add(index);
				}
			}
		}
		return dataSet;
	}
}
