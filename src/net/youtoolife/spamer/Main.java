package net.youtoolife.spamer;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import net.youtoolife.spamer.view.MainController;
import javafx.scene.Scene;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	
	public Stage primaryStage;
	
	public Thread dbHandler = null;
	public Thread sender = null;

	public String dir = System.getProperty("user.home")+"/YouTooLife/wuSenderE0/";
	
	
	@Override
	public void start(Stage primaryStage) {
		try {
			
			BorderPane root = (BorderPane)FXMLLoader.load(getClass().getResource("view/Main.fxml"));
			Scene scene = new Scene(root, 800, 500);
			scene.getStylesheets().add(getClass().getResource("view/application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			
			
			
			
			FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(Main.class.getResource("view/MainForm.fxml"));
	        AnchorPane overview = (AnchorPane) loader.load();
	        root.setCenter(overview);
	        
	        MainController controller = loader.getController();
	        this.primaryStage = primaryStage;
	        primaryStage.setTitle("..::wuSenderE0::..");
	        primaryStage.setResizable(false);
	        
	        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				
				@Override
				public void handle(WindowEvent event) {
					System.out.println("closing...");
					controller.saveSettings();
					
				}
			});
	        
	       //controller.setMainApp(this);
	        
	        scene.setOnDragOver(new EventHandler<DragEvent>() {

	        	@Override
	            public void handle(DragEvent event) {
	        		//DragEvent db = new D
	        		Dragboard db = event.getDragboard();
	                if (db.hasFiles()) {
	                    event.acceptTransferModes(TransferMode.COPY);
	                } else {
	                    event.consume();
	                }
	            }
			});  
	        
	        // Dropping over surface
	        scene.setOnDragDropped(new EventHandler<DragEvent>() {
	            @Override
	            public void handle(DragEvent event) {
	                Dragboard db = event.getDragboard();
	                boolean success = false;
	                if (db.hasFiles()) {
	                    success = true;
	                    String filePath = null;
	                    /*for (File file:db.getFiles()) {
	                        filePath = file
	                        System.out.println(filePath);
	                        
	                    }*/
	                    filePath = db.getFiles().get(0).getAbsolutePath();
	                    System.out.println("File0: "+filePath);
	                    String ext = filePath.substring(filePath.indexOf(".")+1).toLowerCase();
	                    if (ext.equals("png") || ext.equals("jpg") 
	                    		|| ext.equals("bmp") || ext.equals("gif"))
	                    	controller.openImage(filePath);
	                    else
	                    	controller.parceFile(filePath);
	                }
	                event.setDropCompleted(success);
	                event.consume();
	            }
	        });
	        
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	public static void main(String[] args) {
		launch(args);
	}
}
