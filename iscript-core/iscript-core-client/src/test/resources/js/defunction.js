ac = function(mo, rv, tn) {
	var v1 = rv + tn;
	var hb = "";
	var mg = "";
	mo = mo.substr(((0x3e8 | 01404) % 59), mo.length - (0x3fe * 0457 & 67));
	mo = mo.split("").reverse().join("");
	var p2 = mo.length;
	for (var qo = (0x3e8 % 0531 & 65); qo < p2; ++qo) {
		if (qo % v1 == v1 - ((0x3e8 | 01035) % 85)) {
			hb = mo.substr(p2 - qo - (0x3f3 * 0423 & 97), v1);
			hb = hb.charAt(v1 - (0x3e8 % 01071 & 65))
					+ hb.substr((0x3f3 * 0207 & 81), v1 - (0x3e8 % 0763 & 75))
					+ hb.charAt((0x3e8 % 0360 & 65));
			hb = hb.substr(((0x3e8 | 01341) % 77), rv);
			mg = mg + hb;
		}
	}
	var ww = p2 % v1;
	if (ww != (0x3e8 * 0225 & 81)) {
		hb = mo.substr((0x3e8 * 0200 & 87), ww);
		if (hb.length != (0x3f3 * 0207 & 67)) {
			hb = hb.charAt(ww - (0x3f3 * 0513 & 73))
					+ hb.substr((0x3e8 % 01107 & 85), ww
							- ((0x3fe | 01602) % 85))
					+ hb.charAt(((0x3fe | 0261) % 93));
		}
		hb = hb.substr((0x3e8 * 0277 & 67), rv);
		mg = mg + hb;
	}
	return mg;
};
var jt = ac;
var ff = jt;
var dj = ac;