package com.yun.opern.model;

import java.math.BigInteger;

public class FeedbackInfo {
    private BigInteger userId;
    private String feedbackMessage;
    private String feedbackDateTime;
    private String communicateFlg;
    private String delFlg;

    public BigInteger getUserId() {
        return userId;
    }

    public void setUserId(BigInteger userId) {
        this.userId = userId;
    }

    public String getFeedbackMessage() {
        return feedbackMessage;
    }

    public void setFeedbackMessage(String feedbackMessage) {
        this.feedbackMessage = feedbackMessage;
    }

    public String getFeedbackDateTime() {
        return feedbackDateTime;
    }

    public void setFeedbackDateTime(String feedbackDateTime) {
        this.feedbackDateTime = feedbackDateTime;
    }

    public String getCommunicateFlg() {
        return communicateFlg;
    }

    public void setCommunicateFlg(String communicateFlg) {
        this.communicateFlg = communicateFlg;
    }

    public String getDelFlg() {
        return delFlg;
    }

    public void setDelFlg(String delFlg) {
        this.delFlg = delFlg;
    }
}
