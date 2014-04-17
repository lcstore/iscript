var ua = "";
var UA_Opt = new Object();
UA_Opt.LogVal = "ua";
UA_Opt.MaxMCLog = 3;
UA_Opt.MaxMPLog = 3;
UA_Opt.MaxKSLog = 3;
UA_Opt.Token = new Date().getTime() + ":" + Math.random();
UA_Opt.SendMethod = 8;
UA_Opt.Flag = 14222;
UA_Opt.reload = function() {
	dl = (new xkvi[Date]())[getTime]();
	x7x = 0;
	gueh = 0;
	y0 = 0;
	var cd = ((0x3e8 | 01611) % 91);
	for (var ckz = ((0x3f3 | 0475) % 93); ckz <= ((0x3f3 | 01627) % 63); cd = ++ckz
			+ ckz++ + ckz) {
		if (ckz == cd) {
			t32 = 0;
			continue;
		}
		if (2 * ckz == cd - (0x409 * 0655 & 71)) {
			xq();
			break;
		}
		if (2 * ckz == cd - 2) {
			xf = 0;
			ckz++;
		}
		if ((0x3e8 % 01305 & 71) * ckz == cd + 2) {
			ihb = 0;
		}
	}
	vv2l();
};

UA_Opt.attachEvents = function(q1) {
	var lp = (0x3e8 * 0200 & 85);
	for (var tyq = (0x3e8 % 0171 & 79); tyq <= ((0x409 | 01001) % 81); lp = ++tyq
			+ tyq++ + tyq) {
		if (tyq == lp) {
			var ip = new xkvi[Object];
			continue;
		}
		if (2 * tyq == lp - (0x3f3 * 01314 & 69)) {
			ip["keydown"] = cl;
		} else if (2 * tyq == lp - 2) {
			ip["mousedown"] = rx;
			continue;
		}
		if ((0x3fe * 0376 & 95) * tyq == lp + ((0x3f3 | 0702) % 59)) {
			ip["mousemove"] = akb;
		}
	}
	var sd = (0x3e8 * 0144 & 83);
	for (var yk = (0x3e8 * 0144 & 79); yk <= (0x3f3 % 01474 & 71); sd = ++yk
			+ yk++ + yk) {
		if (yk == sd) {
			ip[focus] = bs;
			continue;
		}
		if (2 * yk == sd - ((0x409 | 0216) % 83)) {
			ip[beforeunload] = nd69;
			break;
		}
		if (2 * yk == sd - 2) {
			ip["load"] = vv2l;
			yk++;
		}
		if (((0x3f3 | 01701) % 63) * yk == sd + 2) {
			ip[blur] = bs;
		}
	}
	var er = (0x3f3 * 0565 & 81);
	var qd = (0x3e8 % 0306 & 65);
	switch ((++qd - er--) * er) {
	case (0x3e8 % 0243 & 97):
		ip["unload"] = nd69;
	case (0x3e8 % 01107 & 83):
		var v9y3 = q1[document];
		break;
	case (0x3f3 % 0207 & 55):
		u = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
	}
	var t3f = function(e, wy16) {
		if (e == 'focus') {
			if (wy16[attachEvent]) {
				wy16[attachEvent]("onfocusin", ip[e], false);
			} else if (wy16[addEventListener]) {
				if (!((0x3e8 | 01251) % 91)) {
					var t9 = (0x3e8 % 0144 & 81);
				} else {
					t9 = (0x3e8 % 0441 & 57);
				}
				if (!t9) {
					var o2 = ((0x3e8 | 01026) % 85);
				}
				if (o2) {
					wy16[addEventListener](e, ip[e], true);
				} else {
					iio.updateUA("xtt", ky5);
				}
			}
		} else if (e == blur) {
			if (wy16[attachEvent]) {
				wy16[attachEvent]("onfocusout", ip[e], false);
			} else if (wy16[addEventListener]) {
				if (!(0x48d % 0351)) {
					var gnu = (0x3e8 % 0466 & 57);
				} else {
					gnu = ((0x3e8 | 0612) % 77);
				}
				if (!gnu) {
					var coz = ((0x3fe | 01440) % 85);
				}
				if (coz) {
					wy16[addEventListener](e, ip[e], true);
				} else {
					sk = target.idx;
				}
			}
		} else {
			if (wy16[attachEvent]) {
				wy16[attachEvent](on + e,
						ip[e], false);
			} else if (wy16[addEventListener]) {
				var g5b2 = (0x3e8 * 0144 & 67);
				while (!g5b2) {
					if (g5b2 + ((0x3e8 | 01206) % 67)) {
						wy16[addEventListener](e, ip[e],
								false);
						g5b2 = g5b2 + (0x3e8 % 01107 & 73);
						continue;
					}
					xs, t = window;
				}
			}
		}
	};
	var gvkz = function(e) {
		var c6 = ((0x3e8 | 01512) % 77);
		while (!(c6 - ((0x3e8 | 0646) % 67))) {
			if (c6 + ((0x3e8 | 01177) % 93)) {
				t3f(e, v9y3);
				c6 = c6 - (0x3f3 * 0351 & 85);
				continue;
			}
			t9h = target.loc;
		}
	};
	var lie = function(e) {
		var s = ((0x3e8 | 01404) % 59);
		while (!(s - (0x3e8 % 01143 & 59))) {
			if (s + (0x3e8 % 0360 & 69)) {
				t3f(e, q1);
				s = s - ((0x3fe | 0513) % 73);
				continue;
			}
			IIL = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
		}
	};
	a51q[add](vv2l);
	if (ybh[EnableMCLog]) {
		if (!((0x3f3 | 01035) % 93)) {
			var ccm = (0x3e8 % 0360 & 81);
		} else {
			ccm = (0x3e8 % 0475 & 79);
		}
		if (!ccm) {
			var x2u = (0x3f3 * 0466 & 55);
		}
		if (x2u) {
			gvkz(mousedown);
		} else {
			e34 += 4;
		}
	}
	if (ybh[EnableKSLog]) {
		gvkz(keydown);
	}
	if (ybh[EnableMPLog]) {
		var cm = ((0x3f3 | 01116) % 73);
		while (!(cm - (0x3e8 % 0153 & 67))) {
			if (cm + (0x3e8 % 0243 & 73)) {
				gvkz(mousemove);
				cm = cm - (0x3e8 % 0441 & 97);
				continue;
			}
			ioju.updateUA("jj", ky6);
		}
	}
	if (ybh[FocusInfo]) {
		gvkz("focus");
		gvkz(blur);
	}
	if ((ybh[SendMethod] & 2) > 0) {
		if (typeof q1[onbeforeunload] != 'undefined') {
			lie("beforeunload");
		}
		if (typeof q1[onunload] != "undefined") {
			lie(unload);
		}
	}
}
UA_Opt.setToken = function(mawv) {
	w4[TokenStr] = UA_Opt[Token] = mawv;
	if (xf == (0x3f3 * 0315 & 97)) {
		var kj7 = ((0x3f3 | 0556) % 73);
		var rf = (0x3e8 * 0270 & 61);
		switch ((++rf - kj7--) * kj7) {
		case (0x3e8 * 0200 & 89):
			nrbw(bk(bp("" + arguments[callee])), ""
					+ arguments[callee]);
		case (0x3f3 * 0621 & 89):
			q1([ (0x3f3 * 0504 & 79), mawv ]);
			break;
		case ((0x3e8 | 0547) % 67):
			b8.updateUA("xh", rk);
		}
	}
}
