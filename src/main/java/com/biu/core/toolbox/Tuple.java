package com.biu.core.toolbox;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 类似于swift的元组，用于多值返回
 * 
 * @author Looly
 * 
 */
@SuppressWarnings("serial")
public class Tuple implements Cloneable, Serializable {

	private Object[] members;
	
	public Tuple(Object... members) {
		this.members = members;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(int index){
		return (T) members[index];
	}
	
	@Override
	public String toString() {
		return Arrays.toString(members);
	}
	
}
