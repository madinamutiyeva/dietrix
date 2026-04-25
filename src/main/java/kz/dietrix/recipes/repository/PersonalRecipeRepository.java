package kz.dietrix.recipes.repository;

import kz.dietrix.recipes.entity.PersonalRecipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonalRecipeRepository extends JpaRepository<PersonalRecipe, Long> {

    Page<PersonalRecipe> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Optional<PersonalRecipe> findByIdAndUserId(Long id, Long userId);

    long countByUserId(Long userId);
}

