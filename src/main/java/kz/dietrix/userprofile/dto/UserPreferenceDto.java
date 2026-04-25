package kz.dietrix.userprofile.dto;

import kz.dietrix.common.reference.Allergy;
import kz.dietrix.common.reference.DietType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferenceDto {

    private DietType dietType;
    private List<Allergy> allergies;
    private List<String> likedFoods;
    private List<String> dislikedFoods;
    private List<String> cuisinePreferences;
}

