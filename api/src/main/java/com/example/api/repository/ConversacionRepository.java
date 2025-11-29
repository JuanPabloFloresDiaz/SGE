package com.example.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.api.model.Conversacion;

@Repository
public interface ConversacionRepository extends JpaRepository<Conversacion, String> {
}
