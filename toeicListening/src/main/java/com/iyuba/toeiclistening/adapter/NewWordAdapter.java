package com.iyuba.toeiclistening.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.entity.NewWord;
import com.iyuba.toeiclistening.listener.DownLoadStatueChangeListener;
import com.iyuba.toeiclistening.sqlite.ZDBHelper;
import com.iyuba.toeiclistening.util.Constant;
import com.iyuba.toeiclistening.util.DownFile;
import com.iyuba.toeiclistening.util.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public class NewWordAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private ArrayList<NewWord> newWordList;
	private ArrayList<EditCond> ediCondList;

	private ViewHolder viewHolder;
	private ZDBHelper helper;
	private int state = 1;
	public Player player;

	public NewWordAdapter(Context context, ArrayList<NewWord> wordList) {
		mContext = context;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		newWordList = wordList;
		helper = new ZDBHelper(mContext);
		iniEdiCondList();
		player = new Player(mContext, null);
		// TODO Auto-generated constructor stub
	}

	public void iniEdiCondList() {
		ediCondList = new ArrayList<EditCond>();
		for (int i = 0; i < newWordList.size(); i++) {
			EditCond editCond = new EditCond();
			editCond.switchState = 0;
			editCond.explainState = false;
			ediCondList.add(editCond);
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return newWordList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return newWordList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		int curPosition;
		final NewWord curNewWord;
		final EditCond curEditCond;
		final ViewHolder curViewHolder;
		if (arg1 == null) {
			arg1 = mInflater.inflate(R.layout.newwordsbook_in, null);
			findView(arg1);
			arg1.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) arg1.getTag();
		}
		curPosition = arg0;
		curNewWord = newWordList.get(arg0);
		curEditCond = ediCondList.get(arg0);
		curViewHolder = viewHolder;
		// 初始化控件
		setView(curPosition, curEditCond, curNewWord);
		// 添加监听器
		viewHolder.playAudioButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				playAudio(curNewWord);
			}
				
		});
		return arg1;
	}

	public void findView(View arg1) {
		viewHolder = new ViewHolder();
		viewHolder.deleteImageView = (ImageView) arg1
				.findViewById(R.id.new_words_book_in_isDelete);
		viewHolder.meaningTextView = (TextView) arg1
				.findViewById(R.id.new_words_book_in_word_def);
		viewHolder.playAudioButton = (Button) arg1
				.findViewById(R.id.new_words_book_in_word_speaker);
		viewHolder.porTextView = (TextView) arg1
				.findViewById(R.id.new_words_book_in_word_pron);
		viewHolder.wordNameTextView = (TextView) arg1
				.findViewById(R.id.new_words_book_in_word_key);
		viewHolder.linearLayout = (LinearLayout) arg1
				.findViewById(R.id.ll_new_words_book_in);
	}

	public void setView(int curPosition, EditCond curEditCond,
			NewWord curNewWord) {
		if (curEditCond.switchState == 0) {
			viewHolder.deleteImageView.setVisibility(View.GONE);
		} else {
			viewHolder.deleteImageView.setVisibility(View.VISIBLE);
			if (curEditCond.switchState == 1) {
				viewHolder.deleteImageView.setImageResource(R.drawable.rem_sn);
			} else {
				viewHolder.deleteImageView.setImageResource(R.drawable.rem_sns);
			}
		}
		if (curNewWord.pron != null && curNewWord.pron.length() != 0
				&& !curNewWord.pron.equals("null")) {
			viewHolder.porTextView.setText(Html.fromHtml("["
					+ curNewWord.pron + "]"));
		} else {
			viewHolder.porTextView.setText("");
		}
		viewHolder.wordNameTextView.setText(curNewWord.Word);
		if (curEditCond.explainState) {
			viewHolder.meaningTextView.setText(curNewWord.def);
		} else {
			viewHolder.meaningTextView.setText(R.string.new_words_book_in_tip);
		}
		if (curPosition % 2 == 0) {
			viewHolder.linearLayout.setBackgroundDrawable(mContext
					.getResources()
					.getDrawable(R.drawable.test_list_background));
		} else {
			viewHolder.linearLayout.setBackgroundDrawable(mContext
					.getResources().getDrawable(
							R.drawable.test_list_background1));
		}
	}

	class ViewHolder {
		public TextView wordNameTextView;
		public TextView porTextView;
		public TextView meaningTextView;
		public Button playAudioButton;
		public ImageView deleteImageView;
		public LinearLayout linearLayout;
	}

	public int getState() {
		return state;
	}

	/**
	 * 
	 * 设置收藏文章是否可编辑的状态,并更新删除的状态
	 * 
	 * @param state
	 */
	public void setState(int state) {
		this.state = state;
		if (state == 0) {

			for (int i = 0; i < ediCondList.size(); i++) {
				ediCondList.get(i).switchState = 1;// 变为选择的界面
			}
		} else if (state == 1) {
			// 删除list中选定要删除的数据
			updateData();
			for (int i = 0; i < ediCondList.size(); i++) {
				ediCondList.get(i).switchState = 0;
			}
		}
		notifyDataSetChanged();
	}

	public ArrayList<EditCond> getEdiCondList() {
		return ediCondList;
	}

	public void setEdiCondList(ArrayList<EditCond> ediCondList) {
		this.ediCondList = ediCondList;
	}

	class EditCond {
		public int switchState = 0;// 0不可见，2选中，1未选中
		public boolean explainState = true;
	}

	public void updateView(int position) {
		if (ediCondList.get(position).switchState == 1) {
			ediCondList.get(position).switchState = 2;
		} else if (ediCondList.get(position).switchState == 2) {
			ediCondList.get(position).switchState = 1;
		}
		notifyDataSetChanged();
	}

	/**
	 * 
	 * 更新是否显示单词含义的界面
	 * 
	 * @param position
	 */
	public void updateExplainView(int position) {
		if (!ediCondList.get(position).explainState) {
			ediCondList.get(position).explainState = true;
			playAudio(newWordList.get(position));		
		}
		notifyDataSetChanged();
	}

	/**
	 * 更新单词是否选中的界面
	 * 
	 */

	public void updateData() {
		// 利用Itreator进行操作，保障数据的安全，若根据位置变化进行操作，则要同时改变数据位置的偏移
		Iterator<NewWord> favWordItreIterator = newWordList.iterator();
		Iterator<EditCond> editCondIterator = ediCondList.iterator();
		while (editCondIterator.hasNext()) {
			EditCond editCond = editCondIterator.next();
			NewWord favWord = favWordItreIterator.next();
			if (editCond.switchState == 2) {
				editCondIterator.remove();
				favWordItreIterator.remove();
				helper.deleteNewWord(favWord.Word);//删除单词
			}
		}
	}
	//播放音频
	public void playAudio(final NewWord curNewWord){
		if (player != null) {

			// 查看是否存在本地音频文件，如果没有则联网播放

			final String url = com.iyuba.toeiclistening.Constant.APP_DATA_PATH
					+ Constant.SDCARD_AUDIO_PATH + "/"
					+ Constant.SDCARD_FAVWORD_AUDIO_PATH + "/"
					+ curNewWord.Word + ".mp3";

			File file = new File(url);
			if (file.exists()) {
				player.playUrl(url);
			} else {
				if (curNewWord.audio != null) {
					new Thread(){
						public void run(){
							player.playUrl(curNewWord.audio);
						}
					}.start();
					new DownFile(
						mContext,
						curNewWord.audio,
						com.iyuba.toeiclistening.Constant.APP_DATA_PATH
								+ Constant.SDCARD_AUDIO_PATH
								+ "/"
								+ Constant.SDCARD_FAVWORD_AUDIO_PATH
								+ "/", curNewWord.Word
								+ ".mp3",
						new DownLoadStatueChangeListener() {
	
							@Override
							public void onStartListener(String info) {
								// TODO Auto-generated method stub
								
							}
	
							@Override
							public void onPausedListener(String info) {
								// TODO Auto-generated method stub
	
							}
	
							@Override
							public void onFinishedListener(
									String info) {
								// TODO Auto-generated method stub
								
							}
	
							@Override
							public void onErrorListener(String info) {
								// TODO Auto-generated method stub
	
							}
	
							@Override
							public boolean isPausedListener() {
								// TODO Auto-generated method stub
								return false;
							}
						}, null).startDownLoadFile();
				}
			}
		}
	
	}

}
