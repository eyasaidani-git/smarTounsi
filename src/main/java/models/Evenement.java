package models;
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
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

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
    }

    @Override
    public String toString() {
        return "Evenement{" +
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
