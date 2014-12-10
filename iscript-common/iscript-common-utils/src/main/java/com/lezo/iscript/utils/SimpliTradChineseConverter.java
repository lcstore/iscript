package com.lezo.iscript.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * 简繁体中文转换
 * 
 */
public class SimpliTradChineseConverter {

	private final static Map<String, String> tradi_simpli_map = new HashMap<String, String>(1 << 13);

	static {
		String Simplified = StringUtils.EMPTY, Traditional = StringUtils.EMPTY;

		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		InputStream ins = cl.getResourceAsStream("chinese/simplified");
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(ins));
			Simplified = br.readLine();
			br.close();
			ins.close();
		} catch (Exception e) {
			throw new RuntimeException("chinese/simplified read error.");
		}

		ins = cl.getResourceAsStream("chinese/traditional");
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(ins));
			Traditional = br.readLine();
			br.close();
			ins.close();
		} catch (Exception e) {
			throw new RuntimeException("chinese/traditional read error.");
		}

		// create Map
		int i = 0, length = Traditional.length();
		while (i < length) {
			char sim = Simplified.charAt(i), tradi = Traditional.charAt(i);
			i++;
			// 对字库的字符做一层校验
			if (sim == tradi) {
				// 繁体简体字符一致
				continue;
			}
			if (Simplified.indexOf(tradi) != -1) {
				// 简体字库中包含此字符
				continue;
			}
			tradi_simpli_map.put(tradi + "", sim + "");
		}
		Simplified = null;
		Traditional = null;
	}

	/**
	 * 将字符串转换为简体
	 * 
	 * @param Simplified
	 * @return
	 */
	public static String toSimplify(String _simplified) {
		if (_simplified == null || StringUtils.isBlank(_simplified))
			return _simplified;

		int length = _simplified.length();
		StringBuilder builder = new StringBuilder(length);
		char old;
		String _new;
		for (int i = 0; i < length; i++) {
			old = _simplified.charAt(i);
			if (isChinese(old)) {
				_new = tradi_simpli_map.get(old + "");
				if (_new != null) {
					builder.append(_new);
				} else {
					builder.append(old);
				}
			} else {
				builder.append(old);
			}
		}
		return builder.toString();
	}

	private static final boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A) {
			return true;
		}
		return false;
	}

	/**
	 * test case
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		char c = '\u9FA5';
		for (int d = c; d < c + 10; d++) {
			System.out.println((char) d);
		}
		System.out.println(Integer.toHexString('貓'));

		long startTime = System.currentTimeMillis();
		System.out.println(toSimplify("蘇寧易購 保羅蓋帝"));
		System.out.println(toSimplify("天貓商城"));
		System.out.println(toSimplify("叠丰旗舰店"));
		System.out.println(toSimplify("爱华仕OIWAS,1號店,愛華仕箱包"));
		System.out.println(toSimplify("韓都衣舍秀衣蓮專賣店"));
		System.out.println(toSimplify("韓都衣舍丹蘭軒專賣店"));
		System.out.println(new String("浠欎箰濞囧".getBytes("GBK"), "UTF-8"));
		System.out.println(new String("鐟炲嚡鍏存".getBytes("GBK"), "UTF-8"));
		System.out.println((System.currentTimeMillis() - startTime) + "ms.");

		System.out.println("---------------------------------------");

		InputStream ins = new FileInputStream("C:\\Users\\jack.zhu\\Desktop\\代理SQL\\t_shop_manage.csv");
		List<String> source = new ArrayList<String>(15000);
		List<String> result = new ArrayList<String>(30000);
		BufferedReader br = new BufferedReader(new InputStreamReader(ins, "UTF-8"));
		String str = StringUtils.EMPTY;
		while ((str = br.readLine()) != null) {
			source.add(str);
		}
		ins.close();
		br.close();
		startTime = System.currentTimeMillis();
		String _after = StringUtils.EMPTY;
		for (String s : source) {
			_after = toSimplify(s);
			if (!_after.equals(s)) {
				result.add("before: " + s);
				result.add("after - : " + _after);
			}
		}
		System.out.println((System.currentTimeMillis() - startTime) + "ms.");

		// write result to file
		OutputStream ous = new FileOutputStream("C:\\Users\\jack.zhu\\Desktop\\代理SQL\\t_shop_manage_result.txt");
		OutputStreamWriter ousw = new OutputStreamWriter(ous, "UTF-8");
		for (String r : result) {
			ousw.write(r + "\r\n");
		}
		ous.flush();
		ous.close();

	}
}
