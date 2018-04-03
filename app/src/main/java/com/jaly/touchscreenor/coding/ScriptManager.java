package com.jaly.touchscreenor.coding;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.xutils.x;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;

import com.jaly.touchscreenor.util.AppUtils;

/**
 * 脚本管理器
 * 
 * @author Administrator
 * 
 */
public class ScriptManager {

	private final static String SCRIPT_FOLDER = "/scripts/"; // 存放脚本的文件夹
	private final static String SRC_EXT = ".xml";            // 脚本源码文件格式
	private final static String BIN_EXT = ".ts";             // 脚本执行文件格式
	private final static String INF_EXT = ".inf";            // 脚本信息文件格式

	private String scriptFolder;
	private Context context;

	public ScriptManager(Context context) {
		this.context = context;
		scriptFolder = context.getFilesDir().getAbsolutePath() + SCRIPT_FOLDER;
	}

	/**
	 * 脚本文件重命名
	 * 
	 * @param fileName
	 * @param newName
	 */
	public void rename(String fileName, String newName) {
		if (newName.endsWith(SRC_EXT)) {
			newName.replace(SRC_EXT, "");
		}

		File file = new File(scriptFolder + fileName + SRC_EXT);
		File newFile = new File(scriptFolder + newName + SRC_EXT);
		if (file.renameTo(newFile)) {
			file = new File(scriptFolder + fileName + BIN_EXT);
			newFile = new File(scriptFolder + newName + BIN_EXT);
			file.renameTo(newFile);
			file = new File(scriptFolder + fileName + INF_EXT);
			newFile = new File(scriptFolder + newName + INF_EXT);
			file.renameTo(newFile);
		}
	}

	/**
	 * 保存脚本和解码后的二进制文件
	 * 
	 * @param fileName
	 * @param content 
	 * @throws IOException
	 */
	public void save(String fileName, String content) throws Exception {
		File folder = new File(scriptFolder);
		if (!folder.exists()) {
			folder.mkdir();
		}
		if (fileName.endsWith(SRC_EXT)) {
			fileName = fileName.replace(SRC_EXT, "");
		}
		// 保存脚本源码文件
		String srcFilePath = scriptFolder + fileName + SRC_EXT;
		FileOutputStream fos = new FileOutputStream(srcFilePath);
		fos.write(content.getBytes());
		fos.flush();
		fos.close();

		decode(fileName);
	}

	/**
	 * 上传脚本文件到服务器
	 * 
	 * @param fileName
	 * @param requestUrl
	 */
	public void upload(String fileName, String requestUrl) {
		String srcFilePath = scriptFolder + fileName + SRC_EXT;
		RequestParams params = new RequestParams(requestUrl);
		params.setMultipart(true);
		params.addBodyParameter("app", new File(srcFilePath));
		x.http().post(params, new Callback.CommonCallback<String>() {

			@Override
			public void onCancelled(CancelledException arg0) {
				Log.e("ScriptManger", arg0.getMessage());
			}

			@Override
			public void onError(Throwable arg0, boolean arg1) {
				Log.e("ScriptManger", arg0.getMessage());
			}

			@Override
			public void onFinished() {
				Log.i("ScriptManger", "upload finished");
			}

			@Override
			public void onSuccess(String arg0) {
				Log.i("ScriptManger", arg0);
			}
		});
	}

	/**
	 * 从服务器下载文件
	 * 
	 * @param url
	 */
	public void download(String url) {
		RequestParams params = new RequestParams(url);
		// 自定义保存路径，Environment.getExternalStorageDirectory()：SD卡的根目录
		params.setSaveFilePath(Environment.getExternalStorageDirectory()
				+ "/download/");
		// 自动为文件命名
		params.setAutoRename(true);
		x.http().post(params, new Callback.ProgressCallback<File>() {
			@Override
			public void onSuccess(File result) {
				Log.i("ScriptManager", result.getName());
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
			}

			@Override
			public void onCancelled(CancelledException cex) {
			}

			@Override
			public void onFinished() {
			}

			// 网络请求之前回调
			@Override
			public void onWaiting() {
			}

			// 网络请求开始的时候回调
			@Override
			public void onStarted() {
			}

			// 下载的时候不断回调的方法
			@Override
			public void onLoading(long total, long current,
					boolean isDownloading) {
				// 当前进度和文件总大小
				Log.i("ScriptManager", "current：" + current + "，total：" + total);
			}
		});
	}

	/**
	 * 脚本解码，保存脚本信息文件和执行文件
	 * 
	 * @param fileName
	 * @throws Exception
	 */
	public void decode(String fileName) throws Exception {
		String srcFilePath = scriptFolder + fileName + SRC_EXT;
		// 脚本解码
		ScriptDecoding decoding = new ScriptDecoding(srcFilePath);
		TagScript tagScript = decoding.getTagScript();
		// 保存执行文件
		String binFilePath = scriptFolder + fileName + BIN_EXT;
		FileOutputStream fos = new FileOutputStream(binFilePath);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(tagScript);
		oos.flush();
		fos.close();
		oos.close();
		// 保存脚本信息
		String infFilePath = scriptFolder + fileName + INF_EXT;
		String pkg = tagScript.getPkgName();
		Date modifiedDate = new Date(new File(srcFilePath).lastModified());
		ScriptInfo scriptInfo = new ScriptInfo(fileName, pkg, modifiedDate);
		fos = new FileOutputStream(infFilePath);
		oos = new ObjectOutputStream(fos);
		oos.writeObject(scriptInfo);
		oos.flush();
		fos.close();
		oos.close();
	}

	/**
	 * 读取脚本文件的文本内容
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public String readSource(String fileName) throws IOException {
		String filePath = scriptFolder + fileName + SRC_EXT;
		StringBuilder sb = new StringBuilder();
		FileInputStream fis = new FileInputStream(filePath);
		byte[] buf = new byte[1024];
		int len = 0;
		while ((len = fis.read(buf)) != -1) {
			sb.append(new String(buf, 0, len));
		}
		fis.close();
		return sb.toString();
	}

	/**
	 * 读取执行代码
	 * 
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public TagScript readBinary(String fileName) throws Exception {
		String binFilePath = scriptFolder + fileName + BIN_EXT;
		FileInputStream fis = new FileInputStream(binFilePath);
		ObjectInputStream ois = new ObjectInputStream(fis);
		TagScript tagScript = (TagScript) ois.readObject();
		fis.close();
		ois.close();
		return tagScript;
	}

	/**
	 * 读取脚本信息
	 * 
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public ScriptInfo readInfo(String fileName) throws Exception {
		String infFilePath = scriptFolder + fileName + INF_EXT;
		FileInputStream fis = new FileInputStream(infFilePath);
		ObjectInputStream ois = new ObjectInputStream(fis);
		ScriptInfo scriptInfo = (ScriptInfo) ois.readObject();
		fis.close();
		ois.close();
		return scriptInfo;
	}

	/**
	 * 删除脚本和执行文件
	 * 
	 * @param fileName
	 */
	public void delete(String fileName) {
		File file = new File(scriptFolder + fileName + SRC_EXT);
		if (file.delete()) {
			file = new File(scriptFolder + fileName + INF_EXT);
			file.delete();
			file = new File(scriptFolder + fileName + BIN_EXT);
			file.delete();
		}
	}

	/**
	 * 获取脚本信息列表
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<ScriptInfo> listScripts() throws Exception {
		PackageManager pm = context.getPackageManager();
		List<ScriptInfo> infos = new ArrayList<ScriptInfo>();
		File folder = new File(scriptFolder);
		if (folder.exists()) {
			File[] listFiles = folder.listFiles();
			for (File f : listFiles) {
				String fileName = f.getName();
				if (fileName.endsWith(INF_EXT)) {
					fileName = fileName.replace(INF_EXT, "");
					ScriptInfo info = readInfo(fileName);
					Drawable icon = pm.getApplicationIcon(pm
							.getApplicationInfo(info.getPkgName(), 0));
					info.setAppIcon(icon);
					infos.add(info);
					// 建立脚本名和图片的键值对
					AppUtils.putAppIcon(fileName, icon);
				}
			}
		}
		return infos;
	}

}
