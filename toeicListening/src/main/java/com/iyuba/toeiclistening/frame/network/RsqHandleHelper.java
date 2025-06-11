package com.iyuba.toeiclistening.frame.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Application;
import android.net.ConnectivityManager;
import android.os.Build;

import com.iyuba.configation.RuntimeManager;
import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.frame.protocol.BaseHttpRequest;
import com.iyuba.toeiclistening.frame.protocol.BaseHttpResponse;
import com.iyuba.toeiclistening.frame.protocol.BaseResponse;
import com.iyuba.toeiclistening.frame.protocol.ErrorResponse;
/**
 * 璇锋眰澶勭悊杈呭姪绫?
 * 
 * @author wuwei
 * 
 */
public class RsqHandleHelper {

	private final static boolean needGZip = false;// GZip寮?叧锛岀渷寰楁瘡娆¤鎴寘閮藉緱鍘绘敞閲婏紝骞虫椂涓簍rue锛岄渶瑕佹埅鍖呮椂缃负false

	static BaseResponse getResponseImpl(int rspCookie, BaseHttpRequest request,
			INetStateReceiver stateReceiver) {

		DefaultHttpClient connection = null;
		BaseHttpResponse httpResponse = null;

		HttpUriRequest httpUriRequest = null;

		HttpResponse response = null;

		ErrorResponse err = null;

		try {
			// 璁剧疆杩炴帴
			connection = connectServer(rspCookie, request, stateReceiver);
			// 浼犻?鍙傛暟
			httpUriRequest = buildAndSendRsq(connection, rspCookie, request,
					stateReceiver);
			// 鎵ц锛屽緱鍒拌繑鍥炲?
			response = connection.execute(httpUriRequest);
			// 瑙ｆ瀽杩斿洖缁撴灉
			BaseResponse rsp = recvAndParseRsp(response, rspCookie, request,
					stateReceiver);
			if (rsp instanceof ErrorResponse) {
				return rsp;
			}
			httpResponse = (BaseHttpResponse) rsp;
			return httpResponse;
		} catch (HttpHostConnectException e) {
			e.printStackTrace();
			err = new ErrorResponse(ErrorResponse.ERROR_NET_CONNECTION,
					e.getMessage());

		} catch (ClientProtocolException e) {// Client鍗忚寮傚父
			e.printStackTrace();

			err = new ErrorResponse(ErrorResponse.ERROR_ClLIENT_PROTOCOL,
					e.getMessage());

		} catch (SocketTimeoutException e) {// Socket瓒呮椂寮傚父
			e.printStackTrace();

			err = new ErrorResponse(ErrorResponse.ERROR_NET_TIMEOUT,
					e.getMessage());

		} catch (SocketException e) {// Socket寮傚父
			e.printStackTrace();

			err = new ErrorResponse(ErrorResponse.ERROR_NET_SOCKET,
					e.getMessage());

		} catch (IOException e) {// IO寮傚父
			e.printStackTrace();

			err = new ErrorResponse(ErrorResponse.ERROR_NET_IO, e.getMessage());

		} catch (Exception e) {// 鍏朵粬寮傚父
			e.printStackTrace();

			err = new ErrorResponse(ErrorResponse.ERROR_UNKNOWN, e.getMessage());

		} finally {
			closeConnection(connection);
		}

		if (stateReceiver != null && err != null) {
			stateReceiver.onNetError(request, rspCookie, err);
		}
		return err;

	}

	static private DefaultHttpClient connectServer(int rspCookie,
			BaseHttpRequest request, INetStateReceiver stateReceiver)
			throws IOException {
		DefaultHttpClient connection = null;

		if (stateReceiver != null) {
			stateReceiver.onStartConnect(request, rspCookie);
		}

		HttpParams my_httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(my_httpParams,
				request.getConnectionTimeout());
		HttpConnectionParams.setSoTimeout(my_httpParams,
				request.getConnectionTimeout());

		connection = new DefaultHttpClient(my_httpParams);

		// 濡傛灉鏄娇鐢ㄧ殑杩愯惀鍟嗙綉缁?
		if (NetworkData.getNetworkInfo().getType() == ConnectivityManager.TYPE_MOBILE) {
			// 鑾峰彇榛樿浠ｇ悊涓绘満ip
			String host = android.net.Proxy.getDefaultHost();
			// 鑾峰彇绔彛
			int port = android.net.Proxy.getDefaultPort();
			if (host != null && port != -1) {
				HttpHost proxy = new HttpHost(host, port);
				connection.getParams().setParameter(
						ConnRoutePNames.DEFAULT_PROXY, proxy);
			}
		}

		if (stateReceiver != null) {
			// Log.e("缃戠粶璇锋眰", "寮瑰嚭鐘舵?绐椾綋");
			stateReceiver.onConnected(request, rspCookie);
		}

		return connection;

	}

	static private HttpUriRequest buildAndSendRsq(DefaultHttpClient connection,
			int rspCookie, BaseHttpRequest request,
			INetStateReceiver stateReceiver) throws IOException {

		HttpUriRequest httpUriRequest = null;

		ByteArrayEntity entity;

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		if (stateReceiver != null) {
			stateReceiver.onStartSend(request, rspCookie, -1);
		}

		if (request.getMethod() == BaseHttpRequest.GET) {
			httpUriRequest = new HttpGet(request.getAbsoluteURI());
			if (needGZip) {
				httpUriRequest.addHeader("Accept-Encoding", "gzip");
			}

			if (NetworkData.SMD != null && !NetworkData.SMD.equals("")) {
				httpUriRequest.addHeader("SMD", NetworkData.SMD);
			}

			Application application = new Application();

			String agent = RuntimeManager.getString(R.string.minName) + "/"
					+ RuntimeManager.getString(R.string.minVer) + " "
					+ RuntimeManager.getString(R.string.runEnv_name) + "/"
					+ Build.VERSION.RELEASE;
			httpUriRequest.addHeader("Client-Agent", agent);
			httpUriRequest.addHeader("ParamSet",
					"ssite=" + RuntimeManager.getString(R.string.ssite_code));
		} else {
			HttpPost httpPost = null;

			httpPost = new HttpPost(request.getAbsoluteURI());
			request.fillOutputStream(rspCookie, outputStream, stateReceiver);

			entity = new ByteArrayEntity(outputStream.toByteArray());

			String[][] aryHeaders = request.getExtraHeaders();
			if (aryHeaders != null) {
				int length = aryHeaders.length;
				if (aryHeaders != null) {
					for (int i = 0; i < length; ++i) {
						if (aryHeaders[i].length != 2) {
							throw new IllegalArgumentException(
									"aryheader must be 2 columns!");
						}

						for (int j = 0; j < 2; ++j) {
							if (aryHeaders[i][0].equals("Content-Type")) {
								entity.setContentType(aryHeaders[i][1]);
							}
						}
					}
				}
			}

			httpPost.setEntity(entity);
			if (request.getNeedGZip() && needGZip) {
				httpPost.addHeader("Accept-Encoding", "gzip");
			}
			if (NetworkData.SMD != null && !NetworkData.SMD.equals("")) {
				httpPost.addHeader("SMD", NetworkData.SMD);
			}

			Application application = RuntimeManager.getApplication();

			String agent = application.getString(R.string.minName) + "/"
					+ application.getString(R.string.minVer) + " "
					+ application.getString(R.string.runEnv_name) + "/"
					+ Build.VERSION.RELEASE;
			httpPost.addHeader("Client-Agent", agent);
			httpPost.addHeader("ParamSet",
					"ssite=" + application.getString(R.string.ssite_code));
			httpUriRequest = httpPost;
		}

		if (stateReceiver != null) {
			stateReceiver.onSendFinish(request, rspCookie);
		}

		return httpUriRequest;

	}

	static private BaseResponse recvAndParseRsp(HttpResponse response,
			int rspCookie, BaseHttpRequest request,
			INetStateReceiver stateReceiver) throws IOException {
		BaseHttpResponse httpResponse = null;

		int code = response.getStatusLine().getStatusCode();
		if (code == HttpStatus.SC_OK) {// 200

			request.headers = response.getAllHeaders();

			int len = (int) response.getEntity().getContentLength();

			// 閽堝褰撳墠鍗忚锛岃繑鍥炲唴瀹归暱搴︿笉搴旇涓?,鏁呭嚭鐜版鎯呭喌杩斿洖閿欒
			if (len == 0) {
				return new ErrorResponse(ErrorResponse.ERROR_UNKNOWN);
			}

			if (stateReceiver != null) {
				stateReceiver.onStartRecv(request, rspCookie, len);
			}

			InputStream instream = response.getEntity().getContent();
			Header contentEncoding = response
					.getFirstHeader("Content-Encoding");
			// Log.e("MAP", instream.toString());
			// Log.e("Test", Boolean.toString(contentEncoding != null));
			// Log.e("Test",
			// Boolean.toString(contentEncoding.getValue().equalsIgnoreCase("gzip")));
			if (contentEncoding != null
					&& contentEncoding.getValue().equalsIgnoreCase("gzip")) {

				instream = new GZIPInputStream(instream);
			}

			httpResponse = request.createResponse();
			ErrorResponse err = httpResponse.parseInputStream(rspCookie,
					request, instream, len, stateReceiver);

			if (stateReceiver != null) {
				stateReceiver.onRecvFinish(request, rspCookie);
			}

			if (err != null) {
				httpResponse = null;
				return err;
			}
			return httpResponse;
		} else {
			return new ErrorResponse(ErrorResponse.ERROR_UNKNOWN);
		}

	}

	static private void closeConnection(DefaultHttpClient connection) {
		try {
			if (connection != null)
				connection.getConnectionManager().shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
