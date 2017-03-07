package com.boyiqove.protocol;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.boyiqove.library.volley.AuthFailureError;
import com.boyiqove.library.volley.NetworkResponse;
import com.boyiqove.library.volley.ParseError;
import com.boyiqove.library.volley.Request;
import com.boyiqove.library.volley.Response;
import com.boyiqove.library.volley.Response.ErrorListener;
import com.boyiqove.library.volley.Response.Listener;
import com.boyiqove.library.volley.toolbox.HttpHeaderParser;

public  class JsonObjectPostRequest extends Request<JSONObject>{
	private Map<String,String> mMap;
	private Listener<JSONObject>  mListener;


	public JsonObjectPostRequest(String url,Listener<JSONObject> listener, ErrorListener errorListener, Map<String, String> map) {
		super(Request.Method.POST, url, errorListener);
		mListener = listener;
		mMap = map;

	}
    
	@Override
	protected Map<String, String> getParams() throws AuthFailureError {

		return mMap;
	}

	@Override
	protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
		try {
			String jsonString =
					new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            
//			JSONObject obj = new JSONObject(jsonString);
//            int status = obj.getInt("status");
//            switch (status) {
//			case StatusCode.OK:
//				break;
//			case StatusCode.NO_DATA:
//				break;
//			case StatusCode.AUTH_FAILURE:
//                break;
//			case StatusCode.PARM_ERROR:
//				break;
//
//			default:
//				break;
//			}
            
			return Response.success(new JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response));
            
		} catch (UnsupportedEncodingException e) {
            
			return Response.error(new ParseError(e));
		} catch (JSONException je) {
            
			return Response.error(new ParseError(je));
		}
	}

	@Override
	protected void deliverResponse(JSONObject response) {
		mListener.onResponse(response);
	}
}
