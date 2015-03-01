package com.lezo.iscript.scope;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lezo
 * @email lcstore@126.com
 * @since 2014年10月14日
 */
public class ScriptableUtils {
	private static Logger logger = LoggerFactory.getLogger(ScriptableUtils.class);

	private static class QueryInstance {
		private static Scriptable instance;
		static {
			try {
				instance = new ResourceScriptableFactory(null, "script/core/iQuery.js").createScriptable();
			} catch (Exception e) {
				logger.warn("creat query scriptable.cause:", e);
			}
		}
	}

	public static Scriptable getQueryScriptable() {
		return QueryInstance.instance;
	}

	private static class JSONInstance {
		private static Scriptable instacne;
		static {
			try {
				instacne = new ResourceScriptableFactory(null, "script/core/json2.js").createScriptable();
			} catch (Exception e) {
				logger.warn("creat json scriptable.cause:", e);
			}
		}
	}

	public static Scriptable getJSONScriptable() {
		return JSONInstance.instacne;
	}

	private static class CoreInstance {
		private static Scriptable instacne;
		static {
			try {
				instacne = new ResourceScriptableFactory((ScriptableObject) getJSONScriptable(), "script/core/core.js", "script/core/iQuery.js").createScriptable();
			} catch (Exception e) {
				logger.warn("creat json scriptable.cause:", e);
			}
		}
	}

	public static Scriptable getCoreScriptable() {
		return CoreInstance.instacne;
	}
}
