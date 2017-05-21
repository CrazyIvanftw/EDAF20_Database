package kookies.model;

import java.util.ArrayList;
import java.util.List;

public class Load {
	
	private int spaceLeft;
	private int loadNbr;
	private List<Order> orders;
	
	public Load(int loadNbr){
		this.loadNbr = loadNbr;
		this.spaceLeft = 60;
		this.orders = new ArrayList<Order>();
	}

	public int getLoadNbr(){
		return loadNbr;
	}
	
	public int getSpaceLeft(){
		return spaceLeft;
	}
	
	public List<Order> getOrders(){
		return orders;
	}
	
	public List<String> printLoadingBill(){
		ArrayList<String> loadingBill = new ArrayList<String>();
		for(Order o : orders){
			for(String s : o.getOrderDetails()){
				loadingBill.add(s.toString());
			}
			loadingBill.add("Delivery TimeStamp: ");
		}
		return loadingBill;
	}
	
	public void addOrder(Order order){
		int orderTotal = order.getPalletTotal();
		if((spaceLeft - orderTotal)>0){
			orders.add(order);
			spaceLeft = spaceLeft - orderTotal;
		}
		
	}
	
	
	
	

}
