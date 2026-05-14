package models;

import enums.EvenementType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Evenement {
    private int idEvenement;
    private String titre;
    private String description;
    private EvenementType typeEvenement;
    private String lieu;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private BigDecimal tarif;
    private int idCreateur;
    private String imageEvenement;
    private LocalDateTime dateCreation;
    private boolean organiseParSite;
    private String statut;
    private int capacity;
    public Evenement() {
    }

    public Evenement(String titre, String description, EvenementType typeEvenement, String lieu,
                     LocalDateTime dateDebut, LocalDateTime dateFin, BigDecimal tarif,
                     int idCreateur, String imageEvenement,String statut,int capacity) {
        this.titre = titre;
        this.description = description;
        this.typeEvenement = typeEvenement;
        this.lieu = lieu;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.tarif = tarif;
        this.idCreateur = idCreateur;
        this.imageEvenement = imageEvenement;
        this.statut=statut;
        this.capacity=capacity;
    }

    public int getId() {
        return idEvenement;
    }

    public void setId(int id) {
        this.idEvenement = id;
    }


    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public EvenementType getTypeEvenement() {
        return typeEvenement;
    }

    public void setTypeEvenement(EvenementType typeEvenement) {
        this.typeEvenement = typeEvenement;
    }


    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }


    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }


    public LocalDateTime getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
    }


    public BigDecimal getTarif() {
        return tarif;
    }

    public void setTarif(BigDecimal tarif) {
        this.tarif = tarif;
    }


    public int getIdCreateur() {
        return idCreateur;
    }

    public void setIdCreateur(int idCreateur) {
        this.idCreateur = idCreateur;
    }


    public String getImageEvenement() {
        return imageEvenement;
    }

    public void setImageEvenement(String imageEvenement) {
        this.imageEvenement = imageEvenement;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public boolean isOrganiseParSite() {
        return organiseParSite;
    }

    public void setOrganiseParSite(boolean organiseParSite) {
        this.organiseParSite = organiseParSite;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return "Evenement{" +
                "id=" + idEvenement +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", typeEvenement=" + typeEvenement +
                ", lieu='" + lieu + '\'' +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", tarif=" + tarif +
                ", idCreateur=" + idCreateur +
                ", imageEvenement='" + imageEvenement + '\'' +
                ", dateCreation=" + dateCreation + ", status=" + statut +
                ",capacité=" + capacity + '}';
    }
}