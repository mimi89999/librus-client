package pl.librus.client.db;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.common.collect.Iterables;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ResourceHelper;
import org.robolectric.shadows.ShadowNotification;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import java8.util.function.Consumer;
import java8.util.stream.IntStreams;
import pl.librus.client.api.APIClient;
import pl.librus.client.api.LibrusGcmListenerService;
import pl.librus.client.api.NotificationService;
import pl.librus.client.datamodel.Announcement;
import pl.librus.client.datamodel.Grade;
import pl.librus.client.datamodel.HasId;
import pl.librus.client.datamodel.Subject;
import pl.librus.client.sql.UpdateHelper;

import static com.google.common.collect.Lists.newArrayList;
import static java8.util.concurrent.CompletableFuture.completedFuture;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.LOLLIPOP)
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
        ShadowNotification notification = shadowOf(singleNotification());
        assertThat(notification.getContentTitle(), is("Nowa ocena"));
        assertThat(notification.getContentText(), is("Matematyka 4+"));
    }

    @Test
    public void shouldNotifyAboutNewAnnouncement() throws ExecutionException, InterruptedException {
        //given
        Announcement newAnnouncement = EntityTemplates.announcement();
        LibrusGcmListenerService service = serviceWithMockClient();
        addMockAnnouncements(newAnnouncement);

        //when
        service.onMessageReceived(null, mockBundle());
        service.getReloads().get();

        //then
        ShadowNotification notification = shadowOf(singleNotification());
        assertThat(notification.getContentTitle(), is("Tytuł ogłoszenia"));
        assertThat(notification.getContentText(), is("Treść ogłoszenia"));
        assertThat(notification.getBigContentTitle(), is("Tytuł ogłoszenia"));
        assertThat(notification.getBigText(), is("Treść ogłoszenia"));
    }

    @Test
    public void shouldNotifyAboutNewAnnouncements() throws ExecutionException, InterruptedException {
        //given
        Announcement[] newAnnouncements = IntStreams.range(1, 11)
                .mapToObj(index -> EntityTemplates.announcement()
                        .withId(String.valueOf(index))
                        .withSubject("Ogłoszenie #" + index))
                .toArray(Announcement[]::new);
        LibrusGcmListenerService service = serviceWithMockClient();

        addMockAnnouncements(newAnnouncements);

        //when
        service.onMessageReceived(null, mockBundle());
        service.getReloads().get();

        //then
        Notification realNotification = singleNotification();
        ShadowNotification notification = shadowOf(realNotification);

        assertThat(notification.getContentTitle(), is("10 nowych ogłoszeń"));
        assertThat(notification.getContentText().toString(), startsWith("Ogłoszenie #1, Ogłoszenie #2"));
        assertThat(notification.getBigContentTitle(), is("10 nowych ogłoszeń"));
        assertThat(getInboxLine(realNotification,0),is("Ogłoszenie #1"));
        assertThat(getInboxLine(realNotification,1),is("Ogłoszenie #2"));
    }

    private String getInboxLine(Notification n,int line) {
        //noinspection deprecation
        View view = n.bigContentView.apply(RuntimeEnvironment.application, new FrameLayout(RuntimeEnvironment.application));
        int internalResourceId = ResourceHelper.getInternalResourceId("inbox_text" + line);
        TextView lineView = (TextView) view.findViewById(internalResourceId);
        return lineView.getText().toString();
    }

    private Notification singleNotification() {
        List<Notification> all = shadowOf(notificationManager).getAllNotifications();
        return Iterables.getOnlyElement(all);
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
