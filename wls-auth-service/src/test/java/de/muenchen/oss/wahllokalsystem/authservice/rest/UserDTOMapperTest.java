package de.muenchen.oss.wahllokalsystem.authservice.rest;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class UserDTOMapperTest {

    @Nested
    class ToDTO {

        UserDTOMapper unitUnderTest = Mappers.getMapper(UserDTOMapper.class);

        @Test
        void should_returnDTO_when_givenModel() {

        }
    }
}
