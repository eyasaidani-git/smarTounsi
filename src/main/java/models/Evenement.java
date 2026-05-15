package models;

import enums.EvenementType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Evenement {

    private int idEvenement;
    private String titre;
    private String description;
    private EvenementType typeEvenement;
    private boolean organiseParSite;
    private String lieu;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private BigDecimal tarif;
    private int capacity;
    private String statut;
    private int idCreateur;
    private String imageEvenement;
    private LocalDateTime dateCreation;

    public Evenement() {
        this.typeEvenement = EvenementType.AUTRE;
        this.organiseParSite = true;
        this.tarif = BigDecimal.ZERO;
        this.capacity = 0;
        this.statut = "a_venir";
    }

    public Evenement(String titre, String description, EvenementType typeEvenement, String lieu,
                     LocalDateTime dateDebut, LocalDateTime dateFin, BigDecimal tarif,
                     int idCreateur, String imageEvenement, boolean organiseParSite,
                     String statut, int capacity) {
        this.titre = titre;
        this.description = description;
        this.typeEvenement = typeEvenement == null ? EvenementType.AUTRE : typeEvenement;
        this.lieu = lieu;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.tarif = tarif == null ? BigDecimal.ZERO : tarif;
        this.idCreateur = idCreateur;
        this.imageEvenement = imageEvenement;
        this.organiseParSite = organiseParSite;
        this.statut = statut == null || statut.isBlank() ? "a_venir" : statut;
        this.capacity = capacity;
    }

    public int getId() {
        return idEvenement;
    }

    public void setId(int id) {
        this.idEvenement = id;
    }

    public int getIdEvenement() {
        return idEvenement;
    }

    public void setIdEvenement(int idEvenement) {
        this.idEvenement = idEvenement;
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
        this.typeEvenement = typeEvenement == null ? EvenementType.AUTRE : typeEvenement;
    }

    public boolean isOrganiseParSite() {
        return organiseParSite;
    }

    public void setOrganiseParSite(boolean organiseParSite) {
        this.organiseParSite = organiseParSite;
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
        this.tarif = tarif == null ? BigDecimal.ZERO : tarif;
    }

    // Compatibilité avec l'ancien code DAO qui utilisait prix
    public double getPrix() {
        return tarif == null ? 0.0 : tarif.doubleValue();
    }

    public void setPrix(double prix) {
        this.tarif = BigDecimal.valueOf(prix);
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut == null || statut.isBlank() ? "a_venir" : statut;
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

    // Compatibilité avec l'ancien code DAO qui utilisait image
    public String getImage() {
        return imageEvenement;
    }

    public void setImage(String image) {
        this.imageEvenement = image;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    @Override
    public String toString() {
        return "Evenement{" +
                "idEvenement=" + idEvenement +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", typeEvenement=" + typeEvenement +
                ", organiseParSite=" + organiseParSite +
                ", lieu='" + lieu + '\'' +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", tarif=" + tarif +
                ", capacity=" + capacity +
                ", statut='" + statut + '\'' +
                ", idCreateur=" + idCreateur +
                ", imageEvenement='" + imageEvenement + '\'' +
                ", dateCreation=" + dateCreation +
                '}';
    }
}
