package com.biu.core.toolbox.file;

import com.alibaba.druid.util.Base64;
import com.biu.core.constant.ConstConfig;
import com.biu.core.constant.Cst;
import com.biu.core.toolbox.Func;
import com.biu.core.toolbox.kit.DateKit;
import com.biu.core.toolbox.kit.PathKit;
import com.biu.core.toolbox.kit.StrKit;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.stream.FileImageOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class BladeFileKit {

	// 定义允许上传的文件扩展名
	private static HashMap<String, String> extMap = new HashMap<String, String>();
	// 图片扩展名
	private static String[] fileTypes = new String[] { "gif", "jpg", "jpeg", "png", "bmp" };

	static {
		extMap.put("image", ".gif,.jpg,.jpeg,.png,.bmp,.JPG,.JPEG,.PNG");
		extMap.put("flash", ".swf,.flv");
		extMap.put("media", ".swf,.flv,.mp3,.mp4,.wav,.wma,.wmv,.mid,.avi,.mpg,.asf,.rm,.rmvb");
		extMap.put("file", ".doc,.docx,.xls,.xlsx,.ppt,.htm,.html,.txt,.zip,.rar,.gz,.bz2");
		extMap.put("allfile", ".gif,.jpg,.jpeg,.png,.bmp,.swf,.flv,.mp3,.mp4,.wav,.wma,.wmv,.mid,.avi,.mpg,.asf,.rm,.rmvb,.doc,.docx,.xls,.xlsx,.ppt,.htm,.html,.txt,.zip,.rar,.gz,.bz2");
	}
	
	/**
	 * 获取文件后缀
	 * 
	 * @param @param fileName
	 * @param @return 设定文件
	 * @return String 返回类型
	 */
	public static String getFileExt(String fileName) {
		return fileName.substring(fileName.lastIndexOf('.'), fileName.length());
	}
	
	/**
	 * 测试文件后缀 只让指定后缀的文件上传，像jsp,war,sh等危险的后缀禁止
	 * 
	 * @return
	 */
	public static boolean testExt(String dir, String fileName) {
		String fileExt = getFileExt(fileName);

		String ext = extMap.get(dir);
		if (StrKit.isBlank(ext) || ext.indexOf(fileExt) == -1) {
			return false;
		}
		return true;
	}

	/**
	 * 文件管理排序
	 */
	public enum FileSort {
		size, type, name;

		// 文本排序转换成枚举
		public static FileSort of(String sort) {
			try {
				return FileSort.valueOf(sort);
			} catch (Exception e) {
				return FileSort.name;
			}
		}
	}

	// 理论上应该根据dir到指定目录下进行文件分类的汇总
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map<String, Object> listFiles(String dir, String path, String order) {
		String saveDir = Cst.me().getUploadCtxPath().replaceFirst("/", "");
		saveDir = StrKit.isBlank(saveDir) ? "upload" : saveDir;
		// 获取webRoot目录
		String webRoot = PathKit.getWebRootPath();

		// 根目录路径，可以指定绝对路径，比如 /var/www/attached/
		String rootPath = webRoot + File.separator + saveDir + File.separator;
		// 根目录URL，可以指定绝对路径，比如 http://www.yoursite.com/attached/
		String rootUrl = Cst.me().getContextPath() + File.separator + saveDir + File.separator;

		String currentPath = rootPath + path;
		String currentUrl = rootUrl + path;
		String currentDirPath = path;
		String moveupDirPath = "";
		if (!"".equals(path)) {
			String str = currentDirPath.substring(0, currentDirPath.length() - 1);
			moveupDirPath = str.lastIndexOf("/") >= 0 ? str.substring(0, str.lastIndexOf("/") + 1) : "";
		}

		File currentPathFile = new File(currentPath);
		// 未上传时upload目录不一定存在
		if (!currentPathFile.exists()) {
			currentPathFile.mkdirs();
		}
		File[] files = currentPathFile.listFiles();

		// 遍历目录取的文件信息
		List<Hashtable> fileList = new ArrayList<Hashtable>();
		if (files != null) {
			for (File file : files) {
				Hashtable<String, Object> hash = new Hashtable<String, Object>();
				String fileName = file.getName();
				if (file.isDirectory()) {
					hash.put("is_dir", true);
					hash.put("has_file", (file.listFiles() != null));
					hash.put("filesize", 0L);
					hash.put("is_photo", false);
					hash.put("filetype", "");
				} else if (file.isFile()) {
					String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
					hash.put("is_dir", false);
					hash.put("has_file", false);
					hash.put("filesize", file.length());
					hash.put("is_photo", Arrays.<String> asList(fileTypes).contains(fileExt));
					hash.put("filetype", fileExt);
				}
				hash.put("filename", fileName);
				hash.put("datetime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(file.lastModified()));
				fileList.add(hash);
			}
		}
		FileSort sort = FileSort.of(order);
		if (sort.equals(FileSort.size)) {
			Collections.sort(fileList, new SizeComparator());
		} else if (sort.equals(FileSort.type)) {
			Collections.sort(fileList, new TypeComparator());
		} else {
			Collections.sort(fileList, new NameComparator());
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("moveup_dir_path", moveupDirPath);
		result.put("current_dir_path", currentDirPath);
		result.put("current_url", currentUrl);
		result.put("total_count", fileList.size());
		result.put("file_list", fileList);

		return result;
	}

	@SuppressWarnings("rawtypes")
	public static class NameComparator implements Comparator {
		public int compare(Object a, Object b) {
			Hashtable hashA = (Hashtable) a;
			Hashtable hashB = (Hashtable) b;
			if (((Boolean) hashA.get("is_dir")) && !((Boolean) hashB.get("is_dir"))) {
				return -1;
			} else if (!((Boolean) hashA.get("is_dir")) && ((Boolean) hashB.get("is_dir"))) {
				return 1;
			} else {
				return ((String) hashA.get("filename")).compareTo((String) hashB.get("filename"));
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public static class SizeComparator implements Comparator {
		public int compare(Object a, Object b) {
			Hashtable hashA = (Hashtable) a;
			Hashtable hashB = (Hashtable) b;
			if (((Boolean) hashA.get("is_dir")) && !((Boolean) hashB.get("is_dir"))) {
				return -1;
			} else if (!((Boolean) hashA.get("is_dir")) && ((Boolean) hashB.get("is_dir"))) {
				return 1;
			} else {
				if (((Long) hashA.get("filesize")) > ((Long) hashB.get("filesize"))) {
					return 1;
				} else if (((Long) hashA.get("filesize")) < ((Long) hashB.get("filesize"))) {
					return -1;
				} else {
					return 0;
				}
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public static class TypeComparator implements Comparator {
		public int compare(Object a, Object b) {
			Hashtable hashA = (Hashtable) a;
			Hashtable hashB = (Hashtable) b;
			if (((Boolean) hashA.get("is_dir")) && !((Boolean) hashB.get("is_dir"))) {
				return -1;
			} else if (!((Boolean) hashA.get("is_dir")) && ((Boolean) hashB.get("is_dir"))) {
				return 1;
			} else {
				return ((String) hashA.get("filetype")).compareTo((String) hashB.get("filetype"));
			}
		}
	}

	public static String formatUrl(String url) {
		return url.replaceAll("\\\\", "/");
	}
	
	public static String uploadBase64(String nr, String addr) {
		String contentPath = "";
		try {
			String nowdate =  DateKit.getDays();
			byte[] b = Base64.base64ToByteArray(nr);
			try {
				String path = Cst.me().getUploadRealPath();
				contentPath = Cst.me().getUploadCtxPath();
				File file = new File(path + "/" + nowdate);
				String newFileName = DateKit.getAllTime() + "_" + new Random().nextInt(1000) + "." + addr;
				String uploadPath = path + "/" + nowdate + "/" + newFileName;
				contentPath = contentPath + "/" + nowdate + "/" + newFileName;
				if (!file.exists() && !file.isDirectory()) {
					file.mkdirs();
				}
				File imgfile = new File(uploadPath);
				imgfile.createNewFile();
				FileImageOutputStream imageOutput = new FileImageOutputStream(new File(uploadPath));
				imageOutput.write(b, 0, b.length);
				imageOutput.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return contentPath.replace(Cst.me().getContextPath(), "");
	}

	public static String uploadMultiFile(MultipartFile mfile) {
		String contentPath = "";
		try {
			String nowdate = DateKit.getDays();
			String path = Cst.me().getUploadRealPath();
			contentPath = Cst.me().getUploadCtxPath();
			File file = new File(path + "/" + nowdate);
			String newFileName = DateKit.getAllTime() + "_" + new Random().nextInt(1000) + getFileExt(mfile.getOriginalFilename());
			String uploadPath = path + "/" + nowdate + "/" + newFileName;
			contentPath = contentPath + "/" + nowdate + "/" + newFileName;
			if (!file.exists() && !file.isDirectory()) {
				file.mkdirs();
			}
			File imgfile = new File(uploadPath);
			mfile.transferTo(imgfile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return contentPath.replace(Cst.me().getContextPath(), "");
	}
	
	
	/********************************BladeFile封装********************************************************/
	
	/**
	 * 获取BladeFile封装类
	 * @param file
	 * @return
	 */
	public static BladeFile getFile(MultipartFile file){
		return getFile(file, "image", null, null);
	}
	
	/**
	 * 获取BladeFile封装类
	 * @param file
	 * @param dir
	 * @return
	 */
	public static BladeFile getFile(MultipartFile file, String dir){
		return getFile(file, dir, null, null);
	}
	
	/**
	 * 获取BladeFile封装类
	 * @param file
	 * @param dir
	 * @param path
	 * @param virtualPath
	 * @return
	 */
	public static BladeFile getFile(MultipartFile file, String dir, String path, String virtualPath){
		return new BladeFile(file, dir, path, virtualPath);
	}
	
	/**
	 * 获取BladeFile封装类
	 * @param files
	 * @return
	 */
	public static List<BladeFile> getFiles(List<MultipartFile> files){
		return getFiles(files, "image", null, null);
	}
	
	/**
	 * 获取BladeFile封装类
	 * @param files
	 * @param dir
	 * @return
	 */
	public static List<BladeFile> getFiles(List<MultipartFile> files, String dir){
		return getFiles(files, dir, null, null);
	}
	
	/**
	 * 获取BladeFile封装类
	 * @param files
	 * @param path
	 * @param virtualPath
	 * @return
	 */
	public static List<BladeFile> getFiles(List<MultipartFile> files, String dir, String path, String virtualPath){
		List<BladeFile> list = new ArrayList<>();
		for (MultipartFile file : files){
			list.add(new BladeFile(file, dir, path, virtualPath));
		}
		return list;
	}
	
	/**   
	 * 为虚拟路径添加配置的域名前缀
	 * @param map  对象
	 * @param name 路径字段名
	*/
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void addDomain(Map map, String name) {
		if (null == map) {
			return;
		}
		String url = Func.toStr(map.get(name));
		// map为引用传递, 防止每次附加到缓存
		if (url.indexOf(ConstConfig.DOMAIN) >= 0 || StrKit.isBlank(url)) {
			return;
		} else {
			url = ConstConfig.DOMAIN + url;
			map.put(name, url);
		}
	}
	
	/**   
	 * 为虚拟路径添加配置的域名前缀
	 * @param map  对象
	 * @param name 路径字段名
	 * @param defaultUrl 默认路径
	*/
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void addDomain(Map map, String name, String defaultUrl) {
		if (null == map) {
			return;
		}
		String url = Func.toStr(map.get(name));
		// map为引用传递, 防止每次附加到缓存
		if (url.indexOf(ConstConfig.DOMAIN) >= 0) {
			return;
		} else {
			url = ConstConfig.DOMAIN + (StrKit.isBlank(url) ? defaultUrl : url);
			map.put(name, url);
		}
	}

	/**   
	 * 为虚拟路径添加配置的域名前缀
	 * @param list 对象
	 * @param name 路径字段名
	*/
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void addDomain(List<Map> list, String name) {
		for (Map m : list) {
			if (null == m) {
				continue;
			}
			String url = Func.toStr(m.get(name));
			// map为引用传递, 防止每次附加到缓存
			if (url.indexOf(ConstConfig.DOMAIN) >= 0 || StrKit.isBlank(url)) {
				break;
			} else {
				url = ConstConfig.DOMAIN + url;
				m.put(name, url);
			}
		}
	}
	
	/**   
	 * 为虚拟路径添加配置的域名前缀
	 * @param list 对象
	 * @param name 路径字段名
	 * @param defaultUrl 默认路径
	*/
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void addDomain(List<Map> list, String name, String defaultUrl) {
		for (Map m : list) {
			if (null == m) {
				continue;
			}
			String url = Func.toStr(m.get(name));
			// map为引用传递, 防止每次附加到缓存
			if (url.indexOf(ConstConfig.DOMAIN) >= 0) {
				break;
			} else {
				url = ConstConfig.DOMAIN + (StrKit.isBlank(url) ? defaultUrl : url);
				m.put(name, url);
			}
		}
	}

}
