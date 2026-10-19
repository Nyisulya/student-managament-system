package com.studentmanagement.studentmanagement.student;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<Student> createStudent(
            @RequestBody Student student) {

        Student createdStudent =
                studentService.createStudent(student);

        return ResponseEntity.ok(createdStudent);
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {

        List<Student> students =
                studentService.getAllStudents();

        return ResponseEntity.ok(students);
    }

    // GET ONE
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(
            @PathVariable Long id) {

        Student student =
                studentService.getStudentById(id);

        return ResponseEntity.ok(student);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(
            @PathVariable Long id,
            @RequestBody Student student) {

        Student updatedStudent =
                studentService.updateStudent(
                        id,
                        student
                );

        return ResponseEntity.ok(updatedStudent);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudent(
            @PathVariable Long id) {

        studentService.deleteStudent(id);

        return ResponseEntity.ok(
                "Student deleted successfully"
        );
    }
}