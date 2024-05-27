package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class PasswordManager extends JFrame {
    // Путь к файлу с паролями
    private static final String FILE_PATH = "passwords.json";
    // Экземпляр Gson для работы с JSON
    private static final Gson gson = new Gson();

    // Поля класса для компонентов графического интерфейса
    private JTextField lengthField;
    private JTextArea generatedPasswordArea;
    private JTextField serviceField;
    private JTextField usernameField;
    private JTextField passwordField;
    private JTextArea savedPasswordArea;

    // Конструктор класса PasswordManager
    public PasswordManager() {
        setTitle("Password Manager");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Создание вкладок с функционалом
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Generate Password", createGeneratePanel());
        tabbedPane.addTab("Save Password", createSavePanel());
        tabbedPane.addTab("Get Password", createGetPanel());

        // Добавление вкладок на основное окно
        add(tabbedPane);
    }

    // Метод создания панели для генерации пароля
    private JPanel createGeneratePanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1));

        // Панель для ввода длины пароля
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Enter length:"));
        lengthField = new JTextField(10);
        inputPanel.add(lengthField);

        // Кнопка для генерации пароля
        JButton generateButton = new JButton("Generate");
        generateButton.addActionListener(new GenerateButtonListener());

        // Область для отображения сгенерированного пароля
        generatedPasswordArea = new JTextArea(2, 20);
        generatedPasswordArea.setLineWrap(true);
        generatedPasswordArea.setWrapStyleWord(true);
        generatedPasswordArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(generatedPasswordArea);

        // Добавление компонентов на панель
        panel.add(inputPanel);
        panel.add(generateButton);
        panel.add(scrollPane);

        return panel;
    }

    // Метод создания панели для сохранения пароля
    private JPanel createSavePanel() {
        JPanel panel = new JPanel(new GridLayout(5, 1));

        // Панели для ввода сервиса, имени пользователя и пароля
        JPanel servicePanel = new JPanel();
        servicePanel.add(new JLabel("Service:"));
        serviceField = new JTextField(20);
        servicePanel.add(serviceField);

        JPanel usernamePanel = new JPanel();
        usernamePanel.add(new JLabel("Username:"));
        usernameField = new JTextField(20);
        usernamePanel.add(usernameField);

        JPanel passwordPanel = new JPanel();
        passwordPanel.add(new JLabel("Password:"));
        passwordField = new JTextField(20);
        passwordPanel.add(passwordField);

        // Кнопка для сохранения пароля
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new SaveButtonListener());

        // Добавление компонентов на панель
        panel.add(servicePanel);
        panel.add(usernamePanel);
        panel.add(passwordPanel);
        panel.add(saveButton);

        return panel;
    }

    // Метод создания панели для получения пароля
    private JPanel createGetPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 1));

        // Панели для ввода сервиса и имени пользователя
        JPanel servicePanel = new JPanel();
        servicePanel.add(new JLabel("Service:"));
        serviceField = new JTextField(20);
        servicePanel.add(serviceField);

        JPanel usernamePanel = new JPanel();
        usernamePanel.add(new JLabel("Username:"));
        usernameField = new JTextField(20);
        usernamePanel.add(usernameField);

        // Кнопка для получения пароля
        JButton getButton = new JButton("Get");
        getButton.addActionListener(new GetButtonListener());

        // Область для отображения полученного пароля
        savedPasswordArea = new JTextArea(2, 20);
        savedPasswordArea.setLineWrap(true);
        savedPasswordArea.setWrapStyleWord(true);
        savedPasswordArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(savedPasswordArea);

        // Добавление компонентов на панель
        panel.add(servicePanel);
        panel.add(usernamePanel);
        panel.add(getButton);
        panel.add(scrollPane);

        return panel;
    }

    // Внутренний класс для обработки событий кнопки генерации пароля
    private class GenerateButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int length = Integer.parseInt(lengthField.getText());
            String generatedPassword = generatePassword(length);
            generatedPasswordArea.setText(generatedPassword);
        }
    }

    // Внутренний класс для обработки событий кнопки сохранения пароля
    private class SaveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String service = serviceField.getText();
            String username = usernameField.getText();
            String password = passwordField.getText();
            if (password.isEmpty()) {
                password = generatePassword(12);
            }
            savePassword(service, username, password);
            JOptionPane.showMessageDialog(null, "Password saved.");
        }
    }
    // Взаимодействие с кнопкой
    private class GetButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String service = serviceField.getText();
            String username = usernameField.getText();
            String password = getPassword(service, username);
            if (password != null) {
                savedPasswordArea.setText(password);
            } else {
                savedPasswordArea.setText("Password not found.");
            }
        }
    }
    // Генерация пароля
    private String generatePassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+{}[]|:;<>,.?/";
        Random random = new Random();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }
    // Сохранение пароля
    private void savePassword(String service, String username, String password) {
        Map<String, Map<String, String>> passwords = loadPasswords();
        passwords.putIfAbsent(service, new HashMap<>());
        passwords.get(service).put(username, password);
        savePasswords(passwords);
    }
    // Получение пароля
    private String getPassword(String service, String username) {
        Map<String, Map<String, String>> passwords = loadPasswords();
        if (passwords.containsKey(service) && passwords.get(service).containsKey(username)) {
            return passwords.get(service).get(username);
        }
        return null;
    }
    // Загрузка паролей из файла
    private Map<String, Map<String, String>> loadPasswords() {
        if (Files.exists(Paths.get(FILE_PATH))) {
            try (Reader reader = Files.newBufferedReader(Paths.get(FILE_PATH))) {
                java.lang.reflect.Type type = new TypeToken<Map<String, Map<String, String>>>(){}.getType();
                return gson.fromJson(reader, type);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new HashMap<>();
    }
    // Сохранение паролей в файл
    private void savePasswords(Map<String, Map<String, String>> passwords) {
        try (Writer writer = Files.newBufferedWriter(Paths.get(FILE_PATH))) {
            gson.toJson(passwords, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Видимость окна интерфейса
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PasswordManager app = new PasswordManager();
            app.setVisible(true);
        });
    }

}
