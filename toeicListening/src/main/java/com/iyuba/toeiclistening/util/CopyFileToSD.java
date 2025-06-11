package com.iyuba.toeiclistening.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;

import com.iyuba.toeiclistening.listener.OnFinishedListener;

/**
 * 
 * @author Administrator
 * 
 *         鍔熻兘锛氬皢assets鐩綍涓嬫寚瀹氭枃浠跺す鍙婂瓙鏂囦欢澶瑰拰鏂囦欢鎷疯礉鍒皊d锟?
 * 
 */
public class CopyFileToSD {
	private Context mContext;
	private OnFinishedListener oflistener;
	private String targetDir;// 鎷疯礉鍒皊d鐨勭洰鏍囧湴锟?
	private int sourceFileNum;// 婧愭枃浠舵暟

	/**
	 * 
	 * 鎶奱ssets鐩綍涓嬫寚瀹氭枃浠跺す閲岀殑鏂囦欢鎷疯礉鍒版寚瀹歴d鍗＄洰褰曚笅
	 * 
	 * @param context
	 * @param assetDir
	 *            assets鐩綍涓嬫寚瀹氭枃浠跺す锟?
	 * @param dir
	 *            sd鍗＄洰锟?
	 */
	public CopyFileToSD(Context context, String assetDir, String dir,
			OnFinishedListener oflistener) throws IOException {
		mContext = context;
		this.oflistener = oflistener;

		this.targetDir = dir;
		this.sourceFileNum = mContext.getResources().getAssets().list(assetDir).length;
		if (ListFileUtil.checkSubFiles(targetDir) == sourceFileNum) {
			oflistener.onFinishedListener();
			return;
		}
		CopyAssets(assetDir, dir);
		oflistener.onFinishedListener();
		/*
		if (targetDir.equals(dir)
				&& ListFileUtil.checkSubFiles(targetDir) == sourceFileNum) {// 瀛愭枃浠跺す鏁扮瓑锟?0
			oflistener.onFinishedListener();
			return;
		}*/
	}

	public CopyFileToSD(Context context, String outputdir, String outfileName,
			String filePath, OnFinishedListener onFinishedListener)
			throws IOException {
		mContext = context;
		this.oflistener = onFinishedListener;
		copyFile(outputdir, outfileName, filePath);
	}

	public void copyFile(String outputdir, String outfileName, String filePath) {
		File dFile = new File(outputdir);
		if (!dFile.exists()) {
			dFile.mkdirs();
		}
		File outFile = new File(outputdir, outfileName);
		//Log.e("outFile",outFile.length()+"");
		if (outFile.exists() && outFile.length() > 1024 * 1024) {
			return;
		} else if (outFile.exists()) {
			outFile.delete();
		}
		if (!outFile.exists()) {
			try {
				boolean flag = outFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		InputStream in = null;
		try {
			in = mContext.getAssets().open(filePath);
			OutputStream out = new FileOutputStream(outFile);
			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();

			oflistener.onFinishedListener();
			return;
		} catch (IOException e) {
			oflistener.onErrorListener();
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param assetDir
	 * @param dir
	 *            鍔熻兘锟?iYuba/jlpt3/image
	 */
	public void CopyAssets(String assetDir, String dir) {
		String[] files;
		File mWorkingPath = new File(dir);
		// if this directory does not exists, make one.
		// 鍒ゆ柇/iYuba/jlpt3/image鏂囦欢鏄惁瀛樺湪
		if (!mWorkingPath.exists()) {
			mWorkingPath.mkdirs();
		}

		try {
			files = mContext.getResources().getAssets().list(assetDir);
			// Log.e(" assets涓嬫枃浠舵暟",
			// "褰撳墠assetDir锟?+assetDir+"涓嬫枃浠舵暟锟?+files.length);
		} catch (IOException e1) {
			// oflistener.onFinishedListener();
			return;
		}

		for (int i = 0; i < files.length; i++) {
			try {
				String fileName = files[i];
				// we make sure file name not contains '.' to be a folder.
				if (!fileName.contains(".")) {
					if (0 == assetDir.length()) {
						CopyAssets(fileName, dir + "/" + fileName + "/");
					} else {
						CopyAssets(assetDir + "/" + fileName, dir + "/"
								+ fileName + "/");
					}
					continue;
				}
				File outFile = new File(mWorkingPath, fileName);
			
				//Log.e("outFile",outFile.length()+"");
				if (outFile.exists() && outFile.length() > 1024) {
					continue;
				} else if (outFile.exists()) {
					outFile.delete();
				}
				if (!outFile.exists()) {
					boolean flag = outFile.createNewFile();
				}

				InputStream in = null;
				if (0 != assetDir.length())
					in = mContext.getAssets().open(assetDir + "/" + fileName);
				else
					in = mContext.getAssets().open(fileName);
				OutputStream out = new FileOutputStream(outFile);

				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}

				in.close();
				out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// Log.e("CopyFileToSD:images",dir+"鎷疯礉鎴愬姛");
		// Log.e("",
		// "鐩爣鍦板潃"+targetDir+"鏂囦欢锟?+ListFileUtil.checkSubFiles(targetDir));
		// 锟?锟斤拷鐩爣鍦板潃鏂囦欢澶规暟
		

	}

}
