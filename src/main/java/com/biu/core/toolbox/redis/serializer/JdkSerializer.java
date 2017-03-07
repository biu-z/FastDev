/**
 * Copyright (c) 2011-2016, James Zhan 詹波 (jfinal@126.com).
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

package com.biu.core.toolbox.redis.serializer;

import com.biu.core.toolbox.kit.LogKit;
import redis.clients.util.SafeEncoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * JdkSerializer.
 */
public class JdkSerializer implements ISerializer {
	
	public static final ISerializer me = new JdkSerializer();
	
	public byte[] keyToBytes(String key) {
		return SafeEncoder.encode(key);
	}
	
	public String keyFromBytes(byte[] bytes) {
		return SafeEncoder.encode(bytes);
	}
	
	public byte[] fieldToBytes(Object field) {
		return serialize(field);
	}
	
    public Object fieldFromBytes(byte[] bytes) {
    	return deserialize(bytes);
    }
    
	public byte[] valueToBytes(Object value) {
		return serialize(value);
	}
	
	public Object valueFromBytes(byte[] bytes) {
		return deserialize(bytes);
	}
	
    public byte[] serialize(Object value) {
		if (value instanceof byte[]) {
			return (byte[]) value;
		}
		ObjectOutputStream objectOut = null;
		try {
			ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
			objectOut = new ObjectOutputStream(bytesOut);
			objectOut.writeObject(value);
			objectOut.flush();
			return bytesOut.toByteArray();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		finally {
			if(objectOut != null)
				try {objectOut.close();} catch (Exception e) {LogKit.error(e.getMessage(), e);}
		}
    }
    
    public Object deserialize(byte[] bytes) {
		if(bytes == null || bytes.length == 0)
			return null;
		
		ObjectInputStream objectInput = null;
		try {
			ByteArrayInputStream bytesInput = new ByteArrayInputStream(bytes);
			objectInput = new ObjectInputStream(bytesInput);
			return objectInput.readObject();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		finally {
			if (objectInput != null)
				try {objectInput.close();} catch (Exception e) {LogKit.error(e.getMessage(), e);}
		}
    }

	public byte[] clone(final byte[] array) {
		if (array == null) {
			return null;
		}
		return array.clone();
	}

	public byte[] mergeBytes(final byte[] array1, final byte... array2) {
		if (array1 == null) {
			return clone(array2);
		} else if (array2 == null) {
			return clone(array1);
		}
		final byte[] joinedArray = new byte[array1.length + array2.length];
		System.arraycopy(array1, 0, joinedArray, 0, array1.length);
		System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
		return joinedArray;
	}
}



