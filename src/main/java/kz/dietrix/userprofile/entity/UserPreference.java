package kz.dietrix.userprofile.entity;

import jakarta.persistence.*;
import kz.dietrix.auth.entity.User;
import kz.dietrix.common.entity.BaseEntity;
import kz.dietrix.common.reference.Allergy;
import kz.dietrix.common.reference.DietType;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_preferences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreference extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "diet_type")
    @Builder.Default
    private DietType dietType = DietType.NONE;

    @ElementCollection(targetClass = Allergy.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_allergies", joinColumns = @JoinColumn(name = "preference_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "allergy")
    @Builder.Default
    private List<Allergy> allergies = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_liked_foods", joinColumns = @JoinColumn(name = "preference_id"))
    @Column(name = "food")
    @Builder.Default
    private List<String> likedFoods = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_disliked_foods", joinColumns = @JoinColumn(name = "preference_id"))
    @Column(name = "food")
    @Builder.Default
    private List<String> dislikedFoods = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_cuisine_preferences", joinColumns = @JoinColumn(name = "preference_id"))
    @Column(name = "cuisine")
    @Builder.Default
    private List<String> cuisinePreferences = new ArrayList<>();
}

