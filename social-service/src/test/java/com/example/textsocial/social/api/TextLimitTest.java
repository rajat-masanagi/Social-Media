package com.example.textsocial.social.api;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;

class TextLimitTest {
    @Test
    void rejectsTextLongerThan250Characters() {
        try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
            var request = new SocialDtos.CreateContentRequest("x".repeat(251));
            assertThat(validatorFactory.getValidator().validate(request)).isNotEmpty();
        }
    }
}

