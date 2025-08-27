package com.sekhanov.flashcard.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"password", "flashcardSets", "lastSeenFlashcardSets"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String name;
    private String surname;

    @Column(nullable = false, unique = true)
    private String login;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Column(nullable = false)
    private Boolean isEmailConfirmed = false;

    @Column(name = "confirmation_token", unique = true)
    private String confirmationToken;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_flashcard_set",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "flashcard_set_id")
    )
    @JsonManagedReference
    @JsonIgnore
    private Set<FlashcardSet> flashcardSets = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<LastSeenFlashcardSet> lastSeenFlashcardSets = new HashSet<>();
}
