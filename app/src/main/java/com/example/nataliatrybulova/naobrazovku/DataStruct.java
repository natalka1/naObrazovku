package com.example.nataliatrybulova.naobrazovku;

import java.util.ArrayList;


public class DataStruct {
	String[] args;
    ArrayList<String> struct;
    String type;

    public DataStruct(String arg0, String arg1, String arg2, String arg3, String arg4, ArrayList<String> struct, String type){
        this.args = new String[5];
        this.args[0] = arg0;
        this.args[1] = arg1;
        this.args[2] = arg2;
        this.args[3] = arg3;
        this.args[4] = arg4;

        this.type = type;
        this.struct = struct;
    }
	
	public String getArg(int pos){
            return args[pos];
	}

    public String getType(){
        return type;
    }

    public ArrayList<String> getStruct(){
        return struct;
    }
}
