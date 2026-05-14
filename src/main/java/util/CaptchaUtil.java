package util;

import java.util.Random;

public class CaptchaUtil {

    private int a;
    private int b;
    private String question;
    private int answer;

    public CaptchaUtil() {
        generate();
    }

    public void generate() {
        Random random = new Random();

        a = random.nextInt(9) + 1;
        b = random.nextInt(9) + 1;

        question = a + " + " + b + " = ?";
        answer = a + b;
    }

    public String getQuestion() {
        return question;
    }

    public boolean validate(String userAnswer) {
        try {
            int value = Integer.parseInt(userAnswer.trim());
            return value == answer;
        } catch (Exception e) {
            return false;
        }
    }
}