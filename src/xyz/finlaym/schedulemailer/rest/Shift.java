package xyz.finlaym.schedulemailer.rest;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.simple.JSONObject;

public class Shift {
	public static SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
	
	private Date date;
	private double grossHours;
	private double netHours;
	private Date start;
	private Date end;
	private String position;
	private String store;
	private JSONObject additionalProperties;

	public Shift(JSONObject parent) throws Exception{
		this.date = format.parse((String)parent.get("date"));
		this.grossHours = (double) parent.get("grossHours");
		this.netHours = (double) parent.get("netHours");
		String[] startDate = ((String) parent.get("startTime")).split(":",2);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.HOUR_OF_DAY, Integer.valueOf(startDate[0]));
		cal.add(Calendar.MINUTE, Integer.valueOf(startDate[1]));
		this.start = cal.getTime();
		String[] endDate = ((String) parent.get("endTime")).split(":",2);
		cal.setTime(date);
		cal.add(Calendar.HOUR_OF_DAY, Integer.valueOf(endDate[0]));
		cal.add(Calendar.MINUTE, Integer.valueOf(endDate[1]));
		this.end = cal.getTime();
		this.position = (String) parent.get("position");
		this.store = (String) parent.get("storeNumber");
		this.additionalProperties = (JSONObject) parent.get("additionalProperties");
	}
	public Date getDate() {
		return date;
	}
	public double getGrossHours() {
		return grossHours;
	}
	public double getNetHours() {
		return netHours;
	}
	public Date getStart() {
		return start;
	}
	public Date getEnd() {
		return end;
	}
	public String getPosition() {
		return position;
	}
	public String getStore() {
		return store;
	}
	public JSONObject getAdditionalProperties() {
		return additionalProperties;
	}
	
}
