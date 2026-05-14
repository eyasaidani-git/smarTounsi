package models;
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
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

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