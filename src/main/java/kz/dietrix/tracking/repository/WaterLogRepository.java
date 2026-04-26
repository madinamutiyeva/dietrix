package kz.dietrix.tracking.repository;

import kz.dietrix.tracking.entity.WaterLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WaterLogRepository extends JpaRepository<WaterLog, Long> {

    Optional<WaterLog> findByIdAndUserId(Long id, Long userId);

    List<WaterLog> findByUserIdAndLoggedOnOrderByCreatedAtAsc(Long userId, LocalDate loggedOn);

    long deleteByUserIdAndLoggedOn(Long userId, LocalDate loggedOn);

    @Query("SELECT COALESCE(SUM(w.amountMl), 0) FROM WaterLog w WHERE w.user.id = :userId AND w.loggedOn = :date")
    int sumByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("SELECT w.loggedOn AS day, COALESCE(SUM(w.amountMl), 0) AS total " +
            "FROM WaterLog w WHERE w.user.id = :userId AND w.loggedOn BETWEEN :from AND :to " +
            "GROUP BY w.loggedOn ORDER BY w.loggedOn")
    List<Object[]> sumByDay(@Param("userId") Long userId,
                            @Param("from") LocalDate from,
                            @Param("to") LocalDate to);
}
