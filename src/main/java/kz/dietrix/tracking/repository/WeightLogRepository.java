package kz.dietrix.tracking.repository;

import kz.dietrix.tracking.entity.WeightLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeightLogRepository extends JpaRepository<WeightLog, Long> {

    Optional<WeightLog> findByUserIdAndLoggedOn(Long userId, LocalDate loggedOn);

    Optional<WeightLog> findByIdAndUserId(Long id, Long userId);

    List<WeightLog> findByUserIdOrderByLoggedOnDesc(Long userId);

    @Query("SELECT w FROM WeightLog w WHERE w.user.id = :userId AND w.loggedOn BETWEEN :from AND :to ORDER BY w.loggedOn ASC")
    List<WeightLog> findByUserIdAndDateRange(@Param("userId") Long userId,
                                             @Param("from") LocalDate from,
                                             @Param("to") LocalDate to);

    @Query("SELECT w FROM WeightLog w WHERE w.user.id = :userId ORDER BY w.loggedOn DESC LIMIT 1")
    Optional<WeightLog> findLatestByUserId(@Param("userId") Long userId);
}

