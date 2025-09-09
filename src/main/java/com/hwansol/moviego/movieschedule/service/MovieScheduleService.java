package com.hwansol.moviego.movieschedule.service;

import com.hwansol.moviego.common.exception.NotFoundException;
import com.hwansol.moviego.image.dto.ImageGetDto;
import com.hwansol.moviego.image.model.Image;
import com.hwansol.moviego.image.service.ImageService;
import com.hwansol.moviego.movieschedule.dto.MovieScheduleGetDto;
import com.hwansol.moviego.movieschedule.model.MovieSchedule;
import com.hwansol.moviego.movieschedule.repository.MovieScheduleRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MovieScheduleService {

    private final MovieScheduleRepository movieScheduleRepository;
    private final ImageService imageService;

    /**
     * 영화 스케줄 상세 조회 서비스
     *
     * @param movieScheduleId 조회할 영화 스케줄 pk
     * @return 조회된 영화 스케줄의 상세 정보를 담고 있는 response dto
     */
    @Transactional(readOnly = true)
    public MovieScheduleGetDto.Response getMovieSchedule(Long movieScheduleId) {
        MovieSchedule movieSchedule = movieScheduleRepository.findById(movieScheduleId)
                .orElseThrow(NotFoundException::new);

        List<Image> movieImages = movieSchedule.getMovie().getImages();
        List<ImageGetDto.Response> movieImageList = new ArrayList<>();

        if (movieImages != null && !movieImages.isEmpty()) {
            movieImageList = imageService.getImageList(movieImages);
        }

        return MovieScheduleGetDto.Response.from(movieSchedule, movieImageList);
    }
}
