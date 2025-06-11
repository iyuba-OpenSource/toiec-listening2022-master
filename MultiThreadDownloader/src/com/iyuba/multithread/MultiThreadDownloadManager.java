package com.iyuba.multithread;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import android.content.Context;
import android.util.Log;

/**
 * @author chenzefeng
 *
 *????????????
 */
public class MultiThreadDownloadManager {
	
	private static final String DOWNLOAD_STATE_DOWNLOADING = "isdownloading";
	private static final String DOWNLOAD_STATE_PAUSING = "ispausing";
	private static final String DOWNLOAD_STATE_NO_DOWNLOADING = "nothingisdownloading";
	private static final String DOWNLOAD_STATE_APP_CLOSING = "appisclosing";

	private static String currentState = null;
	
	
	/**
	 * ???е?????????
	 */
	private static HashMap<Integer,FileDownloader> tasks = new HashMap<Integer, FileDownloader>();
	
	/**
	 * ????????
	 */
	private static FileService fileService;
	
	
//	/**
//	 * ????????δ???????????????????????id???
//	 */
//	public static HashMap<Integer,DownloadTask> tasks = new HashMap<Integer, FileService.DownloadTask>();
	
	public static void enQueue(final Context context,
			final int voaid,
			final String downloadUrl, 
			final File fileSaveDir,
			final int threadNum,
			final DownloadProgressListener listener){
		if (fileService == null) {
			fileService = new FileService(context);
		}
		DownloadProgressListener progressListener = new DownloadProgressListener() {
			@Override
			public void onProgressUpdate(int id,String downloadurl, int fileDownloadSize) {
				// TODO ??????????????
				if (listener!=null) {
					listener.onProgressUpdate(id,downloadUrl, fileDownloadSize);
				}
			}
			
			@Override
			public void onDownloadStoped(int id) {
				// TODO ??????????????
				if (tasks.isEmpty()) {
					currentState = DOWNLOAD_STATE_NO_DOWNLOADING;
				}
				if (listener!=null) {
					listener.onDownloadStoped(id);
				}
			}
			
			@Override
			public void onDownloadStart(FileDownloader fileDownloader,int id,int fileTotalSize) {
				// TODO ??????????????
				if (!tasks.containsKey(id)) {
					tasks.put(id, fileDownloader);
					currentState = DOWNLOAD_STATE_DOWNLOADING;
				}
				if (listener!=null) {
					listener.onDownloadStart(fileDownloader,id,fileTotalSize);
				}
			}
			
			@Override
			public void onDownloadError(String errorMessage) {
				// TODO ??????????????
				if (listener!=null) {
					listener.onDownloadError(errorMessage);
				}
			}
			
			@Override
			public void onDownloadComplete(int id,String savePathFullName) {
				// TODO ??????????????
				tasks.remove(id);
				if (tasks.isEmpty()) {
					currentState = DOWNLOAD_STATE_NO_DOWNLOADING;
				}
				if (listener!=null) {
					listener.onDownloadComplete(id,savePathFullName);
				}
			}
		};
		if (tasks.containsKey(voaid)) {
			try {
				if (!tasks.get(voaid).isRunning()) {
					tasks.get(voaid).download(voaid, progressListener);
				}
				else {
					Log.e("enQueue", "the task+"+voaid+" is running");
				}
			} catch (Exception e) {
				// TODO ???????? catch ??
				e.printStackTrace();
			}
		}
		else {
			final FileDownloader tempDownloader = new FileDownloader(
					context,
					downloadUrl,
					fileSaveDir,
					threadNum);
			try {
				tempDownloader.download(voaid, progressListener);
			} catch (Exception e) {
				// TODO ???????? catch ??
				e.printStackTrace();
			}
		}
		
							
	}

	
	
	/**
	 * ?????????????
	 */
	public static void stopDownloads(){
		if (tasks != null) {
			Iterator iterator = tasks.keySet().iterator();
			while(iterator.hasNext()){
				FileDownloader type = (FileDownloader) tasks.get(iterator.next());
				if (type.isRunning()) {
					type.cancle();
				}
			}
		}
		currentState = DOWNLOAD_STATE_NO_DOWNLOADING;
	}
	
	/**
	 * ????????????
	 * @param id
	 * ?????id
	 */
	public static void stopDownload(int id){
		if (tasks != null) {
			FileDownloader type = (FileDownloader) tasks.get(id);
			type.cancle();
		}
		if (tasks.isEmpty()) {
			currentState = DOWNLOAD_STATE_NO_DOWNLOADING;
		}
	}
	
	/**
	 * ????????????δ???????
	 * @param context
	 * @return
	 */
	public static boolean hasUnFinishTask(Context context){
		if (fileService == null) {
			fileService = new FileService(context);
		}
		tasks = fileService.findDownload(context);
		return !tasks.isEmpty();
	}
	
	/**
	 * ????id???????
	 * @param context
	 * @return
	 */
	public static int getTaskPercentage(int id){
		if (tasks.containsKey(id)) {
			return tasks.get(id).getDownloadPercentage();
		}
		return 0;
	}
	

	/**
	 * ????id???????????????
	 * @param context
	 * @return
	 */
	public static void removeTask(int id){
		
		if (tasks.containsKey(id)) {
			tasks.remove(id);
		}
		if (tasks.isEmpty()) {
			currentState = DOWNLOAD_STATE_NO_DOWNLOADING;
		}
	}
	
	/**
	 * ????д?task
	 * @param context
	 * @return
	 */
	public static boolean hasTask(int id){
		
		return tasks.containsKey(id);
	}
	
	/**
	 * ???????????????????
	 * @param context
	 * @return
	 */
	public static void removeAllTask(){
		
		if (tasks != null) {
			Iterator iterator = tasks.keySet().iterator();
			while(iterator.hasNext()){
				FileDownloader type = (FileDownloader) tasks.get(iterator.next());
				type.cancle();
				File file = new File(type.getDownloadUrl());
				if (file.exists()) {
					file.delete();
				}
				fileService.delete(type.getDownloadUrl());
			}
		}
		currentState = DOWNLOAD_STATE_NO_DOWNLOADING;
	}
	
	/**
	 * ????????????????????????
	 * @return
	 */
	public static boolean IsDowning(){
		return DOWNLOAD_STATE_DOWNLOADING.equals(currentState);
	}
	
	/**
	 * ????????????????????
	 * @return
	 */
	public static int DowningTaskNum(){
		return tasks.size();
	}
}
