package com.lezo.iscript.match.algorithm.matcher;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.lezo.iscript.match.algorithm.IAdorner;
import com.lezo.iscript.match.algorithm.IAnalyser;
import com.lezo.iscript.match.algorithm.IMatcher;
import com.lezo.iscript.match.algorithm.IStrainer;
import com.lezo.iscript.match.algorithm.ITokenizer;
import com.lezo.iscript.match.algorithm.adorn.LowerCaseAdorner;
import com.lezo.iscript.match.algorithm.adorn.ReplaceAdorner;
import com.lezo.iscript.match.algorithm.analyse.BrandAnalyser;
import com.lezo.iscript.match.algorithm.analyse.ModelAnalyser;
import com.lezo.iscript.match.algorithm.analyse.UnitAnalyser;
import com.lezo.iscript.match.algorithm.cluster.SimilarCluster;
import com.lezo.iscript.match.algorithm.strainer.ContainStrainer;
import com.lezo.iscript.match.algorithm.strainer.SuffixStrainer;
import com.lezo.iscript.match.algorithm.tokenizer.BlankTokenizer;
import com.lezo.iscript.match.algorithm.tokenizer.BracketTokenizer;
import com.lezo.iscript.match.algorithm.tokenizer.BrandTokenizer;
import com.lezo.iscript.match.algorithm.tokenizer.UnitTokenizer;
import com.lezo.iscript.match.pojo.CellAssort;
import com.lezo.iscript.match.pojo.CellToken;
import com.lezo.iscript.match.pojo.SimilarCenter;
import com.lezo.iscript.match.pojo.SimilarIn;
import com.lezo.iscript.match.utils.CellAssortUtils;
import com.lezo.iscript.match.utils.SimilarFactUtils;

public class FoodMatcher implements IMatcher {
    private static List<IAdorner> adorners = Lists
            .newArrayList(new LowerCaseAdorner(), new ReplaceAdorner("^1号生鲜", ""));
    private static List<ITokenizer> tokenizers = Lists.newArrayList(new BlankTokenizer(),
            new BracketTokenizer(), new UnitTokenizer(), new BrandTokenizer());
    private static List<IStrainer> strainers = Lists.newArrayList(new SuffixStrainer(), new ContainStrainer(),
            new SuffixStrainer());
    private static IAnalyser brandAnalyser = new BrandAnalyser();
    private static IAnalyser unitAnalyser = new UnitAnalyser();
    private static IAnalyser modelAnalyser = new ModelAnalyser();
    private static SimilarCluster cluster = new SimilarCluster();
    @Override
    public List<SimilarCenter> doMatcher(List<SimilarIn> similarIns) {
        if (CollectionUtils.isEmpty(similarIns)) {
            return Collections.emptyList();
        }
        for (SimilarIn newIn : similarIns) {
            String origin = newIn.getProductName();
            for (IAdorner adorner : getAdorners()) {
                origin = adorner.doAdorn(origin);
            }
            List<CellToken> tokens = createCellTokens(origin);
            CellAssort assort = brandAnalyser.analyse(tokens);
            newIn.setTokenBrand(assort);
            tokens = CellAssortUtils.removeAssort(tokens, assort);
            assort = unitAnalyser.analyse(tokens);
            newIn.setTokenUnit(assort);
            tokens = CellAssortUtils.removeAssort(tokens, assort);
            assort = modelAnalyser.analyse(tokens);
            newIn.setTokenModel(assort);
            tokens = CellAssortUtils.removeAssort(tokens, assort);

            Comparator<CellToken> cmp = new Comparator<CellToken>() {
                @Override
                public int compare(CellToken o1, CellToken o2) {
                    return o1.getValue().compareTo(o2.getValue());
                }
            };
            Collections.sort(tokens, cmp);
            StringBuilder sb = new StringBuilder();
            for (CellToken tk : tokens) {
                if (sb.length() > 0) {
                    sb.append(" ");
                }
                sb.append(tk.getValue());
            }
            newIn.setRemain(CellAssortUtils.toAssort(sb.toString()));
        }
        return cluster.doCluster(similarIns, SimilarFactUtils.getDefaultFacts());
    }

    private List<CellToken> createCellTokens(String origin) {
        Set<CellToken> sumCellSet = Sets.newHashSet();
        for (ITokenizer tokenizer : tokenizers) {
            List<CellToken> tokenList = tokenizer.token(origin);
            if (CollectionUtils.isNotEmpty(tokenList)) {
                sumCellSet.addAll(tokenList);
            }
        }
        List<CellToken> targets = Lists.newArrayList(sumCellSet);
        for (IStrainer strainer : strainers) {
            targets = strainer.strain(targets);
        }
        return targets;
    }

    public List<IAdorner> getAdorners() {
        return adorners;
    }

}
