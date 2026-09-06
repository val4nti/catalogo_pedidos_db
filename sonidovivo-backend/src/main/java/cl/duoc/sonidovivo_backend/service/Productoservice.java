package cl.duoc.sonidovivo_backend.service;


import cl.duoc.sonidovivo_backend.model.Categoria;
import cl.duoc.sonidovivo_backend.model.Producto;
import cl.duoc.sonidovivo_backend.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import cl.duoc.sonidovivo_backend.exception.RecursoNoEncontradoException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productorepository;
    private final CategoriaService categoriaService;

    public List<Producto> listarTodos() {
        return productorepository.findAll();
    }

    public Producto buscarPorId(Long id) {
        return productorepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado: " + id));
    }

    public List<Producto> buscarPorNombre(String nombre) {
        return productorepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<Producto> listarConStockCritico() {
        // Regla de negocio: productos cuyo stock ya llegó al umbral de alerta.
        return productorepository.findAll().stream()
                .filter(Producto::isStockBajoCritico)
                .toList();
    }

    public Producto crear(Producto producto) {
        // Verifica que la categoría exista antes de guardar (lanza 404 si no).
        Categoria categoria = categoriaService.buscarPorId(producto.getCategoria().getId());
        producto.setCategoria(categoria);
        return productorepository.save(producto);
    }

    public Producto actualizar(Long id, Producto datos) {
        Producto existente = buscarPorId(id);
        existente.setNombre(datos.getNombre());
        existente.setDescripcion(datos.getDescripcion());
        existente.setPrecio(datos.getPrecio());
        existente.setStock(datos.getStock());
        existente.setStockCritico(datos.getStockCritico());
        existente.setImagen(datos.getImagen());
        if (datos.getCategoria() != null && datos.getCategoria().getId() != null) {
            existente.setCategoria(categoriaService.buscarPorId(datos.getCategoria().getId()));
        }
        return productorepository.save(existente);
    }

    public void eliminar(Long id) {
        Producto existente = buscarPorId(id);
        productorepository.delete(existente);
    }
}