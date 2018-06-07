package testownik;

/*
 * Testownik PWr
 * Copyright © 2018, Krzysztof Wojciechowski.
 * All rights reserved.
 * License: MIT
 */

import java.util.Objects;

/**
 * An answer to a specific quiz question.
 */
class Answer {
    private String text;
    private boolean correct;
    private char symbol;

    /**
     * Create a new answer, for use with any question.
     *
     * @param text           The text of the answer.
     * @param answerKeyEntry The entry (0/1) for this question from the answer key.
     */
    public Answer(String text, char answerKeyEntry) {
        this.text = text;
        this.symbol = text.trim().charAt(1);
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
