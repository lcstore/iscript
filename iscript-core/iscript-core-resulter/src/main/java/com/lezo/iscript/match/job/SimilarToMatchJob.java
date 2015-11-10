package com.lezo.iscript.match.job;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.extern.log4j.Log4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.lezo.iscript.match.algorithm.IAnalyser;
import com.lezo.iscript.match.algorithm.IMatcher;
import com.lezo.iscript.match.algorithm.analyse.BrandAnalyser;
import com.lezo.iscript.match.algorithm.analyse.ModelAnalyser;
import com.lezo.iscript.match.algorithm.analyse.UnitAnalyser;
import com.lezo.iscript.match.algorithm.cluster.SimilarCluster;
import com.lezo.iscript.match.algorithm.matcher.FoodMatcher;
import com.lezo.iscript.match.map.BrandMapper;
import com.lezo.iscript.match.map.SameEntity;
import com.lezo.iscript.match.map.loader.DicLoader;
import com.lezo.iscript.match.map.loader.LineDicLoader;
import com.lezo.iscript.match.pojo.CellAssort;
import com.lezo.iscript.match.pojo.CellToken;
import com.lezo.iscript.match.pojo.SimilarCenter;
import com.lezo.iscript.match.pojo.SimilarIn;
import com.lezo.iscript.match.pojo.SimilarOut;
import com.lezo.iscript.match.utils.CellAssortUtils;
import com.lezo.iscript.match.utils.CellTokenUtils;
import com.lezo.iscript.service.crawler.dto.MatchDto;
import com.lezo.iscript.service.crawler.dto.SimilarDto;
import com.lezo.iscript.service.crawler.dto.SiteDto;
import com.lezo.iscript.service.crawler.service.MatchService;
import com.lezo.iscript.service.crawler.service.SimilarService;
import com.lezo.iscript.service.crawler.service.SynonymBrandService;
import com.lezo.iscript.service.crawler.utils.BrandRepoCacher;
import com.lezo.iscript.service.crawler.utils.SiteCacher;
import com.lezo.iscript.spring.context.SpringBeanUtils;

@Log4j
public class SimilarToMatchJob implements Runnable {
    private static AtomicBoolean running = new AtomicBoolean(false);

    public static void main(String[] args) {
        String[] configs = new String[] { "classpath:spring-config-ds.xml" };
        ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
        new SimilarToMatchJob().run();
    }

    @Override
    public void run() {
        if (running.get()) {
            log.warn("ClusterSimilarJob is running..");
            return;
        }
        long start = System.currentTimeMillis();
        // DicLoader loader = newLoader();
        // DicLoader loader = createBrandLoader();
        DicLoader loader = createRepoLoader();
        BrandMapper.getInstance().setLoader(loader);
        try {
            running.set(true);
            SimilarService similarService = SpringBeanUtils.getBean(SimilarService.class);
            MatchService matchService = SpringBeanUtils.getBean(MatchService.class);
            List<String> jobIds = Lists.newArrayList();
            List<Integer> siteIds = Lists.newArrayList();
            List<String> brandList = similarService.getBrandByJobIdsOrSiteIds(jobIds, siteIds);
            int limit = 500;
            Long fromId = 0L;
            SimilarCluster cluster = new SimilarCluster();
            IMatcher matcher = new FoodMatcher();
            // brandList = Lists.newArrayList();
            // Iterator<String> it = BrandMapper.getInstance().iterator();
            // while (it.hasNext()) {
            // brandList.add(it.next());
            // }
            boolean hasDo = false;
            for (String brand : brandList) {
                if (StringUtils.isBlank(brand)) {
                    log.warn("empty brand...");
                    continue;
                }
                // if (!hasDo) {
                // hasDo = "brother".equals(brand);
                // }
                // if (!hasDo) {
                // continue;
                // }
                fromId = 0L;
                Map<String, List<SimilarDto>> skuMap = Maps.newHashMap();
                while (true) {
                    List<SimilarDto> hasDtos = similarService.getSimilarDtoByBrandAndId(brand, fromId, limit);
                    for (SimilarDto dto : hasDtos) {
                        List<SimilarDto> sameList = skuMap.get(dto.getSkuCode());
                        if (sameList == null) {
                            sameList = Lists.newArrayList();
                            skuMap.put(dto.getSkuCode(), sameList);
                        }
                        sameList.add(dto);
                    }
                    for (SimilarDto dto : hasDtos) {
                        if (fromId < dto.getId()) {
                            fromId = dto.getId();
                        }
                    }
                    log.info("query brand:" + brand + ",fromId:" + fromId + ",size:" + hasDtos.size());
                    if (hasDtos.size() < limit) {
                        break;
                    }
                }
                List<SimilarDto> similarDtos = mergeSimilarDto(skuMap);
                List<SimilarIn> similarIns = toSimilarIns(similarDtos);
                // List<SimilarCenter> centers = cluster.doCluster(similarIns, SimilarFactUtils.getDefaultFacts());
                List<SimilarCenter> centers = matcher.doMatcher(similarIns);
                List<MatchDto> matchDtos = toMatchDtos(centers, similarDtos);
                log.info("handle brand:" + brand + ",mDto:" + matchDtos.size());
                matchService.batchSaveDtos(matchDtos);
                log.info("save brand:" + brand + ",similar:" + similarDtos.size() + ",match:" + matchDtos.size());
            }
            long cost = System.currentTimeMillis() - start;
            log.info("done.match,cost:" + cost);
        } catch (Exception e) {
            log.warn("", e);
        } finally {
            running.set(false);
        }
    }

    private List<SimilarDto> mergeSimilarDto(Map<String, List<SimilarDto>> skuMap) {
        List<SimilarDto> similarDtos = Lists.newArrayList();
        for (Entry<String, List<SimilarDto>> entry : skuMap.entrySet()) {
            SimilarDto mDto = null;
            for (SimilarDto dto : entry.getValue()) {
                if (mDto == null) {
                    mDto = dto;
                    similarDtos.add(mDto);
                } else {
                    if (mDto.getShopId() < 1) {
                        dto.setShopId(dto.getShopId());
                    }
                    for (Field field : SimilarDto.class.getDeclaredFields()) {
                        try {
                            if (!field.isAccessible()) {
                                field.setAccessible(true);
                            }
                            Object valObject = field.get(mDto);
                            if (valObject == null || isBlankString(valObject, field)) {
                                Object newObject = field.get(dto);
                                field.set(mDto, newObject);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                }
            }

        }
        return similarDtos;
    }

    private boolean isBlankString(Object valObject, Field field) {
        if (field.getType().isAssignableFrom(String.class)) {
            return valObject == null || StringUtils.isBlank(valObject.toString());
        }
        return false;
    }

    private DicLoader createBrandLoader() {
        return new DicLoader() {
            @Override
            public Map<String, SameEntity> loadDic(InputStream in) throws Exception {
                SynonymBrandService synonymBrandService = SpringBeanUtils.getBean(SynonymBrandService.class);
                Iterator<String> it = synonymBrandService.iteratorKeys();
                Map<String, SameEntity> sameMap = Maps.newHashMap();
                while (it.hasNext()) {
                    String brandName = it.next();
                    Set<String> sameSet = synonymBrandService.getSynonyms(brandName);
                    SameEntity sSameSet = LineDicLoader.toSameChars(sameSet);
                    for (String sVal : sSameSet.getSameSet()) {
                        sameMap.put(sVal, sSameSet);

                    }
                }
                return sameMap;
            }
        };
    }

    private DicLoader createRepoLoader() {
        return new DicLoader() {
            @Override
            public Map<String, SameEntity> loadDic(InputStream in) throws Exception {
                Map<String, Set<String>> map = BrandRepoCacher.getInstance().getMap();
                Map<String, SameEntity> sameMap = Maps.newHashMap();
                for (Entry<String, Set<String>> entry : map.entrySet()) {
                    Set<String> sameSet = entry.getValue();
                    SameEntity sSameSet = new SameEntity();
                    sSameSet.setValue(entry.getKey());
                    sSameSet.setSameSet(sameSet);
                    for (String sVal : sSameSet.getSameSet()) {
                        sameMap.put(sVal, sSameSet);
                    }
                }
                return sameMap;
            }
        };
    }

    private DicLoader newLoader() {
        return new DicLoader() {
            @Override
            public Map<String, SameEntity> loadDic(InputStream in) throws Exception {
                IOUtils.closeQuietly(in);
                Map<String, SameEntity> entityMap = Maps.newHashMap();
                SameEntity entity = new SameEntity();
                String key = "妇炎洁";
                Set<String> sameSet = Sets.newHashSet();
                sameSet.add(key);
                entity.setSameSet(sameSet);
                entity.setValue(key);
                entityMap.put(key, entity);
                return entityMap;
            }
        };
    }

    private List<MatchDto> toMatchDtos(List<SimilarCenter> centers, List<SimilarDto> similarDtos) {
        List<MatchDto> matchDtos = Lists.newArrayList();
        Date currentDate = new Date();
        Map<String, SimilarDto> skuCodeMap = Maps.newHashMap();
        for (SimilarDto dto : similarDtos) {
            skuCodeMap.put(dto.getSkuCode(), dto);
        }
        for (SimilarCenter center : centers) {
            if (CollectionUtils.isEmpty(center.getOuts())) {
                continue;
            }
            MatchDto referDto = new MatchDto();
            copyProperties(referDto, center.getValue(), skuCodeMap);
            referDto.setId(null);
            referDto.setMatchCode(MatchDto.newMatchCode());
            referDto.setCreateTime(currentDate);
            referDto.setUpdateTime(currentDate);
            referDto.setSimilarScore(100);
            referDto.setCaption("center");
            referDto.setArbiterId(MatchDto.ARBITER_NAME);
            for (SimilarOut out : center.getOuts()) {
                MatchDto currentDto = new MatchDto();
                copyProperties(currentDto, out.getCurrent(), skuCodeMap);
                currentDto.setMatchCode(referDto.getMatchCode());
                currentDto.setCreateTime(currentDate);
                currentDto.setUpdateTime(currentDate);
                currentDto.setSimilarScore(out.getScore());
                currentDto.setArbiterId(MatchDto.ARBITER_NAME);
                currentDto.setId(null);
                matchDtos.add(currentDto);
            }
            matchDtos.add(referDto);
        }
        return matchDtos;
    }

    private void copyProperties(MatchDto newDto, SimilarIn centerIn, Map<String, SimilarDto> skuCodeMap) {
        SimilarDto similarDto = skuCodeMap.get(centerIn.getSkuCode());
        if (similarDto != null) {
            BeanUtils.copyProperties(similarDto, newDto);
        }
        newDto.setTokenBrand(CellAssortUtils.getValueOrDefault(centerIn.getTokenBrand(), StringUtils.EMPTY));
        newDto.setTokenModel(CellAssortUtils.getValueOrDefault(centerIn.getTokenModel(), StringUtils.EMPTY));
        newDto.setTokenUnit(CellAssortUtils.getValueOrDefault(centerIn.getTokenUnit(), StringUtils.EMPTY));
        if (newDto.getSiteId().equals(1001)) {
            newDto.setProductUrl("http://item.jd.com/" + newDto.getProductCode() + ".html");
        } else {
            String url = newDto.getProductUrl();
            SiteDto siteDto = null;
            if (url.indexOf("51buy.com") > 0) {
                siteDto = SiteCacher.getInstance().getSiteDto(1007);
            } else {
                siteDto = SiteCacher.getInstance().getDomainSiteDto(url);
            }
            if (siteDto != null) {
                boolean bSelf = newDto.getSiteId().equals(newDto.getShopId());
                newDto.setSiteId(siteDto.getId());
                newDto.setSkuCode(newDto.getSiteId() + "_" + newDto.getProductCode());
                if (bSelf) {
                    newDto.setShopId(newDto.getSiteId());
                }
            }

        }
    }

    private List<SimilarIn> toSimilarIns(List<SimilarDto> similarDtos) {
        List<SimilarIn> similarIns = Lists.newArrayList();
        for (SimilarDto sDto : similarDtos) {
            SimilarIn in = newSimilarIn(sDto);
            similarIns.add(in);
        }
        return similarIns;
    }

    private SimilarIn newSimilarIn(SimilarDto sDto) {
        SimilarIn newIn = new SimilarIn();
        newIn.setSkuCode(sDto.getSkuCode());
        newIn.setProductName(sDto.getProductName());
        newIn.setBarCode(CellAssortUtils.toAssort(sDto.getBarCode()));
        newIn.setWareCode(CellAssortUtils.toAssort(sDto.getWareCode()));
        // tokenizer(newIn);
        return newIn;
    }

    private void tokenizer(SimilarIn newIn) {
        IAnalyser brandAnalyser = new BrandAnalyser();
        IAnalyser unitAnalyser = new UnitAnalyser();
        IAnalyser modelAnalyser = new ModelAnalyser();
        List<CellToken> tokens = CellTokenUtils.getTokens(newIn.getProductName().toLowerCase());
        CellAssort assort = brandAnalyser.analyse(tokens);
        newIn.setTokenBrand(assort);
        tokens = CellAssortUtils.removeAssort(tokens, assort);
        assort = unitAnalyser.analyse(tokens);
        newIn.setTokenUnit(assort);
        tokens = CellAssortUtils.removeAssort(tokens, assort);
        assort = modelAnalyser.analyse(tokens);
        newIn.setTokenModel(assort);
        tokens = CellAssortUtils.removeAssort(tokens, assort);
        Comparator<CellToken> cmp = new Comparator<CellToken>() {
            @Override
            public int compare(CellToken o1, CellToken o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        };
        Collections.sort(tokens, cmp);
        StringBuilder sb = new StringBuilder();
        for (CellToken tk : tokens) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(tk.getValue());
        }
        newIn.setRemain(CellAssortUtils.toAssort(sb.toString()));
    }

}
