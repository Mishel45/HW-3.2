package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.service.FacultyService;

import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FacultyController.class)
class FacultyControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private FacultyService facultyService;

    @Test
    void testCreateFaculty() throws Exception {
        Faculty faculty = new Faculty(null, "Гриффиндор", "Красный");
        Faculty savedFaculty = new Faculty(1L, "Гриффиндор", "Красный");

        Mockito.when(facultyService.addFaculty(Mockito.any(Faculty.class))).thenReturn(savedFaculty);

        mockMvc.perform(MockMvcRequestBuilders.post("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(faculty)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Гриффиндор"));
    }

    @Test
    void testGetFacultyInfo() throws Exception {
        Faculty faculty = new Faculty(1L, "Слизерин", "Зеленый");

        Mockito.when(facultyService.findFaculty(1L)).thenReturn(faculty);

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.color").value("Зеленый"));
    }

    @Test
    void testGetFacultyInfoNotFound() throws Exception {
        Mockito.when(facultyService.findFaculty(999L))
                .thenThrow(new IllegalArgumentException("Факультет не найден"));

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testEditFaculty() throws Exception {
        Faculty faculty = new Faculty(1L, "Когтевран", "Синий");

        Mockito.when(facultyService.editFaculty(Mockito.any(Faculty.class))).thenReturn(faculty);

        mockMvc.perform(MockMvcRequestBuilders.put("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(faculty)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Когтевран"));
    }

    @Test
    void testDeleteFaculty() throws Exception {
        Mockito.doNothing().when(facultyService).deleteFaculty(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/faculty/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testFindByNameOrColor() throws Exception {
        List<Faculty> faculties = Collections.singletonList(new Faculty(1L, "Гриффиндор", "Красный"));

        Mockito.when(facultyService.findByNameOrColor("грифф")).thenReturn(faculties);

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/search?search=грифф"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Гриффиндор"));
    }
}