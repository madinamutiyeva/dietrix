package kz.dietrix.assistant.repository;

import kz.dietrix.assistant.entity.FaqItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FaqItemRepository extends JpaRepository<FaqItem, Long> {

    List<FaqItem> findByCategoryOrderByIdAsc(String category);

    List<FaqItem> findAllByOrderByIdAsc();
}

