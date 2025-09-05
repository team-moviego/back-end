package com.hwansol.moviego.director.service;

import com.hwansol.moviego.director.dto.DirectorSimpleGetDto;
import com.hwansol.moviego.director.model.Director;
import com.hwansol.moviego.director.repository.DirectorRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorRepository directorRepository;

    /**
     * 감독 리스트 조회 서비스
     *
     * @return 조회된 감독 리스트 dto
     */
    public List<DirectorSimpleGetDto.Response> getDirectorList() {
        List<Director> directorList = directorRepository.findAll();

        return directorList.stream()
                .map(DirectorSimpleGetDto.Response::from)
                .toList();
    }
}
