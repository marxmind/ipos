package com.italia.ipos.enm;

public enum SelectionInput {
	
	CASH("ca"),
	CREDIT("cr"),
	CUSTOMER("cu"),
	VOID_PREVIOUS("vp"),
	VOID_ALL("vl"),
	ADD_ITEM("ai"),
	VIEW_HISTORY("vh"),
	DISPENSE("dp"),
	RECALL("rc"),
	CLEAR_ALL("cl"),
	CHECK_PRICE("cp"),
	EXTRA_ITEM("xx"),
	POINTS("ps"),
	RETURN_ITEM("ri");
	
	private String name;
	
	private SelectionInput(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
}
