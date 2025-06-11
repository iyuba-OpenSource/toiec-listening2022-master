package com.iyuba.core.common.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import java.io.File;
import java.util.List;

public class SelectPicUtils {

	/***
	 * 选择一张图片 图片类型，这里是image/*，当然也可以设置限制 如：image/jpeg等
	 * 
	 * @param activity
	 *          Activity
	 */
	@SuppressLint("InlinedApi")
	public static void selectPicture(Activity activity, int requestCode) {
		Intent intent = new Intent();
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
		} else {
			intent.addCategory(Intent.CATEGORY_OPENABLE);
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
		}
		activity.startActivityForResult(intent, requestCode);
	}

	@SuppressLint("InlinedApi")
	public static void selectPictureNew(Activity activity, int requestCode) {
		Intent intent = new Intent();
		intent.addCategory("android.intent.category.OPENABLE");
		intent.setAction("android.intent.action.OPEN_DOCUMENT");
		if (Build.VERSION.SDK_INT >= 24) {
			intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
		}

		intent.setType("image/*");
		List<ResolveInfo> resolveInfos = activity.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		
		if (resolveInfos.size() != 0) {
			activity.startActivityForResult(intent, requestCode);
		} else {
			newSelect(activity, requestCode, true);
		}
	}

	private static void newSelect(Activity activity, int requestCode, boolean isImage) {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.GET_CONTENT");
		if (Build.VERSION.SDK_INT >= 24) {
			intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
		}

		if (isImage) {
			intent.setType("image/*");
			activity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), requestCode);
		} else {
			intent.setType("video/*");
			activity.startActivityForResult(Intent.createChooser(intent, "Select Video"), requestCode);
		}

	}

	/***
	 * 裁剪图片
	 * 
	 * @param activity
	 *          Activity
	 * @param uri
	 *          图片的Uri
	 */
	public static void cropPicture(Activity activity, Uri uri, int requestCode,String pathString) {
		Intent innerIntent = new Intent("com.android.camera.action.CROP");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			innerIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			innerIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
		}
		innerIntent.setDataAndType(uri, "image/*");
		innerIntent.putExtra("crop", "true");// 才能出剪辑的小方框，不然没有剪辑功能，只能选取图片
		innerIntent.putExtra("aspectX", 1); // 放大缩小比例的X
		innerIntent.putExtra("aspectY", 1);// 放大缩小比例的X 这里的比例为： 1:1
		innerIntent.putExtra("outputX", 200); // 这个是限制输出图片大小
		innerIntent.putExtra("outputY", 200);
		innerIntent.putExtra("return-data", false);
		innerIntent.putExtra("scale", true);
		//activity.startActivityForResult(innerIntent, requestCode);


		Uri uritempFile = Uri.parse("file://"  + Environment.getExternalStorageDirectory().getPath() + "/" + pathString);
		innerIntent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
		innerIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

		activity.startActivityForResult(innerIntent, requestCode);
	}

	public static void cropPicture(Activity activity, Uri uri, int requestCode) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");// 才能出剪辑的小方框，不然没有剪辑功能，只能选取图片
		intent.putExtra("aspectX", 1); // 放大缩小比例的X
		intent.putExtra("aspectY", 1);// 放大缩小比例的X 这里的比例为： 1:1
		intent.putExtra("outputX", 200); // 这个是限制输出图片大小
		intent.putExtra("outputY", 200);
		intent.putExtra("return-data", true);
		intent.putExtra("scale", true);
		activity.startActivityForResult(intent, requestCode);
	}

	public static void cropPicture24(Activity activity, File inputFile, int requestCode, File outputFile) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		Uri uri = getFileUri(activity, inputFile);
//		Uri uri = getImageContentUri(activity, inputFile);
		intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");// 才能出剪辑的小方框，不然没有剪辑功能，只能选取图片
		intent.putExtra("aspectX", 1); // 放大缩小比例的X
		intent.putExtra("aspectY", 1);// 放大缩小比例的X 这里的比例为： 1:1
		intent.putExtra("outputX", 200); // 这个是限制输出图片大小
		intent.putExtra("outputY", 200);
		intent.putExtra("scale", true);
		intent.putExtra("return-data", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outputFile));
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true);
		activity.startActivityForResult(intent, requestCode);
	}

	public static Uri getImageContentUri(Context context, File imageFile) {
		String filePath = imageFile.getAbsolutePath();
		Cursor cursor = context.getContentResolver().query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Images.Media._ID },
				MediaStore.Images.Media.DATA + "=? ",
				new String[] { filePath }, null);

		if (cursor != null && cursor.moveToFirst()) {
			int id = cursor.getInt(cursor
					.getColumnIndex(MediaStore.MediaColumns._ID));
			Uri baseUri = Uri.parse("content://media/external/images/media");
			return Uri.withAppendedPath(baseUri, "" + id);
		} else {
			if (imageFile.exists()) {
				ContentValues values = new ContentValues();
				values.put(MediaStore.Images.Media.DATA, filePath);
				return context.getContentResolver().insert(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
			} else {
				return null;
			}
		}
	}

	public static Uri getFileUri(Context context, File file) {
		if (Build.VERSION.SDK_INT >= 24) {
			String authority = "com.iyuba.toeiclistening";
			return FileProvider.getUriForFile(context, authority, file);
		} else {
			return Uri.fromFile(file);
		}
	}


	/**
	 * 
	 * Get a file path from a Uri. This will get the the path for Storage Access
	 * Framework Documents, as well as the _data field for the MediaStore and
	 * other file-based ContentProviders.
	 * 
	 * @param context
	 *          The context.
	 * @param uri
	 *          The Uri to query.
	 * @author paulburke
	 */
	@SuppressLint("NewApi")
	public static String getPath(final Context context, final Uri uri) {

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}

				// TODO handle non-primary volumes
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {
				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] { split[1] };

				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {

			// Return the remote address
			if (isGooglePhotosUri(uri))
				return uri.getLastPathSegment();

			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 * 
	 * @param context
	 *          The context.
	 * @param uri
	 *          The Uri to query.
	 * @param selection
	 *          (Optional) Filter used in the query.
	 * @param selectionArgs
	 *          (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri, String selection,
			String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };

		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param uri
	 *          The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri
	 *          The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri
	 *          The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri
	 *          The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}
}
