package com.lezo.iscript.yeam;

public class ClientConstant {
	/**
	 * 获取任务
	 */
	public static final int GET_TASK = 0;
	/**
	 * 获取客户端
	 */
	public static final int GET_CLIENT = 1;
	/**
	 * 获取配置
	 */
	public static final int GET_CONFIG = 2;

	public static final int GET_NONE = 3;

	public static final int MAX_TASK_QUEUE_CAPACITY = 20;
	public static final int MAX_RESULT_QUEUE_CAPACITY = 5;
	public static final int MAX_TASK_BUFFER_SIZE = MAX_TASK_QUEUE_CAPACITY / 2;

	public static final int FETCH_INTERVAL_TIME = 10 * 1000;
	public static final int EXECUTE_INTERVAL_TIME = 5 * 1000;
	public static final int SUBMIT_INTERVAL_TIME = 10 * 1000;

	public static final String CLIENT_CHARSET = "UTF-8";
	public static final String CMD_CHARSET = "GBK";

	public static final String CLIENT_ENV_HEAD = "yeam_";
	public static final String CLIENT_TASKER_HOST = CLIENT_ENV_HEAD+"tasker_host";
	public static final String CLIENT_RESULTER_HOST = CLIENT_ENV_HEAD+"resulter_host";
	public static final String CLIENT_DEFEND_TASKER = CLIENT_ENV_HEAD+"defend_tasker";

	public static final String CLIENT_CONFIG_STAMP = CLIENT_ENV_HEAD+"cofing_stamp";

	public static final String CLIENT_PATH = CLIENT_ENV_HEAD+"client_path";
	public static final String CLIENT_UPDATE_SPACE = "updatespace";
	public static final String CLIENT_WORK_SPACE = "workspace";
	public static final String CLIENT_NAME = CLIENT_ENV_HEAD+"name";
	public static final String CLIENT_VERSION = CLIENT_ENV_HEAD+"version";
}
