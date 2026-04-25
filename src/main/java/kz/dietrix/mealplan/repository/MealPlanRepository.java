package kz.dietrix.mealplan.repository;

import kz.dietrix.mealplan.entity.MealPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MealPlanRepository extends JpaRepository<MealPlan, Long> {

    @Query("SELECT mp FROM MealPlan mp WHERE mp.user.id = :userId AND mp.status = 'ACTIVE' ORDER BY mp.createdAt DESC")
    List<MealPlan> findActiveByUserId(@Param("userId") Long userId);

    @Query("SELECT mp FROM MealPlan mp WHERE mp.user.id = :userId AND mp.weekStartDate = :date AND mp.status = 'ACTIVE'")
    List<MealPlan> findByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("SELECT mp FROM MealPlan mp WHERE mp.user.id = :userId AND mp.weekStartDate BETWEEN :from AND :to ORDER BY mp.weekStartDate DESC, mp.createdAt DESC")
    List<MealPlan> findByUserIdAndDateRange(@Param("userId") Long userId,
                                            @Param("from") LocalDate from,
                                            @Param("to") LocalDate to);

    @Query("SELECT mp FROM MealPlan mp WHERE mp.user.id = :userId ORDER BY mp.weekStartDate DESC, mp.createdAt DESC")
    List<MealPlan> findAllByUserIdOrderByDateDesc(@Param("userId") Long userId);

    Optional<MealPlan> findByIdAndUserId(Long id, Long userId);
}

