package mysqlWebService;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.omg.CORBA.portable.InputStream;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.mysql.jdbc.PreparedStatement;

public class SimpleJob implements Job {
	
	final String API_KEY = "AIzaSyCfJd1yfGnx5GOy-ojKbmHgrVphW1OR1Uw";
	public String nomUser = null, regID = null, nomPract = null;
	public int pract = 0, usuari = 0;
	public long time, lasttime;
	public double nota = 0;
	
	public void execute(JobExecutionContext ctx) throws JobExecutionException {
		
		System.out.printf(new Locale("es", "MX"), "%tc Ejecutando tarea...%n", new java.util.Date());

		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://campusvirtual-tfg-uab.cat.mialias.net/233932moodle_220160510203715";
		String user = "mycam8326";
		String password = "sDx4XJqk";
		
		Statement sta = null;
		Statement sta2 = null;
		Connection con = null;
		ResultSet res = null;
		ResultSet res2 = null;
		
		try {			
			time = (System.currentTimeMillis()/1000L)-100;
			//System.out.println("TEMPS MAQUINA: "+time);
			Class.forName(driver).newInstance();
			con = DriverManager.getConnection(url, user, password);
			sta = con.createStatement();
			sta2 = con.createStatement();
			res = sta.executeQuery("SELECT usuari, grade, pract, temps FROM app_tfg_prac");
			
			while (res.next()) {
				if(res.isLast()){
					res2 = sta2.executeQuery("SELECT temps FROM app_tfg_prac");
					while (res2.next()){
						lasttime = res2.getLong(1);
					}
					//System.out.println("TEMPS BBDD: "+lasttime);
					if (lasttime>time){
						lasttime = time;
						usuari = Integer.parseInt(res.getString(1));
						nota = res.getDouble(2);
						pract = Integer.parseInt(res.getString(3));
						
						java.sql.PreparedStatement pCmd = con.prepareStatement("SELECT username FROM mdl_user WHERE id=?");
						pCmd.setInt(1, usuari);
						res = pCmd.executeQuery();
						while (res.next()){
							nomUser = res.getString(1);
						}
						java.sql.PreparedStatement pCmd3 = con.prepareStatement("SELECT device FROM app_tfg_user WHERE usuari=?");
						pCmd3.setString(1, nomUser);
						res = pCmd3.executeQuery();
						while (res.next()){
							regID = res.getString(1);
						}
						java.sql.PreparedStatement pCmd2 = con.prepareStatement("SELECT itemname FROM mdl_grade_items WHERE id=?");
						pCmd2.setInt(1, pract);
						res = pCmd2.executeQuery();
						while (res.next()){
							nomPract = res.getString(1);
						}
				        POST2GCM.post(API_KEY, regID, nomPract, nota);
					}					
				}
			}
			sta.close();
			con.close();
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}
}