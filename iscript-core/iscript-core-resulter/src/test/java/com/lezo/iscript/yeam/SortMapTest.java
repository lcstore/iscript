package com.lezo.iscript.yeam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONObject;
import org.junit.Test;

public class SortMapTest {

	@Test
	public void doSort() {
		List<SortDto> dtoList = getSortDtos();
		Map<String, List<SortDto>> typeKeyMap = new HashMap<String, List<SortDto>>();
		for (SortDto dto : dtoList) {
			String key = "" + dto.getSiteId() + dto.getIsDelete() + dto.getType();
			List<SortDto> typeKeyList = typeKeyMap.get(key);
			if (typeKeyList == null) {
				typeKeyList = new ArrayList<SortDto>();
				typeKeyMap.put(key, typeKeyList);
			}
			typeKeyList.add(dto);
		}
		Map<Integer, SiteSortVo> siteMap = new HashMap<Integer, SiteSortVo>();
		for (Entry<String, List<SortDto>> entry : typeKeyMap.entrySet()) {
			SortDto sortDto = entry.getValue().get(0);
			SiteSortVo ssVo = siteMap.get(sortDto.getSiteId());
			if (ssVo == null) {
				ssVo = new SiteSortVo();
				ssVo.setSiteId(sortDto.getSiteId());
				siteMap.put(sortDto.getSiteId(), ssVo);
			}
			TypeSortVo typeVo = new TypeSortVo();
			typeVo.setType(sortDto.getType());
			for (SortDto sDto : entry.getValue()) {
				typeVo.getDtoList().add(sDto);
			}
			if (sortDto.getIsDelete() == 1) {
				ssVo.getdList().add(typeVo);
			} else {
				ssVo.getoList().add(typeVo);
			}
		}
		List<SiteSortVo> destList = new ArrayList<SiteSortVo>(siteMap.values());
		JSONObject jObject = new JSONObject(destList);
		System.out.println(destList.size());
		System.out.println(jObject);
	}

	private List<SortDto> getSortDtos() {
		List<SortDto> dtoList = new ArrayList<SortDto>();
		SortDto dto = new SortDto();
		dto.setSiteId(1);
		dto.setIsDelete(1);
		dto.setType(1);
		dto.setShopName("111");
		dtoList.add(dto);
		dto = new SortDto();
		dto.setSiteId(1);
		dto.setIsDelete(0);
		dto.setType(1);
		dto.setShopName("101");
		dtoList.add(dto);
		dto = new SortDto();
		dto.setSiteId(1);
		dto.setIsDelete(0);
		dto.setType(2);
		dto.setShopName("102");
		dtoList.add(dto);
		dto = new SortDto();
		dto.setSiteId(1);
		dto.setIsDelete(1);
		dto.setType(2);
		dto.setShopName("112");
		dtoList.add(dto);
		dto = new SortDto();
		dto.setSiteId(2);
		dto.setIsDelete(1);
		dto.setType(2);
		dto.setShopName("212");
		dtoList.add(dto);
		return dtoList;
	}

	class TypeSortVo {
		private Integer type;
		private List<SortDto> dtoList = new ArrayList<SortDto>();

		public Integer getType() {
			return type;
		}

		public void setType(Integer type) {
			this.type = type;
		}

		public List<SortDto> getDtoList() {
			return dtoList;
		}

		public void setDtoList(List<SortDto> dtoList) {
			this.dtoList = dtoList;
		}
	}

	class SiteSortVo {
		private Integer siteId;
		private List<TypeSortVo> dList = new ArrayList<TypeSortVo>();
		private List<TypeSortVo> oList = new ArrayList<TypeSortVo>();

		public Integer getSiteId() {
			return siteId;
		}

		public void setSiteId(Integer siteId) {
			this.siteId = siteId;
		}

		public List<TypeSortVo> getdList() {
			return dList;
		}

		public void setdList(List<TypeSortVo> dList) {
			this.dList = dList;
		}

		public List<TypeSortVo> getoList() {
			return oList;
		}

		public void setoList(List<TypeSortVo> oList) {
			this.oList = oList;
		}
	}

	class SortDto {
		private Integer siteId;
		private Integer isDelete;
		private Integer type;
		private String shopName;

		public Integer getSiteId() {
			return siteId;
		}

		public void setSiteId(Integer siteId) {
			this.siteId = siteId;
		}

		public Integer getIsDelete() {
			return isDelete;
		}

		public void setIsDelete(Integer isDelete) {
			this.isDelete = isDelete;
		}

		public Integer getType() {
			return type;
		}

		public void setType(Integer type) {
			this.type = type;
		}

		public String getShopName() {
			return shopName;
		}

		public void setShopName(String shopName) {
			this.shopName = shopName;
		}
	}
}
