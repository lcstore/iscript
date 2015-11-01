package com.lezo.iscript.convert;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.annotation.Rollback;

import com.google.common.collect.Lists;
import com.lezo.iscript.service.crawler.dto.BrandRepoDto;
import com.lezo.iscript.service.crawler.service.BrandRepoService;
import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.utils.BrandUtils;

public class ConvertBrandRepoTest {
    @Autowired
    private BrandRepoService brandRepoService;

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
    @Rollback(false)
    /**
     * BaseTestCase-->@Rollback(false),批量更新有问题
     * @throws Exception
     */
    public void testUnifyCoreBrand() throws Exception {
        String[] configs = new String[] { "classpath:spring-config-ds.xml" };
        ClassPathXmlApplicationContext cx = new ClassPathXmlApplicationContext(configs);
        brandRepoService = SpringBeanUtils.getBean(BrandRepoService.class);
        brandRepoService = SpringBeanUtils.getBean(BrandRepoService.class);
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
            doUnify(hasList);
            total += hasList.size();
            brandRepoService.batchUpdateDtos(hasList);
            System.err.println("update,count:" + hasList.size() + ",total:" + total);
            if (hasList.size() < limit) {
                break;
            }
        }
        cx.close();
        System.err.println("done....");
    }

    private void doUnify(List<BrandRepoDto> dtoList) {
        for (BrandRepoDto dto : dtoList) {
            dto.setCoreName(BrandUtils.toUnify(dto.getCoreName()));
            dto.setIncludes(BrandUtils.toUnify(dto.getIncludes()));
            dto.setExcludes(BrandUtils.toUnify(dto.getExcludes()));
        }
    }

    @Test
    public void testAddNewBrands() throws Exception {
        String[] configs = new String[] { "classpath:spring-config-ds.xml" };
        ClassPathXmlApplicationContext cx = new ClassPathXmlApplicationContext(configs);
        brandRepoService = SpringBeanUtils.getBean(BrandRepoService.class);
        String path = "/apps/src/codes/lezo/iscript/iscript-core/iscript-core-client/src/test/resources/data/brand.txt";
        List<String> lineList = FileUtils.readLines(new File(path), "UTF-8");
        List<BrandRepoDto> dtoList = Lists.newArrayList();
        Date newDate = new Date();
        for (String line : lineList) {
            String[] brandArr = line.toLowerCase().split(",");
            List<String> brandList = Lists.newArrayList(brandArr);
            Collections.sort(brandList, CMP_CN);
            BrandRepoDto dto = new BrandRepoDto();
            dto.setCoreName(brandList.get(0));
            StringBuilder sb = new StringBuilder();
            for (String brand : brandList) {
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
            dto.setSortName("食品");
            dtoList.add(dto);
        }
        brandRepoService.batchSaveDtos(dtoList);
        cx.close();
        System.err.println("done....size:" + dtoList.size());
    }
    // TODO 品牌里面的特殊符号标准化,euro·fit,euro fit,euro.fit
    // TODO 中文符号转换成英文符号julie‘s--julie's
}
