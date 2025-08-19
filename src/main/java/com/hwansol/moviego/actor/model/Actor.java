package com.hwansol.moviego.actor.model;

import com.hwansol.moviego.config.BaseTImeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at IS NULL")
public class Actor extends BaseTImeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private LocalDateTime deletedAt;

    @Builder
    public Actor(String name, LocalDateTime deletedAt) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Actor 엔티티 생성 실패");
        }

        this.name = name;
        this.deletedAt = deletedAt;
    }
}
