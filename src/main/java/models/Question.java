package models;

public class Question {
    private int id, idQuiz;
    private String enonce, type;

    public Question() {}
    public Question(String enonce, String type, int idQuiz) {
        this.enonce = enonce;
        this.type = type;
        this.idQuiz = idQuiz;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdQuiz() {
        return idQuiz;
    }

    public void setIdQuiz(int idQuiz) {
        this.idQuiz = idQuiz;
    }

    public String getEnonce() {
        return enonce;
    }

    public void setEnonce(String enonce) {
        this.enonce = enonce;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
