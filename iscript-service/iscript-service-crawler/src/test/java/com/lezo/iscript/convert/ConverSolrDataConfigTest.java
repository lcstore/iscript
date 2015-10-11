package com.lezo.iscript.convert;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class ConverSolrDataConfigTest {

    @Test
    public void testConfig() throws Exception {
        String content = FileUtils.readFileToString(new File("src/test/resources/nav.txt"), "UTF-8");
        // Pattern oReg = Pattern.compile("m\\.([a-zA-Z]+)");
        Pattern oReg = Pattern.compile("st\\.([a-zA-Z]+)");
        Matcher matcher = oReg.matcher(content);
        while (matcher.find()) {
            String param = matcher.group(1);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < param.length(); i++) {
                char value = param.charAt(i);
                if (Character.isLowerCase(value)) {
                    sb.append(Character.toUpperCase(value));
                } else {
                    sb.append("_");
                    sb.append(value);
                }
            }
            String destValue = sb.toString();
            content = content.replace("st." + param, "st." + destValue);
        }
        System.err.println(content);
    }
}
