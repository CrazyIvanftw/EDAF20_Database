package kookies;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import kookies.model.Cookie;
import kookies.model.Customer;
import kookies.model.Database;
import kookies.model.Ingredient;
import kookies.model.Order;
import kookies.model.Pallet;
import kookies.model.TimeStamp;

public class TestMain {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {

		Database db = new Database();
		TimeStamp timeStamp = new TimeStamp();
		db.connect();
		
		@SuppressWarnings("rawtypes")
		ArrayList<Cookie> cookies = new ArrayList();
		cookies.addAll(db.getCookieList());
		Customer customer = new Customer("Smabrod AB", "Malmo");
		ArrayList<Integer> palletTotals = new ArrayList<Integer>();
		for(int i = 1 ; i < 7 ; i++){
			palletTotals.add((Integer) i);
		}
		String date = timeStamp.makeTimeStamp().substring(0,10);
		
		ArrayList<Order> testOrder = (ArrayList<Order>) db.getUndeliveredOrders();
		System.out.println(testOrder.size());
		
		for(Order o : testOrder){
			System.out.println(o.getOrderNbr() + " " + o.getExpectedDeliveryDate());
			for(int i : o.getPalletTotals()){
				System.out.print("[" + i + "] ");
			}
			System.out.println("");
			for(String s : o.getOrderDetails()){
				System.out.println(s);
			}
		}
		
		List<Pallet> pallets = db.getPallets();
		for(Pallet p : pallets){
			System.out.println(p.getProductionDate() + " compare to " + date);
			System.out.println("return int: " + p.getProductionDate().compareTo(date));
		}
		

		db.closeConnection();
		

		
	}

}
