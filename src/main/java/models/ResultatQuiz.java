package models;
import java.time.LocalDateTime;

public class ResultatQuiz {
    private int id,idQuiz,idUtilisateur,scoreObtenu;
    private LocalDateTime datePassage;
    private Integer tempsPasse;

    public ResultatQuiz() {
    }
    public ResultatQuiz(int idQuiz, int idUtilisateur, int scoreObtenu, Integer tempsPasse) {
        this.idQuiz = idQuiz;
        this.idUtilisateur = idUtilisateur;
        this.scoreObtenu = scoreObtenu;
        this.tempsPasse = tempsPasse;
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

    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public int getScoreObtenu() {
        return scoreObtenu;
    }

    public void setScoreObtenu(int scoreObtenu) {
        this.scoreObtenu = scoreObtenu;
    }

    public LocalDateTime getDatePassage() {
        return datePassage;
    }

    public void setDatePassage(LocalDateTime datePassage) {
        this.datePassage = datePassage;
    }

    public Integer getTempsPasse() {
        return tempsPasse;
    }

    public void setTempsPasse(Integer tempsPasse) {
        this.tempsPasse = tempsPasse;
    }
<<<<<<< HEAD
}
=======
}
>>>>>>> origin/GestionNour
