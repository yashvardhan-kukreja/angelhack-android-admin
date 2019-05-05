package com.ieeevit.eventoadmin.Classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Session {

    @SerializedName("scannableType")
    @Expose
    private String scannableType;
    @SerializedName("participantsPresent")
    @Expose
    private List<String> participantsPresent = null;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("eventId")
    @Expose
    private String eventId;

    public String getScannableType() {
        return scannableType;
    }

    public void setScannableType(String scannableType) {
        this.scannableType = scannableType;
    }

    public List<String> getParticipantsPresent() {
        return participantsPresent;
    }

    public void setParticipantsPresent(List<String> participantsPresent) {
        this.participantsPresent = participantsPresent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
