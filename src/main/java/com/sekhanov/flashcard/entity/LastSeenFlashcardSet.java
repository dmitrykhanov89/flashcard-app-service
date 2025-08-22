package com.sekhanov.flashcard.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Сущность, представляющая информацию о последнем просмотре пользователем набора карточек.
 * Используется для отображения недавно открытых наборов.
 */
@Entity
@Table(name = "last_seen_flashcard_set")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class LastSeenFlashcardSet {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private LastSeenFlashcardSetId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne
    @MapsId("flashcardSetId")
    @JoinColumn(name = "flashcard_set_id", nullable = false)
    @JsonIgnore
    private FlashcardSet flashcardSet;

    @Column(name = "opened_at", nullable = false)
    private LocalDateTime openedAt;

    @PrePersist
    public void prePersist() {
        if (openedAt == null) {
            openedAt = LocalDateTime.now();
        }
    }

    /**
     * Составной ключ для таблицы last_seen_flashcard_set.
     */
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LastSeenFlashcardSetId implements Serializable {
        private Long userId;
        private Long flashcardSetId;
    }
}
