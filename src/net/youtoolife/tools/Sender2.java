package net.youtoolife.tools;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.youtoolife.spamer.view.MainController;


public class Sender2 /*implements Runnable*/ {
	
	
	private MainController main = null;
	public String msg = "";

	//@Override
	public String run(String number, String body) {
		//while (true) {
			
			/*if (!main.isRun())
				return;
			*/
			if (number != null)
				try {
					if (number.equalsIgnoreCase("")||number.isEmpty()||number == null)
					{
						//main.addText("Error! "+pers.name+" - сообщение не отправлено - номер не задан!");
						msg = "Error!- пользователь не добавлен - номер не задан!";
						//main.val0.add(1);
						//continue;
						return msg;
					}
					sendMessage(number, body);
					//Thread.sleep(main.getDelay());
				
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			return msg;
	}
	
	
	public void post2(String url, String param, String number) throws Exception{
		  String charset = "UTF-8"; 
		  URLConnection connection = new URL(url).openConnection();
		  //connection. .setRequestMethod("POST");
		  connection.setDoOutput(true);
		  connection.setRequestProperty("Accept-Charset", charset);
		  connection.setRequestProperty("Content-Type", "application/json;charset=" + charset);

		  param = "d="+param;
		  try (OutputStream output = connection.getOutputStream()) {
		    output.write(param.getBytes(charset));
		  }
		  catch (Exception e) {
			  /*
			  main.addText("Ошибка отправки запроса!"
						+ " Сообщение на номер '"+pers.phone+"' не отправлено!\n ["+e.getMessage()+"]");
						*/
			  msg = "Ошибка!"
						+ " Пользователь '"+number+"' не добавлен!\n ["+e.getMessage()+"]";
		  }

		  String data = "";
		  InputStream iStream = connection.getInputStream();
		  BufferedReader br = new BufferedReader(new InputStreamReader(iStream, "utf8"));
		  StringBuffer sb = new StringBuffer();
		  String line = "";

		  while ((line = br.readLine()) != null) {
		      sb.append(line);
		  }
		  data = sb.toString();
		  
		  System.out.println(data);
		  
		  JsonParser jp = new JsonParser();
		  JsonObject jo = jp.parse(data).getAsJsonObject();
		  if (jo.get("sent").getAsBoolean()) {
			  //main.addText("Messager: ("+pers.name+") Сообщение успешно отпралено на номер: '"+pers.phone+"'");
			  msg = "Messager: Пользователь успешно добавлен: '"+number+"'";
			  main.val0.set(main.val0.get()+1);
		  }
		  else {
			  /*
			  main.addText("Error! "+pers.name+" - "
			  		+ "Сообщение на номер '"+pers.phone+"' не отправлено!\n"
			  				+ "["+jo.get("message").getAsString()+"]");
			  				*/
			  msg = "Error! \n"
				  		+ "Пользователь '"+number+"' не добавлен!\n"
		  				+ "["+jo.get("message").getAsString()+"]";
		  }
		}
	
	private void post(String url2, String param, String number) throws Exception {

		String charset = "UTF-8"; 
		
		System.out.println(url2);
		String url = url2;//"https://selfsolve.apple.com/wcResults.do";
		URL obj = new URL(url);
		//HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		String USER_AGENT = "Mozilla/5.0";
		//add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("Accept-Charset", charset);
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		String urlParameters = "d="+param;//"sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";
		
		con.setRequestProperty( "Content-Length", Integer.toString( param.getBytes(charset).length ));
		
		// Send post request
		con.setDoOutput(true);
		
		
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.write(urlParameters.getBytes(charset));
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		//print result
		//System.out.println(response.toString());
		String data = response.toString();
		  
		  System.out.println(data);
		  
		  JsonParser jp = new JsonParser();
		  JsonObject jo = jp.parse(data).getAsJsonObject();
		  if (jo.get("sent").getAsBoolean()) {
			  //main.addText("Messager: ("+pers.name+") Сообщение успешно отпралено на номер: '"+pers.phone+"'");
			  msg = "Messager: Пользователь успешно добавлен: '"+number+"'";
			  main.val0.set(main.val0.get()+1);
		  }
		  else {
			  /*
			  main.addText("Error! "+pers.name+" - "
			  		+ "Сообщение на номер '"+pers.phone+"' не отправлено!\n"
			  				+ "["+jo.get("message").getAsString()+"]");
			  				*/
			  msg = "Error! \n"
				  		+ "Пользователь '"+number+"' не добавлен!\n"
		  				+ "["+jo.get("message").getAsString()+"]";
		  }

	}
	
	private void sendMessage(String number, String body) {

		/*
		if (main.getToken().equals(""))
			main.setToken("p2hzeqyjtejpsgyz");*/
		String url = main.settings.url
	
				+
				(main.settings.url.charAt(main.settings.url.length()-1) != '/'?"/":"")
				//+"message?token="+main.getToken();
				+"add_user.php";
			
		//String sb = "{\"phone\":\"79315427605\",\"body\":\""+body+"\"}";
		
		 JsonObject rootObject = new JsonObject();
	        rootObject.addProperty("login", number);
	        rootObject.addProperty("pwd", "ytltoor_root683asdf29ggsd123udkywnxtutruernf");
	        Gson gson = new Gson();
	        String json = gson.toJson(rootObject);
	        String d = RSAISA.rsaEncrypt(json);
	        System.out.println("data: "+d);
	      
	        try {
				post(url, d, number);
			} catch (Exception e) {
				/*
				main.addText("Ошибка отправки запроса!"
						+ " Сообщение на номер '"+pers.phone+"' не отправлено!\n ["+e.getMessage()+"]");
						*/
				msg = "Ошибка отправки запроса!"
						+ " Пользователь '"+number+"' не добавлен!\n ["+e.getMessage()+"]";
				e.printStackTrace();
			}
			  

	}
	
	public Sender2(MainController main) {
		this.main = main;
	}

}
