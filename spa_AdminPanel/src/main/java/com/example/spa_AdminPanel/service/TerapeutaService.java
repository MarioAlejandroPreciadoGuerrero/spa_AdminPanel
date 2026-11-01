package com.example.spa_AdminPanel.service;

import com.example.spa_AdminPanel.model.Servicio;
import com.example.spa_AdminPanel.model.Terapeuta;
import com.example.spa_AdminPanel.repository.SucursalRepository;
import com.example.spa_AdminPanel.repository.ServicioRepository;
import com.example.spa_AdminPanel.repository.TerapeutaRepository;
import com.example.spa_AdminPanel.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TerapeutaService {

    private final TerapeutaRepository terapeutaRepository;

    private final UsuarioRepository usuarioRepository;

    private final ServicioRepository servicioRepository;

    private final SucursalRepository sucursalRepository;

    public TerapeutaService(TerapeutaRepository terapeutaRepository, UsuarioRepository usuarioRepository,
                            ServicioRepository servicioRepository, SucursalRepository sucursalRepository) {
        this.terapeutaRepository = terapeutaRepository;
        this.usuarioRepository = usuarioRepository;
        this.servicioRepository = servicioRepository;
        this.sucursalRepository = sucursalRepository;
    }

    @Transactional(readOnly = true)
    public List<Terapeuta> obtenerTerapeutas() {
        return terapeutaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Terapeuta obtenerTerapeutaPorId(Long idTerapeuta) {
        return terapeutaRepository.findById(idTerapeuta)
                .orElseThrow(() -> new EntityNotFoundException("El terapeuta no existe"));
    }

    @Transactional(readOnly = true)
    public List<Terapeuta> obtenerTerapeutasPorServicio(String nombreServicio) {
        if (nombreServicio == null || nombreServicio.isBlank()) {
            throw new IllegalArgumentException("El nombre del servicio es obligatorio");
        }
        return terapeutaRepository.findDistinctByServicios_NombreIgnoreCase(nombreServicio.trim());
    }

    @Transactional
    public Terapeuta guardarTerapeuta(Terapeuta terapeuta) {
        validarTerapeuta(terapeuta);
        terapeuta.setUsuario(usuarioRepository.findById(terapeuta.getUsuario().getId_usuario())
                .orElseThrow(() -> new EntityNotFoundException("El usuario del terapeuta no existe")));
        if (terapeuta.getSucursal() != null) {
            if (terapeuta.getSucursal().getId_sucursal() == null) {
                throw new IllegalArgumentException("La sucursal debe tener un identificador");
            }
            terapeuta.setSucursal(sucursalRepository.findById(terapeuta.getSucursal().getId_sucursal())
                    .orElseThrow(() -> new EntityNotFoundException("La sucursal no existe")));
        }
        terapeuta.setServicios(cargarServicios(terapeuta.getServicios()));
        return terapeutaRepository.save(terapeuta);
    }

    @Transactional
    public Terapeuta actualizarTerapeuta(Long idTerapeuta, Terapeuta cambios) {
        if (cambios == null) {
            throw new IllegalArgumentException("Los datos del terapeuta son obligatorios");
        }
        Terapeuta terapeuta = obtenerTerapeutaPorId(idTerapeuta);
        if (cambios.getSucursal() != null) {
            if (cambios.getSucursal().getId_sucursal() == null) {
                throw new IllegalArgumentException("La sucursal debe tener un identificador");
            }
            terapeuta.setSucursal(sucursalRepository.findById(cambios.getSucursal().getId_sucursal())
                    .orElseThrow(() -> new EntityNotFoundException("La sucursal no existe")));
        }
        if (cambios.getFecha_contratacion() != null) {
            terapeuta.setFecha_contratacion(cambios.getFecha_contratacion());
        }
        if (cambios.getServicios() != null) {
            terapeuta.setServicios(cargarServicios(cambios.getServicios()));
        }
        return terapeutaRepository.save(terapeuta);
    }

    private void validarTerapeuta(Terapeuta terapeuta) {
        if (terapeuta == null || terapeuta.getUsuario() == null
                || terapeuta.getUsuario().getId_usuario() == null) {
            throw new IllegalArgumentException("El terapeuta debe estar asociado a un usuario existente");
        }
    }

    private List<Servicio> cargarServicios(List<Servicio> servicios) {
        if (servicios == null) {
            return List.of();
        }
        return servicios.stream()
                .map(servicio -> {
                    if (servicio == null || servicio.getId_servicio() == null) {
                        throw new IllegalArgumentException("Cada servicio asignado debe tener un identificador");
                    }
                    return servicioRepository.findById(servicio.getId_servicio())
                            .orElseThrow(() -> new EntityNotFoundException("Uno de los servicios no existe"));
                })
                .distinct()
                .toList();
    }
}
