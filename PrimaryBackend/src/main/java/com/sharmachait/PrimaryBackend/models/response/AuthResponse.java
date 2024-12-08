package com.sharmachait.PrimaryBackend.models.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AuthResponse {
    private String jwt;
    private boolean status = false;
    private String message;
    public String setMessage(){
        return message;
    }
    public void setMessage(String message){
        this.message = message;
    }
    private String session = null;
}