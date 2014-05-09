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
/*
 * Created on Nov 12, 2005
 */
package com.lezo.iscript.envjs.window;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import com.lezo.iscript.envjs.dom.DocumentAdapt;

public class WindowAdapt {
	private static final Logger logger = Logger.getLogger(WindowAdapt.class.getName());
	private static final String GLOABLE_SCOPE_KEY = "g_scope_key";
	private volatile DocumentAdapt document;
	private WindowAdapt window = this;
	private WindowAdapt self = this;
	private List<?> frames = new ArrayList<Object>();
	private String name = "";
	private boolean closed = false;
	private String defaultStatus = "";
	private HistoryAdapt history = new HistoryAdapt(this);
	private LocationAdapt location = new LocationAdapt();
	private NavigatorAdapt navigator = new NavigatorAdapt();
	private WindowAdapt opener = this;
	private WindowAdapt parent = null;
	private WindowAdapt top = this;
	private WindowAdapt content = this;
	private ScreenAdapt screen = new ScreenAdapt();
	private String status = "";
	private int outerWidth = 0;
	private int outerHeight = 0;
	private int pageXOffset = 0;
	private int pageYOffset = 0;
	private int innerWidth = 0;
	private int innerHeight = 0;
	private int screenX = 0;
	private int screenY = 0;
	private int screenLeft = 0;
	private int screenTop = 0;
	private int length = 0;
	private int scrollX = 0;
	private int scrollY = 0;
	private int scrollMaxX = 0;
	private int scrollMaxY = 0;
	private boolean fullScreen = false;
	private Object frameElement;
	private Object sessionStorage;

	public int setInterval(final Function aFunction, final double aTimeInMs) {
		if (aTimeInMs > Integer.MAX_VALUE || aTimeInMs < 0) {
			throw new IllegalArgumentException("Timeout value " + aTimeInMs + " is not supported.");
		}
		return 0;
	}

	public int setInterval(final String aExpression, double aTimeInMs) {
		if (aTimeInMs > Integer.MAX_VALUE || aTimeInMs < 0) {
			throw new IllegalArgumentException("Timeout value " + aTimeInMs + " is not supported.");
		}
		return 0;
	}

	public void clearInterval(int aTimerID) {
	}

	public void alert(String message) {
		logger.info("alert:" + message);
		System.out.println("alert:" + message);
	}

	public void back() {
	}

	public void blur() {
	}

	public int setTimeout(final String expr, double millis) {
		if (millis > Integer.MAX_VALUE || millis < 0) {
			throw new IllegalArgumentException("Timeout value " + millis + " is not supported.");
		}
		return 0;
	}

	public int setTimeout(final Function function, double millis) {
		if (millis > Integer.MAX_VALUE || millis < 0) {
			throw new IllegalArgumentException("Timeout value " + millis + " is not supported.");
		}
		return 0;
	}

	public void clearTimeout(int timeoutID) {
	}

	public void close() {
	}

	public boolean confirm(String message) {
		return false;
	}

	public Object eval(String javascript) {
		Context ctx = Context.enter();
		try {
			Scriptable scope = getGloableScope();
			if (scope == null) {
				throw new IllegalStateException(
						"Scriptable (scope) instance was expected to be keyed as UserData to document using "
								+ GLOABLE_SCOPE_KEY);
			}
			String scriptURI = "window.eval";
//			if (logger.isLoggable(Level.INFO)) {
//				logger.info("eval(): javascript follows...\r\n" + javascript);
//			}
			return ctx.evaluateString(scope, javascript, scriptURI, 1, null);
		} finally {
			Context.exit();
		}
	}

	public void focus() {
	}

	private Scriptable getGloableScope() {
		Object gObject = document.getUserData(GLOABLE_SCOPE_KEY);
		return gObject == null ? null : (Scriptable) gObject;
	}

	public WindowAdapt open(String relativeUrl, String windowName, String windowFeatures, boolean replace) {
		return null;
	}

	public WindowAdapt open(String url) {
		return this.open(url, "window:9999");
	}

	public WindowAdapt open(String url, String windowName) {
		return this.open(url, windowName, "", false);
	}

	public WindowAdapt open(String url, String windowName, String windowFeatures) {
		return this.open(url, windowName, windowFeatures, false);
	}

	public String prompt(String message) {
		return this.prompt(message, "");
	}

	public String prompt(String message, int inputDefault) {
		return this.prompt(message, String.valueOf(inputDefault));
	}

	public String prompt(String message, String inputDefault) {
		return message + ":" + inputDefault;
	}

	public void scrollTo(int x, int y) {
	}

	public void scrollBy(int x, int y) {
	}

	public void resizeTo(int width, int height) {
	}

	public void resizeBy(int byWidth, int byHeight) {
	}

	public DocumentAdapt getDocument() {
		return document;
	}

	public void setDocument(DocumentAdapt document) {
		this.document = document;
	}

	public WindowAdapt getWindow() {
		return window;
	}

	public void setWindow(WindowAdapt window) {
		this.window = window;
	}

	public WindowAdapt getSelf() {
		return self;
	}

	public void setSelf(WindowAdapt self) {
		this.self = self;
	}

	public List<?> getFrames() {
		return frames;
	}

	public void setFrames(List<?> frames) {
		this.frames = frames;
	}

	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	public String getDefaultStatus() {
		return defaultStatus;
	}

	public void setDefaultStatus(String defaultStatus) {
		this.defaultStatus = defaultStatus;
	}

	public HistoryAdapt getHistory() {
		return history;
	}

	public void setHistory(HistoryAdapt history) {
		this.history = history;
	}

	public LocationAdapt getLocation() {
		return location;
	}

	public void setLocation(LocationAdapt location) {
		this.location = location;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public NavigatorAdapt getNavigator() {
		return navigator;
	}

	public void setNavigator(NavigatorAdapt navigator) {
		this.navigator = navigator;
	}

	public WindowAdapt getOpener() {
		return opener;
	}

	public void setOpener(WindowAdapt opener) {
		this.opener = opener;
	}

	public WindowAdapt getParent() {
		return parent;
	}

	public void setParent(WindowAdapt parent) {
		this.parent = parent;
	}

	public WindowAdapt getTop() {
		return top;
	}

	public void setTop(WindowAdapt top) {
		this.top = top;
	}

	public WindowAdapt getContent() {
		return content;
	}

	public void setContent(WindowAdapt content) {
		this.content = content;
	}

	public ScreenAdapt getScreen() {
		return screen;
	}

	public void setScreen(ScreenAdapt screen) {
		this.screen = screen;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getOuterWidth() {
		return outerWidth;
	}

	public void setOuterWidth(int outerWidth) {
		this.outerWidth = outerWidth;
	}

	public int getOuterHeight() {
		return outerHeight;
	}

	public void setOuterHeight(int outerHeight) {
		this.outerHeight = outerHeight;
	}

	public int getPageXOffset() {
		return pageXOffset;
	}

	public void setPageXOffset(int pageXOffset) {
		this.pageXOffset = pageXOffset;
	}

	public int getPageYOffset() {
		return pageYOffset;
	}

	public void setPageYOffset(int pageYOffset) {
		this.pageYOffset = pageYOffset;
	}

	public int getInnerWidth() {
		return innerWidth;
	}

	public void setInnerWidth(int innerWidth) {
		this.innerWidth = innerWidth;
	}

	public int getInnerHeight() {
		return innerHeight;
	}

	public void setInnerHeight(int innerHeight) {
		this.innerHeight = innerHeight;
	}

	public int getScreenX() {
		return screenX;
	}

	public void setScreenX(int screenX) {
		this.screenX = screenX;
	}

	public int getScreenY() {
		return screenY;
	}

	public void setScreenY(int screenY) {
		this.screenY = screenY;
	}

	public int getScreenLeft() {
		return screenLeft;
	}

	public void setScreenLeft(int screenLeft) {
		this.screenLeft = screenLeft;
	}

	public int getScreenTop() {
		return screenTop;
	}

	public void setScreenTop(int screenTop) {
		this.screenTop = screenTop;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getScrollX() {
		return scrollX;
	}

	public void setScrollX(int scrollX) {
		this.scrollX = scrollX;
	}

	public int getScrollY() {
		return scrollY;
	}

	public void setScrollY(int scrollY) {
		this.scrollY = scrollY;
	}

	public int getScrollMaxX() {
		return scrollMaxX;
	}

	public void setScrollMaxX(int scrollMaxX) {
		this.scrollMaxX = scrollMaxX;
	}

	public int getScrollMaxY() {
		return scrollMaxY;
	}

	public void setScrollMaxY(int scrollMaxY) {
		this.scrollMaxY = scrollMaxY;
	}

	public boolean isFullScreen() {
		return fullScreen;
	}

	public void setFullScreen(boolean fullScreen) {
		this.fullScreen = fullScreen;
	}

	public Object getFrameElement() {
		return frameElement;
	}

	public void setFrameElement(Object frameElement) {
		this.frameElement = frameElement;
	}

	public Object getSessionStorage() {
		return sessionStorage;
	}

	public void setSessionStorage(Object sessionStorage) {
		this.sessionStorage = sessionStorage;
	}
}
