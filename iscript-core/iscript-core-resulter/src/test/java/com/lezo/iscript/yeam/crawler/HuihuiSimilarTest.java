package com.lezo.iscript.yeam.crawler;

import org.junit.Test;

import com.lezo.iscript.yeam.writable.TaskWritable;

public class HuihuiSimilarTest {

    @Test
    public void testHuihuiSimilar() throws Exception {
        HuihuiSimilar config = new HuihuiSimilar();
        TaskWritable task = new TaskWritable();
        task.put("url", "http://item.yhd.com/item/21383250");
        task.put("name", "百草味 手剥山核桃 奶油味 190g/袋");
        String rs = config.doParse(task);
        System.err.println(rs);
    }
}
