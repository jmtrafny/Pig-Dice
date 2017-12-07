package application;



public class Player {

	private String name;
	private int points;
	private int coins;
	private int bank;
	private int ordinal;
	
	public Player(String _name, int _ordinal){
		super();
		name = _name;
		points = 0;
		coins = 100;
		bank = 0;
		ordinal = _ordinal;
	}
	
	public int getOrdinal(){
		return ordinal;
	}
	
	public int setOrdinal(int _ordinal){
		ordinal = _ordinal;
		return ordinal;
	}
	
	public String getName(){
		return name;
	}
	
	public int getCoins(){
		return coins;
	}
	
	public int addCoins(int i){
		coins += i;
		return coins;
	}
	
	public int getPoints(){
		return points;
	}
	
	public int addPoints(int i){
		points += i; 
		return points;
	}
	
	public int setPoints(int i){
		points = i;
		return points;
	}
	
	public int setBank(int i){
		bank = i;
		return bank;
	}
	
	public int addToBank(int i){
		bank += i;
		return bank;
	}
	
	public int getBank(){
		return bank;
	}
	
	public int makeBet(int bet){		
		coins = coins - bet;
		return coins;
	}	
}
