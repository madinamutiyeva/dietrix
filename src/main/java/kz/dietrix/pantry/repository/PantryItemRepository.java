package kz.dietrix.pantry.repository;

import kz.dietrix.pantry.entity.PantryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PantryItemRepository extends JpaRepository<PantryItem, Long> {

    List<PantryItem> findByUserIdOrderByNameAsc(Long userId);

    @Query("SELECT DISTINCT p.category FROM PantryItem p WHERE p.user.id = :userId AND p.category IS NOT NULL")
    List<String> findDistinctCategoriesByUserId(Long userId);

    long countByUserId(Long userId);

    void deleteByIdAndUserId(Long id, Long userId);

    java.util.Optional<PantryItem> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT p FROM PantryItem p WHERE p.expirationDate >= :today AND p.expirationDate <= :cutoff")
    List<PantryItem> findExpiringItems(@Param("today") LocalDate today, @Param("cutoff") LocalDate cutoff);

    @Query("""
            SELECT p FROM PantryItem p
            WHERE p.user.id = :userId
              AND (:q IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%')))
              AND (:category IS NULL OR p.category = :category)
              AND (:expiringBefore IS NULL OR (p.expirationDate IS NOT NULL AND p.expirationDate <= :expiringBefore))
            ORDER BY
                CASE WHEN p.expirationDate IS NULL THEN 1 ELSE 0 END,
                p.expirationDate ASC,
                p.name ASC
            """)
    List<PantryItem> search(@Param("userId") Long userId,
                            @Param("q") String q,
                            @Param("category") String category,
                            @Param("expiringBefore") LocalDate expiringBefore);
}

