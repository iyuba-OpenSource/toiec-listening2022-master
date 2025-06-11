package com.iyuba.toeiclistening.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.adapter.DeleteDataAdapter;
import com.iyuba.toeiclistening.util.Constant;

import java.io.File;
import java.util.ArrayList;

public class DeleteDataActivity extends Activity{
	private Context mContext;
	private ListView testListView;
	private Button backButton;
	private Button editButton;
	private ArrayList<File> testList;
	private File file;
	private DeleteDataAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.delete_test);
		ini();
		getSetView();
	}

	public void ini(){
		mContext=this;
		file=new File(com.iyuba.toeiclistening.Constant.APP_DATA_PATH+Constant.SDCARD_AUDIO_PATH);
		Log.e("llll", file.length()+"");
		if(file.exists()){
			File[] files=file.listFiles();
			if(files!=null){
				testList=new ArrayList<File>();
				for(int i=0;i<files.length;i++){
					if(!files[i].getName().equals("word")&&!files[i].getName().equals("Test1")){
						//Log.e("ss", files[i].length()+"");
						testList.add(files[i]);
					}
				}
				adapter=new DeleteDataAdapter(mContext, testList);
			}
		}
	}
	public void getSetView(){
		backButton=(Button)findViewById(R.id.delete_test_button_back);
		backButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		if(adapter!=null){
			testListView=(ListView)findViewById(R.id.delete_test_list);
			testListView.setAdapter(adapter);
			testListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					int state=adapter.getState();
					if(state==0){
						adapter.updateView(arg2);
					}			
				}
			});
		}
		editButton=(Button)this.findViewById(R.id.delete_data_edit);
	    editButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				int state=adapter.getState();
				if(state==1){//如果不处在批量删除的状态
					editButton.setText(R.string.favtitle_finish);
					adapter.setState(0);
					adapter.notifyDataSetChanged();
				}else{//处在批量删除的状态
					editButton.setText(R.string.favtitle_delete);
					adapter.setState(1);
				}
				
			}
		});
		
	}
}
