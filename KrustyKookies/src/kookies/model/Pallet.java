package kookies.model;

import java.util.ArrayList;
import java.util.List;

public class Pallet {
	
	private int palletNbr;
	private Cookie cookie;
	private String productionTimeStamp;
	private int orderNbr;
	private boolean loaded;
	private boolean blocked;
	
	public Pallet(int palletNbr, Cookie cookieType, String productionTimeStamp){
		this.palletNbr = palletNbr;
		this.cookie = cookieType;
		this.productionTimeStamp = productionTimeStamp;
		orderNbr = 0;
		loaded = false;
		blocked = false;
	}
	
	public Pallet(int palletNbr, Cookie cookieType, String productionTimeStamp, int orderNbr, boolean loaded, boolean blocked){
		this.palletNbr = palletNbr;
		this.cookie = cookieType;
		this.productionTimeStamp = productionTimeStamp;
		//System.out.println(productionTimeStamp);
		this.orderNbr = orderNbr;
		this.loaded = loaded;
		this.blocked = blocked;
	}
	
	public int getPalletNbr(){
		return palletNbr;
	}
	
	public Cookie getPalletType(){
		return cookie;
	}
	
	public String getProductionTimeStamp(){
		return productionTimeStamp.toString();
	}
	
	public String getProductionDate(){
		return productionTimeStamp.substring(0, productionTimeStamp.indexOf(" "));
	}
	
	public boolean wasProducedBefore(String date){
		if(date == null){
			return true;
		}else{
			return productionTimeStamp.substring(0, productionTimeStamp.indexOf(" ")).compareTo(date) < 0 ;
		}
	}
	
	public boolean wasProducedAfter(String date){
		if(date == null){
			return true;
		}else{
			return productionTimeStamp.substring(0, productionTimeStamp.indexOf(" ")).compareTo(date) > 0 ;
		}
	}
	
	public boolean isInOrder(Order order){
		if(order == null){
			return false;
		}else{
			return (this.orderNbr == order.getOrderNbr());
		}
	}
	
	public Boolean isInLoad(Load load){
		boolean isIn = false;
		if(load != null){
			for(Order o : load.getOrders()){
				if(this.isInOrder(o)){
					isIn = true;
					break;
				}
			}
		}
		return isIn;
	}
	
	public void addToOrder(int orderNbr){
		this.orderNbr = orderNbr;
	}
	
	public void loadIntoTruck(){
		loaded = true;
	}
	
	public int blockDelivery(){
		blocked = true;
		return orderNbr;
	}
	
	public int getOrderNbr(){
		return orderNbr;
	}
	
	public boolean isLoaded(){
		return loaded;
	}
	
	public boolean isBlocked(){
		return blocked;
	}
	
	public void printAllTheThings(){
		System.out.println(palletNbr + " " + cookie.getName() + " " + productionTimeStamp + " " + orderNbr + " " + loaded + " " + blocked);
	}
	
	public List<String> getPalletInfo(Order order, Load loadingOrder){
		ArrayList<String> info = new ArrayList<String>();
		if(blocked){
			info.add("BLOCKED");
			System.out.println("BLOCKED");
		}
		info.add("pallet Nbr: " + palletNbr);
		System.out.println("pallet Nbr: " + palletNbr);
		info.add("Cookie Type: " + cookie.getName());
		System.out.println("Cookie Type: " + cookie.getName());
		info.add("Produced at: " + productionTimeStamp);
		System.out.println("Produced at: " + productionTimeStamp);
		if(this.isInOrder(order)){
			System.out.println(order.getOrderNbr());
			info.add("In Order: " + orderNbr);
			System.out.println("In Order: " + orderNbr);
			info.add("For Customer: " + order.getCustomer().getName());
			System.out.println("For Customer: " + order.getCustomer().getName());
		}
		if(this.isInLoad(loadingOrder)){
			info.add("In Load: " + loadingOrder.getLoadNbr());
			System.out.println("In Load: " + loadingOrder.getLoadNbr());
			if(loadingOrder.isDelivered()){
				info.add("Delivered: " + loadingOrder.getDeliveryTimeStamp());
			}
		}
		return info;
	}

}
