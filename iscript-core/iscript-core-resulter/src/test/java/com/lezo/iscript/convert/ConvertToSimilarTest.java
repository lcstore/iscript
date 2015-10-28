package com.lezo.iscript.convert;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Lists;
import com.lezo.iscript.resulter.ident.SectionToken;
import com.lezo.iscript.resulter.similar.BrandUtils;
import com.lezo.iscript.resulter.similar.ModelUtils;
import com.lezo.iscript.service.crawler.dao.MatchOneDao;
import com.lezo.iscript.service.crawler.dto.MatchOneDto;
import com.lezo.iscript.service.crawler.dto.SimilarDto;
import com.lezo.iscript.service.crawler.service.SimilarService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring-config-ds.xml" })
// @Transactional
// @TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
public class ConvertToSimilarTest {
    private static final String encoding = "UTF-8";
    @Autowired
    private SimilarService similarService;
    @Autowired
    private MatchOneDao matchOneDao;

    @Test
    public void testConvertFromMatchOne() throws Exception {
        List<String> lineList = FileUtils.readLines(new File("src/test/resources/data/1match.pid"), encoding);
        int siteId = 1002;
        int total = 0;
        String jobId = "";
        String lastId = "";
        boolean last = lastId == null;
        for (String line : lineList) {
            if (!last) {
                last = lastId.equals(line);
            }
            if (!last) {
                continue;
            }
            String productId = line;
            List<MatchOneDto> hasList = matchOneDao.getDtoByProductId(productId);
            if (hasList.isEmpty()) {
                continue;
            }
            try {
                List<SimilarDto> similarDtos = Lists.newArrayList();
                Date createTime = new Date();
                for (MatchOneDto hasDto : hasList) {
                    SimilarDto similarDto = newSimilarDto();
                    similarDto.setProductCode(hasDto.getOpponProductCode());
                    similarDto.setProductName(hasDto.getOpponProductName());
                    similarDto.setProductUrl(hasDto.getOpponProductUrl());
                    similarDto.setSiteId(hasDto.getSiteId());
                    if (hasDto.getIsInshop() != null && hasDto.getIsInshop().equals(1)) {
                        similarDto.setShopId(-1);
                    } else {
                        similarDto.setShopId(similarDto.getSiteId());
                    }
                    similarDto.setCreateTime(createTime);
                    similarDto.setUpdateTime(createTime);
                    similarDto.setJobId(jobId);
                    similarDto.setBarCode("");
                    similarDto.setMarketPrice(-1F);
                    similarDto.setSimilarCode(siteId + "_" + productId);
                    addTokens(similarDto);
                    addArbiterId(similarDto, hasDto);
                    if (similarDto.getProductName() == null) {
                        similarDto.setProductName("");
                    }
                    similarDtos.add(similarDto);
                }
                MatchOneDto hasDto = hasList.get(0);
                SimilarDto similarDto = newSimilarDto();
                similarDto.setProductCode(hasDto.getProductCode());
                similarDto.setProductName(hasDto.getProductName());
                similarDto.setProductUrl("http://item.yhd.com/product/" + hasDto.getProductId());
                similarDto.setSiteId(siteId);
                if (hasDto.getIsInshop() != null && hasDto.getIsInshop().equals(1)) {
                    similarDto.setShopId(-1);
                } else {
                    similarDto.setShopId(similarDto.getSiteId());
                }
                similarDto.setCreateTime(createTime);
                similarDto.setUpdateTime(createTime);
                similarDto.setJobId(jobId);
                similarDto.setMarketPrice(-1F);
                similarDto.setSimilarCode(siteId + "_" + productId);
                similarDto.setWareCode(siteId + "_" + productId);
                if (similarDto.getProductName() == null) {
                    similarDto.setProductName("");
                }
                addTokens(similarDto);
                addArbiterId(similarDto, hasDto);
                similarDtos.add(similarDto);
                similarService.batchInsertSimilarDtos(similarDtos);
                total += similarDtos.size();
                System.err.println("save total:" + total + ",productId:" + productId);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("save total:" + total + ",productId:" + productId);
                break;
            }
        }

    }

    private void addArbiterId(SimilarDto similarDto, MatchOneDto hasDto) {
        if (hasDto.getIsBarcode() != null && hasDto.getIsBarcode().equals(1)) {
            similarDto.setArbiterId(SimilarDto.ARBITER_BARCODE);
        }
        else if (hasDto.getIsSearch() != null && hasDto.getIsSearch().equals(1)) {
            similarDto.setArbiterId(SimilarDto.ARBITER_SEARCH);
        } else {
            similarDto.setArbiterId(SimilarDto.ARBITER_NAME);
        }
    }

    @Test
    public void testNew() {
        SimilarDto dto = newSimilarDto();
        System.err.println("getWareCode:" + dto.getWareCode());
    }

    private void addTokens(SimilarDto similarDto) {
        if (StringUtils.isBlank(similarDto.getProductName())) {
            return;
        }
        List<SectionToken> tokens = BrandUtils.toBrandTokens(similarDto.getProductName());
        if (CollectionUtils.isNotEmpty(tokens)) {
            similarDto.setTokenBrand(tokens.get(0).getValue());
        }
        tokens = ModelUtils.toModelTokens(similarDto.getProductName());
        if (CollectionUtils.isNotEmpty(tokens)) {
            String sVal = tokens.get(0).getValue();
            if (sVal.length() < 30) {
                similarDto.setTokenModel(sVal);
            } else {
                System.err.println("error model:" + sVal);
            }
        }
    }

    private SimilarDto newSimilarDto() {
        SimilarDto dto = new SimilarDto();
        Field[] fields = SimilarDto.class.getDeclaredFields();
        for (Field f : fields) {
            if (f.getType().isAssignableFrom(String.class)) {
                try {
                    FieldUtils.writeField(f, dto, StringUtils.EMPTY, true);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return dto;
    }
}
