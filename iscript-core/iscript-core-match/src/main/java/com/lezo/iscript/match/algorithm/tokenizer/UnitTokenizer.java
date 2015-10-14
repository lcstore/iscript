package com.lezo.iscript.match.algorithm.tokenizer;

import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.lezo.iscript.match.algorithm.ITokenizer;
import com.lezo.iscript.match.map.UnitMapper;
import com.lezo.iscript.match.pojo.CellToken;

/**
 * 单位前是字母，目前无法切出单位(通用的单位切取，暂不考虑这种情况)
 * 
 * @author lezo
 * @since 2015年10月11日
 */
public class UnitTokenizer implements ITokenizer {
    private static final Pattern UNIT_REG = Pattern.compile("(?<=[^0-9a-zA-Z])([0-9.]+)([a-zA-Z\u4E00-\u9FA5]+)");

    @Override
    public List<CellToken> token(String origin) {
        if (StringUtils.isBlank(origin)) {
            return Collections.emptyList();
        }
        List<CellToken> tokens = Lists.newArrayList();
        Matcher matcher = UNIT_REG.matcher(origin);
        while (matcher.find()) {
            String sNum = matcher.group(1);
            String sUnit = matcher.group(2);
            sUnit = getUnit(sUnit);
            if (StringUtils.isBlank(sUnit)) {
                continue;
            }
            String sValue = sNum + sUnit;
            CellToken token = new CellToken();
            token.setCreator(this.getClass().getSimpleName());
            token.setOrigin(origin);
            token.setToken(sValue);
            token.setIndex(token.getOrigin().indexOf(token.getToken()));
            tokens.add(token);
        }
        return tokens;
    }

    private String getUnit(String sUnit) {
        if (StringUtils.isBlank(sUnit)) {
            return null;
        }
        UnitMapper mapper = UnitMapper.getInstance();
        int minLen = mapper.getMinLen();
        int maxLen = mapper.getMaxLen();
        StringBuilder sb = new StringBuilder();
        Stack<String> stack = new Stack<String>();
        for (int i = 0; i < sUnit.length(); i++) {
            sb.append(sUnit.charAt(i));
            int len = sb.length();
            if (len >= minLen) {
                String sValue = sb.toString();
                if (mapper.getSameSet(sValue) != null) {
                    stack.push(sValue);
                }
            }
            if (len >= maxLen) {
                break;
            }
        }
        return stack.isEmpty() ? null : stack.pop();
    }

}
