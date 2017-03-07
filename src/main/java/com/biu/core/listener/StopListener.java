/**
 * Copyright (c) 2015-2017, Chill Zhuang 庄骞 (biu@163.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.biu.core.listener;

import com.biu.core.config.BladeConfig;
import com.biu.core.plugins.PluginManager;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

/**
 * 关闭监听器
 */
@Component
public class StopListener implements ApplicationListener<ContextClosedEvent> {

	@Override
	public void onApplicationEvent(ContextClosedEvent event) {
//		if (event.getApplicationContext().getDisplayName().equals("Root WebApplicationContext")) {
		if (event.getApplicationContext().getParent() == null) {
			destroyPlugin();
			afterBladeStop();
		}
	}

	/**
	 * 插件的停用
	 */
	private void destroyPlugin() {
		PluginManager.init().stop();
	}
	
	/**   
	 * 系统关闭后执行
	*/
	private void afterBladeStop(){
		BladeConfig.getConf().afterBladeStop();
	}

}