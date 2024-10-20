package org.example.demo;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.ArrayList;

public class MyTelegramBot extends TelegramLongPollingBot {
    private ArrayList<Double> coefficients = new ArrayList<>();
    private static final String SECRET_WORD = "Ladibadidabdab";  // Секретное слово для авторизации
    private boolean isAuthorized = false;  // Флаг авторизации
    private SignalAnalyzer signalAnalyzer; // Экземпляр SignalAnalyzer

    public MyTelegramBot() {
        signalAnalyzer = new SignalAnalyzer(); // Инициализация SignalAnalyzer
    }

    @Override
    public String getBotUsername() {
        return "LuckyGet_AssistantBot";  // Укажите имя вашего бота
    }

    @Override
    public String getBotToken() {
        return "7609275319:AAENhldLJvTSqM9SiY6NGzxgRRjDGwdZFPE";  // Укажите токен вашего бота
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            // Проверка авторизации
            if (isAuthorized) {
                if (messageText.equals("🖥 Получить сигнал")) {
                    // Получение сигнала
                    double predictedCoefficient = signalAnalyzer.predictNextCoefficient();
                    signalAnalyzer.addCoefficient(predictedCoefficient); // Добавляем предсказанный коэффициент в список

                    String signalMessage = String.format("✅ Предсказанный коэффициент: %.2f", predictedCoefficient);
                    sendMessage(chatId, signalMessage);
                } else if (messageText.equals("🎮 Регистрация")) {
                    sendRegistrationInstructions(chatId);
                } else {
                    sendMessage(chatId, "❓ Пожалуйста, выберите действие или нажмите '🖥 Получить сигнал'.");
                }
            } else {
                // Обработка ввода секретного слова для авторизации
                if (messageText.equals(SECRET_WORD)) {
                    isAuthorized = true;  // Успешная авторизация
                    sendMessage(chatId, "✅ Вы успешно авторизованы для получения сигналов! Теперь вы можете использовать команды.");
                    // Отправка приветственного сообщения с доступными командами
                    sendMessage(chatId, "💡 Доступные команды:\n🖥 Получить сигнал\n🎮 Регистрация");
                } else {
                    sendMessage(chatId, "❌ Неверное секретное слово. Пожалуйста, попробуйте снова.");
                }
                return;  // Выход из метода, если не авторизован
            }

            // Обработка команды /start
            if (messageText.equals("/start")) {
                sendWelcomeMessage(chatId);
            }
        }

        // Обработка нажатий на кнопки
        if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            // Обработка нажатия кнопки "Получить сигнал"
            if (callbackData.equals("get_signal")) {
                if (isAuthorized) {
                    double predictedCoefficient = signalAnalyzer.predictNextCoefficient();
                    signalAnalyzer.addCoefficient(predictedCoefficient); // Добавляем предсказанный коэффициент в список

                    String signalMessage = String.format("✅ Предсказанный коэффициент: %.2f", predictedCoefficient);
                    sendMessage(chatId, signalMessage);
                } else {
                    sendMessage(chatId, "❌ Вы не авторизованы. Пожалуйста, введите секретное слово для авторизации.");
                }
            } else if (callbackData.equals("registration")) {
                sendRegistrationInstructions(chatId);
            } else if (callbackData.equals("back")) {
                sendWelcomeMessage(chatId);
            } else if (callbackData.equals("instruction")) {
                sendMessage(chatId, "📖 Это инструкция по использованию бота...");
            }
        }
    }


    private void sendWelcomeMessage(long chatId) {
        File photo = new File("src/main/resources/trade.jpg");

        SendPhoto photoMessage = new SendPhoto();
        photoMessage.setChatId(chatId);
        InputFile inputFile = new InputFile(photo);

        // Установка изображения и текста подписи
        photoMessage.setPhoto(inputFile);
        String caption = "\uD83D\uDE0B Добро пожаловать в \uD83D\uDD38Lucky Jet GPT\uD83D\uDD38!\n\n" +
                "\uD83D\uDE80 Lucky Jet — это захватывающая азартная игра, " +
                "которая позволяет вам выигрывать в режиме реального времени. " +
                "🏆 Сделайте ставку и постарайтесь вывести выигрыш вовремя, " +
                "пока Счастливчик Джо не улетел!\n\n" +
                "🤖 Наш бот, powered by OpenAI, предскажет лучшие моменты для кэшаута с точностью до 97%.\n\n" +
                "✨ Используйте команды ниже, чтобы начать!";

        photoMessage.setCaption(caption);
        photoMessage.setReplyMarkup(Buttons.getMainMenuKeyboard());

        try {
            execute(photoMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendRegistrationInstructions(long chatId) {
        File registrationPhoto = new File("src/main/resources/registration.jpg");

        SendPhoto photoMessage = new SendPhoto();
        photoMessage.setChatId(chatId);
        InputFile inputFile = new InputFile(registrationPhoto);

        String caption = "🌐 **Шаг 1 - Зарегистрируйся**\n" +
                "✦ Для синхронизации с ботом зарегистрируй НОВЫЙ аккаунт по ранее неиспользованному номеру телефона на сайте.\n" +
                "✦ Если переходишь по ссылке и попадаешь на старый аккаунт, выйди с него и снова перейди по ссылке регистрации!\n\n" +
                "📩 После завершения регистрации, нажми на 'Помощь' и напиши нашему оператору.\n" +
                "\n" +
                "🌟 **Шаг 2 - Нажми Назад и получи сигнал.**\n" +
                "\n" +
                "🔍 **Шаг 3 - Вставь данные о прошлых 10 играх, и бот начнет анализировать информацию и отправит сигнал.**";

        photoMessage.setPhoto(inputFile);
        photoMessage.setCaption(caption);
        photoMessage.setReplyMarkup(Buttons.getRegistrationMenu());

        try {
            execute(photoMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(long chatId, String messageText) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(messageText);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
