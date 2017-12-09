package com.alosaimi.networking;


public class TcpPacket implements java.io.Serializable
{
	private String mOtherPlayerName;
	private int mDiceOneValue;
	private int mDiceTwoValue;
	private int mBetValue;
	private int mOrdinal;
	private boolean mIsFinished;
	
	public TcpPacket(String otherPlayerName, int diceOneValue, int diceTwoValue, int betValue, int ordinal, boolean isFinished)
	{
		mOtherPlayerName = otherPlayerName;
		mDiceOneValue = diceOneValue;
		mDiceTwoValue = diceTwoValue;
		mBetValue = betValue;
		mOrdinal = ordinal;
		mIsFinished = isFinished;
	}
	
	public String getmOtherPlayerName()
	{
		return mOtherPlayerName;
	}

	public void setmOtherPlayerName(String otherPlayerName)
	{
		mOtherPlayerName = otherPlayerName;
	}

	public int getmDiceOneValue()
	{
		return mDiceOneValue;
	}

	public void setmDiceOneValue(int diceOneValue)
	{
		mDiceOneValue = diceOneValue;
	}
	
	public int getmDiceTwoValue()
	{
		return mDiceTwoValue;
	}

	public void setmDiceTwoValue(int diceTwoValue)
	{
		mDiceTwoValue = diceTwoValue;
	}
	
	public int getmBetValue()
	{
		return mBetValue;
	}

	public void setmBetValue(int betValue)
	{
		mBetValue = betValue;
	}
	
	public int getmOrdinal()
	{
		return mOrdinal;
	}

	public void setmOrdinal(int ordinal)
	{
		mOrdinal = ordinal;
	}

	public boolean ismIsFinished()
	{
		return mIsFinished;
	}

	public void setmIsFinished(boolean isFinished)
	{
		mIsFinished = isFinished;
	}
}