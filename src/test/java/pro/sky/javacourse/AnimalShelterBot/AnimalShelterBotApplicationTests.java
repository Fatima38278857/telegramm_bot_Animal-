package pro.sky.javacourse.AnimalShelterBot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.sky.javacourse.AnimalShelterBot.telegram_bot.TelegramBot;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AnimalShelterBotApplicationTests {

	@Autowired
	private TelegramBot bot;

	@Test
	public void contextLoads() throws Exception {
		assertThat(bot).isNotNull();
	}
}
