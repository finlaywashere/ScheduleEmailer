package xyz.finlaym.schedulemailer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.sun.mail.smtp.SMTPTransport;

import xyz.finlaym.schedulemailer.db.DBInterface;
import xyz.finlaym.schedulemailer.rest.Schedule;
import xyz.finlaym.schedulemailer.rest.ScheduleAPI;
import xyz.finlaym.schedulemailer.rest.Shift;
import xyz.finlaym.schedulemailer.rest.Week;

public class Mailer {
	
	private static SimpleDateFormat format = new SimpleDateFormat("E (dd/MM/yyyy)");
	private static SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
	private static SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");

	public static void main(String[] args) throws Exception {
		DBInterface dbInt = new DBInterface();
		dbInt.init("schedule_mailer","mailer","mailer");
		Properties prop = System.getProperties();
		prop.put("mail.smth.host", "localhost");
		prop.put("mail.smtp.port", "25");
		Session session = Session.getInstance(prop, null);
		while (true) {
			try {
				ArrayList<String[]> ids = dbInt.getIDPairs();
				for (String[] split : ids) {
					try {
						Schedule schedule = ScheduleAPI.getSchedule(split[0]);
						Date lastUpdated = dbInt.getLastUpdated(split[0]);
						if (lastUpdated == null
								|| !schedule.getPublishTimestamp().equals(lastUpdated)) {
							if (schedule == null) {
								System.err.println("Error: Failed to get schedule for badge id " + split[0]);
								continue;
							}
							Message msg = new MimeMessage(session);
							msg.setFrom(new InternetAddress("no-reply@finlaym.xyz"));
							System.out.println("Sending email to " + split[1]);
							msg.setRecipient(Message.RecipientType.TO, new InternetAddress(split[1]));
							msg.setSubject("Schedule:");
							
							String firstName = schedule.getFirstName().substring(0,1).toUpperCase()+schedule.getFirstName().substring(1).toLowerCase();
							String lastName = schedule.getLastName().substring(0,1).toUpperCase()+schedule.getLastName().substring(1).toLowerCase();
							
							String message = firstName + " " + lastName + ", your new schedule is ready!";
							int weekCount = 0;
							for (Week w : schedule.getWeeks()) {
								if (w.getShifts().length != 0) {
									weekCount++;
									message += "\n\nWeek: " + dateformat.format(w.getStart()) + " - "
											+ dateformat.format(w.getEnd()) + ":\n";
									message += "Total Hours: " + w.getTotalHours() + "\n\n";
									for (Shift shift : w.getShifts()) {
										String day = format.format(shift.getStart());
										String startTime = timeFormat.format(shift.getStart());
										String endTime = timeFormat.format(shift.getEnd());
										String position = shift.getPosition();
										double hours = shift.getNetHours();
										message += "Shift " + day + ": " + startTime + " - " + endTime + "\n";
										message += "Net Hours: " + hours + "\n";
										message += "Position: " + position + "\n\n";
									}
								}
							}
							if(weekCount == 0) {
								System.err.println("Badge has no scheduled shifts, skipping");
								continue;
							}
							message += "\nDisclaimer: I don't guarantee this information is accurate, its pulled from the online schedule. I am not responsible for any missed shifts";
							msg.setText(message);

							dbInt.setLastUpdated(split[0], schedule.getPublishTimestamp());
							SMTPTransport t = (SMTPTransport) session.getTransport("smtp");
							t.connect();
							t.sendMessage(msg, msg.getAllRecipients());
							t.close();
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
