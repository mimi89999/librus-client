package pl.librus.client;

import com.google.common.collect.Iterables;
import com.google.common.io.Resources;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import io.requery.Persistable;
import pl.librus.client.data.EntityInfos;
import pl.librus.client.data.server.EntityParser;
import pl.librus.client.data.server.ParseException;
import pl.librus.client.domain.Average;
import pl.librus.client.domain.ImmutableAverage;
import pl.librus.client.domain.ImmutableLibrusAccount;
import pl.librus.client.domain.ImmutableLibrusColor;
import pl.librus.client.domain.ImmutableLuckyNumber;
import pl.librus.client.domain.ImmutableMe;
import pl.librus.client.domain.ImmutablePlainLesson;
import pl.librus.client.domain.ImmutableTeacher;
import pl.librus.client.domain.LibrusAccount;
import pl.librus.client.domain.LibrusColor;
import pl.librus.client.domain.LuckyNumber;
import pl.librus.client.domain.Me;
import pl.librus.client.domain.PlainLesson;
import pl.librus.client.domain.Teacher;
import pl.librus.client.domain.announcement.Announcement;
import pl.librus.client.domain.announcement.ImmutableAnnouncement;
import pl.librus.client.domain.attendance.Attendance;
import pl.librus.client.domain.attendance.AttendanceCategory;
import pl.librus.client.domain.attendance.ImmutableAttendance;
import pl.librus.client.domain.attendance.ImmutableAttendanceCategory;
import pl.librus.client.domain.event.Event;
import pl.librus.client.domain.event.EventCategory;
import pl.librus.client.domain.event.ImmutableEvent;
import pl.librus.client.domain.event.ImmutableEventCategory;
import pl.librus.client.domain.grade.Grade;
import pl.librus.client.domain.grade.GradeCategory;
import pl.librus.client.domain.grade.GradeComment;
import pl.librus.client.domain.grade.ImmutableGrade;
import pl.librus.client.domain.grade.ImmutableGradeCategory;
import pl.librus.client.domain.grade.ImmutableGradeComment;
import pl.librus.client.domain.lesson.ImmutableJsonLesson;
import pl.librus.client.domain.lesson.ImmutableLessonSubject;
import pl.librus.client.domain.lesson.ImmutableLessonTeacher;
import pl.librus.client.domain.lesson.JsonLesson;
import pl.librus.client.domain.lesson.Timetable;
import pl.librus.client.domain.subject.ImmutableSubject;
import pl.librus.client.domain.subject.Subject;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


public class EntityParserTest {

    @Test
    public void shouldParseTeachers() throws IOException {
        //when
        List<Teacher> res = parse("Teachers.json", Teacher.class);

        //then
        assertThat(res, hasItem(ImmutableTeacher.builder()
                .firstName("Tomasz")
                .lastName("Problem")
                .id("12345")
                .build()));

        assertThat(res, hasItem(ImmutableTeacher.builder()
                .id("666")
                .schoolAdministrator(true)
                .build()));
    }

    @Test
    public void shouldParseMe() throws IOException {
        //when
        List<Me> res = parse("Me.json", Me.class);

        //then
        LibrusAccount expectedAccount = ImmutableLibrusAccount.builder()
                .firstName("Tomasz")
                .lastName("Problem")
                .login("12u")
                .build();
        assertThat(Iterables.getOnlyElement(res), is(ImmutableMe.of(expectedAccount)));
    }

    @Test
    public void shouldParseTimetable() throws IOException {
        //when
        List<Timetable> res = EntityParser.parseList(readFile("Timetable.json"), "Timetable", Timetable.class);

        //then
        JsonLesson actual = Iterables.getOnlyElement(res).get(LocalDate.parse("2017-01-30"))
                .get(1).get(0);
        JsonLesson expected = ImmutableJsonLesson.builder()
                .cancelled(false)
                .dayNo(1)
                .hourFrom(LocalTime.parse("08:00"))
                .hourTo(LocalTime.parse("08:45"))
                .lessonNo(1)
                .subject(ImmutableLessonSubject.builder()
                        .id("44561")
                        .name("Godzina wychowawcza")
                        .build())
                .substitutionClass(true)
                .teacher(ImmutableLessonTeacher.builder()
                        .id("1235088")
                        .firstName("Tomasz")
                        .lastName("Problem")
                        .build())
                .substitutionClass(true)
                .orgDate(LocalDate.parse("2017-02-06"))
                .orgLessonNo(2)
                .orgLesson("1822337")
                .orgSubject("44565")
                .orgTeacherId("1235090")
                .build();
        assertEquals(expected, actual);

    }

    @Test
    public void shouldParseGrades() throws IOException {
        //when
        List<Grade> res = parse("Grades.json", Grade.class);

        //then
        assertThat(res, hasItem(ImmutableGrade.builder()
                .addDate(LocalDateTime.parse("2016-09-29T08:30:41"))
                .addedById("1235106")
                .categoryId("164150")
                .addCommentIds("834777")
                .date(LocalDate.parse("2016-09-29"))
                .finalPropositionType(false)
                .finalType(false)
                .grade("np")
                .id("2539299")
                .lessonId("2732545")
                .subjectId("44569")
                .categoryId("392450")
                .semester(1)
                .semesterPropositionType(false)
                .semesterType(false)
                .studentId("1402361")
                .build()));
    }

    @Test
    public void shouldParseCategories() throws IOException {
        //when
        List<GradeCategory> res = parse("GradeCategories.json", GradeCategory.class);

        //then
        assertThat(res, hasItem(ImmutableGradeCategory.builder()
                .name("sprawdzian")
                .id("164149")
                .colorId("26")
                .weight(7)
                .build()));
    }

    @Test
    public void shouldParseComment() throws IOException {
        //when
        List<GradeComment> res = parse("GradeComments.json", GradeComment.class);

        //then
        assertThat(res, hasItem(ImmutableGradeComment.builder()
                .id("834777")
                .text("Srodki artystycznego wyrazu")
                .addedBy("1235106")
                .build()));
    }

    @Test
    public void shouldParseLessons() throws IOException {
        //when
        List<PlainLesson> res = parse("Lessons.json", PlainLesson.class);

        //then
        assertThat(res, hasItem(ImmutablePlainLesson.builder()
                .id("1822337")
                .teacher("1235090")
                .subject("44565")
                .build()));
    }

    @Test
    public void shouldParseHomeWorks() throws IOException {
        //when
        List<Event> res = parse("HomeWorks.json", Event.class);

        //then
        assertThat(res, hasItem(ImmutableEvent.builder()
                .category("7323")
                .addedBy("1235072")
                .date(LocalDate.parse("2016-10-07"))
                .id("1810676")
                .content("Węglowodory.")
                .lessonNo(5)
                .build()));
    }

    @Test
    public void shouldParseHomeWorkCategories() throws IOException {
        //when
        List<EventCategory> res = parse("HomeWorkCategories.json", EventCategory.class);

        //then
        assertThat(res, hasItem(ImmutableEventCategory.builder()
                .id("7789")
                .name("praca klasowa")
                .build()));
    }

    @Test
    public void shouldParseAttendances() throws IOException {
        //when
        List<Attendance> res = parse("Attendances.json", Attendance.class);

        //then
        assertThat(res, hasItem(ImmutableAttendance.builder()
                .id("t403209")
                .lessonId("2714880")
                .date(LocalDate.parse("2016-09-29"))
                .addDate(LocalDateTime.parse("2016-09-28T16:19:22"))
                .lessonNumber(3)
                .semester(1)
                .categoryId("100")
                .addedById("1234988")
                .build()));
    }

    @Test
    public void shouldParseAttendanceTypes() throws IOException {
        //when
        List<AttendanceCategory> res = parse("AttendanceTypes.json", AttendanceCategory.class);

        //then
        assertThat(res, hasItem(ImmutableAttendanceCategory.builder()
                .name("Spóźnienie")
                .id("2")
                .shortName("sp")
                .presenceKind(true)
                .build()));
    }

    @Test
    public void shouldParseSubject() throws IOException {
        //when
        List<Subject> res = parse("Subjects.json", Subject.class);

        //then
        assertThat(res, hasItem(ImmutableSubject.builder()
                .id("44908")
                .name("Matematyka i media")
                .build()));
    }

    @Test
    public void shouldParseLuckyNumbers() throws IOException {
        //when
        List<LuckyNumber> luckyNumbers = parse("LuckyNumbers.json", LuckyNumber.class);

        //then
        assertThat(Iterables.getOnlyElement(luckyNumbers), is(ImmutableLuckyNumber.of(
                LocalDate.parse("2017-02-03"),
                13
        )));
    }

    @Test
    public void shouldParseAverages() {
        //when
        List<Average> averages = parse("Averages.json", Average.class);

        //then
        assertThat(averages, hasItem(ImmutableAverage.builder()
                .subject("44555")
                .fullYear(4.26)
                .semester1(4.29)
                .semester2(4.17)
                .build()));
    }

    @Test
    public void shouldParseColors() {
        //when
        List<LibrusColor> colors = parse("Colors.json", LibrusColor.class);
        //then
        LibrusColor goldenrod = ImmutableLibrusColor.builder()
                .id("13")
                .name("goldenrod")
                .rawColor("DAA520")
                .build();
        assertThat(colors, hasItem(goldenrod));
    }

    @Test
    public void shouldParseAnnouncements() {
        //when
        List<Announcement> res = parse("SchoolNotices.json", Announcement.class);

        //then
        assertThat(res, hasItem(ImmutableAnnouncement.builder()
                .id("167110")
                .startDate(LocalDate.parse("2016-09-21"))
                .endDate(LocalDate.parse("2017-06-14"))
                .subject("Konsultacje z \"matematyki\"")
                .content("Konsultacje z matematyki dla uczniów klas: 1B  2D2A 3A3D")
                .addedById("1575831")
                .build()));
    }

    @Test
    public void shouldNotFailOnDisabled() {
        //when
        List<Average> res = parse("Disabled.json", Average.class);

        //then
        assertThat(res, hasSize(0));
    }

    @Test
    public void shouldNotFailOnNotActive() {
        //when
        List<LuckyNumber> res = parse("NotActive.json", LuckyNumber.class);

        //then
        assertThat(res, hasSize(0));
    }

    @Test(expected = ParseException.class)
    public void shouldFailOnMalformed() {
        //when
        parse("Malformed.json", Average.class);
    }

    @Test
    public void shouldDiscoverMaintenance() {
        //when
        boolean maintenance = EntityParser.isMaintenance(readFile("Maintenance.json"));
        assertTrue(maintenance);
    }

    @Test
    public void shouldNotDiscoverMaintenance() {
        //when
        boolean maintenance = EntityParser.isMaintenance(readFile("SchoolNotices.json"));
        assertFalse(maintenance);
    }

    private static String readFile(String fileName) {
        try {
            return Resources.toString(Resources.getResource(fileName), Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private <T extends Persistable> List<T> parse(String filename, Class<T> clazz) {
        return EntityParser.parseList(readFile(filename), EntityInfos.infoFor(clazz).topLevelName(), clazz);
    }

}
