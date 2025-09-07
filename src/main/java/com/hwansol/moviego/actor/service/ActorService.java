package com.hwansol.moviego.actor.service;

import com.hwansol.moviego.actor.dto.ActorSimpleGetDto;
import com.hwansol.moviego.actor.model.Actor;
import com.hwansol.moviego.actor.repository.ActorRepository;
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
     * @return 조회된 전체 배우 리스트의 순수 정보를 담고 있는 response dto
     */
    @Transactional(readOnly = true)
    public List<ActorSimpleGetDto.Response> getActorList() {
        List<Actor> actors = actorRepository.findAll();

        return actors.stream()
                .map(ActorSimpleGetDto.Response::from)
                .toList();
    }
}
