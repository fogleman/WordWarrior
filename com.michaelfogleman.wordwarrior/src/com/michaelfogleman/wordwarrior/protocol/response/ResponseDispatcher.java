package com.michaelfogleman.wordwarrior.protocol.response;

public class ResponseDispatcher {
	
	public static void dispatch(Response response, IResponseHandler handler) {
		if (response instanceof AbortResponse) {
			AbortResponse instance = (AbortResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof AcceptResponse) {
			AcceptResponse instance = (AcceptResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof AdjournResponse) {
			AdjournResponse instance = (AdjournResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof AdjournedResponse) {
			AdjournedResponse instance = (AdjournedResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof AdjudicateResponse) {
			AdjudicateResponse instance = (AdjudicateResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof AdjustResponse) {
			AdjustResponse instance = (AdjustResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof AliveResponse) {
			AliveResponse instance = (AliveResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof AllObserversResponse) {
			AllObserversResponse instance = (AllObserversResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof AsitisResponse) {
			AsitisResponse instance = (AsitisResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof AskResponse) {
			AskResponse instance = (AskResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof AssessResponse) {
			AssessResponse instance = (AssessResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof BestResponse) {
			BestResponse instance = (BestResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof BuddiesResponse) {
			BuddiesResponse instance = (BuddiesResponse)response;
			handler.handle(instance);
			return;
		}
		
		if (response instanceof BuddyLoginResponse) {
			BuddyLoginResponse instance = (BuddyLoginResponse)response;
			handler.handle(instance);
			return;
		}
		
		if (response instanceof BuddyLogoutResponse) {
			BuddyLogoutResponse instance = (BuddyLogoutResponse)response;
			handler.handle(instance);
			return;
		}
		
		if (response instanceof BuddyStateResponse) {
			BuddyStateResponse instance = (BuddyStateResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof CensorResponse) {
			CensorResponse instance = (CensorResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof ChallengeResponse) {
			ChallengeResponse instance = (ChallengeResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof ChangeResponse) {
			ChangeResponse instance = (ChangeResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof ChannelResponse) {
			ChannelResponse instance = (ChannelResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof CheckResponse) {
			CheckResponse instance = (CheckResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof ClearResponse) {
			ClearResponse instance = (ClearResponse)response;
			handler.handle(instance);
			return;
		}
		
		if (response instanceof ClearSeeksResponse) {
			ClearSeeksResponse instance = (ClearSeeksResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof CloseResponse) {
			CloseResponse instance = (CloseResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof ConnectResponse) {
			ConnectResponse instance = (ConnectResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof DateResponse) {
			DateResponse instance = (DateResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof DeclineResponse) {
			DeclineResponse instance = (DeclineResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof DeleteResponse) {
			DeleteResponse instance = (DeleteResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof DisconnectResponse) {
			DisconnectResponse instance = (DisconnectResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof DuplicateResponse) {
			DuplicateResponse instance = (DuplicateResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof EchoResponse) {
			EchoResponse instance = (EchoResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof ExamineResponse) {
			ExamineResponse instance = (ExamineResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof FingerResponse) {
			FingerResponse instance = (FingerResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof GamesResponse) {
			GamesResponse instance = (GamesResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof NullResponse) {
			NullResponse instance = (NullResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof HelpResponse) {
			HelpResponse instance = (HelpResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof HistoryResponse) {
			HistoryResponse instance = (HistoryResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof ImportResponse) {
			ImportResponse instance = (ImportResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof KibitzResponse) {
			KibitzResponse instance = (KibitzResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof LibraryResponse) {
			LibraryResponse instance = (LibraryResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof ListResponse) {
			ListResponse instance = (ListResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof LoginResponse) {
			LoginResponse instance = (LoginResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof MatchResponse) {
			MatchResponse instance = (MatchResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof MessageResponse) {
			MessageResponse instance = (MessageResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof MoreResponse) {
			MoreResponse instance = (MoreResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof MoveResponse) {
			MoveResponse instance = (MoveResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof NoPlayResponse) {
			NoPlayResponse instance = (NoPlayResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof NoRelayResponse) {
			NoRelayResponse instance = (NoRelayResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof ObserveStartResponse) {
			ObserveStartResponse instance = (ObserveStartResponse)response;
			handler.handle(instance);
			return;
		}
		
		if (response instanceof ObserveMoveResponse) {
			ObserveMoveResponse instance = (ObserveMoveResponse)response;
			handler.handle(instance);
			return;
		}
		
		if (response instanceof ObserveChangeResponse) {
			ObserveChangeResponse instance = (ObserveChangeResponse)response;
			handler.handle(instance);
			return;
		}
		
		if (response instanceof ObservePassResponse) {
			ObservePassResponse instance = (ObservePassResponse)response;
			handler.handle(instance);
			return;
		}
		
		if (response instanceof ObserveChallengeResponse) {
			ObserveChallengeResponse instance = (ObserveChallengeResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof PasResponse) {
			PasResponse instance = (PasResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof PassResponse) {
			PassResponse instance = (PassResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof PendingResponse) {
			PendingResponse instance = (PendingResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof PersonalResponse) {
			PersonalResponse instance = (PersonalResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof PingResponse) {
			PingResponse instance = (PingResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof PlayResponse) {
			PlayResponse instance = (PlayResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof PoolResponse) {
			PoolResponse instance = (PoolResponse)response;
			handler.handle(instance);
			return;
		}
		
		if (response instanceof PreMoveResponse) {
			PreMoveResponse instance = (PreMoveResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof RelayResponse) {
			RelayResponse instance = (RelayResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof ReportResponse) {
			ReportResponse instance = (ReportResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof ReserveResponse) {
			ReserveResponse instance = (ReserveResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof ResignResponse) {
			ResignResponse instance = (ResignResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof ResumeResponse) {
			ResumeResponse instance = (ResumeResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof SayResponse) {
			SayResponse instance = (SayResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof SeekResponse) {
			SeekResponse instance = (SeekResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof SetResponse) {
			SetResponse instance = (SetResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof SetAllResponse) {
			SetAllResponse instance = (SetAllResponse)response;
			handler.handle(instance);
			return;
		}
		
		if (response instanceof SetFormulaResponse) {
			SetFormulaResponse instance = (SetFormulaResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof SFilterResponse) {
			SFilterResponse instance = (SFilterResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof ShoutResponse) {
			ShoutResponse instance = (ShoutResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof SoughtResponse) {
			SoughtResponse instance = (SoughtResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof ShuffleResponse) {
			ShuffleResponse instance = (ShuffleResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof ShutdownResponse) {
			ShutdownResponse instance = (ShutdownResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof TellResponse) {
			TellResponse instance = (TellResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof UnexamineResponse) {
			UnexamineResponse instance = (UnexamineResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof UnmatchResponse) {
			UnmatchResponse instance = (UnmatchResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof UnobserveResponse) {
			UnobserveResponse instance = (UnobserveResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof UnrelayedResponse) {
			UnrelayedResponse instance = (UnrelayedResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof UnseekResponse) {
			UnseekResponse instance = (UnseekResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof UptimeResponse) {
			UptimeResponse instance = (UptimeResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof VarsResponse) {
			VarsResponse instance = (VarsResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof WhisperResponse) {
			WhisperResponse instance = (WhisperResponse)response;
			handler.handle(instance);
			return;
		}
		
		if (response instanceof WhoListResponse) {
			WhoListResponse instance = (WhoListResponse)response;
			handler.handle(instance);
			return;
		}

		if (response instanceof WhoResponse) {
			WhoResponse instance = (WhoResponse)response;
			handler.handle(instance);
			return;
		}
	}

}
