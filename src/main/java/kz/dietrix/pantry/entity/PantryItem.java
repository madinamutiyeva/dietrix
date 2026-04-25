package kz.dietrix.pantry.entity;

import jakarta.persistence.*;
import kz.dietrix.auth.entity.User;
import kz.dietrix.common.entity.BaseEntity;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "pantry_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PantryItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    private Double quantity;

    private String unit;

    private String category;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;
}

