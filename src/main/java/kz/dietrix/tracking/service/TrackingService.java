package kz.dietrix.tracking.service;

import kz.dietrix.auth.entity.User;
import kz.dietrix.common.exception.ResourceNotFoundException;
import kz.dietrix.tracking.dto.*;
import kz.dietrix.tracking.entity.FreeMealLog;
import kz.dietrix.tracking.entity.WaterLog;
import kz.dietrix.tracking.entity.WeightLog;
import kz.dietrix.tracking.repository.FreeMealLogRepository;
import kz.dietrix.tracking.repository.WaterLogRepository;
import kz.dietrix.tracking.repository.WeightLogRepository;
import kz.dietrix.userprofile.dto.UserTargetsDto;
import kz.dietrix.userprofile.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackingService {

    private final WeightLogRepository weightLogRepository;
    private final WaterLogRepository waterLogRepository;
    private final FreeMealLogRepository freeMealLogRepository;
    private final UserProfileService userProfileService;

    /** Timezone used to compute "today" for daily counters (water, free meals, etc.). */
    @Value("${app.default-timezone:Asia/Almaty}")
    private String defaultTimezone;

    /** Today's date in the configured timezone (NOT server UTC). */
    private LocalDate today() {
        try {
            return LocalDate.now(ZoneId.of(defaultTimezone));
        } catch (Exception e) {
            return LocalDate.now();
        }
    }

    // ─── Weight ──────────────────────────────────────────────────────────────

    @Transactional
    public WeightLogDto addWeight(WeightLogDto dto) {
        User user = userProfileService.getCurrentUser();
        LocalDate day = dto.getLoggedOn() != null ? dto.getLoggedOn() : today();

        WeightLog log = weightLogRepository.findByUserIdAndLoggedOn(user.getId(), day)
                .orElseGet(() -> WeightLog.builder().user(user).loggedOn(day).build());
        log.setWeightKg(dto.getWeightKg());
        log.setNote(dto.getNote());
        log = weightLogRepository.save(log);
        return toDto(log);
    }

    @Transactional(readOnly = true)
    public List<WeightLogDto> getWeightLogs() {
        User user = userProfileService.getCurrentUser();
        return weightLogRepository.findByUserIdOrderByLoggedOnDesc(user.getId())
                .stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public WeightStatsDto getWeightStats(int days) {
        User user = userProfileService.getCurrentUser();
        LocalDate to = today();
        LocalDate from = to.minusDays(Math.max(1, days) - 1L);

        List<WeightLog> logs = weightLogRepository.findByUserIdAndDateRange(user.getId(), from, to);
        if (logs.isEmpty()) {
            return WeightStatsDto.builder().points(List.of()).build();
        }

        List<WeightStatsDto.Point> points = logs.stream()
                .map(w -> WeightStatsDto.Point.builder()
                        .date(w.getLoggedOn())
                        .weightKg(w.getWeightKg())
                        .build())
                .toList();

        BigDecimal start = points.get(0).getWeightKg();
        BigDecimal current = points.get(points.size() - 1).getWeightKg();
        BigDecimal min = points.stream().map(WeightStatsDto.Point::getWeightKg).min(Comparator.naturalOrder()).orElse(start);
        BigDecimal max = points.stream().map(WeightStatsDto.Point::getWeightKg).max(Comparator.naturalOrder()).orElse(start);
        BigDecimal sum = points.stream().map(WeightStatsDto.Point::getWeightKg).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal avg = sum.divide(BigDecimal.valueOf(points.size()), 2, RoundingMode.HALF_UP);

        return WeightStatsDto.builder()
                .points(points)
                .startWeight(start)
                .currentWeight(current)
                .change(current.subtract(start))
                .min(min)
                .max(max)
                .average(avg)
                .build();
    }

    @Transactional
    public void deleteWeight(Long id) {
        User user = userProfileService.getCurrentUser();
        WeightLog log = weightLogRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("WeightLog", "id", id));
        weightLogRepository.delete(log);
    }

    // ─── Water ──────────────────────────────────────────────────────────────

    @Transactional
    public WaterStatusDto addWater(WaterLogRequest request) {
        User user = userProfileService.getCurrentUser();
        LocalDate today = LocalDate.now();

        WaterLog log = WaterLog.builder()
                .user(user)
                .amountMl(request.getAmountMl())
                .loggedOn(today)
                .build();
        waterLogRepository.save(log);
        return getWaterToday();
    }

    @Transactional(readOnly = true)
    public WaterStatusDto getWaterToday() {
        User user = userProfileService.getCurrentUser();
        LocalDate today = LocalDate.now();
        int consumed = waterLogRepository.sumByUserIdAndDate(user.getId(), today);
        int target = computeWaterTargetMl();
        double percent = target > 0 ? Math.min(100.0, (consumed * 100.0) / target) : 0;
        return WaterStatusDto.builder()
                .date(today)
                .consumedMl(consumed)
                .targetMl(target)
                .percent(Math.round(percent * 10) / 10.0)
                .build();
    }

    @Transactional
    public void deleteWaterLog(Long id) {
        User user = userProfileService.getCurrentUser();
        WaterLog log = waterLogRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("WaterLog", "id", id));
        waterLogRepository.delete(log);
    }

    /** Delete ALL water logs of the current user for the given date (defaults to today). */
    @Transactional
    public WaterStatusDto clearWaterForDate(LocalDate date) {
        User user = userProfileService.getCurrentUser();
        if (date == null) date = LocalDate.now();
        waterLogRepository.deleteByUserIdAndLoggedOn(user.getId(), date);
        return getWaterToday();
    }

    private int computeWaterTargetMl() {
        try {
            UserTargetsDto t = userProfileService.getTargets();
            if (t.getWaterMl() > 0) return t.getWaterMl();
        } catch (Exception ignored) {}
        return 2000;
    }

    // ─── Free meal log ─────────────────────────────────────────────────────

    @Transactional
    public FreeMealLogDto addFreeMeal(FreeMealLogDto dto) {
        User user = userProfileService.getCurrentUser();
        FreeMealLog entity = FreeMealLog.builder()
                .user(user)
                .name(dto.getName())
                .mealType(dto.getMealType())
                .calories(dto.getCalories())
                .protein(dto.getProtein())
                .carbs(dto.getCarbs())
                .fat(dto.getFat())
                .loggedOn(dto.getLoggedOn() != null ? dto.getLoggedOn() : LocalDate.now())
                .note(dto.getNote())
                .build();
        entity = freeMealLogRepository.save(entity);
        return toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<FreeMealLogDto> getFreeMealsForDate(LocalDate date) {
        User user = userProfileService.getCurrentUser();
        return freeMealLogRepository
                .findByUserIdAndLoggedOnOrderByCreatedAtDesc(user.getId(), date)
                .stream().map(this::toDto).toList();
    }

    @Transactional
    public void deleteFreeMeal(Long id) {
        User user = userProfileService.getCurrentUser();
        FreeMealLog entity = freeMealLogRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("FreeMealLog", "id", id));
        freeMealLogRepository.delete(entity);
    }

    // ─── Helpers used by other services ────────────────────────────────────

    @Transactional(readOnly = true)
    public int sumFreeMealCaloriesForDate(Long userId, LocalDate date) {
        return freeMealLogRepository.findByUserIdAndLoggedOnOrderByCreatedAtDesc(userId, date)
                .stream().filter(f -> f.getCalories() != null).mapToInt(FreeMealLog::getCalories).sum();
    }

    @Transactional(readOnly = true)
    public int[] sumFreeMealMacrosForDate(Long userId, LocalDate date) {
        int p = 0, c = 0, f = 0;
        for (FreeMealLog log : freeMealLogRepository.findByUserIdAndLoggedOnOrderByCreatedAtDesc(userId, date)) {
            if (log.getProtein() != null) p += log.getProtein();
            if (log.getCarbs() != null)   c += log.getCarbs();
            if (log.getFat() != null)     f += log.getFat();
        }
        return new int[]{p, c, f};
    }

    // ─── Mappers ───────────────────────────────────────────────────────────

    private WeightLogDto toDto(WeightLog w) {
        return WeightLogDto.builder()
                .id(w.getId())
                .weightKg(w.getWeightKg())
                .loggedOn(w.getLoggedOn())
                .note(w.getNote())
                .build();
    }

    private FreeMealLogDto toDto(FreeMealLog f) {
        return FreeMealLogDto.builder()
                .id(f.getId())
                .name(f.getName())
                .mealType(f.getMealType())
                .calories(f.getCalories())
                .protein(f.getProtein())
                .carbs(f.getCarbs())
                .fat(f.getFat())
                .loggedOn(f.getLoggedOn())
                .note(f.getNote())
                .build();
    }
}
