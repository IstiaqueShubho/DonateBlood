package com.example.istiaque.donateblood;

public class Model {
    public String Nname,Nemail,Nphnnumber;
    public double NLatitude,NLongitude;
    Model(){}

    public int count = 0;

    public Model(String name, String email, String phnnumber,String location) {
        Nname = name;
        Nemail = email;
        Nphnnumber = phnnumber;
        String[] latlong = location.split("_");
        NLatitude = Double.parseDouble(latlong[0]);
        NLongitude = Double.parseDouble(latlong[1]);
        count++;
    }
    public String getname(){
        return Nname;
    }
    public void setname(String name) {Nname = name;}
    public String getemail(){
        return Nemail;
    }
    public void setemail(String email){
        Nemail = email;
    }
    public String getphnnumber(){
        return Nphnnumber;
    }
    public void setphnnumber(String phnnumber){
        Nphnnumber = phnnumber;
    }
    public double getlatitude(){ return NLatitude;}
    public void setlatitude(double Latitude){ NLatitude = Latitude;}
    public double getlongitude(){ return NLongitude;}
    public void setlongitude(double Longitude){  NLongitude = Longitude;}

    public int getCount(){ return count; }
}
