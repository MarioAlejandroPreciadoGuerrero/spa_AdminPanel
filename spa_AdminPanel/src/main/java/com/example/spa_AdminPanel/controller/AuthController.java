package com.example.spa_AdminPanel.controller;

import com.example.spa_AdminPanel.model.NombreRol;
import com.example.spa_AdminPanel.model.Rol;
import com.example.spa_AdminPanel.model.Usuario;
import com.example.spa_AdminPanel.service.RolService;
import com.example.spa_AdminPanel.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// este controlador se encarga del inicio de sesión de usuarios

@Controller
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RolService  rolService;

    @GetMapping("/")
    public String inicio(HttpSession session) {
        /**
         *
         */
        String rol = (String) session.getAttribute("rol_usuario");
        if (rol == null) {
            return "redirect:/login";
        }
        switch (rol){
            case "ADMIN":
                return "redirect:/admin";
            case "TERAPEUTA":
                return "redirect:/terapeuta";
            case "CLIENTE":
                return "redirect:/cliente";
            default:
                return "redirect:/servicios";

        }
    }

    @GetMapping({"/login", "/auth/login"})
    public String mostrarInicioSesion() {
        return "auth/login";
    }

    @PostMapping({"/login", "/auth/login"})
    public String iniciarSesion(@RequestParam String email, @RequestParam String contrasenia,
                                HttpSession session) {
        //recibe el correo y contraseña del formulario, el usuarioService verifica que sean validos
        Usuario usuario = usuarioService.autenticarUsuario(email, contrasenia);
        if (usuario == null) {
            //si son invalidas lo regresa al login, si no lo lleva al inicio
            return "redirect:/login";
        }
        session.setAttribute("id_usuario", usuario.getId_usuario());
        //Guarda el rol como String en la sesion
        session.setAttribute("rol_usuario", usuario.getRol().getNombre().name());
        return "redirect:/";
    }

    @GetMapping({"/registro", "/auth/registro"})
    public String mostrarRegistro() {
        return "auth/registro";
    }

    @PostMapping({"/registro", "/auth/registro"})
    public String registrar(@RequestParam String nombre, @RequestParam String email,
                            @RequestParam String contrasenia, @RequestParam(required = false) String telefono,
                            @RequestParam String nombreRol) {
        //recibe los datos del formulario de registro de cliente en el formulario, si no existe lo crea
        try {
            String rolDefinir = nombreRol.trim().toLowerCase();
            NombreRol rol;
            switch(rolDefinir){
                case "admin" -> {
                    rol = NombreRol.ADMIN;
                    break;
                }
                case "terapeuta" -> {
                    rol = NombreRol.TERAPEUTA;
                    break;
                }
                case "cliente" -> {
                    rol = NombreRol.CLIENTE;
                    break;
                }
                default -> {
                    return "redirect:/registro";
                }
            }

            Rol rolUsuario = rolService.buscarPorNombre(rol);

            Usuario usuario = new Usuario();
            usuario.setNombre(nombre);
            usuario.setEmail(email);
            usuario.setContrasenia(contrasenia);
            usuario.setTelefono(telefono);
            usuario.setRol(rolUsuario);

            //construye el usuario y lo registra con usuarioService
            usuarioService.registrarUsuario(usuario);
            return "redirect:/login";
        } catch (Exception exception) {
            //si hay errores, redirecciona a registro
            return "redirect:/registro";
        }
    }

    @PostMapping({"/logout", "/auth/logout"})
    public String cerrarSesion(HttpSession session) {
        //invalida la sesion cuando el cliente quiera cerrar sesión
        session.invalidate();
        return "redirect:/login";
    }
}
