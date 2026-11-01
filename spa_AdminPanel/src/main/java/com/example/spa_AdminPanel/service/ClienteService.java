package com.example.spa_AdminPanel.service;

import com.example.spa_AdminPanel.model.Cliente;
import com.example.spa_AdminPanel.repository.ClienteRepository;
import com.example.spa_AdminPanel.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    private final UsuarioRepository usuarioRepository;

    public ClienteService(ClienteRepository clienteRepository, UsuarioRepository usuarioRepository) {
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<Cliente> obtenerClientes() {
        return clienteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Cliente obtenerClientePorId(Long idCliente) {
        return clienteRepository.findById(idCliente)
                .orElseThrow(() -> new EntityNotFoundException("El cliente no existe"));
    }

    @Transactional
    public Cliente registrarCliente(Cliente cliente) {
        if (cliente == null || cliente.getUsuario() == null || cliente.getUsuario().getId_usuario() == null) {
            throw new IllegalArgumentException("El cliente debe estar asociado a un usuario existente");
        }
        if (cliente.getFecha_nacimiento() == null) {
            throw new IllegalArgumentException("La fecha de nacimiento del cliente es obligatoria");
        }
        Long idUsuario = cliente.getUsuario().getId_usuario();
        cliente.setUsuario(usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("El usuario no existe")));
        if (clienteRepository.existsById(idUsuario)) {
            throw new IllegalStateException("El usuario ya tiene un perfil de cliente");
        }
        cliente.setFecha_registro(cliente.getFecha_registro() == null
                ? LocalDateTime.now()
                : cliente.getFecha_registro());
        cliente.setActivo(true);
        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente actualizarCliente(Long idCliente, Cliente cambios) {
        if (cambios == null) {
            throw new IllegalArgumentException("Los datos del cliente son obligatorios");
        }
        Cliente cliente = obtenerClientePorId(idCliente);
        if (cambios.getFecha_nacimiento() != null) {
            cliente.setFecha_nacimiento(cambios.getFecha_nacimiento());
        }
        if (cambios.getNotasMedicas() != null) {
            cliente.setNotasMedicas(cambios.getNotasMedicas());
        }
        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente desactivarCliente(Long idCliente) {
        Cliente cliente = obtenerClientePorId(idCliente);
        cliente.setActivo(false);
        return clienteRepository.save(cliente);
    }
}
