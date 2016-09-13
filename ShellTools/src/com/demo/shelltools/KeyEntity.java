package com.demo.shelltools;

public class KeyEntity {
	
	private int id;
	private int usable;
	private String keyvalue;
	
	KeyEntity(){
		
	}
	
	KeyEntity(int id, int usable, String keyvalue){
		this.id = id;
		this.usable = usable;
		this.keyvalue = keyvalue;
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUsable() {
		return usable;
	}

	public void setUsable(int usable) {
		this.usable = usable;
	}

	public String getKeyvalue() {
		return keyvalue;
	}

	public void setKeyvalue(String keyvalue) {
		this.keyvalue = keyvalue;
	}
	
	

}
