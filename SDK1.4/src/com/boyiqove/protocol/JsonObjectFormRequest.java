package com.boyiqove.protocol;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;


import com.boyiqove.library.volley.NetworkResponse;
import com.boyiqove.library.volley.ParseError;
import com.boyiqove.library.volley.Response;
import com.boyiqove.library.volley.Response.ErrorListener;
import com.boyiqove.library.volley.Response.Listener;
import com.boyiqove.library.volley.toolbox.HttpHeaderParser;

public class JsonObjectFormRequest extends FormRequest<JSONObject> {
    
	public JsonObjectFormRequest(String url, Listener<JSONObject> listener,
			ErrorListener errorListener, Map<String, String> map, FormFile[] files) {
		super(url, listener, errorListener, map, files);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
		// TODO Auto-generated method stub
		try {
			String jsonString =
					new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			return Response.success(new JSONObject(jsonString),
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JSONException je) {
			return Response.error(new ParseError(je));
		}

	}


}
