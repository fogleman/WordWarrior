package com.michaelfogleman.wordwarrior.gui;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.michaelfogleman.wordwarrior.ScrabblePlugin;
import com.michaelfogleman.wordwarrior.model.Session;
import com.michaelfogleman.wordwarrior.protocol.Connection;
import com.michaelfogleman.wordwarrior.protocol.command.*;

public class CommandInterpreter {
	
	private static final String HANDLE = "([a-zA-Z0-9]{1,10})";
	private static final String INTEGER = "([0-9]+)";
	private static final String WORD_LIST = "( [a-zA-Z]+)+";
	
	public void interpret(String input) {
		Matcher matcher;
		Session session = ScrabblePlugin.getDefault().getSession();
		Connection connection = session.getConnection();
		
		matcher = match(input, "abort");
		if (matcher != null) {
			connection.send(new AbortCommand());
		}
		
		matcher = match(input, "adjourn");
		if (matcher != null) {
			connection.send(new AdjournCommand());
		}
		
		matcher = match(input, "add buddy " + HANDLE);
		if (matcher != null) {
			String handle = matcher.group(1);
			connection.send(new AddBuddyCommand(handle));
		}
		
		matcher = match(input, "add channel " + INTEGER);
		if (matcher != null) {
			try {
				int channel = Integer.parseInt(matcher.group(1));
				connection.send(new AddChannelCommand(channel));
			} catch (NumberFormatException e) {
			}
		}
		
		matcher = match(input, "allobservers " + HANDLE);
		if (matcher != null) {
			String handle = matcher.group(1);
			connection.send(new AllObserversCommand(handle));
		}
		
		matcher = match(input, "archive " + HANDLE);
		if (matcher != null) {
			String handle = matcher.group(1);
			session.getArchiveManager().refresh(handle);
		}
		
		matcher = match(input, "ask (.+)");
		if (matcher != null) {
			String message = matcher.group(1);
			connection.send(new AskCommand(message));
		}
		
		matcher = match(input, "assess " + HANDLE + "( [0-9]+)?");
		if (matcher != null) {
			String handle = matcher.group(1);
			String arg = matcher.group(2);
			if (arg == null) {
				connection.send(new AssessCommand(handle));
			}
			else {
				int scoreDifference = Integer.parseInt(arg.trim());
				connection.send(new AssessCommand(handle, scoreDifference));
			}
		}
		
		matcher = match(input, "buddies");
		if (matcher != null) {
			connection.send(new GetBuddiesCommand());
		}
		
		matcher = match(input, "channels");
		if (matcher != null) {
			connection.send(new GetChannelsCommand());
		}
		
		matcher = match(input, "check" + WORD_LIST);
		if (matcher != null) {
			StringTokenizer st = new StringTokenizer(input);
			int n = st.countTokens() - 1;
			String[] words = new String[n];
			st.nextToken();
			for (int i = 0; i < words.length; i++) {
				words[i] = st.nextToken().trim();
			}
			connection.send(new CheckWordsCommand(words));
		}
		
		matcher = match(input, "date");
		if (matcher != null) {
			connection.send(new GetServerTimeCommand());
		}
		
		matcher = match(input, "finger " + HANDLE);
		if (matcher != null) {
			String handle = matcher.group(1);
			connection.send(new GetUserInfoCommand(handle));
		}
		
		matcher = match(input, "help");
		if (matcher != null) {
			connection.send(new HelpCommand());
		}
		
		matcher = match(input, "help ([a-zA-Z]+)");
		if (matcher != null) {
			String topic = matcher.group(1);
			connection.send(new HelpCommand(topic));
		}
		
		matcher = match(input, "kibitz (.+)");
		if (matcher != null) {
			String message = matcher.group(1);
			connection.send(new KibitzCommand(message));
		}
		
		matcher = match(input, "match " + HANDLE);
		if (matcher != null) {
			String handle = matcher.group(1);
			connection.send(new MatchCommand(handle));
		}
		
		matcher = match(input, "message " + HANDLE + " (.*)");
		if (matcher != null) {
			String handle = matcher.group(1);
			String message = matcher.group(2);
			connection.send(new MessageCommand(handle, message));
		}
		
		matcher = match(input, "more");
		if (matcher != null) {
			connection.send(new MoreCommand());
		}
		
		matcher = match(input, "observe");
		if (matcher != null) {
			connection.send(new ObserveCommand());
		}
		
		matcher = match(input, "observe " + HANDLE);
		if (matcher != null) {
			String handle = matcher.group(1);
			connection.send(new ObserveCommand(handle));
		}
		
		matcher = match(input, "pending");
		if (matcher != null) {
			connection.send(new GetPendingMatchesCommand());
		}
		
		matcher = match(input, "personal " + HANDLE);
		if (matcher != null) {
			String handle = matcher.group(1);
			connection.send(new GetPersonalInfoCommand(handle));
		}
		
		matcher = match(input, "ping " + HANDLE);
		if (matcher != null) {
			String handle = matcher.group(1);
			connection.send(new PingUserCommand(handle));
		}
		
		matcher = match(input, "remove buddy " + HANDLE);
		if (matcher != null) {
			String handle = matcher.group(1);
			connection.send(new RemoveBuddyCommand(handle));
		}
		
		matcher = match(input, "remove channel " + INTEGER);
		if (matcher != null) {
			try {
				int channel = Integer.parseInt(matcher.group(1));
				connection.send(new RemoveChannelCommand(channel));
			} catch (NumberFormatException e) {
			}
		}
		
		matcher = match(input, "resign");
		if (matcher != null) {
			connection.send(new ResignCommand());
		}
		
		matcher = match(input, "seek");
		if (matcher != null) {
			connection.send(new SeekCommand());
		}
		
		matcher = match(input, "tell " + HANDLE + " (.*)");
		if (matcher != null) {
			String handle = matcher.group(1);
			String message = matcher.group(2);
			connection.send(new TellCommand(handle, message));
		}
		
		matcher = match(input, "unobserve");
		if (matcher != null) {
			connection.send(new UnobserveCommand());
		}
		
		matcher = match(input, "unseek");
		if (matcher != null) {
			connection.send(new UnseekCommand());
		}
		
		matcher = match(input, "uptime");
		if (matcher != null) {
			connection.send(new GetServerUptimeCommand());
		}
		
		matcher = match(input, "vars " + HANDLE);
		if (matcher != null) {
			String handle = matcher.group(1);
			connection.send(new GetUserVariablesCommand(handle));
		}
		
		matcher = match(input, "whisper (.+)");
		if (matcher != null) {
			String message = matcher.group(1);
			connection.send(new WhisperCommand(message));
		}
	}
	
	private Matcher match(String input, String regex) {
		Matcher matcher = pattern(regex).matcher(input);
		if (matcher.matches()) {
			return matcher;
		}
		return null;
	}
	
	private Pattern pattern(String regex) {
		return Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
	}

}
