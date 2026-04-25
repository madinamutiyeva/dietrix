package kz.dietrix.mealplan.repository;

import kz.dietrix.mealplan.entity.ShoppingListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShoppingListItemRepository extends JpaRepository<ShoppingListItem, Long> {

    List<ShoppingListItem> findByMealPlanId(Long mealPlanId);
}

