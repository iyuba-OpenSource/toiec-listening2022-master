package com.iyuba.core.common.network;

public interface Shareable {
	String getShareUrl(String appName);
	String getShareImageUrl();
	String getShareAudioUrl();
	String getShareTitle();
	String getShareLongText();
	String getShareShortText();
}
