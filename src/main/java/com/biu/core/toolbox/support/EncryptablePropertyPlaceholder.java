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
package com.biu.core.toolbox.support;

import com.biu.core.toolbox.kit.AESKit;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.util.Properties;

/**
 * spring参数加密配置
 */
public class EncryptablePropertyPlaceholder extends PropertyPlaceholderConfigurer {

	protected void processProperties(ConfigurableListableBeanFactory beanFactory, Properties props) throws BeansException {
		try {
			for (Object key : props.keySet()) {
				if (Convert.toStr(key).contains("encrypt")) {
					String value = props.getProperty(Convert.toStr(key));
					if (null != value) {
						try {
							String desryptValue = AESKit.decrypt(value);
							props.setProperty(Convert.toStr(key), desryptValue);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			super.processProperties(beanFactory, props);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BeanInitializationException(e.getMessage());
		}
	}

}
