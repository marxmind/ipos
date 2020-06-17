package com.italia.ipos.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import com.italia.ipos.application.Application;
import com.italia.ipos.controller.StoreProduct;
import com.italia.ipos.enm.SelectionInput;

/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 5-22.2020
 *
 */
@Named
@ViewScoped
public class CashRegBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 435885475751L;
	
	private String inputReader;
	private List<StoreProduct> items = new ArrayList<StoreProduct>();
	private Map<Long, StoreProduct> mapItems = new LinkedHashMap<Long, StoreProduct>();
	
	public void scanInput() {
		if(getInputReader()!=null || !getInputReader().isBlank() || !getInputReader().isEmpty()) {
			
			String val = getInputReader().substring(0, 1).toLowerCase();
			
			if(SelectionInput.CLEAR_ALL.getName().equalsIgnoreCase(val)) {
				
			}else if(SelectionInput.ADD_ITEM.getName().equalsIgnoreCase(val)) {
				
			}else if(SelectionInput.CREDIT.getName().equalsIgnoreCase(val)) {
				
			}else if(SelectionInput.CUSTOMER.getName().equalsIgnoreCase(val)) {
				
			}else if(SelectionInput.VOID_PREVIOUS.getName().equalsIgnoreCase(val)) {
				
			}else if(SelectionInput.VOID_ALL.getName().equalsIgnoreCase(val)) {
				
			}else if(SelectionInput.VIEW_HISTORY.getName().equalsIgnoreCase(val)) {
				
			}else if(SelectionInput.DISPENSE.getName().equalsIgnoreCase(val)) {
				
			}else if(SelectionInput.RECALL.getName().equalsIgnoreCase(val)) {
			
			}else if(SelectionInput.CHECK_PRICE.getName().equalsIgnoreCase(val)) {	
			
			StoreProduct product = StoreProduct.scanBarcode(getInputReader().substring(2,getInputReader().length()));
			
				
			}else if(SelectionInput.EXTRA_ITEM.getName().equalsIgnoreCase(val)) {	
				
			}else if(SelectionInput.POINTS.getName().equalsIgnoreCase(val)) {	
			
			}else if(SelectionInput.RETURN_ITEM.getName().equalsIgnoreCase(val)) {	
				
			}else {
				readingBarcode();
			}
		}
	}
	
	private void readingBarcode() {
		
		StoreProduct product = StoreProduct.scanBarcode(getInputReader());
		if(product!=null) {
			if(mapItems!=null && mapItems.containsKey(product.getId())) {
				double qty = mapItems.get(product.getId()).getQuantity();
					  qty += product.getQuantity();
					  mapItems.get(product.getId()).setQuantity(qty);
					  
					  if(items.contains(mapItems.get(product.getId()))) {
						  items.remove(product);//remove items
						  items.add(mapItems.get(product.getId()));//refresh the new item
					  }
					  
			}else {
				mapItems.put(product.getId(), product);
			}
		}else {
			Application.addMessage(1, "Response", "No item found");
		}
		
	}
	
	public String getInputReader() {
		return inputReader;
	}

	public void setInputReader(String inputReader) {
		this.inputReader = inputReader;
	}

	public List<StoreProduct> getItems() {
		return items;
	}

	public void setItems(List<StoreProduct> items) {
		this.items = items;
	}

	public Map<Long, StoreProduct> getMapItems() {
		return mapItems;
	}

	public void setMapItems(Map<Long, StoreProduct> mapItems) {
		this.mapItems = mapItems;
	}

	
	
}
