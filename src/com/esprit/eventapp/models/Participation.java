package com.esprit.eventapp.models;

import java.sql.Timestamp;

public class Participation {
    private int idParticipation;
    private int idUser;
    private int idEvenement;
    private Timestamp dateParticipation;
    private String statut; // confirmée, waitlist, présent, absence, annulée
    private Timestamp checkinTime;

    // Useful for display
    private String userName;
    private String eventTitle;

    public Participation() {}

    public Participation(int idUser, int idEvenement) {
        this.idUser = idUser;
        this.idEvenement = idEvenement;
        this.statut = "confirmée";
    }

    public Participation(int idUser, int idEvenement, String statut) {
        this.idUser = idUser;
        this.idEvenement = idEvenement;
        this.statut = statut;
    }

    public Participation(int idParticipation, int idUser, int idEvenement, Timestamp dateParticipation, String statut) {
        this.idParticipation = idParticipation;
        this.idUser = idUser;
        this.idEvenement = idEvenement;
        this.dateParticipation = dateParticipation;
        this.statut = statut;
    }

    public int getIdParticipation() { return idParticipation; }
    public void setIdParticipation(int idParticipation) { this.idParticipation = idParticipation; }

    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }

    public int getIdEvenement() { return idEvenement; }
    public void setIdEvenement(int idEvenement) { this.idEvenement = idEvenement; }

    public Timestamp getDateParticipation() { return dateParticipation; }
    public void setDateParticipation(Timestamp dateParticipation) { this.dateParticipation = dateParticipation; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public Timestamp getCheckinTime() { return checkinTime; }
    public void setCheckinTime(Timestamp checkinTime) { this.checkinTime = checkinTime; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getEventTitle() { return eventTitle; }
    public void setEventTitle(String eventTitle) { this.eventTitle = eventTitle; }
}
