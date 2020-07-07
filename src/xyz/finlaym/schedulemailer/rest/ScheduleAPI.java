package xyz.finlaym.schedulemailer.rest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ScheduleAPI {
	public static Schedule getSchedule(String identifier) throws Exception {
		URL url = new URL(
				"https://stas.loblaw.ca/lcl-employeeschedule-services/api/rest/v1/services/en/employeeschedules/"
						+ identifier);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		con.setConnectTimeout(5000);
		con.setReadTimeout(5000);

		int status = con.getResponseCode();
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer content = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			content.append(inputLine);
		}
		in.close();
		if(status != 200)
			return null;
		return new Schedule(content.toString());
	}
}
