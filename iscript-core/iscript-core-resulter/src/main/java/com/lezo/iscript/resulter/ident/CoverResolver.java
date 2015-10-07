package com.lezo.iscript.resulter.ident;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.lezo.iscript.service.crawler.service.SynonymBrandService;
import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.utils.CharsUtils;

public class CoverResolver implements SimilarResolver {
    private static final String VALUE_SPLIT = " ";
    private SynonymBrandService synonymBrandService;

    public CoverResolver() {
        super();
        this.synonymBrandService = SpringBeanUtils.getBean(SynonymBrandService.class);
    }

    public CoverResolver(SynonymBrandService synonymBrandService) {
        super();
        this.synonymBrandService = synonymBrandService;
    }

    @Override
    public EntitySimilar doResolve(EntityToken lToken, EntityToken rToken) {
        // 用本身Token分割EntityToken
        List<SectionToken> leftList = new ArrayList<SectionToken>();
        EntityToken.getLeveChildren(leftList, lToken.getMaster());
        List<SectionToken> rightList = new ArrayList<SectionToken>();
        EntityToken.getLeveChildren(rightList, rToken.getMaster());
        int lTotalDist = getTokenDistance(leftList);
        int rTotalDist = getTokenDistance(rightList);

        String lString = toSectionChars(leftList);
        String rString = toSectionChars(rightList);
        // 覆盖同义词,获取差异文本
        TokenCover lCoverToken = doCoverTokens(lString, rightList);
        TokenCover rCoverToken = doCoverTokens(rString, leftList);
        doCover(lCoverToken, rCoverToken);
        lCoverToken.setEntity(lToken);
        rCoverToken.setEntity(rToken);

        int lUnCoverDist = getTokenDistance(lCoverToken.getUnCovers());
        int rUnCoverDist = getTokenDistance(rCoverToken.getUnCovers());
        // 计算两边cover的最小文本距离
        int charsDist = StringUtils.getLevenshteinDistance(lCoverToken.getCover(), rCoverToken.getCover());
        int unCoverDist = charsDist + lUnCoverDist + rUnCoverDist;
        int totalDist = (lTotalDist + rTotalDist);
        int unCoverScore = totalDist == 0 ? 100 : unCoverDist * 100 / totalDist;
        // 合并同义词token
        // 计算各个token的最小文本距离StringUtils.getLevenshteinDistance,token聚类
        // 文本长度、文本距离、权重，计算综合文本相似度
        // System.err.println("lString:" + lCoverToken.getCover());
        // System.err.println("rString:" + rCoverToken.getCover());
        // System.err.println("rString:"
        // + StringUtils.getLevenshteinDistance(lCoverToken.getCover(),
        // rCoverToken.getCover()));
        // System.err.println("lTotalDist:" + lTotalDist);
        // System.err.println("rTotalDist:" + rTotalDist);
        // System.err.println("lUnCoverDist:" + lUnCoverDist);
        // System.err.println("rUnCoverDist:" + rUnCoverDist);
        // System.err.println("unCoverScore:" + unCoverScore);
        EntitySimilar similar = new EntitySimilar();
        similar.setSimilar(100 - unCoverScore);
        similar.setLeftCover(lCoverToken);
        similar.setRightCover(rCoverToken);
        return similar;
    }

    private int getTokenDistance(List<SectionToken> lTokens) {
        int distance = 0;
        if (CollectionUtils.isNotEmpty(lTokens)) {
            Set<String> hasSet = new HashSet<String>();
            for (SectionToken lToken : lTokens) {
                String value = CharsUtils.unifyChars(lToken.getValue());
                if (hasSet.contains(value)) {
                    continue;
                }
                if (BrandTokenizer.class.getName() == lToken.getTokenizer()) {
                    Set<String> brandSet = synonymBrandService.getSynonyms(value);
                    if (brandSet != null) {
                        hasSet.addAll(brandSet);
                    }
                }
                hasSet.add(value);
                distance += value.length() * lToken.getTrust();
            }
        }
        return distance;
    }

    private void doCover(TokenCover lCoverToken, TokenCover rCoverToken) {
        String lString = lCoverToken.getCover();
        String rString = rCoverToken.getCover();
        String[] lArray = lString.split(VALUE_SPLIT);
        String[] rArray = rString.split(VALUE_SPLIT);
        lString = doCover(lString, rArray);
        rString = doCover(rString, lArray);
        lString = lString.replaceAll("[^0-9a-zA-Z\u4e00-\u9fa5]+", VALUE_SPLIT);
        rString = rString.replaceAll("[^0-9a-zA-Z\u4e00-\u9fa5]+", VALUE_SPLIT);
        lCoverToken.setCover(lString);
        rCoverToken.setCover(rString);
        doCover(lCoverToken.getUnCovers(), rCoverToken.getUnCovers());
    }

    private void doCover(List<SectionToken> lTokenList, List<SectionToken> rTokenList) {
        if (CollectionUtils.isEmpty(lTokenList) || CollectionUtils.isEmpty(rTokenList)) {
            return;
        }
        Map<String, SectionToken> lValueMap = toValueTokenMap(lTokenList);
        Map<String, SectionToken> rValueMap = toValueTokenMap(rTokenList);
        for (SectionToken token : lTokenList) {
            String key = CharsUtils.unifyChars(token.getValue());
            SectionToken rToken = rValueMap.get(key);
            if (rToken != null) {
                if (BrandTokenizer.class.getName() == token.getTokenizer()
                        || BrandTokenizer.class.getName() == rToken.getTokenizer()) {
                    Set<String> synValueSet = synonymBrandService.getSynonyms(token.getValue());
                    if (synValueSet == null) {
                        synValueSet = new HashSet<String>();
                        synValueSet.add(token.getValue());
                    }
                    for (String brand : synValueSet) {
                        brand = CharsUtils.unifyChars(brand);
                        lValueMap.remove(brand);
                        rValueMap.remove(brand);
                    }
                } else {
                    lValueMap.remove(key);
                    rValueMap.remove(key);
                }
            }
        }
        lTokenList.clear();
        for (Entry<String, SectionToken> entry : lValueMap.entrySet()) {
            lTokenList.add(entry.getValue());
        }
        rTokenList.clear();
        for (Entry<String, SectionToken> entry : rValueMap.entrySet()) {
            rTokenList.add(entry.getValue());
        }
    }

    private String doCover(String lString, String[] rArray) {
        if (rArray == null) {
            return lString;
        }
        lString = CharsUtils.unifyChars(lString);
        for (String chars : rArray) {
            if (StringUtils.isNotBlank(chars)) {
                chars = CharsUtils.unifyChars(chars);
                lString = lString.replace(chars, VALUE_SPLIT);
            }
        }
        return lString;
    }

    private TokenCover doCoverTokens(String lString, List<SectionToken> rightList) {
        if (CollectionUtils.isEmpty(rightList)) {
            TokenCover coverToken = new TokenCover();
            coverToken.setCover(lString);
            coverToken.setUnCovers(new ArrayList<SectionToken>());
            return coverToken;
        }
        EntityToken.sortTokenLengthDesc(rightList);
        lString = CharsUtils.unifyChars(lString);
        List<SectionToken> unCovers = new ArrayList<SectionToken>(rightList.size());
        Set<String> hasSet = new HashSet<String>();
        for (SectionToken st : rightList) {
            if (StringUtils.isBlank(lString) || StringUtils.isBlank(st.getValue())) {
                continue;
            }
            Set<String> valueSet = new HashSet<String>();
            valueSet.add(CharsUtils.unifyChars(st.getValue()));
            if (BrandTokenizer.class.getName() == st.getTokenizer()) {
                Set<String> synValueSet = synonymBrandService.getSynonyms(st.getValue());
                if (synValueSet != null) {
                    valueSet.addAll(synValueSet);
                }
            }
            boolean unCover = true;
            for (String chars : valueSet) {
                chars = CharsUtils.unifyChars(chars);
                if (lString.contains(chars)) {
                    lString = lString.replace(chars, VALUE_SPLIT);
                    unCover = false;
                    hasSet.add(chars);
                } else if (hasSet.contains(chars)) {
                    unCover = false;
                }
            }
            if (unCover) {
                unCovers.add(st);
            }
        }
        TokenCover coverToken = new TokenCover();
        coverToken.setCover(lString);
        coverToken.setUnCovers(unCovers);
        return coverToken;
    }

    private String toSectionChars(List<SectionToken> tokens) {
        StringBuilder sb = new StringBuilder();
        Set<String> hasSet = new HashSet<String>();
        for (SectionToken st : tokens) {
            String chars = st.getValue();
            if (StringUtils.isBlank(chars)) {
                continue;
            }
            chars = CharsUtils.unifyChars(chars);
            if (hasSet.contains(chars)) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(VALUE_SPLIT);
            }
            sb.append(chars);
            hasSet.add(chars);
        }
        return sb.toString();
    }

    private Map<String, SectionToken> toValueTokenMap(List<SectionToken> lTokenList) {
        Map<String, SectionToken> lValueTokenMap = new HashMap<String, SectionToken>();
        for (SectionToken lToken : lTokenList) {
            String unifyValue = CharsUtils.unifyChars(lToken.getValue());
            lValueTokenMap.put(unifyValue, lToken);
        }
        return lValueTokenMap;
    }
}
