package com.boyiqove.protocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.boyiqove.library.volley.NetworkResponse;
import com.boyiqove.library.volley.Request;
import com.boyiqove.library.volley.Response;
import com.boyiqove.library.volley.Request.Method;
import com.boyiqove.library.volley.Response.ErrorListener;
import com.boyiqove.library.volley.Response.Listener;
import com.boyiqove.library.volley.toolbox.HttpHeaderParser;
import com.boyiqove.util.AES;

public class ChapterRequest extends Request<String> {

	 private Listener<String> mListener;
     
     private String mPathFile;

	    /**
	     * Creates a new request with the given method.
	     *
	     * @param method the request {@link Method} to use
	     * @param url URL to fetch the string at
	     * @param listener Listener to receive the String response
	     * @param errorListener Error listener, or null to ignore errors
	     */
	    public ChapterRequest(int method, String url, Listener<String> listener,
	            ErrorListener errorListener, String pathFile) {
	        super(method, url, errorListener);
	        mListener = listener;
            mPathFile = pathFile;
	    }

	    /**
	     * Creates a new GET request.
	     *
	     * @param url URL to fetch the string at
	     * @param listener Listener to receive the String response
	     * @param errorListener Error listener, or null to ignore errors
	     */
	    public ChapterRequest(String url, Listener<String> listener, ErrorListener errorListener, String pathFile) {
	        this(Method.GET, url, listener, errorListener, pathFile);
	    }

	    @Override
	    protected void deliverResponse(String response) {
	        mListener.onResponse(response);
	    }

	    @Override
	    protected Response<String> parseNetworkResponse(NetworkResponse response) {
	        String parsed = null;
          	ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(response.data));
            try {
            	ZipEntry ze;
                while((ze = zis.getNextEntry()) != null) {
                	ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int count;
                    while((count = zis.read(buffer)) != -1) {
                        baos.write(buffer, 0, count);
                    }
                    byte[] bytes = baos.toByteArray();
                    
                    // save as local cache
                    FileOutputStream fos = new FileOutputStream(mPathFile);
                    fos.write(bytes);
                    fos.close();
                    
                    parsed = AES.decrypt(bytes, HttpHeaderParser.parseCharset(response.headers));
                    
                    break;
                }

            } catch (IOException e) {
            	// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
                try {
					zis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            
	        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
	    }
}
