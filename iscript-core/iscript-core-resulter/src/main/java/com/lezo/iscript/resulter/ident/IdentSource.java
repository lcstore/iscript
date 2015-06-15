package com.lezo.iscript.resulter.ident;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class IdentSource {
	private String productName;
	private Long marketPrice;
	private Long productPrice;
	private String productBrand;
	private String productModel;
	private String barCode;
	private String categoryNav;
	private String tokenBrand;
	private String tokenCategory;
	private String spuCodes;
	private String spuVary;
	private Map<String, String> attrs = new HashMap<String, String>();
}
