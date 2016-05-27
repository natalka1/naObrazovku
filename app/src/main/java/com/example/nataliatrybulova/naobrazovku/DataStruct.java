package com.example.nataliatrybulova.naobrazovku;

public class DataStruct {
	String[] args;
    int points;

    public DataStruct(String arg0, String arg1, int points){
        this.args = new String[3];
        this.args[0] = arg0; //name
        this.args[1] = arg1; //enabled
        this.points = points; //points


    }
	
	public String getArg(int pos){
            return args[pos];
	}

    public int getPoints(){
        return points;
    }

}
