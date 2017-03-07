/**
 * Copyright (c) 2011-2015, James Zhan 詹波 (jfinal@126.com).
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
package com.biu.core.toolbox.file;

import com.biu.core.constant.Cst;
import com.biu.core.toolbox.kit.LogKit;
import com.biu.core.toolbox.kit.StrKit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class FileMaker {
	private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";
	
	private File file;
	
	private HttpServletRequest request;
	
	private HttpServletResponse response;
	
	public static FileMaker init(HttpServletRequest request, HttpServletResponse response, File file) {
		return new FileMaker(request, response, file);
	}
	
	private FileMaker(HttpServletRequest request, HttpServletResponse response, File file) {
		if (file == null) {
			throw new IllegalArgumentException("file can not be null.");
		}
		this.file = file;
		this.request = request;
		this.response = response;
	}
	
	public void start() {
		if (file == null || !file.isFile()) {
			throw new RuntimeException();
        }
		
		// ---------
		response.setHeader("Accept-Ranges", "bytes");
		response.setHeader("Content-disposition", "attachment; filename=" + encodeFileName(file.getName()));
        response.setContentType(DEFAULT_CONTENT_TYPE);
        
        // ---------
        if (StrKit.isBlank(request.getHeader("Range")))
        	normalStart();
        else
        	rangeStart();
	}
	
	private String encodeFileName(String fileName) {
		try {
			return new String(fileName.getBytes("GBK"), "ISO8859-1");
		} catch (UnsupportedEncodingException e) {
			return fileName;
		}
	}
	
	private void normalStart() {
		response.setHeader("Content-Length", String.valueOf(file.length()));
		InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(file));
            outputStream = response.getOutputStream();
            byte[] buffer = new byte[1024];
            for (int len = -1; (len = inputStream.read(buffer)) != -1;) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();
        }
        catch (IOException e) {
        	if (Cst.me().isDevMode())	throw new RuntimeException(e);
        }
        catch (Exception e) {
        	throw new RuntimeException(e);
        }
        finally {
            if (inputStream != null)
                try {inputStream.close();} catch (IOException e) {LogKit.error(e.getMessage(), e);}
            if (outputStream != null)
            	try {outputStream.close();} catch (IOException e) {LogKit.error(e.getMessage(), e);}
        }
	}
	
	private void rangeStart() {
		Long[] range = {null, null};
		processRange(range);
		
		String contentLength = String.valueOf(range[1].longValue() - range[0].longValue() + 1);
		response.setHeader("Content-Length", contentLength);
		response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);	// status = 206
		
		// Content-Range: bytes 0-499/10000
		StringBuilder contentRange = new StringBuilder("bytes ").append(String.valueOf(range[0])).append("-").append(String.valueOf(range[1])).append("/").append(String.valueOf(file.length()));
		response.setHeader("Content-Range", contentRange.toString());
		
		InputStream inputStream = null;
		OutputStream outputStream = null;
        try {
        	long start = range[0];
        	long end = range[1];
            inputStream = new BufferedInputStream(new FileInputStream(file));
            if (inputStream.skip(start) != start)
                	throw new RuntimeException("File skip error");
            outputStream = response.getOutputStream();
            byte[] buffer = new byte[1024];
            long position = start;
            for (int len; position <= end && (len = inputStream.read(buffer)) != -1;) {
            	if (position + len <= end) {
            		outputStream.write(buffer, 0, len);
            		position += len;
            	}
            	else {
            		for (int i=0; i<len && position <= end; i++) {
            			outputStream.write(buffer[i]);
                    	position++;
            		}
            	}
            }
            outputStream.flush();
        }
        catch (IOException e) {
        	if (Cst.me().isDevMode())	throw new RuntimeException(e);
        }
        catch (Exception e) {
        	throw new RuntimeException(e);
        }
        finally {
            if (inputStream != null)
                try {inputStream.close();} catch (IOException e) {LogKit.error(e.getMessage(), e);}
            if (outputStream != null)
            	try {outputStream.close();} catch (IOException e) {LogKit.error(e.getMessage(), e);}
        }
	}
	
	/**
	 * Examples of byte-ranges-specifier values (assuming an model-body of length 10000):
	 * The first 500 bytes (byte offsets 0-499, inclusive): bytes=0-499
	 * The second 500 bytes (byte offsets 500-999, inclusive): bytes=500-999
	 * The final 500 bytes (byte offsets 9500-9999, inclusive): bytes=-500
	 * 															Or bytes=9500-
	 */
	private void processRange(Long[] range) {
		String rangeStr = request.getHeader("Range");
		int index = rangeStr.indexOf(',');
		if (index != -1)
			rangeStr = rangeStr.substring(0, index);
		rangeStr = rangeStr.replace("bytes=", "");
		
		String[] arr = rangeStr.split("-", 2);
		if (arr.length < 2)
			throw new RuntimeException("Range error");
		
		long fileLength = file.length();
		for (int i=0; i<range.length; i++) {
			if (StrKit.notBlank(arr[i])) {
				range[i] = Long.parseLong(arr[i].trim());
				if (range[i] >= fileLength)
					range[i] = fileLength - 1;
			}
		}
		
		// Range format like: 9500-
		if (range[0] != null && range[1] == null) {
			range[1] = fileLength - 1;
		}
		// Range format like: -500
		else if (range[0] == null && range[1] != null) {
			range[0] = fileLength - range[1];
			range[1] = fileLength - 1;
		}
		
		// check final range
		if (range[0] == null || range[1] == null || range[0].longValue() > range[1].longValue())
			throw new RuntimeException("Range error");
	}
}
