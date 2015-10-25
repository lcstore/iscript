package com.lezo.iscript.convert;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lombok.extern.log4j.Log4j;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lezo.iscript.match.algorithm.IAnalyser;
import com.lezo.iscript.match.algorithm.analyse.BrandAnalyser;
import com.lezo.iscript.match.algorithm.analyse.ModelAnalyser;
import com.lezo.iscript.match.algorithm.analyse.UnitAnalyser;
import com.lezo.iscript.match.map.BrandMapper;
import com.lezo.iscript.match.map.SameEntity;
import com.lezo.iscript.match.map.loader.DicLoader;
import com.lezo.iscript.match.map.loader.LineDicLoader;
import com.lezo.iscript.match.pojo.CellAssort;
import com.lezo.iscript.match.pojo.CellToken;
import com.lezo.iscript.match.utils.CellAssortUtils;
import com.lezo.iscript.match.utils.CellTokenUtils;
import com.lezo.iscript.service.crawler.dto.SimilarDto;
import com.lezo.iscript.service.crawler.service.SimilarService;
import com.lezo.iscript.service.crawler.service.SynonymBrandService;
import com.lezo.iscript.service.crawler.utils.BrandRepoCacher;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring-config-ds.xml" })
// @Transactional
// @TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
@Log4j
public class ConvertSimilarTokenTest {
    @Autowired
    private SimilarService similarService;
    @Autowired
    private SynonymBrandService synonymBrandService;

    @Test
    public void testConvertToken() throws Exception {
        long start = System.currentTimeMillis();
        String jobId = "";
        Long fromId = 0L;
        int limit = 500;
        // DicLoader loader = createLoader();
        DicLoader loader = createRepoLoader();
        BrandMapper.getInstance().setLoader(loader);
        List<Integer> siteIds = Lists.newArrayList(1001, 1002);
        for (Integer sid : siteIds) {
            int total = 0;
            fromId = 0L;
            while (true) {
                List<SimilarDto> dtoList = similarService.getSimilarDtoByJobIdSiteId(jobId, sid, fromId, limit);
                doToken(dtoList);
                for (SimilarDto dto : dtoList) {
                    if (fromId < dto.getId()) {
                        fromId = dto.getId();
                    }
                }
                similarService.batchUpdateSimilarDtos(dtoList);
                total += dtoList.size();
                log.info("siteId:" + sid + ",fromId:" + fromId + ",size:" + dtoList.size() + ",total:" + total);
                if (dtoList.size() < limit) {
                    break;
                }

            }
            log.info("done,siteId:" + sid + ",total:" + total);
        }
        long cost = System.currentTimeMillis() - start;
        log.info("done...,cost:" + cost);
    }

    private DicLoader createLoader() {
        return new DicLoader() {
            @Override
            public Map<String, SameEntity> loadDic(InputStream in) throws Exception {
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
                    SameEntity sSameSet = LineDicLoader.toSameChars(sameSet);
                    for (String sVal : sSameSet.getSameSet()) {
                        sameMap.put(sVal, sSameSet);
                    }
                }
                return sameMap;
            }
        };
    }

    private void doToken(List<SimilarDto> dtoList) {
        IAnalyser brandAnalyser = new BrandAnalyser();
        IAnalyser unitAnalyser = new UnitAnalyser();
        IAnalyser modelAnalyser = new ModelAnalyser();
        BrandMapper mapper = BrandMapper.getInstance();
        for (SimilarDto dto : dtoList) {
            List<CellToken> tokens = CellTokenUtils.getTokens(dto.getProductName().toLowerCase());
            CellAssort assort = brandAnalyser.analyse(tokens);
            if (assort.getValue() == null) {
                dto.setTokenBrand(StringUtils.EMPTY);
            } else {
                String sBrand = assort.getValue().getValue().getValue();
                SameEntity sameSet = mapper.getSameEntity(sBrand);
                dto.setTokenBrand(sameSet.getValue());
                // log.info("sBrand:" + sBrand + ",unify:" + dto.getTokenBrand() + ",name:" + dto.getProductName());
            }
            tokens = CellAssortUtils.removeAssort(tokens, assort);
            assort = unitAnalyser.analyse(tokens);
            if (assort.getValue() == null) {
                dto.setTokenUnit(StringUtils.EMPTY);
            } else {
                String sValue = assort.getValue().getValue().getValue();
                dto.setTokenUnit(sValue);
            }
            tokens = CellAssortUtils.removeAssort(tokens, assort);
            assort = modelAnalyser.analyse(tokens);
            if (assort.getValue() == null) {
                dto.setTokenModel(StringUtils.EMPTY);
            } else {
                String sValue = assort.getValue().getValue().getValue();
                dto.setTokenModel(sValue);
            }

        }

    }

}
