package com.lezo.iscript.resulter.ident;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.lezo.iscript.service.crawler.service.SynonymBrandService;
import com.lezo.iscript.utils.CharsUtils;

public class BrandTokenizer extends AbstractTokenizer {
    private static final Set<String> brandAssistKeySet = new HashSet<String>();
    static {
        brandAssistKeySet.add("productBrand");
        brandAssistKeySet.add("品牌");
        brandAssistKeySet.add("商品品牌");
    }
    private SynonymBrandService synonymBrandService;

    public BrandTokenizer(SynonymBrandService synonymBrandService) {
        super();
        this.synonymBrandService = synonymBrandService;
    }

    @Override
    protected void doToken(List<EntityToken> entityTokens) {

        for (EntityToken entity : entityTokens) {
            List<SectionToken> tokenList = doToken(entity);
            for (SectionToken token : tokenList) {
                token.setStable(true);
                token.getParent().addChild(token);
            }
        }
    }

    private List<SectionToken> doToken(EntityToken entity) {
        List<SectionToken> tokenList = new ArrayList<SectionToken>();
        tokenAssist(tokenList, entity);
        tokenChildren(tokenList, entity);
        return new ArrayList<SectionToken>(tokenList);
    }

    private void tokenChildren(Collection<SectionToken> tokenCollection, EntityToken entity) {
        List<SectionToken> leaveList = new ArrayList<SectionToken>();
        EntityToken.getLeveChildren(leaveList, entity.getMaster());
        Iterator<String> it = synonymBrandService.iteratorKeys();
        while (it.hasNext()) {
            String token = it.next();
            for (SectionToken sectionToken : leaveList) {
                String unifyValue = CharsUtils.unifyChars(sectionToken.getValue());
                if (CharsUtils.contains(unifyValue, token)) {
                    int index = unifyValue.indexOf(token);
                    if (index > 0) {
                        String headChars = unifyValue.substring(0, index);
                        String tailChars = unifyValue.substring(index + token.length());
                        if (headChars.matches("[0-9a-zA-Z]+$") || tailChars.matches("^[0-9a-zA-Z]+")) {
                            continue;
                        }
                    }
                    SectionToken newToken = new SectionToken(sectionToken.getKey(), token);
                    newToken.setParent(sectionToken);
                    newToken.setTokenizer(this.getClass().getName());
                    newToken.setTrust(80);
                    tokenCollection.add(newToken);
                }
            }
        }
    }

    private void tokenAssist(Collection<SectionToken> tokenCollection, EntityToken entity) {
        List<SectionToken> assists = entity.getAssists();
        if (CollectionUtils.isEmpty(assists)) {
            return;
        }
        List<SectionToken> brandTokens = new ArrayList<SectionToken>();
        for (SectionToken token : assists) {
            if (brandAssistKeySet.contains(token.getKey())) {
                brandTokens.add(token);
            }
        }
        for (SectionToken token : brandTokens) {
            String sBrand = token.getValue();
            if (StringUtils.isEmpty(sBrand)) {
                continue;
            }
            sBrand = sBrand.replaceAll("[\\s]*（[\\s]*", "(");
            sBrand = sBrand.replaceAll("[\\s]*）[\\s]*", ")");
            sBrand = sBrand.trim();
            Pattern oReg = Pattern.compile("^(.+?)\\((.+)\\)$");
            String sMain = entity.getMaster().getValue();
            Matcher matcher = oReg.matcher(sBrand);
            while (matcher.find()) {
                int index = 0;
                String destBrand = matcher.group(++index);
                if (sMain.contains(destBrand)) {
                    String key = token.getKey();
                    SectionToken newToken = new SectionToken(key, destBrand);
                    newToken.setParent(entity.getMaster());
                    newToken.setTokenizer(this.getClass().getName());
                    newToken.setTrust(100);
                    tokenCollection.add(newToken);
                }
                destBrand = matcher.group(++index);
                if (sMain.contains(destBrand)) {
                    String key = token.getKey();
                    SectionToken newToken = new SectionToken(key, destBrand);
                    newToken.setParent(entity.getMaster());
                    newToken.setTokenizer(this.getClass().getName());
                    newToken.setTrust(100);
                    tokenCollection.add(newToken);
                }
            }
        }
        Iterator<String> it = synonymBrandService.iteratorKeys();
        while (it.hasNext()) {
            String token = it.next();
            for (SectionToken sectionToken : brandTokens) {
                String unifyValue = CharsUtils.unifyChars(sectionToken.getValue());
                if (CharsUtils.contains(unifyValue, token)) {
                    String headChars = "([0-9a-zA-Z]+" + token + ")";
                    String tailChars = "(" + token + "[0-9a-zA-Z]+)";
                    Pattern oReg = Pattern.compile(headChars + "|" + tailChars);
                    Matcher matcher = oReg.matcher(unifyValue);
                    if (matcher.find()) {
                        continue;
                    }
                    SectionToken newToken = new SectionToken(sectionToken.getKey(), token);
                    newToken.setParent(entity.getMaster());
                    newToken.setTokenizer(this.getClass().getName());
                    newToken.setTrust(100);
                    tokenCollection.add(newToken);
                }
            }
        }

    }
}
