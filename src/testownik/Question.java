package testownik;

/*
 * Testownik PWr
 * Copyright © 2018-2020, Krzysztof Wojciechowski.
 * All rights reserved.
 * License: MIT
 */

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class Question {
    private static final String IMG_DIR = "img/";
    private static final String IMG_PREFIX = "img=";
    private static final char LETTER_BASELINE = 'a' - 2; // we use 2-indexing for answers
    private static final int IMG_PREFIX_LEN = IMG_PREFIX.length();
    private static final Pattern LEGACY_QUESTION_LINE = Pattern.compile("\\s*(\\d+)[.)]\\s*(.*?)");
    private static final Pattern LEGACY_ANSWER_KEYED = Pattern.compile("\\(?([A-Za-z])[.)]\\s*(.*?)");
    private static final Pattern LEGACY_ANSWER_UNKEYED = Pattern.compile("-\\s+(.*?)");
    private final ArrayList<Answer> answers;
    private int number;
    private String text;
    private boolean shuffle = true;
    private Path image = null;

    public Question(List<String> lines, Path sourceFile, Path databaseDirectory) throws Exception {
        answers = new ArrayList<>();

        if (lines.get(0).startsWith("\uFEFF")) { // UTF-8 BOM
            lines.set(0, lines.get(0).substring(1));
        }

        if (lines.get(0).startsWith("QQ")) {
            loadNewFormat(lines, databaseDirectory);
        } else if (lines.get(0).startsWith("X")) {
            loadLegacyFormat(lines, sourceFile, databaseDirectory);
        } else {
            throw new Exception("Invalid file format. " + lines.toString());
        }

        if (correctAnswerCount() == 0) {
            throw new Exception("Every question needs at least one correct answer (" + toString() + ")");
        }
    }

    /** Load new question format (questions starting with QQ, image and meta support) */
    public void loadNewFormat(List<String> lines, Path databaseDirectory) throws Exception {
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
                String imageSetting = setting.substring(IMG_PREFIX_LEN);
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
    }

    /** Load legacy question format (questions starting with X, anything goes) */
    public void loadLegacyFormat(List<String> lines, Path sourceFile, Path databaseDirectory) throws Exception {
        String[] settings = lines.get(0).split(";");
        String answerKey = ("X" + settings[0]).trim();
        if (answerKey.length() != (lines.size())) {
            throw new Exception(String.format(
                    "Invalid file format. Found %d items in answer key and %d lines",
                    answerKey.length(), lines.size()));
        }

        List<String> trimmedLines = lines.stream().map(String::trim).collect(Collectors.toList());

        String questionLine = trimmedLines.get(1);
        Matcher m = LEGACY_QUESTION_LINE.matcher(questionLine);
        boolean b = m.matches();
        if (b) {
            number = Integer.parseInt(m.group(1));
            text = m.group(2);
        } else {
            try {
                number = Integer.parseInt(sourceFile.getFileName().toString().split("\\.")[0]);
            } catch (NumberFormatException e) {
                number = 0;
            }
            text = questionLine;
        }


        // First, verify if we can use the format. If there are unmatching lines, err on the side of caution.
        boolean matches_keyed = true;
        boolean matches_unkeyed = true;
        for (int i = 2; i < lines.size(); i++) {
            String line = trimmedLines.get(i);
            matches_keyed = matches_keyed && LEGACY_ANSWER_KEYED.matcher(line).matches();
            matches_unkeyed = matches_unkeyed && LEGACY_ANSWER_UNKEYED.matcher(line).matches();
        }

        // Parse lines.
        for (int i = 2; i < lines.size(); i++) {
            String rawQuestionLine = trimmedLines.get(i);
            char symbol = (char)(LETTER_BASELINE + i);
            if (matches_keyed) {
                m = LEGACY_ANSWER_KEYED.matcher(rawQuestionLine);
                b = m.matches();
                if (!b) {
                    throw new Exception("Parsing error (unmatched regex when match expected)");
                }
                rawQuestionLine = m.group(2);
                symbol = m.group(1).toLowerCase().charAt(0);
            } else if (matches_unkeyed) {
                m = LEGACY_ANSWER_UNKEYED.matcher(rawQuestionLine);
                b = m.matches();
                if (b) {
                    rawQuestionLine = m.group(1);
                }
            }

            answers.add(Answer.fromRawText(rawQuestionLine, symbol, answerKey.charAt(i)));
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
