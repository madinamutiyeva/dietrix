package kz.dietrix.mealplan.repository;

import kz.dietrix.mealplan.entity.MealPlanMeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MealPlanMealRepository extends JpaRepository<MealPlanMeal, Long> {

    @Query("SELECT m FROM MealPlanMeal m WHERE m.id = :mealId AND m.mealPlanDay.mealPlan.id = :planId")
    Optional<MealPlanMeal> findByIdAndPlanId(@Param("mealId") Long mealId, @Param("planId") Long planId);

    @Query("SELECT COUNT(m) FROM MealPlanMeal m WHERE m.mealPlanDay.mealPlan.id = :planId AND m.completed = true")
    long countCompletedByPlanId(@Param("planId") Long planId);

    @Query("SELECT COUNT(m) FROM MealPlanMeal m WHERE m.mealPlanDay.mealPlan.id = :planId")
    long countByPlanId(@Param("planId") Long planId);
}

