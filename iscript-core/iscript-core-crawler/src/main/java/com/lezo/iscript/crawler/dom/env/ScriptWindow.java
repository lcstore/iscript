package com.lezo.iscript.crawler.dom.env;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.w3c.dom.Document;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventException;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import com.lezo.iscript.crawler.dom.ScriptDocument;
import com.lezo.iscript.crawler.dom.ScriptElement;
import com.lezo.iscript.envjs.EnvjsUtils;

public class ScriptWindow implements EventTarget {
	private static final Logger logger = Logger.getLogger(ScriptWindow.class.getName());
	private List<?> frames = new ArrayList<Object>();
	private String name = "";
	private boolean closed = false;
	private String defaultStatus = "";
	private ScriptWindow window = this;
	private ScriptWindow self = this;
	private ScriptWindow opener = this;
	private ScriptWindow parent = null;
	private ScriptWindow top = this;
	private ScriptWindow content = this;
	private String status = "";
	private int outerWidth = 1366;
	private int outerHeight = 728;
	private int pageXOffset = 0;
	private int pageYOffset = 0;
	private int innerWidth = 1366;
	private int innerHeight = 147;
	private int screenX = 0;
	private int screenY = 0;
	private int screenLeft = 0;
	private int screenTop = 0;
	private int length = 1;
	private int scrollX = 0;
	private int scrollY = 0;
	private int scrollMaxX = 0;
	private int scrollMaxY = 0;
	private boolean fullScreen = false;
	private Object frameElement;
	private Object sessionStorage;

	private ScriptNavigator navigator;
	private ScriptScreen screen;
	private volatile Document document;
	private Scriptable scriptable;

	public ScriptWindow() {
		this(null);
	}

	public ScriptWindow(ScriptableObject paren) {
		Context cx = enterContext();
		this.scriptable = cx.initStandardObjects(paren);
		Context.exit();
		this.navigator = new ScriptNavigator();
		this.screen = new ScriptScreen();
		// defineClass();

		ScriptableObject.putProperty(getScriptable(), "screen", this.screen);
		ScriptableObject.putProperty(getScriptable(), "navigator", this.navigator);
	}

	private void defineClass() {
		try {
			// ScriptableObject.defineClass(getScriptable(),
			// ScriptElement.class);
			// ScriptableObject.defineClass(getScriptable(),
			// ScriptDocument.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

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
		// Context ctx = Context.enter();
		Context ctx = enterContext();
		try {
			Scriptable scope = getScriptable();
			if (scope == null) {
				throw new IllegalStateException("Scriptable (scope) instance was not init");
			}
			String scriptURI = "window.eval";
			// if (logger.isLoggable(Level.INFO)) {
			// logger.info("eval(): javascript follows...\r\n" + javascript);
			// }
			return ctx.evaluateString(scope, javascript, scriptURI, 0, null);
		} finally {
			Context.exit();
		}
	}

	public void focus() {
	}

	public ScriptWindow open(String relativeUrl, String windowName, String windowFeatures, boolean replace) {
		return null;
	}

	public ScriptWindow open(String url) {
		return this.open(url, "window:9999");
	}

	public ScriptWindow open(String url, String windowName) {
		return this.open(url, windowName, "", false);
	}

	public ScriptWindow open(String url, String windowName, String windowFeatures) {
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

	public ScriptWindow getWindow() {
		return window;
	}

	public void setWindow(ScriptWindow window) {
		this.window = window;
	}

	public ScriptWindow getSelf() {
		return self;
	}

	public void setSelf(ScriptWindow self) {
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ScriptWindow getOpener() {
		return opener;
	}

	public void setOpener(ScriptWindow opener) {
		this.opener = opener;
	}

	public ScriptWindow getParent() {
		return parent;
	}

	public void setParent(ScriptWindow parent) {
		this.parent = parent;
	}

	public ScriptWindow getTop() {
		return top;
	}

	public void setTop(ScriptWindow top) {
		this.top = top;
	}

	public ScriptWindow getContent() {
		return content;
	}

	public void setContent(ScriptWindow content) {
		this.content = content;
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

	@Override
	public void addEventListener(String type, EventListener listener, boolean useCapture) {
		// document.addEventListener(type, listener, useCapture);
	}

	@Override
	public void removeEventListener(String type, EventListener listener, boolean useCapture) {
		// document.removeEventListener(type, listener, useCapture);
	}

	@Override
	public boolean dispatchEvent(Event event) throws EventException {
		// return document.dispatchEvent(event);
		return true;
	}

	public ScriptNavigator getNavigator() {
		return navigator;
	}

	public void setNavigator(ScriptNavigator navigator) {
		this.navigator = navigator;
	}

	public ScriptScreen getScreen() {
		return screen;
	}

	public void setScreen(ScriptScreen screen) {
		this.screen = screen;
	}

	public ScriptLocation getLocation() {
		ScriptDocument scriptDocument = (ScriptDocument) getDocument();
		return scriptDocument.getLocation();
	}

	public void setLocation(ScriptLocation location) {
		ScriptDocument scriptDocument = (ScriptDocument) getDocument();
		scriptDocument.setLocation(location);
	}

	public Document getDocument() {
		return document;
	}

	public synchronized void setDocument(Document document) {
		this.document = document;
		if (document instanceof ScriptDocument) {
			ScriptDocument scriptDocument = (ScriptDocument) document;
			ScriptableObject.putProperty(getScriptable(), "location", scriptDocument.getLocation());
		}
		ScriptableObject.putProperty(getScriptable(), "document", this.document);
	}

	public Scriptable getScriptable() {
		return scriptable;
	}

	public void setScriptable(Scriptable scriptable) {
		this.scriptable = scriptable;
	}

	public static Context enterContext() {
		return ScriptContextFactory.getGlobal().enterContext();
	}
}
