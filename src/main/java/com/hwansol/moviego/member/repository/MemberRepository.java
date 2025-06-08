package com.hwansol.moviego.member.repository;

import com.hwansol.moviego.member.model.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUserId(String userId);

    Optional<Member> findByUserEmail(String userEmail);

    boolean existsByUserId(String userId);

    boolean existsByUserEmail(String userEmail);
}
