package kookies;


import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import kookies.model.Cookie;
import kookies.model.Database;
import kookies.model.Ingredient;

public class MainApp extends Application {
	
	private Stage primaryStage;
	private BorderPane rootLayout;
	private ObservableList<Cookie> cookieType = FXCollections.observableArrayList();//temp
	private ArrayList<Ingredient> list = new ArrayList<Ingredient>();//temp

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Krusty Kookies");
		
		initRootLayout();
		
		showProductionScreen();
		Database db = new Database();
	}
	
	public void initRootLayout(){
		try{
			//FXMLLoader loader = new FXMLLoader();
			//loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
			FXMLLoader loader = new FXMLLoader(getClass().getResource("view/RootLayout.fxml"));
			System.out.println("root layout path");
			rootLayout = (BorderPane) loader.load();
			System.out.println("root layout loaded");
			
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			System.out.println("scene made");
			primaryStage.show();
			System.out.println("scene shown");
		}catch(IOException e){
			System.out.println("ROOT LAYOUT EXCEPTION");
			//e.printStackTrace();
		}
	}
	
	public MainApp(){//temp
		
		cookieType.add(new Cookie("Nut Ring"));
		cookieType.add(new Cookie("Nut Cookie"));
		cookieType.add(new Cookie("Tango"));
		cookieType.add(new Cookie("Amneris"));
		cookieType.add(new Cookie("Almond Delight"));
		cookieType.add(new Cookie("Berliner"));
		
		
	}
	public ObservableList<Cookie> getCookieType(){//temp
		return cookieType;
	}
	public void showProductionScreen(){
		try{
			//FXMLLoader loader = new FXMLLoader();
			//loader.setLocation(MainApp.class.getResource("view/ProductionScreen.fxml"));
			FXMLLoader loader = new FXMLLoader(getClass().getResource("view/ProductionScreen.fxml"));

			AnchorPane productionScreen = (AnchorPane) loader.load();
			
			rootLayout.setCenter(productionScreen);
			
			//Give the controller access to the main app
			ProductionController controller = loader.getController();
			controller.setMainApp(this);
			
			
		}catch(IOException e){
			System.out.println("PRODUCTION SCREEN EXCEPTION");
			//e.printStackTrace();
		}
	}
	
	
	
	
	public Stage getPrimaryStage() {
        return primaryStage;
    } 
    
	public static void main(String[] args) {
		launch(args);
	}
}