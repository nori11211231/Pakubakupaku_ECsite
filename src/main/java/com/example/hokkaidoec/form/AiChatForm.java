package com.example.hokkaidoec.form;

public class AiChatForm {

	// ユーザーが入力した質問文
	private String userMessage;

	// AIコンシェルジュの返答文
	private String aiReply;

	public String getUserMessage() {
		return userMessage;
	}

	public void setUserMessage(String userMessage) {
		this.userMessage = userMessage;
	}

	public String getAiReply() {
		return aiReply;
	}

	public void setAiReply(String aiReply) {
		this.aiReply = aiReply;
	}
}
