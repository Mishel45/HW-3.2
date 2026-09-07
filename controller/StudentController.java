package ru.hogwarts.school.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.Collection;

@RestController
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/{id}")
    public Student getStudentInfo(@PathVariable Long id) {
        return studentService.findStudent(id);
    }

    @GetMapping
    public Collection<Student> findStudents(
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge) {
        if (age != null) {
            return studentService.findByAge(age);
        }
        if (minAge != null && maxAge != null) {
            return studentService.findByAgeBetween(minAge, maxAge);
        }
        return studentService.getAllStudents();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Student createStudent(@RequestBody Student student) {
        return studentService.addStudent(student);
    }

    @PutMapping
    public Student editStudent(@RequestBody Student student) {
        return studentService.editStudent(student);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
    }

    @GetMapping("/{id}/faculty")
    public Faculty getFacultyByStudent(@PathVariable Long id) {
        return studentService.getFacultyByStudentId(id);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleIllegalArgumentException(IllegalArgumentException e) {
        return e.getMessage();
    }

    @GetMapping("/count")
    public long getStudentsCount() {
        return studentService.getStudentsCount();
    }

    @GetMapping("/average-age")
    public double getAverageAge() {
        return studentService.getAverageAge();
    }

    @GetMapping("/last-five")
    public Collection<Student> getLastFiveStudents() {
        return studentService.getLastFiveStudents();
    }
}