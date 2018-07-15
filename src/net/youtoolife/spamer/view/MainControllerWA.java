package net.youtoolife.spamer.view;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.google.gson.Gson;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import net.youtoolife.spamer.Settings;
import net.youtoolife.tools.Sender;
import net.youtoolife.tools.Sender2;
import net.youtoolife.tools.XlsParcer;

public class MainControllerWA {
	
	
	@FXML
	private TextArea area;
	@FXML
	private TextArea console;
	
	@FXML
	private ListView<String> listView;
	
	
	
	@FXML
	private Button startBtn;
	
	@FXML
	private Button showBtn;
	
	@FXML
	private Button addUserBtn;
	
	@FXML
	private Button imgUpload;
	
	
	@FXML
	private TextField titleField;
	@FXML
	private TextField urlField;
	@FXML
	private TextField tokenField;
	@FXML
	private TextField sleepField;
	
	@FXML
	private Label qLabel;
	@FXML
	private Label sendsLabel;
	
	@FXML
	private ImageView imgUp;

	
	private Executor exec;
	
	
	public Task<Void> dbTask;
	public Task<Void> sendTask;
	public Sender sender;
	public Sender2 sender2;
	
	private StringProperty value1 = new SimpleStringProperty("Очередь: null");
	private StringProperty value2 = new SimpleStringProperty("Отправлено: null");
	private StringProperty value3 = new SimpleStringProperty("Console: null");
	//public  IntegerProperty queuSize = new SimpleIntegerProperty(0);
	public  IntegerProperty val0 = new SimpleIntegerProperty(0);
	
	
	volatile int arrSize = 0;
	private int delay = 20000;
	private boolean run = false;
	public Settings settings = null;
	
	XlsParcer parcer = new XlsParcer();
	
	
	
	private String imgToUp = null;
	private byte[] blob = null;

   
    public MainControllerWA() {
    	
    }
    
	@FXML
    private void initialize() {
		System.out.println("Init");
		
		String dir0 = System.getProperty("user.home")+"/YouTooLife/wuSenderE0/";
		
		File dir = new File(dir0);
		try {
		if (!dir.exists()) 
			dir.mkdirs();
		File setnFile = new File(dir0 + "settings.conf");
		if (setnFile.exists()) {
			Scanner sc = new Scanner(setnFile);
			String json = sc.next();
			sc.close();
			Gson gson = new Gson();
			settings = gson.fromJson(json, Settings.class);
			
		}
		else {
			settings = new Settings();
		}
		
		setToken(getStrFromGson(settings.token));
		sleepField.setText(String.valueOf(settings.delay));
		titleField.setText(getStrFromGson(settings.title));
		urlField.setText(getStrFromGson(settings.url));
		delay = settings.delay;
		
		}
		catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		
		
		exec = Executors.newCachedThreadPool(runnable -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t ;
        });
		
		listView.setVisible(true);
		listView.setDisable(false);
		
		//titleField.setDisable(true);
		
		//sender = new Sender(this);
		//sender2 = new Sender2(this);
		addUserBtn.setVisible(false);
		addUserBtn.setDisable(true);
		
		qLabel.textProperty().bind(value1);
		sendsLabel.textProperty().bind(value2);
		console.textProperty().bind(value3);
		
	}
	
	private String setStrToGson(String str) {
    	String result = "";
    	for (int i = 0; i < str.length(); i++) {
    		char c = str.charAt(i);
    		if (c == ' ')
    			result+="<;s>";
    		else
    			result += c;
    	}
    	return result;
    }
    
    private String getStrFromGson(String str) {
    	String result = "";
    	String[] s = str.split("<;s>");
    	for (int i = 0; i < s.length; i++) {
    		result += s[i];
    		if (i != s.length-1)
    			result += " ";
    	}
    	return result;
    }
    
    
    
	
	
	public void saveSettings() {
		try {
			
			if (sendTask.isRunning())
				sendTask.cancel();
			
		String dir0 = System.getProperty("user.home")+"/YouTooLife/wuSenderE0/";
		
		settings.delay = Integer.valueOf(sleepField.getText());
		//settings.lastID = lastID;
		//settings.lastDBID = lastDBID;
		settings.token = setStrToGson(tokenField.getText());
		settings.url = setStrToGson(urlField.getText());
		settings.title = setStrToGson(titleField.getText());
		
		Gson gson = new Gson();
		String str = gson.toJson(settings, Settings.class);
		PrintWriter pw = new PrintWriter(new File(dir0+"settings.conf"));
		pw.write(str);
		pw.close();
	
		
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	
	@FXML
    private void startBtn() {
		System.out.println("Start");
		
		
		if (run) {
			run = false;
			startBtn.setText("Start");
			sendTask.cancel();
		}
		else {
			run = true;
			
			delay = Integer.parseInt(sleepField.getText());
			settings.title = titleField.getText();
			settings.url = urlField.getText();
			
			/*main.dbHandler = new Thread(new DBHandler(this));
			main.sender = new Thread(new Sender(this));
			main.dbHandler.start();
			main.sender.start();*/
			String body = area.getText();
	        sendTask = new Task<Void>() {
	            @Override 
	            public Void call() {
	            	
	            	ObservableList<String> nums = listView.getItems();
	            	for (String number:nums) {
	            		//String number = "";
	            		//for (int i = 0; i < nums.size(); i++) 
	            			//number += nums.get(i) + (i < (nums.size()-1)?",":"");
	            		
	            	//String str = sender.run(number, body, imgToUp, blob);
	            		
          		Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                            	//val0.set(val0.get()+1);
                            	//value3.set(value3.get()+"\n"+str);
                            	value1.setValue(String.format("Очередь: %d", nums.size() 
                            			-(val0.get()>nums.size()?val0.get()%nums.size():val0.get()) ));
                                value2.setValue(String.format("Отправлено: %d", val0.get()));
                                
                                console.setScrollTop(Double.MAX_VALUE);
                            }
                        });
          		try {
          			//System.out.println("getDelay() = "+getDelay());
					Thread.sleep(getDelay());
					//System.out.println("next...");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	            }
	            	
          		return null;
	            }

	            @Override 
	            protected void succeeded() {
	                super.succeeded();
	                run = false;
	                startBtn.setText("Start");
	                value3.set(value3.get()+"\nВсе сообщения отправлены!");
	                value2.setValue(String.format("Отправлено: %d - Done!", val0.get()));
	            }

	            @Override 
	            protected void cancelled() {
	                super.cancelled();
	            }

	        @Override 
	        protected void failed() {
	            super.failed();
	            }
	        };
	        exec.execute(sendTask);
	        
			startBtn.setText("Stop");
		}
		
	}
	
	
	
	@FXML
    private void addUsers() {
		System.out.println("Start8");
		
		
		//return;
		
		
		if (run) {
			run = false;
			addUserBtn.setText("Start");
			sendTask.cancel();
		}
		else {
			run = true;
			
			delay = Integer.parseInt(sleepField.getText());
			settings.title = titleField.getText();
			settings.url = urlField.getText();
			
			/*main.dbHandler = new Thread(new DBHandler(this));
			main.sender = new Thread(new Sender(this));
			main.dbHandler.start();
			main.sender.start();*/
			String body = area.getText();
	        sendTask = new Task<Void>() {
	            @Override 
	            public Void call() {
	            	
	            	ObservableList<String> nums = listView.getItems();
	            	for (String number:nums) {
	            		
	            	String str = sender2.run(number, body);
	            		
          		Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                            	//val0.set(val0.get()+1);
                            	value3.set(value3.get()+"\n"+str);
                            	value1.setValue(String.format("Очередь: %d", nums.size() - val0.get()));
                                value2.setValue(String.format("Отправлено: %d", val0.get()));
                            }
                        });
          		try {
          			//System.out.println("getDelay() = "+getDelay());
					Thread.sleep(getDelay());
					//System.out.println("next...");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	            }
	            	
          		return null;
	            }

	            @Override 
	            protected void succeeded() {
	                super.succeeded();
	                run = false;
	                addUserBtn.setText("Add users");
	                value3.set(value3.get()+"\nВсе сообщения отправлены!");
	                value2.setValue(String.format("Отправлено: %d - Done!", val0.get()));
	            }

	            @Override 
	            protected void cancelled() {
	                super.cancelled();
	            }

	        @Override 
	        protected void failed() {
	            super.failed();
	            }
	        };
	        exec.execute(sendTask);
	        
	        addUserBtn.setText("Stop");
		}
		
	}
	
	
	
	public void parceFile(String path) {
		ArrayList<String> sheets = parcer.openFile(path);
		
		ObservableList<String> list = FXCollections.observableArrayList();
		list.addAll(parcer.parseToArr(path, sheets.get(0)));
		listView.setItems(list);
		value1.setValue(String.format("Очередь: %d", list.size()));
	}
	
	public boolean isRun() {
		return run;
	}
	
	public int getDelay() {
		return delay;
	}
	
	public String getToken() {
		return tokenField.getText();
	}
	
	public void setToken(String s) {
		tokenField.setText(s);
	}
	
	@FXML
    private void plusBtn() {
		TextInputDialog dialog = new TextInputDialog("79*********");
		dialog.setTitle("Add number");
		//dialog.setHeaderText("Look, a Text Input Dialog");
		dialog.setContentText("Please enter your number:");
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()){
			ObservableList<String> list = listView.getItems();
			if (list == null)
				list = FXCollections.observableArrayList();
			list.add(result.get());
			listView.setItems(list);
		}
		
	}
	
	@FXML
    private void minusBtn() {
		int selectedIndex = listView.getSelectionModel().getSelectedIndex();
	    if (selectedIndex >= 0) {
	        listView.getItems().remove(selectedIndex);
	    }
	}
	
	@FXML
    private void openBtn() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open document...");
        fileChooser.setInitialDirectory(
            new File(System.getProperty("user.dir"))
        );                 
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("XLSX", "*.xlsx"),
            new FileChooser.ExtensionFilter("Old format xls", "*.xls"),
            new FileChooser.ExtensionFilter("All documents", "*.*")
        );
		
		
		File selectedFile = fileChooser.showOpenDialog(null);

		if (selectedFile == null) {

			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Information Dialog");
			alert.setHeaderText(null);
			alert.setContentText("File selection cancelled.");
			alert.showAndWait();
			return;
		}
		
		String fileName = selectedFile.getAbsolutePath();
		parceFile(fileName);
	}
	
	@FXML
    private void openImgBtn() {
		
		/*
		if (blob != null) {
			blob = null;
			imgToUp = "";
			imgUpload.setText("Upload img");
			return;
		}
		*/
		
		if (imgToUp != null &&  !imgToUp.isEmpty()) {
			imgToUp = null;
			//imgToUp = "";
			imgUpload.setText("Upload img");
			return;
		}
		/*
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose a image");
        fileChooser.setInitialDirectory(
            new File(System.getProperty("user.dir"))
        );                 
        fileChooser.getExtensionFilters().addAll(
        	new FileChooser.ExtensionFilter("Images", "*.png", "*.bmp", "*.jpg", 
        			"*.jpeg", "*.gif", "*.tga", "*.svg"),
            new FileChooser.ExtensionFilter("PNG", "*.png"),
            new FileChooser.ExtensionFilter("JPG", "*.jpg"),
            new FileChooser.ExtensionFilter("BMP", "*.bmp"),
            new FileChooser.ExtensionFilter("All documents", "*.*")
        );
		
		
		File selectedFile = fileChooser.showOpenDialog(null);

		if (selectedFile == null) {

			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Information Dialog");
			alert.setHeaderText(null);
			alert.setContentText("File selection cancelled.");
			alert.showAndWait();
			return;
		}
		
		String fileName = selectedFile.getAbsolutePath();
		openImage(fileName);
		*/
		openImage(null);
	}
	

    private byte[] getBytesFromFile(String fileName) {
    	byte[] bytes = null;
    	//File file = new File(fileName);
    	System.out.println("startblob:");
    	try {
			bytes = Files.readAllBytes(new File(fileName).toPath());
			//System.out.println(Base64.getEncoder().encodeToString(bytes));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	System.out.println("done!");
    	return bytes;
    }
    
    private String getBytesToBlobStr(byte[] bytes) {
    	String res = "";
    	System.out.println("starBtS:");
    	ArrayList<byte[]> list = new ArrayList<>();
    	int n = bytes.length;
    	int offset = 0;
    	while (offset < n) {
    		int to = offset + 512*1024;
    		if (to > n)
    			to = n;
    		list.add(Arrays.copyOfRange(bytes, offset, to));
    		offset = to;
    	}
		res = Base64.getEncoder().encodeToString(bytes);	
		System.out.println("res len: "+res.length());
		byte[] bres = Base64.getDecoder().decode(res);
		
		System.out.println(bres.length+" "+bytes.length);
		int s2 = 0;
		for (byte[] bb:list)
			s2 += bb.length;
		System.out.println("lsize: "+list.size()+" len: "+s2);
    	//res+= String.valueOf(bytes[n-1]); 
    	System.out.println("end:");
    	return res;
    }
    
    
	
	public void openImage(String fileName) {
		//imgToUp = getBytesToBlobStr(getBytesFromFile(fileName));
		//blob = getBytesFromFile(fileName);
		//System.out.println("BYTES: \n"+imgToUp);
		
		//System.out.println("File: "+fileName+" : "+file.exists());
		//File f = new File(fileName);
		//imgToUp = f.getName();
		
		/*
		byte[] hash;
		try {
			hash = MessageDigest.getInstance("MD5").digest(blob);
			String actual = DatatypeConverter.printHexBinary(hash);
			imgToUp += ";"+actual;
			
			tokenField.setText(imgToUp);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		/*
		System.out.println(f.exists());
		String uri = f.toURI().toString();
			imgUp.setImage(new Image(uri));*/
		
			
			TextInputDialog dialog = new TextInputDialog("http://host.net/pic.png");
			dialog.setTitle("input image's URL");
			//dialog.setHeaderText("Look, a Text Input Dialog");
			dialog.setContentText("Please input image's URL:");
			Optional<String> result = dialog.showAndWait();
			if (result.isPresent()){
				imgToUp = result.get();
			}
		imgUp.setImage(new Image(imgToUp));
		imgUpload.setText("Delete img");
		
		
		
		//uploadImgThread();
	}
	
	
	private void uploadImgThread() {
			System.out.println("Start");
			
			settings.url = urlField.getText();

	        sendTask = new Task<Void>() {
	            @Override 
	            public Void call() {
	           
	            		
	            //String str = sender.run("777", "nop", imgToUp, blob);
	            		
          		Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                            	//val0.set(val0.get()+1);
                            	//value3.set(value3.get()+"\n"+str);
                            	//value1.setValue(String.format("Очередь: %d", nums.size() - val0.get()));
                                //value2.setValue(String.format("Отправлено: %d", val0.get()));
                            }
                        });
	            	
          		return null;
	            }

	            @Override 
	            protected void succeeded() {
	                super.succeeded();
	               
	                value3.set(value3.get()+"\nИзображение отправлено!");
	                //value2.setValue(String.format("Отправлено: %d - Done!", val0.get()));
	            }

	            @Override 
	            protected void cancelled() {
	                super.cancelled();
	            }

	        @Override 
	        protected void failed() {
	            super.failed();
	            }
	        };
	        exec.execute(sendTask);
	}
	
	@FXML
    private void showBtn() {
	
	}
	
	
	@FXML
    private void testBtn()  {
		
	}
	
	
}
