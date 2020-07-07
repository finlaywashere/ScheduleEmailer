package xyz.finlaym.schedulemailer.rest;

import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Schedule {
	private Date publishTimestamp;
	private String firstName;
	private String lastName;
	private String store;
	private JSONObject additionalProperties;
	private String userMsgEnglish;
	private String userMsgFrench;
	private Week[] weeks;
	
	public Schedule(String json) throws Exception{
		JSONParser parser = new JSONParser();
		JSONObject obj = (JSONObject) parser.parse(json);
		this.publishTimestamp = Shift.format.parse((String) obj.get("publishTimeStamp"));
		this.firstName = (String) obj.get("firstName");
		this.lastName = (String) obj.get("lastName");
		this.store = (String) obj.get("homeStore");
		this.userMsgEnglish = (String) obj.get("userMsgEn");
		this.userMsgFrench = (String) obj.get("userMsgFr");
		this.additionalProperties = (JSONObject) obj.get("additionalProperties");
		JSONArray weeksArray = (JSONArray) obj.get("weeks");
		this.weeks = new Week[weeksArray.size()];
		for(int i = 0; i < weeks.length; i++) {
			this.weeks[i] = new Week((JSONObject)weeksArray.get(i));
		}
	}

	public Date getPublishTimestamp() {
		return publishTimestamp;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getStore() {
		return store;
	}

	public JSONObject getAdditionalProperties() {
		return additionalProperties;
	}

	public String getUserMsgEnglish() {
		return userMsgEnglish;
	}

	public String getUserMsgFrench() {
		return userMsgFrench;
	}

	public Week[] getWeeks() {
		return weeks;
	}
	
}
