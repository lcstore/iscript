package com.lezo.iscript.font;

public class CharCode {
	public CharCode() {
		String str = "我";
		try {
			byte[] b = str.getBytes("GB2312");
			for (int i = 0; i < b.length; i++) {
				System.out.println(Integer.toHexString(b[i] & 0xFF));
			}
			int dd =  ((0xCE-0xA1)*94+(0xD2-0xA1))*24;
			System.out.println(dd);
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < b.length; i++) {
				int v = b[i] & 0xFF;
				String hv = Integer.toHexString(v);
				if (hv.length() < 2) {
					sb.append(0);
				}
				sb.append(hv);
			}
			System.out.println(sb.toString());
			System.out.println(new String(b,"GB2312"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {
		CharCode charcode = new CharCode();
	}
}