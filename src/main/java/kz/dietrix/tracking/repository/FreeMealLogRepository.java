package kz.dietrix.tracking.repository;

import kz.dietrix.tracking.entity.FreeMealLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FreeMealLogRepository extends JpaRepository<FreeMealLog, Long> {

    Optional<FreeMealLog> findByIdAndUserId(Long id, Long userId);

    List<FreeMealLog> findByUserIdAndLoggedOnOrderByCreatedAtDesc(Long userId, LocalDate loggedOn);

    @Query("SELECT f FROM FreeMealLog f WHERE f.user.id = :userId AND f.loggedOn BETWEEN :from AND :to ORDER BY f.loggedOn DESC, f.createdAt DESC")
    List<FreeMealLog> findByUserIdAndDateRange(@Param("userId") Long userId,
                                               @Param("from") LocalDate from,
                                               @Param("to") LocalDate to);
}

