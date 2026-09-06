package cl.duoc.sonidovivo_backend.repository;

import cl.duoc.sonidovivo_backend.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Al extender JpaRepository, Spring genera automáticamente los métodos
 * CRUD básicos (save, findById, findAll, deleteById, etc.) sin que
 * tengamos que escribir ni una línea de SQL.
 */
public interface Categoriarepository extends JpaRepository<Categoria, Long> {
    boolean existsByNombreIgnoreCase(String nombre);
}