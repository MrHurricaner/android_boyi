/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.boyiqove.protocol;

import com.boyiqove.library.volley.AuthFailureError;
import com.boyiqove.library.volley.NetworkResponse;
import com.boyiqove.library.volley.Request;
import com.boyiqove.library.volley.Response;
import com.boyiqove.library.volley.Response.ErrorListener;
import com.boyiqove.library.volley.Response.Listener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public abstract class FormRequest<T> extends Request<T> {
    protected final static String TWO_HYPENS = "--";
    protected final static String BOUNDARY = "****WebKitFormBoundaryGKCBY7hqkdata";
    protected final static String LINE_END = System.getProperty("line.separator");
	
    private final Listener<T> mListener;
    private Map<String, String>  mParamMap;
    private FormFile[] mFiles;
    
    public FormRequest(String url, Listener<T> listener, ErrorListener errorListener, 
    		Map<String, String> map, FormFile[] files) {
        super(Method.POST, url, errorListener);
        mListener = listener;
        mParamMap = map;
        mFiles = files;
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }
    
    
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
		// TODO Auto-generated method stub
		//return super.getParams();
        return mParamMap;
	}
    
    protected FormFile[] getFormFiles() {
    	return mFiles;
    }

    @Override
    abstract protected Response<T> parseNetworkResponse(NetworkResponse response);

    
    /**
     * @deprecated Use {@link #getBodyContentType()}.
     */
    @Override
    public String getPostBodyContentType() {
        return getBodyContentType();
    }

    /**
     * @throws AuthFailureError 
     * @deprecated Use {@link #getBody()}.
     */
    @Override
    public byte[] getPostBody() throws AuthFailureError {
        return getBody();
    }


	@Override
	public String getBodyContentType() {
		// TODO Auto-generated method stub
		//return super.getBodyContentType();
        return "multipart/form-data; boundary=" + BOUNDARY;
	}

    @Override
	public byte[] getBody() throws AuthFailureError {
		// TODO Auto-generated method stub
		//return super.getBody();
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();  

    	try {
    		Map<String, String> params = getParams();

    		if (params != null && params.size() > 0) {
    			baos.write(encodeParameters(params));
    		}

    		FormFile[] files = getFormFiles();
    		if(files != null && files.length > 0) {
    			encodeFormFiles(files, baos);
    		}

    		if(baos.size() > 0) {
    			String end = TWO_HYPENS + BOUNDARY + TWO_HYPENS + LINE_END;
    			baos.write(end.getBytes());
    			return baos.toByteArray();
    		} else {
    			return null;
    		}
            
    	} catch (IOException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    		return null;
    	}

    }
    
    
    private byte[] encodeParameters(Map<String, String> params) {
    	StringBuilder encodedParams = new StringBuilder();

    	for (Map.Entry<String, String> entry : params.entrySet()) {
    		encodedParams.append(TWO_HYPENS + BOUNDARY + LINE_END);
    		encodedParams.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINE_END);
    		encodedParams.append(LINE_END);
    		encodedParams.append(entry.getValue() + LINE_END);
    	}
    	//DebugLog.d("formRequest", encodedParams.toString());

        try {
			return encodedParams.toString().getBytes(getParamsEncoding());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			return encodedParams.toString().getBytes();
		}
    }
    
    private void encodeFormFiles(FormFile[] files, OutputStream os) throws IOException {
    	for(FormFile file : files){  
            StringBuilder split = new StringBuilder();  
            split.append(TWO_HYPENS);  
            split.append(BOUNDARY);  
            split.append(LINE_END);  
            split.append("Content-Disposition: form-data;name=\""+ file.getFormname()+ "\";filename=\"" + file.getFilname() + "\"" + LINE_END);  
            split.append("Content-Type: "+ file.getContentType() + LINE_END + LINE_END);  
            
            os.write(split.toString().getBytes());  
            os.write(file.getData(), 0, file.getData().length);  
            os.write(LINE_END.getBytes());  
    	}
    }
}
