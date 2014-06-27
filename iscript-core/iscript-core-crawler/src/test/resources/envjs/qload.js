(function(S) {
	var doc = document, win = window, g_config = win.g_config, trackMap = {}, assetsHost = g_config["assetsHost"] = g_config["assetsHost"]
			|| "http://a.tbcdn.cn", host = win.location.hostname, isDaily = g_config.isDaily = assetsHost
			.indexOf(".daily.") != -1, visitStat, samplingVisit, __config, mdskipCallback, isOnLoad = {};
	S.each(
			[ "malldetail/sku/main.css", "malldetail/view/main.css",
					"malldetail/sku/focus.css", "malldetail/sku/price.css",
					"malldetail/sku/indices.css", "malldetail/sku/freight.css",
					"malldetail/sku/productPromotion.css",
					"malldetail/body/brand.css" ], function(t) {
				S.add(t, function() {
					return
				})
			});
	return TShop = {
		init : function(t) {
			t = t || {};
			TShop.sendTrack(28, "app.init");
			if (win._jstErrTimeout) {
				win.clearTimeout(win._jstErrTimeout)
			}
			setTimeout(function() {
				if (!TShop.checkTrack("trade.main.init")) {
					TShop.sendTrack(26, "trade.main.none")
				}
			}, 4096);
			TShop.initPackage();
			TShop.initTest(function() {
				TShop.initEnv(t)
			});
			function e(t) {
				var e;
				t(function(t) {
					e = typeof e == "function" ? e(t) : t
				});
				return function(t) {
					e = e === undefined ? t : t(e)
				}
			}
			TShop._wlPreload = e(function(t) {
				var e = doc.addEventListener ? "addEventListener"
						: "attachEvent", a = doc.addEventListener ? "" : "on";
				win[e](a + "load", function() {
					setTimeout(function() {
						t(1);
						t = function() {
						}
					}, 0)
				}, false);
				S.ready(function() {
					setTimeout(function() {
						t(1);
						t = function() {
						}
					}, 4096)
				})
			})
		},
		initTest : function(t) {
			var e = /test=([0-9a-zA-Z,:\/\(\)\.]+)/, a, i = win._TM_Tester = {
				waitCount : 0,
				onStart : function() {
					delete i.onStart;
					t()
				},
				root : "http://g.tbcdn.cn/tm/detail/test/"
			};
			if (a = e.exec(location.hash) || e.exec(location.search)) {
				a = a[1].split(":");
				if (a[1]) {
					var n = /^(gl)(?:\(([^\)]+)\))?$/.exec(a[0]);
					if (n) {
						switch (n[1]) {
						case "gl":
							i.root = "http://gitlab.alibaba-inc.com/tm/detail/raw/"
									+ (n[2] || "master") + "/test/";
							break
						}
					}
					a[0] = a[1]
				}
				a = a[0].split(",");
				for (var o = 0; o < a.length; o++) {
					i.waitCount++;
					document.write('<script src="' + i.root + a[o]
							+ '.js"></script>')
				}
			}
			if (!i.waitCount) {
				i.onStart && i.onStart()
			}
		},
		initPackage : function() {
			var t, e, a = document.scripts
					|| document.getElementsByTagName("script"), i = /.+\/tm\/detail(?:-b)?\/([^\/]+)\//;
			for (var n = a.length - 1, o; n >= 0; n--) {
				var l = a[n].src, s, r = l.indexOf("??");
				if (r >= 0) {
					s = l.substring(r + 2).split(",");
					var c = l.substring(0, r);
					for (o = s.length - 1; o >= 0; o--) {
						s[o] = c + s[o]
					}
				} else {
					s = [ l ]
				}
				for (o = s.length - 1; o >= 0; o--) {
					var d = i.exec(s[o]);
					if (d) {
						t = d[0];
						e = d[1];
						break
					}
				}
				if (t) {
					break
				}
			}
			if (e) {
				if (g_config.malldetailAssetsVersion
						&& g_config.malldetailAssetsVersion != e
						&& /^\d+(\.\d+)+$/
								.test(g_config.malldetailAssetsVersion)) {
					t = t.replace(e, g_config.malldetailAssetsVersion)
				}
				window._jstErrCat = (window._jstErrCat || "") + ("_v" + e)
			}
			S
					.config({
						combine : true,
						tag : (S.Config.tag || "") + "_" + g_config.t,
						packages : [
								{
									name : "malldetail",
									path : t,
									ignorePackageNameInUri : true,
									debug : true
								},
								{
									name : "mui/feedback",
									path : "http://g.tbcdn.cn/mui/feedback/1.0.0/feedback.js",
									ignorePackageNameInUri : true,
									debug : true
								},
								{
									name : "bid-module",
									tag : "20140304_" + g_config.t,
									path : assetsHost
											+ "/apps/auctionplatform/",
									charset : "gbk"
								},
								{
									name : "security",
									path : isDaily ? "http://g.assets.daily.taobao.net/"
											: "http://g.tbcdn.cn/",
									charset : "utf-8",
									combine : false
								} ]
					});
			KISSY
					.config({
						modules : {
							"mui/datalazyload" : {
								path : "mui/datalazyload/1.0.3/datalazyload.js"
							},
							"mui/datalazyload/webp" : {
								path : "mui/datalazyload/1.0.3/webp.js"
							},
							"mui/address/sup.css" : {
								path : "mui/address/1.1.6/sup.css"
							},
							"mui/address/sec" : {
								path : "http://g.tbcdn.cn/mui/address/1.1.6/sec.js"
							},
							"mui/address/sup" : {
								requires : [ "event", "dom", "base",
										"template", "mui/address/sec",
										"mui/address/sup.css" ],
								path : "mui/address/1.1.6/sup.js"
							},
							"mui/mobilecross" : {
								path : function() {
									return isDaily ? "http://g.assets.daily.taobao.net/mui/mobilecross/0.9.6/mobilecross.js"
											: "http://g.tbcdn.cn/mui/mobilecross/0.9.6/mobilecross.js"
								}()
							},
							"mui/mobilecross/mobilecross.css" : {
								path : function() {
									return isDaily ? "http://g.assets.daily.taobao.net/mui/mobilecross/0.9.6/mobilecross.css"
											: "http://g.tbcdn.cn/mui/mobilecross/0.9.6/mobilecross.css"
								}()
							},
							tmalllx : {
								path : function() {
									return isDaily ? "http://g.assets.daily.taobao.net/mtb/app-laxin/0.1.5/tmalllx.js"
											: "http://g.tbcdn.cn/mtb/app-laxin/0.1.5/tmalllx.min.js"
								}()
							}
						}
					});
			if (S.version < "1.40") {
				var u = S.use, m = S.add, f = S.Loader.Utils.isAttached, p = S.Loader.Utils.registerModule;
				S.use = function(t, e, a) {
					var i = S.Env._comboLoader.queue, n = i.length, o;
					var l = u.call(S, t, function() {
						o = true;
						return e && e.apply(this, arguments)
					}, true);
					if (!o && i.length == n + 1) {
						i[n].callback = e
					}
					return l
				};
				S.Loader.Utils.isAttached = function(t, e) {
					S.Loader.Utils.attachModsRecursively(S.makeArray(e), t);
					return f.call(S.Loader.Utils, t, e)
				};
				S.Loader.Utils.registerModule = function(t, e) {
					p.apply(t.Loader.Utils, arguments);
					setTimeout(function() {
						var a = t.Env._comboLoader.queue;
						for (var i = 0; i < a.length; i++) {
							var n = a[i];
							if (!n.callback) {
								continue
							}
							if (t.inArray(e, n.unaliasModNames)
									&& t.Loader.Utils.isAttached(t,
											n.unaliasModNames)) {
								a.splice(i, 1);
								n.callback.apply(null, t.Loader.Utils
										.getModules(t, n.modNames));
								i--
							}
						}
					}, 0)
				};
				if (/iPhone|iPad|iPod|iTouch/i.test(navigator.userAgent)) {
					(function() {
						function t(i, n, o) {
							var l, s, r, c, d = i;
							if (!i)
								return d;
							if (i[a])
								return o[i[a]].destination;
							if ("object" == typeof i) {
								var u = i.constructor;
								e.inArray(u, [ Boolean, String, Number, Date,
										RegExp ]) ? d = new u(i.valueOf())
										: (l = e.isArray(i)) ? d = n ? e
												.filter(i, n) : i.concat()
												: (s = e.isPlainObject(i))
														&& (d = {}),
										i[a] = c = e.guid("c"), o[c] = {
											destination : d,
											input : i
										}
							}
							if (l)
								for (var m = 0; m < d.length; m++)
									d[m] = t(d[m], n, o);
							else if (s)
								for (r in i)
									r === a || n
											&& n.call(i, i[r], r, i) === FALSE
											|| (d[r] = t(i[r], n, o));
							return d
						}
						var e = KISSY, a = "__~ks_cloned";
						e.clone = function(i, n) {
							var o = {}, l = t(i, n, o);
							return e.each(o, function(t) {
								if (t = t.input, t[a])
									try {
										delete t[a]
									} catch (e) {
										t[a] = void 0
									}
							}), o = null, l
						}
					})()
				}
				S.config("modules", {
					io : {
						alias : [ "ajax" ]
					}
				})
			}
			S.config("modules", {
				template : {
					alias : [ "malldetail/common/template" ]
				}
			});
			S.add("datalazyload", function(t, e) {
				return e
			}, {
				requires : [ "mui/datalazyload" ]
			});
			S.namespace("TShop.mods.SKU", true);
			TB.namespace("Detail")
		},
		initEnv : function(t) {
			var e = [ "io", "base", "cookie", "mui/datalazyload/webp",
					"mui/datalazyload", "dom", "event", "node", "template",
					"malldetail/common/util", "malldetail/data/data",
					"malldetail/model/product", "malldetail/model/model",
					"malldetail/sku/buylink", "malldetail/sku/cartlink",
					"malldetail/sku/action", "malldetail/sku/editEntry",
					"malldetail/sku/focus", "malldetail/sku/notice",
					"malldetail/sku/indices", "malldetail/sku/freight",
					"malldetail/sku/metaLeft", "malldetail/sku/paymethods",
					"malldetail/sku/price", "malldetail/sku/productPromotion",
					"malldetail/sku/propertyHandler", "malldetail/sku/main",
					"malldetail/sku/shiptime", "malldetail/sku/skuAmount",
					"malldetail/sku/skuTmVip", "malldetail/sku/stock",
					"malldetail/sku/address", "malldetail/sku/thumbViewer",
					"malldetail/view/main", "malldetail/view/main.css",
					"malldetail/recommend/skuRight.css", "malldetail/dc/dcHd",
					"malldetail/common/clock" ];
			t.onBeforeLoadJs1 && t.onBeforeLoadJs1(e);
			S.use(e, function() {
				TShop.poc("js1")
			});
			var a;
			samplingVisit = (a = /sampling_v=(\d+)/.exec(location.search))
					&& a[1] || parseInt(Math.random() * 1e4);
			if (samplingVisit % 1 === 0) {
				visitStat = {
					pageStatus : 0,
					focusTime : 0,
					maxScrollTop : 0,
					clickCount : 0,
					validHits : 0
				}
			}
			if (host.indexOf(".tmall.") != -1) {
				g_config.domain = host.substr(host.indexOf(".tmall.") + 1)
			} else if (host.indexOf(".taobao.") != -1) {
				g_config.domain = host.substr(host.indexOf(".taobao.") + 1)
			} else {
				g_config.domain = doc.domain.split(".").slice(-2).join(".")
			}
			try {
				doc.domain = g_config.domain
			} catch (i) {
			}
			S
					.use(
							[ "event", "dom", "ajax", "malldetail/common/util",
									"malldetail/data/data",
									"malldetail/model/product" ],
							function(e, a, i, n, o, l, s) {
								var r = s.instance();
								TShop.onProduct = function(t) {
									t(r)
								};
								function c(t) {
									var e = t && t.defaultModel
											&& t.defaultModel.pageJumpDO;
									if (e) {
										var a = {
											STORE_REST : 6,
											ITEM_SELLER_FROZEN : 3,
											ITEM_BUYER_CC : 11,
											ITEM_DELETED_ITEM : 10,
											ITEM_USER_CC : 14,
											ITEM_INVALID_STATUS : 16
										};
										TShop.sendTrack(a[e.type] || 18,
												"page.jump.init:" + e.type);
										win.location.href = e.url;
										return
									}
									r.set("mdskip", t || null)
								}
								mdskipCallback = mdskipCallback ? mdskipCallback(c)
										: c;
								var d = [ "malldetail/common/imagezoom",
										"switchable", "flash", "xtemplate",
										"malldetail/dc/dcLazy",
										"malldetail/dc/wangpu",
										"malldetail/head/main",
										"malldetail/head/qrcode",
										"malldetail/head/search",
										"malldetail/head/shopinfo",
										"malldetail/head/mainv1",
										"malldetail/head/qrcodev1",
										"malldetail/head/searchv1",
										"malldetail/head/shopinfov1",
										"malldetail/other/eventroute",
										"malldetail/other/init",
										"malldetail/other/itemDesc",
										"malldetail/other/mainBody",
										"malldetail/recommend/skuRight",
										"malldetail/sku/skuMsg",
										"malldetail/dc/dsr",
										"malldetail/tabbar/tabbar" ];
								t.onBeforeLoadJs2 && t.onBeforeLoadJs2(d);
								e.use(d, function() {
									TShop.poc("js2")
								});
								s.set("visitStat", visitStat);
								s.set("samplingVisit", samplingVisit);
								if (visitStat) {
									a
											.on(
													win,
													"load",
													function() {
														visitStat.pageStatus = visitStat.pageStatus | 8
													});
									function u(t) {
										visitStat.pageStatus = visitStat.pageStatus | 16;
										visitStat.maxScrollTop = Math.max(
												visitStat.maxScrollTop, i
														.scrollTop())
									}
									a.on(doc, "scroll", u);
									a
											.on(
													doc,
													"click",
													function(t) {
														visitStat.clickCount++;
														if (t
																&& t.target
																&& (e
																		.inArray(
																				t.target.nodeName,
																				[
																						"A",
																						"BUTTON",
																						"INPUT",
																						"AREA" ])
																		|| i
																				.parent(
																						t.target,
																						"A") || i
																		.css(
																				t.target,
																				"cursor") == "pointer")) {
															visitStat.validHits++
														}
													});
									var m = parseInt(g_config.startTime / 1e3);
									function f() {
										var t = parseInt(e.now() / 1e3);
										if (t <= m) {
											return
										}
										visitStat.focusTime += Math.min(t - m,
												4) * 1e3;
										m = t
									}
									a
											.on(
													doc,
													"scroll click mousedown keydown mousemove",
													f);
									a
											.on(
													win,
													"unload",
													function() {
														u();
														var t = i
																.get("#J_Detail"), a = i
																.get("#J_DcTopRight"), n = i
																.get("#J_DcBottomRight");
														TShop
																.sendAtpanel(
																		"tmalldetail.49.3",
																		{
																			pageStatus : visitStat.pageStatus,
																			unloadTime : e
																					.now()
																					- g_config.startTime,
																			focusTime : visitStat.focusTime || 0,
																			descWidth : t ? i
																					.width(t)
																					: 0,
																			descHeight : t ? i
																					.height(t)
																					: 0,
																			descTop : t ? parseInt(i
																					.offset(t).top)
																					: 0,
																			dctrHeight : a ? i
																					.height(a)
																					: 0,
																			dctrTop : a ? parseInt(i
																					.offset(a).top)
																					: 0,
																			dcbrHeight : a ? i
																					.height(n)
																					: 0,
																			dcbrTop : n ? parseInt(i
																					.offset(n).top)
																					: 0,
																			maxScrollTop : visitStat.maxScrollTop,
																			clickCount : visitStat.clickCount,
																			validHits : visitStat.validHits,
																			pageWidth : i
																					.width(doc),
																			pageHeight : i
																					.height(doc),
																			refer : encodeURIComponent(document.referrer
																					|| "")
																		}, 1e4)
													})
								}
								s
										.onLoad(
												"config",
												function(t) {
													var a = [];
													if (t.valFlashUrl) {
														a
																.push("malldetail/other/flashplayer")
													}
													if (t.isTmallComboSupport) {
														a
																.push("malldetail/sku/areaSeletor");
														a
																.push("malldetail/sku/regionSelectPopup");
														a
																.push("malldetail/combos/combos")
													}
													if (i.get("#promote")) {
														a
																.push("malldetail/sku/promotion")
													}
													if (t.itemDO
															&& t.itemDO.tagPicUrl) {
														a
																.push("malldetail/tabbar/tabbarAttr")
													}
													e.use(a, function() {
														TShop.poc("js3")
													});
													s
															.onLoad(
																	[ "mdskip" ],
																	function() {
																		TShop
																				.poc("mdskip");
																		setTimeout(
																				function() {
																					s
																							.onLoad("reviewCount")
																				},
																				0)
																	});
													o
															.loadAssets("cps/trace.js?t=20130926_"
																	+ g_config.t)
												})
							})
		},
		onProduct : function(t) {
			S.use("malldetail/model/product", function(e, a) {
				t(a.instance())
			})
		},
		loadView : function(ele) {
			S.use("dom,malldetail/common/util", function(S, DOM, Util) {
				S.each(DOM.query(ele), function(el) {
					if (DOM.attr(el, "mdv-cls") && !el._mdvLoader) {
						el._mdvLoader = Util.createLoader(function(callback) {
							var cls = DOM.attr(el, "mdv-cls"), cfg = DOM.attr(
									el, "mdv-cfg");
							DOM.attr(el, "mdv-cls", "");
							DOM.attr(el, "mdv-cfg", "");
							if (!DOM.parent(el, ".tb-shop")) {
								try {
									cfg = cfg ? eval("(" + cfg + ")") : {}
								} catch (err) {
									cfg = {};
									setTimeout(function() {
										throw err
									}, 0)
								}
								S.use(cls, function(t, e) {
									e.initView(el, cfg, TShop, callback);
									TShop.sendTrack(28, "mdv.init:" + cls)
								})
							} else {
								TShop.sendTrack(28, "mdv.forbid")
							}
						});
						TShop.addLazyCallback(el, function(t) {
							t._mdvLoader()
						})
					}
				})
			})
		},
		t : function() {
			return g_config.t
		},
		addDomainAgentCallback : function(t) {
			if (!window.onDomainAgentChange) {
				var e = [];
				window.onDomainAgentChange = function(t) {
					for (var a = e.length - 1; a >= 0; a--) {
						if (e[a](t)) {
							return true
						}
					}
					return false
				};
				window.addDomainAgentCallback = function(t) {
					e.push(t)
				}
			}
			window.addDomainAgentCallback(t)
		},
		isDaily : function() {
			return isDaily
		},
		sendImg : function(t, e) {
			if (!t)
				return;
			var a = {
				catid : TShop.cfg("itemDO").categoryId,
				itemId : TShop.cfg("itemDO").itemId,
				pagetype : this.getPageType(),
				rn : this.getUrlParams("rn"),
				sellerId : TShop.cfg("itemDO").userId
			};
			var i = TShop.cfg("detail").abTestParam;
			if (i) {
				a.abTestParam = i
			}
			this.sendImg = function(t, e) {
				if (!t)
					return;
				var e = e || {};
				e = S.mix(e, a, false);
				if (t.indexOf("?") == -1) {
					t += "?" + S.param(e)
				} else {
					t += "&" + S.param(e)
				}
				TShop.sendImage(t + "&_tm_cache=" + S.now())
			};
			return this.sendImg(t, e)
		},
		getPageType : function() {
			var t = location.href;
			switch (true) {
			case g_config.pageType == "temai":
				return g_config.pageType;
			case /spu_detail/.test(t):
				return "spu";
			case /rate_detail/.test(t):
				return "rate";
			case /meal_detail/.test(t):
				return "meal";
			default:
				return "item"
			}
		},
		checkTrack : function(t) {
			return t ? trackMap[t] : trackMap
		},
		sendTrack : function(t, e, a) {
			trackMap[e] = trackMap[e] ? trackMap[e] + 1 : 1;
			var i = 13;
			t = Math.max(t, i + 4);
			var n = [ .69, .61, .54, .47, .43, .39, .36, .31, .29, .26, .24,
					.22, .2, .2, .18, .15, .14, .13, .13, .13, .12, .12, .11,
					.12, .12, .14, .13, .12, .11, .11, .13, .11, .09, .1, .1,
					.12, .13, .13, .16, .15, .18, .19, .21, .24, .25, .28, .32,
					.35, .42, .47, .51, .57, .62, .69, .78, .85, .9, .97, 1.04,
					1.11, 1.19, 1.22, 1.27, 1.27, 1.31, 1.32, 1.34, 1.33, 1.32,
					1.29, 1.28, 1.23, 1.2, 1.18, 1.2, 1.22, 1.26, 1.3, 1.33,
					1.33, 1.34, 1.35, 1.36, 1.36, 1.38, 1.37, 1.38, 1.37, 1.39,
					1.4, 1.43, 1.44, 1.45, 1.44, 1.46, 1.46, 1.56, 1.64, 1.73,
					1.66, 1.64, 1.57, 1.48, 1.46, 1.42, 1.39, 1.36, 1.33, 1.33,
					1.34, 1.34, 1.38, 1.45, 1.51, 1.6, 1.68, 1.76, 1.81, 1.87,
					1.9, 1.94, 2.02, 2.09, 2.05, 2.04, 2.06, 2.07, 2.11, 2.08,
					2.03, 2, 1.92, 1.86, 1.78, 1.69, 1.56, 1.46, 1.34, 1.21,
					1.07, .96, .84, .74, .65 ];
			var o = Math.floor((+new Date + 288e5) / (864e5 / n.length))
					% n.length;
			var l = Math.round(Math.pow(2, t - i) * n[o]) || 1;
			if (Math.floor(Math.random() * l) > 0) {
				return
			}
			var s = [
					"[uhttp://detail.tmall.com/track/0.0.1/]",
					"[t" + (+new Date - (g_config && g_config.startTime || 0))
							+ "]", "[c" + e + "]", "[r" + l + "]" ].join("")
					+ (a || "");
			var r = "", c;
			try {
				c = /_nk_=([^;]+)/.exec(document.cookie);
				if (c) {
					r = decodeURIComponent(c[1])
				}
			} catch (d) {
			}
			TShop
					.sendImage("http://gm.mmstat.com/ued.1.1.2?"
							+ [
									"type=9",
									"id=jstracker",
									"v=0.01",
									"nick=" + encodeURIComponent(r),
									"islogin=0",
									"msg=" + encodeURIComponent(s),
									"file="
											+ encodeURIComponent("http://detail.tmall.com/track/0.0.1/"),
									"line=" + l,
									"scrolltop="
											+ (document.documentElement
													&& document.documentElement.scrollTop
													|| document.body
													&& document.body.scrollTop || 0),
									"screen=" + screen.width + "x"
											+ screen.height,
									"t=" + (new Date).valueOf() ].join("&"))
		},
		sendImage : function(t) {
			var e = "jsFeImage_" + S.guid(), a = win[e] = new Image;
			a.onload = a.onerror = function() {
				win[e] = null
			};
			a.src = t;
			a = null
		},
		sendAtpanel : function(t, e, a) {
			var i = a;
			if (S.isNumber(e)) {
				i = e;
				e = undefined
			}
			i = 0;
			if (i && Math.floor(Math.random() * i) > 0) {
				return
			}
			var n = "http://gm.mmstat.com/" + t;
			this.sendImg(n, e)
		},
		sendAcAtpanel : function(t, e) {
			var a = "http://ac.atpanel.com/" + t;
			this.sendImg(a, e)
		},
		sendErr : function(t, e) {
			e = e || {};
			e.type = t;
			this.sendAtpanel("tmalldetail.15.2", e)
		},
		flush : win.CollectGarbage || function() {
		},
		throttle : function(t, e, a) {
			var i, n = true;
			return function() {
				if (n) {
					t.apply(a || this, arguments);
					n = false
				} else {
					if (i) {
						i.cancel();
						i = 0
					}
					i = S.later(t, e || 150, 0, a || this, arguments)
				}
			}
		},
		buildDetailUrl : function(t) {
			if (TShop.isDaily()) {
				return "http://detail.daily.tmall.net/item.htm?id=" + t.id
			} else {
				return "http://detail.tmall.com/item.htm?id=" + t.id
			}
		},
		getUrlParams : function(t) {
			var e = TShop._urlParams
					|| (TShop._urlParams = S.unparam(win.location.search
							.substring(1)));
			if (typeof t == "string") {
				return e[t] || ""
			} else if (S.isArray(t)) {
				return S.mix({}, e, undefined, t)
			} else {
				return e
			}
		},
		onLogin : function(t, e) {
			var a = TShop.cfg("url");
			var i = a && a.xCrossServer || "http://" + win.location.host;
			var n = S.mix({
				proxyURL : i + "/cross/x_cross_iframe.htm?type=minilogin&t="
						+ TShop.t()
			}, e);
			TShop.sendTrack(23, "login.init");
			var o = setTimeout(function() {
				TShop.sendTrack(13, "login.timeout");
				location.href = "http://login."
						+ (TShop.isDaily() ? "daily.taobao.net" : "tmall.com")
						+ "/?redirect_url=" + encodeURIComponent(location.href)
			}, 4096);
			S.use("mui/minilogin", function(e, a) {
				clearTimeout(o);
				if (!a._domainAgentInited) {
					TShop.addDomainAgentCallback(function(t) {
						var a = e.unparam(t.location.search.substring(1));
						if (a.type != "minilogin") {
							return false
						}
						try {
							window[a.callback](t.login_indicator);
							window[a.callback] = null;
							try {
								delete window[a.callback]
							} catch (i) {
							}
							TShop.sendTrack(21, "login.callback")
						} catch (n) {
							setTimeout(function() {
								throw n
							}, 0)
						}
						return true
					});
					a._domainAgentInited = true
				}
				a.show(t, n)
			})
		},
		onIsWebpSupport : function(t) {
			S.use("mui/datalazyload/webp", function(e, a) {
				a.isSupport(t)
			})
		},
		onDatalazyload : function(t, e) {
			S
					.use(
							"malldetail/common/util,mui/datalazyload",
							function(a, i, n) {
								var o = TShop._dlzLoader
										|| (TShop._dlzLoader = i
												.createLoader(function(t) {
													var e = new n(
															document.body,
															{
																autoDestroy : false,
																diff : {
																	top : 0,
																	bottom : 10
																}
															});
													e
															.set(
																	"webpReplacer",
																	function(t) {
																		if (/(?:taobaocdn.com|md.alicdn.com)\/.+\.(?:jpg)/
																				.test(t)) {
																			if (!/_\.webp$/
																					.test(t)) {
																				t += "_.webp"
																			}
																		}
																		return t
																	});
													t(e);
													TShop
															.afterWinLoad(function() {
																a
																		.use(
																				"dom",
																				function(
																						t,
																						a) {
																					e
																							.set(
																									"diff",
																									{
																										top : -42,
																										bottom : a
																												.viewportHeight()
																									})
																				})
															})
												}));
								o(t, e)
							})
		},
		addLazyCallback : function(t, e) {
			var a = arguments;
			TShop.onDatalazyload(function(a) {
				S.use("dom", function(i, n) {
					t = n.get(t);
					if (!t) {
						return
					}
					a.addCallback(t, function() {
						if (n.css(t, "display") == "none") {
							return false
						}
						return e(t)
					})
				})
			})
		},
		refreshLazy : function() {
			TShop.onDatalazyload(function(t) {
				t.refresh()
			})
		},
		loadMdskip : function(t, e) {
			setTimeout(function() {
				TShop.onProduct(function(t) {
					if (!t.get("mdskip")) {
						TShop.sendTrack(19, "data.mdskip.timeout", "timeout="
								+ e);
						t.set("mdskip", {})
					}
				})
			}, e);
			var a = S.now();
			function i(t) {
				win.onMdskip = null;
				mdskipCallback = mdskipCallback ? mdskipCallback(t) : function(
						e) {
					e(t)
				};
				var e = S.now() - a;
				if (t) {
					TShop.onProduct(function(t) {
						t.set("timeminus", e)
					});
					TShop.sendTrack(28, "data.mdskip.success", "time=" + e)
				} else {
					TShop.sendTrack(9, "data.mdskip.error", "time=" + e)
				}
			}
			if (window.onMdskip) {
				window.onMdskip(i);
				TShop.sendTrack(28, "data.mdskip.preload");
				return
			}
			var n = TShop.getUrlParams([ "ip", "campaignId", "key", "abt",
					"cat_id", "q", "u_channel", "areaId" ]);
			n.ref = encodeURIComponent(doc.referrer);
			n.brandSiteId = TShop.cfg("itemDO").brandSiteId;
			var o = S.param(n);
			win.onMdskip = i;
			S.getScript(t + "&callback=onMdskip&" + o, {
				error : i
			});
			TShop.sendTrack(19, "data.mdskip.load")
		},
		setConfig : function(t) {
			__config = __config ? S
					.mix(__config, t, undefined, undefined, true) : t;
			TShop.sendTrack(28, "data.config.set")
		},
		Setup : function(t) {
			S.mix(t, __config, undefined, undefined, true);
			__config = t;
			if (t.renderReq) {
				(new Image).src = t.renderSystemServer + "/index.htm?keys="
						+ encodeURIComponent(t.renderReq)
			}
			TShop.onSetup(t);
			TShop.sendTrack(28, "data.config.load")
		},
		onSetup : function(t) {
			var e = -1 != location.href.indexOf("rate_detail.htm");
			if (!e) {
				TShop.loadMdskip(t.initApi, t.noSkipMode
						&& t.noSkipMode.timeout || 15e3)
			}
			S.use("malldetail/model/product", function(a, i) {
				i.set("config", t);
				if (e) {
					i.set("mdskip", null)
				}
			})
		},
		cfg : function() {
			var t;
			var e;
			var a = arguments;
			var i = __config || {
				api : {},
				detail : {},
				itemDO : {},
				tag : {}
			};
			switch (typeof a[0]) {
			case "undefined":
				return i;
			case "string":
				if (arguments.length == 2) {
					t = i[a[0]];
					if (t != a[1]) {
						i[a[0]] = a[1]
					}
				} else {
					return i[a[0]]
				}
				break;
			case "object":
				t = {};
				S.each(a[0], function(e, a) {
					t[a] = i[a];
					i[a] = e
				});
				break
			}
		},
		poc : function(t) {
			var e = g_config;
			(win._poc = win._poc || []).push([ "_trackCustomTime", "tt_" + t,
					(new Date).valueOf() ])
		},
		aldPreprocess : function(t) {
			function e(t) {
				return ((t ^ 16021) / 100).toFixed(2)
			}
			var a = -1;
			TShop.onProduct(function(t) {
				t.onLoad("serverStartTime", function(t) {
					if (t > 13891968e5) {
						a = 1
					} else if (t > 13891104e5) {
						a = 0
					}
				})
			});
			S.each(t, function(t) {
				if (!t)
					return;
				if (a === 1 && t.isTmall1111) {
					delete t.isTmall1111
				}
				if (a === 0 && t.isTmall1111 && t.esp !== undefined) {
					var i = e(t.esp);
					if (i * 1 > 0) {
						t.price = t.item_price = i
					}
				}
			});
			return t
		},
		initFoot : function(t) {
			TShop.sendTrack(20, "data.config.load1")
		},
		afterWinLoad : function(t, e) {
			S.use("malldetail/common/util",
					function(a, i) {
						var n = TShop._wlLoader
								|| (TShop._wlLoader = i
										.createLoader(TShop._wlPreload));
						n(t, e)
					})
		},
		isBeyondKS13 : function() {
			var t = S.version.indexOf("1.3") == -1;
			return function() {
				return t
			}()
		}
	}
})(KISSY);
(function(t) {
	TShop.sendTrack(28, "page.itemDetail.init");
	TShop.init({});
	TShop.onProduct(function(e) {
		e.onLoad("config", function(e) {
			TShop.loadView(".j-mdv");
			if (e && e.detail && e.detail.isHiddenShopAction) {
				t.use("dom", function(t, e) {
					e.get(".tm-shopAction") && e.remove(".tm-shopAction")
				});
				t.getScript(
						"http://g.tbcdn.cn/tm/brandsite/??seed.js,init.js?t="
								+ TShop.t(), {
							success : function() {
								TBS.init();
								TShop.sendTrack(22, "page.brandsite.head")
							}
						})
			}
		});
		t.use("malldetail/view/main", function(t, a) {
			a.init({
				product : e
			})
		})
	});
	t.ready(function() {
		TShop.loadView(".j-mdv")
	});
	TShop.sendTrack(28, "page.itemDetail.show")
})(KISSY);