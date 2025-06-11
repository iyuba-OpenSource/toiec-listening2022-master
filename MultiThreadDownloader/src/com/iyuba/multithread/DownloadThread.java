package com.iyuba.multithread;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import com.iyuba.multithread.util.NetStatusUtil;

import android.R.integer;
import android.util.Log;

/**
 * ?????????
 * @author Administrator
 *
 */
public class DownloadThread extends Thread {
	private static final String TAG = "DownloadThread";
	private File saveFile;
	private URL downUrl;
	private int block;
	
	/* ??????λ??  */
	private int threadId = -1;	
	private int downLength;
	private boolean finish = false;
	private FileDownloader downloader;
	
	
	private int bufferSize;	
	/**
	 * @param downloader:??????
	 * @param downUrl:??????
	 * @param saveFile:???????
	 * 
	 */
	public DownloadThread(FileDownloader downloader, URL downUrl, File saveFile, int block, int downLength, int threadId) {
		this(downloader, downUrl, saveFile, block, downLength, threadId, 1024);
	}
	
	/**
	 * @param downloader:??????
	 * @param downUrl:??????
	 * @param saveFile:???????
	 * 
	 */
	public DownloadThread(FileDownloader downloader, URL downUrl, File saveFile, int block, int downLength, int threadId,int bufferSize) {
		this.downUrl = downUrl;
		this.saveFile = saveFile;
		this.block = block;
		this.downloader = downloader;
		this.threadId = threadId;
		this.downLength = downLength;
		this.bufferSize = bufferSize;
	}
	
	@Override
	public void run() {
		if(downLength < block){//δ???????
			try {
				//???Get???????
				HttpURLConnection http = (HttpURLConnection) downUrl.openConnection();
				http.setConnectTimeout(5 * 1000);
				http.setRequestMethod("GET");
				http.setRequestProperty("Accept", "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
				http.setRequestProperty("Accept-Language", "zh-CN");
				http.setRequestProperty("Referer", downUrl.toString()); 
				http.setRequestProperty("Charset", "UTF-8");
				int startPos = block * (threadId - 1) + downLength;//???λ??
				int endPos = block * threadId -1;//????λ??
				http.setRequestProperty("Range", "bytes=" + startPos + "-"+ endPos);//???????????????Χ
				http.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
				http.setRequestProperty("Connection", "Keep-Alive");
				
				InputStream inStream = http.getInputStream();
				byte[] buffer = new byte[bufferSize];
				int offset = 0;
				print("Thread " + this.threadId + " start download from position "+ startPos);
				RandomAccessFile threadfile = new RandomAccessFile(this.saveFile, "rwd");
				threadfile.seek(startPos);
				while ((offset = inStream.read(buffer, 0, buffer.length)) != -1
						&&!Thread.currentThread().isInterrupted()) {
					threadfile.write(buffer, 0, offset);
					downLength += offset;
					downloader.append(offset);
//					if (Thread.currentThread().isInterrupted()) {
//						break;
//					}
//					??????????
//					print("Thread " + this.threadId +"  : "+downLength);
//					print("Thread " + this.threadId +"  endpos : "+endPos);
				}
				threadfile.close();
				inStream.close();
				downloader.update(this.threadId, downLength);
				print("Thread " + this.threadId + " download finish");
				print("Thread " + this.threadId +"  downLength : "+downLength);
				print("Thread " + this.threadId +"  endposition : "+ (block * (threadId - 1) + downLength));
				print("Thread " + this.threadId +"  block : "+block);
				finish = true;
				if (block-downLength<1024) {
					downLength = block;
				}
			} catch (Exception e) {
				downLength = -1;
				downloader.update(this.threadId, downLength);
				print("Thread "+ this.threadId+ ":"+ e);
			}
		}
		else {
			
		}
	}
	
	/**
	 * ?????????
	 * @param msg
	 */
	private static void print(String msg){
		Log.i(TAG, msg);
	}
	
	/**
	 * ??????????
	 * @return
	 */
	public boolean isFinish() {
		return finish;
	}
	
	/**
	 * ?????????????С
	 * @return ?????????-1,???????????
	 */
	public long getDownLength() {
		return downLength;
	}
}
