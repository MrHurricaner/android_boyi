package com.boyiqove.protocol;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import android.text.TextUtils;

import com.boyiqove.library.volley.NetworkResponse;
import com.boyiqove.library.volley.Request;
import com.boyiqove.library.volley.Response;
import com.boyiqove.library.volley.Response.ErrorListener;
import com.boyiqove.library.volley.Response.Listener;
import com.boyiqove.library.volley.toolbox.HttpHeaderParser;
import com.boyiqove.library.volley.toolbox.ProgressListener;

/**
 * A canned request for retrieving the response body at a given URL as a String.
 */
public class DownloadRequest extends Request<String> implements ProgressListener {
	private final Listener<String> mListener;
	private final String mDownloadPath;
	private ProgressListener mProgressListener;
    
	
	/**
	 * Creates a new request with the given method.
	 *
	 * @param method the request {@link Method} to use
	 * @param url URL to fetch the string at
	 * @param download_apth path to save the file to
	 * @param listener Listener to receive the String response
	 * @param errorListener Error listener, or null to ignore errors
	 */
	public DownloadRequest(String url, String download_path, Listener<String> listener,
			ErrorListener errorListener) {
		super(Method.GET, url, errorListener);
		mDownloadPath = download_path;
		mListener = listener;
	}
	public void setOnProgressListener(ProgressListener listener){
		mProgressListener = listener;
	}
	@Override
	protected void deliverResponse(String response) {
		if(null != mListener){
			mListener.onResponse(response);
		}
	}
    
	@Override
	protected Response<String> parseNetworkResponse(NetworkResponse response) {
		String parsed = null;
		try {
			byte[] data = response.data;
			//convert array of bytes into file
			FileOutputStream fileOuputStream = new FileOutputStream(mDownloadPath); 
			fileOuputStream.write(data);
			fileOuputStream.close();
			parsed = mDownloadPath;
            
		} catch (UnsupportedEncodingException e) {
			parsed = new String(response.data);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}  catch (IOException e) {
			e.printStackTrace();
		} finally{
			if(TextUtils.isEmpty(parsed)){
				parsed = "";
			}
		}
		return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
	}
	@Override
	public void onProgress(long transferredBytes, long totalSize) {
		if(null != mProgressListener){
			mProgressListener.onProgress(transferredBytes, totalSize);
		}
	}
}

