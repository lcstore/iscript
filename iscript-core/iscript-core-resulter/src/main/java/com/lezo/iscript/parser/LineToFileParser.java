package com.lezo.iscript.parser;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.lezo.iscript.IoGain;
import com.lezo.iscript.io.IoWatcher;

public class LineToFileParser implements IParser {

    @Override
    public void doParse(IoGain ioGain) {
        String type = ioGain.getIoSeed().getType();
        String key = ioGain.getIoSeed().toKey();
        System.err.println("key:" + key);
        String data = ioGain.getValue().toString();
        File file =
                new File("/apps/src/codes/lezo/data/20150913/" + type, Math.abs(key.hashCode()) + "_"
                        + System.currentTimeMillis()
                        + ".data");
        try {
            FileUtils.writeStringToFile(file, data, "UTF-8");
            IoWatcher.getInstance().addFile(ioGain.getIoSeed());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
