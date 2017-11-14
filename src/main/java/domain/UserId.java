package domain;

import java.util.Objects;

/**
 * Identifiziert einen Benutzer
 */
public class UserId {
    private final String id;

    public UserId(Integer id) {
        this.id = String.valueOf(Objects.requireNonNull(id));
    }

    public UserId(String id) {
        this.id = Objects.requireNonNull(id);
    }

    public String asString() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserId userId = (UserId) o;

        return id.equals(userId.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
