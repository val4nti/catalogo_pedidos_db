package cl.duoc.sonidovivo_backend.controller;

import cl.duoc.sonidovivo_backend.model.Producto;
import cl.duoc.sonidovivo_backend.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class Productocontroller {

    private final ProductoService productoService;

    // GET /api/productos            -> lista todo
    // GET /api/productos?nombre=xxx -> filtra por nombre
    @GetMapping
    public List<Producto> listar(@RequestParam(required = false) String nombre) {
        if (nombre != null && !nombre.isBlank()) {
            return productoService.buscarPorNombre(nombre);
        }
        return productoService.listarTodos();
    }

    @GetMapping("/{id}")
    public Producto buscarPorId(@PathVariable Long id) {
        return productoService.buscarPorId(id);
    }

    // Endpoint para el panel admin: qué productos necesitan reposición.
    @GetMapping("/stock-critico")
    public List<Producto> listarConStockCritico() {
        return productoService.listarConStockCritico();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Producto crear(@Valid @RequestBody Producto producto) {
        return productoService.crear(producto);
    }

    @PutMapping("/{id}")
    public Producto actualizar(@PathVariable Long id, @Valid @RequestBody Producto producto) {
        return productoService.actualizar(id, producto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}