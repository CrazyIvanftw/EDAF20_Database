package kookies.model;
	
	import java.sql.*;
	import java.util.ArrayList;
	import java.util.List;
	/**
	 * Database is a class that specifies the interface to the 
	 * movie database. Uses JDBC and the MySQL Connector/J driver.
	 */
	public class Database {
		
		private TimeStamp timeStamp;
		
	    /** The database connection. */
	    private Connection conn;
	    
	        
	    /**
	     * Create the database interface object. Connection to the database
	     * is performed later.
	     */
	    public Database() {
	        conn = null;
	        timeStamp = new TimeStamp();
	    }
	    
	    public Connection connect(){
	    	//Connection conn = null;
	    	DBString string = new DBString();
	    	try {
	    	Class.forName("com.mysql.jdbc.Driver");
	    	conn = DriverManager.getConnection 
	                ("jdbc:mysql://localhost/krusty_kookies",
	                 string.user, string.password);
	    	//System.out.println("Connection Established");
	    	}catch (SQLException e) {
	            System.err.println(e);
	            e.printStackTrace();
	           
	        }
	        catch (ClassNotFoundException e) {
	            System.err.println(e);
	            e.printStackTrace();
	            
	        }
	    	return conn;
	    }
	    
	    public void disconnect(){
	    	
	    }
	        
	    /** 
	     * Open a connection to the database, using the specified user name
	     * and password.
	     *
	     * @param userName The user name.
	     * @param password The user's password.
	     * @return true if the connection succeeded, false if the supplied
	     * user name and password were not recognized. Returns false also
	     * if the JDBC driver isn't found.
	     */
	    public boolean openConnection(String userName, String password) {
	        try {
	        	//"jdbc:mysql://puccini.cs.lth.se/ + userName"
	        	//"jdbc:mysql://" + serverName + "/" + mydatabase;
	            Class.forName("com.mysql.jdbc.Driver");
	            conn = DriverManager.getConnection 
	                ("jdbc:mysql://localhost/krusty_kookies", userName, password);
	        }
	        catch (SQLException e) {
	            System.err.println(e);
	            e.printStackTrace();
	            return false;
	        }
	        catch (ClassNotFoundException e) {
	            System.err.println(e);
	            e.printStackTrace();
	            return false;
	        }
	        return true;
	    }
	        
	    /**
	     * Close the connection to the database.
	     */
	    public void closeConnection() {
	        try {
	            if (conn != null)
	                conn.close();
	        }
	        catch (SQLException e) {
	        	e.printStackTrace();
	        }
	        conn = null;
	        
	        System.err.println("Database connection closed.");
	    }
	        
	    /**
	     * Check if the connection to the database has been established
	     *
	     * @return true if the connection has been established
	     */
	    public boolean isConnected() {
	        return conn != null;
	    }
	    
	    
	    // QUERY DB FOR COOKIE LIST
	    public List<Cookie> getCookieList(){
	    	ArrayList<Cookie> cookieList = new ArrayList<Cookie>();
	    	String cookies = "select * from cookies;";
	    	Statement statement = null;
	    	try{
	    		statement = conn.createStatement();	    		
	    		ResultSet cookieListRS = statement.executeQuery(cookies);
	    		while(cookieListRS.next()){
	    			cookieList.add(new Cookie(cookieListRS.getString(1)));
	    		}
	    		cookieListRS.close();
	    		statement.close();
	    	}catch(SQLException e){
	    		e.printStackTrace();
	    	}
	    	for(Cookie c : cookieList){
	    		c.addRecipe(getRecipe(c.getName()));
	    	}
	    	return cookieList;
	    }
	    
	    // QUERY DB FOR RECIPE LIST FOR SPECIFIC COOKIE
	    private List<Ingredient> getRecipe(String cookie){
	    	StringBuilder builder = new StringBuilder();
	    	builder.append("'");
	    	builder.append(cookie);
	    	builder.append("'");
	    	IngredientFactory factory = new IngredientFactory();
	    	ArrayList<Ingredient> recipe = new ArrayList<Ingredient>();
	    	String recipeByCookie = "select * from recipes where cookieName ="+builder.toString()+ ";";
	    	Statement statement = null;
	    	
	    	try{
	    		statement = conn.createStatement();
	    		ResultSet recipeRS = statement.executeQuery(recipeByCookie);
	    		while(recipeRS.next()){
	    			recipe.add(factory.buildIngredientObject(recipeRS.getString(2), recipeRS.getDouble(3)));
	    		}
	    		recipeRS.close();
	    		statement.close();
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}
	    	return recipe;
	    }
	    
	    // UPDATE DB FOR CURRENT AMMOUNTS OF INGREDIENTS IN STOCK
	    public List<Ingredient> getIngredientStock(){
	    	ArrayList<Ingredient> stock = new ArrayList<Ingredient>();
	    	IngredientFactory factory = new IngredientFactory();
	    	String query = "select * from ingredients";
	    	try{
	    		Statement statement = conn.createStatement();
	    		ResultSet stockRS = statement.executeQuery(query);
	    		while(stockRS.next()){
	    			stock.add(factory.buildIngredientObject(stockRS.getString(1), stockRS.getDouble(2)));
	    		}
	    		stockRS.close();
	    		statement.close();
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}
	    	return stock;
	    }
	    
	    // UPDATE DB FOR INGREDIENT DELIVERY
	    public int ingredientStockDelivery(double amount, String ingredient){
	    	String updateStock = "call delivery(?,?,?)";
	    	int changes = 0;
	    	try{
	    		PreparedStatement statement = conn.prepareStatement(updateStock);
	    		statement.setDouble(3, amount);
	    		statement.setString(2, timeStamp.makeTimeStamp());
	    		statement.setString(1, ingredient);
	    		changes = statement.executeUpdate();
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}
	    	return changes;
	    	
	    }
	    
	    // UPDATE DB FOR PRODUCING A PALLET
	    public void palletProducetion(Cookie cookie){
	    	String producePallet = "call producePallet(?,?)";
	    	try{
    			PreparedStatement statement = conn.prepareStatement(producePallet);
    			statement.setString(1, cookie.getName());
    			statement.setString(2, timeStamp.makeTimeStamp());
    			statement.executeUpdate();
    		}catch(Exception e){
    			e.printStackTrace();
    		}
	    }

	    // UPDATE DB FOR PLACEING AN ORDER
	    public void placeOrder(Order order){
	    	String customer = order.getCustomer().getName();
	    	String expDate = order.getExpectedDeliveryDate();
	    	int[] totals = order.getPalletTotals();
	    	String placeOrder = "call placeOrder(?,?,?,?,?,?,?,?)";
	    	try{
    			PreparedStatement statement = conn.prepareStatement(placeOrder);
    			statement.setString(1, customer);
    			statement.setString(2, expDate);
    			for(int i = 0 ; i < totals.length ; i++){
    				statement.setInt(i+3, totals[i]);
    			}
    			statement.executeUpdate();
    		}catch(Exception e){
    			e.printStackTrace();
    		}
	    }
	    
	    // QUERY DB FOR CUSTOMER LIST
	    public List<Customer> getCustomerList(){
	    	String getCustomers = "select * from customers";
	    	ArrayList<Customer> customerList = new ArrayList<Customer>();
	    	try{
    			PreparedStatement statement = conn.prepareStatement(getCustomers);
    			ResultSet customerRS = statement.executeQuery();
    			while(customerRS.next()){
    				customerList.add(new Customer(customerRS.getString(1), customerRS.getString(2)));
    			}
    		}catch(Exception e){
    			e.printStackTrace();
    		}
	    	return customerList;
	    }

	    // QUERY DB FOR PALLET LIST
		public List<Pallet> getPallets() {
			String getPallets = "select * from pallets";
			ArrayList<Pallet> palletList = new ArrayList<Pallet>();
			try{
    			PreparedStatement statement = conn.prepareStatement(getPallets);
    			ResultSet palletsRS = statement.executeQuery();
    			while(palletsRS.next()){
    				palletList.add(
    					new Pallet(
    						palletsRS.getInt(1), 
    						new Cookie(palletsRS.getString(3)), 
    						palletsRS.getDate(2) + " " + palletsRS.getTime(2),
    						palletsRS.getInt(4),
    						palletsRS.getBoolean(5),
    						palletsRS.getBoolean(6))
    				);
    			}
    		}catch(Exception e){
    			e.printStackTrace();
    		}
			return palletList;
		}
		
		// QUERY DB FOR LIST OF ACTIVE ORDERS
		public List<Order> getUndeliveredOrders(){
			ArrayList<Order> undeliveredList = new ArrayList<Order>();
			String getUndelivered = "select * from orders natural join customers where orderNbr not in (select orderNbr from loadingbills)";
			try{
    			PreparedStatement statement = conn.prepareStatement(getUndelivered);
    			ResultSet undeliveredRS = statement.executeQuery();
    			while(undeliveredRS.next()){
    				Order o = new Order(
    					new Customer(undeliveredRS.getString(1),undeliveredRS.getString(5)),
    					undeliveredRS.getString(3)
    				);
    				o.setOrderNbr(undeliveredRS.getInt(2));
    				o.setPalletTotals(getCookieList(), getNbrPallets(o.getOrderNbr()));
    				for(Pallet p : getPalletsByOrderNbr(o.getOrderNbr())){
    					o.addPalletToOrder(p);
    				}
    				undeliveredList.add(o);
    			}
    		}catch(Exception e){
    			e.printStackTrace();
    		}
			return undeliveredList;
		}
		
		// QUERY DB FOR ALL PALLETS IN A SPECIFIC ORDER
		public List<Pallet> getPalletsByOrderNbr(int orderNbr){
			System.out.println("getPalletsByOrderNbr(): " + orderNbr);
			ArrayList<Pallet> pallets = new ArrayList<Pallet>();
			String palletsByOrderNbr = "select * from pallets where orderNbr = " + orderNbr;
			System.out.println(palletsByOrderNbr);
			try{
	    		Statement statement = conn.createStatement();
	    		ResultSet palletsRS = statement.executeQuery(palletsByOrderNbr);
	    		int i = 1;
	    		while(palletsRS.next()){
	    			System.out.println("while " + i);
	    			Pallet p = new Pallet(
    						palletsRS.getInt(1), 
    						new Cookie(palletsRS.getString(3)), 
    						palletsRS.getDate(2) + " " + palletsRS.getTime(2),
    						palletsRS.getInt(4),
    						palletsRS.getBoolean(5),
    						palletsRS.getBoolean(6));
	    			pallets.add(p);
	    			i++;
	    		}
	    		statement.close();
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}
			return pallets;
		}
		
		// TODO MAY NOT NEED TO ACTUALLY HAVE THIS THIS...
		public Pallet getPalletsByPalletNbr(int palletNbr){
			Pallet pallet = null;
			//TODO
			return pallet;
		}
		
		// PUT A PALLET INTO AN ORDER
		public void palletIntoOrder(Pallet pallet, Order order){
	    	String palletIntoOrder = "call palletIntoAnOrder(?,?)";
	    	System.out.println(palletIntoOrder);
	    	try{
    			PreparedStatement statement = conn.prepareStatement(palletIntoOrder);
    			statement.setInt(1, pallet.getPalletNbr());
    			statement.setInt(2, order.getOrderNbr());
    			statement.executeUpdate();
    		}catch(Exception e){
    			e.printStackTrace();
    		}
	    }
		
		// TAKE PALLET OUT OF ORDER 
		public void palletOutOfOrder(Pallet pallet){
			   	String palletOutOfOrder = "call palletOutOfAnOrder(?)";
			   	try{
		    		PreparedStatement statement = conn.prepareStatement(palletOutOfOrder);
		    		statement.setInt(1, pallet.getPalletNbr());
		    		statement.executeUpdate();
		    	}catch(Exception e){
		    		e.printStackTrace();
		    	}
			}
		
		// QUERY THE DB FOR TODAL NUMBER OF PALLETS IN SPECIFIC ORDER
		public List<Integer> getNbrPallets(int orderNbr){
			ArrayList<Integer> nbrPallets = new ArrayList<Integer>();
			String nbrPalletsQuery = "select * from nbrPallets where orderNbr = " + orderNbr;
			try{
	    		Statement statement = conn.createStatement();
	    		ResultSet nbrRS = statement.executeQuery(nbrPalletsQuery);
	    		while(nbrRS.next()){
	    			nbrPallets.add(nbrRS.getInt(3));
	    		}
	    		statement.close();
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}
			return nbrPallets;
		}
		
		// MAKE A LOADING BILL
		
		// PUT AN ORDER INTO THE LOADING BILL
		
		// 
		
		// QUERY DB FOR ALL LOADING BILLS
		public List<Load> getLoadingBills(){
			ArrayList<Load> loadingBills = new ArrayList<Load>();
			String loadingBillsQuery = "select * from loadingBills";
			try{
	    		Statement statement = conn.createStatement();
	    		ResultSet loadingBillsRS = statement.executeQuery(loadingBillsQuery);
	    		int lastLoadNbr = 0;
	    		while(loadingBillsRS.next()){
	    			int currentLoadNbr = loadingBillsRS.getInt(1);
	    			if(currentLoadNbr != lastLoadNbr){
	    				Load loadingBill = new Load(currentLoadNbr, loadingBillsRS.getString(3));
	    				List<Order> orders = getOrdersByLoadingBillNbr(loadingBill.getLoadNbr());
	    				for(Order o : orders){
	    					loadingBill.addOrder(o);
	    				}
	    				loadingBills.add(loadingBill);
	    				System.out.print("DB Bill#"+loadingBill.getLoadNbr());
	    			}
	    			lastLoadNbr = currentLoadNbr;
	    		}
	    		statement.close();
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}
			return loadingBills;
		}
		
		// QUERY DB FOR ALL LOADING ORDERS
		public List<Load> getLoadingOrders(){
			ArrayList<Load> loadingOrders = new ArrayList<Load>();
			String loadingOrdersQuery = "select * from loadingorders";
			try{
	    		Statement statement = conn.createStatement();
	    		ResultSet loadingOrdersRS = statement.executeQuery(loadingOrdersQuery);
	    		int lastLoadNbr = 0;
	    		while(loadingOrdersRS.next()){
	    			int currentLoadNbr = loadingOrdersRS.getInt(1);
	    			if(currentLoadNbr != lastLoadNbr){
	    				Load loadingOrder = new Load(currentLoadNbr);
	    				List<Order> orders = getOrdersByLoadingNbr(loadingOrder.getLoadNbr());
	    				for(Order o : orders){
	    					loadingOrder.addOrder(o);
	    				}
	    				loadingOrders.add(loadingOrder);
	    			}
	    			lastLoadNbr = currentLoadNbr;
	    		}
	    		statement.close();
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}
			return loadingOrders;
		}
		
		// UPDATE DB WHEN ORDER ASSIGNED TO LOAD
		public void assignOrderToLoad(Order order, Load load){
			if(load == null){
				//TODO call orderIntoALoad(null,order.getOrderNbr());
				String orderIntoLoad = "insert into loadingorders values(null,?)";
		    	try{
	    			PreparedStatement statement = conn.prepareStatement(orderIntoLoad);
	    			statement.setInt(1, order.getOrderNbr());
	    			statement.executeUpdate();
	    		}catch(Exception e){
	    			e.printStackTrace();
	    		}
			}else{
				//TODO call orderIntoALoad(loadNbr,order.getOrderNbr());
				String orderIntoLoad = "call orderIntoALoad(?,?)";
		    	try{
	    			PreparedStatement statement = conn.prepareStatement(orderIntoLoad);
	    			statement.setInt(1, load.getLoadNbr());
	    			statement.setInt(2, order.getOrderNbr());
	    			statement.executeUpdate();
	    		}catch(Exception e){
	    			e.printStackTrace();
	    		}
			}
		}
		
		// QUERY DB FOR ORDERS ATTACHED TO A LOADING ORDER
		public List<Order> getOrdersByLoadingNbr(int loadingNbr){
			ArrayList<Order> ordersList = new ArrayList<Order>();
			String ordersQuery = "select * from orders natural join customers where orderNbr "
					+ "in (select orderNbr from loadingorders where loadNbr = "+loadingNbr +")";
			try{
	    		Statement statement = conn.createStatement();
	    		ResultSet ordersRS = statement.executeQuery(ordersQuery);
	    		while(ordersRS.next()){
	    			Order o = new Order(
	    					new Customer(ordersRS.getString(1),ordersRS.getString(5)),
	    					ordersRS.getString(3)
	    				);
	    			o.setOrderNbr(ordersRS.getInt(2));
	    			o.setPalletTotals(getCookieList(), getNbrPallets(o.getOrderNbr()));
	    			for(Pallet p : getPalletsByOrderNbr(o.getOrderNbr())){
	    				System.out.println("Pallet by load from db: " + p.getPalletNbr());
	    				o.addPalletToOrder(p);
	    			}
	    			ordersList.add(o);
	    		}
	    		statement.close();
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}
			return ordersList;
		}
		
		// QUERY DB FOR ORDERS ATTACHED TO A LOADING BILL
				public List<Order> getOrdersByLoadingBillNbr(int loadingNbr){
					ArrayList<Order> ordersList = new ArrayList<Order>();
					String ordersQuery = "select * from orders natural join customers where orderNbr in (select orderNbr from loadingbills where loadNbr = "+loadingNbr +")";
					try{
			    		Statement statement = conn.createStatement();
			    		ResultSet ordersRS = statement.executeQuery(ordersQuery);
			    		while(ordersRS.next()){
			    			Order o = new Order(
			    					new Customer(ordersRS.getString(1),ordersRS.getString(5)),
			    					ordersRS.getString(3)
			    				);
			    			o.setOrderNbr(ordersRS.getInt(2));
			    			o.setPalletTotals(getCookieList(), getNbrPallets(o.getOrderNbr()));
			    			for(Pallet p : getPalletsByOrderNbr(o.getOrderNbr())){
			    				System.out.println("Pallet by bill from db: " + p.getPalletNbr());
			    				o.addPalletToOrder(p);
			    			}
			    			ordersList.add(o);
			    		}
			    		statement.close();
			    	}catch(Exception e){
			    		e.printStackTrace();
			    	}
					return ordersList;
				}
		
		// UPDATE DB FOR DELIVERY OF ORDER TO CUSTOMER
		public void orderDelivered(Order order){
			//TODO orderDelivered(order.getOrderNbr(), timeStamp.makeTimeStamp());
		}
		
		public List<Pallet> getPalletListFromQuery(String query){
			ArrayList<Pallet> pallets = new ArrayList<Pallet>();
			String palletQuery = query;
			System.out.println(palletQuery);
			try{
	    		Statement statement = conn.createStatement();
	    		ResultSet palletsRS = statement.executeQuery(palletQuery);
	    		int i = 1;
	    		while(palletsRS.next()){
	    			System.out.println("while " + i);
	    			Pallet p = new Pallet(
    						palletsRS.getInt(1), 
    						new Cookie(palletsRS.getString(3)), 
    						palletsRS.getDate(2) + " " + palletsRS.getTime(2),
    						palletsRS.getInt(4),
    						palletsRS.getBoolean(5),
    						palletsRS.getBoolean(6));
	    			pallets.add(p);
	    			i++;
	    		}
	    		statement.close();
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}
			return pallets;
		}

}
