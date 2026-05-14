package models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ParticipationEvenement {

    private int id;
    private int idUtilisateur;
    private int idEvenement;

    private String typeParticipation;

    private String statut;

    private BigDecimal montantPaye;
    private String modePaiement;
    private String referencePaiement;
    private LocalDateTime datePaiement;

    private LocalDateTime dateParticipation;
    private LocalDateTime checkinTime;

    private String nomUtilisateur;
    private String titreEvenement;

    public ParticipationEvenement() {
    }

    public ParticipationEvenement(int idUtilisateur, int idEvenement, String typeParticipation, String statut) {
        this.idUtilisateur = idUtilisateur;
        this.idEvenement = idEvenement;
        this.typeParticipation = typeParticipation;
        this.statut = statut;
        this.montantPaye = BigDecimal.ZERO;
    }

    public ParticipationEvenement(int idUtilisateur, int idEvenement, String typeParticipation,
                                  String statut, BigDecimal montantPaye) {
        this.idUtilisateur = idUtilisateur;
        this.idEvenement = idEvenement;
        this.typeParticipation = typeParticipation;
        this.statut = statut;
        this.montantPaye = montantPaye;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }


    public int getIdEvenement() {
        return idEvenement;
    }

    public void setIdEvenement(int idEvenement) {
        this.idEvenement = idEvenement;
    }


    public String getTypeParticipation() {
        return typeParticipation;
    }

    public void setTypeParticipation(String typeParticipation) {
        this.typeParticipation = typeParticipation;
    }


    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }


    public BigDecimal getMontantPaye() {
        return montantPaye;
    }

    public void setMontantPaye(BigDecimal montantPaye) {
        this.montantPaye = montantPaye;
    }


    public String getModePaiement() {
        return modePaiement;
    }

    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
    }


    public String getReferencePaiement() {
        return referencePaiement;
    }

    public void setReferencePaiement(String referencePaiement) {
        this.referencePaiement = referencePaiement;
    }


    public LocalDateTime getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(LocalDateTime datePaiement) {
        this.datePaiement = datePaiement;
    }


    public LocalDateTime getDateParticipation() {
        return dateParticipation;
    }

    public void setDateParticipation(LocalDateTime dateParticipation) {
        this.dateParticipation = dateParticipation;
    }


    public LocalDateTime getCheckinTime() {
        return checkinTime;
    }

    public void setCheckinTime(LocalDateTime checkinTime) {
        this.checkinTime = checkinTime;
    }


    public String getNomUtilisateur() {
        return nomUtilisateur;
    }

    public void setNomUtilisateur(String nomUtilisateur) {
        this.nomUtilisateur = nomUtilisateur;
    }


    public String getTitreEvenement() {
        return titreEvenement;
    }

    public void setTitreEvenement(String titreEvenement) {
        this.titreEvenement = titreEvenement;
    }

    @Override
    public String toString() {
        return "ParticipationEvenement{" +
                "id=" + id +
                ", idUtilisateur=" + idUtilisateur +
                ", idEvenement=" + idEvenement +
                ", typeParticipation='" + typeParticipation + '\'' +
                ", statut='" + statut + '\'' +
                ", montantPaye=" + montantPaye +
                ", modePaiement='" + modePaiement + '\'' +
                ", referencePaiement='" + referencePaiement + '\'' +
                ", datePaiement=" + datePaiement +
                ", dateParticipation=" + dateParticipation +
                ", checkinTime=" + checkinTime +
                ", nomUtilisateur='" + nomUtilisateur + '\'' +
                ", titreEvenement='" + titreEvenement + '\'' +
                '}';
    }
}

