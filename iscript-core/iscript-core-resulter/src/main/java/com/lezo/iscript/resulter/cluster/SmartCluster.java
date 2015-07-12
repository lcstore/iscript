package com.lezo.iscript.resulter.cluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;

import com.lezo.iscript.resulter.ident.EntitySimilar;
import com.lezo.iscript.resulter.ident.EntityToken;
import com.lezo.iscript.resulter.ident.ModelTokenizer;
import com.lezo.iscript.resulter.ident.SectionToken;
import com.lezo.iscript.resulter.ident.SimilarResolver;
import com.lezo.iscript.resulter.ident.TokenCover;
import com.lezo.iscript.utils.CharsUtils;

public class SmartCluster {
    private static final int MIN_SIMILAR = 60;
    private static final int MIN_CENTER_SIMILAR = 75;
    private static final int MAX_SCATTERED_SIMILAR = 70;
    private SimilarResolver resolver;

    public SmartCluster(SimilarResolver resolver) {
        super();
        this.resolver = resolver;
    }

    public List<CenterToken> cluster(List<EntityToken> entityList) {
        if (CollectionUtils.isEmpty(entityList) || entityList.size() < 2) {
            throw new IllegalArgumentException("points number must more than 2");
        }
        // sumsing 是品牌了就不能是其他的
        int centerCount = 2;
        Set<Integer> indexSet = getRandomSet(entityList.size(), centerCount);
        List<CenterToken> centerList = new ArrayList<CenterToken>(centerCount);
        for (Integer index : indexSet) {
            CenterToken center = new CenterToken(entityList.get(index));
            centerList.add(center);
        }
        int maxCount = 2;
        while (maxCount > 0) {
            centerList = resetCenters(centerList);
            for (EntityToken et : entityList) {
                CenterToken destCenter = selectCenter(et, centerList);
                destCenter.addMember(et);
            }
            int originCount = centerList.size();
            centerList = mergeCenters(centerList);
            centerList = assortCenters(centerList);
            if (centerList.size() == originCount) {
                maxCount--;
            } else {
                maxCount = 0;
            }
        }

        return centerList;
    }

    private List<CenterToken> resetCenters(List<CenterToken> centerList) {
        for (CenterToken center : centerList) {
            center.clear();
        }
        return centerList;
    }

    private List<CenterToken> assortCenters(List<CenterToken> centerList) {
        List<CenterToken> newCenters = new ArrayList<CenterToken>();
        for (CenterToken center : centerList) {
            List<EntityToken> scatters = getScattered(center);
            if (scatters != null) {
                for (EntityToken sc : scatters) {
                    newCenters.add(new CenterToken(sc));
                }
            }
        }
        if (!newCenters.isEmpty()) {
            centerList.addAll(newCenters);
        }
        return centerList;
    }

    private List<EntityToken> getScattered(CenterToken center) {
        if (CollectionUtils.isEmpty(center.getMembers()) || CollectionUtils.isEmpty(center.getSimilars())) {
            return null;
        }
        int total = center.getMembers().size();
        if (total < 3) {
            return null;
        }
        Map<String, EntityToken> modelEntityMap = new HashMap<String, EntityToken>();
        for (EntitySimilar sm : center.getSimilars()) {
            if (sm.getSimilar() < MAX_SCATTERED_SIMILAR) {
                SectionToken rModel = getUnCoverModel(sm.getRightCover());
                if (rModel != null) {
                    SectionToken lModel = getUnCoverModel(sm.getLeftCover());
                    if (lModel != null) {
                        modelEntityMap.put(CharsUtils.unifyChars(rModel.getValue()), sm.getRightCover().getEntity());
                    }
                }
            }
        }
        return new ArrayList<EntityToken>(modelEntityMap.values());
    }

    private SectionToken getUnCoverModel(TokenCover leftCover) {
        if (CollectionUtils.isEmpty(leftCover.getUnCovers())) {
            return null;
        }
        Pattern oNumReg = Pattern.compile("[0-9]+");
        Pattern oWordReg = Pattern.compile("[a-zA-Z]+");
        String tokenizer = ModelTokenizer.class.getName();
        for (SectionToken unCover : leftCover.getUnCovers()) {
            if (!tokenizer.equals(unCover.getTokenizer())) {
                continue;
            }
            Matcher matcher = oNumReg.matcher(unCover.getValue());
            if (!matcher.find()) {
                continue;
            }
            matcher = oWordReg.matcher(unCover.getValue());
            if (matcher.find() && unCover.getValue().length() >= 3 && unCover.getTrust() >= 100) {
                return unCover;
            }
        }
        return null;
    }

    private List<CenterToken> mergeCenters(List<CenterToken> centerList) {
        boolean hasMerge = true;
        while (hasMerge) {
            hasMerge = false;
            int size = centerList.size();
            for (int i = 0; i < size; i++) {
                if (doMerge(i, centerList)) {
                    hasMerge = true;
                    break;
                }
            }
        }
        return centerList;

    }

    private boolean doMerge(int index, List<CenterToken> centerList) {
        CenterToken center = centerList.get(index);
        for (int j = index + 1; j < centerList.size(); j++) {
            CenterToken inCenter = centerList.get(j);
            if (inCenter == center) {
                continue;
            }
            EntitySimilar tokenSimilar = resolver.doResolve(center.getCenter(), inCenter.getCenter());
            if (tokenSimilar.getSimilar() >= MIN_CENTER_SIMILAR) {
                int lLen = tokenSimilar.getLeftCover().getCover().length();
                int rLen = tokenSimilar.getRightCover().getCover().length();
                if (lLen < rLen) {
                    centerList.remove(inCenter);
                } else {
                    centerList.remove(center);
                }
                return true;
            }
        }
        return false;
    }

    private CenterToken selectCenter(EntityToken entity, List<CenterToken> centerList) {
        CenterToken selectCenter = null;
        EntitySimilar maxSimilar = null;
        for (CenterToken center : centerList) {
            EntitySimilar tokenSimilar = resolver.doResolve(center.getCenter(), entity);
            if (maxSimilar == null || maxSimilar.getSimilar() < tokenSimilar.getSimilar()) {
                maxSimilar = tokenSimilar;
                selectCenter = center;
            }

        }
        if (maxSimilar.getSimilar() < MIN_SIMILAR || isVariant(maxSimilar)) {
            selectCenter = new CenterToken(entity);
            centerList.add(selectCenter);
        } else {
            selectCenter.addSimilar(maxSimilar);
        }
        return selectCenter;
    }

    private boolean isVariant(EntitySimilar maxSimilar) {
        if (maxSimilar.getSimilar() >= MIN_CENTER_SIMILAR) {
            return false;
        }
        Pattern oReg = Pattern.compile("([a-zA-Z]+[0-9]*[a-zA-Z]*)|([a-zA-Z]*[0-9]+[a-zA-Z]+)");
        List<SectionToken> unCovers = maxSimilar.getLeftCover().getUnCovers();
        List<SectionToken> modelList = EntityToken.getTokensByTokenizer(unCovers, ModelTokenizer.class.getName());
        if (hasVary(modelList, oReg)) {
            return true;
        }
        unCovers = maxSimilar.getRightCover().getUnCovers();
        modelList = EntityToken.getTokensByTokenizer(unCovers, ModelTokenizer.class.getName());
        if (hasVary(modelList, oReg)) {
            return true;
        }
        return false;
    }

    private boolean hasVary(List<SectionToken> modelList, Pattern oReg) {
        for (SectionToken mToken : modelList) {
            Matcher matcher = oReg.matcher(mToken.getValue());
            if (matcher.find()) {
                return true;
            }
        }
        return false;
    }

    private Set<Integer> getRandomSet(int maxNum, int setCount) {
        Set<Integer> indexSet = new HashSet<Integer>();
        while (indexSet.size() < setCount) {
            int curNum = new Random().nextInt(maxNum);
            indexSet.add(curNum);
        }
        return indexSet;
    }
}
