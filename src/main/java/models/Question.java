package models;

import enums.QuestionType;

public class Question {

    private int id;
    private int idQuiz;
    private String enonce;
    private QuestionType typeQuestion;
    private int points;
    private int ordre;

    public Question() {
    }

    public Question(int idQuiz, String enonce, QuestionType typeQuestion, int points, int ordre) {
        this.idQuiz = idQuiz;
        this.enonce = enonce;
        this.typeQuestion = typeQuestion;
        this.points = points;
        this.ordre = ordre;
    }

    public Question(int id, int idQuiz, String enonce, QuestionType typeQuestion, int points, int ordre) {
        this.id = id;
        this.idQuiz = idQuiz;
        this.enonce = enonce;
        this.typeQuestion = typeQuestion;
        this.points = points;
        this.ordre = ordre;
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


    public QuestionType getTypeQuestion() {
        return typeQuestion;
    }

    public void setTypeQuestion(QuestionType typeQuestion) {
        this.typeQuestion = typeQuestion;
    }

    public String getType() {
        return typeQuestion == null ? null : typeQuestion.name();
    }

    public void setType(String type) {
        if (type == null) {
            this.typeQuestion = null;
        } else {
            this.typeQuestion = QuestionType.valueOf(type.toUpperCase());
        }
    }


    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }


    public int getOrdre() {
        return ordre;
    }

    public void setOrdre(int ordre) {
        this.ordre = ordre;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", idQuiz=" + idQuiz +
                ", enonce='" + enonce + '\'' +
                ", typeQuestion=" + typeQuestion +
                ", points=" + points +
                ", ordre=" + ordre +
                '}';
    }
}