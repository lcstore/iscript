package com.lezo.iscript.match.algorithm.cluster;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import lombok.extern.log4j.Log4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.lezo.iscript.match.algorithm.ICluster;
import com.lezo.iscript.match.pojo.CellAssort;
import com.lezo.iscript.match.pojo.SimilarCenter;
import com.lezo.iscript.match.pojo.SimilarFact;
import com.lezo.iscript.match.pojo.SimilarIn;
import com.lezo.iscript.match.pojo.SimilarOut;
import com.lezo.iscript.match.pojo.SimilarPart;
import com.lezo.iscript.match.utils.CellAssortUtils;
import com.lezo.iscript.match.utils.SimilarUtils;
import com.lezo.iscript.utils.BarCodeUtils;

@Log4j
public class SimilarCluster implements ICluster {
    private static final Comparator<SimilarCenter> CMP_CENTER = new Comparator<SimilarCenter>() {

        @Override
        public int compare(SimilarCenter o1, SimilarCenter o2) {
            int count = o2.getOuts().size() - o1.getOuts().size();
            if (count == 0) {
                int i1 = getIntegrity(o1.getValue());
                int i2 = getIntegrity(o2.getValue());
                return i2 - i1;
            }
            return count;
        }
    };

    @Override
    public List<SimilarCenter> doCluster(List<SimilarIn> similarIns, List<SimilarFact> facts) {
        if (CollectionUtils.isEmpty(similarIns)) {
            return Collections.emptyList();
        }
        List<SimilarCenter> oldCenters = null;
        List<SimilarCenter> newCenters = null;
        int maxCount = 100;
        int index = 0;
        while (true) {
            newCenters = newCenters(oldCenters, similarIns, facts);
            // selectCenter
            for (SimilarIn in : similarIns) {
                selectCenter(newCenters, in, facts);
            }
            if (isSteady(oldCenters, newCenters)) {
                break;
            }
            oldCenters = newCenters;
            if (index++ >= maxCount) {
                log.warn("abort,do center time:" + index + ",src count:" + similarIns.size());
                break;
            }
        }
        newCenters = mergeCenters(newCenters, facts);
        return newCenters;
    }

    private List<SimilarCenter> mergeCenters(List<SimilarCenter> centers, List<SimilarFact> facts) {
        List<Set<SimilarCenter>> groupCenters = Lists.newArrayList();
        Set<SimilarCenter> handleSet = Sets.newHashSet();
        for (SimilarCenter center : centers) {
            if (handleSet.contains(center)) {
                continue;
            }
            Set<SimilarCenter> sortCenterSet = assortCenters(center, centers, handleSet, facts);
            handleSet.addAll(sortCenterSet);
            groupCenters.add(sortCenterSet);
        }
        List<SimilarCenter> newCenters = Lists.newArrayList();
        for (Set<SimilarCenter> gCenterSet : groupCenters) {
            List<SimilarCenter> assortCenters = Lists.newArrayList(gCenterSet);
            SimilarCenter targetCenter = selectTargetCenter(assortCenters);
            newCenters.add(targetCenter);
            for (SimilarCenter center : assortCenters) {
                if (targetCenter == center) {
                    continue;
                }
                SimilarOut newOut = createSimilarOut(center.getValue(), targetCenter.getValue(), facts);
                targetCenter.getOuts().add(newOut);
                if (!center.getOuts().isEmpty()) {
                    for (SimilarOut out : center.getOuts()) {
                        SimilarOut mOut = createSimilarOut(out.getCurrent(), targetCenter.getValue(), facts);
                        targetCenter.getOuts().add(mOut);
                    }
                }
            }
        }
        return newCenters;
    }

    private SimilarCenter selectTargetCenter(List<SimilarCenter> assortCenters) {
        Collections.sort(assortCenters, CMP_CENTER);
        return assortCenters.get(0);
    }

    /**
     * 中心归类合并
     * 
     * @param mCenter
     * @param newCenters
     * @param handleSet
     * @param facts
     * @return
     */
    private Set<SimilarCenter> assortCenters(SimilarCenter mCenter, List<SimilarCenter> newCenters,
            Set<SimilarCenter> handleSet, List<SimilarFact> facts) {
        Set<SimilarCenter> toCenterSet = Sets.newHashSet(mCenter);
        SimilarIn in = mCenter.getValue();
        int minScore = 80;
        for (SimilarCenter center : newCenters) {
            // in 为center的参照商品，则忽略
            if (center.getValue().getSkuCode().equals(in.getSkuCode())) {
                continue;
            }
            if (handleSet.contains(center)) {
                continue;
            }
            SimilarOut newOut = createSimilarOut(in, center.getValue(), facts);
            if (newOut.getScore() >= minScore) {
                toCenterSet.add(center);
            }
        }
        return toCenterSet;
    }

    private void selectCenter(List<SimilarCenter> newCenters, SimilarIn in, List<SimilarFact> facts) {
        SimilarCenter toCenter = null;
        SimilarOut targetOut = null;
        for (SimilarCenter center : newCenters) {
            // in 为center的参照商品，则忽略
            if (center.getValue().getSkuCode().equals(in.getSkuCode())) {
                // TODO merge fields
                return;
            }
            SimilarOut newOut = createSimilarOut(in, center.getValue(), facts);
            if (targetOut == null || targetOut.getScore() < newOut.getScore()) {
                toCenter = center;
                targetOut = newOut;
            }
        }
        toCenter.getOuts().add(targetOut);

    }

    protected SimilarOut createSimilarOut(SimilarIn current, SimilarIn refer, List<SimilarFact> facts) {
        // current.getClass().getDeclaredField(name);
        SimilarOut destOut = new SimilarOut();
        destOut.setCurrent(current);
        destOut.setRefer(refer);
        List<SimilarPart> parts = Lists.newArrayList();
        destOut.setParts(parts);
        boolean forceAccess = true;
        float total = 0;
        Class<SimilarIn> inClass = SimilarIn.class;
        for (SimilarFact fact : facts) {
            String name = fact.getName();
            Field field = FieldUtils.getDeclaredField(inClass, name, forceAccess);
            if (field == null) {
                continue;
            }
            try {
                CellAssort curAssort = (CellAssort) FieldUtils.readField(field, current);
                CellAssort referAssort = (CellAssort) FieldUtils.readField(field, refer);
                int score = fact.getSimilar().similar(curAssort, referAssort);
                total += score * fact.getFact();
                SimilarPart newPart = new SimilarPart();
                newPart.setCurrent(curAssort);
                newPart.setRefer(referAssort);
                newPart.setScore(score);
                destOut.getParts().add(newPart);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("calc similar:" + field.getName() + ",cause:", e);
            }
        }
        int dScore = (int) total;
        destOut.setScore(SimilarUtils.clamp(dScore));
        // score += getSimilarCanBlank(current.getBarCode(), refer.getBarCode(), 100, 20, 10);
        // score += getSimilarCanBlank(current.getWareCode(), refer.getWareCode(), 100, 10, 0);
        // score += getSimilar(current.getTokenBrand(), refer.getTokenBrand(), 40, 0, -100);
        // score += getSimilar(current.getTokenUnit(), refer.getTokenUnit(), 30, 10, -50);
        // score += getSimilar(current.getTokenModel(), refer.getTokenModel(), 30, 20, -60);
        return destOut;
    }

    private List<SimilarCenter> newCenters(List<SimilarCenter> oldCenters, List<SimilarIn> similarIns,
            List<SimilarFact> similarMap) {
        if (oldCenters == null) {
            int centerCount = similarIns.size() / 10;
            centerCount = centerCount < 2 ? 2 : centerCount;
            Set<Integer> indexSet = getRandomSet(similarIns.size(), centerCount);
            return initCenters(indexSet, similarIns);
        }
        printOldCenters(oldCenters);
        List<SimilarCenter> newCenters = Lists.newArrayList();
        for (SimilarCenter ct : oldCenters) {
            List<SimilarOut> bestOuts = Lists.newArrayList();
            List<SimilarOut> worstOuts = Lists.newArrayList();
            doAssort(ct, bestOuts, worstOuts);
            newCenterWithBest(ct, bestOuts, newCenters, similarMap);
            newCenterWithWorst(ct, worstOuts, newCenters, similarMap);
        }
        // mergeCenters
        // assortCenters
        return newCenters;
    }

    private void printOldCenters(List<SimilarCenter> oldCenters) {
        int index = 0;
        for (SimilarCenter ct : oldCenters) {
            index++;
            System.err.println(index + ",ct:" + ct.getValue());
            System.err.println(index + ",outs:" + ArrayUtils.toString(ct.getOuts()));
        }
        System.err.println("-----------------\n");
    }

    private void newCenterWithWorst(SimilarCenter center, List<SimilarOut> worstOuts, List<SimilarCenter> newCenters,
            List<SimilarFact> facts) {
        if (CollectionUtils.isEmpty(worstOuts)) {
            return;
        }
        Comparator<SimilarOut> cmp = new Comparator<SimilarOut>() {
            @Override
            public int compare(SimilarOut o1, SimilarOut o2) {
                return o1.getScore() - o2.getScore();
            }
        };
        Collections.sort(worstOuts, cmp);
        SimilarOut firstOut = worstOuts.get(0);
        SimilarOut lastOut = worstOuts.get(worstOuts.size() - 1);
        SimilarOut newOut = createSimilarOut(firstOut.getCurrent(), lastOut.getCurrent(), facts);
        if (newOut.getScore() < 70) {
            addNewCenter(firstOut.getCurrent(), newCenters);
            addNewCenter(lastOut.getCurrent(), newCenters);
        } else {
            addNewCenter(lastOut.getCurrent(), newCenters);
        }

    }

    private void addNewCenter(SimilarIn current, List<SimilarCenter> newCenters) {
        SimilarCenter newCenter = new SimilarCenter();
        newCenter.setValue(current);
        List<SimilarOut> outs = Lists.newArrayList();
        newCenter.setOuts(outs);
        newCenters.add(newCenter);
    }

    private void newCenterWithBest(SimilarCenter center, List<SimilarOut> bestOuts, List<SimilarCenter> newCenters,
            List<SimilarFact> facts) {
        SimilarIn newCenterIn = center.getValue();
        int maxCount = getIntegrity(newCenterIn);
        for (SimilarOut out : bestOuts) {
            int newCount = getIntegrity(out.getCurrent());
            if (maxCount < newCount) {
                newCenterIn = out.getCurrent();
            }
        }
        SimilarCenter newCenter = new SimilarCenter();
        newCenter.setValue(newCenterIn);
        List<SimilarOut> outs = Lists.newArrayList();
        newCenter.setOuts(outs);
        newCenters.add(newCenter);
    }

    /**
     * 获取输入信息的完整度
     * 
     * @param in
     * @return
     */
    private static int getIntegrity(SimilarIn in) {
        int count = 0;
        if (in == null) {
            return count;
        }
        if (BarCodeUtils.isBarCode(CellAssortUtils.toValue(in.getBarCode()))) {
            count += 100;
        }
        if (StringUtils.isNotBlank(CellAssortUtils.toValue(in.getTokenBrand()))) {
            count += 100;
        }
        if (StringUtils.isNotBlank(CellAssortUtils.toValue(in.getTokenUnit()))) {
            count += 40;
        }
        if (StringUtils.isNotBlank(CellAssortUtils.toValue(in.getTokenModel()))) {
            count += 20;
        }
        if (StringUtils.isNotBlank(CellAssortUtils.toValue(in.getWareCode()))) {
            count += 10;
        }
        return count;
    }

    /**
     * 最好、最差分类
     * 
     * @param center
     * @param bestOuts
     * @param worstOuts
     */
    private void doAssort(SimilarCenter center, List<SimilarOut> bestOuts, List<SimilarOut> worstOuts) {
        if (CollectionUtils.isEmpty(center.getOuts())) {
            return;
        }
        int lowScore = 70;
        for (SimilarOut out : center.getOuts()) {
            if (out.getScore() < lowScore) {
                worstOuts.add(out);
            } else {
                bestOuts.add(out);
            }
        }
    }

    /**
     * 是否已稳定
     * 
     * @param oldCenters
     * @param newCenters
     * @return
     */
    private boolean isSteady(List<SimilarCenter> oldCenters, List<SimilarCenter> newCenters) {
        if (oldCenters == null || newCenters == null) {
            return false;
        } else if (oldCenters.size() != newCenters.size()) {
            return false;
        }
        Map<String, SimilarCenter> keyCenterMap = Maps.newHashMap();
        for (SimilarCenter oct : oldCenters) {
            keyCenterMap.put(oct.getValue().getSkuCode(), oct);
        }
        boolean hasCenter = true;
        for (SimilarCenter nct : newCenters) {
            if (!keyCenterMap.containsKey(nct.getValue().getSkuCode())) {
                hasCenter = false;
                break;
            }
        }
        return hasCenter;
    }

    private List<SimilarCenter> initCenters(Set<Integer> indexSet, List<SimilarIn> similarIns) {
        List<SimilarCenter> centerList = new ArrayList<SimilarCenter>(indexSet.size());
        for (Integer index : indexSet) {
            SimilarCenter center = new SimilarCenter();
            center.setValue(similarIns.get(index));
            List<SimilarOut> outs = Lists.newArrayList();
            center.setOuts(outs);
            centerList.add(center);
        }
        return centerList;
    }

    private Set<Integer> getRandomSet(int size, int centerCount) {
        Set<Integer> indexSet = new HashSet<Integer>();
        while (indexSet.size() < centerCount && size > indexSet.size()) {
            int curNum = new Random().nextInt(size);
            indexSet.add(curNum);
        }
        return indexSet;
    }

}
