package pl.librus.client.db;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import pl.librus.client.datamodel.Announcement;
import pl.librus.client.datamodel.Grade;
import pl.librus.client.datamodel.HasId;
import pl.librus.client.datamodel.ImmutableAnnouncement;
import pl.librus.client.datamodel.ImmutableGrade;
import pl.librus.client.datamodel.ImmutableMe;
import pl.librus.client.datamodel.ImmutableSubject;
import pl.librus.client.datamodel.LibrusAccount;
import pl.librus.client.datamodel.MultipleIds;
import pl.librus.client.datamodel.Subject;
import pl.librus.client.datamodel.Teacher;

/**
 * Created by robwys on 12/02/2017.
 */

public class EntityTemplates {
    public static ImmutableGrade grade() {
        return new Grade.Builder()
                .date(LocalDate.now())
                .addDate(LocalDateTime.now())
                .addedBy(HasId.of("12"))
                .category(HasId.of("34"))
                .finalPropositionType(false)
                .finalType(false)
                .grade("4+")
                .id("45632")
                .lesson(HasId.of("56"))
                .semester(1)
                .semesterPropositionType(false)
                .semesterType(false)
                .subject(HasId.of(subject().id()))
                .comments(MultipleIds.fromIds(Lists.newArrayList("777", "888")))
                .student(HasId.of("77779"))
                .build();
    }

    public static ImmutableSubject subject() {
        return new Subject.Builder()
                .id("123")
                .name("Matematyka")
                .build();
    }


    public static ImmutableAnnouncement announcement() {
        return new Announcement.Builder()
                .id("167110")
                .startDate(LocalDate.parse("2016-09-21"))
                .endDate(LocalDate.parse("2017-06-14"))
                .subject("Tytuł ogłoszenia")
                .content("Treść ogłoszenia")
                .addedBy(HasId.of("1575831"))
                .build();
    }

    public static ImmutableMe me() {
        return ImmutableMe.of(new LibrusAccount.Builder()
                .email("tompro@gmail.com")
                .firstName("Tomasz")
                .lastName("Problem")
                .login("12u")
                .build());
    }

    public static Teacher teacher() {
        return new Teacher.Builder()
                .firstName("Ala")
                .lastName("Makota")
                .id("12345")
                .build();
    }
}
