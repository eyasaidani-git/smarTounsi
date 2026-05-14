package models;
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
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getIdModule() {
        return idModule;
    }

    public void setIdModule(int idModule) {
        this.idModule = idModule;
    }

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
    }

    @Override
    public String toString() {
        return "Document{" +
                "id=" + id +
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