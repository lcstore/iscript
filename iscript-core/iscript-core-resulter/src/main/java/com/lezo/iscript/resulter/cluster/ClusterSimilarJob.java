package com.lezo.iscript.resulter.cluster;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.Setter;
import lombok.extern.log4j.Log4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.lezo.iscript.resulter.ident.AttrTokenizer;
import com.lezo.iscript.resulter.ident.BrandTokenizer;
import com.lezo.iscript.resulter.ident.CoverResolver;
import com.lezo.iscript.resulter.ident.EntitySimilar;
import com.lezo.iscript.resulter.ident.EntityToken;
import com.lezo.iscript.resulter.ident.ModelTokenizer;
import com.lezo.iscript.resulter.ident.SectionToken;
import com.lezo.iscript.resulter.ident.Tokenizer;
import com.lezo.iscript.resulter.similar.BrandUtils;
import com.lezo.iscript.service.crawler.dto.ProductDto;
import com.lezo.iscript.service.crawler.dto.SimilarDto;
import com.lezo.iscript.service.crawler.service.ProductService;
import com.lezo.iscript.service.crawler.service.SimilarService;
import com.lezo.iscript.service.crawler.service.SynonymBrandService;
import com.lezo.iscript.utils.ConvertUtils;
import com.lezo.iscript.utils.JSONUtils;

@Log4j
@Setter
public class ClusterSimilarJob {
	private static final Integer MAX_SIMILAR_VALUE = 100;
	private static AtomicBoolean running = new AtomicBoolean(false);
	@Autowired
	private ProductService productService;
	@Autowired
	private SynonymBrandService synonymBrandService;
	@Autowired
	private SimilarService similarService;

	public void run() {
		if (running.get()) {
			log.warn("ClusterSimilarJob is running..");
			return;
		}
		long start = System.currentTimeMillis();
		try {
			running.set(true);
			Date fromDate = new Date();
			fromDate = DateUtils.addDays(fromDate, -2);
			fromDate = DateUtils.setHours(fromDate, 0);
			fromDate = DateUtils.setMinutes(fromDate, 0);
			fromDate = DateUtils.setSeconds(fromDate, 0);
			Date toDate = DateUtils.addDays(fromDate, 10);
			String sCategory = "手机";
			Integer siteId = null;
			Long fromId = null;
			int limit = 1000;
			List<ProductDto> dtoList = productService.getProductDtosByDateCateSiteId(fromDate, toDate, sCategory,
					siteId, fromId, limit);
			Map<String, List<ProductDto>> brand2DtosMap = new HashMap<String, List<ProductDto>>();
			List<ProductDto> updateBrandList = new ArrayList<ProductDto>();
			for (ProductDto dto : dtoList) {
				if (StringUtils.isBlank(dto.getTokenBrand())) {
					String tokenBrand = doBrandTokenizer(dto);
					if (StringUtils.isBlank(tokenBrand)) {
						continue;
					} else {
						dto.setTokenBrand(tokenBrand);
						updateBrandList.add(dto);
					}
				}
				List<ProductDto> sameBrandList = brand2DtosMap.get(dto.getTokenBrand());
				if (sameBrandList == null) {
					sameBrandList = new ArrayList<ProductDto>();
					brand2DtosMap.put(dto.getTokenBrand(), sameBrandList);
				}
				sameBrandList.add(dto);
			}
			productService.batchUpdateProductDtos(updateBrandList);
			// same brand similar
			for (Entry<String, List<ProductDto>> entry : brand2DtosMap.entrySet()) {
				Map<Integer, Set<String>> siteCodesMap = new HashMap<Integer, Set<String>>();
				siteCodesMap = getIncrSite2CodeMap(entry);
				// get spu code'dto
				List<ProductDto> incrDtos = getIncrDtos(siteCodesMap);
				updateBrandList.clear();
				for (ProductDto dto : incrDtos) {
					if (StringUtils.isBlank(dto.getTokenBrand())) {
						String tokenBrand = doBrandTokenizer(dto);
						if (StringUtils.isBlank(tokenBrand)) {
							continue;
						} else {
							dto.setTokenBrand(tokenBrand);
							updateBrandList.add(dto);
						}
					}
					dto.setTokenCategory(sCategory);
				}
				productService.batchUpdateProductDtos(updateBrandList);

				List<ProductDto> patchDtos = entry.getValue();
				patchDtos.addAll(incrDtos);
				if (patchDtos.size() < 2) {
					continue;
				}
				List<EntityToken> entityList = toEntityTokens(patchDtos);
				// do tokenizer
				doTokenizers(entityList);
				CoverResolver resolver = new CoverResolver(synonymBrandService);
				SmartCluster smartCluster = new SmartCluster(resolver);
				List<CenterToken> clusters = smartCluster.cluster(entityList);
				List<SimilarDto> similarList = new ArrayList<SimilarDto>(entityList.size());
				for (CenterToken cluster : clusters) {
					List<SimilarDto> similarDtos = toSimilars(cluster);
					if (CollectionUtils.isNotEmpty(similarDtos)) {
						similarList.addAll(similarDtos);
					}
				}
				if (!similarList.isEmpty()) {
					Map<String, Set<String>> code2SpuCodeMap = new HashMap<String, Set<String>>();
					for (ProductDto dto : entry.getValue()) {
						if (StringUtils.isBlank(dto.getSpuCodes())) {
							continue;
						}
						String key = dto.getSiteId() + "-" + dto.getProductCode();
						Set<String> spuCodeSet = code2SpuCodeMap.get(key);
						if (spuCodeSet == null) {
							spuCodeSet = new HashSet<String>();
							spuCodeSet.add(dto.getProductCode());
						}
						String[] sCodeArr = dto.getSpuCodes().split(",");
						for (String sCode : sCodeArr) {
							spuCodeSet.add(sCode);
						}
						for (String code : spuCodeSet) {
							key = dto.getSiteId() + "-" + code;
							code2SpuCodeMap.put(key, spuCodeSet);
						}
					}
					Map<Set<String>, Long> codeSet2WareCodeMap = new HashMap<Set<String>, Long>();
					for (Set<String> codeSet : code2SpuCodeMap.values()) {
						codeSet2WareCodeMap.put(codeSet, System.currentTimeMillis());
					}
					for (SimilarDto sDto : similarList) {
						String key = sDto.getSiteId() + "-" + sDto.getProductCode();
						Set<String> codeSet = code2SpuCodeMap.get(key);
						if (codeSet == null) {
							sDto.setWareCode(sDto.getSimilarCode());
						} else {
							Long wareCode = codeSet2WareCodeMap.get(codeSet);
							sDto.setWareCode(wareCode);
							sDto.setWareType(SimilarDto.WARE_TYPE_CONFIRM);
						}
						JSONObject kvObject = new JSONObject();
						JSONUtils.put(kvObject, sDto.getArbiterId().toString(), sDto.getSimilarScore());
						sDto.setDeciderKvs(kvObject.toString());
					}
				}
				similarService.batchSaveSimilarDtos(similarList);
				log.info("save brand:" + entry.getKey() + ",similar:" + similarList.size());

			}
			long cost = System.currentTimeMillis() - start;
			log.info("save brandCount:" + brand2DtosMap.size() + ",cost:" + cost);
		} catch (Exception e) {
			log.warn("", e);
		} finally {
			running.set(false);
		}
	}

	private String doBrandTokenizer(ProductDto dto) {
		EntityToken entity = new EntityToken(dto.getProductName());
		entity.addAssistToken(new SectionToken("productBrand", dto.getProductBrand()));
		List<SectionToken> brandList = BrandUtils.toBrandTokens(entity);
		if (CollectionUtils.isEmpty(brandList)) {
			log.warn("can not token brand,url:" + dto.getProductUrl());
		} else {
			return brandList.get(0).getValue();
		}
		return null;
	}

	private List<SimilarDto> toSimilars(CenterToken cluster) {
		if (CollectionUtils.isEmpty(cluster.getMembers())) {
			return Collections.emptyList();
		}
		Class<?> targetClass = SimilarDto.class;
		Map<String, Field> name2FieldMap = new HashMap<String, Field>();
		for (Field field : targetClass.getDeclaredFields()) {
			name2FieldMap.put(field.getName(), field);
		}
		long sCode = System.currentTimeMillis();
		List<SimilarDto> dtoList = new ArrayList<SimilarDto>(cluster.getMembers().size());
		for (EntitySimilar m : cluster.getSimilars()) {
			SimilarDto sDto = new SimilarDto();
			sDto.setSimilarCode(sCode);
			sDto.setSimilarScore(m.getSimilar());
			EntityToken entity = m.getRightCover().getEntity();
			sDto.setProductName(entity.getMaster().getValue());
			copyProperties(entity, sDto, name2FieldMap);
			sDto.setCreateTime(new Date(sCode));
			sDto.setUpdateTime(sDto.getCreateTime());
			dtoList.add(sDto);
		}
		// add center entity
		SimilarDto sDto = new SimilarDto();
		sDto.setSimilarCode(sCode);
		sDto.setSimilarScore(MAX_SIMILAR_VALUE);
		EntityToken entity = cluster.getCenter();
		sDto.setProductName(entity.getMaster().getValue());
		copyProperties(entity, sDto, name2FieldMap);
		sDto.setCreateTime(new Date(sCode));
		sDto.setUpdateTime(sDto.getCreateTime());
		sDto.setIsStandard(1);
		dtoList.add(sDto);
		return dtoList;
	}

	private void copyProperties(EntityToken entity, SimilarDto sDto, Map<String, Field> name2FieldMap) {
		for (SectionToken asis : entity.getAssists()) {
			Field field = name2FieldMap.get(asis.getKey());
			if (field == null) {
				continue;
			}
			try {
				Object destValue = ConvertUtils.convertTo(asis.getValue(), field.getType());
				if (!field.isAccessible()) {
					field.setAccessible(true);
				}
				field.set(sDto, destValue);
			} catch (Exception e) {
				log.warn("", e);
			}

		}
	}

	private void doTokenizers(List<EntityToken> entityList) {
		List<Tokenizer> tokenizers = new ArrayList<Tokenizer>();
		tokenizers.add(new AttrTokenizer());
		tokenizers.add(new BrandTokenizer(synonymBrandService));
		tokenizers.add(new ModelTokenizer());
		for (Tokenizer tokenizer : tokenizers) {
			tokenizer.identify(entityList);
		}
	}

	private List<EntityToken> toEntityTokens(List<ProductDto> patchDtos) {
		List<EntityToken> entityList = new ArrayList<EntityToken>(patchDtos.size());
		Set<String> ignoreSet = new HashSet<String>();
		ignoreSet.add("productName");
		ignoreSet.add("id");
		for (ProductDto dto : patchDtos) {
			Object target = dto;
			EntityToken entity = new EntityToken(dto.getProductName());
			for (Field field : dto.getClass().getDeclaredFields()) {
				if (ignoreSet.contains(field.getName())) {
					continue;
				} else if (Date.class.isAssignableFrom(field.getType())) {
					continue;
				}
				Object value = null;
				try {
					value = FieldUtils.readDeclaredField(target, field.getName(), true);
				} catch (IllegalAccessException e) {
					log.warn("", e);
				}
				if (value != null) {
					entity.addAssistToken(new SectionToken(field.getName(), value.toString()));
				}
			}
			entityList.add(entity);
		}
		return entityList;
	}

	private List<ProductDto> getIncrDtos(Map<Integer, Set<String>> siteCodesMap) {
		List<ProductDto> incrDtos = null;
		for (Entry<Integer, Set<String>> scEntry : siteCodesMap.entrySet()) {
			List<ProductDto> spuList = productService.getProductDtos(new ArrayList<String>(scEntry.getValue()),
					scEntry.getKey());
			if (CollectionUtils.isNotEmpty(spuList)) {
				if (incrDtos == null) {
					incrDtos = spuList;
				} else {
					incrDtos.addAll(spuList);
				}
			}
		}
		return incrDtos == null ? new ArrayList<ProductDto>() : incrDtos;
	}

	private Map<Integer, Set<String>> getIncrSite2CodeMap(Entry<String, List<ProductDto>> entry) {
		Map<Integer, Set<String>> siteCodesMap = new HashMap<Integer, Set<String>>();
		Set<String> hasKeySet = new HashSet<String>();
		for (ProductDto dto : entry.getValue()) {
			hasKeySet.add(dto.getSiteId() + "-" + dto.getProductCode());
		}
		for (ProductDto dto : entry.getValue()) {
			if (StringUtils.isBlank(dto.getSpuCodes())) {
				continue;
			}
			String[] codeArray = dto.getSpuCodes().split(",");
			if (codeArray == null) {
				continue;
			}
			Set<String> codeSet = siteCodesMap.get(dto.getSiteId());
			if (codeSet == null) {
				codeSet = new HashSet<String>();
				siteCodesMap.put(dto.getSiteId(), codeSet);
			}
			for (String code : codeArray) {
				String key = dto.getSiteId() + "-" + code;
				if (hasKeySet.contains(key)) {
					continue;
				}
				codeSet.add(code);
			}
		}
		return siteCodesMap;
	}
}
