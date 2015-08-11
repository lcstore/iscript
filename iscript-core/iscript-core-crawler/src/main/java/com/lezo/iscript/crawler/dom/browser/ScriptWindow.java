package com.lezo.iscript.crawler.dom.browser;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.Timer;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventException;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import com.lezo.iscript.crawler.dom.ScriptDocument;
import com.lezo.iscript.crawler.dom.rhino.ContextUtils;

public class ScriptWindow implements EventTarget {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ScriptWindow.class);
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

	private static AtomicInteger timerIdCounter = new AtomicInteger(0);
	private ConcurrentHashMap<Integer, TimerWrapper> timerMap = new ConcurrentHashMap<Integer, TimerWrapper>();

	private ScriptableObject scope;

	public ScriptWindow() {
		this(null);
	}

	public ScriptWindow(final ScriptableObject parent) {
		super();
		this.navigator = new ScriptNavigator();
		this.screen = new ScriptScreen();
		initContext(parent);
	}

	private void initContext(final ScriptableObject parent) {
		final ScriptWindow target = this;
		final ScriptNavigator navigator = this.navigator;
		final ScriptScreen screen = this.screen;
		ContextUtils.doAction(new ContextAction() {
			@Override
			public Object run(Context cx) {
				Scriptable scope = cx.initStandardObjects(parent);

				// set window to top Scriptable
				Scriptable scriptable = Context.toObject(target, scope);
				scriptable.setParentScope(null);
				scriptable.setPrototype(scope);
				scope.setParentScope(scriptable);

				ScriptableObject.putProperty(scope, "navigator", Context.toObject(navigator, scope));
				ScriptableObject.putProperty(scope, "screen", Context.toObject(screen, scope));
				ScriptableObject.putProperty(scope, "window", scriptable);

				ScriptableObject.putProperty(scope, "Element", Element.class);
				setScope((ScriptableObject) scope);
				return scope;
			}
		});
	}

	public int setInterval(final Function aFunction, final double aTimeInMs) {
		if (aTimeInMs > Integer.MAX_VALUE || aTimeInMs < 0) {
			throw new IllegalArgumentException("Timeout value " + aTimeInMs + " is not supported.");
		}
		final int timerId = generateTimerID();
		ActionListener task = new FunctionActionListener(this, aFunction, timerId, false);
		int t = (int) aTimeInMs;
		if (t < 1) {
			t = 1;
		}
		Timer timer = new Timer(t, task);
		timer.setRepeats(true); // The only difference with setTimeout
		this.putAndStartTask(timerId, timer, aFunction);
		return timerId;
	}

	public int setInterval(final String aExpression, double aTimeInMs) {
		if (aTimeInMs > Integer.MAX_VALUE || aTimeInMs < 0) {
			throw new IllegalArgumentException("Timeout value " + aTimeInMs + " is not supported.");
		}
		final int timeId = generateTimerID();
		ActionListener task = new ExpressActionListener(this, aExpression, timeId, false);
		int t = (int) aTimeInMs;
		if (t < 1) {
			t = 1;
		}
		Timer timer = new Timer(t, task);
		timer.setRepeats(true); // The only difference with setTimeout
		return timeId;
	}

	private void putAndStartTask(Integer timerId, Timer timer, Object retained) {
		TimerWrapper oldTimer = this.timerMap.put(timerId, new TimerWrapper(timerId, timer, retained));
		if (oldTimer != null) {
			oldTimer.getTimer().stop();
		}
		timer.start();
	}

	private void forgetTask(Integer timerId, boolean cancel) {
		TimerWrapper oldTimer = timerMap.remove(timerId);
		if (oldTimer != null && cancel) {
			oldTimer.getTimer().stop();
		}
	}

	private class FunctionActionListener implements ActionListener {
		private ScriptWindow window;
		private WeakReference<Function> functionRef;
		private int timerId;
		private boolean remove;

		public FunctionActionListener(ScriptWindow window, Function function, int timerId, boolean remove) {
			super();
			this.window = window;
			this.functionRef = new WeakReference<Function>(function);
			this.timerId = timerId;
			this.remove = remove;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if (this.window == null) {
					if (logger.isInfoEnabled()) {
						logger.info("actionPerformed(): Window is no longer available.");
					}
					return;
				}
				if (this.remove) {
					window.forgetTask(this.timerId, false);
				}
				Document doc = this.window.getDocument();
				if (doc == null) {
					throw new IllegalStateException("Cannot perform operation when document is unset.");
				}
				Function function = (Function) this.functionRef.get();
				if (function == null) {
					throw new IllegalStateException("Cannot perform operation. Function is no longer available.");
				}
				ContextUtils.call(getScope(), getScope(), function, new Object[0]);
			} catch (Exception err) {
				logger.warn("timerId:" + this.timerId + ",cause:", err);
			}
		}
	}

	private class ExpressActionListener implements ActionListener {
		private ScriptWindow window;
		private String expression;
		private int timerId;
		private boolean remove;

		public ExpressActionListener(ScriptWindow window, String expression, int timerId, boolean remove) {
			super();
			this.window = window;
			this.expression = expression;
			this.timerId = timerId;
			this.remove = remove;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if (this.window == null) {
					if (logger.isInfoEnabled()) {
						logger.info("actionPerformed(): Window is no longer available.");
					}
					return;
				}
				if (this.remove) {
					window.forgetTask(this.timerId, false);
				}
				Document doc = this.window.getDocument();
				if (doc == null) {
					throw new IllegalStateException("Cannot perform operation when document is unset.");
				}
				window.eval(this.expression);
			} catch (Exception err) {
				logger.warn("timeId:" + this.timerId + ",express:" + this.expression, err);
			}
		}

	}

	private int generateTimerID() {
		return timerIdCounter.incrementAndGet();
	}

	public void clearInterval(int timerId) {
		this.forgetTask(timerId, true);
	}

	public void alert(String message) {
		logger.info("alert:" + message);
		System.out.println("alert:" + message);
	}

	public void back() {
	}

	public void blur() {
	}

	public int setTimeout(final String aExpression, double aTimeInMs) {
		if (aTimeInMs > Integer.MAX_VALUE || aTimeInMs < 0) {
			throw new IllegalArgumentException("Timeout value " + aTimeInMs + " is not supported.");
		}
		final int timerId = generateTimerID();
		ActionListener task = new ExpressActionListener(this, aExpression, timerId, true);
		int t = (int) aTimeInMs;
		if (t < 1) {
			t = 1;
		}
		Timer timer = new Timer(t, task);
		timer.setRepeats(false); // The only difference with setInterval
		this.putAndStartTask(timerId, timer, aExpression);
		return timerId;
	}

	public int setTimeout(final Function aFunction, double aTimeInMs) {
		if (aTimeInMs > Integer.MAX_VALUE || aTimeInMs < 0) {
			throw new IllegalArgumentException("Timeout value " + aTimeInMs + " is not supported.");
		}
		final int timerId = generateTimerID();
		ActionListener task = new FunctionActionListener(this, aFunction, timerId, true);
		int t = (int) (aTimeInMs < 1 ? 1 : aTimeInMs);
		Timer timer = new Timer(t, task);
		timer.setRepeats(false); // The only difference with setInterval
		this.putAndStartTask(timerId, timer, aFunction);
		return timerId;
	}

	public void clearTimeout(int timerId) {
		this.forgetTask(timerId, true);
	}

	public void close() {
	}

	public boolean confirm(String message) {
		return false;
	}

	public Object eval(String source) {
		return ContextUtils.call(getScope(), source, "window.eval");
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

	public void setLocation(String location) {
		ScriptDocument scriptDocument = (ScriptDocument) getDocument();
		scriptDocument.setLocation(location);
	}

	public Document getDocument() {
		return document;
	}

	public synchronized void setDocument(Document document) {
		this.document = document;
		if (document instanceof ScriptDocument) {
			final ScriptDocument scriptDocument = (ScriptDocument) document;
			final ScriptWindow target = this;
			ContextUtils.doAction(new ContextAction() {
				@Override
				public Object run(Context cx) {
					Scriptable scope = target.getScope();
					ScriptableObject.putProperty(scope, "document", Context.toObject(scriptDocument, scope));
					ScriptableObject.putProperty(scope, "location",
							Context.toObject(scriptDocument.getLocation(), scope));
					return scope;
				}
			});
		}
	}

	@SuppressWarnings("unused")
	private class TimerWrapper {
		private int timerId;
		private Timer timer;
		private Object retained;

		public TimerWrapper(int timerId, Timer timer, Object retained) {
			super();
			this.timerId = timerId;
			this.timer = timer;
			this.retained = retained;
		}

		public Timer getTimer() {
			return timer;
		}
	}

	public ScriptableObject getScope() {
		return scope;
	}

	public void setScope(ScriptableObject scope) {
		this.scope = scope;
	}

	public String getClassName() {
		return "ScriptWindow";
	}

}
