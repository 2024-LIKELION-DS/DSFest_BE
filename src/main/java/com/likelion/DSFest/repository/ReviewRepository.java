package com.likelion.DSFest.repository;

import com.likelion.DSFest.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Override
    List<Review> findAll();
}
