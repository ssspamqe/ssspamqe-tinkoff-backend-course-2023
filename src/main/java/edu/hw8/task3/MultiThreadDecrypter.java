package edu.hw8.task3;

import edu.hw8.task3.coded.CodedDB;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MultiThreadDecrypter {
    private static final Logger LOGGER = LogManager.getLogger();
    private final CodedDB fakeDB;
    private String alphabet = " ";
    private final int countingSystem;
    private Map<String, String> decodedPasswords;
    private ExecutorService threadPool;

    public MultiThreadDecrypter(List<String> filePaths) {
        fakeDB = new CodedDB(filePaths);

        loadAlphabet();
        countingSystem = alphabet.length();

    }

    public MultiThreadDecrypter() {
        fakeDB = new CodedDB();

        loadAlphabet();
        countingSystem = alphabet.length();

    }

    private void loadAlphabet(String... specialCharacters) {
        StringBuilder stringBuilder = new StringBuilder(alphabet);
        for (int i = 0; i < 26; i++) {
            stringBuilder.append((char) ('a' + i));
            stringBuilder.append((char) ('A' + i));
        }
        stringBuilder.append("1234567890");
        Arrays.stream(specialCharacters).forEach(stringBuilder::append);

        alphabet = stringBuilder.toString();
    }

    public Map<String, String> getDecodedMap(int minLen, int maxLen, int nThreads) throws InterruptedException {
        if (minLen <= 0) {
            throw new IllegalArgumentException("minLen must be positive number");
        }
        decodedPasswords = new HashMap<>();
        threadPool = Executors.newFixedThreadPool(nThreads);

        var firstNumber = getFirstNumber(minLen, maxLen);
        AtomicBoolean running = new AtomicBoolean(true);
        for (int fistDigit = 1; fistDigit < alphabet.length() && running.get(); fistDigit++) {
            int finalFistDigit = fistDigit;
            threadPool.execute(() -> {
                var currentNumber = firstNumber.clone();
                currentNumber[0] = finalFistDigit;

                var nextNumber = Optional.of(currentNumber);


                while (nextNumber.isPresent() && running.get()) {
                    currentNumber = nextNumber.get();
                    String password = getPassword(currentNumber);
                    handlePassword(password);
                    if (fakeDB.isEmpty()) {
                        running.set(false);
                    }
                    nextNumber = getNextNumber(currentNumber);
                }
            });
        }
        threadPool.close();

        return decodedPasswords;
    }

    private Optional<int[]> getNextNumber(int[] currentNumber) {
        int[] nextNumber = currentNumber.clone();
        int i = 1;
        while (i < nextNumber.length && nextNumber[i] >= countingSystem - 1) {
            nextNumber[i] = 1;
            i++;
        }
        if (i == nextNumber.length) {
            return Optional.empty();
        }
        nextNumber[i]++;
        return Optional.of(nextNumber);
    }

    private String getPassword(int[] number) {
        StringBuilder password = new StringBuilder();
        for (int j : number) {
            if (j == 0) {
                break;
            }
            password.append(alphabet.charAt(j));
        }
        return password.toString();
    }

    private void handlePassword(String password) {
        String hash = DigestUtils.md5Hex(password);
        String removedUser = fakeDB.remove(hash);
        if (removedUser != null) {
            decodedPasswords.put(removedUser, password);
        }
    }

    private int[] getFirstNumber(int minLen, int maxLen) {
        if (minLen <= 0) {
            throw new IllegalArgumentException("minLen must be positive number");
        }
        int[] number = new int[maxLen];
        for (int i = 0; i < minLen; i++) {
            number[i] = 1;
        }
        return number;
    }
}
