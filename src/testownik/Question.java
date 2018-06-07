package testownik;

/*
 * Testownik PWr
 * Copyright © 2018, Krzysztof Wojciechowski.
 * All rights reserved.
 * License: MIT
 */

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class Question {
    private static final String IMG_DIR = "img/";
    private static final String IMG_PREFIX = "img=";
    private static final int IMG_PREFIX_LEN = IMG_PREFIX.length();

    private int number;
    private String text;
    private ArrayList<Answer> answers;
    private boolean shuffle = true;
    private Path image = null;

    public Question(List<String> lines, Path databaseDirectory) throws Exception {
        answers = new ArrayList<>();
        String imageSetting;

        if (!lines.get(0).startsWith("QQ")) {
            throw new Exception("Invalid file format. " + lines.toString());
        }

        String[] settings = lines.get(0).split(";");
        String answerKey = settings[0];
        if (answerKey.length() != lines.size()) {
            throw new Exception(String.format(
                "Invalid file format. Found %d items in answer key and %d lines",
                answerKey.length(), lines.size()));
        }

        String setting;
        for (int i = 1; i < settings.length; i++) {
            setting = settings[i];
            if (setting.equals("noshuffle")) {
                shuffle = false;
            }
            if (setting.startsWith(IMG_PREFIX)) {
                imageSetting = setting.substring(IMG_PREFIX_LEN);
                image = databaseDirectory.resolve(IMG_DIR).resolve(imageSetting);
                image = Paths.get(databaseDirectory.toString(), IMG_DIR, imageSetting);
            }
        }

        String[] questionLine = lines.get(1).split("\\.\t", 2);
        number = Integer.parseInt(questionLine[0]);
        text = questionLine[1];
        for (int i = 2; i < lines.size(); i++) {
            answers.add(new Answer(lines.get(i).trim(), answerKey.charAt(i)));
        }

        if (correctAnswerCount() == 0) {
            throw new Exception("Every question needs at least one correct answer (" + toString() + ")");
        }
    }

    /**
     * Check if the question has multiple correct answers.
     */
    public boolean hasMultipleCorrectAnswers() {
        return correctAnswerCount() > 1;
    }

    private long correctAnswerCount() {
        return answers.stream().filter(Answer::isCorrect).count();
    }

    @Override
    public String toString() {
        return number + ". " + text;
    }

    public ArrayList<Answer> getAnswers() {
        return answers;
    }

    public Path getImage() {
        return image;
    }

    public boolean hasImage() {
        return image != null;
    }

    public boolean hasShuffle() {
        return shuffle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Question question = (Question)o;
        return number == question.number &&
                shuffle == question.shuffle &&
                Objects.equals(text, question.text) &&
                Objects.equals(answers, question.answers) &&
                Objects.equals(image, question.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, text, answers, shuffle, image);
    }

    public int getNumber() {
        return number;
    }
}
