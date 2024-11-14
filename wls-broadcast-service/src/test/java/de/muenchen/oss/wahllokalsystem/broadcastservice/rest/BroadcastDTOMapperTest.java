package de.muenchen.oss.wahllokalsystem.broadcastservice.rest;

import de.muenchen.oss.wahllokalsystem.broadcastservice.utils.TestdataFactory;
import java.time.LocalDateTime;
import java.util.Arrays;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class BroadcastDTOMapperTest {

    private final BroadcastDTOMapper unitUnderTest = Mappers.getMapper(BroadcastDTOMapper.class);

    @Nested
    class ToDto {

        @Test
        void should_returnNull_when_givenNull() {
            Assertions.assertThat(unitUnderTest.toDto(null)).isNull();
        }

        @Test
        void should_returnMessageDTO_when_givenMessageEntity() {
            val mockedMessageEntity = TestdataFactory.CreateMessageEntity.withCustomParams("wahlbezirkID", "nachricht", LocalDateTime.now());
            val expectedMessageDTO = TestdataFactory.CreateMessageDto.fromEntity(mockedMessageEntity);

            val result = unitUnderTest.toDto(mockedMessageEntity);
            Assertions.assertThat(result).isEqualTo(expectedMessageDTO);
        }
    }

    @Nested
    class ToListOfMessageEntity {

        @Test
        void should_returnNull_when_givenNull() {
            Assertions.assertThat(unitUnderTest.toDto(null)).isNull();
        }

        @Test
        void should_returnListOfMessageEntity_when_givenBroadcastMessageDTO() {
            val wahlbezirkIds = Arrays.asList("1", "2", "3", "4");
            val empfangsZeit = LocalDateTime.now().withNano(0);
            val mockedBroadcastMessageDTO = TestdataFactory.CreateBroadcastMessageDto.withCustomParams(wahlbezirkIds, "nachricht");
            val expectedListOfMessageEntity = TestdataFactory.CreateMessageEntity.listFromBroadcastMessageDto(mockedBroadcastMessageDTO, empfangsZeit);

            val result = unitUnderTest.toListOfMessageEntity(mockedBroadcastMessageDTO, empfangsZeit);
            Assertions.assertThat(result).isEqualTo(expectedListOfMessageEntity);
        }
    }
}
