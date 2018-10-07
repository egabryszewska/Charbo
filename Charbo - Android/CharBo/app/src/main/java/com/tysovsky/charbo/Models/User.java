package com.tysovsky.charbo.Models;

public class User {
     public String id;
     public String FirstName;
     public String LastName;
     public String AccountId;

     public User(){

     }

     public User(String id, String FirstName, String LastName, String AccountId){
         this.id = id;
         this.FirstName = FirstName;
         this.LastName = LastName;
         this.AccountId = AccountId;
     }

}
