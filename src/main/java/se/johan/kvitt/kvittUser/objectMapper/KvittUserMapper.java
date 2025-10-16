package se.johan.kvitt.kvittUser.objectMapper;

import org.springframework.stereotype.Component;
import se.johan.kvitt.event.dto.request.EventCreationRequestDTO;
import se.johan.kvitt.event.model.Event;
import se.johan.kvitt.kvittUser.dto.request.KvittUserCreationRequestDTO;
import se.johan.kvitt.kvittUser.model.KvittUser;

import java.time.LocalDateTime;

@Component
public class KvittUserMapper {
    public KvittUser toEntity(KvittUserCreationRequestDTO kvittUserCreationRequestDTO) {
        return new KvittUser(
                null,
                kvittUserCreationRequestDTO.username(),
                kvittUserCreationRequestDTO.password(),
                kvittUserCreationRequestDTO.totalIncome(),
                kvittUserCreationRequestDTO.totalExpense()
        );
    }

}
