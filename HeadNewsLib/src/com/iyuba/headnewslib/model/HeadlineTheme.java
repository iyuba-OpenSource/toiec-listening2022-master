package com.iyuba.headnewslib.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.iyuba.headnewslib.R;

public class HeadlineTheme implements Parcelable{
	public static final String TAG = HeadlineTheme.class.getSimpleName();

	public static HeadlineTheme DEFAULT_THEME = new HeadlineTheme(0xff0077d5, 0xfff5f5f5, 0xff239bf0,
			R.drawable.headnewslib_back_button);

	public int titleBgColor;
	public int titleTextColor;
	public int indicatorColor;
	public int backBtnResId;

	public HeadlineTheme() {

	}

	public HeadlineTheme(int titleBgColor, int titleTextColor, int indicatorColor, int backBtnResId) {
		this.titleBgColor = titleBgColor;
		this.titleTextColor = titleTextColor;
		this.indicatorColor = indicatorColor;
		this.backBtnResId = backBtnResId;
	}
	
	protected HeadlineTheme(Parcel in){
		titleBgColor = in.readInt();
		titleTextColor = in.readInt();
		indicatorColor = in.readInt();
		backBtnResId = in.readInt();
	}
	
	public static final Creator<HeadlineTheme> CREATOR = new Creator<HeadlineTheme>() {
		@Override
		public HeadlineTheme createFromParcel(Parcel in) {
			return new HeadlineTheme(in);
		}

		@Override
		public HeadlineTheme[] newArray(int size) {
			return new HeadlineTheme[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(titleBgColor);
		dest.writeInt(titleTextColor);
		dest.writeInt(indicatorColor);
		dest.writeInt(backBtnResId);
	}

}
