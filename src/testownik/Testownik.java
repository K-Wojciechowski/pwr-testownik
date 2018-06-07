package testownik;

/*
 * Testownik PWr
 * Copyright © 2018, Krzysztof Wojciechowski.
 * All rights reserved.
 * License: MIT
 */

import darrylbu.icon.StretchIcon;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Testownik {
    private static final int TOGGLE_SIZE = 16;
    private final String START_TEXT = "Start";
    private final String NEXT_QUESTION_TEXT = "Następne pytanie";
    private final String CHECK_TEXT = "Sprawdź";
    private JFrame frame;
    private JPanel mainPanel;
    private JTextArea questionBox;
    private JButton mainButton;
    private JScrollPane imageContainer;
    private JLabel imageLabel;
    private JPanel answerPanel;
    private JLabel questionSeqLabel;
    private JLabel correctLabel;
    private JLabel incorrectLabel;
    private JLabel totalQuestionsLabel;
    private Path dbPath = null;
    private ArrayList<Question> questions = new ArrayList<>();
    private ArrayList<Question> questionsToAsk = new ArrayList<>();
    private ArrayList<Question> questionsCorrect = new ArrayList<>();
    private ArrayList<Question> questionsIncorrect = new ArrayList<>();
    private Question currentQuestion = null;
    private HashMap<JToggleButton, Answer> answerFormComponents = new HashMap<>();
    private ArrayList<JToggleButton> orderedAnswerToggles = new ArrayList<>();
    private int questionIndex = 0;
    private boolean hasBeenChecked = false;
    private boolean imageVisible = true;
    private int imageHeight = 250;
    private JSplitPane questionExtrasSplitPane;
    private boolean hasCheckboxes = false;
    private List<String> splash = Collections.emptyList();
    private JCheckBox shuffleQuestions;
    private JCheckBox shuffleAnswers;


    public Testownik(JFrame frame) {
        this.frame = frame;
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(0, 0));
        mainPanel.setPreferredSize(new Dimension(800, 600));

        // Set Nimbus look and feel on all platforms
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Let the default take over.
        }

        questionBox = new JTextArea("Ładowanie bazy…");
        setFontSize(questionBox, 20);
        questionBox.setLineWrap(true);
        questionBox.setEditable(false);
        questionBox.setWrapStyleWord(true);
        mainPanel.add(questionBox, BorderLayout.NORTH);

        imageContainer = new JScrollPane();
        imageLabel = new JLabel();
        imageLabel.setText("");
        imageLabel.setPreferredSize(new Dimension(800, 100));
        imageContainer.setViewportView(imageLabel);
        answerPanel = new JPanel();
        answerPanel.setLayout(new BoxLayout(answerPanel, BoxLayout.Y_AXIS));

        JScrollPane answerPanelContainer = new JScrollPane();
        answerPanelContainer.setViewportView(answerPanel);

        questionExtrasSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, imageContainer, answerPanelContainer);
        questionExtrasSplitPane.setDividerLocation(imageHeight);
        mainPanel.add(questionExtrasSplitPane, BorderLayout.CENTER);

        JPanel bottomBarPanel = new JPanel();
        bottomBarPanel.setLayout(new BorderLayout(10, 0));
        mainPanel.add(bottomBarPanel, BorderLayout.SOUTH);

        /*
        final JPanel dbPanel = new JPanel();
        dbPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JButton changeDatabaseButton = new JButton();
        changeDatabaseButton.setText("Zmień bazę danych…");
        dbSize = new JLabel("??? pytań w bazie");
        dbPanel.add(changeDatabaseButton);
        dbPanel.add(dbSize);
        bottomBarPanel.add(dbPanel, BorderLayout.WEST);
        */
        final JPanel questionStatusPanel = new JPanel();
        questionStatusPanel.setLayout(new GridBagLayout());
        bottomBarPanel.add(questionStatusPanel, BorderLayout.EAST);

        questionStatusPanel.add(makeBoldLabel("Pytanie: "), gridPos(0, 0, GridBagConstraints.WEST));
        questionStatusPanel.add(makeBoldLabel("Poprawne: "), gridPos(0, 1, GridBagConstraints.WEST));
        questionStatusPanel.add(makeBoldLabel("Niepoprawne: "), gridPos(0, 2, GridBagConstraints.WEST));

        questionSeqLabel = new JLabel("000");
        questionSeqLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        questionStatusPanel.add(questionSeqLabel, gridPos(1, 0, GridBagConstraints.EAST));

        correctLabel = new JLabel("0");
        correctLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        questionStatusPanel.add(correctLabel, gridPos(1, 1, GridBagConstraints.EAST));

        incorrectLabel = new JLabel("0");
        incorrectLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        questionStatusPanel.add(incorrectLabel, gridPos(1, 2, GridBagConstraints.EAST));

        totalQuestionsLabel = new JLabel("000");
        totalQuestionsLabel.setHorizontalAlignment(SwingConstants.LEFT);
        questionStatusPanel.add(totalQuestionsLabel, gridPos(3, 0, GridBagConstraints.WEST));

        questionStatusPanel.add(new JLabel("/"), gridPos(2, 0, GridBagConstraints.CENTER));

        mainButton = new JButton();
        mainButton.setText(NEXT_QUESTION_TEXT);
        setFontSize(mainButton, 16);
        bottomBarPanel.add(mainButton, BorderLayout.CENTER);

        mainButton.addActionListener(this::mainButtonAction);

        KeyListener keyListener = new LetterKeyListener();
        frame.addKeyListener(keyListener);
        mainPanel.addKeyListener(keyListener);
        questionBox.addKeyListener(keyListener);
        answerPanel.addKeyListener(keyListener);
        mainButton.addKeyListener(keyListener);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Testownik");
        Testownik inst = new Testownik(frame);
        frame.setContentPane(inst.mainPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        inst.firstStart();
    }

    private void setFontSize(JComponent component, float size) {
        Font font = component.getFont();
        font = font.deriveFont(size);
        component.setFont(font);
    }

    private void makeBold(JComponent component) {
        Font font = component.getFont();
        font = font.deriveFont(font.getStyle() | Font.BOLD);
        component.setFont(font);
    }

    private JLabel makeBoldLabel(String text) {
        JLabel label = new JLabel(text);
        makeBold(label);
        return label;
    }

    private GridBagConstraints gridPos(int gridx, int gridy, int anchor) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.anchor = anchor;
        return gbc;
    }

    private void firstStart() {
        // Find database
        try {
            Path jarPath = Paths.get(Testownik.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            if (jarPath.toString().endsWith(".jar")) {
                jarPath = jarPath.getParent();
            }
            dbPath = jarPath.resolve("qdb");
        } catch (Exception e) {
            dbPath = null;
        }

        reloadDatabase();
        showStartScreen();
    }

    private void getDatabasePath() {
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(Paths.get(".").toAbsolutePath().normalize().toFile());
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(java.io.File f) {
                return true;
            }

            @Override
            public String getDescription() {
                return "Folder z bazą pytań";
            }
        });
        fc.setDialogTitle("Otwórz folder z bazą pytań…");

        fc.showOpenDialog(null);
        if (fc.getSelectedFile() != null) {
            dbPath = fc.getSelectedFile().toPath();
        }
    }

    private void reloadDatabase() {
        while (dbPath == null || Files.notExists(dbPath)) {
            showError("Nie znaleziono bazy danych. Wskaż folder z pytaniami (qdb).");
            getDatabasePath();
        }
        try {
            List<Path> files = Files.list(dbPath).filter(path -> path.toString().endsWith(".txt")).sorted().collect(Collectors.toList());
            questions = new ArrayList<>();
            questions.ensureCapacity(files.size());

            for (Path file : files) {
                try {
                    List<String> lines = Files.readAllLines(file, java.nio.charset.Charset.forName("UTF-8"));
                    Question q = new Question(lines, dbPath);
                    questions.add(q);
                } catch (Exception e) {
                    throw new Exception(String.format("Error while reading %s: %s", file, e), e);
                }
            }
        } catch (Exception e) {
            showError(e.toString());
            System.exit(1);
        }

        try {
            Path file = dbPath.resolve("splash.nfo");
            splash = Files.readAllLines(file, java.nio.charset.Charset.forName("UTF-8"));
        } catch (IOException e) {
            // Ignore this one.
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(frame, message, "Błąd", JOptionPane.ERROR_MESSAGE);
    }

    private void startGame() {
        startGame(questions);
    }

    private void startGame(List<Question> questionsToAsk) {
        this.questionsToAsk = new ArrayList<>(questionsToAsk);
        if (shuffleQuestions.isSelected()) {
            Collections.shuffle(this.questionsToAsk);
        } else {
            this.questionsToAsk.sort(Comparator.comparing(Question::getNumber));
        }
        questionsCorrect.clear();
        questionsIncorrect.clear();
        questionIndex = -1;
        nextQuestion();
    }

    private void nextQuestion() {
        hasBeenChecked = false;
        questionIndex += 1;
        if (questionIndex >= questionsToAsk.size()) {
            // end of questions
            questionIndex -= 1;
            showEndScreen();
            return;
        }
        updateCounters();
        currentQuestion = questionsToAsk.get(questionIndex);
        ArrayList<Answer> currentAnswers = new ArrayList<>(currentQuestion.getAnswers());

        answerPanel.removeAll();
        answerFormComponents.clear();
        orderedAnswerToggles.clear();

        if (currentQuestion.hasShuffle() && shuffleAnswers.isSelected()) {
            Collections.shuffle(currentAnswers);
        }
        ButtonGroup buttonGroup = new ButtonGroup();
        JToggleButton answerToggle;

        questionBox.setText(currentQuestion.toString());

        refreshAll();

        if (currentQuestion.hasImage()) {
            showImage();
            final ImageIcon imageIcon = new StretchIcon(currentQuestion.getImage().toString(), "Materiał graficzny do pytania");
            imageLabel.setPreferredSize(new Dimension(imageIcon.getIconWidth(), imageIcon.getIconHeight()));
            imageLabel.setIcon(imageIcon);
        } else {
            hideImage();
        }

        final JLabel prompt = new JLabel();
        setFontSize(prompt, 9);
        if (currentQuestion.hasMultipleCorrectAnswers()) {
            prompt.setText("Zaznacz wszystkie poprawne odpowiedzi.");
            mainButton.setText(CHECK_TEXT);
            hasCheckboxes = true;
        } else {
            prompt.setText("Zaznacz jedną odpowiedź.");
            mainButton.setText(NEXT_QUESTION_TEXT);
            hasCheckboxes = false;
        }
        answerPanel.add(prompt);

        for (Answer answer : currentAnswers) {
            if (hasCheckboxes) {
                // Add checkboxes
                answerToggle = new JCheckBox(answer.toString());
                answerToggle.addActionListener(this::checkboxListener);
                answerToggle.setIcon(Icons.CHECKBOX_UNSEL_UNKNOWN);
                answerToggle.setSelectedIcon(Icons.CHECKBOX_SEL_UNKNOWN);
                answerToggle.setRolloverIcon(Icons.CHECKBOX_UNSEL_HL);
                answerToggle.setRolloverSelectedIcon(Icons.CHECKBOX_SEL_HL);
            } else {
                // Add radiobuttons
                answerToggle = new JRadioButton(answer.toString());
                answerToggle.addActionListener(this::radioListener);
                buttonGroup.add(answerToggle);
                answerToggle.setIcon(Icons.RADIO_UNSEL_UNKNOWN);
                answerToggle.setSelectedIcon(Icons.RADIO_SEL_UNKNOWN);
                answerToggle.setRolloverIcon(Icons.RADIO_UNSEL_HL);
                answerToggle.setRolloverSelectedIcon(Icons.RADIO_SEL_HL);
            }
            answerFormComponents.put(answerToggle, answer);
            orderedAnswerToggles.add(answerToggle);
            answerToggle.setIconTextGap(5);
            setFontSize(answerToggle, TOGGLE_SIZE);
            answerPanel.add(answerToggle);
        }
        questionBox.requestFocus();
        mainButton.setEnabled(false);
        refreshAll();
    }

    private void updateCounters() {
        questionSeqLabel.setText(String.format("%03d", questionIndex + 1));
        totalQuestionsLabel.setText(String.format("%03d", questionsToAsk.size()));
        correctLabel.setText(String.format("%d", questionsCorrect.size()));
        incorrectLabel.setText(String.format("%d", questionsIncorrect.size()));
    }

    private void showStartScreen() {
        mainButton.setEnabled(true);
        mainButton.setText(START_TEXT);
        answerPanel.removeAll();

        if (!splash.isEmpty()) {
            questionBox.setText(splash.get(0));
        } else {
            questionBox.setText("Testownik");
        }

        JButton changeDatabaseButton = new JButton();
        changeDatabaseButton.setText("Zmień bazę danych…");
        changeDatabaseButton.addActionListener(e -> {
            getDatabasePath();
            reloadDatabase();
            showStartScreen();
        });
        JLabel dbSize = new JLabel(String.format("%d pytań w bazie", questions.size()));
        answerPanel.add(changeDatabaseButton);
        answerPanel.add(dbSize);

        shuffleQuestions = new JCheckBox("Losowa kolejność pytań", true);
        shuffleAnswers = new JCheckBox("Losowa kolejność odpowiedzi", true);
        answerPanel.add(shuffleQuestions);
        answerPanel.add(shuffleAnswers);

        final JLabel row0 = makeBoldLabel("Informacje o bazie:");
        answerPanel.add(row0);

        for (String message : splash) {
            answerPanel.add(new JLabel(message));
        }

        final JLabel row1 = makeBoldLabel("Gra z klawiatury:");
        final JLabel row2 = new JLabel("Klawisze a-i wybierają opcję oznaczoną wskazaną literą.");
        final JLabel row3 = new JLabel("Klawisze 1-9 wybierają opcję zgodnie z kolejnością wyświetlania na ekranie.");
        final JLabel row4 = new JLabel("Spacja sprawdza odpowiedź (wybór wielokrotny) lub przechodzi do kolejnego pytania.");

        showTestownikCopyright();
        answerPanel.add(row1);
        answerPanel.add(row2);
        answerPanel.add(row3);
        answerPanel.add(row4);
        hideImage();
        refreshAll();
        mainButton.requestFocus();

    }

    private void showEndScreen() {
        mainButton.setEnabled(false);
        updateCounters();
        questionBox.setText("To już koniec...");
        hideImage();
        answerPanel.removeAll();
        final int totalC = questionsToAsk.size();
        final int correctC = questionsCorrect.size();
        final int incorrectC = questionsIncorrect.size();
        final double percentage = ((double)correctC / totalC) * 100;
        final JLabel row1 = new JLabel(String.format("Odpowiedziałeś/aś na %d pytań, w tym:", totalC));
        final JLabel row2 = new JLabel(String.format("- %d poprawnie", correctC));
        final JLabel row3 = new JLabel(String.format("- %d niepoprawnie", incorrectC));
        final JLabel row4 = new JLabel(String.format("%d%% poprawnych odpowiedzi", (int)percentage));
        setFontSize(row4, 20);
        final JLabel row5 = new JLabel("Czy chcesz zagrać jeszcze raz?");

        JButton allButton = new JButton("Tak, zadaj mi wszystkie pytania");
        JButton incorrectButton = new JButton("Tak, zadaj mi tylko pytania z błędną odpowiedzią");
        JButton noButton = new JButton("Nie, dziękuję");


        allButton.addActionListener(e -> startGame());
        incorrectButton.addActionListener(e -> startGame(questionsIncorrect));
        noButton.addActionListener(e -> frame.dispose());

        if (incorrectC == 0) {
            incorrectButton.setEnabled(false);
        }

        answerPanel.add(row1);
        answerPanel.add(row2);
        answerPanel.add(row3);
        answerPanel.add(row4);
        answerPanel.add(row5);
        answerPanel.add(allButton);
        answerPanel.add(incorrectButton);
        answerPanel.add(noButton);

        final JLabel row9 = makeBoldLabel("Informacje o bazie:");
        answerPanel.add(row9);

        for (String message : splash) {
            answerPanel.add(new JLabel(message));
        }

        showTestownikCopyright();

        refreshAll();
        answerPanel.repaint();

        allButton.requestFocus();
    }

    private void showTestownikCopyright() {
        final JLabel copyright0 = makeBoldLabel("Informacje o testowniku:");
        final JLabel copyright1 = new JLabel("Autor testownika (v4): Krzysztof Wojciechowski");
        final JLabel copyright2 = new JLabel("Licencja testownika: MIT. Wykorzystano klasę StretchIcon autorstwa Darryla Burke.");
        final JLabel copyright3 = new JLabel("Kod źródłowy: https://bitbucket.org/k_wojciechowski/pwr_testownik");

        answerPanel.add(copyright0);
        answerPanel.add(copyright1);
        answerPanel.add(copyright2);
        answerPanel.add(copyright3);
    }

    @SuppressWarnings("unused")
    private void mainButtonAction(ActionEvent e) {
        switch (mainButton.getText()) {
            case NEXT_QUESTION_TEXT:
                nextQuestion();
                break;
            case START_TEXT:
                startGame();
                break;
            case CHECK_TEXT:
                checkAnswers();
                mainButton.requestFocus();
                break;
            default:
                showError("Main button has unknown action");
        }
    }

    private void checkAnswers() {
        if (hasBeenChecked) {
            return;
        }
        hasBeenChecked = true;
        int correct = 0, incorrect = 0;
        for (java.util.Map.Entry<JToggleButton, Answer> entry : answerFormComponents.entrySet()) {
            JToggleButton jToggleButton = entry.getKey();
            Answer answer = entry.getValue();

            Boolean answerIcon = null; // no icon

            if (jToggleButton.isSelected() && answer.isCorrect()) {
                ++correct;
                // green checkmark
                answerIcon = true;
            } else if (jToggleButton.isSelected() != answer.isCorrect()) {
                ++incorrect;
                // red cross
                answerIcon = false;
            }
            if (!jToggleButton.isSelected() && answer.isCorrect()) {
                // black checkmark
                answerIcon = true;
            }
            jToggleButton.setIcon(Icons.getToggleIcon(jToggleButton, answerIcon));
            jToggleButton.setSelectedIcon(jToggleButton.getIcon());
            jToggleButton.setRolloverIcon(jToggleButton.getIcon());
            jToggleButton.setRolloverSelectedIcon(jToggleButton.getIcon());
            setFontSize(jToggleButton, TOGGLE_SIZE);
        }

        JLabel resultLabel = new JLabel("?");
        resultLabel.setIconTextGap(5);
        setFontSize(resultLabel, 32);

        if (incorrect == 0 && correct > 0) {
            resultLabel.setText("Poprawna odpowiedź");
            resultLabel.setIcon(Icons.CORRECT_BIG);
            questionsCorrect.add(currentQuestion);
        } else if (correct == 0) {
            resultLabel.setText("Niepoprawna odpowiedź");
            resultLabel.setIcon(Icons.INCORRECT_BIG);
            questionsIncorrect.add(currentQuestion);
        } else {
            resultLabel.setText("Częściowo poprawnie");
            resultLabel.setIcon(Icons.PARTIAL_BIG);
            questionsIncorrect.add(currentQuestion);
        }

        answerPanel.add(resultLabel);
        updateCounters();
        mainButton.setText(NEXT_QUESTION_TEXT);
        mainButton.setEnabled(true);
        mainButton.requestFocus();
    }

    private void showImage() {
        if (imageVisible) {
            return;
        }
        imageContainer.setVisible(true);
        questionExtrasSplitPane.setDividerLocation(imageHeight);
        imageVisible = true;
    }

    private void hideImage() {
        if (!imageVisible) {
            return;
        }
        imageHeight = questionExtrasSplitPane.getDividerLocation();
        questionExtrasSplitPane.setDividerLocation(0);
        imageContainer.setVisible(false);
        imageVisible = false;
    }

    private void refreshAll() {
        questionBox.revalidate();
        questionBox.repaint();
        answerPanel.revalidate();
        answerPanel.repaint();
        mainPanel.revalidate();
        mainPanel.repaint();
        // frame.pack();
    }

    @SuppressWarnings("unused")
    private void checkboxListener(ActionEvent e) {
        final boolean anySelected = orderedAnswerToggles.stream().anyMatch(JToggleButton::isSelected);
        mainButton.setEnabled(anySelected);
        if (anySelected) {
            mainButton.requestFocus();
        } else {
            questionBox.requestFocus();
        }
    }

    @SuppressWarnings("unused")
    private void radioListener(ActionEvent e) {
        checkAnswers();
    }

    class LetterKeyListener implements KeyListener {
        /**
         * Invoked when a key has been typed.
         * See the class description for {@link KeyEvent} for a definition of
         * a key typed event.
         */
        @Override
        public void keyTyped(KeyEvent e) {
            char symbol = e.getKeyChar();
            if (symbol >= '0' && symbol <= '9') {
                int index = symbol - 0x30 - 1;
                if (index == -1) {
                    index = 9; // '0' -> -1 -> 9 (10th entry)
                }
                simulateToggle(orderedAnswerToggles.get(index));
                return;
            }
            for (java.util.Map.Entry<JToggleButton, Answer> entry : answerFormComponents.entrySet()) {
                JToggleButton jToggleButton = entry.getKey();
                Answer answer = entry.getValue();
                if (answer.getSymbol() == symbol) {
                    simulateToggle(jToggleButton);
                }
            }
        }

        private void simulateToggle(JToggleButton jToggleButton) {
            jToggleButton.setSelected(!jToggleButton.isSelected());
            if (hasCheckboxes) {
                checkboxListener(null);
            } else {
                radioListener(null);
            }
        }

        /**
         * Invoked when a key has been pressed.
         * See the class description for {@link KeyEvent} for a definition of
         * a key pressed event.
         */
        @Override
        public void keyPressed(KeyEvent e) {

        }

        /**
         * Invoked when a key has been released.
         * See the class description for {@link KeyEvent} for a definition of
         * a key released event.
         */
        @Override
        public void keyReleased(KeyEvent e) {

        }
    }
}
