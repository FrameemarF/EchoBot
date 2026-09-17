import org.example.EchoReplyGenerator;
import org.example.EchoUpdateConsumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Функциональный тест для {@link EchoUpdateConsumer}.
 * {@link TelegramClient} подменён на мок
 */
@ExtendWith(MockitoExtension.class)
class EchoUpdateConsumerTest {

    @Mock
    private TelegramClient telegramClient;

    @Test
    void userSendsTextMessageAndReceivesSameTextBack() throws Exception {
        long chatId = 123456789;
        String userText = "Привет, бот!";

        Chat chat = Chat.builder().id(chatId).type("private").build();

        Message incomingMessage = new Message();
        incomingMessage.setChat(chat);
        incomingMessage.setText(userText);

        Update incomingUpdate = new Update();
        incomingUpdate.setMessage(incomingMessage);

        EchoReplyGenerator echoReplyGenerator = new EchoReplyGenerator();
        EchoUpdateConsumer echoUpdateConsumer = new EchoUpdateConsumer(telegramClient, echoReplyGenerator);

        echoUpdateConsumer.consume(incomingUpdate);

        ArgumentCaptor<SendMessage> sentMessageCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramClient).execute(sentMessageCaptor.capture());

        SendMessage sentMessage = sentMessageCaptor.getValue();
        assertEquals(String.valueOf(chatId), sentMessage.getChatId());
        assertEquals(userText, sentMessage.getText());
    }

    @Test
    void updateWithoutTextIsIgnored() {
        Update updateWithoutMessage = new Update();
        // сообщение вообще не задано

        EchoReplyGenerator echoReplyGenerator = new EchoReplyGenerator();
        EchoUpdateConsumer echoUpdateConsumer = new EchoUpdateConsumer(telegramClient, echoReplyGenerator);

        echoUpdateConsumer.consume(updateWithoutMessage);

        verifyNoInteractions(telegramClient);
    }
}