package models;
<<<<<<< HEAD

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
                     int idCreateur, String imageEvenement) {
        this.titre = titre;
        this.description = description;
        this.typeEvenement = typeEvenement;
        this.lieu = lieu;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.tarif = tarif;
        this.idCreateur = idCreateur;
        this.imageEvenement = imageEvenement;
        this.organiseParSite = true;
        this.statut = "a_venir";
        this.capacity = 0;
    }

    public Evenement(String titre, String description, EvenementType typeEvenement, String lieu,
                     LocalDateTime dateDebut, LocalDateTime dateFin, BigDecimal tarif,
                     int idCreateur, String imageEvenement, boolean organiseParSite,
                     String statut, int capacity) {
        this.titre = titre;
        this.description = description;
        this.typeEvenement = typeEvenement;
        this.lieu = lieu;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.tarif = tarif;
        this.idCreateur = idCreateur;
        this.imageEvenement = imageEvenement;
        this.organiseParSite = organiseParSite;
        this.statut = statut;
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
=======
import java.time.LocalDate;
import java.time.LocalTime;
public class Evenement {
    private int id, idOrganisateur;
    private String nom, description, type, emplacement;
    private float tarif;
    private LocalDate dateEvenement;
    private LocalTime heureEvenement;

    public Evenement() {}
    public Evenement(String nom, String description, String type,
                     float tarif, String emplacement,
                     LocalDate dateEvenement, LocalTime heureEvenement,
                     int idOrganisateur) {
        this.nom = nom;
        this.description = description;
        this.type = type;
        this.tarif = tarif;
        this.emplacement = emplacement;
        this.dateEvenement = dateEvenement;
        this.heureEvenement = heureEvenement;
        this.idOrganisateur = idOrganisateur;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdOrganisateur() {
        return idOrganisateur;
    }

    public void setIdOrganisateur(int idOrganisateur) {
        this.idOrganisateur = idOrganisateur;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
>>>>>>> origin/gestionikram
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

<<<<<<< HEAD
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
=======
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmplacement() {
        return emplacement;
    }

    public void setEmplacement(String emplacement) {
        this.emplacement = emplacement;
    }

    public float getTarif() {
        return tarif;
    }

    public void setTarif(float tarif) {
        this.tarif = tarif;
    }

    public LocalDate getDateEvenement() {
        return dateEvenement;
    }

    public void setDateEvenement(LocalDate dateEvenement) {
        this.dateEvenement = dateEvenement;
    }

    public LocalTime getHeureEvenement() {
        return heureEvenement;
    }

    public void setHeureEvenement(LocalTime heureEvenement) {
        this.heureEvenement = heureEvenement;
>>>>>>> origin/gestionikram
    }

    @Override
    public String toString() {
        return "Evenement{" +
<<<<<<< HEAD
                "idEvenement=" + idEvenement +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", typeEvenement=" + typeEvenement +
                ", lieu='" + lieu + '\'' +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", tarif=" + tarif +
                ", idCreateur=" + idCreateur +
                ", imageEvenement='" + imageEvenement + '\'' +
                ", dateCreation=" + dateCreation +
                ", organiseParSite=" + organiseParSite +
                ", statut='" + statut + '\'' +
                ", capacity=" + capacity +
                '}';
    }
}
=======
                "id=" + id +
                ", idOrganisateur=" + idOrganisateur +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", emplacement='" + emplacement + '\'' +
                ", tarif=" + tarif +
                ", dateEvenement=" + dateEvenement +
                ", heureEvenement=" + heureEvenement +
                '}';
    }
}
>>>>>>> origin/gestionikram
