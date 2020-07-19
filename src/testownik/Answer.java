package testownik;

/*
 * Testownik PWr
 * Copyright © 2018-2020, Krzysztof Wojciechowski.
 * All rights reserved.
 * License: MIT
 */

import java.util.Objects;

/**
 * An answer to a specific quiz question.
 */
class Answer {
    private static final String ANSWER_BASE_FORMAT = "(%c) %s";

    private final String text;
    private final boolean correct;
    private final char symbol;

    /**
     * Create a new answer, for use with any question. Text will be displayed directly.
     * @param text           The text of the answer, in display format.
     * @param symbol         The symbol of the answer (a-z).
     * @param answerKeyEntry The entry (0/1) for this question from the answer key.
     */
    public Answer(String text, char symbol, char answerKeyEntry) {
        this.text = text;
        this.symbol = symbol;
        switch (answerKeyEntry) {
            case '0':
                this.correct = false;
                break;
            case '1':
                this.correct = true;
                break;
            default:
                throw new RuntimeException("Unknown answer key entry: " + answerKeyEntry);
        }
    }

    /**
     * Create a new answer, for use with any question. Text will be displayed directly, symbol will be inferred from it.
     * @param text           The text of the answer, in display format.
     * @param answerKeyEntry The entry (0/1) for this question from the answer key.
     */
    public Answer(String text, char answerKeyEntry) {
        this(text, text.trim().charAt(1), answerKeyEntry);
    }

    /**
     * Create a new answer, for use with any question.
     * @param rawText        The text of the answer, without any symbols.
     * @param symbol         The symbol of the answer (a-z).
     * @param answerKeyEntry The entry (0/1) for this question from the answer key.
     */
    public static Answer fromRawText(String rawText, char symbol, char answerKeyEntry) {
        return new Answer(String.format(ANSWER_BASE_FORMAT, symbol, rawText), symbol, answerKeyEntry);
    }

    public boolean isCorrect() {
        return correct;
    }

    public char getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Answer answer = (Answer)o;
        return correct == answer.correct &&
                symbol == answer.symbol &&
                Objects.equals(text, answer.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, symbol, correct);
    }
}
