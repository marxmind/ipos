package com.italia.ipos.controller;

public class DataMigrateStore {

	public static void main(String[] args) {
		int cnt =0;
		for(Product prod : Product.retrieve("", new String[0])){
			
			
			ProductPricingTrans price = ProductPricingTrans.retrievePrice(prod.getProdid()+"");
			ProductProperties prop = ProductProperties.properties(prod.getProductProperties().getPropid()+"");
			System.out.println(cnt++ + " " + prod.getProductProperties().getProductname() + " Price: " + price.getSellingprice());
			
			double prc = 0d;
			double purprice = 0d;
			double net = 0d;
			
			try{purprice = price.getPurchasedprice().doubleValue();}catch(Exception e){}
			try{prc = price.getSellingprice().doubleValue();}catch(Exception e){}
			try{net = price.getNetprice().doubleValue();}catch(Exception e){}
			
			StoreProduct store = new StoreProduct();
			store.setBarcode(prod.getBarcode());
			store.setProductName(prop.getProductname());
			store.setUomSymbol(prop.getUom().getSymbol());
			store.setQuantity(0);
			store.setPurchasedPrice(purprice);
			store.setSellingPrice(prc);
			store.setNetPrice(net);
			store.setIsActive(1);
			store.setProduct(prod);
			store.setProductProperties(prop);
			store.setUom(prop.getUom());
			store.save();
		}
		
	}
	
	
}
