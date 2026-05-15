package util;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import models.Utilisateur;

public final class UiRoleUtil {

    private UiRoleUtil() {
    }

    public static void applySessionRoleLabels(Parent root) {
        if (root == null) {
            return;
        }

        Utilisateur user = Session.getCurrentUser();
        if (user == null) {
            return;
        }

        applyRoleLabels(root, getRoleDisplayText(user.getRole()));
    }

    public static String getRoleDisplayText(String role) {
        if (role == null) {
            return "Etudiant";
        }

        String normalized = role.trim().toLowerCase();
        if ("admin".equals(normalized)) {
            return "Administrateur";
        }
        if ("prof".equals(normalized) || "professeur".equals(normalized)) {
            return "Professeur";
        }
        return "Etudiant";
    }

    private static void applyRoleLabels(Node node, String roleText) {
        if (node instanceof Label label && isRoleBadge(label)) {
            label.setText(roleText);
        }

        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                applyRoleLabels(child, roleText);
            }
        }
    }

    private static boolean isRoleBadge(Label label) {
        if (label.getStyleClass().contains("student-pill")
                || label.getStyleClass().contains("student-text")) {
            return true;
        }

        String text = label.getText() == null ? "" : label.getText().toLowerCase();
        String style = label.getStyle() == null ? "" : label.getStyle().toLowerCase();
        return text.contains("tudiant") && style.contains("background-color");
    }
}
