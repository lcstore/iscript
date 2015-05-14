var args = {};
args.url = "http://httpbin.org/ip";
var builderFactory = {};
(function() {
	var init = false;
	(function() {
		if (init) {
			return;
		}
		function BaseUrlBuilder() {
			this.encode = function(input) {
				return input;
			}, this.build = function(input) {
				return this.encode(input);
			}
		}
		function PrxysComUrlBuilder() {
			this.flag = 'norefer';
			this.ginf = {
				url : 'http://prxys.com',
				script : 'browse.php',
				target : {
					h : '',
					p : '',
					b : '',
					u : ''
				},
				enc : {
					u : 'v64EJ2QhGK4Nldb8ze75AsYt5Dl7onvKjpeTfS0LqSnBVmxiqsbPJZ8EtkzFxUfE7hncZbLfUYFD80KpjL4Sz2mQaLlvtr0qwbqmd1pVOSoMJY1SyTyXKKDIjO4A3SQq',
					e : '1',
					x : '',
					p : ''
				},
				b : '29'
			};

			// Shortcut to <URL TO SCRIPT><SCRIPT NAME>
			var ginf = this.ginf;
			if (ginf) {
				if (ginf.enc.u && ginf.enc.x) {
					ginf.target.h = arcfour(ginf.enc.u,
							base64_decode(ginf.target.h));
					ginf.target.p = arcfour(ginf.enc.u,
							base64_decode(ginf.target.p));
					ginf.target.u = arcfour(ginf.enc.u,
							base64_decode(ginf.target.u));
				}
				siteURL = ginf.url + '/' + ginf.script;
			}
			;
		}
		PrxysComUrlBuilder.prototype = new BaseUrlBuilder();
		PrxysComUrlBuilder.prototype.encode = function(input) {
			var ginf = this.ginf;
			var flag = this.flag;
			if (ginf.enc.e) {

				// Part of our encoding is to remove HTTP (saves space and helps
				// avoid
				// detection)
				input = input.substr(4);

				// Are we using unique URLs?
				if (ginf.enc.u) {

					// Encrypt
					input = base64_encode(arcfour(ginf.enc.u, input));
				}
			}
			// Protect chars that have other meaning in URLs
			input = encodeURIComponent(input);

			// Return in path info format (only when encoding is on)
			if (ginf.enc.p && ginf.enc.e) {
				input = input.replace(/%/g, '_');
				return siteURL + '/' + input + '/b' + ginf.b + '/'
						+ (flag ? 'f' + flag + '/' : '') + jumpTo;
			}
			var jumpTo = "";
			// Otherwise, return in 'normal' (query string) format
			return siteURL + '?u=' + input + '&b=' + ginf.b
					+ (flag ? '&f=' + flag : '') + jumpTo;
		}

		/***********************************************************************
		 * Helper functions - mostly javascript equivalents of the PHP function
		 * with the same name
		 **********************************************************************/

		function base64_encode(d) {
			var q = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=';
			var z, y, x, w, v, u, t, s, i = 0, j = 0, p = '', r = [];
			if (!d) {
				return d;
			}
			do {
				z = d.charCodeAt(i++);
				y = d.charCodeAt(i++);
				x = d.charCodeAt(i++);
				s = z << 16 | y << 8 | x;
				w = s >> 18 & 0x3f;
				v = s >> 12 & 0x3f;
				u = s >> 6 & 0x3f;
				t = s & 0x3f;
				r[j++] = q.charAt(w) + q.charAt(v) + q.charAt(u) + q.charAt(t);
			} while (i < d.length);
			p = r.join('');
			var r = d.length % 3;
			return (r ? p.slice(0, r - 3) : p) + '==='.slice(r || 3);
		}
		function base64_decode(d) {
			var q = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=';
			var z, y, x, w, v, u, t, s, i = 0, j = 0, r = [];
			if (!d) {
				return d;
			}
			d += '';
			do {
				w = q.indexOf(d.charAt(i++));
				v = q.indexOf(d.charAt(i++));
				u = q.indexOf(d.charAt(i++));
				t = q.indexOf(d.charAt(i++));
				s = w << 18 | v << 12 | u << 6 | t;
				z = s >> 16 & 0xff;
				y = s >> 8 & 0xff;
				x = s & 0xff;
				if (u == 64) {
					r[j++] = String.fromCharCode(z);
				} else if (t == 64) {
					r[j++] = String.fromCharCode(z, y);
				} else {
					r[j++] = String.fromCharCode(z, y, x);
				}
			} while (i < d.length);
			return r.join('');
		}
		function arcfour(k, d) {
			var o = '';
			s = new Array();
			var n = 256;
			l = k.length;
			for (var i = 0; i < n; i++) {
				s[i] = i;
			}
			for (var j = i = 0; i < n; i++) {
				j = (j + s[i] + k.charCodeAt(i % l)) % n;
				var x = s[i];
				s[i] = s[j];
				s[j] = x;
			}
			for (var i = j = y = 0; y < d.length; y++) {
				i = (i + 1) % n;
				j = (j + s[i]) % n;
				x = s[i];
				s[i] = s[j];
				s[j] = x;
				o += String
						.fromCharCode(d.charCodeAt(y) ^ s[(s[i] + s[j]) % n]);
			}
			return o;
		}

		// Make a replacement using position and length values
		function substr_replace(str, replacement, start, length) {
			return str.substr(0, start) + replacement
					+ str.substr(start + length);
		}
		init = true;
		builderFactory["proxys.com"] = new PrxysComUrlBuilder();
	}())
	var builder = builderFactory["proxys.com"];
	var dUrl = builder.build(args.url);
	return dUrl;
}(args))