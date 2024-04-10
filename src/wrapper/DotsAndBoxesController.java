package wrapper;

import java.net.URL;
import java.util.*;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;

public class DotsAndBoxesController {
    char[][] board = new char[7][7];
    int playerScore = 0;
    Label[] labels;
    Rectangle[] borders;

    private int getScoreAi(){
        int counter = 0;
        boolean finished = true;
        for(Label i : labels){
            if(i.getText().equals("Computer")){
                counter++;
            }else if(i.getText().equals("Empty")){
                finished = false;
            }

        }
        if(!finished){
            return counter;
        }else return -1;

    }


    private void changeLabelValue(String labelID, String value){
        for (Label label : labels) {
            if (label.getId().equals(labelID)) {
                label.setText(value);
            }
        }
    }

    private void changeBorderValue(String rectangleID){
        for(Rectangle border : borders){
            if(border.getId().equals(rectangleID) && border.getFill() == Color.WHITE){
                border.setFill(Color.RED);
            }
        }
    }

    private void printBoard(){
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                System.out.print(board[i][j] + " ");
                if(board[i][j] == '+' || board[i][j] == '2'){
                    changeLabelValue("lbl" + i + "" + j, board[i][j] == '+' ? "Player" : "Computer");
                }
            }
            System.out.println();
        }
    }

    public void checkForBoxes(char[][] board ,int x, int y, Boolean isHorizontal, boolean isUser){
        //horizontal up
        if(x > 0){
            if(isHorizontal && board[x - 1][y - 1] == '|' && board[x - 2][y] == '-' && board[x - 1][y + 1] == '|') {
                board[x - 1][y] = isUser ? '+' : '2';
                if(isUser) playerScore++;
            }
        }
        //horizontal down
        if(x < 6){
            if(isHorizontal && board[x + 1][y - 1] == '|' && board[x + 2][y] == '-' && board[x + 1][y + 1] == '|') {
                board[x + 1][y] = isUser ? '+' : '2';
                if(isUser) playerScore++;
            }
        }
        //vertical left
        if(y > 0){
            if(!isHorizontal && board[x][y - 2] == '|' && board[x - 1][y - 1] == '-' && board[x + 1][y - 1] == '-'){
                board[x][y - 1] = isUser ? '+' : '2';
                if(isUser) playerScore++;
            }
        }
        //vertical right
        if(y < 6){
            if(!isHorizontal && board[x][y + 2] == '|' && board[x + 1][y + 1] == '-' && board[x - 1][y + 1] == '-'){
                board[x][y + 1] = isUser ? '+' : '2';
                if(isUser) playerScore++;
            }
        }
    }

    public void move(char[][] board, int row, int col, boolean isUser){
        if(row == 0 || row == 2 || row == 4 || row == 6){
            board[row][col] = '-';
        }else {
            board[row][col] = '|';
        }
        checkForBoxes(board, row, col, row == 0 || row == 2 || row == 4 || row == 6, isUser);
    }

    public void moveFinal(char[][] board, int row, int col, boolean isUser){
        if(row == 0 || row == 2 || row == 4 || row == 6){
            board[row][col] = '-';
        }else {
            board[row][col] = '|';
        }
        checkForBoxes(board, row, col, row == 0 || row == 2 || row == 4 || row == 6, isUser);
        changeBorderValue("rctBorder" + row + "" + col );
    }

    public char[][] copyBoard(char[][] boardToCopy){
        char[][] copiedBoard = new char[7][7];
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                copiedBoard[i][j] = boardToCopy[i][j];
            }
        }
        return copiedBoard;
    }

    public ArrayList<Pair<Integer, Integer>> getEmptyBorders(char[][] board){
        ArrayList<Pair<Integer, Integer>> emptyBorders = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            for (int j = 1; j < 7; j += 2) {
                if(board[i][j] == '.') emptyBorders.add(new Pair<>(i, j));
            }
            i++;
            if(i == 7) break;
            for (int j = 0; j < 7; j += 2) {
                if(board[i][j] == '.') emptyBorders.add(new Pair<>(i, j));
            }
        }
        return emptyBorders;
    }

    public void aiMove(){
        int oldscore = getScoreAi();
        char[][] stateSave = copyBoard(board);
        int[] moveCoords =  minimax(stateSave, Integer.MIN_VALUE, Integer.MAX_VALUE, 6,  false, 0, 0);
        moveFinal(board, moveCoords[0], moveCoords[1], false);
        printBoard();
        int currentScore = getScoreAi();
        if(oldscore < currentScore){
            aiMove();
        }else if(currentScore == -1){
            evaluate();
        }
    }

    public void aiMoveOnce(){
        char[][] stateSave = copyBoard(board);
        int[] moveCoords =  minimax(stateSave, Integer.MIN_VALUE, Integer.MAX_VALUE, 6,  false, 0, 0);
        moveFinal(board, moveCoords[0], moveCoords[1], false);
        printBoard();
        if(getScoreAi() == -1){
          evaluate();
        }
    }


    public int[] minimax(char[][] givenState, int alpha, int beta, int depth, boolean isPlayer, int x, int y){
        ArrayList<Pair<Integer, Integer>> openBorders = getEmptyBorders(givenState);
        if(openBorders.isEmpty() || depth == 0){
            return new int[]{x, y, getScoreDiff(givenState)};
        }

        if(!isPlayer){
            int[] bestMove = new int[2];
            int score = Integer.MIN_VALUE;
            for (Pair<Integer, Integer> i : openBorders) {
                char[][] currentState = copyBoard(givenState);
                move(currentState, i.getKey(), i.getValue(), isPlayer);
                int[] temp = minimax(currentState, alpha, beta, depth - 1, !isPlayer, i.getKey(), i.getValue());
                if(temp[2] > score){
                    bestMove[0] = i.getKey();
                    bestMove[1] = i.getValue();
                    score = temp[2];
                }
                alpha = Math.max(score, alpha);
                if(beta <= alpha){
                    break;
                }
            }
            return new int[]{bestMove[0], bestMove[1], score};
        }else{
            int[] worstMove = new int[2];
            int score = Integer.MAX_VALUE;
            for(Pair<Integer, Integer> i : openBorders){
                char[][] currentState = copyBoard(givenState);
                move(currentState, i.getKey(), i.getValue(), isPlayer);
                int[] temp = minimax(currentState, alpha, beta, depth - 1, !isPlayer, i.getKey(), i.getValue());
                if(temp[2] < score){
                    worstMove[0] = i.getKey();
                    worstMove[1] = i.getValue();
                    score = temp[2];
                }
                beta = Math.min(beta, score);
                if(beta <= alpha){
                    break;
                }
            }
            return new int[]{worstMove[0], worstMove[1], score};
        }

    }

    public int getScoreDiff (char[][] state){
        int aiScore = 0;
        int playerScore = 0;
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                if(state[i][j] == '+'){
                    playerScore++;
                }else if(state[i][j] == '2'){
                    aiScore++;
                }
            }
        }
        return aiScore - playerScore;
    }

    private void playerMove(Rectangle border, int x, int y, char sign){
        if(border.getFill() == Color.WHITE){
            border.setFill(Color.BLUE);
            board[x][y] = sign;
            int oldScore = playerScore;
            checkForBoxes(board, x, y, sign == '-', true);
            printBoard();
//            if(oldScore == playerScore){
//                aiMove();
//            }else if(getScoreAi() == -1){
//                evaluate();
//            }

            if(getScoreAi() == -1){
                evaluate();
            }else aiMoveOnce();

        }else{
            System.out.println("The border is already filled");
        }
    }

    private void evaluate(){
        int scoreAi = 0;
        int scorePlayer = 0;
        for(Label i : labels){
            if(i.getText().equals("Player")){
                scorePlayer++;
            }else scoreAi++;
        }
        String winner;
        if(scorePlayer > scoreAi){
            winner = "Player";
        }else{
            winner = "Computer";
        }
        String result = String.format("The game is over! \n The winner is the %s", winner);
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setContentText(result);
        a.showAndWait();
        Platform.exit();
    }

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label lbl11;

    @FXML
    private Label lbl13;

    @FXML
    private Label lbl15;

    @FXML
    private Label lbl31;

    @FXML
    private Label lbl33;

    @FXML
    private Label lbl35;

    @FXML
    private Label lbl51;

    @FXML
    private Label lbl53;

    @FXML
    private Label lbl55;

    @FXML
    private Rectangle rctBorder01;

    @FXML
    private Rectangle rctBorder03;

    @FXML
    private Rectangle rctBorder05;

    @FXML
    private Rectangle rctBorder10;

    @FXML
    private Rectangle rctBorder12;

    @FXML
    private Rectangle rctBorder14;

    @FXML
    private Rectangle rctBorder16;

    @FXML
    private Rectangle rctBorder21;

    @FXML
    private Rectangle rctBorder23;

    @FXML
    private Rectangle rctBorder25;

    @FXML
    private Rectangle rctBorder30;

    @FXML
    private Rectangle rctBorder32;

    @FXML
    private Rectangle rctBorder34;

    @FXML
    private Rectangle rctBorder36;

    @FXML
    private Rectangle rctBorder41;

    @FXML
    private Rectangle rctBorder43;

    @FXML
    private Rectangle rctBorder45;

    @FXML
    private Rectangle rctBorder50;

    @FXML
    private Rectangle rctBorder52;

    @FXML
    private Rectangle rctBorder54;

    @FXML
    private Rectangle rctBorder56;

    @FXML
    private Rectangle rctBorder61;

    @FXML
    private Rectangle rctBorder63;

    @FXML
    private Rectangle rctBorder65;

    @FXML
    void rctBorder01OnClick(MouseEvent event) {
        playerMove(rctBorder01, 0, 1, '-');
    }

    @FXML
    void rctBorder03OnClick(MouseEvent event) {
        playerMove(rctBorder03, 0, 3, '-');
    }

    @FXML
    void rctBorder05OnClick(MouseEvent event) {
        playerMove(rctBorder05, 0, 5, '-');
    }

    @FXML
    void rctBorder10OnClick(MouseEvent event) {
        playerMove(rctBorder10, 1, 0, '|');
    }

    @FXML
    void rctBorder12OnClick(MouseEvent event) {
        playerMove(rctBorder12, 1, 2, '|');
    }

    @FXML
    void rctBorder14OnClick(MouseEvent event) {
        playerMove(rctBorder14, 1, 4, '|');
    }

    @FXML
    void rctBorder16OnClick(MouseEvent event) {
        playerMove(rctBorder16, 1, 6, '|');
    }

    @FXML
    void rctBorder21OnClick(MouseEvent event) {
        playerMove(rctBorder21, 2, 1, '-');
    }

    @FXML
    void rctBorder23OnClick(MouseEvent event) {
        playerMove(rctBorder23, 2, 3, '-');
    }

    @FXML
    void rctBorder25OnClick(MouseEvent event) {
        playerMove(rctBorder25, 2, 5, '-');
    }

    @FXML
    void rctBorder30OnClick(MouseEvent event) {
        playerMove(rctBorder30, 3, 0, '|');
    }

    @FXML
    void rctBorder32OnClick(MouseEvent event) {
        playerMove(rctBorder32, 3, 2, '|');
    }

    @FXML
    void rctBorder34OnClick(MouseEvent event) {
        playerMove(rctBorder34, 3, 4, '|');
    }

    @FXML
    void rctBorder36OnClick(MouseEvent event) {
        playerMove(rctBorder36, 3, 6, '|');
    }

    @FXML
    void rctBorder41OnClick(MouseEvent event) {
        playerMove(rctBorder41, 4, 1, '-');
    }

    @FXML
    void rctBorder43OnClick(MouseEvent event) {
        playerMove(rctBorder43, 4, 3, '-');
    }

    @FXML
    void rctBorder45OnClick(MouseEvent event) {
        playerMove(rctBorder45, 4, 5, '-');
    }

    @FXML
    void rctBorder50OnClick(MouseEvent event) {
        playerMove(rctBorder50, 5, 0, '|');
    }

    @FXML
    void rctBorder52OnClick(MouseEvent event) {
        playerMove(rctBorder52, 5, 2, '|');
    }

    @FXML
    void rctBorder54OnClick(MouseEvent event) {
        playerMove(rctBorder54, 5, 4, '|');
    }

    @FXML
    void rctBorder56OnClick(MouseEvent event) {
        playerMove(rctBorder56, 5, 6, '|');
    }

    @FXML
    void rctBorder61OnClick(MouseEvent event) {
        playerMove(rctBorder61, 6, 1, '-');
    }

    @FXML
    void rctBorder63OnClick(MouseEvent event) {
        playerMove(rctBorder63, 6, 3, '-');
    }

    @FXML
    void rctBorder65OnClick(MouseEvent event) {
        playerMove(rctBorder65, 6, 5, '-');
    }

    @FXML
    void initialize() {
        assert lbl11 != null : "fx:id=\"lbl11\" was not injected: check your FXML file 'gui.fxml'.";
        assert lbl13 != null : "fx:id=\"lbl13\" was not injected: check your FXML file 'gui.fxml'.";
        assert lbl15 != null : "fx:id=\"lbl15\" was not injected: check your FXML file 'gui.fxml'.";
        assert lbl31 != null : "fx:id=\"lbl31\" was not injected: check your FXML file 'gui.fxml'.";
        assert lbl33 != null : "fx:id=\"lbl33\" was not injected: check your FXML file 'gui.fxml'.";
        assert lbl35 != null : "fx:id=\"lbl35\" was not injected: check your FXML file 'gui.fxml'.";
        assert lbl51 != null : "fx:id=\"lbl51\" was not injected: check your FXML file 'gui.fxml'.";
        assert lbl53 != null : "fx:id=\"lbl53\" was not injected: check your FXML file 'gui.fxml'.";
        assert lbl55 != null : "fx:id=\"lbl55\" was not injected: check your FXML file 'gui.fxml'.";
        assert rctBorder01 != null : "fx:id=\"rctBorder01\" was not injected: check your FXML file 'gui.fxml'.";
        assert rctBorder03 != null : "fx:id=\"rctBorder03\" was not injected: check your FXML file 'gui.fxml'.";
        assert rctBorder05 != null : "fx:id=\"rctBorder05\" was not injected: check your FXML file 'gui.fxml'.";
        assert rctBorder10 != null : "fx:id=\"rctBorder10\" was not injected: check your FXML file 'gui.fxml'.";
        assert rctBorder12 != null : "fx:id=\"rctBorder12\" was not injected: check your FXML file 'gui.fxml'.";
        assert rctBorder14 != null : "fx:id=\"rctBorder14\" was not injected: check your FXML file 'gui.fxml'.";
        assert rctBorder16 != null : "fx:id=\"rctBorder16\" was not injected: check your FXML file 'gui.fxml'.";
        assert rctBorder21 != null : "fx:id=\"rctBorder21\" was not injected: check your FXML file 'gui.fxml'.";
        assert rctBorder23 != null : "fx:id=\"rctBorder23\" was not injected: check your FXML file 'gui.fxml'.";
        assert rctBorder25 != null : "fx:id=\"rctBorder25\" was not injected: check your FXML file 'gui.fxml'.";
        assert rctBorder30 != null : "fx:id=\"rctBorder30\" was not injected: check your FXML file 'gui.fxml'.";
        assert rctBorder32 != null : "fx:id=\"rctBorder32\" was not injected: check your FXML file 'gui.fxml'.";
        assert rctBorder34 != null : "fx:id=\"rctBorder34\" was not injected: check your FXML file 'gui.fxml'.";
        assert rctBorder36 != null : "fx:id=\"rctBorder36\" was not injected: check your FXML file 'gui.fxml'.";
        assert rctBorder41 != null : "fx:id=\"rctBorder41\" was not injected: check your FXML file 'gui.fxml'.";
        assert rctBorder43 != null : "fx:id=\"rctBorder43\" was not injected: check your FXML file 'gui.fxml'.";
        assert rctBorder45 != null : "fx:id=\"rctBorder45\" was not injected: check your FXML file 'gui.fxml'.";
        assert rctBorder50 != null : "fx:id=\"rctBorder50\" was not injected: check your FXML file 'gui.fxml'.";
        assert rctBorder52 != null : "fx:id=\"rctBorder52\" was not injected: check your FXML file 'gui.fxml'.";
        assert rctBorder54 != null : "fx:id=\"rctBorder54\" was not injected: check your FXML file 'gui.fxml'.";
        assert rctBorder56 != null : "fx:id=\"rctBorder56\" was not injected: check your FXML file 'gui.fxml'.";
        assert rctBorder61 != null : "fx:id=\"rctBorder61\" was not injected: check your FXML file 'gui.fxml'.";
        assert rctBorder63 != null : "fx:id=\"rctBorder63\" was not injected: check your FXML file 'gui.fxml'.";
        assert rctBorder65 != null : "fx:id=\"rctBorder65\" was not injected: check your FXML file 'gui.fxml'.";

        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                board[i][j] = '.';
            }
        }

        labels = new Label[]{lbl11, lbl13, lbl15, lbl31, lbl33, lbl35, lbl51, lbl53, lbl55};
        borders = new Rectangle[]{rctBorder01, rctBorder03, rctBorder05, rctBorder10, rctBorder12, rctBorder14, rctBorder16
            , rctBorder21, rctBorder23, rctBorder25, rctBorder30, rctBorder32, rctBorder34, rctBorder36, rctBorder41, rctBorder43, rctBorder45, rctBorder50
            , rctBorder52, rctBorder54, rctBorder56, rctBorder61, rctBorder63, rctBorder65};
    }

}
