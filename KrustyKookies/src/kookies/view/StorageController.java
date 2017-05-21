package kookies.view;

import kookies.MainApp;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;


import kookies.MainApp;
import kookies.model.Cookie;
import kookies.model.Customer;
import kookies.model.Database;
import kookies.model.Load;
import kookies.model.Order;
//import kookies.model.Storage;
import kookies.model.Pallet;



public class StorageController {

	private Database db = new Database();
	private MainApp mainApp;
	
	private Pallet currentPallet;
	private Order currentOrder;
	private Load currentLoadingOrder;
	
	private String locationQuery;
	private String[] locations = {"All", "Storage", "Out For Delivery", "Delivered"};
	private String currentCookieType;
	private String currentCustomer;
	
	private ObservableList<String> obLocationsBox = FXCollections.observableArrayList();
	private ObservableList<String> obCookieTypeBox = FXCollections.observableArrayList();
	private ObservableList<String> obCustomerBox = FXCollections.observableArrayList();
	
	private ObservableList<String> observablePallets = FXCollections.observableArrayList();
	private ObservableList<String> observablePalletsInfo = FXCollections.observableArrayList();
	private ObservableList<String> observableOrders = FXCollections.observableArrayList();
	private ObservableList<String> observableLoadingOrders = FXCollections.observableArrayList();
	
	private List<Pallet> dbPalletList;
	private List<Order> dbOrderList;
	private List<Load> dbLoadingOrderList;
	private List<Cookie> dbCookieList;
	private List<Customer> dbCustomerList;
	
	@FXML
	private Button showPalletsButton;
	@FXML
	private Button blockPalletsButton;
	@FXML
	private Button addPalletsButton;
	@FXML
	private Button removePalletsButton;
	@FXML
	private Button showOrdersButton;
	@FXML
	private Button showPalletsFromOrderButton;
	@FXML
	private Button addOrdersButton;
	@FXML
	private Button newLoadingOrderButton;	
	@FXML
	private Button sendOutOrdersButton;		
	
	@FXML
	private ListView<String> palletList;
	@FXML
	private ListView<String> palletInfoList;
	@FXML
	private ListView<String> activeOrdersList;
	@FXML
	private ListView<String> loadingOrdersList;
	
	@FXML
	private CheckBox blockedCheckBox;
	@FXML
	private CheckBox palletLLCheckBox;
	@FXML
	private CheckBox palletULCheckBox;
	@FXML
	private CheckBox orderLLCheckBox;
	@FXML
	private CheckBox orderULCheckBox;
	
	
	@FXML
	private TextField palletLLField;
	@FXML
	private TextField palletULField;
	@FXML
	private TextField orderLLField;
	@FXML
	private TextField orderULField;
	@FXML
	private TextField palletNbrField;
	
	@FXML
	private ChoiceBox<String> locationChoiceBox;
	@FXML
	private ChoiceBox<String> cookieTypeChoiceBox;
	@FXML
	private ChoiceBox<String> customerChoiceBox;


	public StorageController(){
		
	}
	
	
	@FXML
	public void initialize(){
		
		db.connect();
		
		locationChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>(){
			@Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		        if(newValue == null){
		        	System.out.println("no selection");
		        }else{
		        	locationSelected(newValue);
		        }
		    }
		});
		
		cookieTypeChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>(){
			@Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		        if(newValue == null){
		        	System.out.println("no selection");
		        }else{
		        	cookieTypeSelected(newValue);
		        }
		    }
		});
		
		customerChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>(){
			@Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		        if(newValue == null){
		        	System.out.println("no selection");
		        }else{
		        	customerSelected(newValue);
		        }
		    }
		});
		
		palletList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>(){
			@Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		        if(newValue == null){
		        	System.out.println("no selection");
		        }else{
		        	//TODO make sure string is only pallet nbr
		        	setCurrentPallet(Integer.parseInt(newValue));
		        }
		    }
		});

		activeOrdersList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>(){
			@Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		        if(newValue == null){
		        	System.out.println("no selection");
		        }else{
		        	//TODO make sure string is only order nbr
		        	setCurrentOrder(Integer.parseInt(newValue));
		        }
		    }
		});

		loadingOrdersList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>(){
			@Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		        if(newValue == null){
		        	System.out.println("no selection");
		        }else{
		        	//TODO make sure string is only load nbr
		        	setCurrentLoadingOrder(Integer.parseInt(newValue));
		        }
		    }
		});
		
		// Locations Choice Box
		for(String s : locations){
			obLocationsBox.add(s);
		}
		locationChoiceBox.setItems(obLocationsBox);
		
		// Cookie Choice Box
		dbCookieList = db.getCookieList();
		for(Cookie c : dbCookieList){
			obCookieTypeBox.add(c.getName());
		}
		cookieTypeChoiceBox.setItems(obCookieTypeBox);
		
		// Customer Choice Box
		dbCustomerList = db.getCustomerList();
		for(Customer c : dbCustomerList){
			obCustomerBox.add(c.getName());
		}
		customerChoiceBox.setItems(obCustomerBox);
		
		// Pallet List
		dbPalletList = db.getPallets(); //TODO should make a method to specify what pallets;
		for(Pallet p : dbPalletList){
			observablePallets.add(String.valueOf(p.getPalletNbr()));
		}
		palletList.setItems(observablePallets);
		
		// Pallet Info List
		palletInfoList.setItems(observablePalletsInfo);
		
		// Orders List
		dbOrderList = db.getUndeliveredOrders();
		for(Order o : dbOrderList){
			observableOrders.add(String.valueOf(o.getOrderNbr()));
		}
		activeOrdersList.setItems(observableOrders);
		
		// Loading Orders
		dbLoadingOrderList = db.getLoadingOrders();
		for(Load l : dbLoadingOrderList){
			observableLoadingOrders.add(String.valueOf(l.getLoadNbr()));
		}
		loadingOrdersList.setItems(observableLoadingOrders);

		
		
	}
	
	
	public void initRootLayout(){
		
		
	}
	
	private void setCurrentPallet(int palletNbr){
		for(Pallet p : dbPalletList){
			if(p.getPalletNbr() == palletNbr){
				this.currentPallet = p;
				break;
			}
		}
	}
	
	private void setCurrentOrder(int orderNbr){
		for(Order o : dbOrderList){
			if(o.getOrderNbr() == orderNbr){
				this.currentOrder = o;
				break;
			}
		}
	}

	private void setCurrentLoadingOrder(int loadNbr){
		for(Load l : dbLoadingOrderList){
			if(l.getLoadNbr() == loadNbr){
				this.currentLoadingOrder = l;
				break;
			}
		}
	}
	
	private List<Pallet> filterPallets(){
		ArrayList<Pallet> filtered = new ArrayList<Pallet>();
		boolean blockedSetting = blockedCheckBox.isSelected();
		String lowerLimit = null;
		String upperLimit = null;
		if(palletLLCheckBox.isSelected()){
			lowerLimit = palletLLField.getText();
		}
		if(palletULCheckBox.isSelected()){
			upperLimit = palletULField.getText();
		}
		for(Pallet p : dbPalletList){
			for(Order o : dbOrderList){
				
			}
			
		}
		return filtered;
	}
	
	@FXML
	private void locationSelected(String location){
		this.locationQuery = location;
	}
	
	@FXML
	private void cookieTypeSelected(String cookie){
		this.currentCookieType = cookie;
	}
	
	@FXML
	private void customerSelected(String customer){
		this.currentCustomer = customer;
	}
	
	@FXML
	private void showPalletsButtonPressed(){

	}

	@FXML
	private void blockPalletsButtonPressed(){

	}

	@FXML
	private void addPalletsButtonPressed(){

	}

	@FXML
	private void removePalletsButtonPressed(){

	}

	@FXML
	private void showOrdersButtonPressed(){

	}

	@FXML
	private void showPalletsFromOrderButtonPressed(){

	}

	@FXML
	private void addOrdersButtonPressed(){

	}

	@FXML
	private void newLoadingOrderButtonPressed(){

	}

	@FXML
	private void sendOutOrdersButtonPressed(){

	}
	
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		
	}

}
