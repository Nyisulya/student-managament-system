package com.studentmanagement.studentmanagement.student;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // CREATE
    public Student createStudent(Student student) {

        if (studentRepository.existsByRegistrationNumber(
                student.getRegistrationNumber())) {

            throw new RuntimeException(
                    "Registration number already exists"
            );
        }

        if (studentRepository.existsByEmail(
                student.getEmail())) {

            throw new RuntimeException(
                    "Email already exists"
            );
        }

        return studentRepository.save(student);
    }

    // GET ALL
    public List<Student> getAllStudents() {

        return studentRepository.findAll();
    }

    // GET ONE
    public Student getStudentById(Long id) {

        return studentRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Student not found with id: " + id
                        )
                );
    }

    // UPDATE
    public Student updateStudent(
            Long id,
            Student updatedStudent) {

        Student existingStudent =
                getStudentById(id);

        existingStudent.setRegistrationNumber(
                updatedStudent.getRegistrationNumber()
        );

        existingStudent.setFullName(
                updatedStudent.getFullName()
        );

        existingStudent.setEmail(
                updatedStudent.getEmail()
        );

        existingStudent.setPhone(
                updatedStudent.getPhone()
        );

        existingStudent.setGender(
                updatedStudent.getGender()
        );

        existingStudent.setDateOfBirth(
                updatedStudent.getDateOfBirth()
        );

        return studentRepository.save(existingStudent);
    }

    // DELETE
    @Transactional
    public void deleteStudent(Long id) {

        Student student =
                getStudentById(id);

        studentRepository.delete(student);
    }
}