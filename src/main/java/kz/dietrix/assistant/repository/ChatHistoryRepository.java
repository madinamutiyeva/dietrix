package kz.dietrix.assistant.repository;

import kz.dietrix.assistant.entity.ChatHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {

    List<ChatHistory> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    List<ChatHistory> findByUserIdOrderByCreatedAtAsc(Long userId);
}

