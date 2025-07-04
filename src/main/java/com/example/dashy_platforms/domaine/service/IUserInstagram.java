package com.example.dashy_platforms.domaine.service;

public interface IUserInstagram {

    public  Boolean userExists(String instagramUserId);
    public void saveInstagramUserIfNotExists(String instagramUserId);
}
