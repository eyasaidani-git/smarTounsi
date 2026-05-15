package enums;

public enum EvenementType {
    FORUM_STAGE,
    FETE,
    HACKATHON,
    CONFERENCE,
    AUTRE;

    public static EvenementType fromDb(String value) {
        if (value == null || value.isBlank()) {
            return AUTRE;
        }

        String normalized = value.trim().toUpperCase();
        try {
            return EvenementType.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            return AUTRE;
        }
    }

    public String toDbValue() {
        return name().toLowerCase();
    }
}
