package org.example.ceasarciper;

import java.io.*;
import java.util.*;

public class CipherProcessor {

    private static final char[] ALPHABET_ENG = "abcdefghijklmnopqrstuvwxyz".toCharArray();

    public void encryptFile(String inputFilePath, String outputFilePath, int key) {
        processFile(inputFilePath, outputFilePath, text -> encrypt(text, key));
    }

    public void decryptFile(String inputFilePath, String outputFilePath, int key) {
        processFile(inputFilePath, outputFilePath, text -> decrypt(text, key));
    }

    public void bruteForceDecrypt(String inputFilePath, String outputFilePath) {
        String encryptedText = readFile(inputFilePath);
        for (int key = 1; key < ALPHABET_ENG.length; key++) {
            String decryptedText = decrypt(encryptedText, key);
            if (isTextMeaningful(decryptedText)) {
                writeFile(outputFilePath, decryptedText);
                System.out.println("Найден правильный ключ: " + key);
                return;
            }
        }
        System.out.println("Не удалось найти правильный ключ.");
    }

    public void statisticalAnalysisDecrypt(String inputFilePath, String outputFilePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath))) {
            StringBuilder encryptedText = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                encryptedText.append(line).append("\n");
            }

            StatisticalAnalyzer analyzer = new StatisticalAnalyzer(encryptedText.toString(), new String(ALPHABET_ENG));
            int bestKey = analyzer.findBestKey();
            String decryptedText = decrypt(encryptedText.toString(), bestKey);

            System.out.println("Найден правильный ключ: " + bestKey);
            System.out.println("Расшифрованный текст:");
            System.out.println(decryptedText);

            writeFile(outputFilePath, decryptedText);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processFile(String inputFilePath, String outputFilePath, CipherFunction cipherFunction) {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {

            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(cipherFunction.apply(line));
                writer.newLine();
            }
            System.out.println("Результат записан в файл: " + outputFilePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String encrypt(String text, int key) {
        StringBuilder encryptedText = new StringBuilder();

        for (char c : text.toCharArray()) {
            int index = new String(ALPHABET_ENG).indexOf(Character.toLowerCase(c));
            if (index != -1) {
                int newIndex = (index + key) % ALPHABET_ENG.length;
                char newChar = ALPHABET_ENG[newIndex];
                encryptedText.append(Character.isUpperCase(c) ? Character.toUpperCase(newChar) : newChar);
            } else {
                encryptedText.append(c);
            }
        }

        return encryptedText.toString();
    }

    private String decrypt(String text, int key) {
        StringBuilder decryptedText = new StringBuilder();

        for (char c : text.toCharArray()) {
            int index = new String(ALPHABET_ENG).indexOf(Character.toLowerCase(c));
            if (index != -1) {
                int newIndex = (index - key + ALPHABET_ENG.length) % ALPHABET_ENG.length;
                char newChar = ALPHABET_ENG[newIndex];
                decryptedText.append(Character.isUpperCase(c) ? Character.toUpperCase(newChar) : newChar);
            } else {
                decryptedText.append(c);
            }
        }

        return decryptedText.toString();
    }

    private String readFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void writeFile(String filePath, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(content);
            System.out.println("Результат записан в файл: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isTextMeaningful(String text) {
        String[] commonWords = {"the", "and", "is", "in", "it", "of", "to", "that", "was", "for"};
        for (String word : commonWords) {
            if (text.toLowerCase().contains(word)) {
                return true;
            }
        }
        return false;
    }

    @FunctionalInterface
    interface CipherFunction {
        String apply(String line);
    }
}

class StatisticalAnalyzer {
    private String text;
    private String alphabet;

    public StatisticalAnalyzer(String text, String alphabet) {
        this.text = text;
        this.alphabet = alphabet;
    }

    public int findBestKey() {
        Map<Character, Integer> frequencyMap = new HashMap<>();

        // Подсчет частоты встречаемости букв
        for (char c : text.toCharArray()) {
            if (alphabet.indexOf(Character.toLowerCase(c)) != -1) {
                frequencyMap.put(Character.toLowerCase(c), frequencyMap.getOrDefault(Character.toLowerCase(c), 0) + 1);
            }
        }

        // Поиск наиболее часто встречающейся буквы
        char mostFrequentChar = ' ';
        int maxFrequency = 0;

        for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
            if (entry.getValue() > maxFrequency) {
                maxFrequency = entry.getValue();
                mostFrequentChar = entry.getKey();
            }
        }

        // Предполагаем, что наиболее часто встречающаяся буква в зашифрованном тексте соответствует 'e'
        int key = (alphabet.indexOf(mostFrequentChar) - alphabet.indexOf('e') + alphabet.length()) % alphabet.length();

        return key;
    }
}