package com.hwansol.moviego.screen.service;

import com.hwansol.moviego.common.dto.CommonDto;
import com.hwansol.moviego.common.exception.DuplicatedException;
import com.hwansol.moviego.common.exception.HardDeleteException;
import com.hwansol.moviego.common.exception.NotFoundException;
import com.hwansol.moviego.image.dto.ImageGetDto;
import com.hwansol.moviego.image.model.Image;
import com.hwansol.moviego.image.service.ImageService;
import com.hwansol.moviego.movieschedule.dto.MovieScheduleSimpleGetDto;
import com.hwansol.moviego.screen.dto.ScreenCreateDto;
import com.hwansol.moviego.screen.dto.ScreenGetDto;
import com.hwansol.moviego.screen.model.Screen;
import com.hwansol.moviego.screen.repository.ScreenRepository;
import com.hwansol.moviego.seat.dto.SeatCreateDto;
import com.hwansol.moviego.seat.dto.SeatSimpleGetDto;
import java.util.ArrayList;
import java.util.Comparator;
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

    /**
     * 상영관 생성 서비스
     *
     * @param request ScreenCreateDto.Request
     * @return 생성된 상영관 엔티티
     */
    @Transactional
    public Screen createScreen(ScreenCreateDto.Request request) {
        Screen screen = screenRepository.findByName(request.getName())
                .orElse(null);

        if (screen != null) {
            throw new DuplicatedException();
        }

        Screen newScreen = request.toEntity();

        List<SeatCreateDto.Request> seatCreateRequestDtoList = request.getSeatCreateDtoList();

        seatCreateRequestDtoList.stream()
                .map(SeatCreateDto.Request::toEntity)
                .forEach(newScreen::addSeat);

        return screenRepository.save(newScreen);
    }

    /**
     * 상영관 삭제 (하드딜리트)
     *
     * @param screenId 삭제할 상영관 pk
     * @param request  ScreenDeleteDto.Request
     * @return 삭제된 상영관 엔티티
     */
    @Transactional
    public Screen deleteScreen(Long screenId, CommonDto.DeleteRequest request) {
        Screen screen = screenRepository.findById(screenId)
                .orElseThrow(NotFoundException::new);

        if (!request.getDeleteString().equals(screen.getName())) {
            throw new HardDeleteException();
        }

        screenRepository.delete(screen);

        return screen;
    }

    // 상영관 조회 응답 dto 생성 메서드
    private ScreenGetDto.Response getScreenGetDtoResponse(Screen screen) {
        List<SeatSimpleGetDto.Response> seatList =
                screen.getSeats() == null ? new ArrayList<>() : screen.getSeats().stream()
                        .map(SeatSimpleGetDto.Response::from)
                        .sorted(Comparator.comparing(SeatSimpleGetDto.Response::getSeatRow)
                                        .thenComparing(SeatSimpleGetDto.Response::getSeatNum))
                        .toList();

        List<MovieScheduleSimpleGetDto.Response> movieScheduleResponseList =
                screen.getMovieSchedules() == null ? new ArrayList<>() :
                screen.getMovieSchedules().stream()
                        .map(ms -> {
                            List<Image> imageList = ms.getMovie().getImages();
                            List<ImageGetDto.Response> imageResponse = imageService.getImageList(
                                    imageList);

                            return MovieScheduleSimpleGetDto.Response.from(ms);
                        })
                        .sorted(Comparator.comparing(
                                MovieScheduleSimpleGetDto.Response::getStartDateTime))
                        .toList();

        return ScreenGetDto.Response.from(screen, seatList, movieScheduleResponseList);
    }
}
