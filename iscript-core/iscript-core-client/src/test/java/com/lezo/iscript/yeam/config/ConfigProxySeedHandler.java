package com.lezo.iscript.yeam.config;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.NativeJSON;
import org.mozilla.javascript.Scriptable;

import com.lezo.iscript.crawler.dom.rhino.ContextUtils;
import com.lezo.iscript.rest.http.HttpClientManager;
import com.lezo.iscript.utils.InetAddressUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.utils.encrypt.Base64Decryptor;
import com.lezo.iscript.yeam.ClientConstant;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.service.DataBean;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ConfigProxySeedHandler implements ConfigParser {
    private DefaultHttpClient client = HttpClientManager.getDefaultHttpClient();

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
        dataBean.getTargetList().add("ProxyAddrDto");
        dataBean.getTargetList().add("ProxyCollectHisDto");

        ObjectMapper mapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, dataBean);
        String dataString = writer.toString();

        JSONObject returnObject = new JSONObject();
        JSONUtils.put(returnObject, ClientConstant.KEY_CALLBACK_RESULT, JSONUtils.EMPTY_JSONOBJECT);
        JSONUtils.put(returnObject, ClientConstant.KEY_STORAGE_RESULT, dataString);
        return returnObject.toString();
    }

    private DataBean getDataObject(TaskWritable task) throws Exception {
        JSONObject argsObject = JSONUtils.getJSONObject(task.getArgs());
        JSONObject urlObject = getFetchUrls(argsObject);
        if (urlObject == null) {
            throw new IllegalArgumentException("get 0 fetch urls by url:" + task.get("url"));
        }
        int fetchPage = 0;
        String decodePageFun = JSONUtils.getString(argsObject, "DecodePageFun");
        Set<ProxyAddrDto> resultSet = new HashSet<ConfigProxySeedHandler.ProxyAddrDto>();
        Iterator<?> it = urlObject.keys();
        while (it.hasNext()) {
            String key = it.next().toString();
            JSONArray urlArray = JSONUtils.get(urlObject, key);
            for (int i = 0; i < urlArray.length(); i++) {
                String sProxyUrl = urlArray.getString(i);
                HttpGet get = new HttpGet(sProxyUrl);
                HttpResponse respone = client.execute(get);
                HttpEntity entity = respone.getEntity();
                byte[] dataBytes = EntityUtils.toByteArray(entity);
                String charset = getOrHeadCharset(entity.getContentType(), dataBytes);
                String html = new String(dataBytes, charset);
                List<ProxyAddrDto> findList = findProxy(html, decodePageFun);
                if (findList.size() < 5) {
                    Document dom = Jsoup.parse(html);
                    html = dom.text();
                    findList = findProxy(html, decodePageFun);
                }
                fetchPage++;
                if (findList.isEmpty()) {
                    break;
                } else {
                    resultSet.addAll(findList);
                }
            }
        }
        Long seedId = JSONUtils.getLong(argsObject, "seedId");
        for (ProxyAddrDto dataAddrDto : resultSet) {
            dataAddrDto.setSeedId(seedId);
        }
        ProxyCollectHisDto collectHisDto = new ProxyCollectHisDto();
        int totalCount = resultSet.size();
        collectHisDto.setTaskId(task.getId());
        collectHisDto.setSeedId(seedId);
        collectHisDto.setTotalCount(totalCount);
        collectHisDto.setTotalPage(urlObject.length());
        collectHisDto.setFetchPage(fetchPage);
        collectHisDto.getProxyList().addAll(resultSet);
        DataBean dataBean = new DataBean();
        dataBean.getDataList().add(collectHisDto);
        argsObject.remove("CreateUrlsFun");
        argsObject.remove("DecodePageFun");
        return dataBean;
    }

    private String getOrHeadCharset(Header header, byte[] dataBytes) throws Exception {
        if (header != null) {
            HeaderElement[] elements = header.getElements();
            if (elements.length > 0) {
                HeaderElement helem = elements[0];
                NameValuePair param = helem.getParameterByName("charset");
                if (param != null) {
                    return param.getValue();
                }
            }
        }
        String souce = new String(dataBytes, "GBK");
        int index = souce.indexOf("Content-Type");
        if (index > 0) {
            int maxLen = 100;
            maxLen = maxLen < souce.length() ? maxLen : souce.length();
            souce = souce.substring(index, index + maxLen);
            Pattern oReg = Pattern.compile("charset.*?=([a-zA-Z0-9\\-]{3,})");
            Matcher matcher = oReg.matcher(souce);
            if (matcher.find()) {
                return matcher.group(1).trim();
            }
        }
        return "UTF-8";
    }

    private JSONObject getFetchUrls(JSONObject argsObject) throws Exception {
        String url = JSONUtils.getString(argsObject, "url");
        String createUrlsFun = JSONUtils.getString(argsObject, "CreateUrlsFun");
        JSONObject urlObject = new JSONObject();
        String urlKey = "default";
        if (StringUtils.isEmpty(createUrlsFun)) {
            JSONArray urlArray = new JSONArray();
            if (url.indexOf("%s") > 0) {
                throw new RuntimeException("createUrlsFun is empty,and url:" + url);
            } else {
                urlArray.put(url);
            }
            JSONUtils.put(urlObject, urlKey, urlArray);
            return urlObject;
        }
        JSONObject paramObject = new JSONObject();
        JSONUtils.put(paramObject, "url", url);
        final String source = String.format("(function(args){%s})(%s);", createUrlsFun, paramObject.toString());
        String sResult = ContextUtils.doAction(new ContextAction() {
            @Override
            public Object run(Context cx) {
                Scriptable scope = cx.initStandardObjects();
                Object rsObject = ContextUtils.call(scope, source, "FetchUrls");
                return NativeJSON.stringify(cx, scope, rsObject, null, null);
            }
        }).toString();
        if (sResult.trim().startsWith("{")) {
            return JSONUtils.getJSONObject(sResult);
        } else {
            JSONArray urlArray = new JSONArray(sResult);
            JSONUtils.put(urlObject, urlKey, urlArray);
        }
        return urlObject;
    }

    public List<ProxyAddrDto> findProxy(String source, String decodePageFun) throws JSONException {
        source = doDecode(source, decodePageFun);
        JSONArray proxyArray = doProxyParser(source);
        List<ProxyAddrDto> dtoList = convert2Dto(proxyArray);
        return dtoList;
    }

    public String doDecode(String html, String decodePageFun) {
        if (StringUtils.isEmpty(decodePageFun)) {
            return html;
        }
        JSONObject argsObject = new JSONObject();
        JSONUtils.put(argsObject, "source", html);
        final String source = String.format("(function(args){%s})(%s);", decodePageFun, argsObject.toString());
        String sResult = ContextUtils.doAction(new ContextAction() {
            @Override
            public Object run(Context cx) {
                Scriptable scope = cx.initStandardObjects();
                Object rsObject = ContextUtils.call(scope, source, "decode");
                return Context.toString(rsObject);
            }
        }).toString();
        return sResult;
    }

    public JSONArray doProxyParser(String source) {
        Pattern oReg = Pattern.compile("([0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+)[^0-9]+?([0-9]{2,})", Pattern.MULTILINE);
        Matcher matcher = oReg.matcher(source);
        JSONArray proxyArray = new JSONArray();
        while (matcher.find()) {
            JSONObject ipObject = new JSONObject();
            String ipString = matcher.group(1);
            String portString = matcher.group(2);
            Integer port = NumberUtils.toInt(portString, -1);
            if (port > 0 && port < 65536) {
                JSONUtils.put(ipObject, "ip", InetAddressUtils.inet_aton(ipString));
                JSONUtils.put(ipObject, "port", Integer.valueOf(matcher.group(2)));
                proxyArray.put(ipObject);
            }
            // System.out.println(matcher.group(1) + ":" + matcher.group(2));
        }
        return proxyArray;
    }

    private String decode(String html) throws Exception {
        Pattern oReg = Pattern.compile("Base64.decode\\s*\\(.*?([0-9a-zA-Z=]+).*?\\)");
        Matcher matcher = oReg.matcher(html);
        Base64Decryptor decryptor = new Base64Decryptor();
        while (matcher.find()) {
            String ipEncode = matcher.group(1);
            System.out.println(ipEncode);
            String ipDecode = decryptor.decript(ipEncode.getBytes());
            html = html.replace(matcher.group(), ipDecode);
            // matcher.replaceFirst(ipDecode);
        }
        return html;
    }

    private List<ProxyAddrDto> convert2Dto(JSONArray proxyArray) throws JSONException {
        List<ProxyAddrDto> dtoList = new ArrayList<ProxyAddrDto>(proxyArray.length());
        for (int i = 0; i < proxyArray.length(); i++) {
            JSONObject ipObject = proxyArray.getJSONObject(i);
            ProxyAddrDto dto = new ProxyAddrDto();
            dto.setIp(JSONUtils.getLong(ipObject, "ip"));
            dto.setPort(JSONUtils.getInteger(ipObject, "port"));
            if (dto.getPort() > 0 && dto.getPort() < 65536) {
                dtoList.add(dto);
            }
        }
        return dtoList;
    }

    private static class ProxyCollectHisDto {
        private Long seedId;
        private Long taskId;
        private Integer totalPage = 0;
        private Integer fetchPage = 0;
        private Integer totalCount = 0;
        private Set<ProxyAddrDto> proxyList = new HashSet<ConfigProxySeedHandler.ProxyAddrDto>();

        public Long getSeedId() {
            return seedId;
        }

        public void setSeedId(Long seedId) {
            this.seedId = seedId;
        }

        public Integer getTotalPage() {
            return totalPage;
        }

        public void setTotalPage(Integer totalPage) {
            this.totalPage = totalPage;
        }

        public Integer getFetchPage() {
            return fetchPage;
        }

        public void setFetchPage(Integer fetchPage) {
            this.fetchPage = fetchPage;
        }

        public Integer getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(Integer totalCount) {
            this.totalCount = totalCount;
        }

        public Set<ProxyAddrDto> getProxyList() {
            return proxyList;
        }

        public void setProxyList(Set<ProxyAddrDto> proxyList) {
            this.proxyList = proxyList;
        }

        public Long getTaskId() {
            return taskId;
        }

        public void setTaskId(Long taskId) {
            this.taskId = taskId;
        }

    }

    public static Scriptable newScriptable(Scriptable parent) {
        Context cx = Context.enter();
        Scriptable newScope = cx.newObject(parent);
        newScope.setPrototype(parent);
        newScope.setParentScope(null);
        return newScope;
    }

    public static class ProxyAddrDto {
        private Long seedId;
        private Long ip;
        private Integer port;

        public Long getIp() {
            return ip;
        }

        public void setIp(Long ip) {
            this.ip = ip;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder().append(this.ip).append(this.port).toHashCode();
        }

        public Long getSeedId() {
            return seedId;
        }

        public void setSeedId(Long seedId) {
            this.seedId = seedId;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ProxyAddrDto other = (ProxyAddrDto) obj;
            return new EqualsBuilder().append(this.ip, other.getIp()).append(this.port, other.getPort()).isEquals();
        }
    }
}
