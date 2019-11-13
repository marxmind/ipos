package com.italia.ipos.security;
/**
 * 
 * @author mark italia
 * @since 01/24/2017
 * @version 1.0
 *
 */
public enum Module {

	DELIVERY(1,"DELIVERY"),
	SALES(2,"SALES"),
	INVENTORY(3,"INVENTORY"),
	ROOM(1,"ROOM");
	
	
	private int id;
	private String name;
	
	public static String moduleName(int id){
		
		for(Module m : Module.values()){
			if(id==m.getId()){
				return m.getName();
			}
		}
		return Module.DELIVERY.getName();
	}
	
	public static int moduleId(String name){
		
		for(Module m : Module.values()){
			if(name.equalsIgnoreCase(m.getName())){
				return m.getId();
			}
		}
		return Module.DELIVERY.getId();
	}
	
	public static Module selected(int id){
		for(Module m : Module.values()){
			if(id==m.getId()){
				return m;
			}
		}
		return Module.DELIVERY;
	}
	
	public int getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	private Module(int id, String name){
		this.id = id;
		this.name = name;
	}
	
}
