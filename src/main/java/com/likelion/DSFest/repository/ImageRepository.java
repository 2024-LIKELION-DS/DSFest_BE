package com.likelion.DSFest.repository;

import com.likelion.DSFest.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {
    public List<Image> findByNotice_NoticeId(Integer id);
    public void deleteByNotice_NoticeId(Integer id);
}
