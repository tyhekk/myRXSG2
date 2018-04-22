package com.rxsg2.wgh.myrxsg2;

import java.util.ArrayList;
import java.util.List;

public class Generals {
    List<General> list = new ArrayList<General>();

    public void addGeneral(String id,int OringnalQuality,String Name,int Quality,int Level,
             int Status, String CityName,int PosXY, double Power,double Captain,
             double Interior,double Brave,double Intelligence) {
        list.add(new General(id, OringnalQuality, Name, Quality, Level, Status, CityName, PosXY, Power, Captain, Interior, Brave, Intelligence));
    }
    public void removeGeneral(String id){
        for(General general:list){
            if(id.equals(general.getID()) ){
                list.remove(general);
            }
        }
    }
    public void clear(){
        list.clear();
    }
    // 随机获取一个低级（不需要的将领）ID
    public String getLowGeneral(){
        for(General general:list){
            if(general.getQuanlity() < 2 ){
                return general.getID();
            }
        }
        return null;
    }

    public boolean isUpdate(){
        return list.size() == 0?false:true;
    }
}
// 存放将领信息
class General {
    private String id;//ID
    private int OringnalQuality;// 初始品质
    private String Name;//将领姓名
    private int Quality;//将领现在品质
    private int Level;//将领等级
    private String Status;//将领状态
    private String CityName;//坐在城市名称
    private int PosXY;//城池坐标
    private double Power;//战斗力
    private double Captain;//统帅
    private double Interior;//内政
    private double Brave;//勇武
    private double Intelligence;//智谋

    public General(String id,int OringnalQuality,String Name,int Quality,int Level,
                   int Status, String CityName,int PosXY, double Power,double Captain,
                   double Interior,double Brave,double Intelligence){
        setProperties(id,OringnalQuality,Name,Quality,Level,Status,CityName,PosXY,Power,Captain,Interior,Brave,Intelligence);
        //print();
    }

    public void setProperties(String id,int OringnalQuality,String Name,int Quality,int Level,
                              int Status, String CityName,int PosXY, double Power,double Captain,
                              double Interior,double Brave,double Intelligence){
        this.id = id;
        this.OringnalQuality = OringnalQuality;
        this.Name = Name;
        this.Quality = Quality;
        this.Level = Level;
        switch (Status){
            case 1:
                this.Status = "Free";
                break;
            case 2:
                this.Status = "Busy";
                break;
            case 3:
                this.Status = "Captive";
                break;
                default:
                    this.Status = "Unknown";
                    break;
        }

        this.CityName = CityName;
        this.PosXY= PosXY;
        this.Power = Power;
        this.Captain= Captain;
        this.Interior= Interior;
        this.Brave = Brave;
        this.Intelligence = Intelligence;
    }
    public String getID(){
        return id;
    }
    public int getQuanlity(){
        return this.Quality;
    }
    public int getLevel(){return Level;}

    public void print(){
        System.out.println("ID:" + id);
        if(false){
            return;
        }
        System.out.println("初始品质:" + OringnalQuality);
        System.out.println("将领姓名:" + Name);
        System.out.println("将领现在品质:" + Quality);
        System.out.println("将领等级:" + Level);
        System.out.println("将领状态:" + Status);
        System.out.println("坐在城市名称:" + CityName);
        System.out.println("城池坐标:" + PosXY);
        System.out.println("战斗力:" + Power);
        System.out.println("统帅:" + Captain);
        System.out.println("内政:" + Interior);
        System.out.println("勇武:" + Brave);
        System.out.println("智谋:" + Intelligence);

    }
}
