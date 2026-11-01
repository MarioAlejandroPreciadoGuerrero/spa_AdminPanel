package com.example.spa_AdminPanel.service;

import com.example.spa_AdminPanel.model.NombreRol;
import com.example.spa_AdminPanel.model.Rol;
import com.example.spa_AdminPanel.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RolService {

    @Autowired
    private RolRepository rolRepository;

    public Rol buscarPorNombre(NombreRol nombre) {
        return rolRepository.findByNombre(nombre).orElse(null);
    }

}
