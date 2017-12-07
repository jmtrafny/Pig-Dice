package application;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Director {

	private static final int SIDES = 6;
	private boolean lastRoundWon = false;
	private boolean rollAgain = true;
	private boolean forceRoll = false;
	
	private Map<String, Integer> occuranceMap;

	private int pot = 0;
	private int d1_Result;
	private int d2_Result;

	public Director() {
		super();
		occuranceMap = new HashMap<>();
	}

	public boolean getLastRoundWon() {
		return lastRoundWon;
	}

	public void setLastRoundWon(boolean b) {
		this.lastRoundWon = b;
	}

	public boolean getRollAgain() {
		return rollAgain;
	}

	public void setRollAgain(boolean b) {
		this.rollAgain = b;
	}

	public boolean getForceRoll() {
		return forceRoll;
	}

	public void setForceRoll(boolean b) {
		this.forceRoll = b;
	}

	public int getPot() {
		return pot;
	}

	public void setPot(int pot) {
		this.pot = pot;
	}

	public void rollDice() {
		Random rand;
		rand = new Random();
		d1_Result = rand.nextInt(SIDES) + 1;
		d2_Result = rand.nextInt(SIDES) + 1;
	}

	public int[] getResult() {
		int[] diceResults = new int[2];
		diceResults[0] = d1_Result;
		diceResults[1] = d2_Result;
		return diceResults;
	}

	public void resolveDice(Player p) {

		int d1 = d1_Result;
		int d2 = d2_Result;
		
		String key = createKey(d1, d2);
		updateOccuranceMap(key);
		

		if (d1 == 1 && d2 == 1) {
			p.setPoints(0);
			p.setBank(0);
			forceRoll = false;
			rollAgain = false;
		} else if (d1 == 1 || d2 == 1) {
			p.setBank(0);
			forceRoll = false;
			rollAgain = false;
		} else if (d1 == d2) {
			p.addPoints(d1 + d2 + p.getBank());
			p.setBank(0);
			forceRoll = true;
		} else {
			forceRoll = false;
			p.addToBank(d1 + d2);
		}
	}
	
	public String createKey(int d1, int d2){
		if (d2 < d1){
			int tmp = d1;
			d1 = d2;
			d2 = tmp;
		}
		String d1String = String.valueOf(d1);
		String d2String = String.valueOf(d2);
		String resultString = d1String + d2String;
//		System.out.println(resultString);
		return resultString;
	}
	
	public void updateOccuranceMap(String inKey){
		if (occuranceMap.containsKey(inKey)) {
			occuranceMap.put(inKey, occuranceMap.get(inKey) + 1);
		    } else {
		       occuranceMap.put(inKey, 1);
		    }
	}
	
	public Map<String, Integer> getOccuranceMap(){		
		//sort by values, and reserve it, 10,9,8,7,6...
        Map<String, Integer> sortedOccuranceMap = occuranceMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        return sortedOccuranceMap;
	}

	public void endGame(Player p) {
		String winnerName = p.getName();

		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("We have a WINNER!");
		alert.setHeaderText(null);
		alert.setContentText("Congradulations " + winnerName + "!  You win!");

		alert.showAndWait();
		
		Main.launch();
	}

}
