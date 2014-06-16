iu = function(lr, u, lq) {
	var zk = u + lq;
	var fy = "";
	var yn = "";
	lr = lr.substr((0x3e8 % 01267 & 65), lr.length - (0x3fe * 0277 & 91));
	var p8 = lr.length;
	for (var wg = ((0x3f3 | 01215) % 93); wg < p8; ++wg) {
		if (wg % zk == zk - (0x3e8 % 01107 & 85)) {
			fy = lr.substr(wg - zk + (0x3f3 * 0673 & 73), zk);
			fy = fy.split("").reverse().join("");
			fy = fy.substr((0x3e8 % 0540 & 65), u);
			yn = yn + fy;
		}
	}
	var sq = p8 % zk;
	if (sq != (0x3e8 * 0270 & 63)) {
		fy = lr.substr(p8 - sq, p8 - (0x3e8 % 01053 & 67));
		fy = fy.split("").reverse().join("");
		fy = fy.substr(((0x3e8 | 01215) % 67), u);
		yn = yn + fy;
	}
	return yn;
};
var p = iu;
var h8 = p;
var q = iu;