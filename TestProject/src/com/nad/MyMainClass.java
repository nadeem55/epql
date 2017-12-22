package com.nad;

public class MyMainClass {

	public static void main(String[] args) {
	
		int fanAmpereA = 5;
		int fanFlowA = 322;
				
		fanFlowA = (fanFlowA < 321) ? fanFlowA : 0;
		fanFlowA = (fanAmpereA > 5) ? fanFlowA : 0;
		
		if (fanFlowA > 321 || fanAmpereA < 5)fanFlowA =0;
				
		
		System.out.print("fanFlowA : ["+ fanFlowA +"]");

	}

}
