package com.lezo.iscript.yeam.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.ClientConstant;
import com.lezo.iscript.yeam.config.ConfigParserBuffer;
import com.lezo.iscript.yeam.file.PersistentCollector;
import com.lezo.iscript.yeam.mina.utils.HeaderUtils;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.ResultWritable;
import com.lezo.iscript.yeam.writable.TaskWritable;
import com.lezo.rest.data.ClientRest;
import com.lezo.rest.data.ClientRestFactory;

public class TaskWorker implements Callable<ResultWritable> {
	private static Logger logger = LoggerFactory.getLogger(TaskWorker.class);
	private TaskWritable task;

	public TaskWorker(TaskWritable task) {
		super();
		this.task = task;
	}

	@Override
	public ResultWritable call() throws Exception {
		long start = System.currentTimeMillis();
		ResultWritable rsWritable = new ResultWritable();
		rsWritable.setTaskId(task.getId());
		JSONObject callBackObject = new JSONObject();
		JSONObject argsObject = new JSONObject(task.getArgs());
		JSONUtils.put(argsObject, "name@client", HeaderUtils.CLIENT_NAME);
		String type = null;
		try {
			Object typeObject = task.get("type");
			if (typeObject == null) {
				throw new IllegalArgumentException("No type for id:" + task.getId() + ",args:" + argsObject);
			}
			type = typeObject.toString();
			rsWritable.setType(type);
			// argsObject.remove("type");
			ConfigParser parser = ConfigParserBuffer.getInstance().getParser(type);
			if (parser == null) {
				throw new IllegalArgumentException("No config for type:" + type + ",id:" + task.getId() + ",args:" + argsObject);
			}
			String sReturn = parser.doParse(task);
			rsWritable.setStatus(ResultWritable.RESULT_SUCCESS);
			JSONObject returnObject = JSONUtils.getJSONObject(sReturn);
			JSONObject storageDataObject = JSONUtils.getJSONObject(returnObject, ClientConstant.KEY_STORAGE_RESULT);
			if (storageDataObject != null) {
				JSONObject storageObject = new JSONObject();
				JSONUtils.put(storageObject, "args", argsObject);
				JSONUtils.put(storageObject, "rs", storageDataObject);
				List<JSONObject> dataList = new ArrayList<JSONObject>(1);
				dataList.add(storageObject);
//				QiniuBucketMac bucketMac = QiniuBucketMacFactory.getRandomBucketMac();
				ClientRest clientRest = ClientRestFactory.getInstance().getRandom();
				JSONUtils.put(argsObject, "data_bucket", clientRest.getBucket());
				JSONUtils.put(argsObject, "data_domain", clientRest.getDomain());
				PersistentCollector.getInstance().getBufferWriter().write(dataList);
			}
			JSONObject callBackDataObject = JSONUtils.getJSONObject(returnObject, ClientConstant.KEY_CALLBACK_RESULT);
			if (callBackDataObject != null) {
				JSONUtils.put(callBackObject, "rs", callBackDataObject);
			} else {
				JSONUtils.put(callBackObject, "rs", sReturn);
			}
		} catch (Exception e) {
			rsWritable.setStatus(ResultWritable.RESULT_FAIL);
			String strStack = ExceptionUtils.getStackTrace(e);
			JSONObject exObject = new JSONObject();
			JSONUtils.put(exObject, "name", e.getClass().getName());
			JSONUtils.put(exObject, "stack", ExceptionUtils.getStackTrace(e));
			JSONUtils.put(callBackObject, "ex", exObject);
			logger.warn(strStack);
		} finally {
			JSONUtils.put(callBackObject, "args", argsObject);
			rsWritable.setResult(callBackObject.toString());
			long cost = System.currentTimeMillis() - start;
			String msg = String.format("done task:%d,type:%s,status:%d,cost:%d", task.getId(), type, rsWritable.getStatus(), cost);
			logger.info(msg);
		}
		return rsWritable;
	}
}
