package com.example.nataliatrybulova.naobrazovku;

public class DataStruct {
	String[] args;
    //ArrayList<String> struct;
    String type;

    public DataStruct(String arg0, String arg1){
        this.args = new String[2];
        this.args[0] = arg0;
        this.args[1] = arg1;

        this.type = type;
    }
	
	public String getArg(int pos){
            return args[pos];
	}

    public String getType(){
        return type;
    }

}
