package com.example.dashy_platforms.domaine.model.TokenDTOs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class InstagramTokenResponse {
    private List<TokenData> data;

    public List<TokenData> getData() {
        return data;
    }

    public void setData(List<TokenData> data) {
        this.data = data;
    }

    public static class TokenData {
        private String access_token;
        private String user_id;
        private String permissions;

        // Getters and setters
        public String getAccess_token() {
            return access_token;
        }

        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getPermissions() {
            return permissions;
        }

        public void setPermissions(String permissions) {
            this.permissions = permissions;
        }
    }
}