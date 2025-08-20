package com.hwansol.moviego.seat.model;

import com.hwansol.moviego.config.BaseTImeEntity;
import com.hwansol.moviego.screen.model.Screen;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class Seat extends BaseTImeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String seatRow;

    @Column
    private int seatNum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id", nullable = false)
    private Screen screen;

    @Column
    private LocalDateTime deletedAt;

    @Builder
    public Seat(String seatRow, int seatNum, Screen screen, LocalDateTime deletedAt) {
        boolean isValidateDataFail = seatNum <= 0 || seatRow == null || seatRow.isBlank();

        if (isValidateDataFail) {
            throw new IllegalArgumentException("Seat 엔티티 생성 실패");
        }

        this.seatNum = seatNum;
        this.screen = screen;
        this.deletedAt = deletedAt;
    }

    public void relatedScreen(Screen screen) {
        if (this.screen != null) {
            throw new IllegalStateException("이미 연결된 상태입니다.");
        }

        if (screen == null) {
            throw new IllegalArgumentException("연관관계 연결 실패");
        }

        this.screen = screen;
    }
}
