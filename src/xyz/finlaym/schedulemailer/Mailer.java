package xyz.finlaym.schedulemailer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.sun.mail.smtp.SMTPTransport;

import xyz.finlaym.schedulemailer.rest.Schedule;
import xyz.finlaym.schedulemailer.rest.ScheduleAPI;
import xyz.finlaym.schedulemailer.rest.Shift;
import xyz.finlaym.schedulemailer.rest.Week;

public class Mailer {
	private static Map<String, Date> lastUpdated = new HashMap<String, Date>();
	private static SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy kk:mm");
	private static SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");

	public static void main(String[] args) throws Exception {
		List<String> ids = new ArrayList<String>();
		Scanner in = new Scanner(new File("ids.txt"));
		while (in.hasNextLine()) {
			String s = in.nextLine();
			if (s.trim().isEmpty())
				continue;
			ids.add(s);
		}
		in.close();
		Properties prop = System.getProperties();
		prop.put("mail.smth.host", "localhost");
		prop.put("mail.smtp.port", "25");
		Session session = Session.getInstance(prop, null);
		while (true) {
			try {
				for (String s : ids) {
					try {
						String[] split = s.split(":", 2);
						Schedule schedule = ScheduleAPI.getSchedule(split[0]);
						if (lastUpdated.get(split[0]) == null
								|| !schedule.getPublishTimestamp().equals(lastUpdated.get(split[0]))) {
							if (schedule == null) {
								System.err.println("Error: Failed to get schedule for badge id " + split[0]);
								continue;
							}
							Message msg = new MimeMessage(session);
							msg.setFrom(new InternetAddress("no-reply@finlaym.xyz"));
							System.out.println("Sending email to " + split[1]);
							msg.setRecipient(Message.RecipientType.TO, new InternetAddress(split[1]));
							msg.setSubject("Schedule:");

							String message = schedule.getFirstName() + " " + schedule.getLastName()
									+ ", your new schedule is ready!\n\n";
							int weekCount = 0;
							for (Week w : schedule.getWeeks()) {
								if (w.getShifts().length != 0) {
									weekCount++;
									message += "Week: " + dateformat.format(w.getStart()) + " - "
											+ dateformat.format(w.getEnd()) + ":\n";
									message += "Hours: " + w.getTotalHours() + "\n\n";
									for (Shift shift : w.getShifts()) {
										String start = format.format(shift.getStart());
										String end = format.format(shift.getEnd());
										String position = shift.getPosition();
										double hours = shift.getNetHours();
										message += "Shift: " + start + " - " + end + "\n";
										message += "Net Hours: " + hours + "\n";
										message += "Position: " + position + "\n";
									}
								}
							}
							if(weekCount == 0) {
								System.err.println("Badge has no scheduled shifts, skipping");
								continue;
							}
							msg.setText(message);
							SMTPTransport t = (SMTPTransport) session.getTransport("smtp");
							t.connect();
							t.sendMessage(msg, msg.getAllRecipients());
							t.close();
							lastUpdated.put(split[0], schedule.getPublishTimestamp());
						}
					} catch (Exception e) {
						e.printStackTrace();
						System.err.println("Error in sending email");
					}
				}

				Thread.sleep(1000 * 60 * 60 * 4);
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Error in sending emails");
			}
		}
	}
}
