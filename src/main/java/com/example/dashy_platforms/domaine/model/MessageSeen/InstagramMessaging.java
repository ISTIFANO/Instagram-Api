package com.example.dashy_platforms.domaine.model.MessageSeen;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@Getter
@Setter
@ToString
public class InstagramMessaging {

    private InstagramUser sender;
    private InstagramUser recipient;
    private Long timestamp;
    private InstagramReadReceipt read;
   private Object message;
}
