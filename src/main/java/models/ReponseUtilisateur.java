package models;

public class ReponseUtilisateur {

    private int id;
    private int idResultat;
    private int idQuestion;
    private Integer idReponse;
    private String texteLibre;
    private Boolean estCorrecte;

    public ReponseUtilisateur() {
    }

    public ReponseUtilisateur(int idResultat, int idQuestion, Integer idReponse,
                              String texteLibre, Boolean estCorrecte) {
        this.idResultat = idResultat;
        this.idQuestion = idQuestion;
        this.idReponse = idReponse;
        this.texteLibre = texteLibre;
        this.estCorrecte = estCorrecte;
    }

    public ReponseUtilisateur(int id, int idResultat, int idQuestion, Integer idReponse,
                              String texteLibre, Boolean estCorrecte) {
        this.id = id;
        this.idResultat = idResultat;
        this.idQuestion = idQuestion;
        this.idReponse = idReponse;
        this.texteLibre = texteLibre;
        this.estCorrecte = estCorrecte;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public int getIdResultat() {
        return idResultat;
    }

    public void setIdResultat(int idResultat) {
        this.idResultat = idResultat;
    }


    public int getIdQuestion() {
        return idQuestion;
    }

    public void setIdQuestion(int idQuestion) {
        this.idQuestion = idQuestion;
    }


    public Integer getIdReponse() {
        return idReponse;
    }

    public void setIdReponse(Integer idReponse) {
        this.idReponse = idReponse;
    }


    public String getTexteLibre() {
        return texteLibre;
    }

    public void setTexteLibre(String texteLibre) {
        this.texteLibre = texteLibre;
    }


    public Boolean getEstCorrecte() {
        return estCorrecte;
    }

    public void setEstCorrecte(Boolean estCorrecte) {
        this.estCorrecte = estCorrecte;
    }

    public Boolean isCorrecte() {
        return estCorrecte;
    }

    public void setCorrecte(Boolean correcte) {
        this.estCorrecte = correcte;
    }

    @Override
    public String toString() {
        return "ReponseUtilisateur{" +
                "id=" + id +
                ", idResultat=" + idResultat +
                ", idQuestion=" + idQuestion +
                ", idReponse=" + idReponse +
                ", texteLibre='" + texteLibre + '\'' +
                ", estCorrecte=" + estCorrecte +
                '}';
    }
}