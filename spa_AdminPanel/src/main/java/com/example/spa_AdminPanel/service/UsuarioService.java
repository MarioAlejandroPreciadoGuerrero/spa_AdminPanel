package com.example.spa_AdminPanel.service;

import com.example.spa_AdminPanel.model.Usuario;
import com.example.spa_AdminPanel.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder;

    private final HttpSession session;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, HttpSession session) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.session = session;
    }

    @Transactional
    public Usuario registrarUsuario(Usuario user) {
        validarUsuarioNuevo(user);
        if (usuarioRepository.findUsuarioByEmail(user.getEmail().trim()).isPresent()) {
            throw new IllegalStateException("Ya existe un usuario registrado con ese correo");
        }
        user.setEmail(user.getEmail().trim());
        user.setContrasenia(passwordEncoder.encode(user.getContrasenia()));
        return usuarioRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Usuario autenticarUsuario(String email, String password) {
        if (email == null || password == null) {
            return null;
        }
        Optional<Usuario> usuarioOpt = usuarioRepository.findUsuarioByEmail(email);
        if (usuarioOpt.isPresent()) {
            Usuario user = usuarioOpt.get();
            if (user.isActivo() && passwordEncoder.matches(password, user.getContrasenia())) {
                return user;
            }
        }
        return null;
    }

    @Transactional(readOnly = true)
    public Usuario obtenerUsuarioLogueado() {
        Long usuarioId = (Long) session.getAttribute("id_usuario");
        if (usuarioId != null) {
            return usuarioRepository.findById(usuarioId).orElse(null);
        }
        return null;
    }

    @Transactional
    public void actualizarUsuario(Usuario cambios) {
        if (cambios == null || cambios.getId_usuario() == null) {
            throw new IllegalArgumentException("El id del usuario es obligatorio");
        }
        Usuario usuario = usuarioRepository.findById(cambios.getId_usuario())
                .orElseThrow(() -> new EntityNotFoundException("El usuario no existe"));
        aplicarCambios(usuario, cambios);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void actualizarParcialmenteUsuario(Usuario cambios) {
        actualizarUsuario(cambios);
    }

    @Transactional
    public void deshabilitarUsuario(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("El usuario no existe"));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    private void validarUsuarioNuevo(Usuario user) {
        if (user == null || user.getEmail() == null || user.getEmail().isBlank()
                || user.getContrasenia() == null || user.getContrasenia().isBlank()
                || user.getNombre() == null || user.getNombre().isBlank()) {
            throw new IllegalArgumentException("El usuario requiere nombre, correo y contraseña");
        }
    }

    private void aplicarCambios(Usuario usuario, Usuario cambios) {
        if (cambios.getNombre() != null && !cambios.getNombre().isBlank()) {
            usuario.setNombre(cambios.getNombre());
        }
        if (cambios.getEmail() != null && !cambios.getEmail().isBlank()) {
            String email = cambios.getEmail().trim();
            Optional<Usuario> existente = usuarioRepository.findUsuarioByEmail(email);
            if (existente.isPresent() && !existente.get().getId_usuario().equals(usuario.getId_usuario())) {
                throw new IllegalStateException("Ya existe un usuario registrado con ese correo");
            }
            usuario.setEmail(email);
        }
        if (cambios.getTelefono() != null) {
            usuario.setTelefono(cambios.getTelefono());
        }
        if (cambios.getContrasenia() != null && !cambios.getContrasenia().isBlank()) {
            usuario.setContrasenia(passwordEncoder.encode(cambios.getContrasenia()));
        }
        if (cambios.getRol() != null) {
            usuario.setRol(cambios.getRol());
        }
    }
}