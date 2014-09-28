/*
    GNU LESSER GENERAL PUBLIC LICENSE
    Copyright (C) 2006 The Lobo Project

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

    Contact info: lobochief@users.sourceforge.net
 */

package com.lezo.iscript.envjs.window;

public class NavigatorAdapt {
	private String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.116 Safari/537.36";
	private String platform = "Win32";
	private String appVersion = "5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.114 Safari/537.36";
	private String appName = "Netscape";
	private String appCodeName = "Mozilla";

	public NavigatorAdapt() {
		super();
	}

	public String getAppMinorVersion() {
		return null;
	}

	public String getPlatform() {
		return platform;
	}

	public String getUserAgent() {
		return this.userAgent;
	}

	public String getVendor() {
		return null;
	}

	public String getProduct() {
		return null;
	}

	public boolean javaEnabled() {
		// True always?
		return true;
	}

	private MimeTypesCollection mimeTypes;

	public MimeTypesCollection getMimeTypes() {
		synchronized (this) {
			MimeTypesCollection mt = this.mimeTypes;
			if (mt == null) {
				mt = new MimeTypesCollection();
				this.mimeTypes = mt;
			}
			return mt;
		}
	}

	public class MimeTypesCollection {
		// Class must be public to allow JavaScript access
		public int getLength() {
			return 0;
		}

		public Object item(int index) {
			return null;
		}

		public Object namedItem(String name) {
			return null;
		}
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppCodeName() {
		return appCodeName;
	}

	public void setAppCodeName(String appCodeName) {
		this.appCodeName = appCodeName;
	}

}