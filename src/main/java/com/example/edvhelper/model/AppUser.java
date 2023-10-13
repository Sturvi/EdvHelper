package com.example.edvhelper.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "telegram_chat_id", name = "telegram_chat_id_unique")
})
@Setter
@Getter
@NoArgsConstructor
@SuperBuilder
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "telegram_chat_id")
    private Long telegramChatId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "username")
    private String username;

    @Column(name = "user_status")
    private boolean userStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_state", columnDefinition = "varchar(255) default 'MAIN'")
    private AppUserStateEnum userState;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", columnDefinition = "varchar(255) default 'USER'")
    private AppUserRoleEnum role;
}
