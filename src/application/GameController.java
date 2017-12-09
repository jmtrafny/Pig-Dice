package application;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;

import com.alosaimi.networking.TcpPacket;

import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class GameController implements Initializable {

	public static int PORT = 8000;//8011;
	public static String IP_ADDRESS = "localhost";//"10.33.11.106";
	
	Media sound = new Media(new File("src/dice_sound.mp3").toURI().toString());
	MediaPlayer mediaPlayer = new MediaPlayer(sound);
	
	Director dir = new Director();
	Player localPlayer;
	Player networkedPlayer;
	Player activePlayer;
	Player inactivePlayer;

	@FXML	Button serverBtn;
	@FXML	Button clientBtn;
	
	@FXML	TextField player1TF;
	@FXML	TextField betText;
	
	@FXML	Label pleaseWaitLabel;
	@FXML	Label p1NameLabel;
	@FXML	Label p2NameLabel;
	@FXML	Label p1CoinsLabel;
	@FXML	Label p2CoinsLabel;
	@FXML	Label potLabel;
	@FXML	Label apNameLabel;
	@FXML	Label apPointsLabel;
	@FXML	Label apBankLabel;
	@FXML	Label infoLabel;
	@FXML   Label betWaitingLabel;
	@FXML   Label betLabel;
		
	@FXML	VBox playerNameVBox;
	@FXML   VBox occVBox;
	@FXML	VBox diceVBox;
	@FXML	VBox bettingVBox;
	@FXML	VBox recieveVBox;
	@FXML   VBox sendVBox;
	
	@FXML	Pane mainPane;

	Image diceFace1 = new Image("/dice face 1.png");
	Image diceFace2 = new Image("/dice face 2.png");
	Image diceFace3 = new Image("/dice face 3.png");
	Image diceFace4 = new Image("/dice face 4.png");
	Image diceFace5 = new Image("/dice face 5.png");
	Image diceFace6 = new Image("/dice face 6.png");
	
	ArrayList<Image> diceImgArr = new ArrayList<Image>();
	
	@FXML	ImageView d1IMV;
	@FXML	ImageView d2IMV;
	@FXML	Button rollBtn;
	@FXML	Button holdBtn;
	@FXML	Button okBtn;
	@FXML	Button acceptBtn;
	@FXML	Button declineBtn;
	
	@FXML	ToggleGroup tg;
	@FXML	RadioButton acceptRDO;
	@FXML	RadioButton declineRDO;
	
	Socket socket;
	Timeline timeline;

	@FXML
	private void serverButtonPressed(ActionEvent event) {
		
		// Get and display IP address
		InetAddress ip;
		try {
			ip = InetAddress.getLocalHost();
			pleaseWaitLabel.setText("Please Wait... \nServer Address is: \n" + ip);
		} catch (UnknownHostException e) {
			pleaseWaitLabel.setText("Error!");
			e.printStackTrace();
		}
		
		new Thread(() -> {
			
			try{
				// Get names and ordinals
				String myName = player1TF.getText();
			
				// Get random ordinal
				Random r = new Random();
				int ordinal = r.nextInt(2) + 1;
			
				if(ordinal == 1){
					localPlayer = new Player(myName, ordinal);
					activePlayer = localPlayer;
					ordinal = 2;
				} else {
					localPlayer = new Player(myName, ordinal);
					inactivePlayer = localPlayer;
					ordinal = 1;
				}
			
				// Create server socket
				ServerSocket server = new ServerSocket(PORT);
				socket = server.accept();
				
				// Get other players name
				ObjectInputStream fromClient = new ObjectInputStream(socket.getInputStream());
				TcpPacket namePacket = (TcpPacket) fromClient.readObject();
							
				if(ordinal == 1){
					networkedPlayer = new Player(namePacket.getmOtherPlayerName(), ordinal);
					activePlayer = networkedPlayer;
				} else {
					networkedPlayer = new Player(namePacket.getmOtherPlayerName(), ordinal);
					inactivePlayer = networkedPlayer;
				}
			
				// Send our name
				TcpPacket nameHandShake = new TcpPacket(myName, 0, 0, 0, ordinal, false);
				ObjectOutputStream toClient = new ObjectOutputStream(socket.getOutputStream());
				toClient.writeObject(nameHandShake);
				toClient.flush();
			
				// Start the game
				Platform.runLater(() -> {startGame();});
					
			
			} catch(IOException e){
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}).start();
	}
	
	@FXML
	private void clientButtonPressed(ActionEvent event) {				
		pleaseWaitLabel.setText("Connecting to 'Local Host'...");
		
		new Thread(() -> {		
			try{
				String myName = player1TF.getText();
			
				// Create server socket
				socket = new Socket(IP_ADDRESS, PORT);//"10.33.29.255", 8011);
				TcpPacket nameHandShake = new TcpPacket(myName, 0, 0, 0, 0, false);
			
				// Send our name
				ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
				toServer.writeObject(nameHandShake);
				toServer.flush();
			
				// Get response
				ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream());
				TcpPacket namePacket = (TcpPacket) fromServer.readObject();
			
				// Parse name from object, and start game
				int myOrdinal= namePacket.getmOrdinal();
				if(myOrdinal == 1){
					localPlayer = new Player(myName, myOrdinal);
					activePlayer = localPlayer;
					networkedPlayer = new Player(namePacket.getmOtherPlayerName(), 2);
					inactivePlayer = networkedPlayer;
				} else {
					localPlayer = new Player(myName, myOrdinal);
					inactivePlayer = localPlayer;
					networkedPlayer = new Player(namePacket.getmOtherPlayerName(), 1);
					activePlayer = networkedPlayer;
				}
			
				// Start the game
				Platform.runLater(() -> {startGame();});
			
			} catch(IOException e){
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}).start();	
	}
	
	public void startGame(){
				
		timeline = new Timeline(new KeyFrame(Duration.seconds(5), e -> { updateOccuranceLabels(); }));
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.play();
		
    	diceVBox.setStyle("-fx-background-color: #ffffff;");
		bettingVBox.setStyle("-fx-background-color: #8baee5;");

		d1IMV.setImage(diceFace1);
		d2IMV.setImage(diceFace1);
		diceImgArr.add(null);
		diceImgArr.add(diceFace1);
		diceImgArr.add(diceFace2);
		diceImgArr.add(diceFace3);
		diceImgArr.add(diceFace4);
		diceImgArr.add(diceFace5);
		diceImgArr.add(diceFace6);
		
		updateLabels();
		collectBets();
	}
	
	private void collectBets(){
		
		playerNameVBox.setVisible(false);
		mainPane.setVisible(false);
		bettingVBox.setVisible(true);
		
//		Platform.runLater(new Runnable() {
//		    @Override
//		    public void run() {
//		    	if(localPlayer.equals(activePlayer)){
//					recieveVBox.setVisible(false);
//					sendVBox.setVisible(true);
//				} else {
//					recieveVBox.setVisible(true);
//					sendVBox.setVisible(false);
//				}
//		    	
//		    	/*
//		    	 *  TODO: IMPLEMENT BETTING
//		    	 *  The following forces a bet of 100 because its broken
//		    	 */
//		    }
//		});
		
		localPlayer.setBet(100);
    	activePlayer.makeBet(100);
		inactivePlayer.makeBet(100);
		dir.setPot(100 * 2);
		finishedCollectingBets();
	}
	
	private void finishedCollectingBets(){
		playerNameVBox.setVisible(false);
		mainPane.setVisible(true);
		bettingVBox.setVisible(false);
		dir.setLastRoundWon(false);
		
		updateLabels();
		setPlayerControl();
	}
	
	/*
	 * This method disables the UI if it is not
	 * local players turn
	 */
	private void setPlayerControl(){
		if (networkedPlayer.equals(activePlayer)){
			// disable ui and wait for incoming packets
			okBtn.setDisable(true);
			holdBtn.setDisable(true);
			rollBtn.setDisable(true);
			notMyTurn();
		} else if (localPlayer.equals(activePlayer)) {
			okBtn.setDisable(false);
			holdBtn.setDisable(false);
			rollBtn.setDisable(false);
		}
		updateLabels();
	}
	
	/*
	 * Handle incoming networked commands
	 */	
	private void notMyTurn(){
		new Thread(() -> {
			boolean notMyTurn = true;
			while(notMyTurn){
				try {
					// Obtain stream
					ObjectInputStream fromOtherPlayer = new ObjectInputStream(socket.getInputStream());
					TcpPacket turnInformation = (TcpPacket) fromOtherPlayer.readObject();
				
					// Collect dice results
					int diceResults[] = new int[2];
					diceResults[0] = turnInformation.getmDiceOneValue();
					diceResults[1] = turnInformation.getmDiceTwoValue();
					System.out.println(diceResults[0] + diceResults[1]);
				
					// Check if the other player is done
					if (turnInformation.ismIsFinished()){
						notMyTurn = false;
						if (dir.getRollAgain()) {
							dir.setRollAgain(false);
							activePlayer.addPoints(activePlayer.getBank());
							activePlayer.setBank(0);
						}
					} else { // Other player is not done
						Platform.runLater(() -> { rollBtnPressed(diceResults); });
					}	
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			// Switch active and inactiveplayer
			// then reativate ui control
			Platform.runLater(() -> {
				swapPlayers();
				setPlayerControl();
			});
		}).start();
	}
	
	@FXML
	private void betAccepted(ActionEvent event){
						
	}
	
	@FXML
	private void betDeclined(ActionEvent event){
						
	}
	
	@FXML
	private void sendBtnPressed(ActionEvent event){
						
	}
	
	@FXML
	private void goBtnPressed(ActionEvent event){
						
	}
	
	/*
	 *  Hold button was pressed on UI
	 */
	@FXML
	private void holdBtnPressed(ActionEvent event) {

		if (dir.getRollAgain()) {
			dir.setRollAgain(false);
			activePlayer.addPoints(activePlayer.getBank());
			activePlayer.setBank(0);

			// check for round winner
			if (activePlayer.getPoints() >= 100) {
				activePlayer.addCoins(dir.getPot());
				activePlayer.setPoints(0);
				inactivePlayer.setPoints(0);
				activePlayer.setBank(0);
				inactivePlayer.setBank(0);
				
				if (localPlayer.getCoins() <= 0){ dir.endGame(networkedPlayer);}
				if (networkedPlayer.getCoins() <= 0){ dir.endGame(localPlayer);}
				
				// show winner  in infoLabel
				infoLabel.setText(activePlayer.getName() + " won the pot!\n");
				// go to betting
				swapPlayers();
				bettingVBox.setVisible(true);	
			}
			
			// Send tcp packet informing the other player our
			// turn is over
			new Thread(() -> {
				TcpPacket myTurnInformation = new TcpPacket(localPlayer.getName(), 0, 0, localPlayer.getBet(), localPlayer.getOtherPlayerOrdinal(), true);
			
				ObjectOutputStream toOtherPlayer;
				try {
					toOtherPlayer = new ObjectOutputStream(socket.getOutputStream());
					toOtherPlayer.writeObject(myTurnInformation);
					toOtherPlayer.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			
				// Switch active and inactiveplayer
				// then deativate ui control
				Platform.runLater(() -> {
					swapPlayers();
					setPlayerControl();
				});
			}).start();
		}
	}

	/*
	 * Roll button pressed in UI
	 */
	@FXML
	private void rollBtnPressed(ActionEvent event) {
		if (dir.getRollAgain()) {

			mediaPlayer.stop();
			dir.rollDice();
			mediaPlayer.play();
			int[] diceResults = new int[2];
			diceResults = dir.getResult();

			setDiceImage(diceResults[0], diceResults[1]);
			animateDice();

			dir.resolveDice(activePlayer);
			updateLabels();
			
			if (dir.getForceRoll()) {
				holdBtn.setDisable(true);
				rollBtn.setDisable(false);
				okBtn.setDisable(true);
			} else {
				holdBtn.setDisable(false);
				rollBtn.setDisable(false);
				okBtn.setDisable(true);
			}

			if (dir.getRollAgain()) {
				holdBtn.setDisable(false);
				rollBtn.setDisable(false);
				okBtn.setDisable(true);
			} else {
				holdBtn.setDisable(true);
				rollBtn.setDisable(true);
				okBtn.setDisable(false);
			}
			
			// Send tcp packet about our roll
			new Thread(() -> {
				int[] cheat = dir.getResult();
				TcpPacket myTurnInformation = new TcpPacket(localPlayer.getName(), cheat[0], cheat[1], localPlayer.getBet(), localPlayer.getOtherPlayerOrdinal(), false);
			
				ObjectOutputStream toOtherPlayer;
				try {
					toOtherPlayer = new ObjectOutputStream(socket.getOutputStream());
					toOtherPlayer.writeObject(myTurnInformation);
					toOtherPlayer.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}).start();
		}
	}
	
	/*
	 * Overloaded method for providing the dice results
	 * obtained over the network
	 */
	private void rollBtnPressed(int[] diceResults){
		
		if (dir.getRollAgain()) {
			mediaPlayer.stop();
			mediaPlayer.play();
			
			dir.setResult(diceResults[0], diceResults[1]);

			setDiceImage(diceResults[0], diceResults[1]);
			animateDice();

			dir.resolveDice(activePlayer);
			updateLabels();
		}
	}

	/*
	 * Ok button was pressed on GUI
	 */
	@FXML
	private void okBtnPressed(ActionEvent event) {
		if (!dir.getRollAgain()) {
			rollBtn.setDisable(false);
			holdBtn.setDisable(false);
			updateLabels();
			
			// Send tcp packet informing the other player our
			// turn is over
			TcpPacket myTurnInformation = new TcpPacket(localPlayer.getName(), -1, -1, localPlayer.getBet(), localPlayer.getOtherPlayerOrdinal(), true);
			new Thread( () -> {
				ObjectOutputStream toOtherPlayer;
				try {
					toOtherPlayer = new ObjectOutputStream(socket.getOutputStream());
					toOtherPlayer.writeObject(myTurnInformation);
					toOtherPlayer.flush();
					Platform.runLater(() -> {
						swapPlayers();
						setPlayerControl();
					});
				} catch (IOException e) {
					e.printStackTrace();
				}
			}).start();
			
			// Switch players and give up UI control to network
			
		}
	}

	public void animateDice() {
		RotateTransition rt1 = new RotateTransition(Duration.millis(300), d1IMV);
		RotateTransition rt2 = new RotateTransition(Duration.millis(300), d2IMV);
		rt1.setByAngle(-180);
		rt1.setCycleCount(1);
		rt2.setByAngle(180);
		rt2.setCycleCount(1);
		rt1.play();
		rt2.play();
	}

	public void setDiceImage(int result1, int result2) {

		d1IMV.setImage(diceImgArr.get(result1));
		d2IMV.setImage(diceImgArr.get(result2));

	}

	public void updateLabels() {
    	p1NameLabel.setText(localPlayer.getName());
		p2NameLabel.setText(networkedPlayer.getName());
		
		p1CoinsLabel.setText(String.valueOf(localPlayer.getCoins()));
        p2CoinsLabel.setText(String.valueOf(networkedPlayer.getCoins()));
        potLabel.setText(String.valueOf(dir.getPot()));

        apNameLabel.setText(activePlayer.getName());
        apPointsLabel.setText(String.valueOf(activePlayer.getPoints()));
        apBankLabel.setText(String.valueOf(activePlayer.getBank()));
	}

	public void swapPlayers() {
		Player tmp = activePlayer;
		activePlayer = inactivePlayer;
		inactivePlayer = tmp;
		dir.setRollAgain(true);
		updateLabels();
	}
	
	public void updateOccuranceLabels(){
		// Clear the current labels and make our own
		occVBox.getChildren().clear();
		Map<String, Integer> sortedMap = dir.getOccuranceMap();
		Label labels[] = new Label[sortedMap.size()];
		int index = 0;
		int red_count = 0;
		
		for (Map.Entry<String, Integer> entry : sortedMap.entrySet()){
			String key = entry.getKey();
			Integer value = entry.getValue();
			
			if(labels.length > 5){
				labels[index] = new Label(key + " : " + String.valueOf(value));
				
				if(red_count < 3 ){
					labels[index].setStyle("-fx-background-color: red;");
					red_count++;
				}
				
				if(labels.length - index <= 3){
					labels[index].setStyle("-fx-background-color: green;");
				}
				
			} else {
				labels[index] = new Label(key + " : " + String.valueOf(value));
			}
			
			occVBox.getChildren().add(labels[index]);
			index++;
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {}

}
