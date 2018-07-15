package net.youtoolife.tools;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.youtoolife.spamer.view.MainController;



public class Sender /*implements Runnable*/ {
	
	
	private MainController main = null;
	public String msg = "";
	
	
	int size = 0;
	
	private byte[] blob;
	private String fileName;
	private ArrayList<String> pers = null;
	//@Override
	public String run(ArrayList<String> pers, String body, String fileName, byte[] blob) {
		
		//while (true) {
		
		
		msg = "";
			
		this.blob = blob;
		this.fileName = fileName;
		
		System.out.println("Hi sender  ");
		if (pers != null)
			this.pers = new ArrayList<>(pers);
			if (this.pers != null && this.pers.size() > 0 &&
					(body != null && !body.isEmpty() || this.fileName != null && !this.fileName.isEmpty())) {
				try {
					//if (pers.phone.equalsIgnoreCase("")||pers.phone.isEmpty()||pers.phone == null)
					//{
						//main.addText("Error! "+pers.name+" - сообщение не отправлено - номер не задан!");
						//msg = "!!!"+getDate()+" Для "+pers.name+" не задан номер. Сообщение не отправлено!";
						//main.val0.add(1);
						//continue;
					size+= this.pers.size();
					//System.out.println("____========================================___size: "+size);
					
					//System.out.println("__1__1");
					//System.out.println("Sender: "+this.pers.size());
						//return msg;
					//}
					sendMessage(body);
					//Thread.sleep(main.getDelay());
				
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					msg = "Error! 0xF2\n"+e.getMessage() + msg;
				}
			}
			else
			if (this.fileName != null && !this.fileName.isEmpty() && this.blob != null)
			{
				System.out.println("UPLOADING img...");
				sendImg();
			}
				
			return msg;
	}
	
	private void sendImg() {
		String url = main.settings.url+
				(main.settings.url.charAt(main.settings.url.length()-1) != '/'?"/":"")
				//+"message?token="+main.getToken();
				+"send_img.php";
		//String sb = "{\"phone\":\"79315427605\",\"body\":\""+body+"\"}";
		
		 JsonObject rootObject = new JsonObject();
	        rootObject.addProperty("pwd", "ytltoor_root683asdf29ggsd123udkywnxtutruernf");
	        //rootObject.addProperty("img", img);
	       
	        //----BLOB----//
	        rootObject.addProperty("h", this.fileName);
	        
	        Gson gson = new Gson();
	        String json = gson.toJson(rootObject);
	        String d = RSAISA.rsaEncrypt(json);
	     
	        	        
	        int n = this.blob.length;
	    	int offset = 0;
	    	int ind = 0;
	    	while (offset < n) {
	    		System.out.println("POK: ");
	    		int to = offset + 312*1024;
	    		if (to > n)
	    			to = n;
	    		
	    		byte[] blob0 = Arrays.copyOfRange(this.blob, offset, to);
	    	
			String sblob = Base64.getEncoder().encodeToString(blob0);	
	        try {
	        	
	        		postImg(url, "i="+ind+"&d="+d+"&b="+sblob, ind);
			} catch (Exception e) {
				/*
				main.addText("Ошибка отправки запроса!"
						+ " Сообщение на номер '"+pers.phone+"' не отправлено!\n ["+e.getMessage()+"]");
						*/
				msg = "Ошибка отправки изображения!"
						+ " Фрагмент index = '"+String.valueOf(ind)+"' не отправлен!\n ["+e.getMessage()+"]";
				e.printStackTrace();
			}
	    	offset = to;
	    	ind++;
	    	try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	}
	}
	
	private String getDate() {
		  Date dateNow = new Date();
	      SimpleDateFormat formatForDateNow = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	      return formatForDateNow.format(dateNow);
	}
	
	public void post(String url, String param) throws Exception{
		  String charset = "UTF-8"; 
		  URLConnection connection = new URL(url).openConnection();
		  connection.setDoOutput(true);
		  connection.setRequestProperty("Accept-Charset", charset);
		  //connection.setRequestProperty("Content-Type", "application/json;charset=" + charset);
		  connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
		  connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		  
		  String urlParameters = param;//"sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";
		  
		  //System.out.println("len: "+param.length()/1024);
			
		  connection.setRequestProperty( "Content-Length", Integer.toString(urlParameters.getBytes(charset).length ));

		  try (OutputStream output = connection.getOutputStream()) {
		    output.write(urlParameters.getBytes(charset));
		  }
		  catch (Exception e) {
			  /*
			  main.addText("Ошибка отправки запроса!"
						+ " Сообщение на номер '"+pers.phone+"' не отправлено!\n ["+e.getMessage()+"]");
						*/
			  msg = "Error! 0xF2\nОшибка отправки запроса!\n";
				for (String per:pers)
					msg	+= " Сообщение на номер '"+per+"' не отправлено!\n";
				msg+="["+e.getLocalizedMessage()+"\n"+e.getMessage()+"]\n";
				e.printStackTrace();
				return;
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
		  try {
		  JsonObject jo = jp.parse(data).getAsJsonObject();
		  if (jo.get("sent").getAsBoolean()) {
			  //main.addText("Messager: ("+pers.name+") Сообщение успешно отпралено на номер: '"+pers.phone+"'");
			  for (String per:pers) {
					msg	+=  getDate()+" Сообщение успешно отпралено на номер: '"+per+"' \n";	
					main.val0.set(main.val0.get()+1);
			  }
		  }
		  else {
			  /*
			  main.addText("Error! "+pers.name+" - "
			  		+ "Сообщение на номер '"+pers.phone+"' не отправлено!\n"
			  				+ "["+jo.get("message").getAsString()+"]");
			  				*/
			  msg = "Error!\n";
				for (String per:pers)
					msg	+= "Сообщение на номер '"+per+"' не отправлено!\n";		
		  	msg+="["+jo.get("message").getAsString()+"]";
		  }
		  } catch (Exception e) {
			  msg = "Error! 0xF2\n";
				for (String per:pers)
					msg	+= "Сообщение на номер '"+per+"' не отправлено!\n";		
		  	msg+="["+e.getMessage()+"]";
		  	msg+="{"+data+"}";
		  	e.printStackTrace();
		  	
		}
		}
	
	public void postImg(String url, String param, int index) throws Exception{
		  String charset = "UTF-8"; 
		  URLConnection connection = new URL(url).openConnection();
		  connection.setDoOutput(true);
		  connection.setRequestProperty("Accept-Charset", charset);
		  //connection.setRequestProperty("Content-Type", "application/json;charset=" + charset);
		  connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
		  connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		  
		  connection.setRequestProperty( "Content-Length", Integer.toString(param.getBytes(charset).length ));

		  try (OutputStream output = connection.getOutputStream()) {
		    output.write(param.getBytes(charset));
		  }
		  catch (Exception e) {
			  e.printStackTrace();
			  /*
			  main.addText("Ошибка отправки запроса!"
						+ " Сообщение на номер '"+pers.phone+"' не отправлено!\n ["+e.getMessage()+"]");
						*/
			  msg = "Ошибка отправки изображения!\n";
			  msg += "Part: "+index+"\n";
				msg+="["+e.getMessage()+"]";
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
			  msg += "Part: "+index+" has been uploaded!\n";
		  }
		  else {
			  /*
			  main.addText("Error! "+pers.name+" - "
			  		+ "Сообщение на номер '"+pers.phone+"' не отправлено!\n"
			  				+ "["+jo.get("message").getAsString()+"]");
			  				*/
			  msg = "Ошибка отправки изображения!\n";
			  msg += "Part: "+index+"\n";
		  	msg+="["+jo.get("message").getAsString()+"]";
		  }
		}
	

	
	private void sendMessage(String body) {
		
		//String head = "SMS-INFO ГГКЭИТ "; 
		String head = main.settings.title;//"SMS-INFO Медколледж ";

		//if (main.getToken().equals(""))
		//	main.setToken("ybh3buw58y");
		String url = main.settings.url+
				(main.settings.url.charAt(main.settings.url.length()-1) != '/'?"/":"")
				+"send_msg2.php";	
		//String sb = "{\"phone\":\"79315427605\",\"body\":\""+body+"\"}";
		
		
		JsonObject rootObject = new JsonObject();
		JsonObject rootObject2 = new JsonObject();
		
		if (this.fileName != null && !this.fileName.isEmpty()) {
			rootObject2.addProperty("h", this.fileName);
			System.out.println("IMG: "+rootObject2.get("h").getAsString());
		}
		else {
			System.out.println("IMG: NO");
		}
		
		JsonArray jarr = new JsonArray();
		for (String per:pers) {
			JsonObject rootObject3 = new JsonObject();
			rootObject3.addProperty(per, body);
			jarr.add(rootObject3);
		}
		rootObject2.add("msgs", jarr);

        
        rootObject.addProperty("pwd", "ytltoor_root683asdf29ggsd123udkywnxtutruernf");
        rootObject.addProperty("t", head);
        
        //rootObject.addProperty("img", img);
        
        Gson gson = new Gson();
        String json = gson.toJson(rootObject);
        String json2 = gson.toJson(rootObject2);

        String d = RSAISA.rsaEncrypt(json);
        byte[] b2 = null;
		try {
			b2 = json2.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String dd = Base64.getEncoder().encodeToString(b2);
		

        try{
        		post(url, "d="+d+"&dd="+dd);
        	
		} catch (Exception e) {
			/*
			main.addText("Ошибка отправки запроса!"
					+ " Сообщение на номер '"+pers.phone+"' не отправлено!\n ["+e.getMessage()+"]");
					*/
			msg = "Error! 0xF2\nОшибка отправки запроса!\n";
			for (String per:pers)
				msg	+= " Сообщение на номер '"+per+"' не отправлено!\n";
			msg+="["+e.getLocalizedMessage()+"\n"+e.getMessage()+"]\n";
			System.out.println("__e_");
			e.printStackTrace();
		}
			  

	}
	
	public Sender(MainController main) {
		this.main = main;
	}

}
