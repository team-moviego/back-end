package com.hwansol.moviego.director.service;

import com.hwansol.moviego.common.CommonDto;
import com.hwansol.moviego.common.DuplicatedException;
import com.hwansol.moviego.common.HardDeleteException;
import com.hwansol.moviego.common.NotFoundException;
import com.hwansol.moviego.director.dto.DirectorCreateDto;
import com.hwansol.moviego.director.dto.DirectorSimpleGetDto;
import com.hwansol.moviego.director.dto.DirectorUpdateNameDto;
import com.hwansol.moviego.director.model.Director;
import com.hwansol.moviego.director.repository.DirectorRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorRepository directorRepository;

    /**
     * 감독 리스트 조회 서비스
     *
     * @return 조회된 감독 리스트 dto
     */
    @Transactional(readOnly = true)
    public List<DirectorSimpleGetDto.Response> getDirectorList() {
        List<Director> directorList = directorRepository.findAll();

        return directorList.stream()
                .map(DirectorSimpleGetDto.Response::from)
                .toList();
    }

    /**
     * 감독 생성 서비스
     *
     * @param request 감독 생성에 필요한 이름 필드 정보를 가지고 있는 Request dto 클래스
     * @return 생성된 감독 엔티티 dto
     */
    @Transactional
    public CommonDto.Response createDirector(DirectorCreateDto.Request request) {
        Director director = directorRepository.findByName(request.getName())
                .orElse(null);

        if (director != null) {
            throw new DuplicatedException();
        }

        Director newDirector = request.toEntity();
        Director savedDirector = directorRepository.save(newDirector);

        return CommonDto.Response.from(savedDirector.getId());
    }

    /**
     * 감독명 변경 서비스
     *
     * @param directorId 감독명을 변경할 엔티티 pk
     * @param request    변경할 감독명 정보를 담고 있는 request dto
     * @return 감독명이 변경된 엔티티의 pk 정보를 담고 있는 response dto
     */
    @Transactional
    public CommonDto.Response updateDirectorName(Long directorId,
            DirectorUpdateNameDto.Request request) {
        Director director = directorRepository.findById(directorId)
                .orElseThrow(NotFoundException::new);

        Director existedDirector = directorRepository.findByName(request.getNewName())
                .orElse(null);

        if (existedDirector != null) {
            throw new DuplicatedException();
        }

        director.updateName(request.getNewName());

        return CommonDto.Response.from(director.getId());
    }

    /**
     * 감독 제거 컨트롤러
     *
     * @param directorId 제거할 감독 pk
     * @param request    영구 제거를 위한 문구를 포함하고 있는 request dto
     * @return 제거된 감독 엔티티의 pk를 담은 response dto
     */
    @Transactional
    public CommonDto.Response deleteDirector(Long directorId, CommonDto.DeleteRequest request) {
        Director director = directorRepository.findById(directorId)
                .orElseThrow(NotFoundException::new);

        if (!director.getName().equals(request.getDeleteString())) {
            throw new HardDeleteException();
        }

        directorRepository.delete(director);

        return CommonDto.Response.from(director.getId());
    }
}
