package models;

public class Reponse {

    private int id;
    private int idQuestion;
    private String texteReponse;
    private boolean estCorrecte;

    public Reponse() {
    }

    public Reponse(int idQuestion, String texteReponse, boolean estCorrecte) {
        this.idQuestion = idQuestion;
        this.texteReponse = texteReponse;
        this.estCorrecte = estCorrecte;
    }

    public Reponse(int id, int idQuestion, String texteReponse, boolean estCorrecte) {
        this.id = id;
        this.idQuestion = idQuestion;
        this.texteReponse = texteReponse;
        this.estCorrecte = estCorrecte;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public int getIdQuestion() {
        return idQuestion;
    }

    public void setIdQuestion(int idQuestion) {
        this.idQuestion = idQuestion;
    }


    public String getTexteReponse() {
        return texteReponse;
    }

    public void setTexteReponse(String texteReponse) {
        this.texteReponse = texteReponse;
    }

    public String getContenu() {
        return texteReponse;
    }

    public void setContenu(String contenu) {
        this.texteReponse = contenu;
    }


    public boolean isEstCorrecte() {
        return estCorrecte;
    }

    public void setEstCorrecte(boolean estCorrecte) {
        this.estCorrecte = estCorrecte;
    }

    public boolean isCorrecte() {
        return estCorrecte;
    }

    public void setCorrecte(boolean correcte) {
        this.estCorrecte = correcte;
<<<<<<< HEAD
    }

    @Override
    public String toString() {
        return "Reponse{" +
                "id=" + id +
                ", idQuestion=" + idQuestion +
                ", texteReponse='" + texteReponse + '\'' +
                ", estCorrecte=" + estCorrecte +
                '}';
=======
>>>>>>> origin/gestionmohamed
    }

    @Override
    public String toString() {
        return "Reponse{" +
                "id=" + id +
                ", idQuestion=" + idQuestion +
                ", texteReponse='" + texteReponse + '\'' +
                ", estCorrecte=" + estCorrecte +
                '}';
    }
}