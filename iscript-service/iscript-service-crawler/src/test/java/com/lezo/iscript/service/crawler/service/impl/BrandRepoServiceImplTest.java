package com.lezo.iscript.service.crawler.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.lezo.iscript.BaseTestCase;
import com.lezo.iscript.service.crawler.dto.BrandRepoDto;
import com.lezo.iscript.service.crawler.service.BrandRepoService;
import com.lezo.iscript.service.crawler.service.SynonymBrandService;
import com.lezo.iscript.utils.BatchIterator;

public class BrandRepoServiceImplTest extends BaseTestCase {
    @Autowired
    private BrandRepoService brandRepoService;
    @Autowired
    private SynonymBrandService synonymBrandService;

    private static final Pattern CN_REG = Pattern.compile("[\u4e00-\u9fa5]+");
    private static final Comparator<String> CMP_CN = new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            boolean hasCn1 = CN_REG.matcher(o1).find();
            boolean hasCn2 = CN_REG.matcher(o2).find();
            if (hasCn1 && !hasCn2) {
                return -1;
            } else if (!hasCn1 && hasCn2) {
                return 1;
            }
            return o2.length() - o1.length();
        }
    };

    @Test
    @Rollback(true)
    public void testBatchInsert() throws Exception {
        List<BrandRepoDto> dtoList = new ArrayList<BrandRepoDto>();
        BrandRepoDto dto = new BrandRepoDto();
        dto.setCoreName("兰特");
        dto.setCrumbNav("进口食品、进口牛奶=进口牛奶乳品=进口牛奶");
        dto.setSortName("食品");
        dto.setIncludes("lactel,兰特");
        dto.setRegionName(StringUtils.EMPTY);
        dto.setExcludes(StringUtils.EMPTY);
        dtoList.add(dto);
        dto = new BrandRepoDto();
        dto.setCoreName("娇韵诗");
        dto.setCrumbNav("个护化妆=身体护肤");
        dto.setSortName("化妆品");
        dto.setIncludes("clarins,娇韵诗");
        dto.setRegionName(StringUtils.EMPTY);
        dto.setExcludes(StringUtils.EMPTY);
        dtoList.add(dto);
        brandRepoService.batchInsertDtos(dtoList);
    }

    @Test
    @Rollback(false)
    public void testConvertToBrandRepo() throws Exception {
        Iterator<String> it = synonymBrandService.iteratorKeys();
        Set<Set<String>> synsSet = Sets.newHashSet();
        while (it.hasNext()) {
            Set<String> hasSet = synonymBrandService.getSynonyms(it.next());
            synsSet.add(hasSet);
        }
        Date newDate = new Date();
        List<BrandRepoDto> dtoList = new ArrayList<BrandRepoDto>();
        int total = 0;
        for (Set<String> brandSet : synsSet) {
            BrandRepoDto dto = new BrandRepoDto();
            List<String> brands = Lists.newArrayList(brandSet);
            dto.setCoreName(brands.get(0));
            StringBuilder sb = new StringBuilder();
            for (String brand : brandSet) {
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(brand);
            }
            dto.setIncludes(sb.toString());
            dto.setSortName(StringUtils.EMPTY);
            dto.setExcludes(StringUtils.EMPTY);
            dto.setCrumbNav(StringUtils.EMPTY);
            dto.setRegionName(StringUtils.EMPTY);
            dto.setCreateTime(newDate);
            dto.setUpdateTime(dto.getCreateTime());
            dtoList.add(dto);
            if (dtoList.size() >= 500) {
                total += dtoList.size();
                brandRepoService.batchInsertDtos(dtoList);
                System.err.println("save brandRepo,count:" + dtoList.size() + ",total:" + total);
                dtoList.clear();
            }
        }
        brandRepoService.batchInsertDtos(dtoList);
        System.err.println("done...");
    }

    @Test
    @Rollback(false)
    public void testSplitBrandWithRegion() throws Exception {
        List<String> lineList = FileUtils.readLines(new File("src/test/resources/data/brandrepo.txt"), "UTF-8");
        List<Long> idList = Lists.newArrayList();
        for (String line : lineList) {
            idList.add(Long.valueOf(line.trim()));
        }
        BatchIterator<Long> it = new BatchIterator<Long>(idList);
        Pattern oReg = Pattern.compile("（(.*?)）");
        int total = 0;
        while (it.hasNext()) {
            List<BrandRepoDto> hasList = brandRepoService.getDtoByIds(it.next());
            for (BrandRepoDto has : hasList) {
                Matcher matcher = oReg.matcher(has.getCoreName());
                if (matcher.find()) {
                    has.setCoreName(matcher.replaceAll(""));
                }
                matcher = oReg.matcher(has.getIncludes());
                if (matcher.find()) {
                    has.setRegionName(matcher.group(1));
                    has.setIncludes(matcher.replaceAll(""));
                }
            }
            total += hasList.size();
            brandRepoService.batchUpdateDtos(hasList);
            System.err.println("update,count:" + hasList.size() + ",total:" + total);
        }
    }

    @Test
    @Rollback(false)
    public void testUnifyCoreBrand() throws Exception {
        long fromId = 0L;
        int limit = 500;
        int total = 0;
        while (true) {
            List<BrandRepoDto> hasList = brandRepoService.getDtoByIdWithLimit(fromId, limit);
            for (BrandRepoDto hasDto : hasList) {
                List<String> sameList = Lists.newArrayList(hasDto.getIncludes().split(","));
                Collections.sort(sameList, CMP_CN);
                hasDto.setCoreName(sameList.get(0));
                if (fromId < hasDto.getId()) {
                    fromId = hasDto.getId();
                }
            }
            total += hasList.size();
            brandRepoService.batchUpdateDtos(hasList);
            System.err.println("update,count:" + hasList.size() + ",total:" + total);
            if (hasList.size() < limit) {
                break;
            }
        }
        System.err.println("done....");
    }

    @Test
    public void testGetDtoByCoreOrSort() throws Exception {
        String sortName = "";
        List<String> coreList = Lists.newArrayList("麦丽莎");
        List<BrandRepoDto> hasList = brandRepoService.getDtoByCoreOrSort(coreList, sortName);
        System.err.println(hasList.size());
        System.err.println(ArrayUtils.toString(hasList));
    }

}
