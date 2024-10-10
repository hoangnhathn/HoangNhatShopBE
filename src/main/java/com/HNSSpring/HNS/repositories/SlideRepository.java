package com.HNSSpring.HNS.repositories;

import com.HNSSpring.HNS.models.Slide;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SlideRepository extends JpaRepository<Slide, Integer> {
    Page<Slide> findAll(Pageable pageable);
}
