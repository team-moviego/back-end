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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Builder
    public Seat(String seatRow, int seatNum, Screen screen) {
        boolean isValidateDataFail = seatNum <= 0 || seatRow == null || seatRow.isBlank();

        if (isValidateDataFail) {
            throw new IllegalArgumentException("Seat 엔티티 생성 실패");
        }

        this.seatRow = seatRow;
        this.seatNum = seatNum;
        this.screen = screen;
    }

    // 테스트용
    public void withId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("pk는 null, 0 또는 음수일 수 없습니다.");
        }

        if (this.id != null) {
            throw new IllegalStateException("이미 pk값이 지정되어 있습니다.");
        }

        this.id = id;
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
