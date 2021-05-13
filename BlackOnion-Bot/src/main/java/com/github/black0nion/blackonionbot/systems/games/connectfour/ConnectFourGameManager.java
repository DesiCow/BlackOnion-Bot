package com.github.black0nion.blackonionbot.systems.games.connectfour;

import java.util.ArrayList;

import net.dv8tion.jda.api.entities.MessageChannel;

public class ConnectFourGameManager {
	private static ArrayList<ConnectFour> games = new ArrayList<>();
	
	public static int Y = 8;
	public static int X = 9;
	
	/**
	 *
	 * @param messageID of the Game Message
	 * @param playerX UserID of Player X
	 * @param playerY UserID of Player Y
	 * @return The new created Game
	 */
	public static ConnectFour createGame(MessageChannel channel, String playerX, String playerNameX, String playerY, String playerNameY) {
		ConnectFour newGame = new ConnectFour(channel, playerX, playerNameX, playerY, playerNameY);
		games.add(newGame);
		return newGame;
	}
	
	public static void deleteGame(ConnectFour game) {
		for (int i = 0; i < games.size(); i++) {
			if (games.get(i).getMessageID() == game.getMessageID())
				games.remove(i);
		}
	}
	
	public static boolean isIngame(String userID) {
		for (ConnectFour game : games) {
			if (game.getPlayerX().equals(userID) || game.getPlayerY().equals(userID))
				return true;
		}
		return false;
	}
	
	public static ConnectFour getGameByMessageID(long messageID) {
		for (ConnectFour game : games) {
			if (game.getMessageID() == messageID)
				return game;
		}
		return null;
	}
}
