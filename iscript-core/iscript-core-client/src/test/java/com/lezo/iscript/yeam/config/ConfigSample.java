package com.lezo.iscript.yeam.config;

import java.io.StringWriter;
import java.util.regex.Pattern;

import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;

import com.lezo.iscript.rest.http.HttpClientManager;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.ClientConstant;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.service.DataBean;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ConfigSample implements ConfigParser {
    private DefaultHttpClient client = HttpClientManager.getDefaultHttpClient();
    private static Pattern oBarCodeReg = Pattern.compile("条形码[\\s]*([0-9]{13,})");

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String doParse(TaskWritable task) throws Exception {
        DataBean dataBean = getDataObject(task);
        return convert2TaskCallBack(dataBean, task);
    }

    private String convert2TaskCallBack(DataBean dataBean, TaskWritable task) throws Exception {
        JSONObject returnObject = new JSONObject();
        JSONUtils.put(returnObject, ClientConstant.KEY_CALLBACK_RESULT, JSONUtils.EMPTY_JSONOBJECT);
        if (dataBean != null) {
            dataBean.getTargetList().add("ProductDto");
            dataBean.getTargetList().add("ProductStatDto");

            ObjectMapper mapper = new ObjectMapper();
            StringWriter writer = new StringWriter();
            mapper.writeValue(writer, dataBean);
            String dataString = writer.toString();

            JSONUtils.put(returnObject, ClientConstant.KEY_STORAGE_RESULT, dataString);
        }
        return returnObject.toString();
    }

    /**
     * {"dataList":[],"nextList":[]}
     * 
     * @param task
     * @return
     * @throws Exception
     */
    private DataBean getDataObject(TaskWritable task) throws Exception {
        String url = task.get("url").toString();
        DataBean dataBean = new DataBean();
        return dataBean;
    }

}