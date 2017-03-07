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
import com.biu.core.constant.Cst;
import com.biu.core.plugins.IPluginHolder;
import com.biu.core.plugins.PluginFactory;
import com.biu.core.plugins.PluginManager;
import com.biu.core.plugins.connection.RedisPlugin;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * 启动监听器
 */
@Component
public class StartupListener implements ApplicationListener<ContextRefreshedEvent> {

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
//		if (event.getApplicationContext().getDisplayName().equals("Root WebApplicationContext")) {
		if (event.getApplicationContext().getParent() == null) {
			globalConstants(Cst.me());
			registerPlugins();
			globalSettings();
			afterBladeStart();
		}
	}
	
	/**   
	 * 全局配置
	*/
	private void globalConstants(Cst me){
		BladeConfig.getConf().globalConstants(me);
	}

	/**
	 * 插件的启用
	 */
	private void registerPlugins() {
		IPluginHolder plugins = PluginFactory.init();
//		plugins.register(SQLManagerPlugin.init());
		plugins.register(RedisPlugin.init());
		BladeConfig.getConf().registerPlugins(plugins);//自定义配置插件	
		PluginManager.init().start();
	}
	
	/**   
	 * 全局配置
	*/
	private void globalSettings(){
		BladeConfig.getConf().globalSettings();
	}
	
	/**   
	 * 系统启动后执行
	*/
	private void afterBladeStart(){
		BladeConfig.getConf().afterBladeStart();
	}
	
}