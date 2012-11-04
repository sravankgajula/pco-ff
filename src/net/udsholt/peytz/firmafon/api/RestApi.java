package net.udsholt.peytz.firmafon.api;

import java.util.ArrayList;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.udsholt.peytz.firmafon.domain.Reception;

public class RestApi 
{
	public final String defaultBaseUrl = "https://app.firmafon.dk/api/v1";
	
	protected String baseUrl = "";
	protected String appKey  = "";
	protected String userKey = "";

	public RestApi() {
		this.baseUrl = this.defaultBaseUrl;
	}

	public RestApi(final String appKey) {
		this.appKey  = appKey;
		this.baseUrl = this.defaultBaseUrl;
	}
	
	public void setBaseUrl(final String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public void setAppKey(final String appKey) {
		this.appKey = appKey;
	}

	public void setUserKey(final String userKey) {
		this.userKey = userKey;
	}

	public ArrayList<Reception> getPossibleCloakReceptions() throws ApiExpception 
	{
		JSONObject json = this.getJSONResponse(this.getRequestByResource("/receptions"));

		ArrayList<Reception> receptions = new ArrayList<Reception>();
		
		receptions.add(new Reception(0, "Disable", "Disable cloak reception"));
		
		try {
			JSONArray jsonReceptions = json.getJSONArray("receptions");

			for (int i = 0; i < jsonReceptions.length(); i++) {

				JSONObject jsonReception = jsonReceptions.getJSONObject(i);

				Reception reception = new Reception();
				reception.id = jsonReception.getInt("id");
				reception.name = jsonReception.getString("name");
				reception.number = jsonReception.getJSONObject("phone_number").getString("number");

				receptions.add(reception);

			}

		} catch (JSONException e) {
			e.printStackTrace();
			throw new ApiExpception("Unsupported data format");
		}

		return receptions;

	}
	
	public int getCloakReceptionId() throws ApiExpception
	{
		JSONObject json = this.getJSONResponse(this.getRequestByResource("/employee"));
		
		try {
			
			if (!json.isNull("cloak_reception")) {
				return json.getJSONObject("cloak_reception").getInt("id");
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
			throw new ApiExpception("Unsupported data format");
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ApiExpception("Unsupported data format");
		}
		
		return 0;
	}
	
	public boolean setCloakReceptionId(int cloakReceptionId) throws ApiExpception
	{
		JSONObject jsonPostData = new JSONObject();
		
		boolean success = false;
		
		try {
			if (cloakReceptionId == 0) {
				jsonPostData.put("cloak_reception_id", JSONObject.NULL);
			} else {
				jsonPostData.put("cloak_reception_id", cloakReceptionId);
			}
			
			RestRequest request = this.getRequestByResource("/employee");
			request.addHeader("Content-type", "application/json");
			request.setMethod(RestRequest.Method.POST);
			request.setRawPost(jsonPostData.toString());
			
			this.getJSONResponse(request);
			
			success = true;
			
		} catch (JSONException e) {
			e.printStackTrace();
			throw new ApiExpception("Unsupported data format");
		} 
		
		return success;
	}
	

	protected RestRequest getRequestByResource(final String resource) {
		final RestRequest request = new RestRequest(this.baseUrl + resource);
		request.addHeader("Accept", "application/json");
		request.addHeader("Firmafon-App-Key", this.appKey);

		if (this.userKey != "") {
			request.addHeader("Firmafon-User-Key", this.userKey);
		}

		return request;
	}

	protected JSONObject getJSONResponse(final RestRequest request) throws ApiExpception
{
		try {
			request.execute();
		} catch (ConnectTimeoutException e) { 
			e.printStackTrace();
			throw new ApiExpception("Timeout while waiting for api");
		} catch (Exception e) {
			e.printStackTrace();
			throw new ApiExpception("Something went wrong");
		}

		if (request.getResponseCode() == 0 || request.getResponse() == null) {
			throw new ApiExpception("Invalid http response");
		}

		if (request.getResponseCode() == 403) {
			throw new ApiExpception("Invalid credentials");
		}

		JSONObject json = null;

		try {
			json = new JSONObject(request.getResponse());
		} catch (JSONException e) {
			throw new ApiExpception("Invalid json response");
		}

		return json;
	}
}
