package testownik;

/*
 * Testownik PWr
 * Copyright © 2018-2020, Krzysztof Wojciechowski.
 * All rights reserved.
 * License: MIT
 */

import javax.swing.*;

class Icons {
    static final ImageIcon CORRECT_BIG = new ImageIcon(Testownik.class.getResource("/testownik/icons/correctBig.png"), "Poprawna odpowiedź");
    static final ImageIcon PARTIAL_BIG = new ImageIcon(Testownik.class.getResource("/testownik/icons/partialBig.png"), "Częściowo poprawna odpowiedź");
    static final ImageIcon INCORRECT_BIG = new ImageIcon(Testownik.class.getResource("/testownik/icons/incorrectBig.png"), "Niepoprawna odpowiedź");


    static final ImageIcon CHECKBOX_UNSEL_UNKNOWN = new ImageIcon(Testownik.class.getResource("/testownik/icons/checkbox/unsel_unknown.png"), "Niezaznaczone");
    static final ImageIcon CHECKBOX_SEL_UNKNOWN = new ImageIcon(Testownik.class.getResource("/testownik/icons/checkbox/sel_unknown.png"), "Zaznaczone");
    static final ImageIcon CHECKBOX_UNSEL_HL = new ImageIcon(Testownik.class.getResource("/testownik/icons/checkbox/unsel_hl.png"), "Niezaznaczone");
    static final ImageIcon CHECKBOX_SEL_HL = new ImageIcon(Testownik.class.getResource("/testownik/icons/checkbox/sel_hl.png"), "Zaznaczone");

    static final ImageIcon RADIO_UNSEL_UNKNOWN = new ImageIcon(Testownik.class.getResource("/testownik/icons/radio/unsel_unknown.png"), "Niezaznaczone");
    static final ImageIcon RADIO_SEL_UNKNOWN = new ImageIcon(Testownik.class.getResource("/testownik/icons/radio/sel_unknown.png"), "Zaznaczone");
    static final ImageIcon RADIO_UNSEL_HL = new ImageIcon(Testownik.class.getResource("/testownik/icons/radio/unsel_hl.png"), "Niezaznaczone");
    static final ImageIcon RADIO_SEL_HL = new ImageIcon(Testownik.class.getResource("/testownik/icons/radio/sel_hl.png"), "Zaznaczone");

    static ImageIcon getToggleIcon(JToggleButton button, Boolean correct) {
        String dir, selstr, correctstr, description;

        if (button instanceof JCheckBox) {
            dir = "checkbox";
        } else {
            dir = "radio";
        }

        if (button.isSelected()) {
            selstr = "sel";
            description = "Zaznaczone";
        } else {
            selstr = "unsel";
            description = "Niezaznaczone";
        }

        if (correct == null) {
            correctstr = "unknown";
        } else if (correct) {
            correctstr = "correct";
            description += ", poprawne";
        } else {
            correctstr = "incorrect";
            description += ", niepoprawne";
        }

        return new ImageIcon(Testownik.class.getResource("/testownik/icons/" + dir + "/" + selstr + "_" + correctstr + ".png"), description);
    }
}
