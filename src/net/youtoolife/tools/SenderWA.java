package net.youtoolife.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.youtoolife.spamer.view.MainController;


public class SenderWA {
	
	
	private MainController main = null;
	public String msg = "";
	
	
	private byte[] blob;
	private String fileName;
	
	//@Override
	public String run(String number, String body, String fileName, byte[] blob) {
		//while (true) {
			
			/*if (!main.isRun())
				return;
			*/
			if (number != null)
				try {
					if (number.equalsIgnoreCase("")||number.isEmpty()||number == null)
					{
						//main.addText("Error! "+pers.name+" - сообщение не отправлено - номер не задан!");
						msg = "Error!- сообщение не отправлено - номер не задан!";
						//main.val0.add(1);
						//continue;
						return msg;
					}
					this.blob = blob;
					this.fileName = fileName;
					if (number.equalsIgnoreCase("777"))
						sendImg();
					else
						sendMessage(number, body);
					//Thread.sleep(main.getDelay());
				
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			return msg;
	}
	
	
	public void post(String url, String param, String number) throws Exception{
		
		  String charset = "UTF-8"; 
		  URLConnection connection = new URL(url).openConnection();
		  connection.setDoOutput(true);
		  connection.setRequestProperty("Accept-Charset", charset);
		  connection.setRequestProperty("Content-Type", "application/json;charset=" + charset);
		  //connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
		  connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		  
		  String urlParameters = param;
		  
		  System.out.println("Message size: "+ (param.length()>1024? 
				  (param.length()/1024)+"kbytes" : param.length()+"bytes"));
			
		  
		  connection.setRequestProperty( "Content-Length", 
				  Integer.toString(param.length()));
				  

		  try (OutputStream output = connection.getOutputStream()) {
		    output.write(urlParameters.getBytes(charset));
		  }
		  catch (Exception e) {
			  /*
			  main.addText("Ошибка отправки запроса!"
						+ " Сообщение на номер '"+pers.phone+"' не отправлено!\n ["+e.getMessage()+"]");
						*/
			  msg = "Ошибка отправки запроса!"
						+ " Сообщение на номер '"+number+"' не отправлено!\n ["+e.getMessage()+"]";
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
			  msg = "Messager: Сообщение успешно отпралено на номер: '"+number+"'";
			  main.val0.set(main.val0.get()+1);
		  }
		  else {
			  /*
			  main.addText("Error! "+pers.name+" - "
			  		+ "Сообщение на номер '"+pers.phone+"' не отправлено!\n"
			  				+ "["+jo.get("message").getAsString()+"]");
			  				*/
			  msg = "Error! \n"
				  		+ "Сообщение на номер '"+number+"' не отправлено!\n"
		  				+ "["+jo.get("message").getAsString()+"]";
		  }
		}
	
	@SuppressWarnings("static-access")
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
	        rootObject.addProperty("h", fileName);
	        
	        Gson gson = new Gson();
	        String json = gson.toJson(rootObject);
	        String d = RSAISA.rsaEncrypt(json);
	     
	        	        
	        int n = blob.length;
	    	int offset = 0;
	    	int ind = 0;
	    	while (offset < n) {
	    		System.out.println("POK: ");
	    		int to = offset + 312*1024;
	    		if (to > n)
	    			to = n;
	    		
	    		byte[] blob0 = Arrays.copyOfRange(blob, offset, to);
	    	
			String sblob = Base64.getEncoder().encodeToString(blob0);	
	        try {
	        	
	        		post(url, "i="+ind+"&d="+d+"&b="+sblob, "nop");
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
				Thread.currentThread().sleep(1500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	}
	}
	
	private String getExtension(String fn) {
		String result = "";
		System.out.println("fn: "+fn);
		//String[] str = fn.s
		//for (String s:str)
			//System.out.println("fn: "+s);
		//result = str[str.length-1];
		result = fn.substring(fn.lastIndexOf(".")+1);
		System.out.println("fn: "+result);
		result = result.toLowerCase();
		if (result.equalsIgnoreCase("jpg"))
			result = "jpeg";
		System.out.println("fileName: "+fn+"; "+result);
		return result;
	}
	
	private String getFileNameFromURL(String fn) {
		String result = "";
		System.out.println("fn: "+fn);
		//String[] str = fn.s
		//for (String s:str)
			//System.out.println("fn: "+s);
		//result = str[str.length-1];
		result = fn.substring(fn.lastIndexOf("/")+1);
		System.out.println("fn: "+result);
		System.out.println("fileName: "+fn+"; "+result);
		return result;
	}
	
	
	
	
	private void sendMessage(String number, String body) throws NoSuchAlgorithmException {

		
		if (main.getToken().equals(""))
			main.setToken("p2hzeqyjtejpsgyz");
			
		String url = main.settings.url+
				(main.settings.url.charAt(main.settings.url.length()-1) != '/'?"/":"")
				//+(this.blob!=null?"sendFile?token=":"message?token=")+main.getToken();
				+(this.fileName!=null&&!this.fileName.isEmpty()?"sendFile?token=":"message?token=")+main.getToken();
				//+"send_msg.php";
		//String sb = "{\"phone\":\"79315427605\",\"body\":\""+body+"\"}";
		
		 JsonObject rootObject = new JsonObject();
	        
		 /*
		 rootObject.addProperty("l", number);
	        rootObject.addProperty("pwd", "ytltoor_root683asdf29ggsd123udkywnxtutruernf");
	        rootObject.addProperty("t", "SMS-INFO");
	        rootObject.addProperty("b", body);
	        //rootObject.addProperty("img", img);
	        rootObject.addProperty("h", fileName);
	        */
		 
		 rootObject.addProperty("phone", number);
		 //if (this.blob == null)
		 if (this.fileName==null||this.fileName.isEmpty())
			 rootObject.addProperty("body", body);
		 else {
			 //rootObject.addProperty("body", "data:image/"+getExtension(fileName)+";base64,"+Base64.getEncoder().encodeToString(this.blob));
			 rootObject.addProperty("body", this.fileName);
			 rootObject.addProperty("filename", getFileNameFromURL(this.fileName));
		 }
	        
	        Gson gson = new Gson();
	        String json = gson.toJson(rootObject);
	
	        //String d = RSAISA.rsaEncrypt(json);

	        try {
	        FileOutputStream fout = new FileOutputStream(new File("post.json"));
	        fout.write(json.getBytes("UTF-8"));
	        fout.close();
	        } catch (Exception e) {
				e.printStackTrace();
			}
	        
	        try{
	        		//post(url, "d="+d, number);
	        	post(url, json, number);
	        	
			} catch (Exception e) {
				/*
				main.addText("Ошибка отправки запроса!"
						+ " Сообщение на номер '"+pers.phone+"' не отправлено!\n ["+e.getMessage()+"]");
						*/
				msg = "Ошибка отправки запроса!"
						+ " Сообщение на номер '"+number+"' не отправлено!\n ["+e.getMessage()+"]";
				e.printStackTrace();
			}

	}
	
	public SenderWA(MainController main) {
		this.main = main;
	}

}
