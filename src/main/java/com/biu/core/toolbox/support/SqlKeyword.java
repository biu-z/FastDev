package com.biu.core.toolbox.support;

import com.biu.core.toolbox.Func;
import com.biu.core.toolbox.kit.JsonKit;
import com.biu.core.toolbox.kit.StrKit;

import java.util.HashMap;
import java.util.Map;

/**
 * 定义常用的 sql关键字
 */
public class SqlKeyword {
	private static final String EQUAL = "_equal";
	private static final String NOT_EQUAL = "_notequal";
	private static final String LIKE = "_like";
	private static final String NOT_LIKE = "_notlike";
	private static final String GT = "_gt";
	private static final String LT = "_lt";
	private static final String DATE_GT = "_dategt";
	private static final String DATE_LT = "_datelt";
	private static final String OR = "or_";
	private static final String AND = "and_";
	private static final String SECOND = "_2nd";
	private static final String IS_NULL = "_null";
	private static final String NOT_NULL = "_notnull";
	public static final String TOINT = "toint_";
	public static final String IT = "it_";
	public static final String F_IT = "f_it_";
	private static HashMap<String, String> keyWord = new HashMap<String, String>();
	static {
		keyWord.put(EQUAL, " = ? ");
		keyWord.put(NOT_EQUAL, " <> ? ");
		keyWord.put(LIKE, " like ? ");
		keyWord.put(NOT_LIKE, " not like ? ");
		keyWord.put(GT, " > ? ");
		keyWord.put(LT, " < ? ");
		keyWord.put(DATE_GT, " > ? ");
		keyWord.put(DATE_LT, " < ? ");
		keyWord.put(IS_NULL, " is null ");
		keyWord.put(NOT_NULL, " is not null ");
	}

	/**
	 * 根据前台json格式转化成map进行遍历
	 * 
	 * @param w
	 * @return String
	 */
	@SuppressWarnings("unchecked")
	public static String getWhere(String w) {
		try {
			StringBuilder where = new StringBuilder(" where 1=1 ");
			StringBuilder or = new StringBuilder();
			StringBuilder and = new StringBuilder();
			if (StrKit.notBlank(w)) {
				w = Func.decodeUrl(w);
				Map<String, String> mm = JsonKit.parse(w, HashMap.class);
				for (String m : mm.keySet()) {
					String col = clearKeyWord(m);
					String k = "";
					for (String key : keyWord.keySet()) {
						if (m.indexOf(key) >= 0) {
							k = key;
							break;
						}
					}
					if (StrKit.isBlank(k)) {
						k = LIKE;
					}
					String filter = col + getKeyWord(k, m);
					if (m.indexOf(OR) == -1) {
						and.append(" and (" + filter + ")");
					} else {
						or.append(" or (" + filter + ")");
					}
				}
				where.append(and.toString()).append(or.toString());
			}
			return where.toString();
		} catch (Exception ex) {
			return " where 1=1 ";
		}
	}

	public static String clearKeyWord(String key) {
		for (String k : keyWord.keySet()) {
			key = key.replace(k, "");
		}
		key = key.replace(OR, "").replace(AND, "").replace(SECOND, "").replace(TOINT, "");
		return key;
	}

	public static String getKeyWord(String key, String value) {
		String _keyWord = keyWord.get(key);
		if (key.equals(DATE_GT) || key.equals(DATE_LT)) {
			return _keyWord.replace("?", value);
		}
		if(key.indexOf("like") > 0){
			return _keyWord.replace("?", " CONCAT('%', #{" + value + "},'%')  ");
		}
		return _keyWord.replace("?", "#{" + value + "}");
	}
	
}
