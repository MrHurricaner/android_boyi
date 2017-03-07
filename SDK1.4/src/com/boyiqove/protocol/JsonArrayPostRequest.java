package com.boyiqove.protocol;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import com.boyiqove.library.volley.AuthFailureError;
import com.boyiqove.library.volley.NetworkResponse;
import com.boyiqove.library.volley.ParseError;
import com.boyiqove.library.volley.Request;
import com.boyiqove.library.volley.Response;
import com.boyiqove.library.volley.Response.ErrorListener;
import com.boyiqove.library.volley.Response.Listener;
import com.boyiqove.library.volley.toolbox.HttpHeaderParser;

public  class JsonArrayPostRequest extends Request<JSONArray>{
	private Map<String,String> mMap;
	private Listener<JSONArray>  mListener;


	public JsonArrayPostRequest(String url,Listener<JSONArray> listener, ErrorListener errorListener, Map<String, String> map) {
		super(Request.Method.POST, url, errorListener);
		mListener = listener;
		mMap = map;

		// TODO Auto-generated constructor stub
	}
	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		// TODO Auto-generated method stub
		return mMap;
	}

	@Override
	protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
		try {
			String jsonString =
					new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			return Response.success(new JSONArray(jsonString),
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JSONException je) {
			return Response.error(new ParseError(je));
		}
	}

	@Override
	protected void deliverResponse(JSONArray response) {
		mListener.onResponse(response);

	}

}