package org.example;

import org.example.enums.ProfileEnum;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.GetUpdates;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MyTelegramBot extends TelegramLongPollingBot {
    private List<ProfileDTO> list = new LinkedList<>();
    @Override
    public String getBotUsername() {
        return "###";
    }//Bot Username

    @Override
    public String getBotToken() {
        return "###";
    }//Token

    @Override
    public void onUpdateReceived(Update update) {
        if (update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();

            if (update.getMessage().getText().equals("/start")) {
                sendTextMessage(chatId, "Salom! Iltimos, ismingizni va familiyangizni yuboring.");
                ProfileDTO profileDTO = new ProfileDTO();
                profileDTO.setId(update.getMessage().getChatId());
                profileDTO.setStep(ProfileEnum.EnterFullName);
                list.add(profileDTO);
            } else if (getUser(update.getMessage().getChatId()).getStep().equals(ProfileEnum.EnterFullName)) {
                String userText = update.getMessage().getText();
                String[] names = userText.split(" ");
                if (names.length >= 2) {
                    System.out.println("Ismni saqlash.");
                    ProfileDTO profileDTO = getUser(update.getMessage().getChatId());
                    profileDTO.setFullName(userText);
                    profileDTO.setStep(ProfileEnum.SendDoc);
                    updateUser(profileDTO);
                    sendTextMessage(chatId, "Rahmat! Endi ZIP faylingizni yuboring.");
                } else {
                    sendTextMessage(chatId, "Noto'g'ri format. Iltimos, ism va familiyangizni to'liq yuboring.");
                }
            }
        } else if (update.getMessage().hasDocument()) {
            long chatId = update.getMessage().getChatId();
            if (getUser(update.getMessage().getChatId()).getStep().equals(ProfileEnum.SendDoc)) {
                System.out.println("Zip faylni forward qilish uchun kirdi");
                if (update.getMessage().hasDocument()) {
                    String fileName = update.getMessage().getDocument().getFileName();
                    if (fileName != null && fileName.endsWith(".zip")) {
                        SendDocument sendDocument = new SendDocument();
                        sendDocument.setChatId("###");//Kanal nomi ko'yish kerak
                        sendDocument.setDocument(new InputFile(update.getMessage().getDocument().getFileId()));
                        sendDocument.setCaption(getUser(update.getMessage().getChatId()).getFullName());
                        try {
                            execute(sendDocument);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                        list.remove(chatId);
                    } else {
                        sendTextMessage(chatId, "Siz faqat zip fayl jo'natishingiz mumkin!\nQayta urinib ko'ring!");
                    }
                }
            }
        }
    }



    private void sendTextMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setText(text);
        message.setChatId(chatId);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private ProfileDTO getUser(Long id){
        for (ProfileDTO p:list) {
            if (p.getId().equals(id))return p;
        }
        return null;
    }
    private void updateUser(ProfileDTO profileDTO){
        list.remove(profileDTO.getId());
        list.add(profileDTO);
    }
}
