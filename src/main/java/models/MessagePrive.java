package models;
<<<<<<< HEAD

import java.time.LocalDateTime;

public class MessagePrive {
    private int id;
    private int idExpediteur;
    private int idDestinataire;
    private String contenu;
    private LocalDateTime dateEnvoi;
    private boolean estLu;
    private LocalDateTime dateLecture;

    public MessagePrive() {
    }

    public MessagePrive(int idExpediteur, int idDestinataire, String contenu) {
        this.idExpediteur = idExpediteur;
        this.idDestinataire = idDestinataire;
        this.contenu = contenu;
        this.estLu = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public int getIdExpediteur() {
        return idExpediteur;
    }

    public void setIdExpediteur(int idExpediteur) {
        this.idExpediteur = idExpediteur;
    }


    public int getIdDestinataire() {
        return idDestinataire;
    }

    public void setIdDestinataire(int idDestinataire) {
        this.idDestinataire = idDestinataire;
    }


    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }


    public LocalDateTime getDateEnvoi() {
        return dateEnvoi;
    }

    public void setDateEnvoi(LocalDateTime dateEnvoi) {
        this.dateEnvoi = dateEnvoi;
    }


    public boolean isEstLu() {
        return estLu;
    }

    public void setEstLu(boolean estLu) {
        this.estLu = estLu;
    }


    public LocalDateTime getDateLecture() {
        return dateLecture;
    }

    public void setDateLecture(LocalDateTime dateLecture) {
        this.dateLecture = dateLecture;
    }

    @Override
    public String toString() {
        return "MessagePrive{" +
                "id=" + id +
                ", idExpediteur=" + idExpediteur +
                ", idDestinataire=" + idDestinataire +
                ", contenu='" + contenu + '\'' +
                ", dateEnvoi=" + dateEnvoi +
                ", estLu=" + estLu +
                ", dateLecture=" + dateLecture +
                '}';
    }
}
=======
import java.time.LocalDateTime;
public class MessagePrive {
        private int id, idExpediteur, idDestinataire;
        private String contenu;
        private boolean lu;
        private LocalDateTime dateEnvoi;

        public MessagePrive() {
        }

        public MessagePrive(String contenu, int idExpediteur, int idDestinataire) {
            this.contenu = contenu;
            this.idExpediteur = idExpediteur;
            this.idDestinataire = idDestinataire;
            this.lu = false;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getIdExpediteur() {
            return idExpediteur;
        }

        public void setIdExpediteur(int idExpediteur) {
            this.idExpediteur = idExpediteur;
        }

        public int getIdDestinataire() {
            return idDestinataire;
        }

        public void setIdDestinataire(int idDestinataire) {
            this.idDestinataire = idDestinataire;
        }

        public String getContenu() {
            return contenu;
        }

        public void setContenu(String contenu) {
            this.contenu = contenu;
        }

        public boolean isLu() {
            return lu;
        }

        public void setLu(boolean lu) {
            this.lu = lu;
        }

        public LocalDateTime getDateEnvoi() {
            return dateEnvoi;
        }

        public void setDateEnvoi(LocalDateTime dateEnvoi) {
            this.dateEnvoi = dateEnvoi;
        }

    }

>>>>>>> origin/gestionikram
