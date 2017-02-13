package pl.librus.client.db;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.games.Game;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowNotification;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import java8.util.concurrent.CompletableFuture;
import java8.util.function.Consumer;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import pl.librus.client.api.APIClient;
import pl.librus.client.api.LibrusGcmListenerService;
import pl.librus.client.api.NotificationService;
import pl.librus.client.datamodel.Announcement;
import pl.librus.client.datamodel.Grade;
import pl.librus.client.datamodel.HasId;
import pl.librus.client.datamodel.ImmutableGrade;
import pl.librus.client.datamodel.Subject;
import pl.librus.client.datamodel.Teacher;
import pl.librus.client.sql.ImmutableEntityChange;
import pl.librus.client.sql.UpdateHelper;

import static com.google.common.collect.Lists.newArrayList;
import static java8.util.concurrent.CompletableFuture.completedFuture;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;
import static pl.librus.client.sql.EntityChange.Type.ADDED;
import static pl.librus.client.sql.EntityChange.Type.CHANGED;

@RunWith(RobolectricTestRunner.class)
public class NotificationTest extends BaseDBTest {

    private NotificationManager notificationManager;

    private APIClient apiClient;

    @Before
    public void setUp() {
        notificationManager = (NotificationManager) RuntimeEnvironment.application.getSystemService(Context.NOTIFICATION_SERVICE);
    }
    @Test
    public void shouldNotifyAboutNewGrade() throws ExecutionException, InterruptedException {
        //given
        Subject subject = EntityTemplates.subject();
        Grade newGrade = EntityTemplates.grade()
                .withSubject(HasId.of(subject.id()));
        LibrusGcmListenerService service = serviceWithMockClient();
        addMockGrades(newGrade);
        data.insert(subject);

        //when
        service.onMessageReceived(null, mockBundle());
        service.getReloads().get();

        //then
        ShadowNotification notification = singleNotification();
        assertThat(notification.getContentTitle(), is("Nowa ocena"));
        assertThat(notification.getContentText(), is("Matematyka 4+"));
    }

    @Test
    public void shouldNotifyAboutNewAnnouncement() throws ExecutionException, InterruptedException {
        //given
        Teacher teacher = EntityTemplates.teacher();
        Announcement newAnnouncement = EntityTemplates.announcement()
                .withAddedBy(HasId.of(teacher.id()));
        LibrusGcmListenerService service = serviceWithMockClient();
        addMockAnnouncements(newAnnouncement);

        data.insert(EntityTemplates.me());
        data.insert(teacher);

        //when
        service.onMessageReceived(null, mockBundle());
        service.getReloads().get();

        //then
        ShadowNotification notification = singleNotification();
        assertThat(notification.getContentTitle(), is("Tytuł ogłoszenia"));
        assertThat(notification.getContentText(), is("Treść ogłoszenia"));
        assertThat(notification.getBigContentTitle(), is("Tytuł ogłoszenia"));
        assertThat(notification.getBigContentText(), is("12u - Tomasz Problem"));
    }

    private ShadowNotification singleNotification() {
        List<Notification> all = shadowOf(notificationManager).getAllNotifications();
        Notification onlyOne = Iterables.getOnlyElement(all);
        return shadowOf(onlyOne);
    }

    private LibrusGcmListenerService serviceWithMockClient() {
        apiClient = mock(APIClient.class);
        LibrusGcmListenerService service = new LibrusGcmListenerService();
        service.setUpdateHelper(new UpdateHelper(apiClient));
        service.setFirebaseLogger(mock(Consumer.class));
        service.setNotificationService(new NotificationService(RuntimeEnvironment.application));
        when(apiClient.getAll(any()))
                .thenReturn(completedFuture(Collections.emptyList()));
        return service;
    }

    private void addMockGrades(Grade... grades) {
        when(apiClient.getAll(eq(Grade.class)))
                .thenReturn(completedFuture(newArrayList(grades)));
    }

    private void addMockAnnouncements(Announcement... announcements) {
        when(apiClient.getAll(eq(Announcement.class)))
                .thenReturn(completedFuture(newArrayList(announcements)));
    }

    private Bundle mockBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("objectT", "mock");
        return bundle;
    }
}
