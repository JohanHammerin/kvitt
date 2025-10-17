package se.johan.kvitt.kvittUser.objectMapper;

import org.springframework.stereotype.Component;
import se.johan.kvitt.kvittUser.dto.request.KvittUserCreateKvittUserRequestDTO;
import se.johan.kvitt.kvittUser.model.KvittUser;

@Component
public class KvittUserMapper {
    public KvittUser toEntity(KvittUserCreateKvittUserRequestDTO kvittUserCreateKvittUserRequestDTO) {
        return new KvittUser(
                null,
                kvittUserCreateKvittUserRequestDTO.username(),
                kvittUserCreateKvittUserRequestDTO.password(),
                kvittUserCreateKvittUserRequestDTO.totalIncome(),
                kvittUserCreateKvittUserRequestDTO.totalExpense()
        );
    }

}
