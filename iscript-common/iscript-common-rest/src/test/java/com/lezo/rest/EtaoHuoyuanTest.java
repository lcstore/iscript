package com.lezo.rest;

import org.junit.Assert;
import org.junit.Test;

import com.taobao.api.internal.util.TaobaoUtils;

public class EtaoHuoyuanTest {

    @Test
    public void testSignSearch_OK() throws Exception {
        String appKey = "21702307";
        String keySign = TaobaoUtils.byte2hex(TaobaoUtils.encryptMD5(appKey)).toLowerCase();
        Assert.assertEquals("5e1687ec37e9b76ee7b82464c11bd10b", keySign);
        String version = "2.0";
        String verSign = TaobaoUtils.byte2hex(TaobaoUtils.encryptMD5(version)).toLowerCase();
        String timestamp = "1447083131112";
        Assert.assertEquals("d41d8cd98f00b204e9800998ecf8427e", verSign);
        String appSecret =
                "AolwXt5ylKYeiRE4v7x1aMOfGo3AhRJVUufTOEBWIAzx"
                        .toLowerCase();
        String api = "mtop.etao.kaka.barcode.search";
        String source =
                appSecret + "&" + keySign + "&" + api + "&" + version
                        + "&460011607507288&861000020674881&" + verSign
                        + "&" + timestamp;
        String sign = TaobaoUtils.byte2hex(TaobaoUtils.encryptMD5(source)).toLowerCase();
        System.err.println("sign:" + sign);
        Assert.assertEquals("486c62325b6666258072f37adf495d25", sign);
    }

    @Test
    public void testSignGetTimestamp_OK() throws Exception {
        String appKey = "21702307";
        String keySign = TaobaoUtils.byte2hex(TaobaoUtils.encryptMD5(appKey)).toLowerCase();
        Assert.assertEquals("5e1687ec37e9b76ee7b82464c11bd10b", keySign);
        String verSign = TaobaoUtils.byte2hex(TaobaoUtils.encryptMD5("")).toLowerCase();
        Assert.assertEquals("d41d8cd98f00b204e9800998ecf8427e", verSign);
        String appSecret =
                "82c7836fd0ce8870d6d990aa03dc7ca2624a1557d3355a76fb673bed29983a03b0520692fc69ac5cb3aaa06ab7f708a8039ddfa1ff1ea1b1f841a12c707b2da2670f97f12370b0af263c05d89a42abe1858fcbc2d01e253613a3d51fbedc9efe09497b2e0e9716f7db5bd713c268377f8d6ef000f6c2be6cbdc46ae9f9458f6219c6c06e8d93369372d5650838057eb36fa5ad6f01a24e33e726cfc40c8d96ca9e86e7287eb49cf56953b13ec185030f66763ed7de3d445d86f0700fbeba934167cdc036c3c40e2808383c7ceee3fe3cc0daa3dd2c8cc470590f21fd1dc5b25afdcabebbd90fde861be8bbe765e9e0ae09e12a3ec7e66d882f06c69f93d9cedd903a7cbb732e5e3aa0a19e7191e77c0c1850b028f327f1133d523f6ff27b355b133175a4f3da575b5a1b54fa8619919a2935624192513dba8aa9ae61e0f2aa861d54bb35845aa2311520007000a43d4388139afbc259c285a887e17aa296596df5f867376f4efb9f090b9a43cfd6eb1747b2bafa743454da704709e6205458a34007bebef8b35fa346bea92ef2a1b0b02442209599028b17da94bf282fadc2365f30c3cf6cd2ad32ad8599c1a74d9ff3b06e5871d0b8927b15c0b65f12e7a9f80c046d65982e0cfeed495e22a4160d18b294e04012b6d3b2cb1b6f4b316580b9"
                        .toLowerCase();
        String source =
                appSecret + "&" + keySign + "&mtop.common.getTimestamp&*&860308028232581&460026214427182&" + verSign
                        + "&1447002867861";
        String sign = TaobaoUtils.byte2hex(TaobaoUtils.encryptMD5(source)).toLowerCase();
        System.err.println("sign:" + sign);
        Assert.assertEquals("86a5f960498a4435565885de1d9c442f", sign);
    }

    @Test
    public void testSignGetTimestamp() throws Exception {
        String source = "21702307";
        String keySign = TaobaoUtils.byte2hex(TaobaoUtils.encryptMD5(source)).toLowerCase();
        Assert.assertEquals("5e1687ec37e9b76ee7b82464c11bd10b", keySign);
        String verSign = TaobaoUtils.byte2hex(TaobaoUtils.encryptMD5("")).toLowerCase();
        Assert.assertEquals("d41d8cd98f00b204e9800998ecf8427e", verSign);
        source =
                "null&" + keySign + "&mtop.common.getTimestamp&*&860308028232581&460026214427182&" + verSign
                        + "&1446870318379";
        String sign = TaobaoUtils.byte2hex(TaobaoUtils.encryptMD5(source)).toLowerCase();
        System.err.println("sign:" + sign);
        Assert.assertEquals("85834a3ca526bf14f766986de06d5c1b", sign);
    }

    @Test
    public void testSignNewDeviceId() throws Exception {
        String keySign = TaobaoUtils.byte2hex(TaobaoUtils.encryptMD5("21702307")).toLowerCase();
        Assert.assertEquals("5e1687ec37e9b76ee7b82464c11bd10b", keySign);
        String data =
                "{\"new_device\":true,\"c1\":\"MI 2\",\"c2\":\"860308028232581\",\"device_global_id\":\"UdBLKIqLT7MBAOoUQwq+E4wH860308028232581460029721530838\",\"c0\":\"Xiaomi\",\"c6\":\"19bbd6abaf74b3fe\",\"c5\":\"4e42e69\",\"c4\":\"ac:f7:f3:43:53:f1\",\"c3\":\"460026214427182\"}";
        String dataSign = TaobaoUtils.byte2hex(TaobaoUtils.encryptMD5(data)).toLowerCase();
        Assert.assertEquals("7adf9f0d40c44eacad3c4f2af93c56d4", dataSign);
        String source =
                "null&" + keySign + "&mtop.sys.newDeviceId&4.0&860308028232581&460026214427182&" + dataSign
                        + "&1446870848238";
        byte[] bytes = TaobaoUtils.encryptMD5(source);
        String sign = TaobaoUtils.byte2hex(bytes);
        sign = sign.toLowerCase();
        System.err.println("sign:" + sign);
        Assert.assertEquals("5fdfbe0dc291011de3978ed6abf01ec6", sign);
    }
}
