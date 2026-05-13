package models;
import enums.EvenementType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
public class Evenement {
    private int id;
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
    }

}
