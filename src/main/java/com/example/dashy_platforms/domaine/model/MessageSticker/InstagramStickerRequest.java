package com.example.dashy_platforms.domaine.model.MessageSticker;
import com.example.dashy_platforms.domaine.model.Recipient;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class InstagramStickerRequest {
    private Recipient recipient;
    private Message message;
}
