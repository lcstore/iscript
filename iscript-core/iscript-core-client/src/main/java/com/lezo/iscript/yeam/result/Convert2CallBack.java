package com.lezo.iscript.yeam.result;

import java.util.ArrayList;
import java.util.List;

import com.lezo.iscript.yeam.config.ConfigParserBuffer;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.ResultWritable;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class Convert2CallBack implements Convertable<List<ResultWritable>> {

	public List<ResultWritable> doConvert(List<ResultWritable> rsList) {
		List<ResultWritable> successList = new ArrayList<ResultWritable>();
		List<ResultWritable> errorList = new ArrayList<ResultWritable>();
		// assort into success & error
		doAssort(rsList, successList, errorList);
		List<ResultWritable> hasNextTaskList = new ArrayList<ResultWritable>();
		List<ResultWritable> otherList = new ArrayList<ResultWritable>();
		// get has next task
		convertToRetain(successList, hasNextTaskList, otherList);
		List<ResultWritable> toSendList = new ArrayList<ResultWritable>(errorList);
		toSendList.addAll(hasNextTaskList);
		toSendList.addAll(otherList);
		return toSendList;
	}

	private void doAssort(List<ResultWritable> rsList, List<ResultWritable> successList, List<ResultWritable> errorList) {
		for (ResultWritable rs : rsList) {
			if (ResultWritable.RESULT_SUCCESS == rs.getStatus()) {
				successList.add(rs);
			} else {
				errorList.add(rs);
			}
		}
	}

	private void convertToRetain(List<ResultWritable> successList, List<ResultWritable> retainList,
			List<ResultWritable> otherList) {
		TaskWritable taskWritable = new TaskWritable();
		ConfigParser calllBackDecider = ConfigParserBuffer.getInstance().getParser("CalllBackDecider");
		for (ResultWritable rw : successList) {
			if (calllBackDecider == null) {
				otherList.add(rw);
			} else {
				String retain = null;
				try {
					taskWritable.put("ResultWritable", rw);
					retain = calllBackDecider.doParse(taskWritable);
				} catch (Exception e) {
					e.printStackTrace();
					otherList.add(rw);
				}
				if (retain != null) {
					ResultWritable newWritable = new ResultWritable();
					newWritable.setStatus(rw.getStatus());
					newWritable.setTaskId(rw.getTaskId());
					newWritable.setType(rw.getType());
					newWritable.setResult(retain);
					retainList.add(newWritable);
				}
			}
		}
	}
}
