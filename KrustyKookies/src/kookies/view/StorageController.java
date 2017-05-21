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
	private String currentLocation;
	
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
	private List<Load> dbLoadingBillList;
	
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
		        	setCurrentPallet(Integer.parseInt(newValue.substring(0, newValue.indexOf(" "))));
		        	updatePalletInfoList(currentPallet.getPalletInfo(findOrderByPallet(currentPallet),findLoadByPallet(currentPallet)));
		        	printCurrents();
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
		        	setCurrentOrder(Integer.parseInt(newValue.substring(0, newValue.indexOf(" "))));
		        	printCurrents();
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
		        	setCurrentLoadingOrder(Integer.parseInt(newValue.substring(0, newValue.indexOf(" "))));
		        	printCurrents();
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
		obCookieTypeBox.add("All");
		for(Cookie c : dbCookieList){
			obCookieTypeBox.add(c.getName());
		}
		cookieTypeChoiceBox.setItems(obCookieTypeBox);
		
		// Customer Choice Box
		dbCustomerList = db.getCustomerList();
		obCustomerBox.add("All");
		for(Customer c : dbCustomerList){
			obCustomerBox.add(c.getName());
		}
		customerChoiceBox.setItems(obCustomerBox);
		
		// Pallet List
		dbPalletList = db.getPallets();
		updatePalletList(dbPalletList);
		
		// Pallet Info List
		palletInfoList.setItems(observablePalletsInfo);
		//updatePalletInfoList(null);
		
		// Orders List
		dbOrderList = db.getUndeliveredOrders();
		updateOrderList(dbOrderList);
		
		// Loading Orders
		dbLoadingOrderList = db.getLoadingOrders();
		updateLoadingOrderList(dbLoadingOrderList);

		// Delivered Loads
		dbLoadingBillList = db.getLoadingBills();
		for(Load l : dbLoadingBillList){
			System.out.println("load# " + l.getLoadNbr() + " delivered at: " + l.getDeliveryTimeStamp());
		}
		
	}
	
	
	public void initRootLayout(){
		
		
	}
	
	// SET CURRENT ORDER
	private void setCurrentPallet(int palletNbr){
		for(Pallet p : dbPalletList){
			if(p.getPalletNbr() == palletNbr){
				this.currentPallet = p;
				break;
			}
		}
	}
	
	// SET CURRENT ORDER
	private void setCurrentOrder(int orderNbr){
		for(Order o : dbOrderList){
			if(o.getOrderNbr() == orderNbr){
				this.currentOrder = o;
				break;
			}
		}
	}

	// SET CURRENT LOADING ORDER
	private void setCurrentLoadingOrder(int loadNbr){
		for(Load l : dbLoadingOrderList){
			if(l.getLoadNbr() == loadNbr){
				this.currentLoadingOrder = l;
				break;
			}
		}
	}
	
	// UPDATE PALLET LIST
	private void updatePalletList(List<Pallet> pallets){
		observablePallets.clear();
		for(Pallet p : pallets){
			observablePallets.add(String.valueOf(p.getPalletNbr()) + " : " + p.getPalletType().getName());
		}
		palletList.setItems(observablePallets);
	}
	
	//UPDATE ORDER LIST
	private void updateOrderList(List<Order> orders){
		observableOrders.clear();
		for(Order o : orders){
			observableOrders.add(String.valueOf(o.getOrderNbr()) + " : " + o.getExpectedDeliveryDate() + " " + o.getCookiesTag());
		}
		activeOrdersList.setItems(observableOrders);
	}
	
	// UPDATE LOADING ORDER LIST
	private void updateLoadingOrderList(List<Load> loadingOrders){
		observableLoadingOrders.clear();
		for(Load l : loadingOrders){
			observableLoadingOrders.add(String.valueOf(l.getLoadNbr()) + " : " + l.getOrderTags());
		}
		loadingOrdersList.setItems(observableLoadingOrders);
	}
	
	// UPDATE PALLET INFO
	private void updatePalletInfoList(List<String> info){
		observablePalletsInfo.clear();
		for(String s : info){
			observablePalletsInfo.add(s);
		}
		palletInfoList.setItems(observablePalletsInfo);
	}
	
	private Order findOrderByPallet(Pallet pallet){
		for(Order o : dbOrderList){
			if(pallet.isInOrder(o)){
				return o;
			}
		}
		return null;
	}
	
	private Load findLoadByPallet(Pallet pallet){
		for(Load l : dbLoadingOrderList){
			System.out.println("pallet in load: " + pallet.isInLoad(l));
			if(pallet.isInLoad(l)){
				return l;
			}
		}
		for(Load l : dbLoadingBillList){
			System.out.println("pallet in load: " + pallet.isInLoad(l));
			if(pallet.isInLoad(l)){
				return l;
			}
		}
		return null;
	}
	
	private boolean checkPalletDelivered(){
		for(Load l : dbLoadingBillList){
			if(currentPallet.isInLoad(l)){
				return true;
			}
		}
		return false;
	}
	
	private boolean checkPalletLoaded(){
		for(Load l : dbLoadingOrderList){
			if(currentPallet.isInLoad(l)){
				return true;
			}
		}
		return false;
	}
	
	@FXML
	private void locationSelected(String location){
		System.out.println(location);
		currentLocation = location;
	}
	
	@FXML
	private void cookieTypeSelected(String cookie){
		System.out.println(cookie);
		currentCookieType = cookie;
	}
	
	@FXML
	private void customerSelected(String customer){
		System.out.println(customer);
		currentCustomer = customer;
		if(currentCustomer.equals("All")){
			updatePalletList(db.getPalletListFromQuery("select * from pallets"));
		}else{
			for(Order o : dbOrderList){
				if(o.getCustomer().getName().equals(currentCustomer)){
					updatePalletList(db.getPalletListFromQuery("select * from pallets where orderNbr =" + o.getOrderNbr()));
				}
			}
		}
	}
	
	@FXML
	private void showPalletsButtonPressed(){
		String field = palletNbrField.getText();
		if(field.equals("")){
			updatePalletList(db.getPalletListFromQuery("select * from pallets"));
		}else{
			updatePalletList(db.getPalletListFromQuery("select * from pallets where palletNbr =" + field));
		}
	}

	@FXML
	private void blockPalletsButtonPressed(){
		
	}

	@FXML
	private void addPalletsButtonPressed(){
		db.palletIntoOrder(currentPallet, currentOrder);
		dbPalletList = db.getPallets();
		updatePalletList(dbPalletList);
	}

	@FXML
	private void removePalletsButtonPressed(){
		db.palletOutOfOrder(currentPallet);
		dbPalletList = db.getPallets();
		updatePalletList(dbPalletList);
	}

	@FXML
	private void showOrdersButtonPressed(){
		
	}

	@FXML
	private void showPalletsFromOrderButtonPressed(){
		System.out.println("Pallets from order");
		updatePalletList(db.getPalletsByOrderNbr(currentOrder.getOrderNbr()));
	}

	@FXML
	private void addOrdersButtonPressed(){
		db.assignOrderToLoad(currentOrder,currentLoadingOrder);
		dbLoadingOrderList = db.getLoadingOrders();
		updateLoadingOrderList(dbLoadingOrderList);
	}

	@FXML
	private void newLoadingOrderButtonPressed(){
		db.assignOrderToLoad(currentOrder,null);
		dbLoadingOrderList = db.getLoadingOrders();
		updateLoadingOrderList(dbLoadingOrderList);
	}

	@FXML
	private void sendOutOrdersButtonPressed(){

	}
	
	public void printCurrents(){
		if(currentPallet != null)
		System.out.println("currentPallet: " + currentPallet.getPalletNbr());
		if(currentOrder != null)
		System.out.println("currentOrder: " + currentOrder.getOrderNbr());
	}
	
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		
	}

}
