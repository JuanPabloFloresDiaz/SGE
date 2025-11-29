package com.example.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.api.model.PreguntaIA;

@Repository
public interface PreguntaIARepository extends JpaRepository<PreguntaIA, String> {
}
