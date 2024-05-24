package org.example;
import java.util.Scanner;
import java.util.Random;
import java.nio.file.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import java.awt.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

public class PasswordManager {
    private static final String FILE_PATH = "passwords.json";
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Менеджер паролей");
            System.out.println("1. Сгенерировать пароль");
            System.out.println("2. Сохранить пароль");
            System.out.println("3. Получить пароль");
            System.out.println("4. Выйти");
            System.out.print("Выберите опцию: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("Введите длину пароля: ");
                    int length = Integer.parseInt(scanner.nextLine());
                    System.out.println("Сгенерированный пароль: " + generatePassword(length));
                    break;
                case "2":
                    System.out.print("Введите название сервиса: ");
                    String service = scanner.nextLine();
                    System.out.print("Введите имя пользователя: ");
                    String username = scanner.nextLine();
                    System.out.print("Введите пароль (или нажмите Enter для генерации): ");
                    String password = scanner.nextLine();
                    if (password.isEmpty()) {
                        password = generatePassword(12);
                    }
                    savePassword(service, username, password);
                    System.out.println("Пароль сохранен.");
                    break;
                case "3":
                    System.out.print("Введите название сервиса: ");
                    service = scanner.nextLine();
                    System.out.print("Введите имя пользователя: ");
                    username = scanner.nextLine();
                    password = getPassword(service, username);
                    if (password != null) {
                        System.out.println("Пароль: " + password);
                    } else {
                        System.out.println("Пароль не найден.");
                    }
                    break;
                case "4":
                    System.exit(0);
                default:
                    System.out.println("Неверный выбор, попробуйте снова.");
            }
        }
    }

    private static String generatePassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+{}[]|:;<>,.?/";
        Random random = new Random();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }

    private static void savePassword(String service, String username, String password) {
        Map<String, Map<String, String>> passwords = loadPasswords();
        passwords.putIfAbsent(service, new HashMap<>());
        passwords.get(service).put(username, password);
        savePasswords(passwords);
    }

    private static String getPassword(String service, String username) {
        Map<String, Map<String, String>> passwords = loadPasswords();
        if (passwords.containsKey(service) && passwords.get(service).containsKey(username)) {
            return passwords.get(service).get(username);
        }
        return null;
    }

    private static Map<String, Map<String, String>> loadPasswords() {
        if (Files.exists(Paths.get(FILE_PATH))) {
            try (Reader reader = Files.newBufferedReader(Paths.get(FILE_PATH))) {
                Type type = new TypeToken<Map<String, Map<String, String>>>(){}.getType();
                return gson.fromJson(reader, type);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new HashMap<>();
    }

    private static void savePasswords(Map<String, Map<String, String>> passwords) {
        try (Writer writer = Files.newBufferedWriter(Paths.get(FILE_PATH))) {
            gson.toJson(passwords, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
