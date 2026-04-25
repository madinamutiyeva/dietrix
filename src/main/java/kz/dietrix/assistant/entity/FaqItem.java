package kz.dietrix.assistant.entity;

import jakarta.persistence.*;
import kz.dietrix.common.entity.BaseEntity;
import lombok.*;

@Entity
@Table(name = "faq_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FaqItem extends BaseEntity {

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String answer;

    private String category;
}

