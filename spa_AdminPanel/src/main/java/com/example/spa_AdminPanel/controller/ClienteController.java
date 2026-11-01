package com.example.spa_AdminPanel.controller;

import com.example.spa_AdminPanel.model.Cliente;
import com.example.spa_AdminPanel.repository.UsuarioRepository;
import com.example.spa_AdminPanel.service.ClienteService;
import com.example.spa_AdminPanel.service.UsuarioService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    //para obtener todos los clientes
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", clienteService.obtenerClientes());
        return "clientes/lista";
    }

    //para el formulario de registro
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "clientes/formulario";
    }

    //permite registrar un cliente
    @PostMapping
    public String registrar(@ModelAttribute("cliente") Cliente cliente) {
        try {
            cliente.setId_cliente(null);
            clienteService.registrarCliente(cliente);
            return "redirect:/clientes";
        } catch (Exception exception) {
            return "redirect:/clientes/nuevo";
        }
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        //recibe el id del cliente desde la url, obtiene el cliente y retorna el cliente
        model.addAttribute("cliente", clienteService.obtenerClientePorId(id));
        return "clientes/detalle";
    }

    //para editar un usuario
    @GetMapping("/{id}/editar")
    public String mostrarEdicion(@PathVariable Long id, Model model) {
        model.addAttribute("cliente", clienteService.obtenerClientePorId(id));
        return "clientes/formulario";
    }

    //actualizar un usuario
    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id, @ModelAttribute("cliente") Cliente cambios) {
        try {
            clienteService.actualizarCliente(id, cambios);
        } catch (Exception exception) {
            return "redirect:/clientes/" + id;
        }
        return "redirect:/clientes/" + id;
    }

    //para desactivar un cliente
    @PostMapping("/{id}/desactivar")
    public String desactivar(@PathVariable Long id) {
        try {
            clienteService.desactivarCliente(id);
        } catch (Exception exception) {
            return "redirect:/clientes";
        }
        return "redirect:/clientes";
    }
}
