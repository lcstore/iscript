package com.lezo.iscript.resulter.ident;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;
import org.junit.Test;

import com.lezo.iscript.service.crawler.service.SynonymBrandService;
import com.lezo.iscript.utils.JSONUtils;

public class BrandTokenizerTest {

	@Test
	public void testBrandTokenizer() {
		List<String> brandList = new ArrayList<String>();
		brandList.add("三星");
		brandList.add("samsung");
		BrandTokenizer tokenizer = new BrandTokenizer(makeSynonymBrandService(brandList));
		List<EntityToken> entityList = new ArrayList<EntityToken>();
		String value = "三星 Galaxy S5 (G9008W) 闪耀白 移动4G手机 双卡双待";
		EntityToken entityToken = new EntityToken(value);
		entityToken.getAssists().add(new SectionToken("productMode", "Galaxy S5 (G9008W)"));
		entityToken.getAssists().add(new SectionToken("productBrand", "三星（SAMSUNG）"));
		String spuVar = "{\"Size\":\"移动4G（16G ROM）双卡版\",\"Color\":\"闪耀白\"}";
		addAssists(JSONUtils.getJSONObject(spuVar), entityToken);
		String attrs = "{\"可用空间\":\"操作系统和预置应用程序占用部分存储空间, 因此实际用户可用空间少于存储器标称容量。操作系统或软件版本的更新可能会导致用户可用空间发生变化。\",\"操作系统版本\":\"Android 4.4\",\"蓝牙\":\"支持\",\"闪光灯\":\"LED补光灯\",\"cpu型号\":\"MSM8974Pro\",\"机身尺寸（mm）\":\"142 x 72.5 x 8.1mm\",\"cpu品牌\":\"Qualcomm 骁龙\",\"运行内存\":\"2GB RAM\",\"最大存储扩展\":\"128GB\",\"屏幕材质\":\"Super AMOLED\",\"wi-fi\":\"支持\",\"重力感应\":\"支持\",\"传感器类型\":\"CMOS\",\"变焦模式\":\"数码变焦\",\"电池容量（mah）\":\"2800mAh\",\"数据线\":\"Micro USB\",\"品牌\":\"三星（SAMSUNG）\",\"屏幕尺寸\":\"5.1英寸\",\"音乐播放\":\"支持\",\"cpu核数\":\"四核\",\"电视播放\":\"不支持\",\"触摸屏\":\"电容屏，多点触控\",\"机身内存\":\"16GB ROM\",\"cpu频率\":\"2.5GHz\",\"颜色\":\"闪耀白\",\"机身重量（g）\":\"163\",\"视频播放\":\"支持\",\"操作系统\":\"安卓（Android）\",\"智能机\":\"是\",\"分辨率\":\"1920×1080(FHD,1080P)\",\"上市年份\":\"2014年\",\"电池类型\":\"锂电池\",\"电池更换\":\"支持\",\"gps模块\":\"支持\",\"储存卡类型\":\"MicroSD(TF)\",\"双卡机类型\":\"双卡双待双通\",\"光线感应\":\"支持\",\"2g网络制式\":\"移动2G/联通2G(GSM)\",\"型号\":\"Galaxy S5 G9008W\",\"距离感应\":\"支持\",\"运营商标志或内容\":\"在机身、在开机画面、在内置应用\",\"3g网络制式\":\"支持国际联通3G漫游，不支持国内联通3G。\",\"网络频率\":\"TD-LTE(1900/2300/2600) TD-SCDMA(1880/2010) GSM (850/900/1800/1900 MHZ)\",\"前置摄像头\":\"200万像素\",\"4g网络制式\":\"移动4G(TD-LTE)\",\"屏幕色彩\":\"1600万色\",\"自动对焦\":\"支持\",\"输入方式\":\"触控\",\"后置摄像头\":\"1600万像素\",\"sim卡尺寸\":\"Micro SIM卡(小卡)\"}";
		addAssists(JSONUtils.getJSONObject(attrs), entityToken);
		entityList.add(entityToken);

		new AttrTokenizer().identify(entityList);
		tokenizer.identify(entityList);
		for (EntityToken entity : entityList) {
			System.out.println("master:" + entity.getMaster().getValue());
			for (SectionToken child : entity.getMaster().getChildren()) {
				System.out.println("child:" + child.getKey() + "=" + child.getValue() + ",trust:" + child.getTrust());
				// System.out.println("child:" + child);
			}
		}
	}

	@Test
	public void testBrandTokenizer2() {
		List<String> brandList = new ArrayList<String>();
		brandList.add("lenovo");
		brandList.add("联想");
		BrandTokenizer tokenizer = new BrandTokenizer(makeSynonymBrandService(brandList));
		List<EntityToken> entityList = new ArrayList<EntityToken>();
		String value = "Lenovo/联想 A880 WCDMA/GSM 6英寸大屏 1.3G四核 双卡双待黑色非合约机官方标配";
		EntityToken entityToken = new EntityToken(value);
		entityToken.getAssists().add(new SectionToken("productBrand", "Lenovo/联想"));
		entityList.add(entityToken);

		new AttrTokenizer().identify(entityList);
		tokenizer.identify(entityList);
		for (EntityToken entity : entityList) {
			System.out.println("master:" + entity.getMaster().getValue());
			for (SectionToken child : entity.getMaster().getChildren()) {
				System.out.println("child:" + child.getKey() + "=" + child.getValue() + ",trust:" + child.getTrust()
						+ ",tokenizer:" + child.getTokenizer());
				// System.out.println("child:" + child);
			}
		}
	}

	@Test
	public void testBrandTokenizer3() {
		List<String> brandList = new ArrayList<String>();
		brandList.add("华为");
		brandList.add("huawei");
		brandList.add("ua");
		BrandTokenizer tokenizer = new BrandTokenizer(makeSynonymBrandService(brandList));
		List<EntityToken> entityList = new ArrayList<EntityToken>();
		String value = "荣耀 6 Plus (PE-TL20) 3GB内存标准版 白色 移动4G手机 双卡双待双通";
		EntityToken entityToken = new EntityToken(value);
		entityToken.getAssists().add(new SectionToken("productBrand", "华为（HUAWEI）"));
		entityList.add(entityToken);

		new AttrTokenizer().identify(entityList);
		tokenizer.identify(entityList);
		for (EntityToken entity : entityList) {
			System.out.println("master:" + entity.getMaster().getValue());
			for (SectionToken child : entity.getMaster().getChildren()) {
				System.out.println("child:" + child.getKey() + "=" + child.getValue() + ",trust:" + child.getTrust()
						+ ",tokenizer:" + child.getTokenizer());
				// System.out.println("child:" + child);
			}
		}
	}

	private void addAssists(JSONObject attrObject, EntityToken entiryToken) {
		Iterator<?> it = attrObject.keys();
		while (it.hasNext()) {
			String key = it.next().toString();
			String attrValue = JSONUtils.getString(attrObject, key);
			entiryToken.getAssists().add(new SectionToken(key, attrValue));
		}
	}

	private SynonymBrandService makeSynonymBrandService(final List<String> brandList) {
		return new SynonymBrandService() {

			@Override
			public Set<String> getSynonyms(String brandName) {
				return new HashSet<String>(brandList);
			}

			@Override
			public boolean isSynonym(String left, String right) {
				return false;
			}

			@Override
			public Iterator<String> iteratorKeys() {
				return brandList.iterator();
			}
		};
	}
}
