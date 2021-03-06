package pl.librus.client.data;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

import io.requery.Persistable;
import pl.librus.client.domain.Average;
import pl.librus.client.domain.event.Event;
import pl.librus.client.domain.event.EventCategory;
import pl.librus.client.domain.LibrusColor;
import pl.librus.client.domain.LuckyNumber;
import pl.librus.client.domain.Me;
import pl.librus.client.domain.PlainLesson;
import pl.librus.client.domain.Teacher;
import pl.librus.client.domain.announcement.Announcement;
import pl.librus.client.domain.attendance.Attendance;
import pl.librus.client.domain.attendance.AttendanceCategory;
import pl.librus.client.domain.grade.Grade;
import pl.librus.client.domain.grade.GradeCategory;
import pl.librus.client.domain.grade.GradeComment;
import pl.librus.client.domain.lesson.Lesson;
import pl.librus.client.domain.subject.Subject;

/**
 * Created by robwys on 11/02/2017.
 *
 */

public class EntityInfos {
    private final static Map<Class<? extends Persistable>, EntityInfo> infos = new ImmutableMap.Builder<Class<? extends Persistable>, EntityInfo>()
            .put(Announcement.class, EntityInfo.of("SchoolNotice"))
            .put(Attendance.class, EntityInfo.of("Attendance"))
            .put(AttendanceCategory.class, EntityInfo.builder()
                    .name("Type")
                    .endpointPrefix("Attendances")
                    .refreshDays(30)
                    .build())
            .put(Average.class, EntityInfo.builder()
                    .name("Average")
                    .endpointPrefix("Grades")
                    .build())
            .put(Event.class, EntityInfo.of("HomeWork"))
            .put(EventCategory.class, EntityInfo.builder()
                    .name("Category")
                    .pluralName("Categories")
                    .endpointPrefix("HomeWorks")
                    .refreshDays(30)
                    .build())
            .put(Grade.class, EntityInfo.of("Grade"))
            .put(GradeCategory.class, EntityInfo.builder()
                    .name("Category")
                    .pluralName("Categories")
                    .endpointPrefix("Grades")
                    .refreshDays(30)
                    .build())
            .put(GradeComment.class, EntityInfo.builder()
                    .name("Comment")
                    .endpointPrefix("Grades")
                    .build())
            .put(Me.class, EntityInfo.builder()
                    .name("Me")
                    .single(true)
                    .refreshDays(30)
                    .build())
            .put(LibrusColor.class, EntityInfo.builder()
                    .refreshDays(30)
                    .name("Color")
                    .build())
            .put(LuckyNumber.class, EntityInfo.builder()
                    .name("LuckyNumbers")
                    .topLevelName("LuckyNumber")
                    .single(true)
                    .build())
            .put(PlainLesson.class, EntityInfo.builder()
                    .refreshDays(30)
                    .name("Lesson")
                    .build())
            .put(Subject.class, EntityInfo.builder()
                    .name("Subject")
                    .refreshDays(30)
                    .build())
            .put(Teacher.class, EntityInfo.builder()
                    .name("User")
                    .refreshDays(30)
                    .build())
            .put(Lesson.class, EntityInfo.builder()
                    .name("Timetable")
                    .build())
            .build();

    public static EntityInfo infoFor(Class<? extends Persistable> clazz) {
        return infos.get(clazz);
    }

    public static Map<Class<? extends Persistable>, EntityInfo> all() {
        return infos;
    }
}
