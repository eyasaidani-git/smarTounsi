package models;

public class Reponse {
    private int id , idQuestion;
    private String contenu;
    private boolean estCorrecte;
    public Reponse() {
    }
    public Reponse(String contenu, boolean estCorrecte, int idQuestion) {
        this.contenu = contenu;
        this.estCorrecte = estCorrecte;
        this.idQuestion = idQuestion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public boolean isEstCorrecte() {
        return estCorrecte;
    }

    public void setEstCorrecte(boolean estCorrecte) {
        this.estCorrecte = estCorrecte;
    }

    public int getIdQuestion() {
        return idQuestion;
    }

    public void setIdQuestion(int idQuestion) {
        this.idQuestion = idQuestion;
    }
}
