package com.hwansol.moviego.actor.service;

import com.hwansol.moviego.actor.dto.ActorCreateDto;
import com.hwansol.moviego.actor.dto.ActorSimpleGetDto;
import com.hwansol.moviego.actor.model.Actor;
import com.hwansol.moviego.actor.repository.ActorRepository;
import com.hwansol.moviego.common.CommonDto;
import com.hwansol.moviego.common.DuplicatedException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActorService {

    private final ActorRepository actorRepository;

    /**
     * 배우 전체 리스트 조회 서비스
     *
     * @return 조회된 전체 배우 리스트의 순수 정보를 담고 있는 dto 리스트
     */
    @Transactional(readOnly = true)
    public List<ActorSimpleGetDto.Response> getActorList() {
        List<Actor> actors = actorRepository.findAll();

        return actors.stream()
                .map(ActorSimpleGetDto.Response::from)
                .toList();
    }

    /**
     * 배우 생성 서비스
     *
     * @param request 생성할 배우의 배우명 정보를 가지고 있는 request dto
     * @return 생성된 배우의 pk 정보를 담고 있는 response dto
     */
    @Transactional
    public CommonDto.Response createActor(ActorCreateDto.Request request) {
        Actor actor = actorRepository.findByName(request.getName())
                .orElse(null);

        if (actor != null) {
            throw new DuplicatedException();
        }

        Actor newActor = request.toEntity();
        Actor savedActor = actorRepository.save(newActor);

        return CommonDto.Response.from(savedActor.getId());
    }
}
