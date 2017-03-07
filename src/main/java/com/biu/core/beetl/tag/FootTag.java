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
package com.biu.core.beetl.tag;

import com.biu.core.toolbox.Func;
import com.biu.core.toolbox.kit.DateKit;
import org.beetl.core.Tag;

import java.io.IOException;
import java.util.Map;

public class FootTag extends Tag {

	public static String company = "biu@163.com";
	public static String customer = "biu";
	
	@Override
	@SuppressWarnings("unchecked")
	public void render() {
		try {
			if(args.length > 1){
				Map<String, String> param = (Map<String, String>) args[1];
			    company = (Func.isEmpty(param.get("tonbusoft"))) ? company : param.get("tonbusoft");
			    customer = (Func.isEmpty(param.get("customer"))) ? customer : param.get("customer");
			}
			String year = DateKit.getYear();

			StringBuilder sb = new StringBuilder();
			
			sb.append("<div class=\"footer\">");
			sb.append("	<div class=\"footer-inner\">");
			sb.append("		<div class=\"footer-content\" style=\"height:30px;background-color:#fbfbfb;\">");
			sb.append("			<span class=\"bigger-110\">技术支持 :</span>");
			sb.append("			<span class=\"bigger-110\" id=\"support_tonbusoft\">" + company + "</span>");
			sb.append("			<span class=\"bigger-110\"  style=\"padding-left:15px;\">");
			sb.append("				© " + year);
			sb.append("			</span>");
			sb.append("		</div>");
			sb.append("	</div>");
			sb.append("</div>");
			sb.append("<a href=\"#\" id=\"btn-scroll-up\" class=\"btn-scroll-up btn btn-sm btn-inverse\">");
			sb.append(" <i class=\"ace-icon fa fa-angle-double-up icon-only bigger-110\">");
			sb.append("  顶部");
			sb.append(" </i>");
			sb.append("</a>");
			
			
			/*sb.append("<div class=\"footer\">");
			sb.append(" <div class=\"footer-inner\">");
			sb.append("  <div class=\"footer-content\" style=\"padding-bottom:15px;\">");
			sb.append("   <span class=\"bigger-120\">");
			sb.append("    <span class=\"blue bolder\">" + customer + "</span>");
			sb.append("     &nbsp;copyright &copy; "+year);
			sb.append("   </span>");
			sb.append("&nbsp; &nbsp;");
			sb.append("  </div>");
			sb.append("  <div style=\"padding-bottom:1px;\">");
			sb.append("   <span class=\"bigger-120\">");
			sb.append("    <span style=\"font-size:4px;\">技术支持：</span>");
			sb.append("    <span style=\"font-size:4px;\" id=\"support_tonbusoft\">" + company + "</span>");
			sb.append("   </span>");
			sb.append("  </div>");
			sb.append(" </div>");
			sb.append("</div>");
			sb.append("<a href=\"#\" id=\"btn-scroll-up\" class=\"btn-scroll-up btn btn-sm btn-inverse\">");
			sb.append(" <i class=\"ace-icon fa fa-angle-double-up icon-only bigger-110\">");
			sb.append("  顶部");
			sb.append(" </i>");
			sb.append("</a>");*/
			ctx.byteWriter.writeString(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
