package models;

public class Quiz {

    private int id;
    private int idModule;
    private int idCreateur;

    private String titre;
    private String description;

    /*
     * Champs utilisés seulement pour l'affichage dans l'interface Quiz.
     * Ils ne sont pas obligatoires dans la table quiz.
     */
    private String matiere;
    private int nombreQuestions;
    private String type;
    private String icone;

    public Quiz() {
    }

    public Quiz(String titre, String description, int idModule, int idCreateur) {
        this.titre = titre;
        this.description = description;
        this.idModule = idModule;
        this.idCreateur = idCreateur;
    }

    public Quiz(String titre, String matiere, int nombreQuestions, String type, String icone) {
        this.titre = titre;
        this.matiere = matiere;
        this.nombreQuestions = nombreQuestions;
        this.type = type;
        this.icone = icone;
        this.description = matiere + " - " + type;
        this.idModule = 0;
        this.idCreateur = 1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public int getIdModule() {
        return idModule;
    }

    public void setIdModule(int idModule) {
        this.idModule = idModule;
    }


    public int getIdCreateur() {
        return idCreateur;
    }

    public void setIdCreateur(int idCreateur) {
        this.idCreateur = idCreateur;
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


    public String getMatiere() {
        if (matiere == null || matiere.isEmpty()) {
            return "Module " + idModule;
        }
        return matiere;
    }

    public void setMatiere(String matiere) {
        this.matiere = matiere;
    }


    public int getNombreQuestions() {
        return nombreQuestions;
    }

    public void setNombreQuestions(int nombreQuestions) {
        this.nombreQuestions = nombreQuestions;
    }


    public String getType() {
        if (type == null || type.isEmpty()) {
            return "QCM";
        }
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getIcone() {
        if (icone == null || icone.isEmpty()) {
            return "📝";
        }
        return icone;
    }

    public void setIcone(String icone) {
        this.icone = icone;
    }

    @Override
    public String toString() {
        return "Quiz{" +
                "id=" + id +
                ", idModule=" + idModule +
                ", idCreateur=" + idCreateur +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", matiere='" + matiere + '\'' +
                ", nombreQuestions=" + nombreQuestions +
                ", type='" + type + '\'' +
                ", icone='" + icone + '\'' +
                '}';
    }
}