package com.biu.common.task;

import com.biu.core.toolbox.kit.DateKit;

public class GlobalTask implements Runnable {

	@Override
	public void run() {
		
		System.out.println("任务调度执行:" + DateKit.getTime());
	}

}
