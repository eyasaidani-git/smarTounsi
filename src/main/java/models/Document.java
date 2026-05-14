package models;
<<<<<<< HEAD
import enums.DocumentStatut;
import enums.DocumentType;
import java.time.LocalDateTime;
public class Document {
    private int id,idModule,idUploadeur;
    private String titre , description ,fichierUrl;
    private DocumentType typeDocument;
    private DocumentStatut statut;
    private LocalDateTime dateUpload, dateApprobation;
    private Integer idApprobateur;

    public Document() {
    }

    public Document(String titre, String description, DocumentType typeDocument, String fichierUrl, int idModule, int idUploadeur) {
        this.titre = titre;
        this.description = description;
        this.typeDocument = typeDocument;
        this.fichierUrl = fichierUrl;
        this.idModule = idModule;
        this.idUploadeur = idUploadeur;
        this.statut = DocumentStatut.EN_ATTENTE;
=======
import java.time.LocalDateTime;

public class Document {
    private int id, idModule, idUtilisateur;
    private String titre, type, contenu;
    private LocalDateTime dateAjout;
    private boolean approuve;
    public Document() {}

    public Document(String titre, String type, String contenu, int idModule, int idUtilisateur) {
        this.titre = titre;
        this.type = type;
        this.contenu = contenu;
        this.idModule = idModule;
        this.idUtilisateur = idUtilisateur;
        this.approuve = false;

>>>>>>> origin/gestionikram
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

<<<<<<< HEAD
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

    public DocumentType getTypeDocument() {
        return typeDocument;
    }

    public void setTypeDocument(DocumentType typeDocument) {
        this.typeDocument = typeDocument;
    }

    public String getFichierUrl() {
        return fichierUrl;
    }

    public void setFichierUrl(String fichierUrl) {
        this.fichierUrl = fichierUrl;
    }

=======
>>>>>>> origin/gestionikram
    public int getIdModule() {
        return idModule;
    }

    public void setIdModule(int idModule) {
        this.idModule = idModule;
    }

<<<<<<< HEAD
    public int getIdUploadeur() {
        return idUploadeur;
    }

    public void setIdUploadeur(int idUploadeur) {
        this.idUploadeur = idUploadeur;
    }

    public DocumentStatut getStatut() {
        return statut;
    }

    public void setStatut(DocumentStatut statut) {
        this.statut = statut;
    }

    public LocalDateTime getDateUpload() {
        return dateUpload;
    }

    public void setDateUpload(LocalDateTime dateUpload) {
        this.dateUpload = dateUpload;
    }

    public LocalDateTime getDateApprobation() {
        return dateApprobation;
    }

    public void setDateApprobation(LocalDateTime dateApprobation) {
        this.dateApprobation = dateApprobation;
    }

    public Integer getIdApprobateur() {
        return idApprobateur;
    }

    public void setIdApprobateur(Integer idApprobateur) {
        this.idApprobateur = idApprobateur;
=======
    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public LocalDateTime getDateAjout() {
        return dateAjout;
    }

    public void setDateAjout(LocalDateTime dateAjout) {
        this.dateAjout = dateAjout;
    }

    public boolean isApprouve() {
        return approuve;
    }

    public void setApprouve(boolean approuve) {
        this.approuve = approuve;
>>>>>>> origin/gestionikram
    }

    @Override
    public String toString() {
        return "Document{" +
                "id=" + id +
<<<<<<< HEAD
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", typeDocument=" + typeDocument +
                ", fichierUrl='" + fichierUrl + '\'' +
                ", idModule=" + idModule +
                ", idUploadeur=" + idUploadeur +
                ", statut=" + statut +
                ", dateUpload=" + dateUpload +
                ", dateApprobation=" + dateApprobation +
                ", idApprobateur=" + idApprobateur +
                '}';
    }
}
=======
                ", idModule=" + idModule +
                ", idUtilisateur=" + idUtilisateur +
                ", titre='" + titre + '\'' +
                ", type='" + type + '\'' +
                ", contenu='" + contenu + '\'' +
                ", dateAjout=" + dateAjout +
                ", approuve=" + approuve +
                '}';
    }
}
>>>>>>> origin/gestionikram
