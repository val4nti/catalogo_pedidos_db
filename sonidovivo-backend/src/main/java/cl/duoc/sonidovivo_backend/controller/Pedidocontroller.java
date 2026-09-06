package cl.duoc.sonidovivo_backend.controller;

import cl.duoc.sonidovivo_backend.model.Pedido;
import cl.duoc.sonidovivo_backend.service.Pedidoservice;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class Pedidocontroller {

    private final Pedidoservice pedidoService;

    @GetMapping
    public List<Pedido> listar(@RequestParam(required = false) Long usuarioId) {
        if (usuarioId != null) {
            return pedidoService.listarPorUsuario(usuarioId);
        }
        return pedidoService.listarTodos();
    }

    @GetMapping("/{id}")
    public Pedido buscarPorId(@PathVariable Long id) {
        return pedidoService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Pedido crear(@Valid @RequestBody CrearPedidoRequest request) {
        return pedidoService.crearPedido(request);
    }
}