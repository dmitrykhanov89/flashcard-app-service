package com.sekhanov.flashcard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sekhanov.flashcard.dto.*;
import com.sekhanov.flashcard.service.CardsService;
import com.sekhanov.flashcard.service.FlashcardSetService;
import com.sekhanov.flashcard.service.LastSeenFlashcardSetService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = FlashcardSetController.class)
@AutoConfigureMockMvc(addFilters = false)
class FlashcardSetControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private FlashcardSetService flashcardSetService;
    @MockitoBean
    private LastSeenFlashcardSetService lastSeenFlashcardSetService;
    @MockitoBean
    private CardsService cardsService;

    @Test
    void createFlashcardSet_withValidRequest_shouldReturnCreatedSet() throws Exception {
        CreateFlashcardSetDTO requestDto = new CreateFlashcardSetDTO();
        requestDto.setName("English Basics");
        requestDto.setDescription("Basic English vocabulary");
        FlashcardSetDTO responseDto = new FlashcardSetDTO();
        responseDto.setId(1L);
        responseDto.setName("English Basics");

        when(flashcardSetService.createFlashcardSet(any(CreateFlashcardSetDTO.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/flashcardSet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpectAll(status().isCreated(), content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.id").value(1L),
                        jsonPath("$.name").value("English Basics"));
    }

    @Test
    void getFlashcardSetById_withExistingId_shouldReturnSet() throws Exception {
        FlashcardSetDTO responseDto = new FlashcardSetDTO();
        responseDto.setId(1L);
        responseDto.setName("English Basics");

        when(flashcardSetService.getFlashcardSetById(1L)).thenReturn(Optional.of(responseDto));

        mockMvc.perform(get("/api/flashcardSet/1"))
                .andExpectAll(status().isOk(), content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.id").value(1L),
                        jsonPath("$.name").value("English Basics"));
    }

    @Test
    void getFlashcardSetById_withNonExistingId_shouldThrowNotFound() throws Exception {
        when(flashcardSetService.getFlashcardSetById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/flashcardSet/999"))
                .andExpect(result -> {
                    Throwable ex = result.getResolvedException();
                    assert ex instanceof EntityNotFoundException;
                    assert ex.getMessage().contains("Список слов с id 999 не найден");
                });
    }

    @Test
    void updateFlashcardSet_withExistingId_shouldReturnUpdatedSet() throws Exception {
        CreateFlashcardSetDTO requestDto = new CreateFlashcardSetDTO();
        requestDto.setName("Updated Name");
        requestDto.setDescription("Updated Description");
        FlashcardSetDTO responseDto = new FlashcardSetDTO();
        responseDto.setId(1L);
        responseDto.setName("Updated Name");

        when(flashcardSetService.updateFlashcardSet(anyLong(), any(CreateFlashcardSetDTO.class))).thenReturn(Optional.of(responseDto));

        mockMvc.perform(put("/api/flashcardSet/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpectAll(status().isOk(), content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.id").value(1L),
                        jsonPath("$.name").value("Updated Name"));
    }

    @Test
    void updateFlashcardSet_withNonExistingId_shouldThrowNotFound() throws Exception {
        CreateFlashcardSetDTO requestDto = new CreateFlashcardSetDTO();
        requestDto.setName("Updated Name");

        when(flashcardSetService.updateFlashcardSet(anyLong(), any(CreateFlashcardSetDTO.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/flashcardSet/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(result -> {
                    Throwable ex = result.getResolvedException();
                    assert ex instanceof EntityNotFoundException;
                    assert ex.getMessage().contains("Список слов с id 999 не найден");
                });
    }

    @Test
    void deleteFlashcardSet_withExistingId_shouldReturnNoContent() throws Exception {
        when(flashcardSetService.deleteFlashcardSet(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/flashcardSet/1")).andExpect(status().isNoContent());
    }

    @Test
    void deleteFlashcardSet_withNonExistingId_shouldThrowNotFound() throws Exception {
        when(flashcardSetService.deleteFlashcardSet(999L)).thenReturn(false);

        mockMvc.perform(delete("/api/flashcardSet/999"))
                .andExpect(result -> {
                    Throwable ex = result.getResolvedException();
                    assert ex instanceof EntityNotFoundException;
                    assert ex.getMessage().contains("Список слов с id 999 не найден");
                });
    }

    @Test
    void getLastSeenSets_forCurrentUser_shouldReturnList() throws Exception {
        LastSeenFlashcardSetDto dto = new LastSeenFlashcardSetDto();
        dto.setFlashcardSetId(1L);
        dto.setFlashcardSetName("Recent Set");
        dto.setOpenedAt(LocalDateTime.now());

        when(lastSeenFlashcardSetService.getLastSeenSetsForCurrentUser()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/flashcardSet/LastSeenSets"))
                .andExpectAll(status().isOk(), content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$[0].flashcardSetId").value(1L),
                        jsonPath("$[0].flashcardSetName").value("Recent Set"),
                        jsonPath("$[0].openedAt").exists());
    }

    @Test
    void getFlashcardSetsByOwner_withExistingOwnerId_shouldReturnList() throws Exception {
        FlashcardSetDTO dto = new FlashcardSetDTO();
        dto.setId(1L);
        dto.setName("Owner Set");

        when(flashcardSetService.getFlashcardSetsByOwnerId(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/flashcardSet/owner/1"))
                .andExpectAll(status().isOk(), content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$[0].id").value(1L),
                        jsonPath("$[0].name").value("Owner Set"));
    }
}
