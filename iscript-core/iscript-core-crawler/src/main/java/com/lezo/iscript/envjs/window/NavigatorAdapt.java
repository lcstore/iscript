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

	public NavigatorAdapt() {
		super();
	}

	public String getAppCodeName() {
		return null;
	}

	public String getAppName() {
		return null;
	}

	public String getAppVersion() {
		return null;
	}

	public String getAppMinorVersion() {
		return null;
	}

	public String getPlatform() {
		return null;
	}

	public String getUserAgent() {
		return "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.116 Safari/537.36";
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
}