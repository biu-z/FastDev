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
package com.biu.common.config;

import com.biu.common.intercept.DefaultSelectFactory;
import com.biu.common.plugins.GlobalPlugin;
import com.biu.core.config.IConfig;
import com.biu.core.constant.Const;
import com.biu.core.constant.Cst;
import com.biu.core.plugins.IPluginHolder;
import com.biu.core.shiro.DefaultShiroFactory;
import com.biu.core.toolbox.cache.EhcacheFactory;
import com.biu.core.toolbox.cache.ICache;
import com.biu.core.toolbox.file.DefaultFileProxyFactory;
import com.biu.core.toolbox.grid.JqGridFactory;
import com.biu.core.toolbox.kit.DateKit;
import com.biu.core.toolbox.kit.Prop;
import com.biu.core.toolbox.kit.PropKit;

import java.math.BigDecimal;

public class WebConfig implements IConfig {

	/** 
	 * 全局参数设置
	 */
	public void globalConstants(Cst me) {
		Prop prop = PropKit.use(Const.PROPERTY_FILE);
		
		//设定开发模式
		me.setDevMode(prop.getBoolean("config.devMode", false));
		
		//设定文件上传是否为远程模式
		me.setRemoteMode(prop.getBoolean("config.remoteMode", false));
		
		//远程上传地址
		me.setRemotePath(prop.get("config.remotePath", ""));
		
		//设定文件上传头文件夹
		me.setUploadPath(prop.get("config.uploadPath", "/upload"));
		
		//设定文件下载头文件夹
		me.setDownloadPath(prop.get("config.downloadPath", "/download"));
		
		//设定上传图片是否压缩
		me.setCompress(prop.getBoolean("config.compress", false));
		
		//设定上传图片压缩比例
		me.setCompressScale(prop.getBigDecimal("config.compressScale", new BigDecimal(2)));
		
		//设定上传图片缩放选择:true放大;false缩小
		me.setCompressFlag(prop.getBoolean("config.compressFlag", false));

		//设定grid工厂类
		me.setDefaultGridFactory(new JqGridFactory());
		
		//设定select工厂类
		me.setDefaultSelectFactory(new DefaultSelectFactory());
		
		//设定shiro工厂类
		me.setDefaultShiroFactory(new DefaultShiroFactory());
		
		//设定文件代理工厂类
		me.setDefaultFileProxyFactory(new DefaultFileProxyFactory());
		
		//设定缓存工厂类
		me.setDefaultCacheFactory(getDefaultCacheFactory());
	}

	/** 
	 * 自定义插件注册
	 */
	public void registerPlugins(IPluginHolder plugins) {
		plugins.register(new GlobalPlugin());
		
		
	}

	/** 
	 * 全局自定义设置
	 */
	public void globalSettings() {
		
	}

	/** 
	 * 工程启动完毕执行逻辑
	 */
	public void afterBladeStart() {
		System.out.println(DateKit.getMsTime() + "	after blade start, you can do something~~~~~~~~~~~~~~~~");
	}

	/** 
	 * 工程关闭执行逻辑
	 */
	public void afterBladeStop() {
		
	}


	/**
	 * 缓存工厂
	 */
	private ICache defaultCacheFactory = new EhcacheFactory();
	
	public ICache getDefaultCacheFactory() {
		return defaultCacheFactory;
	}

	public void setDefaultCacheFactory(ICache defaultCacheFactory) {
		this.defaultCacheFactory = defaultCacheFactory;
	}
}
