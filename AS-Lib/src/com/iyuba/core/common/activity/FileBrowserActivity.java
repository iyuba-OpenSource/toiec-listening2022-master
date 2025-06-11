//package com.iyuba.core.common.activity;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//
//import android.app.ActionBar;
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemLongClickListener;
//import android.widget.BaseAdapter;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import com.iyuba.configation.ConfigManager;
//import com.iyuba.configation.Constant;
//import com.iyuba.core.common.adapter.FileAdapter;
//import com.iyuba.core.common.listener.ResultIntCallBack;
//import com.iyuba.core.common.sqlite.mode.FileInfo;
//import com.iyuba.core.common.util.FileActivityHelper;
//import com.iyuba.core.common.util.FileUtil;
//import com.iyuba.core.common.widget.ContextMenu;
//import com.iyuba.core.common.widget.dialog.CustomToast;
//import com.iyuba.lib.R;
//import com.umeng.analytics.MobclickAgent;
//import com.umeng.message.PushAgent;
//
//public class FileBrowserActivity extends Activity {
//	private TextView _filePath;
//	private List<FileInfo> _files = new ArrayList<FileInfo>();
//	private String _currentPath;
//	private BaseAdapter adapter = null;
//	private Context mContext;
//	private ActionBar actionBar;
//	private ProgressDialog progressDialog;
//	private ContextMenu contextMenu;
//	private ListView filelListView;
//
//	/** Called when the activity is first created. */
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.file_browser_main);
//		mContext = this;
//		_currentPath = Constant.envir;
//		_filePath = (TextView) findViewById(R.id.file_path);
//		initActionbar();
//		initOther();
//		adapter = new FileAdapter(this, _files);
////		setListAdapter(adapter);
//		File file = new File(_currentPath);
//		if (!file.exists()) {
//			file.mkdirs();
//		}
//		filelListView = getListView();
//		filelListView.setOnItemLongClickListener(new OnItemLongClickListener() {
//
//			@Override
//			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
//					int arg2, long arg3) {
//				// TODO Auto-generated method stub
//				contextMenu.setText(mContext.getResources().getStringArray(
//						R.array.file_op));
//				FileInfo fileInfo = _files.get(arg2);
//				final File f = new File(fileInfo.Path);
//				contextMenu.setCallback(new ResultIntCallBack() {
//
//					@Override
//					public void setResult(int result) {
//						// TODO Auto-generated method stub
//						switch (result) {
//						case 0:
//							FileActivityHelper.renameFile(
//									FileBrowserActivity.this, f,
//									createDirHandler);
//							break;
//						case 1:
//							pasteFile(f.getPath(), "COPY");
//							break;
//						case 2:
//							pasteFile(f.getPath(), "MOVE");
//							break;
//						case 3:
//							FileUtil.deleteFile(f);
//							viewFiles(_currentPath);
//							break;
//						case 4:
//							FileActivityHelper.viewFileInfo(
//									FileBrowserActivity.this, f);
//							break;
//						default:
//							break;
//						}
//						contextMenu.dismiss();
//					}
//				});
//				contextMenu.show();
//				return true;
//			}
//		});
//		viewFiles(file.getPath());
//	}
//
//	private void initActionbar() {
//		// TODO 自动生成的方法存根
//		actionBar = FileBrowserActivity.this.getSupportActionBar();
//		actionBar.setTitle("文件目录");
//		actionBar.setDisplayHomeAsUpEnabled(true);
//		actionBar.setHomeButtonEnabled(true);
//		actionBar.setDisplayShowHomeEnabled(true);
//		actionBar.setDisplayShowTitleEnabled(true);
//		actionBar.setDisplayUseLogoEnabled(true);
//		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
//		actionBar.setBackgroundDrawable(getResources().getDrawable(
//				R.drawable.titlebar_lightgray_bg));
//	}
//
//	private void initOther() {
//		PushAgent.getInstance(mContext).onAppStart();
//		contextMenu = (ContextMenu) findViewById(R.id.context_menu);
//	}
//
//	@Override
//	protected void onListItemClick(ListView l, View v, int position, long id) {
//		FileInfo f = _files.get(position);
//		if (f.IsDirectory) {
//			viewFiles(f.Path);
//		} else {
//			openFile(f.Path);
//		}
//	}
//
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			File f = new File(_currentPath);
//			String parentPath = f.getParent();
//			if (contextMenu.isShown()) {
//				contextMenu.dismiss();
//			} else if (parentPath != null) {
//				viewFiles(parentPath, _currentPath);
//			} else {
//				FileBrowserActivity.this.finish();
//			}
//			return true;
//		}
//		return super.onKeyDown(keyCode, event);
//	}
//
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if (Activity.RESULT_OK == resultCode) {
//			Bundle bundle = data.getExtras();
//			if (bundle != null && bundle.containsKey("CURRENTPATH")) {
//				viewFiles(bundle.getString("CURRENTPATH"));
//			}
//		}
//	}
//
//	public boolean onCreateOptionsMenu(Menu menu) {
//		MenuInflater inflater = this.getSupportMenuInflater();
//		inflater.inflate(R.menu.file_menu, menu);
//		return true;
//	}
//
//	public boolean onOptionsItemSelected(MenuItem item) {
//		int itemId = item.getItemId();
//		if (itemId == R.id.mainmenu_home) {
//			viewFiles(Constant.envir);
//		} else if (itemId == R.id.mainmenu_refresh) {
//			viewFiles(_currentPath);
//		} else if (itemId == android.R.id.home) {
//			FileBrowserActivity.this.finish();
//		} else if (itemId == R.id.mainmenu_father) {
//			File f = new File(_currentPath);
//			String parentPath = f.getParent();
//			if (parentPath != null) {
//				viewFiles(parentPath,_currentPath);
//			} else {
//				exit();
//			}
//		} else if (itemId == R.id.mainmenu_createdir) {
//			FileActivityHelper.createDir(FileBrowserActivity.this,
//					_currentPath, createDirHandler);
//		} else if (itemId == R.id.mainmenu_save) {
//			moveFolder();
//		} else {
//		}
//		return true;
//	}
//
//	private void moveFolder() {
//		// TODO Auto-generated method stub
//		handler.sendEmptyMessage(0);
//	}
//
//	private void viewFiles(String filePath) {
//		ArrayList<FileInfo> tmp = FileActivityHelper.getFiles(
//				FileBrowserActivity.this, filePath);
//		if (tmp != null) {
//			_files.clear();
//			_files.addAll(tmp);
//			tmp.clear();
//			_currentPath = filePath;
//			_filePath.setText(filePath);
//			adapter.notifyDataSetChanged();
//		}
//	}
//
//	private void viewFiles(String filePath, String lastFilePath) {
//		ArrayList<FileInfo> tmp = FileActivityHelper.getFiles(
//				FileBrowserActivity.this, filePath);
//		if (tmp != null) {
//			_files.clear();
//			_files.addAll(tmp);
//			tmp.clear();
//			_currentPath = filePath;
//			_filePath.setText(filePath);
//			adapter.notifyDataSetChanged();
//			for (int i = 0; i < _files.size(); i++) {
//				Log.e("asd", i+" "+_files.get(i).Path+" "+lastFilePath);
//				if (_files.get(i).Path.equals(lastFilePath)) {
//					filelListView.setSelection(i);
//					break;
//				}
//			}
//		}
//	}
//
//	private void openFile(String path) {
//		Intent intent = new Intent();
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		intent.setAction(android.content.Intent.ACTION_VIEW);
//
//		File f = new File(path);
//		String type = FileUtil.getMIMEType(f.getName());
//		intent.setDataAndType(Uri.fromFile(f), type);
//		startActivity(intent);
//	}
//
//	private Handler handler = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			switch (msg.what) {
//			case 0:
//				if (!Constant.envir.equals(_filePath.getText().toString())) {
//					progressDialog = ProgressDialog.show(mContext,
//							mContext.getString(R.string.file_remove),
//							mContext.getString(R.string.file_remove_info),
//							true, false);
//					new Thread(new Runnable() {
//
//						@Override
//						public void run() {
//							// TODO Auto-generated method stub
//							try {
//								handler.sendEmptyMessage(4);
//								if (FileUtil
//										.moveFile(new File(Constant.envir),
//												new File(_filePath.getText()
//														.toString()))) {
//									handler.sendEmptyMessage(2);
//									handler.sendEmptyMessage(1);
//									ConfigManager.Instance().putString("envir",
//											_filePath.getText().toString());
////									Constant.reLoadData();
//									FileBrowserActivity.this.finish();
//								} else {
//									handler.sendEmptyMessage(1);
//									handler.sendEmptyMessage(3);
//								}
//
//							} catch (Exception e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//						}
//					}).start();
//				} else {
//					FileBrowserActivity.this.finish();
//				}
//				break;
//			case 1:
//				progressDialog.dismiss();
//				break;
//			case 2:
//				CustomToast
//						.showToast(mContext, R.string.file_path_move_success);
//				break;
//			case 3:
//				CustomToast.showToast(mContext,
//						R.string.file_path_move_exception);
//				break;
//			case 4:
//				viewFiles(_currentPath);
//				handler.sendEmptyMessageDelayed(4, 2000);
//				break;
//			default:
//				break;
//			}
//		}
//	};
//
//	private final Handler createDirHandler = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			if (msg.what == 0) {
//				viewFiles(_currentPath);
//			}
//		}
//	};
//
//	private void pasteFile(String path, String action) {
//		Intent intent = new Intent();
//		Bundle bundle = new Bundle();
//		bundle.putString("CURRENTPASTEFILEPATH", path);
//		bundle.putString("ACTION", action);
//		intent.putExtras(bundle);
//		intent.setClass(mContext, PasteFile.class);
//		startActivityForResult(intent, 0);
//	}
//
//	private void exit() {
//		new AlertDialog.Builder(FileBrowserActivity.this)
//				.setMessage(R.string.confirm_exit)
//				.setCancelable(false)
//				.setPositiveButton(R.string.mainmenu_path_save,
//						new DialogInterface.OnClickListener() {
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								if (!(new File(_filePath.getText().toString())
//										.canWrite())) {
//									CustomToast.showToast(mContext,
//											R.string.file_path_ro);
//									return;
//								}
//								moveFolder();
//							}
//						})
//				.setNegativeButton(R.string.mainmenu_exitnow,
//						new DialogInterface.OnClickListener() {
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								dialog.cancel();
//								FileBrowserActivity.this.finish();
//							}
//						}).show();
//	}
//
//	@Override
//	protected void onPause() {
//		// TODO 自动生成的方法存根
//		super.onPause();
//		MobclickAgent.onPause(this);
//	}
//
//	@Override
//	protected void onResume() {
//		// TODO 自动生成的方法存根
//		super.onResume();
//		MobclickAgent.onResume(this);
//	}
//
//	@Override
//	public void finish() {
//		super.finish();
//		handler.removeMessages(4);
//	}
//}
