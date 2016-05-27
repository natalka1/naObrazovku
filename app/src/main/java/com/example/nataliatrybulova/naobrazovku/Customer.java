package com.example.nataliatrybulova.naobrazovku;

public class Customer {

    int level_id = 0;
    int points = 0;
    String email = "default";

    public Customer(int level_id, int points, String email){


        this.points = points;
        this.level_id = level_id;
        this.email = email;
    }

    public Customer(){

    }


    public int getLevel(){
        return level_id;
    }
    public int getPoints(){
        return points;
    }

    public String getEmail()
    {
        return email;
    }

}

