package org.crl.charts.dataids;
/**
 * Identifier of bar chart 
 * */
public class Label implements DataId {
private String label;
	public Object getValue() {
		return label;
	}
	public String toString(){
		return label;
	}
	public Label(String value){
		this.label=value;
	}

}
