package xyz.finlaym.schedulemailer.rest;

import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Week {
	private Date start;
	private Date end;
	private double totalHours;
	private JSONObject additionalProperties;
	private Shift[] shifts;
	public Week(JSONObject parent) throws Exception{
		this.start = Shift.format.parse((String) parent.get("weekStart"));
		this.end = Shift.format.parse((String) parent.get("weekEnd"));
		Object o = parent.get("totalHoursScheduled");
		this.totalHours = (o != null ? (double) o : 0);
		this.additionalProperties = (JSONObject) parent.get("additionalProperties");
		JSONArray shiftArray = (JSONArray) parent.get("shifts");
		this.shifts = new Shift[shiftArray.size()];
		for(int i = 0; i < this.shifts.length; i++) {
			this.shifts[i] = new Shift((JSONObject) shiftArray.get(i));
		}
	}
	public Date getStart() {
		return start;
	}
	public Date getEnd() {
		return end;
	}
	public double getTotalHours() {
		return totalHours;
	}
	public JSONObject getAdditionalProperties() {
		return additionalProperties;
	}
	public Shift[] getShifts() {
		return shifts;
	}
	
}
