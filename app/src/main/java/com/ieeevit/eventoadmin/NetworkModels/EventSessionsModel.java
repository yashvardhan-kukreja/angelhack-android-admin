package com.ieeevit.eventoadmin.NetworkModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ieeevit.eventoadmin.Classes.Session;

import java.util.List;

public class EventSessionsModel {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("scannables")
    @Expose
    private List<Session> sessions = null;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public void setSessions(List<Session> sessions) {
        this.sessions = sessions;
    }

}
