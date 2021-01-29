package org.lizard;

import org.lizard.user.Prompter;

import java.util.Scanner;

public class Game {
    GameDictionary gameDictionary = GameDictionary.getGameDictionary();
    Player player = new Player("Edgar");
    TextParser parser = new TextParser(gameDictionary);
    Prompter prompter = new Prompter(new Scanner(System.in));
    Board board = new Board();
    Actions actions = new Actions(board, player);

    public void start() {
        new Direction("north");
        new Direction("south");
        new Direction("east");
        new Direction("west");
        gameDictionary.printNouns();
        while(true){
            String input = prompter.promptPlayer("What you wanna do?");
            Command command = parser.parse(input);
            actions.execute(command);
        }
    }

}