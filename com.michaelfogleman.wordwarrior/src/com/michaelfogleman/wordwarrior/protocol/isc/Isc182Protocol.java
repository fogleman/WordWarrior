package com.michaelfogleman.wordwarrior.protocol.isc;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

import com.michaelfogleman.wordwarrior.Convert;
import com.michaelfogleman.wordwarrior.Util;
import com.michaelfogleman.wordwarrior.model.*;
import com.michaelfogleman.wordwarrior.protocol.Data;
import com.michaelfogleman.wordwarrior.protocol.ProtocolAdapter;
import com.michaelfogleman.wordwarrior.protocol.command.*;
import com.michaelfogleman.wordwarrior.protocol.response.*;

public class Isc182Protocol extends ProtocolAdapter {
	
	// crappy state maintenance
	private boolean abortRequested;
	private boolean adjournRequested;
	private boolean observing;
	private MoveResult preMove;
	private String handle;
	
	public boolean isKeepAliveSupported() {
		return true;
	}
	
	public long getKeepAliveRate() {
		return 30000L;
	}
	
	public Data[] preprocess(Data b) {
		Outbound out = null;
		List<Outbound> results = new ArrayList<Outbound>(1);
		String data = new String(b.getBytes());
		
		if (data.equals("0 PING REPLY")) {
			out = new Outbound("PING");
			out.add("REPLY");
		}
		
		if (results.size() == 0 && out != null) {
			results.add(out);
		}
		return convert(results);
	}
	
	public Data[] serialize(Command command) {
		Outbound out = null;
		List<Outbound> results = new ArrayList<Outbound>(1);
		
		if (command instanceof AbortCommand) {
			//AbortCommand cmd = (AbortCommand)command;
			out = new Outbound("ABORT");
			if (abortRequested) {
				out.add("SET");
			}
		}
		
		else if (command instanceof AcceptMatchCommand) {
			// ACCEPT opponent dictionary time interval rated noescape challenge firstPlayer playerTiles opponentTiles
			AcceptMatchCommand cmd = (AcceptMatchCommand)command;
			Match match = cmd.getMatch();
			int firstPlayer = cmd.getFirstPlayer();
			ITileRack[] racks = cmd.getRacks();
			if (match != null && racks != null) {
				out = new Outbound("ACCEPT");
				out.add(match.getHandle());
				out.add(match.getDictionary().getId());
				out.add(match.getTime());
				out.add(match.getIncrement());
				out.add(match.isRated());
				out.add(match.isNoescape());
				out.add(match.getChallengeMode().getId());
				out.add(firstPlayer);
				for (int i = 0; i < racks.length; i++) {
					ITileRack rack = racks[i];
					out.add(rack.toString());
				}
			}
		}

		else if (command instanceof AddBuddyCommand) {
			AddBuddyCommand cmd = (AddBuddyCommand)command;
			out = new Outbound("BUDDIES");
			out.add("LOGIN");
			out.add(cmd.getHandle());
		}
		
		else if (command instanceof AddChannelCommand) {
			AddChannelCommand cmd = (AddChannelCommand)command;
			out = new Outbound("CHANNEL");
			out.add("LOGIN");
			out.add(cmd.getChannel());
		}

		else if (command instanceof AdjournCommand) {
			//AdjournCommand cmd = (AdjournCommand)command;
			out = new Outbound("ADJOURN");
			if (adjournRequested) {
				out.add("SET");
			}
		}
		
		else if (command instanceof AdjournedCommand) {
			AdjournedCommand cmd = (AdjournedCommand)command;
			String handle = cmd.getHandle();
			out = new Outbound("ADJOURNED");
			if (handle != null) {
				out.add(handle);
			}
		}
		
		else if (command instanceof AdjustCommand) {
			//AdjustCommand cmd = (AdjustCommand)command;
			out = new Outbound("ADJUST");
			out.add("OVERTIME");
			out.add(handle);
		}
		
		else if (command instanceof AllObserversCommand) {
			AllObserversCommand cmd = (AllObserversCommand)command;
			String handle = cmd.getHandle();
			if (handle != null) {
				out = new Outbound("ALLOBSERVERS");
				out.add(handle);
			}
		}
		
		else if (command instanceof AskCommand) {
			AskCommand cmd = (AskCommand)command;
			String message = cmd.getMessage();
			if (message != null) {
				out = new Outbound("ASK");
				out.add(message);
			}
		}
		
		else if (command instanceof AssessCommand) {
			AssessCommand cmd = (AssessCommand)command;
			String handle = cmd.getHandle();
			int scoreDifference = cmd.getScoreDifference();
			if (handle != null) {
				out = new Outbound("ASSESS");
				out.add(handle);
				out.add(scoreDifference);
			}
		}
		
		else if (command instanceof ChallengeCommand) {
			ChallengeCommand cmd = (ChallengeCommand)command;
			String[] words = cmd.getWords();
			out = new Outbound("CHALLENGE");
			out.add(cmd.getDictionary().getId());
			for (int i = 0; i < words.length; i++) {
				String word = words[i];
				out.add(word);
			}
		}

		else if (command instanceof CheckWordsCommand) {
			CheckWordsCommand cmd = (CheckWordsCommand)command;
			String[] words = cmd.getWords();
			if (words != null && words.length > 0) {
				out = new Outbound("CHECK");
				for (int i = 0; i < words.length; i++) {
					String word = words[i];
					out.add(word);
				}
			}
		}
		
		else if (command instanceof DeclineMatchCommand) {
			DeclineMatchCommand cmd = (DeclineMatchCommand)command;
			Match match = cmd.getMatch();
			if (match != null) {
				String handle = match.getHandle();
				out = new Outbound("DECLINE");
				out.add(handle);
			}
		}
		
		else if (command instanceof ExamineAdjournedCommand) {
			ExamineAdjournedCommand cmd = (ExamineAdjournedCommand)command;
			String handle = cmd.getHandle();
			int id = cmd.getId();
			if (handle != null && id >= 0) {
				out = new Outbound("EXAMINE");
				out.add("ADJOURNED");
				out.add(handle);
				out.add(id);
			}
		}
		
		else if (command instanceof ExamineHistoryCommand) {
			ExamineHistoryCommand cmd = (ExamineHistoryCommand)command;
			String handle = cmd.getHandle();
			int id = cmd.getId();
			if (handle != null && id >= 0) {
				out = new Outbound("EXAMINE");
				out.add("HISTORY");
				out.add(handle);
				out.add(id);
			}
		}
		
		else if (command instanceof ExamineLibraryCommand) {
			ExamineLibraryCommand cmd = (ExamineLibraryCommand)command;
			String handle = cmd.getHandle();
			int id = cmd.getId();
			if (handle != null && id >= 0) {
				out = new Outbound("EXAMINE");
				out.add("LIBRARY");
				out.add(handle);
				out.add(id);
			}
		}
		
		else if (command instanceof ExchangeTilesCommand) {
			ExchangeTilesCommand cmd = (ExchangeTilesCommand)command;
			out = new Outbound("CHANGE");
			out.add(Convert.toString(cmd.getNewRack()));
			out.add(cmd.getMinutes());
			int seconds = (cmd.getSeconds() / 10) * 10; // for real?
			seconds += cmd.getTiles().length; // gross. ISC blows.
			out.add(seconds);
		}

		else if (command instanceof GetBestPlayersCommand) {
			GetBestPlayersCommand cmd = (GetBestPlayersCommand)command;
			DictionaryType d = cmd.getDictionary();
			if (d != null) {
				out = new Outbound("BEST");
				out.add(d.getName());
			}
		}
		
		else if (command instanceof GetBuddiesCommand) {
			//GetBuddiesCommand cmd = (GetBuddiesCommand)command;
			out = new Outbound("BUDDIES");
		}
		
		else if (command instanceof GetChannelsCommand) {
			//GetChannelsCommand cmd = (GetChannelsCommand)command;
			out = new Outbound("CHANNEL");
		}
		
		else if (command instanceof GetPendingMatchesCommand) {
			//GetPendingMatchesCommand cmd = (GetPendingMatchesCommand)command;
			out = new Outbound("PENDING");
		}
		
		else if (command instanceof GetPersonalInfoCommand) {
			GetPersonalInfoCommand cmd = (GetPersonalInfoCommand)command;
			String handle = cmd.getHandle();
			if (handle != null) {
				out = new Outbound("PERSONAL");
				out.add(handle);
			}
		}

		else if (command instanceof GetServerTimeCommand) {
			//GetServerTimeCommand cmd = (GetServerTimeCommand)command;
			out = new Outbound("DATE");
		}
		
		else if (command instanceof GetServerUptimeCommand) {
			//GetServerUptimeCommand cmd = (GetServerUptimeCommand)command;
			out = new Outbound("UPTIME");
		}

		else if (command instanceof GetUserInfoCommand) {
			GetUserInfoCommand cmd = (GetUserInfoCommand)command;
			String handle = cmd.getHandle();
			if (handle != null) {
				out = new Outbound("FINGER");
				out.add(handle);
			}
		}

		else if (command instanceof GetUserVariablesCommand) {
			GetUserVariablesCommand cmd = (GetUserVariablesCommand)command;
			String handle = cmd.getHandle();
			if (handle != null) {
				out = new Outbound("VARS");
				out.add(handle);
			}
		}

		else if (command instanceof HelpCommand) {
			HelpCommand cmd = (HelpCommand)command;
			String topic = cmd.getTopic();
			out = new Outbound("HELP");
			if (topic != null) {
				out.add(topic);
			}
		}
		
		else if (command instanceof HistoryCommand) {
			HistoryCommand cmd = (HistoryCommand)command;
			String handle = cmd.getHandle();
			out = new Outbound("HISTORY");
			if (handle != null) {
				out.add(handle);
			}
		}

		else if (command instanceof KeepAliveCommand) {
			//KeepAliveCommand cmd = (KeepAliveCommand)command;
			out = new Outbound("ALIVE");
		}

		else if (command instanceof KibitzCommand) {
			KibitzCommand cmd = (KibitzCommand)command;
			String message = cmd.getMessage();
			if (message != null) {
				out = new Outbound("KIBITZ");
				out.add(message);
			}
		}
		
		else if (command instanceof LibraryCommand) {
			LibraryCommand cmd = (LibraryCommand)command;
			String handle = cmd.getHandle();
			out = new Outbound("LIBRARY");
			if (handle != null) {
				out.add(handle);
			}
		}

		else if (command instanceof LoginCommand) {
			// LOGIN username password 1820 1 1 21438 username
			LoginCommand cmd = (LoginCommand)command;
			
			handle = cmd.getHandle();
			
			out = new Outbound("LOGIN");
			out.add(cmd.getHandle());
			out.add(cmd.getPassword());
			out.add(1820);
			out.add(1);
			out.add((int)(Math.random() * 10000 + 1000));
			out.add(cmd.getHandle());
			results.add(out);
			
			out = new Outbound("SOUGHT");
			results.add(out);
			
			out = new Outbound("RESUME");
			out.add("LOGIN");
			results.add(out);
			
//			out = new Outbound("SET");
//			out.add("FAIRPLAY");
//			out.add("ON");
//			results.add(out);
		}
		
		else if (command instanceof MatchCommand) {
			MatchCommand cmd = (MatchCommand)command;
			String handle = cmd.getHandle();
			if (handle != null) {
				out = new Outbound("MATCH");
				out.add(handle);
			}
		}
		
		else if (command instanceof MessageCommand) {
			MessageCommand cmd = (MessageCommand)command;
			String handle = cmd.getHandle();
			String message = cmd.getMessage();
			if (handle != null && message != null) {
				out = new Outbound("MESSAGE");
				out.add(handle);
				out.add(message);
			}
		}
		
		else if (command instanceof MoreCommand) {
			//MoreCommand cmd = (MoreCommand)command;
			out = new Outbound("MORE");
		}
		
		else if (command instanceof MoveCommand) {
			MoveCommand cmd = (MoveCommand)command;
			MoveResult result = cmd.getResult();
			Move move = result.getMove();
			ITileRack rack = cmd.getRack();
			if (result != null && rack != null) {
				out = new Outbound("MOVE");
				out.add(move.getCoordinateString());
				out.add(Convert.toString(move.getWord()));
				out.add(result.getScore());
				out.add(cmd.getMinutes());
				out.add(cmd.getSeconds());
				if (rack.empty()) {
					out.add("---");
				}
				else {
					out.add(rack.toString());
				}
			}
		}

		else if (command instanceof ObserveCommand) {
			ObserveCommand cmd = (ObserveCommand)command;
			out = new Outbound("UNOBSERVE");
			results.add(out);
			String handle = cmd.getHandle();
			if (handle != null) {
				out = new Outbound("OBSERVE");
				out.add(handle);
			}
			else {
				out = new Outbound("OBSERVE");
			}
			results.add(out);
		}
		
		else if (command instanceof PassCommand) {
			PassCommand cmd = (PassCommand)command;
			out = new Outbound("PAS");
			out.add(cmd.getMinutes());
			out.add(cmd.getSeconds());
			if (cmd.isEndGame()) {
				out.add("---");
				results.add(out);
				out = new Outbound("RESIGN");
				out.add(4); // 5 for mult pass, 4 for end game pass
				out.add(cmd.getLocalScore());
				out.add(cmd.getRemoteScore());
				results.add(out);
			}
		}

		else if (command instanceof PingUserCommand) {
			PingUserCommand cmd = (PingUserCommand)command;
			String handle = cmd.getHandle();
			if (handle != null) {
				out = new Outbound("PING");
				out.add("ANSWER");
				out.add(handle);
			}
		}
		
		else if (command instanceof PlayCommand) {
			PlayCommand cmd = (PlayCommand)command;
			Match match = cmd.getMatch();
			int firstPlayer = cmd.getFirstPlayer();
			ITileRack[] racks = cmd.getRacks();
			if (match != null && racks != null) {
				out = new Outbound("PLAY");
				out.add(match.getHandle());
				out.add(match.getDictionary().getId());
				out.add(match.getTime());
				out.add(match.getIncrement());
				out.add(match.isRated());
				out.add(match.isNoescape());
				out.add(match.getChallengeMode().getId());
				out.add(firstPlayer);
				for (int i = 0; i < racks.length; i++) {
					ITileRack rack = racks[i];
					out.add(rack.toString());
				}
			}
		}
		
		else if (command instanceof PreMoveCommand) {
			PreMoveCommand cmd = (PreMoveCommand)command;
			preMove = cmd.getMoveResult();
			out = new Outbound("PREMOVE");
			out.add(cmd.getDictionary().getId());
			for (String word : cmd.getMoveResult().getWords()) {
				out.add(word);
			}
		}

		else if (command instanceof RemoveBuddyCommand) {
			RemoveBuddyCommand cmd = (RemoveBuddyCommand)command;
			out = new Outbound("BUDDIES");
			out.add("CLOSE");
			out.add(cmd.getHandle());
		}
		
		else if (command instanceof RemoveChannelCommand) {
			RemoveChannelCommand cmd = (RemoveChannelCommand)command;
			out = new Outbound("CHANNEL");
			out.add("CLOSE");
			out.add(cmd.getChannel());
		}
		
		else if (command instanceof ResignCommand) {
			//ResignCommand cmd = (ResignCommand)command;
			out = new Outbound("RESIGN");
			out.add(1);
		}
		
		else if (command instanceof ResignOnTimeoutCommand) {
			//ResignOnTimeoutCommand cmd = (ResignOnTimeoutCommand)command;
			out = new Outbound("RESIGN");
			out.add(2);
			// TODO abort on short game
		}
		
		else if (command instanceof SeekCommand) {
			//SeekCommand cmd = (SeekCommand)command;
			out = new Outbound("SEEK");
		}
		
		else if (command instanceof SetAllCommand) {
			SetAllCommand cmd = (SetAllCommand)command;
			Settings settings = cmd.getSettings();
			out = new Outbound("SETALL");
			
			String key;
			
			out.add(0);
			out.add(0);

			key = "user.quietplay";
			out.add(settings.getString(key, "-1"));
			out.add(settings.containsKeyString(key));

			key = "user.dictionary";
			out.add(settings.getString(key, "-1"));
			out.add(settings.containsKeyString(key));

			key = "user.time";
			out.add(settings.getString(key, "-1"));
			out.add(settings.containsKeyString(key));

			key = "user.increment";
			out.add(settings.getString(key, "-1"));
			out.add(settings.containsKeyString(key));

			key = "user.noescape";
			out.add(settings.getString(key, "-1"));
			out.add(settings.containsKeyString(key));

			key = "user.mood";
			out.add(settings.getString(key, "-1"));
			out.add(settings.containsKeyString(key));

			key = "user.sound";
			out.add(settings.getString(key, "-1"));
			out.add(settings.containsKeyString(key));

			out.add(0);
			out.add(0);

			key = "user.channel";
			out.add(settings.getString(key, "-1"));
			out.add(settings.containsKeyString(key));

			key = "user.tell";
			out.add(settings.getString(key, "-1"));
			out.add(settings.containsKeyString(key));

			key = "user.challenge";
			out.add(settings.getString(key, "-1"));
			out.add(settings.containsKeyString(key));

			key = "user.rated";
			out.add(settings.getString(key, "-1"));
			out.add(settings.containsKeyString(key));

			key = "user.private";
			out.add(settings.getString(key, "-1"));
			out.add(settings.containsKeyString(key));

			key = "user.language";
			out.add(settings.getString(key, "-1"));
			out.add(settings.containsKeyString(key));

			key = "user.kibitz";
			out.add(settings.getString(key, "-1"));
			out.add(settings.containsKeyString(key));
		}
		
		else if (command instanceof SoughtCommand) {
			//SoughtCommand cmd = (SoughtCommand)command;
			out = new Outbound("SOUGHT");
		}

		else if (command instanceof TellCommand) {
			TellCommand cmd = (TellCommand)command;
			String handle = cmd.getHandle();
			String message = cmd.getMessage();
			if (handle != null && message != null) {
				out = new Outbound("TELL");
				out.add(handle);
				out.add(message);
			}
		}
		
		else if (command instanceof UndoMoveCommand) {
			UndoMoveCommand cmd = (UndoMoveCommand)command;
			out = new Outbound("PAS");
			out.add(cmd.getMinutes());
			out.add(cmd.getSeconds());
			MoveResult moveResult = cmd.getMoveResult();
			Move move = moveResult.getMove();
			out.add(move.getCoordinateString() + "_" + Convert.toString(move.getWord()) + "_" + moveResult.getScore());
		}

		else if (command instanceof UnexamineCommand) {
			//UnexamineCommand cmd = (UnexamineCommand)command;
			out = new Outbound("UNEXAMINE");
		}

		else if (command instanceof UnobserveCommand) {
			//UnobserveCommand cmd = (UnobserveCommand)command;
			out = new Outbound("UNOBSERVE");
		}

		else if (command instanceof UnseekCommand) {
			//UnseekCommand cmd = (UnseekCommand)command;
			out = new Outbound("UNSEEK");
		}

		else if (command instanceof WhisperCommand) {
			WhisperCommand cmd = (WhisperCommand)command;
			String message = cmd.getMessage();
			if (message != null) {
				out = new Outbound("WHISPER");
				out.add(message);
			}
		}
		
		else if (command instanceof WhoCommand) {
			WhoCommand cmd = (WhoCommand)command;
			out = new Outbound("WHO");
			out.add("LIST");
			out.add(cmd.getFlags());
			out.add(cmd.getMinimumRating());
			out.add(cmd.getMaximumRating());
		}
		
		if (results.size() == 0 && out != null) {
			results.add(out);
		}
		return convert(results);
	}

	public Response[] deserialize(Data data) {
		Inbound in = new Inbound(new String(data.getBytes()));
		
		String cmd = in.next().toUpperCase();
		if (cmd == null) return null;
		
		List<Response> results = new ArrayList<Response>(1);
		
		/* begin parsing */
		
		// opponent requests abort
		//*** Unhandled Response: "0 ABORT  "
		if (cmd.equals("ABORT")) {
			abortRequested = true;
		}

		else if (cmd.equals("ACCEPT")) {
			abortRequested = false;
			adjournRequested = false;
			
			int rating = in.getInt();
			String handle = in.getString();
			DictionaryType dictionary = DictionaryType.getInstance(in.getInt());
			int time = in.getInt();
			int increment = in.getInt();
			boolean rated = in.getBoolean();
			boolean noescape = in.getBoolean();
			ChallengeMode challengeMode = ChallengeMode.getInstance(in.getInt());
			int firstPlayer = in.getInt();
			Tile[] rack1 = Convert.toTiles(in.getString());
			Tile[] rack2 = Convert.toTiles(in.getString());
			results.add(new AcceptResponse(handle, rating, time, increment, rated, noescape, dictionary, challengeMode, firstPlayer, rack1, rack2));
		}

		else if (cmd.equals("ADJOURNED")) {
			String owner = in.getString();
			List<GameReference> references = readGameReferences(in, owner, GameReference.ADJOURNED_TYPE);
			results.add(new AdjournedResponse(owner, references));
		}

		else if (cmd.equals("ADJOURN")) {
			adjournRequested = true;
		}

		else if (cmd.equals("ADJUDICATE")) {
		}

		// opponent out of time
		// *** Unhandled Response: "0 ADJUST OVERTIME elaine45"
		else if (cmd.equals("ADJUST")) {
			String type = in.getString().toUpperCase();
			if (type.equals("OVERTIME")) {
				String handle = in.getString();
				results.add(new AdjustResponse(handle));
			}
		}

		else if (cmd.equals("ALIVE")) {
		}

		else if (cmd.equals("ALLOBSERVERS")) {
		}

		else if (cmd.equals("ASITIS") || cmd.equals("CHECK") || cmd.equals("DATE") || cmd.equals("PERSONAL")) {
			// ASITIS text
			String text = in.remainder();
			results.add(new AsitisResponse(text));
		}

		else if (cmd.equals("ASK")) {
		}

		else if (cmd.equals("ASSESS")) {
		}

		else if (cmd.equals("BEST")) {
		}

		else if (cmd.equals("BUDDIES")) {
			String[] handles = Util.split(in.remainder());
			results.add(new BuddiesResponse(handles));
		}

		else if (cmd.equals("CENSOR")) {
		}

		// opponent challenge unsuccessful
//		*** Unhandled Response: "0 CHALLENGE Yes "
//			Receive: "0 PAS   24 06 ---"
//			com.michaelfogleman.wordwarrior.protocol.response.PassResponse@22d166
//			Send: "ALIVE"
		
		// opponent challenge successful
//		Send: "MOVE 11H swii 19 23 40 engprdm"
//			*** Unhandled Response: "0 CHALLENGE No Yes "

		else if (cmd.equals("CHALLENGE")) {
			boolean successful = false;
			while (in.hasNext()) {
				String valid = in.getString().toLowerCase();
				if (valid.equals("no")) {
					successful = true;
					break;
				}
			}
			results.add(new ChallengeResponse(successful));
		}

		else if (cmd.equals("CHANGE")) {
			Tile[] tiles = Convert.toTiles(in.getString());
			int minutes = in.getInt();
			int seconds = in.getInt();
			int count = 0;//in.getInt();
			results.add(new ChangeResponse(tiles, minutes, seconds, count));
		}

		else if (cmd.equals("CHANNEL")) {
			int channel = in.getInt();
			String handle = in.getString();
			UserType userType = UserType.getInstance(in.getInt());
			String message = in.remainder();
			results.add(new ChannelResponse(channel, handle, userType, message));
		}

		else if (cmd.equals("CHECK")) {
		}

		else if (cmd.equals("CLEAR")) {
		}

		else if (cmd.equals("CLOSE")) {
			String reason = in.remainder();
			results.add(new CloseResponse(reason));
		}

		else if (cmd.equals("CONNECT")) {
		}

		else if (cmd.equals("DATE")) {
		}

		else if (cmd.equals("DECLINE")) {
		}

		else if (cmd.equals("DELETE")) {
		}

		else if (cmd.equals("DISCONNECT")) {
		}

		else if (cmd.equals("DUPLICATE")) {
		}

		else if (cmd.equals("ECHO")) {
		}

		else if (cmd.equals("EXAMINE")) {
			in.getString(); // type
			System.out.println(data.toString());
			IGame game = readGame(in);
			results.add(new ObserveStartResponse(game));
		}

		else if (cmd.equals("FINGER")) {
		}

		else if (cmd.equals("GAMES")) {
		}

		else if (cmd.equals("GOTO")) {
		}

		else if (cmd.equals("HELP")) {
			String text = in.remainder();
			results.add(new HelpResponse(text));
		}

		else if (cmd.equals("HISTORY")) {
			String owner = in.getString();
			List<GameReference> references = readGameReferences(in, owner, GameReference.HISTORY_TYPE);
			results.add(new HistoryResponse(owner, references));
		}

		else if (cmd.equals("IMPORT")) {
		}

		else if (cmd.equals("KIBITZ")) {
			String handle = in.getString();
			UserType userType = UserType.getInstance(in.getInt());
			String message = in.remainder();
			results.add(new KibitzResponse(handle, userType, message));
		}

		else if (cmd.equals("LIBRARY")) {
			String owner = in.getString();
			List<GameReference> references = readGameReferences(in, owner, GameReference.LIBRARY_TYPE);
			results.add(new LibraryResponse(owner, references));
		}

		else if (cmd.equals("LIST")) {
		}

		else if (cmd.equals("LOGIN")) {
			String message = in.remainder();
			results.add(new LoginResponse(message));
		}

		else if (cmd.equals("MATCH")) {
			abortRequested = false;
			adjournRequested = false;
			
			// MATCH 1174 FogleBird 0 60 0 1 1 1 -1
			int rating = in.getInt();
			String handle = in.getString();
			DictionaryType dictionary = DictionaryType.getInstance(in.getInt());
			int time = in.getInt();
			int increment = in.getInt();
			boolean rated = in.getBoolean();
			boolean noescape = in.getBoolean();
			ChallengeMode challengeMode = ChallengeMode.getInstance(in.getInt());
			UserType userType = UserType.getInstance(in.getInt());
			results.add(new MatchResponse(handle, rating, dictionary, time, increment, rated, noescape, challengeMode, userType));
		}

		else if (cmd.equals("MESSAGE")) {
		}

		else if (cmd.equals("MORE")) {
		}

		else if (cmd.equals("MOVE")) {
			abortRequested = false;
			adjournRequested = false;
			
			String coordinates = in.getString();
			String word = in.getString();
			in.getInt(); // score
			int minutes = in.getInt();
			int seconds = in.getInt();
			String opponentTiles = in.getString();
			Move move = Move.getInstance(coordinates + " " + word);
			Tile[] tiles = opponentTiles.equals("---") ? new Tile[0] : Convert.toTiles(opponentTiles);
			results.add(new MoveResponse(move, minutes, seconds, tiles));
		}

		else if (cmd.equals("NOPLAY")) {
		}

		else if (cmd.equals("NORELAY")) {
		}

//		0 OBSERVE LOGIN 
//		101 null 
//		0 15 0 11101
//		oohbadooba 1763 adelpni null
//		MOVE H4 plained 76 14 51 iuweelq 0 MOVE G7 qi 44 14 47 ueelewe 0 MOVE E11 weel 26 14 40 meofegu 0 MOVE A12 foam 39 14 19 uygerer 0 MOVE B6 gruyere 72 13 51 ird?vri 0 MOVE 8A durr 18 12 30 ?etvtii 0 MOVE 10H dive 16 12 27 ?ittafs 0 MOVE 11K fit 22 11 56 tkats?e 0 MOVE 3I jakes 32 11 29 ttiagc? 0 MOVE M9 attic 16 10 59 iy?otrg 0 MOVE J2 randy 33 10 45 itgoio? 0 
//		 STOP 
//		bendetad 1874 uldaabs null
//		MOVE 5E ballads 40 14 11 ?iotudv 0 MOVE F8 divot 21 13 25 ?aosuug 0 MOVE 14A aNgulous 70 12 51 ilnmphx 0 MOVE 6D pix 57 12 21 elnsmhw 0 MOVE 15H hewn 45 11 47 lnrsbmj 0 MOVE 12D bet 10 08 53 lnnrsmj 0 MOVE I9 mir 15 07 49 aelnnsj 0 MOVE I3 jeans 40 07 01 alnottc 0 MOVE 4H penal 29 06 33 enoottc 0 MOVE 4C coot 27 06 08 eeenthz 0 MOVE 12H hent 23 04 27 aaeenoz 0 
//		 STOP 
		
//		0 RESUME 
//		101 01:Sep:06 
//		0 25 0 10101
//		lorem 935 agtoron null
//		MOVE 8H goon 10 24 53 utioraa 0 CHANGE hzt?rga 24 44 4 
//		 STOP 
//		dolor 399 lbdrijv null
//		MOVE I7 job 23 24 29 ldriveq 0 MOVE K3 driven 20 24 18 lqooire 0 
//		 STOP 

		else if (cmd.equals("OBSERVE")) {
			String type = in.getString();
			if (type.equals("LOGIN")) {
				System.out.println(data.toString());
				IGame game = readGame(in);
				results.add(new ObserveStartResponse(game));
				observing = true;
			}
			else if (type.equals("MOVE")) {
				// 0 OBSERVE MOVE  5C reglazed 19 10 02 einoord
				String coordinates = in.getString();
				String word = in.getString();
				in.getInt(); // score
				int minutes = in.getInt();
				int seconds = in.getInt();
				String tileString = in.getString();
				Move move = Move.getInstance(coordinates + " " + word);
				Tile[] tiles = tileString.equals("---") ? new Tile[0] : Convert.toTiles(tileString);
				results.add(new ObserveMoveResponse(move, minutes, seconds, tiles));
			}
			else if (type.equals("PAS")) {
				int minutes = in.getInt();
				int seconds = in.getInt();
				results.add(new ObservePassResponse(minutes, seconds));
			}
			else if (type.equals("CHANGE")) {
				Tile[] tiles = Convert.toTiles(in.getString());
				int minutes = in.getInt();
				int seconds = in.getInt();
				int count = 0;//in.getInt();
				results.add(new ObserveChangeResponse(tiles, minutes, seconds, count));
			}
			else if (type.equals("CHALLENGE")) {
				boolean successful = false;
				while (in.hasNext()) {
					String valid = in.getString().toLowerCase();
					if (valid.equals("no")) {
						successful = true;
						break;
					}
				}
				results.add(new ObserveChallengeResponse(successful));
			}
		}

		else if (cmd.equals("PAS")) {
			abortRequested = false;
			adjournRequested = false;
			
			int minutes = in.getInt();
			int seconds = in.getInt();
			results.add(new PassResponse(minutes, seconds));
		}

		else if (cmd.equals("PASS")) {
		}

		else if (cmd.equals("PENDING")) {
		}

		else if (cmd.equals("PERSONAL")) {
		}

		else if (cmd.equals("PING")) {
		}

		// Receive: "0 PLAY 565 matman 0 60 0 1 1 1 2 uasesgc ifoavpr"
		else if (cmd.equals("PLAY")) {
			abortRequested = false;
			adjournRequested = false;
			
			int rating = in.getInt();
			String handle = in.getString();
			DictionaryType dictionary = DictionaryType.getInstance(in.getInt());
			int time = in.getInt();
			int increment = in.getInt();
			boolean rated = in.getBoolean();
			boolean noescape = in.getBoolean();
			ChallengeMode challengeMode = ChallengeMode.getInstance(in.getInt());
			int firstPlayer = in.getInt();
			Tile[] rack1 = Convert.toTiles(in.getString());
			Tile[] rack2 = Convert.toTiles(in.getString());
			results.add(new AcceptResponse(handle, rating, time, increment, rated, noescape, dictionary, challengeMode, firstPlayer, rack1, rack2));
		}

		else if (cmd.equals("POOL")) {
		}
		
		// --> PREMOVE 0 word word word    0 = Dictionary?
		// <-- PREMOVE No Yes No
		else if (cmd.equals("PREMOVE")) {
			boolean valid = true;
			while (in.hasNext()) {
				String isWord = in.getString().toLowerCase();
				if (isWord.equals("no")) {
					valid = false;
					break;
				}
			}
			results.add(new PreMoveResponse(valid, preMove));
		}

		else if (cmd.equals("RELAY")) {
		}

		else if (cmd.equals("REPORT")) {
		}

		else if (cmd.equals("RESERVE")) {
		}

		// opponent resigned
		// *** Unhandled Response: "0 RESIGN 1"
		// opp aborted
		// *** Unhandled Response: "0 RESIGN 0 Game aborted by lorem."
		// self aborted
		// *** Unhandled Response: "0 RESIGN 0 Game aborted by dolor."
		// mutual abort
		// *** Unhandled Response: "0 RESIGN 0 Game aborted by mutual agreement."
		else if (cmd.equals("RESIGN")) {
		}

		else if (cmd.equals("RESUME")) {
			System.out.println(data.toString());
			IGame game = readGame(in);
			results.add(new ResumeResponse(game));
		}

		else if (cmd.equals("SAY")) {
		}

		else if (cmd.equals("SEEK")) {
			// seeker status? = normal, computer, helper, best, etc
			// SEEK 557 bboss 0 8 0 1 1 3 0
			int rating = in.getInt();
			String handle = in.getString();
			DictionaryType dictionary = DictionaryType.getInstance(in.getInt());
			int time = in.getInt();
			int increment = in.getInt();
			boolean rated = in.getBoolean();
			boolean noescape = in.getBoolean();
			ChallengeMode challengeMode = ChallengeMode.getInstance(in.getInt());
			UserType userType = UserType.getInstance(in.getInt());
			results.add(new SeekResponse(handle, rating, time, increment, rated, noescape, dictionary, challengeMode, userType));
		}

		else if (cmd.equals("SETALL")) {
			Settings settings = new Settings();
			
			setString(in, settings, "null");
			setBoolean(in, settings, "user.quietplay");
			setInt(in, settings, "user.dictionary");
			setInt(in, settings, "user.time");
			setInt(in, settings, "user.increment");
			setBoolean(in, settings, "user.noescape");
			setInt(in, settings, "user.mood");
			setInt(in, settings, "user.sound");
			setBoolean(in, settings, "null");
			setInt(in, settings, "user.channel");
			setInt(in, settings, "user.tell");
			setInt(in, settings, "user.challenge");
			setBoolean(in, settings, "user.rated");
			setInt(in, settings, "user.rating1");
			setInt(in, settings, "user.rating0");
			setInt(in, settings, "user.rating2");
			setInt(in, settings, "user.rating3");
			setInt(in, settings, "user.rating4");
			setBoolean(in, settings, "user.private");
			setInt(in, settings, "user.language");
			setInt(in, settings, "user.rating5");
			setInt(in, settings, "user.rating6");
			setInt(in, settings, "user.kibitz");
			
			results.add(new SetAllResponse(settings));
		}

		else if (cmd.equals("SET")) {
			String type = in.next().toUpperCase();
			if (type != null) {
				if (type.equals("FORMULA")) {
//					int min = in.getInt();
//					int max = in.getInt();
//					boolean provisional = in.getBoolean();
//					boolean member = in.getBoolean();
//					boolean fairplay = in.getBoolean();
//					in.getInt(); // unfinished games
//					results.add(new SetFormulaResponse(min, max, provisional, member, fairplay));
					
					Settings settings = new Settings();
					
					settings.set("formula.minimumRating", in.getInt());
					settings.set("formula.maximumRating", in.getInt());
					settings.set("formula.provisional", in.getBoolean());
					settings.set("formula.member", in.getBoolean());
					settings.set("formula.fairplay", in.getBoolean());
					settings.set("formula.unfinished", in.getInt());
					
					results.add(new SetFormulaResponse(settings));
				}
			}
		}

		else if (cmd.equals("SFILTER")) {
		}

		else if (cmd.equals("SHOUT")) {
		}

		else if (cmd.equals("SHUFFLE")) {
		}

		else if (cmd.equals("SHUTDOWN")) {
		}

		else if (cmd.equals("SOUGHT")) {
			results.add(new ClearSeeksResponse());
			while (in.hasNext()) {
				int rating = in.getInt();
				String handle = in.getString();
				DictionaryType dictionary = DictionaryType.getInstance(in.getInt());
				int time = in.getInt();
				int increment = in.getInt();
				boolean rated = in.getBoolean();
				boolean noescape = in.getBoolean();
				ChallengeMode challengeMode = ChallengeMode.getInstance(in.getInt());
				UserType userType = UserType.getInstance(in.getInt());
				results.add(new SeekResponse(handle, rating, time, increment, rated, noescape, dictionary, challengeMode, userType));
			}
		}

		else if (cmd.equals("TELL")) {
			// TELL azshar 0 hi gl
			String handle = in.getString();
			UserType userType = UserType.getInstance(in.getInt());
			String message = in.remainder();
			results.add(new TellResponse(handle, message, userType));
		}

		else if (cmd.equals("UNEXAMINE")) {
		}

		else if (cmd.equals("UNMATCH")) {
		}

		else if (cmd.equals("UNOBSERVE")) {
		}

		else if (cmd.equals("UNRELAYED")) {
		}

		else if (cmd.equals("UNSEEK")) {
			// UNSEEK handle
			String handle = in.getString();
			results.add(new UnseekResponse(handle));
		}

		else if (cmd.equals("UPTIME")) {
		}

		else if (cmd.equals("VARS")) {
		}

		else if (cmd.equals("WHISPER")) {
			String handle = in.getString();
			UserType userType = UserType.getInstance(in.getInt());
			String message = in.remainder();
			results.add(new WhisperResponse(handle, userType, message));
		}

		else if (cmd.equals("WHO")) {
			// LIST SET LOGIN CLOSE MOVE BEST
			String type = in.next().toUpperCase();
			if (type != null) {
				if (type.equals("SET")) {
					// WHO SET 1157 FogleBird a 0 0 ...
					while (in.hasNext()) {
						User user = readUser(in);
						results.add(new BuddyLoginResponse(user));
					}
				}
				else if (type.equals("LIST")) {
					// WHO LIST 2574 
					// 1959 TakeItAway * 0 0
					// rating	handle	state	type	dictionary
					int count = in.getInt();
					WhoListResponse response = new WhoListResponse(count);
					while (in.hasNext()) {
						User user = readUser(in);
						response.addItem(new WhoResponse(user));
					}
					results.add(response);
				}
				else if (type.equals("LOGIN")) {
					// buddy signed on
					while (in.hasNext()) {
						User user = readUser(in);
						results.add(new BuddyLoginResponse(user));
					}
				}
				else if (type.equals("MOVE")) {
					// buddy changed state
					while (in.hasNext()) {
						User user = readUser(in);
						results.add(new BuddyStateResponse(user));
					}
				}
				else if (type.equals("CLOSE")) {
					// buddy signed off
					while (in.hasNext()) {
						User user = readUser(in);
						results.add(new BuddyLogoutResponse(user));
					}
				}
				else if (type.equals("BEST")) {
					results.add(new NullResponse(new String(data.getBytes())));
				}
			}
		}
		
		else if (cmd.equals("XI")) {
			results.add(new NullResponse(new String(data.getBytes())));
		}
		
		/* end parsing */
		
		Response[] result = new Response[results.size()];
		results.toArray(result);
		return result;
	}
	
	private Data[] convert(List<Outbound> list) {
		if (list == null || list.size() == 0) return null;
		Data[] result = new Data[list.size()];
		for (int i = 0; i < result.length; i++) {
			Outbound out = (Outbound)list.get(i);
			Data data = convert(out);
			result[i] = data;
		}
		return result;
	}
	
	private Data convert(Outbound out) {
		Data result = null;
		if (out.getName() != null) {
			result = new Data(out.toString().getBytes());
		}
		return result;
	}
	
	
	private void setBoolean(Inbound in, Settings settings, String key) {
		boolean value = in.getBoolean();
		if (in.getInt() == 1) {
			settings.set(key, value);
		}
	}
	
	private void setInt(Inbound in, Settings settings, String key) {
		int value = in.getInt();
		if (in.getInt() == 1) {
			settings.set(key, value);
		}
	}
	
	private void setString(Inbound in, Settings settings, String key) {
		String value = in.getString();
		if (in.getInt() == 1) {
			settings.set(key, value);
		}
	}
	
	private List<GameReference> readGameReferences(Inbound in, String owner, int type) {
		List<GameReference> references = new ArrayList<GameReference>();
		while (in.hasNext()) {
			int id = in.getInt();
			int rating1 = in.getInt();
			String handle1 = in.getString();
			int rating2 = in.getInt();
			String handle2 = in.getString();
			String dictionaryName = in.getString();
			if (dictionaryName.equalsIgnoreCase("TWL98")) {
				dictionaryName = "TWL06";
			}
			DictionaryType dictionary = DictionaryType.getInstance(dictionaryName);
			int result = in.getInt();
			String resultString = "???";
			if (result > 0) {
				resultString = "1-0";
			} else if (result < 0) {
				resultString = "0-1";
			}
			result = Math.abs(result);
			if (result == 107) {
				resultString = "Draw";
			}
			if (result == 101 || result == 100 || result == 108) {
				resultString = null;
			}
			String dateString = in.getString(); // 04:Feb:07
			Calendar date = Calendar.getInstance();
			Scanner scanner = new Scanner(dateString).useDelimiter(":");
			String month = "";
			if (scanner.hasNext()) date.set(Calendar.DATE, scanner.nextInt());
			if (scanner.hasNext()) month = scanner.next().toLowerCase();
			if (scanner.hasNext()) date.set(Calendar.YEAR, scanner.nextInt() + 2000);
			// geez
			if (month.equals("jan")) date.set(Calendar.MONTH, Calendar.JANUARY);
			else if (month.equals("feb")) date.set(Calendar.MONTH, Calendar.FEBRUARY);
			else if (month.equals("mar")) date.set(Calendar.MONTH, Calendar.MARCH);
			else if (month.equals("apr")) date.set(Calendar.MONTH, Calendar.APRIL);
			else if (month.equals("may")) date.set(Calendar.MONTH, Calendar.MAY);
			else if (month.equals("jun")) date.set(Calendar.MONTH, Calendar.JUNE);
			else if (month.equals("jul")) date.set(Calendar.MONTH, Calendar.JULY);
			else if (month.equals("aug")) date.set(Calendar.MONTH, Calendar.AUGUST);
			else if (month.equals("sep")) date.set(Calendar.MONTH, Calendar.SEPTEMBER);
			else if (month.equals("oct")) date.set(Calendar.MONTH, Calendar.OCTOBER);
			else if (month.equals("nov")) date.set(Calendar.MONTH, Calendar.NOVEMBER);
			else if (month.equals("dec")) date.set(Calendar.MONTH, Calendar.DECEMBER);
			
			GameReference entry = new GameReference(owner, type);
			entry.setDate(date);
			entry.setDictionary(dictionary);
			entry.setHandle1(handle1);
			entry.setHandle2(handle2);
			entry.setId(id);
			entry.setRating1(rating1);
			entry.setRating2(rating2);
			entry.setResult(resultString);
			references.add(entry);
		}
		return references;
	}
	
	private User readUser(Inbound in) {
		int rating = in.getInt();
		String handle = in.getString();
		UserState userState = UserState.getInstance(in.getChar());
		UserType userType = UserType.getInstance(in.getInt());
		DictionaryType dictionaryType = DictionaryType.getInstance(in.getInt());
		User user = new User();
		user.setDictionaryType(dictionaryType);
		user.setHandle(handle);
		user.setRating(rating);
		user.setUserState(userState);
		user.setUserType(userType);
		return user;
	}
	
	private IGame readGame(Inbound in) {
		in.getString(); // winner?
		in.getString(); // date?
		DictionaryType dictionary = DictionaryType.getInstance(in.getInt());
		int time = in.getInt();
		int increment = in.getInt();
		
//		boolean noescape = true;
		ChallengeMode challengeMode = ChallengeMode.DOUBLE;
//		boolean rated = true;
//		boolean privateGame = false;
		
		// 11101 - noescape, challenge mode, rated, private
		Scanner scanner = new Scanner(in.getString()).useDelimiter("");
		if (scanner.hasNext()) scanner.nextInt(); //noescape = scanner.nextInt() == 1 ? true : false;
		if (scanner.hasNext()) challengeMode = ChallengeMode.getInstance(scanner.nextInt());
		if (scanner.hasNext()) scanner.nextInt(); //rated = scanner.nextInt() == 1 ? true : false;
		if (scanner.hasNext()) scanner.nextInt(); //privateGame = scanner.nextInt() == 1 ? true : false;
		
		// create the game
		IGame game = new StandardGame(dictionary, challengeMode, 2);
		game.setTimeLimit(time);
		game.setTimeIncrement(increment);
		
		ITilePool pool = game.getTilePool();
		
		String handle1 = in.getString();
		int rating1 = in.getInt();
		String startRack1 = in.getString();
		in.getString();
		List<Holder> holders1 = loadHolders(in);
		
		String handle2 = in.getString();
		int rating2 = in.getInt();
		String startRack2 = in.getString();
		in.getString();
		List<Holder> holders2 = loadHolders(in);
		
		IPlayer player1;
		if (handle1.equalsIgnoreCase(handle)) {
			player1 = new LocalPlayer(game, 1, handle1, new StandardTileRack());
		}
		else {
			player1 = new RemotePlayer(game, 1, handle1, new StandardTileRack());
		}
		player1.setRating(rating1);
		
		IPlayer player2;
		if (handle2.equalsIgnoreCase(handle)) {
			player2 = new LocalPlayer(game, 2, handle2, new StandardTileRack());
		}
		else {
			player2 = new RemotePlayer(game, 2, handle2, new StandardTileRack());
		}
		player2.setRating(rating2);
		
		ITileRack rack1 = player1.getTileRack();
		ITileRack rack2 = player2.getTileRack();
		rack1.replaceAll(pool, Convert.toTiles(startRack1));
		rack2.replaceAll(pool, Convert.toTiles(startRack2));
		
		boolean previousAnalysisMode = game.isAnalysisMode();
		game.setAnalysisMode(false);
		game.start();
		
		int count = holders1.size() + holders2.size();
		for (int i = 0; i < count; i++) {
			int p = i % 2;
			Holder holder;
			IPlayer player;
			if (p == 0) {
				holder = holders1.remove(0);
				player = player1;
			}
			else {
				holder = holders2.remove(0);
				player = player2;
			}
			if (holder instanceof MoveHolder) {
				MoveHolder moveHolder = (MoveHolder)holder;
				Move move = Move.getInstance(moveHolder.coordinates + " " + moveHolder.word);
				game.doMove(move);
				player.getClock().set(moveHolder.minutes, moveHolder.seconds);
				player.getTileRack().replaceAll(pool, Convert.toTiles(moveHolder.rack));
			}
			else if (holder instanceof ChangeHolder) {
				ChangeHolder changeHolder = (ChangeHolder)holder;
				game.doExchangeTiles(player.getTileRack().getTiles());
				player.getClock().set(changeHolder.minutes, changeHolder.seconds);
				player.getTileRack().replaceAll(pool, Convert.toTiles(changeHolder.rack));
			}
			else if (holder instanceof PassHolder) {
				PassHolder passHolder = (PassHolder)holder;
				game.doPassTurn();
				player.getClock().set(passHolder.minutes, passHolder.seconds);
			}
		}
		
		game.setAnalysisMode(previousAnalysisMode);
		return game;
	}
	
	private static class Holder {
		int minutes;
		int seconds;
	}
	
	private static class MoveHolder extends Holder {
		String coordinates;
		String word;
		int score;
		String rack;
	}
	
	private static class PassHolder extends Holder {
	}
	
	private static class ChangeHolder extends Holder {
		String rack;
	}
	
	private List<Holder> loadHolders(Inbound in) {
		List<Holder> result = new ArrayList<Holder>();
		
		boolean done = false;
		do {
			String type = in.getString().toUpperCase();
			if (type.equals("MOVE")) {
				MoveHolder holder = new MoveHolder();
				holder.coordinates = in.getString();
				holder.word = in.getString();
				holder.score = in.getInt();
				holder.minutes = in.getInt();
				holder.seconds = in.getInt();
				holder.rack = in.getString();
				in.getString();
				result.add(holder);
			}
			else if (type.equals("PAS")) {
				PassHolder holder = new PassHolder();
				holder.minutes = in.getInt();
				holder.seconds = in.getInt();
				in.getString(); // challenged move (failed) H6_althoid_76
				result.add(holder);
			}
			else if (type.equals("CHANGE")) {
				ChangeHolder holder = new ChangeHolder();
				holder.rack = in.getString();
				holder.minutes = in.getInt();
				holder.seconds = in.getInt();
				in.getString();
				result.add(holder);
			}
			else {
				done = true;
			}
		} while (!done);
		
		return result;
	}

}
