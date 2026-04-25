package kz.dietrix.recipes.repository;

import kz.dietrix.recipes.entity.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    Optional<Recipe> findByTitle(String title);

    Page<Recipe> findByGeneratedFalse(Pageable pageable);

    @Query("SELECT r FROM Recipe r WHERE r.generatedForUser.id = :userId ORDER BY r.createdAt DESC")
    List<Recipe> findRecentGeneratedByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT r FROM Recipe r WHERE " +
            "(:cuisine IS NULL OR r.cuisine = :cuisine) AND " +
            "(:maxCalories IS NULL OR r.calories <= :maxCalories) AND " +
            "(:mealType IS NULL OR r.mealType = :mealType)")
    Page<Recipe> findFiltered(@Param("cuisine") String cuisine,
                              @Param("maxCalories") Integer maxCalories,
                              @Param("mealType") String mealType,
                              Pageable pageable);

    @Query("SELECT r.generatedForUser.id, COUNT(r) FROM Recipe r " +
            "WHERE r.generated = true AND r.generatedForUser IS NOT NULL " +
            "AND r.createdAt >= :from AND r.createdAt <= :to " +
            "GROUP BY r.generatedForUser.id")
    List<Object[]> countGeneratedPerUserBetween(@Param("from") LocalDateTime from,
                                                @Param("to") LocalDateTime to);
}

