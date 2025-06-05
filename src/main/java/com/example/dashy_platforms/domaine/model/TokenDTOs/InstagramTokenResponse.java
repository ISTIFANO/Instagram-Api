package com.example.dashy_platforms.domaine.model.TokenDTOs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class InstagramTokenResponse {

   private List<TokenData>  data;

   public  static  class TokenData{

       @JsonProperty("access_token")
       private String accessToken;

       @JsonProperty("user_id")
       private String userId;

       private String permissions;


       public String getAccessToken() { return accessToken; }
       public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

       public String getUserId() { return userId; }
       public void setUserId(String userId) { this.userId = userId; }

       public String getPermissions() { return permissions; }
       public void setPermissions(String permissions) { this.permissions = permissions; }
   }

   public List<TokenData>  getData() { return data;
   }
    public void setData(List<TokenData>  data) { this.data = data; }
}
