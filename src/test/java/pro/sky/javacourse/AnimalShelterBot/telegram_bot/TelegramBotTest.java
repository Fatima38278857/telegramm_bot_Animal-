package pro.sky.javacourse.AnimalShelterBot.telegram_bot;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import pro.sky.javacourse.AnimalShelterBot.service.BotServiceImpl;

import static org.mockito.Mockito.*;

class TelegramBotTest {
    private final TelegramBot out = Mockito.spy(new TelegramBot("token", "botName", new BotServiceImpl()));
    private final Update update = Mockito.mock(Update.class);
    private final Message message = Mockito.mock(Message.class);
    private final User user = Mockito.mock(User.class);
    private final DeleteMessage deleteMessage = Mockito.mock(DeleteMessage.class);
    private final CallbackQuery callbackQuery = Mockito.mock(CallbackQuery.class);
    private final Long CHATID = 13L;

    @Test
    void onUpdateReceivedShouldCallParticularMenuMethod() throws TelegramApiException {
//      given to many
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(true);
        when(message.getChatId()).thenReturn(CHATID);
        when(message.getFrom()).thenReturn(user);
        when(message.getMessageId()).thenReturn(1);
        when(update.hasCallbackQuery()).thenReturn(false);

//      given
        when(message.getText()).thenReturn("/start");
//      when
        out.onUpdateReceived(update);
//      then
        verify(out, times(1)).menuShelterSelect(message.getChatId());

        when(message.getText()).thenReturn("/help");
        out.onUpdateReceived(update);
        verify(out, times(1)).menuHelp(message.getChatId());

//      given to many
        when(update.hasMessage()).thenReturn(false);
        when(update.getMessage()).thenReturn(null);
        when(message.hasText()).thenReturn(false);
        when(message.getChatId()).thenReturn(null);
        when(message.getFrom()).thenReturn(null);
        when(message.getMessageId()).thenReturn(null);
        when(update.hasCallbackQuery()).thenReturn(true);
        when(update.getCallbackQuery()).thenReturn(callbackQuery);
        when(callbackQuery.getMessage()).thenReturn(message);
        when(message.getMessageId()).thenReturn(1);
        when(message.getChatId()).thenReturn(CHATID);
        when(callbackQuery.getFrom()).thenReturn(user);

//      given
        when(update.getCallbackQuery().getData()).thenReturn("DELETE");
        when(out.execute(deleteMessage)).thenReturn(null);
//      when
        out.onUpdateReceived(update);
//      then
        verify(out, times(1)).execute(deleteMessage);

        when(update.getCallbackQuery().getData()).thenReturn("SHELTER_SELECT");
        out.onUpdateReceived(update);
        verify(out, atLeast(1)).menuShelterSelect(CHATID);

        when(update.getCallbackQuery().getData()).thenReturn("LOCATION_MAP1");
        out.onUpdateReceived(update);
        verify(out, times(1)).menuLocation(update);

        when(update.getCallbackQuery().getData()).thenReturn("SEPARATE_SHELTER0");
        out.onUpdateReceived(update);
        verify(out, times(1)).menuShelterCurrent(update);

        when(update.getCallbackQuery().getData()).thenReturn("HOW_TO1");
        out.onUpdateReceived(update);
        verify(out, times(1)).menuHowTo(CHATID, 1L);

        when(update.getCallbackQuery().getData()).thenReturn("ANIMAL_TYPES2");
        out.onUpdateReceived(update);
        verify(out, times(1)).menuPetType(CHATID, 2L);

        when(update.getCallbackQuery().getData()).thenReturn("СОБАК0");
        out.onUpdateReceived(update);
        verify(out, times(1)).menuPetSelect(CHATID, 0L, "Собака");

        when(update.getCallbackQuery().getData()).thenReturn("КОШК1");
        out.onUpdateReceived(update);
        verify(out, times(1)).menuPetSelect(CHATID, 1L, "Кот");

        when(update.getCallbackQuery().getData()).thenReturn("ОСТАЛЬНЫЕ2");
        out.onUpdateReceived(update);
        verify(out, times(1)).menuPetSelect(CHATID, 2L, "Остальные");

        when(update.getCallbackQuery().getData()).thenReturn("VOLUNTEER_CHAT0");
        out.onUpdateReceived(update);
        verify(out, times(1)).menuVolunteerChat(CHATID, 0L);
    }
}