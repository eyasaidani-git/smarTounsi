package models;
<<<<<<< HEAD
import enums.TodoStatus;
import java.time.LocalTime;
public class ToDoItem {
    private int id,idPlanning,ordre;
    private Integer idDocument;
    private String lienExterne,fichierUrl,nomDocument,description;
    private LocalTime heure;
    private TodoStatus statut;

    public ToDoItem() {
    }

    public ToDoItem(int idPlanning, Integer idDocument, String lienExterne,
                    String fichierUrl, String nomDocument, String description,
                    LocalTime heure, int ordre, TodoStatus statut) {
        this.idPlanning = idPlanning;
        this.idDocument = idDocument;
        this.lienExterne = lienExterne;
        this.fichierUrl = fichierUrl;
        this.nomDocument = nomDocument;
        this.description = description;
        this.heure = heure;
        this.ordre = ordre;
        this.statut = statut;
=======

public class ToDoItem {
    private int id, idPlanning, idDocument, dureeMinutes;
    private String titre, description, statut, heureDebut;
    public ToDoItem() {
    }

    public ToDoItem(int dureeMinutes, String titre, String description, String statut, String heureDebut) {

        this.dureeMinutes = dureeMinutes;
        this.titre = titre;
        this.description = description;
        this.statut = statut;
        this.heureDebut = heureDebut;
>>>>>>> origin/gestionikram
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdPlanning() {
        return idPlanning;
    }

    public void setIdPlanning(int idPlanning) {
        this.idPlanning = idPlanning;
    }

<<<<<<< HEAD
    public Integer getIdDocument() {
        return idDocument;
    }

    public void setIdDocument(Integer idDocument) {
        this.idDocument = idDocument;
    }

    public String getLienExterne() {
        return lienExterne;
    }

    public void setLienExterne(String lienExterne) {
        this.lienExterne = lienExterne;
    }

    public String getFichierUrl() {
        return fichierUrl;
    }

    public void setFichierUrl(String fichierUrl) {
        this.fichierUrl = fichierUrl;
    }

    public String getNomDocument() {
        return nomDocument;
    }

    public void setNomDocument(String nomDocument) {
        this.nomDocument = nomDocument;
=======
    public int getIdDocument() {
        return idDocument;
    }

    public void setIdDocument(int idDocument) {
        this.idDocument = idDocument;
    }

    public int getDureeMinutes() {
        return dureeMinutes;
    }

    public void setDureeMinutes(int dureeMinutes) {
        this.dureeMinutes = dureeMinutes;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
>>>>>>> origin/gestionikram
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

<<<<<<< HEAD
    public LocalTime getHeure() {
        return heure;
    }

    public void setHeure(LocalTime heure) {
        this.heure = heure;
    }

    public int getOrdre() {
        return ordre;
    }

    public void setOrdre(int ordre) {
        this.ordre = ordre;
    }

    public TodoStatus getStatut() {
        return statut;
    }

    public void setStatut(TodoStatus statut) {
        this.statut = statut;
    }

    @Override
    public String toString() {
        return "ToDoItem{" +
                "id=" + id +
                ", idPlanning=" + idPlanning +
                ", idDocument=" + idDocument +
                ", lienExterne='" + lienExterne + '\'' +
                ", fichierUrl='" + fichierUrl + '\'' +
                ", nomDocument='" + nomDocument + '\'' +
                ", description='" + description + '\'' +
                ", heure=" + heure +
                ", ordre=" + ordre +
                ", statut=" + statut +
                '}';
    }
}
=======
    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getHeureDebut() {
        return heureDebut;
    }

    public void setHeureDebut(String heureDebut) {
        this.heureDebut = heureDebut;
    }

    @Override
    public String toString() {
        return "TodoItem{" +
                "id=" + id +
                ", idPlanning=" + idPlanning +
                ", idDocument=" + idDocument +
                ", dureeMinutes=" + dureeMinutes +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", statut='" + statut + '\'' +
                ", heureDebut='" + heureDebut + '\'' +
                '}';
    }
}
>>>>>>> origin/gestionikram
