package com.lezo.iscript.yeam.mina.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.yeam.io.IoConstant;
import com.lezo.iscript.yeam.io.IoRespone;
import com.lezo.iscript.yeam.storage.ResultFutureStorager;
import com.lezo.iscript.yeam.task.TaskWorker;
import com.lezo.iscript.yeam.task.TasksCaller;
import com.lezo.iscript.yeam.writable.ResultWritable;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class TaskIoFilter extends IoFilterAdapter {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(TaskIoFilter.class);

    @Override
    public void messageReceived(NextFilter nextFilter, IoSession session, Object message) throws Exception {
        if (message instanceof IoRespone) {
            IoRespone ioRespone = (IoRespone) message;
            if (IoConstant.EVENT_TYPE_TASK == ioRespone.getType()) {
                addTasks(ioRespone);
                return;
            }
        }
        nextFilter.messageReceived(session, message);
    }

    private void addTasks(IoRespone ioRespone) {
        List<TaskWritable> taskList = getTaskList(ioRespone);
        if (CollectionUtils.isEmpty(taskList)) {
            return;
        }
        // keep ConfigResponeWorker working in the line
        taskList = sunderTasks(taskList);
        ThreadPoolExecutor caller = TasksCaller.getInstance().getCaller();
        ResultFutureStorager storager = ResultFutureStorager.getInstance();
        for (TaskWritable taskWritable : taskList) {
            Future<ResultWritable> future = caller.submit(new TaskWorker(taskWritable));
            storager.getStorageBuffer().add(future);
        }
        String msg = String.format("Get task:%d,Queue:%d,working:%d", taskList.size(), caller.getQueue().size(),
                caller.getActiveCount());
        logger.info(msg);
    }

    /**
     * 同类的任务分开
     * 
     * @param taskList
     * @return
     */
    private List<TaskWritable> sunderTasks(List<TaskWritable> taskList) {
        int size = taskList.size();
        for (int i = 1; i < size; i++) {
            String typeLeft = (String) taskList.get(i - 1).get("type");
            int maxDiff = 0;
            int curIndex = i;
            for (int j = i; j < size; j++) {
                String type = (String) taskList.get(j).get("type");
                int ld = StringUtils.getLevenshteinDistance(typeLeft, type);
                if (ld > maxDiff) {
                    curIndex = j;
                    maxDiff = ld;
                }
            }
            if (i != curIndex) {
                TaskWritable task = taskList.get(i);
                TaskWritable difTask = taskList.get(curIndex);
                taskList.set(i, difTask);
                taskList.set(curIndex, task);
            }

        }
        return taskList;

    }

    // 计算两个字符串的差异值
    public static int getLevenshteinDistance(CharSequence s, CharSequence t) {
        if (s == null || t == null) {
            // 容错，抛出的这个异常是表明在传参的时候，传递了一个不合法或不正确的参数。
            // 好像都这样用，illegal:非法。Argument:参数，证据。
            throw new IllegalArgumentException("Strings must not be null");
        }
        // 计算传入的两个字符串长度
        int n = s.length();
        int m = t.length();
        // 容错，直接返回结果。这个处理不错
        if (n == 0) {
            return m;
        } else if (m == 0) {
            return n;
        }
        // 这一步是根据字符串长短处理，处理后t为长字符串，s为短字符串，方便后面处理
        if (n > m) {
            CharSequence tmp = s;
            s = t;
            t = tmp;
            n = m;
            m = t.length();
        }

        // 开辟一个字符数组，这个n是短字符串的长度
        int p[] = new int[n + 1];
        int d[] = new int[n + 1];
        // 用于交换p和d的数组
        int _d[];

        int i;
        int j;
        char t_j;
        int cost;
        // 赋初值
        for (i = 0; i <= n; i++) {
            p[i] = i;
        }

        for (j = 1; j <= m; j++) {
            // t是字符串长的那个字符
            t_j = t.charAt(j - 1);
            d[0] = j;

            for (i = 1; i <= n; i++) {
                // 计算两个字符是否一样，一样返回0。
                cost = s.charAt(i - 1) == t_j ? 0 : 1;
                // 可以将d的字符数组全部赋值。
                d[i] = Math.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1] + cost);
            }

            // 交换p和d
            _d = p;
            p = d;
            d = _d;
        }

        // 最后的一个值即为差异值
        return p[n];
    }

    @SuppressWarnings("unchecked")
    private List<TaskWritable> getTaskList(IoRespone ioRespone) {
        List<TaskWritable> configList = new ArrayList<TaskWritable>();
        try {
            Object dataObject = ioRespone.getData();
            if (dataObject instanceof TaskWritable) {
                TaskWritable TaskWritable = (TaskWritable) dataObject;
                configList.add(TaskWritable);
            } else if (dataObject instanceof List) {
                configList = (List<TaskWritable>) dataObject;
            }
        } catch (Exception e) {
            logger.warn("can not cast data to config.", e);
        }
        return configList;
    }

}
