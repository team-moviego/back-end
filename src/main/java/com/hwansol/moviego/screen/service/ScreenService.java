package com.hwansol.moviego.screen.service;

import com.hwansol.moviego.common.NotFoundException;
import com.hwansol.moviego.image.dto.ImageGetDto;
import com.hwansol.moviego.image.model.Image;
import com.hwansol.moviego.image.model.PosterType;
import com.hwansol.moviego.image.service.ImageService;
import com.hwansol.moviego.movieschedule.dto.MovieScheduleGetDto;
import com.hwansol.moviego.screen.dto.ScreenGetDto;
import com.hwansol.moviego.screen.model.Screen;
import com.hwansol.moviego.screen.repository.ScreenRepository;
import com.hwansol.moviego.seat.dto.SeatGetDto;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScreenService {

    private final ScreenRepository screenRepository;
    private final ImageService imageService;

    /**
     * 상영관 상세 조회
     *
     * @param screenId 조회할 상영관 pk
     * @return 조회된 상영관의 응답 dto
     */
    @Transactional(readOnly = true)
    public ScreenGetDto.Response getScreen(Long screenId) {
        Screen screen = screenRepository.findById(screenId)
                .orElseThrow(NotFoundException::new);

        return getScreenGetDtoResponse(screen);
    }

    /**
     * 전체 상영관 리스트 조회
     *
     * @return 조회된 전체 상영관의 응답 dto 리스트
     */
    @Transactional(readOnly = true)
    public List<ScreenGetDto.Response> getScreenList() {
        List<Screen> screenList = screenRepository.findAll();

        return screenList.stream()
                .map(this::getScreenGetDtoResponse)
                .toList();
    }

    // 상영관 조회 응답 dto 생성 메서드
    private ScreenGetDto.Response getScreenGetDtoResponse(Screen screen) {
        List<SeatGetDto.Response> seatList = screen.getSeats() == null ? new ArrayList<>() : screen.getSeats().stream()
                .map(SeatGetDto.Response::from)
                .toList();

        List<MovieScheduleGetDto.Response> movieScheduleResponseList = screen.getMovieSchedules() == null ? new ArrayList<>() :
                screen.getMovieSchedules().stream()
                        .map(ms -> {
                            Image image = ms.getMovie().getImages().stream()
                                    .filter(i -> i.getPosterType().equals(PosterType.MAIN))
                                    .findAny()
                                    .orElse(null);

                            if (image == null) {
                                return MovieScheduleGetDto.Response.from(ms, null);
                            }

                            List<ImageGetDto.Response> imageResponse = imageService.getImageList(List.of(image));

                            return MovieScheduleGetDto.Response.from(ms, imageResponse.get(0));
                        })
                        .toList();

        return ScreenGetDto.Response.from(screen, seatList, movieScheduleResponseList);
    }
}
