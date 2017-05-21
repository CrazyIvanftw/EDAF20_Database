package kookies.model;

import java.util.ArrayList;
import java.util.List;

public class Load {
	
	private int spaceLeft;
	private int loadNbr;
	private List<Order> orders;
	private String deliveryTimeStamp;
	
	public Load(int loadNbr){
		this.loadNbr = loadNbr;
		this.spaceLeft = 60;
		this.orders = new ArrayList<Order>();
		this.deliveryTimeStamp = null;
	}
	
	public Load(int loadNbr, String deliveryTimeStamp){
		this.loadNbr = loadNbr;
		this.spaceLeft = 60;
		this.orders = new ArrayList<Order>();
		this.deliveryTimeStamp = deliveryTimeStamp;
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
	
	public boolean isDelivered(){
		return !(deliveryTimeStamp == null);
	}
	
	public String getDeliveryTimeStamp(){
		if(isDelivered()){
			return deliveryTimeStamp.toString();
		}else{
			return null;
		}
	}
	
	public String getOrderTags(){
		StringBuilder builder = new StringBuilder();
		builder.append("|");
		for(Order o : orders){
			builder.append(o.getOrderNbr() + "|");
		}
		return builder.toString();
	}
	
	

}
