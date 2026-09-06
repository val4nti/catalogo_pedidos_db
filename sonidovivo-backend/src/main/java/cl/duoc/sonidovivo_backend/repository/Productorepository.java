package cl.duoc.sonidovivo_backend.repository;

import cl.duoc.sonidovivo_backend.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface Productorepository extends JpaRepository<Producto, Long> {

    Optional<Producto> findByCodigo(String codigo);

    List<Producto> findByCategoriaId(Long categoriaId);

    // Spring traduce el nombre del método directamente a una consulta SQL.
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
}