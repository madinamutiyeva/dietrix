package kz.dietrix.userprofile.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import kz.dietrix.common.util.LooseStringDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserSettingsDto {
    /** LIGHT | DARK | SYSTEM (case-insensitive). Accepts string or {value/name/id/...}. */
    @JsonDeserialize(using = LooseStringDeserializer.class)
    private String theme;

    /** "ru" | "kk" | "en". Accepts string or {value/name/...}. */
    @JsonDeserialize(using = LooseStringDeserializer.class)
    private String locale;

    /** METRIC | IMPERIAL (case-insensitive). Accepts string or {value/name/id/...}. */
    @JsonDeserialize(using = LooseStringDeserializer.class)
    private String units;
}

