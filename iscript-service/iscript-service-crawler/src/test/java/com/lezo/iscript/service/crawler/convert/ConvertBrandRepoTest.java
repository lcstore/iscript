package com.lezo.iscript.service.crawler.convert;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.annotation.Rollback;

import com.google.common.collect.Lists;
import com.lezo.iscript.service.crawler.dto.BrandRepoDto;
import com.lezo.iscript.service.crawler.service.BrandRepoService;
import com.lezo.iscript.spring.context.SpringBeanUtils;

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

}
