
//MOST UPDATED

package org.lizard;

import org.lizard.constants.GameInformation;
import org.lizard.constants.Settings;
import org.lizard.util.Music;
import org.lizard.util.Screen;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MyJFrame extends JFrame implements ActionListener {

    Board board = new Board();
    Player player = new Player("Player");
    Combat combat = new Combat();
    Actions actions = new Actions(board, player, this, combat);
    GameDictionary gameDictionary = GameDictionary.getGameDictionary();
    TextParser parser = new TextParser(gameDictionary);
    String soundName = "princeofdarkness.wav";
    Clip clip = null;
    JTextArea rpsGame;
    JPanel promptPanel;
    JScrollPane scrollPane;
    boolean bossDead = false;
    String result;
    JLabel inputFromUser = new JLabel();
    JLabel imageLabel;
    JTextField textField = new JTextField();
    JButton enterGame, quitGame, helpBtn, musicBtn;
    JSlider volumeSlider;
    JFrame frame;
    JFrame combatWindow;
    JPanel titlePanel, volumePanel;
    JTextArea mainStoryText, instructionsTxt;
    JPanel instructionsPanel, storyPanel, mapPanel, inventoryPanel;
    JPanel inputPanel;
    JTextField numInput;
    BufferedImage img;

    boolean calledOnce = false;

    MyJFrame() {
        new Funsies("jump", "Good for you");
        new Funsies("hello", "hi");
        new Funsies("help", "examine something");
        new Funsies("where", "idk figure it out");
        new Funsies("what", "idk figure it out");

        frame = createGameJFrame();

        //create background image
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File("bluePuppeteer.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert img != null;
        // Adjust image to fit game
        Image bgImg = img.getScaledInstance(1200, 800, Image.SCALE_SMOOTH);
        ImageIcon imageIcon = new ImageIcon(bgImg);

        // Create the image label or "background"
        imageLabel = new JLabel(imageIcon);
        // IMPORTANT : SetContentPane is similar to setting the background
        // It sets the root JComponent to display on the frame
        frame.setContentPane(imageLabel);
        frame.setBackground(Color.BLACK);

        //button to play the game
        enterGame = new JButton("Enter Game");

        int buttonWidth = 200;
        int buttonHeight = 60;

        // Set the button's properties
        enterGame.setBounds(
                Screen.getLeftXCoordinateForElement(buttonWidth),
                Screen.getTopYCoordinateForElement(buttonHeight),
                buttonWidth,
                buttonHeight);
        enterGame.addActionListener(this);

        //button to quit the game
        quitGame = new JButton("Quit Game");
        quitGame.setBounds(990, 415, 120, 40);
        quitGame.addActionListener(this);

        //button to get the help window that shows instructions to the game.
        helpBtn = new JButton("Guidance");
        helpBtn.setBounds(1120, 415, 120, 40);
        helpBtn.addActionListener(this);

        //button to get the help window that shows instructions to the game.
        musicBtn = new JButton("Music");
        musicBtn.setBounds(1250, 415, 120, 40);
        musicBtn.addActionListener(this);

        FloatControl gainControl =
                (FloatControl) Music.clip.getControl(FloatControl.Type.MASTER_GAIN);

        //Create the slider to adjust volume
        volumeSlider = new JSlider(JSlider.VERTICAL,
                (int) gainControl.getMinimum(),
                (int) gainControl.getMaximum(),
                (int) gainControl.getValue());
        volumeSlider.setMajorTickSpacing(25);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setForeground(Color.BLUE);
        volumeSlider.addChangeListener(event -> {
            // Update global volume adjustment whenever it changes
            JSlider volumeSlider = (JSlider) event.getSource();
            if (!volumeSlider.getValueIsAdjusting()) {
                Settings.VOLUME_SETTING = volumeSlider.getValue();
                Music.adjustVolume((float) Settings.VOLUME_SETTING, gainControl);
            }
        });

        // Create a volume panel "container" to hold the slider
        volumePanel = new JPanel();
        volumePanel.setBounds(1400, 550, 80, 250);
        volumePanel.add(volumeSlider);

        //panel with the game title
        titlePanel = new JPanel();
        titlePanel.setBackground(Color.black);
        titlePanel.setBounds(0, 80, 1500, 80);

        JLabel welcome = new JLabel("The Lizard Key Game!");
        welcome.setFont(new Font("IronWood", Font.BOLD, 30));
        welcome.setForeground(Color.green);
        titlePanel.add(welcome, BorderLayout.CENTER);

        // Add components to the frame
        frame.add(titlePanel);
        frame.add(enterGame);

        // Make the frame visible to the player
        frame.setVisible(true);

    }

    // Creates the initial JFrame and sets basic properties
    private JFrame createGameJFrame() {
        JFrame gameFrame = new JFrame();
        // Set title and use String text from GameInformation Constants
        gameFrame.setTitle(GameInformation.TITLE);
        // Set the game container to a specific and absolute size
        gameFrame.setSize(Screen.SPECIFIED_WIDTH, Screen.SPECIFIED_HEIGHT);
        gameFrame.setResizable(false);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        return gameFrame;
    }

    public void createHelpWindow() {
        JFrame helpWindow = new JFrame(); //initiate help window
        Container helpContainer;
        JLabel guidanceTitle;

        //set up help window
        helpWindow.setSize(800, 600);
        helpWindow.setLocation(480, 200);
        helpWindow.setLayout(null); //disables default layout
        helpWindow.setVisible(true); //makes window appear on screen

        //help window container
        helpContainer = helpWindow.getContentPane(); //container inside the window with help content
        helpContainer.setBackground(Color.darkGray);

        //help window title
        guidanceTitle = new JLabel("Guidance");
        guidanceTitle.setBounds(300, -80, 200, 250);
        guidanceTitle.setForeground(Color.orange); //title text color
        guidanceTitle.setFont(new Font("Comic Sans", Font.PLAIN, 36));
        helpContainer.add(guidanceTitle);

        //instructions for the game
        instructionsPanel = new JPanel();
        instructionsPanel.setBounds(30, 90, 720, 450);
        instructionsPanel.setLayout(new BorderLayout());

        //instructions text
        instructionsTxt = new JTextArea();
        instructionsTxt.setText(board.howToPlayInGame());
        instructionsTxt.setMargin(new Insets(10, 10, 10, 10));
        instructionsTxt.setLineWrap(true);
        instructionsTxt.setWrapStyleWord(true);
        instructionsTxt.setForeground(Color.black);
        instructionsTxt.setFont(new Font("Comic Sans", Font.BOLD, 15));
        instructionsTxt.setEditable(false);
        instructionsTxt.setBackground(Color.white);

        //makes story text scrollable
        JScrollPane scrollPane = new JScrollPane(instructionsTxt);
        scrollPane.setPreferredSize(new Dimension(720, 400));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        //add scrollable text to the instructions panel then to the main container.
        instructionsPanel.add(scrollPane);
        helpContainer.add(instructionsPanel);
    }

    public void createGameView() {
        // Create a new "empty" root/base for the new game screen components
        frame.setContentPane(new JLabel());

        img = null;
        try {
            img = ImageIO.read(new File("blackPuppeteer.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert img != null;
        // Create, set properties and add the black puppeteer image to the view
        imageLabel = new JLabel(new ImageIcon(img));
        imageLabel.setLayout(null);
        imageLabel.setBackground(Color.black);
        imageLabel.setBounds(300, 0, 990, 135);
        frame.add(imageLabel); //shows correct location but doesn't remove img

        img = null;
        try {
            img = ImageIO.read(new File("lizard.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert img != null;

        // Create, set properties and add the lizard image to the view
        imageLabel = new JLabel(new ImageIcon(img.getScaledInstance(420, 500, Image.SCALE_SMOOTH)));
        imageLabel.setLayout(null);
        imageLabel.setBackground(Color.black);
        imageLabel.setBounds(30, 260, 420, 500);
        frame.add(imageLabel); //shows correct location but doesn't remove img

        frame.setBackground(Color.black);

        // Add the remaining elements to the view/frame
        frame.add(helpBtn);
        frame.add(quitGame);
        frame.add(musicBtn);
        frame.add(volumePanel);
        titlePanel.setBounds(50, 50, 450, 80);
        frame.add(titlePanel);
        gameScreen(board.introduction());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == quitGame) {
            frame.dispose();
            System.exit(0);
        }
        if (e.getSource() == helpBtn) {
            createHelpWindow();
        }
        if (e.getSource() == musicBtn) {
            // Check if music is already running
            if (Music.isRunning()) {
                Music.stop();
            } else {
                // Otherwise, play music form the specified track
                Music.play();
            }

        }
        if (e.getSource() == enterGame) {


            createGameView();

            mapPanel = new MapView(board.rooms, board.getCurrentRoom().getName());
            mapPanel.setBounds(100, 40, Screen.SPECIFIED_WIDTH, Screen.SPECIFIED_HEIGHT);

            frame.add(mapPanel);
            frame.revalidate();
            frame.repaint();

        }

        if (e.getSource() == textField) {
            result = textField.getText().toLowerCase();
            Command command = parser.parse(result);
            String response = actions.execute(command);
            mainStoryText.setText(response);

            //this code removed map and adds a new one based on the navigation from user input.
            frame.remove(mapPanel);
            frame.repaint();
            frame.revalidate();

            mapPanel = new MapView(board.rooms, board.getCurrentRoom().getName());
            mapPanel.setBounds(100, 40, Screen.SPECIFIED_WIDTH, Screen.SPECIFIED_HEIGHT);
            frame.add(mapPanel);

            // Winning condition check - player has winning key
            if (player.hasWinningKey && board.getCurrentRoom().getName().equalsIgnoreCase("keyRoom")) {
                winScreen();
            }

            if (board.getCurrentRoom().getEnemy() != null && !board.getCurrentRoom().getEnemy().enemyName.equalsIgnoreCase("Copernicus Rex Verwirrtheit Theodore")) {
//                frame.remove(inputPanel);
//                frame.repaint();
//                frame.revalidate();

                displayCombat();
            }

            if (response.equalsIgnoreCase("The sculpture, as you now know, was just Copernicus Rex Verwirrtheit Theodore. The same red liquid from the floor streams from his eyes.") && !bossDead) {
//
                displayCombat();
//                bossDead = true;
            }
            textField.setText("");
        }

        if (e.getSource() == numInput) {
            frame.remove(storyPanel);
            frame.repaint();
            frame.revalidate();

            rpsGame.setText(combat.playerTakesTurn(Integer.parseInt(numInput.getText())));
            if (combat.checkGameEndingStatus().equalsIgnoreCase("Enemy won")) {
                combatWindow.dispose();
                gameOverScreen();
                if (clip != null) {
                    clip.stop();
                }
            } else if (combat.checkGameEndingStatus().equalsIgnoreCase("You defeated the monster!")) {
                combatWindow.dispose();
                if (combat.bossTime) {
                    combat.bossTime = false;
                    board.totalEnemies = -1;

                    gameScreen(actions.execute(new Event(99, board.allItems.get("sculpture"))));
                    frame.setVisible(true);
                    if (clip != null) {
                        clip.stop();
                    }

                } else {

                    if (board.totalEnemies < 0) {
                        gameScreen("Copernicus Rex Verwirrtheit Theodore has fallen in his own world! His magic cape has fallen with him, and you are one step closer to freedom!");
                        combatWindow.dispose();
                    } else {
                        gameScreen("You defeated the monster!");
                    }
                    if (clip != null) {
                        clip.stop();
                    }
                }

            }
            numInput.setText("");
        }
    }

    private void gameScreen(String initialPrint) {

        //main story text
        mainStoryText = new JTextArea();
        mainStoryText.setText(initialPrint);
        mainStoryText.setMargin(new Insets(10, 10, 10, 10));
        mainStoryText.setLineWrap(true);
        mainStoryText.setWrapStyleWord(true);
        mainStoryText.setForeground(Color.white);
        mainStoryText.setFont(new Font("Comic Sans", Font.PLAIN, 16));
        mainStoryText.setEditable(false);
        mainStoryText.setBackground(Color.black);

        //makes story text scrollable
        JScrollPane scrollPane = new JScrollPane(mainStoryText);
        scrollPane.setPreferredSize(new Dimension(700, 250));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        //input prompt
        inputFromUser.setText("What do you want to do?");
        inputFromUser.setFont(new Font("Consolas", Font.BOLD, 20));
        inputFromUser.setForeground(Color.green);

        //area where user enters input
        textField.setPreferredSize(new Dimension(250, 40));
        textField.setFont(new Font("Consolas", Font.BOLD, 15));
        textField.setForeground(Color.blue);
        textField.setBackground(Color.white);
        textField.setCaretColor(Color.blue);//text curser color

        //panel that contains the story text
        storyPanel = new JPanel();
        storyPanel.setBackground(Color.black);
        storyPanel.setBounds(-100, 130, 1800, 280);

        //panel where the input is located.
        inputPanel = new JPanel();
        inputPanel.setBackground(Color.black);
        inputPanel.setBounds(-190, 410, 1800, 60);

//        // Container to hold the inventory panel grid
//        JScrollPane scroll = new JScrollPane(myGrid,
//                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
//                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        //panel where the input is located.
//        inventoryPanel = new JPanel();
//        inventoryPanel.setLayout(new GridLayout(2, 2));
//        inventoryPanel.setBackground(Color.YELLOW);
//        inventoryPanel.setBounds(450, 890, 640, 130);

        // Add the scroll pane with text to its panel container
        storyPanel.add(scrollPane);
        inputPanel.add(inputFromUser);
        inputPanel.add(textField);

        frame.add(storyPanel);
        frame.add(inputPanel);
//        frame.add(inventoryPanel);

        if (!calledOnce) {
            textField.addActionListener(this);
            calledOnce = true;

        }
        textField.requestFocusInWindow();

        frame.setVisible(true);
    }

    //fighting scene with the enemy.
    private void displayCombat() {
        combatWindow = new JFrame(); //initiate game over window

        //set up combat window
        combatWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        combatWindow.setSize(800, 600);
        combatWindow.setVisible(true); //makes window appear on screen
        combatWindow.setLocation(480, 200);

        rpsGame = new JTextArea();
        if (board.totalEnemies < 1) {
            rpsGame.setText("You have finally come face to face with Copernicus Rex Verwirrtheit Theodore!" +
                    "This is your chance to finally defeat your captor and gain the flight to the final key to escaping." +
                    "To defeat him, you must win in combat... or rock, paper, scissors." +
                    "\nPlease choose from the following numbers:" +
                    "\n1: ROCK" +
                    "\n2: PAPER" +
                    "\n3: SCISSOR");
        } else {
            rpsGame.setText("You have come face to face with a monster!" +
                    "To defeat it, you must win in combat... of rock, paper, scissors." +
                    "\nPlease choose from the following numbers:" +
                    "\n1: ROCK" +
                    "\n2: PAPER" +
                    "\n3: SCISSOR");
        }

//        rpsGame.setPreferredSize(new Dimension(500, 100));
        rpsGame.setBounds(50, 50, 100, 100);
        rpsGame.setMargin(new Insets(10, 10, 10, 10));

        rpsGame.setFont(new Font("Sans Script", Font.BOLD, 15));
        rpsGame.setLineWrap(true);
        rpsGame.setWrapStyleWord(true);
//        rpsGame.setBorder(BorderFactory.createBevelBorder(1));
        rpsGame.setForeground(Color.orange);
        rpsGame.setBackground(Color.darkGray);
        rpsGame.setEditable(false);

        numInput = new JTextField();
        numInput.setPreferredSize(new Dimension(500, 50));
        numInput.setBackground(Color.white);

        scrollPane = new JScrollPane(rpsGame);
        scrollPane.setPreferredSize(new Dimension(500, 200));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel panel = new JPanel();
        panel.add(scrollPane);

        JLabel promptLabel = new JLabel();
        promptLabel.setText("What number do you choose?");
        promptLabel.setFont(new Font("Consolas", Font.BOLD, 20));
        promptLabel.setForeground(Color.white);
        promptPanel = new JPanel();
        promptPanel.add(promptLabel, BorderLayout.CENTER);
        promptPanel.add(numInput);
        promptPanel.setBackground(Color.blue);

        JPanel rpgImgPanel = new JPanel();
        rpgImgPanel.setBackground(Color.blue);
        rpgImgPanel.setBounds(0, 300, 800, 400);

        JLabel imgLabel = new JLabel();
        imgLabel.setIcon(new ImageIcon("RPS.png"));
        imgLabel.setLayout(new FlowLayout(FlowLayout.CENTER));
        rpgImgPanel.add(imgLabel);
        combatWindow.add(rpgImgPanel);

        combatWindow.add(scrollPane, BorderLayout.NORTH);
        combatWindow.add(promptPanel);
        combat.startCombat(player, board.getCurrentRoom(), board);
        numInput.addActionListener(this);
    }

    public void winScreen() {
        JFrame winScreen = new JFrame(); //initiate help window
        Container winContainer;
        JLabel victoryTitle;
        JTextArea winTextArea;
        JPanel winTextPanel;

        //set up win window
        winScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        winScreen.setSize(800, 600);
        winScreen.setLocation(480, 200);
        winScreen.setVisible(true);
//        winScreen.setLayout(null); //disables default layout

        //win window container
        winContainer = winScreen.getContentPane(); //container inside the window with help content
        winContainer.setBackground(Color.black);

        //win window title
        victoryTitle = new JLabel("Congratulations, you won!!!");
        victoryTitle.setBounds(180, -70, 600, 250);
        victoryTitle.setForeground(Color.orange); //title text color
        victoryTitle.setFont(new Font("Comic Sans", Font.PLAIN, 36));
        winContainer.add(victoryTitle);

        //victory text panel
        winTextPanel = new JPanel();
        winTextPanel.setBounds(380, 100, 380, 250);
        winTextPanel.setBackground(Color.black);

        //victory text
        winTextArea = new JTextArea("You use the lizard key on the door to exit." +
                "\n\nDarkness surrounds your and wind presses against you back as if the ground is being pulled beneath you." +
                "You close your eyes to avoid dizziness, only for the movement around you to stop." +
                "Upon opening your eyes, you are staring out a small window with people in white scrubs passing in a hall." +
                "You turn around to see padded walls, only to realize that you have escaped Copernicus Rex Verwirrtheit Theodore for now.");
        winTextArea.setLineWrap(true);
        winTextArea.setWrapStyleWord(true);
        winTextArea.setForeground(Color.white);
        winTextArea.setBounds(380, 100, 380, 250);
        winTextArea.setFont(new Font("Comic Sans", Font.BOLD, 15));
        winTextArea.setEditable(false);
        winTextArea.setBackground(Color.black);
        winTextPanel.add(winTextArea);
        winContainer.add(winTextPanel);

        JLabel imgLabel = new JLabel();
        imgLabel.setIcon(new ImageIcon("lizardKey.png"));
        imgLabel.setLayout(new FlowLayout(FlowLayout.CENTER));
        winContainer.add(imgLabel);
    }

    public void gameOverScreen() {
        JFrame gameOverScreen = new JFrame(); //initiate game over window
        JTextArea gameOverText;
        JPanel gPanel;
        JPanel textPanel;
        Container container;

        //set up game over window
        gameOverScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameOverScreen.setSize(800, 600);
        gameOverScreen.setVisible(true); //makes window appear on screen
        gameOverScreen.setLayout(null);
        gameOverScreen.setBackground(Color.black);
        gameOverScreen.setLocation(480, 200);

        container = gameOverScreen.getContentPane();
        container.setBackground(Color.decode("#191919"));

        //game over text panel
        textPanel = new JPanel();
        textPanel.setBackground(Color.decode("#191919"));
        textPanel.setBounds(160, 40, 500, 150);
        textPanel.setLayout(new FlowLayout(FlowLayout.CENTER));


        //game over text
        gameOverText = new JTextArea("\n\nYou could not escape Mr.Rex's clutches and will be forever stuck in this nightmare. " +
                "Nevertheless, your fate is only sealed when you give up trying!");
        gameOverText.setPreferredSize(new Dimension(480, 200));
        gameOverText.setFont(new Font("Comic Sans", Font.PLAIN, 18));
        gameOverText.setLineWrap(true);
        gameOverText.setWrapStyleWord(true);
        gameOverText.setEditable(false);
        gameOverText.setForeground(Color.red);
        gameOverText.setBackground(Color.decode("#191919"));
        textPanel.add(gameOverText);
        container.add(textPanel);

        //image panel
        gPanel = new JPanel();
        gPanel.setBackground(Color.decode("#191919"));
        gPanel.setBounds(0, 100, 800, 400);
        container.add(gPanel);

        //game over image
        JLabel imgLabel = new JLabel();
        imgLabel.setIcon(new ImageIcon("gameOver.png"));
        imgLabel.setLayout(new FlowLayout(FlowLayout.CENTER));
        gPanel.add(imgLabel);

    }

}
