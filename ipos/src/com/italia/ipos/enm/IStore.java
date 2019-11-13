package com.italia.ipos.enm;

import org.apache.lucene.document.Field.Store;

/**
 * 
 * @author mark italia
 * @since created 10/05/2016
 * @version 1.0
 *
 */
public enum IStore {

	GROCERRY("GP","GROCERRY"),
	HARDWARE("HD","HARDWARE"),
	GASOLINE("GS","GASOLINE"),
	RESTOBAR("RB","RESTOBAR"),
	ROOM("RM","ROOM");
	
	private String code;
	private String name;
	
	private  IStore(String code,String name){
		this.code = code;
		this.name = name;
	}
	
	public static String storeCode(IStore storeType){
		for(IStore store : IStore.values() ){
			if(storeType.getName().equalsIgnoreCase(store.getName())){
				return store.getCode();
			}
		}
		return "GP";
	}
	
	public static String storeCode(String storeType){
		for(IStore store : IStore.values() ){
			if(storeType.equalsIgnoreCase(store.getName())){
				return store.getCode();
			}
		}
		return "GP";
	}
	
	public static String storeName(IStore storeCode){
		for(IStore store : IStore.values() ){
			if(storeCode.getCode().equalsIgnoreCase(store.getCode())){
				return store.getName();
			}
		}
		return "GROCERRY";
	}
	

	public static IStore name(String storeCode){
		
		if("GP".equalsIgnoreCase(storeCode)){
			return IStore.GROCERRY;
		}else if("HD".equalsIgnoreCase(storeCode)){
			return IStore.HARDWARE;
		}if("GS".equalsIgnoreCase(storeCode)){
			return IStore.GASOLINE;
		}if("RB".equalsIgnoreCase(storeCode)){
			return IStore.RESTOBAR;
		}
		return IStore.GROCERRY;
	}
	
	public static String storeName(String storeCode){
		for(IStore store : IStore.values() ){
			if(storeCode.equalsIgnoreCase(store.getCode())){
				return store.getName();
			}
		}
		return "GROCERRY";
	}
	
	public String getCode(){
		return code;
	}
	
	public String getName(){
		return name;
	}
	
}
