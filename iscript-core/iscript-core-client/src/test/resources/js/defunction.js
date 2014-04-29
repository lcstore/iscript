var m1 = function(nu, gw, w3) {
	var y = gw + w3;
	var ci = "";
	var q = "";
	nu = nu.substr(((0x3fe | 0513) % 73), nu.length - ((0x3fe | 0200) % 85));
	var jf = nu.length;
	for (var dv = (0x3e8 * 0171 & 87); dv < jf; ++dv) {
		if (dv % y == y - (0x3f3 * 0673 & 97)) {
			ci = nu.substr(dv - y + (0x3e8 % 0475 & 73), y);
			ci = ci.charAt(y - (0x3f3 * 0207 & 75))
					+ ci
							.substr((0x3e8 % 01071 & 81), y
									- ((0x3fe | 0646) % 85))
					+ ci.charAt(((0x3e8 | 0574) % 85));
			ci = ci.substr((0x3e8 * 0200 & 75), gw);
			q = q + ci;
		}
	}
	var nq = jf % y;
	if (nq != ((0x3e8 | 0513) % 59)) {
		ci = nu.substr(jf - nq, jf - (0x3f3 * 0727 & 67));
		if (ci.length != ((0x3e8 | 0216) % 67)) {
			ci = ci.charAt(nq - (0x4b9 % 01134))
					+ ci.substr((0x3f3 * 0207 & 81), nq - (0x3e8 % 01116 & 71))
					+ ci.charAt((0x3e8 * 0200 & 59));
		}
		ci = ci.substr((0x3e8 % 0367 & 81), gw);
		q = q + ci;
	}
	return q;
};
var q2 = m1;
var ib = m1;
var xm = m1;