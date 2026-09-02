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
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
class StudentControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private StudentService studentService;

    @Test
    void testCreateStudent() throws Exception {
        Student student = new Student(null, "Гарри Поттер", 11);
        Student savedStudent = new Student(1L, "Гарри Поттер", 11);

        Mockito.when(studentService.addStudent(Mockito.any(Student.class))).thenReturn(savedStudent);

        mockMvc.perform(MockMvcRequestBuilders.post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Гарри Поттер"));
    }

    @Test
    void testGetStudentInfo() throws Exception {
        Student student = new Student(1L, "Гермиона Грейнджер", 12);

        Mockito.when(studentService.findStudent(1L)).thenReturn(student);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Гермиона Грейнджер"));
    }

    @Test
    void testGetStudentInfoNotFound() throws Exception {
        Mockito.when(studentService.findStudent(999L))
                .thenThrow(new IllegalArgumentException("Студент не найден"));

        mockMvc.perform(MockMvcRequestBuilders.get("/student/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testEditStudent() throws Exception {
        Student student = new Student(1L, "Рон Уизли", 11);

        Mockito.when(studentService.editStudent(Mockito.any(Student.class))).thenReturn(student);

        mockMvc.perform(MockMvcRequestBuilders.put("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Рон Уизли"));
    }

    @Test
    void testDeleteStudent() throws Exception {
        Mockito.doNothing().when(studentService).deleteStudent(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/student/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testFindStudentsByAge() throws Exception {
        List<Student> students = Collections.singletonList(new Student(1L, "Полумна Лавгуд", 12));

        Mockito.when(studentService.findByAge(12)).thenReturn(students);

        mockMvc.perform(MockMvcRequestBuilders.get("/student?age=12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Полумна Лавгуд"));
    }
}
