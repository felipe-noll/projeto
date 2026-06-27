package com.example.demo.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import com.example.demo.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByLogin(String login);
}