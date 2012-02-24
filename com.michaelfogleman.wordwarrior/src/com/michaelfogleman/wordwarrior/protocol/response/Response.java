package com.michaelfogleman.wordwarrior.protocol.response;

public abstract class Response {
	
//	private String rawData;
//	private List arguments;
//	
//	protected Response() {
//		arguments = new ArrayList();
//	}
//	
//	public void setRawData(String rawData) {
//		this.rawData = rawData;
//	}
//	
//	public String getRawData() {
//		return rawData;
//	}
//	
//	protected String getString(int index) {
//		return (String)arguments.get(index);
//	}
//	
//	protected int getInt(int index) {
//		try {
//			return Integer.parseInt(getString(index));
//		} catch (NumberFormatException e) {
//			return 0;
//		}
//	}
//	
//	protected boolean getBoolean(int index) {
//		int n = getInt(index);
//		return (n != 0);
//	}
//	
//	public void addArgument(String argument) {
//		arguments.add(argument);
//	}
//	
//	public void addArgument(int argument) {
//		arguments.add(new Integer(argument));
//	}
//	
//	public void init() {
//	}
	
//	public String toString() {
//		StringBuffer b = new StringBuffer();
//		for (Iterator i = arguments.iterator(); i.hasNext();) {
//			String argument = i.next().toString();
//			if (b.length() > 0) b.append(' ');
//			b.append(argument);
//		}
//		return b.toString();
//	}
	
//	private static Map map;
//	
//	private static Class[] responses = new Class[] {
//		AbortResponse.class, AcceptResponse.class, AdjournResponse.class, AdjournedResponse.class,
//		AdjudicateResponse.class, AdjustResponse.class, AliveResponse.class, AllObserversResponse.class,
//		AsitisResponse.class, AskResponse.class, AssessResponse.class, BestResponse.class,
//		BuddiesResponse.class, CensorResponse.class, ChallengeResponse.class, ChangeResponse.class,
//		ChannelResponse.class, CheckResponse.class, ClearResponse.class, CloseResponse.class,
//		ConnectResponse.class, DateResponse.class, DeclineResponse.class, DeleteResponse.class,
//		DisconnectResponse.class, DuplicateResponse.class, EchoResponse.class, ExamineResponse.class,
//		FingerResponse.class, GamesResponse.class, GotoResponse.class, HelpResponse.class,
//		HistoryResponse.class, ImportResponse.class, KibitzResponse.class, LibraryResponse.class,
//		ListResponse.class, LoginResponse.class, MatchResponse.class, MessageResponse.class,
//		MoreResponse.class, MoveResponse.class, NoPlayResponse.class, NoRelayResponse.class,
//		ObserveResponse.class, PasResponse.class, PassResponse.class, PendingResponse.class,
//		PersonalResponse.class, PingResponse.class, PlayResponse.class, PoolResponse.class,
//		RelayResponse.class, ReportResponse.class, ReserveResponse.class, ResignResponse.class,
//		ResumeResponse.class, SayResponse.class, SeekResponse.class, SetResponse.class,
//		SetAllResponse.class, SFilterResponse.class, ShoutResponse.class, SoughtResponse.class,
//		ShuffleResponse.class, ShutdownResponse.class, TellResponse.class, UnexamineResponse.class,
//		UnmatchResponse.class, UnobserveResponse.class, UnrelayedResponse.class, UnseekResponse.class,
//		UptimeResponse.class, VarsResponse.class, WhisperResponse.class, WhoResponse.class
//	};
//	
//	static {
//		try {
//			map = new HashMap();
//			for (int i = 0; i < responses.length; i++) {
//				Class response = responses[i];
//				Response instance = (Response)response.newInstance();
//				map.put(instance.getName().toUpperCase(), response);
//			}
//		} catch (InstantiationException e) {
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	public static Response createInstance(String name) {
//		Class response = (Class)map.get(name.toUpperCase());
//		if (response != null) {
//			try {
//				Response instance = (Response)response.newInstance();
//				return instance;
//			} catch (InstantiationException e) {
//				e.printStackTrace();
//			} catch (IllegalAccessException e) {
//				e.printStackTrace();
//			}
//		}
//		return null;
//	}

}
