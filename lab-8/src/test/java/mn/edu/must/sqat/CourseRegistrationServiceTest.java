package mn.edu.must.sqat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseRegistrationServiceTest {

    @Mock StudentRepository repository;
    @Mock EmailSender emailSender;

    CourseRegistrationService service;

    @BeforeEach
    void setUp() {
        service = new CourseRegistrationService(repository, emailSender);
    }

    // A1: Идэвхтэй оюутан бүртгүүлэхэд "OK" буцаж,
    //     saveRegistration дуудагдаж, и-мэйл илгээгдэнэ
    @Test
    void register_activeStudentNoCoursePrerequisites_returnsOkAndSavesAndSendsEmail() {
        Student student = new Student("S001", "student@must.edu.mn", true, List.of("CS101"));
        when(repository.findStudent("S001")).thenReturn(student);
        when(repository.courseExists("CS201")).thenReturn(true);
        when(repository.prerequisites("CS201")).thenReturn(List.of());

        String result = service.register("S001", "CS201");

        assertEquals("OK", result);
        verify(repository).saveRegistration("S001", "CS201");
        verify(emailSender).send(eq("student@must.edu.mn"), anyString(), contains("CS201"));
    }

    // A2: Байхгүй оюутанд "ERROR_NO_STUDENT";
    //     сан руу хадгалаагүй, и-мэйл яваагүй
    @Test
    void register_studentNotFound_returnsErrorNoStudentAndNeverSavesOrEmails() {
        when(repository.findStudent("UNKNOWN")).thenReturn(null);

        String result = service.register("UNKNOWN", "CS201");

        assertEquals("ERROR_NO_STUDENT", result);
        verify(repository, never()).saveRegistration(anyString(), anyString());
        verify(emailSender, never()).send(anyString(), anyString(), anyString());
    }

    // A3: Идэвхгүй оюутанд "ERROR_INACTIVE_STUDENT";
    //     и-мэйлтэй огт харьцаагүй
    @Test
    void register_inactiveStudent_returnsErrorInactiveAndNoInteractionWithEmail() {
        Student student = new Student("S002", "inactive@must.edu.mn", false, List.of());
        when(repository.findStudent("S002")).thenReturn(student);

        String result = service.register("S002", "CS201");

        assertEquals("ERROR_INACTIVE_STUDENT", result);
        verifyNoInteractions(emailSender);
    }

    // A4: Өмнөх холбоо хичээл дутуу бол "ERROR_PREREQUISITES"; хадгалаагүй
    @Test
    void register_prerequisitesMissing_returnsErrorPrerequisitesAndNeverSaves() {
        Student student = new Student("S003", "s3@must.edu.mn", true, List.of("CS101"));
        when(repository.findStudent("S003")).thenReturn(student);
        when(repository.courseExists("CS301")).thenReturn(true);
        when(repository.prerequisites("CS301")).thenReturn(List.of("CS201", "CS202"));

        String result = service.register("S003", "CS301");

        assertEquals("ERROR_PREREQUISITES", result);
        verify(repository, never()).saveRegistration(anyString(), anyString());
    }

    // A5: И-мэйл илгээгч exception шидсэн ч бүртгэл "OK", хадгалалт хийгдсэн
    @Test
    void register_emailSenderThrows_returnsOkAndRegistrationStillSaved() {
        Student student = new Student("S004", "s4@must.edu.mn", true, List.of());
        when(repository.findStudent("S004")).thenReturn(student);
        when(repository.courseExists("CS201")).thenReturn(true);
        when(repository.prerequisites("CS201")).thenReturn(List.of());
        doThrow(new RuntimeException("SMTP failure")).when(emailSender)
                .send(anyString(), anyString(), anyString());

        String result = service.register("S004", "CS201");

        assertEquals("OK", result);
        verify(repository).saveRegistration("S004", "CS201");
    }

    // A6: null оролтод IllegalArgumentException; хамаарлуудтай огт харьцаагүй
    @Test
    void register_nullInputs_throwsIllegalArgumentExceptionAndNoRepoInteraction() {
        assertThrows(IllegalArgumentException.class, () -> service.register(null, "CS201"));
        assertThrows(IllegalArgumentException.class, () -> service.register("S001", null));
        verifyNoInteractions(repository);
        verifyNoInteractions(emailSender);
    }

    // A7: Илгээгдсэн и-мэйлийн биед хичээлийн код орсныг шалгах
    @Test
    void register_successfulRegistration_emailBodyContainsCourseId() {
        Student student = new Student("S005", "s5@must.edu.mn", true, List.of());
        when(repository.findStudent("S005")).thenReturn(student);
        when(repository.courseExists("CS999")).thenReturn(true);
        when(repository.prerequisites("CS999")).thenReturn(List.of());

        service.register("S005", "CS999");

        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailSender).send(eq("s5@must.edu.mn"), anyString(), bodyCaptor.capture());
        assertTrue(bodyCaptor.getValue().contains("CS999"),
                "И-мэйлийн бие хичээлийн кодыг агуулсан байх ёстой");
    }

    // A8: Гурван алдааны ангийг нэг параметржүүлсэн тестээр
    // lenient() ашигласан тайлбар: @CsvSource-ийн мөр бүр өөр stub хэрэглэдэг.
    // Mockito-гийн strict stubs горим ашиглагдаагүй stub-ийг UnnecessaryStubbingException-ээр
    // буцааж унагана. Тиймээс мөр бүрд тохирсон (ашиглагдаагүй байж болох) stub-уудыг
    // lenient() хэлбэрээр зарлаж, strict алдаанаас зайлсхийнэ.
    @ParameterizedTest
    @CsvSource({
        "UNKNOWN,   CS201, ERROR_NO_STUDENT",
        "INACTIVE,  CS201, ERROR_INACTIVE_STUDENT",
        "S006,      NOCOURSE, ERROR_NO_COURSE"
    })
    void register_errorCases_returnsExpectedError(String studentId, String courseId, String expected) {
        Student activeStudent = new Student("S006", "s6@must.edu.mn", true, List.of());
        Student inactiveStudent = new Student("INACTIVE", "inactive@must.edu.mn", false, List.of());

        lenient().when(repository.findStudent("UNKNOWN")).thenReturn(null);
        lenient().when(repository.findStudent("INACTIVE")).thenReturn(inactiveStudent);
        lenient().when(repository.findStudent("S006")).thenReturn(activeStudent);
        lenient().when(repository.courseExists("CS201")).thenReturn(true);
        lenient().when(repository.courseExists("NOCOURSE")).thenReturn(false);

        String result = service.register(studentId.trim(), courseId.trim());

        assertEquals(expected.trim(), result);
    }
}
