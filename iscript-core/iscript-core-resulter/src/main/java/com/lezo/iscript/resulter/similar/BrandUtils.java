package com.lezo.iscript.resulter.similar;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import lombok.extern.log4j.Log4j;

import com.lezo.iscript.resulter.ident.BrandTokenizer;
import com.lezo.iscript.resulter.ident.EntityToken;
import com.lezo.iscript.resulter.ident.SectionToken;
import com.lezo.iscript.service.crawler.service.SynonymBrandService;
import com.lezo.iscript.spring.context.SpringBeanUtils;

@Log4j
public class BrandUtils {

    private static final Comparator<SectionToken> comparator = new Comparator<SectionToken>() {
        @Override
        public int compare(SectionToken o1, SectionToken o2) {
            int sub = o2.getTrust() - o1.getTrust();
            if (sub == 0) {
                int len1 = getCharLength(o1.getValue());
                int len2 = getCharLength(o2.getValue());
                return len2 - len1;
            }
            return sub;
        }

        private int getCharLength(String o1) {
            if (o1 == null) {
                return -1;
            }
            try {
                return o1.getBytes("GBK").length;
            } catch (UnsupportedEncodingException e) {
                log.warn("Chars:" + o1 + ",cause:", e);
            }
            return -1;
        }
    };

    public static List<SectionToken> toBrandTokens(String source) {
        EntityToken entity = new EntityToken(source);
        return toBrandTokens(entity);
    }

    public static List<SectionToken> toBrandTokens(EntityToken entity) {
        SynonymBrandService synonymBrandService = SpringBeanUtils.getBean(SynonymBrandService.class);
        BrandTokenizer tokenizer = new BrandTokenizer(synonymBrandService);
        String sTokenizer = tokenizer.getClass().getName();
        List<EntityToken> entityList = new ArrayList<EntityToken>(1);
        entityList.add(entity);
        tokenizer.identify(entityList);
        List<SectionToken> leaveList = new ArrayList<SectionToken>();
        EntityToken.getLeveChildren(leaveList, entity.getMaster());
        List<SectionToken> brandList = EntityToken.getTokensByTokenizer(leaveList, sTokenizer);
        if (!brandList.isEmpty()) {
            Collections.sort(brandList, comparator);
        }
        return brandList;
    }

    public static String toBrandUnify(String source) {
        EntityToken entity = new EntityToken(source);
        return toBrandUnify(entity);
    }

    public static String toBrandUnify(EntityToken entity) {
        SynonymBrandService synonymBrandService = SpringBeanUtils.getBean(SynonymBrandService.class);
        BrandTokenizer tokenizer = new BrandTokenizer(synonymBrandService);
        String sTokenizer = tokenizer.getClass().getName();
        List<EntityToken> entityList = new ArrayList<EntityToken>(1);
        entityList.add(entity);
        tokenizer.identify(entityList);
        List<SectionToken> leaveList = new ArrayList<SectionToken>();
        EntityToken.getLeveChildren(leaveList, entity.getMaster());
        List<SectionToken> brandList = EntityToken.getTokensByTokenizer(leaveList, sTokenizer);
        if (!brandList.isEmpty()) {
            Collections.sort(brandList, comparator);
            String sBrand = brandList.get(0).getValue();
            return synonymBrandService.getSynonyms(sBrand).iterator().next();
        }
        return null;
    }
}
