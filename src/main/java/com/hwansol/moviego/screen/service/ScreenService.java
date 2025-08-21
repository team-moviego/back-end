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
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScreenService {

    private final ScreenRepository screenRepository;
    private final ImageService imageService;

    @Transactional(readOnly = true)
    public ScreenGetDto.Response getScreen(Long screenId) {
        Screen screen = screenRepository.findById(screenId)
                .orElseThrow(NotFoundException::new);

        List<MovieScheduleGetDto.Response> movieScheduleResponseList = screen.getMovieSchedules().stream()
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

        return ScreenGetDto.Response.from(screen, movieScheduleResponseList);
    }
}
