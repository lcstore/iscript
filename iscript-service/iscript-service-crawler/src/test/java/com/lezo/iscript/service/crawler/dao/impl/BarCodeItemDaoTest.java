package com.lezo.iscript.service.crawler.dao.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lezo.iscript.service.crawler.dao.BarCodeItemDao;
import com.lezo.iscript.service.crawler.dao.ShopDao;
import com.lezo.iscript.service.crawler.dto.BarCodeItemDto;
import com.lezo.iscript.service.crawler.dto.ShopDto;
import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.utils.JSONUtils;

public class BarCodeItemDaoTest {

	@Test
	public void testBatchInsert() {
		String[] configs = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		BarCodeItemDao barCodeItemDao = SpringBeanUtils.getBean(BarCodeItemDao.class);

		List<BarCodeItemDto> dtoList = new ArrayList<BarCodeItemDto>();
		BarCodeItemDto dto = new BarCodeItemDto();
		dto.setBarCode("bc");
		dto.setCreateTime(new Date());
		dto.setImgUrl("imgUrl");
		dto.setProductAttr("productAttr");
		dto.setProductName("productName");
		dto.setProductUrl("productUrl");
		dto.setUpdateTime(new Date());
		dtoList.add(dto);
		barCodeItemDao.batchInsert(dtoList);
	}

	@Test
	public void testGetBarCodeItemDtos() {
		String[] configs = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		BarCodeItemDao barCodeItemDao = SpringBeanUtils.getBean(BarCodeItemDao.class);

		List<String> barCodeList = new ArrayList<String>();
		barCodeList.add("bc");
		List<BarCodeItemDto> dtoList = barCodeItemDao.getBarCodeItemDtos(barCodeList);
		Assert.assertEquals(false, dtoList.isEmpty());
	}

	@Test
	public void testFileBatchInsert() throws Exception {
		String[] configs = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		BarCodeItemDao barCodeItemDao = SpringBeanUtils.getBean(BarCodeItemDao.class);
		String file = "D:/ancc-p1/result-1001.log";
		Reader in = new FileReader(file);
		BufferedReader bReader = new BufferedReader(in);
		int count = 0;
		List<BarCodeItemDto> dtoList = new ArrayList<BarCodeItemDto>();
		while (bReader.ready()) {
			String line = bReader.readLine();
			if (line == null) {
				break;
			}
			List<BarCodeItemDto> itemDtos = createBarCodeItemDto(line);
			if (itemDtos.isEmpty()) {
				continue;
			}
			count++;
			dtoList.addAll(itemDtos);
			if (dtoList.size() >= 500) {
				saveItems(barCodeItemDao, dtoList);
				dtoList = new ArrayList<BarCodeItemDto>();
			}
		}
		if (!dtoList.isEmpty()) {
			saveItems(barCodeItemDao, dtoList);
		}
		IOUtils.closeQuietly(bReader);
		System.out.println("total.count:" + count);
	}

	private void saveItems(BarCodeItemDao barCodeItemDao, List<BarCodeItemDto> dtoList) {
		Map<String, BarCodeItemDto> dtoMap = new HashMap<String, BarCodeItemDto>();
		for (BarCodeItemDto dto : dtoList) {
			dtoMap.put(dto.getBarCode(), dto);
		}
		List<BarCodeItemDto> hasDtos = barCodeItemDao.getBarCodeItemDtos(new ArrayList<String>(dtoMap.keySet()));
		Map<String, BarCodeItemDto> hasMap = new HashMap<String, BarCodeItemDto>();
		for (BarCodeItemDto dto : hasDtos) {
			hasMap.put(dto.getBarCode(), dto);
		}
		List<BarCodeItemDto> insertDtos = new ArrayList<BarCodeItemDto>();
		for (Entry<String, BarCodeItemDto> entry : dtoMap.entrySet()) {
			if (!hasDtos.contains(entry.getKey())) {
				insertDtos.add(entry.getValue());
			}
		}
		barCodeItemDao.batchInsert(insertDtos);
		System.out.println("insert:" + insertDtos.size());
	}

	private List<BarCodeItemDto> createBarCodeItemDto(String line) {
		int index = line.indexOf("{");
		List<BarCodeItemDto> dtoList = new ArrayList<BarCodeItemDto>();
		if (index < 0) {
			return dtoList;
		}
		JSONObject jsonObject = JSONUtils.getJSONObject(line.substring(index));
		JSONArray itemArray = JSONUtils.get(jsonObject, "items");
		for (int i = 0; i < itemArray.length(); i++) {
			try {
				JSONObject itemObject = itemArray.getJSONObject(i);
				BarCodeItemDto dto = new BarCodeItemDto();
				dto.setBarCode(JSONUtils.getString(itemObject, "pBarCode"));
				dto.setProductName(JSONUtils.getString(itemObject, "pName"));
				dto.setProductUrl(JSONUtils.getString(itemObject, "pUrl"));
				dto.setProductBrand(JSONUtils.getString(itemObject, "pBrand"));
				dto.setProductModel(JSONUtils.getString(itemObject, "pModel"));
				dto.setProductAttr(JSONUtils.getString(itemObject, "pText"));
				dto.setImgUrl(JSONUtils.getString(itemObject, "pImg"));
				dto.setCreateTime(new Date());
				dto.setUpdateTime(new Date());
				dtoList.add(dto);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return dtoList;
	}

	@Test
	public void testFileShopBatchInsert() throws Exception {
		String[] configs = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		ShopDao shopInfoDao = SpringBeanUtils.getBean(ShopDao.class);
		String file = "D:/ancc-p1/result-1001.log";
		Reader in = new FileReader(file);
		BufferedReader bReader = new BufferedReader(in);
		int count = 0;
		Map<String, ShopDto> shopMap = new HashMap<String, ShopDto>();
		while (bReader.ready()) {
			String line = bReader.readLine();
			if (line == null) {
				break;
			}
			Map<String, ShopDto> dtoMap = createShopInfoDto(line);
			if (dtoMap.isEmpty()) {
				continue;
			}
			count++;
			shopMap.putAll(dtoMap);
			if (shopMap.size() >= 200) {
				shopInfoDao.batchInsert(new ArrayList<ShopDto>(shopMap.values()));
				shopMap.clear();
			}
		}
		if (!shopMap.isEmpty()) {
			shopInfoDao.batchInsert(new ArrayList<ShopDto>(shopMap.values()));
		}
		IOUtils.closeQuietly(bReader);
		System.out.println("total.count:" + count);
	}

	private Map<String, ShopDto> createShopInfoDto(String line) {
		int index = line.indexOf("{");
		Map<String, ShopDto> dtoMap = new HashMap<String, ShopDto>();
		if (index < 0) {
			return dtoMap;
		}
		JSONObject jsonObject = JSONUtils.getJSONObject(line.substring(index));
		JSONArray itemArray = JSONUtils.get(jsonObject, "items");
		for (int i = 0; i < itemArray.length(); i++) {
			try {
				JSONObject itemObject = itemArray.getJSONObject(i);
				String barCode = JSONUtils.getString(itemObject, "pBarCode");
				ShopDto dto = new ShopDto();
				dto.setShopCode(barCode.substring(0, 8));
				dto.setShopName(JSONUtils.getString(itemObject, "spName"));
				dto.setShopUrl(JSONUtils.getString(itemObject, "spUrl"));
				dto.setCreateTime(new Date());
				dto.setUpdateTime(new Date());
				dtoMap.put(dto.getShopCode(), dto);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return dtoMap;
	}

	@Test
	public void testDeleteFromId() {
		String[] configs = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		BarCodeItemDao barCodeItemDao = SpringBeanUtils.getBean(BarCodeItemDao.class);
		List<Long> idList = new ArrayList<Long>();
		idList.add(201L);
		barCodeItemDao.deleteFromId(idList);
	}

	@Test
	public void testDeleteFromIds() {
		String[] configs = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		BarCodeItemDao barCodeItemDao = SpringBeanUtils.getBean(BarCodeItemDao.class);
		List<String> barCodeList = new ArrayList<String>();
		barCodeList.add("0800050004787");
		barCodeList.add("4000417294005");
		barCodeList.add("4002652000748");
		barCodeList.add("4005292002059");
		barCodeList.add("4013197766976");
		barCodeList.add("4510002303220");
		barCodeList.add("4710022203506");
		barCodeList.add("4710098105353");
		barCodeList.add("4710098105834");
		barCodeList.add("4710098162834");
		barCodeList.add("4710098162837");
		barCodeList.add("4710098164268");
		barCodeList.add("4710098168174");
		barCodeList.add("4710098168808");
		barCodeList.add("4710098168853");
		barCodeList.add("4710098716426");
		barCodeList.add("4710126031845");
		barCodeList.add("4710174006352");
		barCodeList.add("4710174041551");
		barCodeList.add("4710174102696");
		barCodeList.add("4710174104102");
		barCodeList.add("4710174104898");
		barCodeList.add("4710174105949");
		barCodeList.add("4710174107196");
		barCodeList.add("4710174107431");
		barCodeList.add("4710199037010");
		barCodeList.add("4710199080566");
		barCodeList.add("4710199080597");
		barCodeList.add("4710199085509");
		barCodeList.add("4710199085707");
		barCodeList.add("4710199085905");
		barCodeList.add("4710199086506");
		barCodeList.add("4710199086704");
		barCodeList.add("4710199087701");
		barCodeList.add("4710218901179");
		barCodeList.add("4710218901261");
		barCodeList.add("4710280212777");
		barCodeList.add("4710298080139");
		barCodeList.add("4710362898929");
		barCodeList.add("4710474000456");
		barCodeList.add("4710474000661");
		barCodeList.add("4710474001675");
		barCodeList.add("4710874161733");
		barCodeList.add("4710910007452");
		barCodeList.add("4710953081136");
		barCodeList.add("4710953081365");
		barCodeList.add("4711148355568");
		barCodeList.add("4711155312707");
		barCodeList.add("4711155313681");
		barCodeList.add("4711162821322");
		barCodeList.add("4711162821520");
		barCodeList.add("4711162821537");
		barCodeList.add("4711162821575");
		barCodeList.add("4711162821797");
		barCodeList.add("4711162821803");
		barCodeList.add("4711162821896");
		barCodeList.add("4711162823609");
		barCodeList.add("4711162824088");
		barCodeList.add("4711452001144");
		barCodeList.add("4711507301083");
		barCodeList.add("4711569027358");
		barCodeList.add("4711569027372");
		barCodeList.add("4711569029109");
		barCodeList.add("4711883010012");
		barCodeList.add("4711883060918");
		barCodeList.add("4712523520250");
		barCodeList.add("4712523520557");
		barCodeList.add("4712585746643");
		barCodeList.add("4712646680503");
		barCodeList.add("4712803800720");
		barCodeList.add("4712803801697");
		barCodeList.add("4712829105137");
		barCodeList.add("4712832449037");
		barCodeList.add("4712839894687");
		barCodeList.add("4712839898378");
		barCodeList.add("4712905017859");
		barCodeList.add("4712905017866");
		barCodeList.add("4712905017989");
		barCodeList.add("4712905021733");
		barCodeList.add("4713093018352");
		barCodeList.add("4713093019151");
		barCodeList.add("4713093030996");
		barCodeList.add("4713269689515");
		barCodeList.add("4713507006067");
		barCodeList.add("4713540003146");
		barCodeList.add("4713909140000");
		barCodeList.add("4713968993548");
		barCodeList.add("4714050122051");
		barCodeList.add("4714398013035");
		barCodeList.add("4714398018092");
		barCodeList.add("4714398320386");
		barCodeList.add("4715479900060");
		barCodeList.add("4715479900077");
		barCodeList.add("4716375000045");
		barCodeList.add("4716820920034");
		barCodeList.add("4717831108299");
		barCodeList.add("4718360222227");
		barCodeList.add("4718590770000");
		barCodeList.add("4719684336683");
		barCodeList.add("4719684990627");
		barCodeList.add("4719684990892");
		barCodeList.add("4719858572855");
		barCodeList.add("4800116012036");
		barCodeList.add("4800116018038");
		barCodeList.add("4806511019199");
		barCodeList.add("4809010272010");
		barCodeList.add("4809010272027");
		barCodeList.add("4890008105366");
		barCodeList.add("4890008155637");
		barCodeList.add("4890008180158");
		barCodeList.add("4891214718210");
		barCodeList.add("4892178010211");
		barCodeList.add("4892178010563");
		barCodeList.add("4892178080214");
		barCodeList.add("4892178080221");
		barCodeList.add("4892178080238");
		barCodeList.add("4892178090213");
		barCodeList.add("4892371700124");
		barCodeList.add("4892396401402");
		barCodeList.add("4893025812323");
		barCodeList.add("4895025812323");
		barCodeList.add("4895058310623");
		barCodeList.add("4895058314287");
		barCodeList.add("4897026958868");
		barCodeList.add("4897033830324");
		barCodeList.add("4897035810355");
		barCodeList.add("4897039210014");
		barCodeList.add("4897041950427");
		barCodeList.add("4897042510163");
		barCodeList.add("4897042510392");
		barCodeList.add("4897042780771");
		barCodeList.add("4897042781174");
		barCodeList.add("4901360287680");
		barCodeList.add("4901362102387");
		barCodeList.add("4901362102639");
		barCodeList.add("4901620147112");
		barCodeList.add("4903041002148");
		barCodeList.add("4946857600320");
		barCodeList.add("4968009901031");
		barCodeList.add("4968009901062");
		barCodeList.add("5410126106183");
		barCodeList.add("5410126716016");
		barCodeList.add("5410291011381");
		barCodeList.add("6293403811711");
		barCodeList.add("6710910007490");
		barCodeList.add("6901180381283");
		barCodeList.add("6902227014812");
		barCodeList.add("6907858401192");
		barCodeList.add("6912112630058");
		barCodeList.add("6914973106581");
		barCodeList.add("6915245003331");
		barCodeList.add("6915288070796");
		barCodeList.add("6917541774632");
		barCodeList.add("6919128882785");
		barCodeList.add("6920324680808");
		barCodeList.add("6920928600127");
		barCodeList.add("6921002000079");
		barCodeList.add("6921138100087");
		barCodeList.add("6921192500427");
		barCodeList.add("6921249298888");
		barCodeList.add("6921440345480");
		barCodeList.add("6921858300033");
		barCodeList.add("6922903301081");
		barCodeList.add("6923118592233");
		barCodeList.add("6923118596217");
		barCodeList.add("6925104422593");
		barCodeList.add("6925505400015");
		barCodeList.add("6925505400039");
		barCodeList.add("6925698102543");
		barCodeList.add("6925698102635");
		barCodeList.add("6925901420297");
		barCodeList.add("6926238400204");
		barCodeList.add("6926468001844");
		barCodeList.add("6926582210054");
		barCodeList.add("6927001916069");
		barCodeList.add("6927146297450");
		barCodeList.add("6928343900228");
		barCodeList.add("6928497820038");
		barCodeList.add("6930334512098");
		barCodeList.add("6930846300245");
		barCodeList.add("6931121668524");
		barCodeList.add("6931441003050");
		barCodeList.add("6931671000799");
		barCodeList.add("6933125655555");
		barCodeList.add("6933329588321");
		barCodeList.add("6933378100666");
		barCodeList.add("6933620900051");
		barCodeList.add("6933620900242");
		barCodeList.add("6937451833571");
		barCodeList.add("6937679210468");
		barCodeList.add("6938832200388");
		barCodeList.add("6938859805078");
		barCodeList.add("6939232900113");
		barCodeList.add("6939271400582");
		barCodeList.add("6939721400544");
		barCodeList.add("6939767880041");
		barCodeList.add("6939948600178");
		barCodeList.add("6940864850035");
		barCodeList.add("6941051303234");
		barCodeList.add("6941439697390");
		barCodeList.add("6941439699653");
		barCodeList.add("6943657510888");
		barCodeList.add("6944149199925");
		barCodeList.add("6944718200083");
		barCodeList.add("6945301212018");
		barCodeList.add("6946291500062");
		barCodeList.add("6946578700055");
		barCodeList.add("6947073110080");
		barCodeList.add("6948189820948");
		barCodeList.add("6948198720369");
		barCodeList.add("6948454611080");
		barCodeList.add("6948939602398");
		barCodeList.add("6950239500021");
		barCodeList.add("6950380400423");
		barCodeList.add("6951181105265");
		barCodeList.add("6951192300321");
		barCodeList.add("6951481905039");
		barCodeList.add("6951520600406");
		barCodeList.add("6951957203324");
		barCodeList.add("6952652900051");
		barCodeList.add("6952825512012");
		barCodeList.add("6953114701032");
		barCodeList.add("6953132526358");
		barCodeList.add("6953539900171");
		barCodeList.add("6953601800316");
		barCodeList.add("6953663012405");
		barCodeList.add("6953837400113");
		barCodeList.add("6953876600444");
		barCodeList.add("6954092882363");
		barCodeList.add("6954244400192");
		barCodeList.add("6954525310035");
		barCodeList.add("6954636700657");
		barCodeList.add("6954682403052");
		barCodeList.add("6954711700046");
		barCodeList.add("6954776100461");
		barCodeList.add("6955329200287");
		barCodeList.add("6955329200300");
		barCodeList.add("6956157810013");
		barCodeList.add("6956157820012");
		barCodeList.add("6956241400243");
		barCodeList.add("6956511901110");
		barCodeList.add("6957670300050");
		barCodeList.add("6957748300012");
		barCodeList.add("6958204202048");
		barCodeList.add("6958407612064");
		barCodeList.add("6958620700005");
		barCodeList.add("6958965102281");
		barCodeList.add("6958965121534");
		barCodeList.add("6959507100411");
		barCodeList.add("6959512700035");
		barCodeList.add("6959531600118");
		barCodeList.add("6959845513064");
		barCodeList.add("8000380005918");
		barCodeList.add("8000380005949");
		barCodeList.add("8000380005963");
		barCodeList.add("8000380140541");
		barCodeList.add("8000380142460");
		barCodeList.add("8000500003787");
		barCodeList.add("8000500009673");
		barCodeList.add("8000500047873");
		barCodeList.add("8015997000607");
		barCodeList.add("8410376000108");
		barCodeList.add("8410525186608");
		barCodeList.add("8410525194375");
		barCodeList.add("8690766374150");
		barCodeList.add("8690766374167");
		barCodeList.add("8800111820112");
		barCodeList.add("8801019005619");
		barCodeList.add("8801019005909");
		barCodeList.add("8801019306273");
		barCodeList.add("8801019306907");
		barCodeList.add("8801019307133");
		barCodeList.add("8801019308321");
		barCodeList.add("8801019308338");
		barCodeList.add("8801019309076");
		barCodeList.add("8801019309571");
		barCodeList.add("8801019602498");
		barCodeList.add("8801019602504");
		barCodeList.add("8801039280027");
		barCodeList.add("8801039905685");
		barCodeList.add("8801039916520");
		barCodeList.add("8801043004701");
		barCodeList.add("8801043019354");
		barCodeList.add("8801062221974");
		barCodeList.add("8801062232314");
		barCodeList.add("8801062247196");
		barCodeList.add("8801062248230");
		barCodeList.add("8801062248278");
		barCodeList.add("8801062248438");
		barCodeList.add("8801062267651");
		barCodeList.add("8801062268597");
		barCodeList.add("8801062272150");
		barCodeList.add("8801062272174");
		barCodeList.add("8801062272570");
		barCodeList.add("8801062273416");
		barCodeList.add("8801062276035");
		barCodeList.add("8801062276059");
		barCodeList.add("8801062279791");
		barCodeList.add("8801062279838");
		barCodeList.add("8801062316137");
		barCodeList.add("8801062317592");
		barCodeList.add("8801062317752");
		barCodeList.add("8801062330157");
		barCodeList.add("8801062331772");
		barCodeList.add("8801062332076");
		barCodeList.add("8801062515417");
		barCodeList.add("8801062515455");
		barCodeList.add("8801062518296");
		barCodeList.add("8801062518333");
		barCodeList.add("8801062629718");
		barCodeList.add("8801062633692");
		barCodeList.add("8801062637201");
		barCodeList.add("8801077297605");
		barCodeList.add("8801111112062");
		barCodeList.add("8801111115247");
		barCodeList.add("8801111130103");
		barCodeList.add("8801111182911");
		barCodeList.add("8801111186070");
		barCodeList.add("8801111186100");
		barCodeList.add("8801111186209");
		barCodeList.add("8801111186230");
		barCodeList.add("8801111186582");
		barCodeList.add("8801111610636");
		barCodeList.add("8801111614382");
		barCodeList.add("8801117250003");
		barCodeList.add("8801117268404");
		barCodeList.add("8801117268602");
		barCodeList.add("8801117534912");
		barCodeList.add("8803556815621");
		barCodeList.add("8803567102352");
		barCodeList.add("8804007413540");
		barCodeList.add("8804007413557");
		barCodeList.add("8805436202842");
		barCodeList.add("8809024450622");
		barCodeList.add("8809031015586");
		barCodeList.add("8809045089429");
		barCodeList.add("8809059292532");
		barCodeList.add("8809113580230");
		barCodeList.add("8809149831023");
		barCodeList.add("8809262721119");
		barCodeList.add("8809331504148");
		barCodeList.add("8850155011121");
		barCodeList.add("8850155021120");
		barCodeList.add("8850157501224");
		barCodeList.add("8850332251111");
		barCodeList.add("8850408000216");
		barCodeList.add("8851339901573");
		barCodeList.add("8852023665511");
		barCodeList.add("8852052110600");
		barCodeList.add("8852690002930");
		barCodeList.add("8858702410823");
		barCodeList.add("8858761400230");
		barCodeList.add("8858761400254");
		barCodeList.add("8870633001697");
		barCodeList.add("8887290526029");
		barCodeList.add("8888296038462");
		barCodeList.add("8934609602032");
		barCodeList.add("8934760211005");
		barCodeList.add("8934941010052");
		barCodeList.add("8935043800404");
		barCodeList.add("8935146095639");
		barCodeList.add("8936014316726");
		barCodeList.add("8936021270127");
		barCodeList.add("8936021960202");
		barCodeList.add("8936050230147");
		barCodeList.add("8936050230178");
		barCodeList.add("8936050230192");
		barCodeList.add("8936050230222");
		barCodeList.add("8938583331067");
		barCodeList.add("8993083927413");
		barCodeList.add("8996001303078");
		barCodeList.add("8996001303702");
		barCodeList.add("9348213032571");
		barCodeList.add("9550621567525");
		barCodeList.add("9555021808230");
		barCodeList.add("9555047500040");
		barCodeList.add("9555047500088");
		barCodeList.add("9555047500101");
		barCodeList.add("9555047500194");
		barCodeList.add("9555047500231");
		barCodeList.add("9555047500255");
		barCodeList.add("9555047500279");
		barCodeList.add("9555047500453");
		barCodeList.add("9555047500477");
		barCodeList.add("9555047500521");
		barCodeList.add("9555118659523");
		barCodeList.add("9555118659530");
		barCodeList.add("9555198303613");
		barCodeList.add("9555319103009");
		barCodeList.add("9555319106079");
		barCodeList.add("9555319106154");
		barCodeList.add("9556023674526");
		barCodeList.add("9556023896744");
		barCodeList.add("9556218126885");
		barCodeList.add("9556218126946");
		barCodeList.add("9556345007491");
		barCodeList.add("9556437002984");
		barCodeList.add("9556439880603");
		barCodeList.add("9557194898506");
		barCodeList.add("9557713370261");
		barCodeList.add("9557713370346");
		barCodeList.add("9581354687631");
		barCodeList.add("9588800331186");
		List<BarCodeItemDto> dtoList = barCodeItemDao.getBarCodeItemDtos(barCodeList);
		Map<String, Set<Long>> codeIdMap = new HashMap<String, Set<Long>>();
		for (BarCodeItemDto dto : dtoList) {
			Set<Long> idSet = codeIdMap.get(dto.getBarCode());
			if (idSet == null) {
				idSet = new HashSet<Long>();
				codeIdMap.put(dto.getBarCode(), idSet);
			}
			idSet.add(dto.getId());
		}
		for (Entry<String, Set<Long>> entry : codeIdMap.entrySet()) {
			List<Long> idList = new ArrayList<Long>(entry.getValue());
			idList = idList.subList(1, idList.size());
			barCodeItemDao.deleteFromId(idList);
		}
	}
}
