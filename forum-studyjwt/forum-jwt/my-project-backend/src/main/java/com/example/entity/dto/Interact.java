package com.example.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class Interact {
    Integer tid;
    Integer uid;
    String type;
    Date time;
    public   String toKey(){
        return tid+":"+uid;
    }
    public static Interact parse(String key,String type){
        String[] split = key.split(":");
        return new Interact(Integer.parseInt(split[0]),Integer.parseInt(split[1]),type,new Date());
    }
}
