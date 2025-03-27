package com.cfk.ebankingbanking.cucumber;

public class OddOrEvenNumber {
      public String find(int number){
        String response = "";
            if(number%2 == 0){
                response = "even";
        }
            else{
              response = "odd";
            }
        return response;
          }
    }

