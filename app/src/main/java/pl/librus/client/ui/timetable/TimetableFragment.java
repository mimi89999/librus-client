package pl.librus.client.ui.timetable;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import org.joda.time.LocalDate;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;
import eu.davidea.flexibleadapter.common.TopSnappedSmoothScroller;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.flexibleadapter.items.IHeader;
import java8.util.stream.Collectors;
import java8.util.stream.RefStreams;
import java8.util.stream.Stream;
import java8.util.stream.StreamSupport;
import pl.librus.client.MainApplication;
import pl.librus.client.R;
import pl.librus.client.domain.Teacher;
import pl.librus.client.domain.grade.FullGrade;
import pl.librus.client.domain.lesson.FullLesson;
import pl.librus.client.domain.lesson.Lesson;
import pl.librus.client.domain.lesson.SchoolWeek;
import pl.librus.client.presentation.MainFragmentPresenter;
import pl.librus.client.presentation.TimetablePresenter;
import pl.librus.client.ui.MainFragment;

public class TimetableFragment extends MainFragment implements TimetableView {
    private final ProgressItem progressItem = new ProgressItem();
    private TimetableAdapter adapter;
    private SwipeRefreshLayout refreshLayout;

    @Inject
    TimetablePresenter presenter;
    private RecyclerView recyclerView;

    public TimetableFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        TopSnappedSmoothScroller.MILLISECONDS_PER_INCH = 15f;

        View root = inflater.inflate(R.layout.fragment_timetable, container, false);

        recyclerView = (RecyclerView) root.findViewById(R.id.fragment_timetable_recycler);
        refreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.fragment_timetable_refresh_layout);

        recyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(getContext()));

        refreshLayout.setColorSchemeResources(R.color.md_blue_grey_400, R.color.md_blue_grey_500, R.color.md_blue_grey_600);

        adapter = new TimetableAdapter(null);

        adapter.setDisplayHeadersAtStartUp(true);
        adapter.setEndlessProgressItem(progressItem);

        adapter.onLoadMoreListener = () -> {
            progressItem.setStatus(ProgressItem.LOADING);
            adapter.notifyItemChanged(adapter.getGlobalPositionOf(progressItem));
            presenter.loadMore();
        };

        adapter.mItemClickListener = this::onItemClick;
        recyclerView.setAdapter(adapter);

        return root;
    }

    @Override
    protected void injectPresenter() {
        MainApplication.getMainActivityComponent()
                .inject(this);
        refreshLayout.setOnRefreshListener(presenter::reload);
        presenter.attachView(this);
    }

    @Override
    protected MainFragmentPresenter getPresenter() {
        return presenter;
    }

    @Override
    public void setProgress(boolean enabled) {
        progressItem.setStatus(enabled ? ProgressItem.LOADING : ProgressItem.IDLE);
    }

    public boolean onItemClick(int position) {
        IFlexible item = adapter.getItem(position);
        if (item instanceof LessonItem) {
            Lesson lesson = ((LessonItem) item).getLesson();
            presenter.lessonClicked(lesson);
            return true;
        } else {
            return false;
        }
    }

    private Stream<IFlexible> mapLessonsForWeek(SchoolWeek schoolWeek) {
        Stream.Builder<IFlexible> builder = RefStreams.builder();

        Map<LocalDate, List<Lesson>> days = StreamSupport.stream(schoolWeek.lessons())
                .collect(Collectors.groupingBy(Lesson::date));
        for (LocalDate date = schoolWeek.weekStart(); date.isBefore(schoolWeek.weekStart().plusWeeks(1)); date = date.plusDays(1)) {
            LessonHeaderItem header = new LessonHeaderItem(date);
            List<Lesson> schoolDay = days.get(date);
            if (schoolDay == null || schoolDay.isEmpty()) {
                builder.add(new EmptyDayItem(header, date));
            } else {
                ImmutableMap<Integer, Lesson> lessonMap = Maps.uniqueIndex(schoolDay, Lesson::lessonNo);

                int maxLessonNumber = Collections.max(lessonMap.keySet());
                int minLessonNumber = Collections.min(lessonMap.keySet());

                minLessonNumber = Math.min(1, minLessonNumber);

                for (int l = minLessonNumber; l <= maxLessonNumber; l++) {
                    Lesson lesson = lessonMap.get(l);
                    if (lesson != null) {
                        builder.add(new LessonItem(header, lesson, getContext()));
                    } else {
                        builder.add(new MissingLessonItem(header, l));
                    }
                }
            }
        }
        return builder.build();
    }

    @Override
    public void display(List<SchoolWeek> content) {
        adapter.clear();
        List<IFlexible> elements = StreamSupport.stream(content)
                .flatMap(this::mapLessonsForWeek)
                .collect(Collectors.toList());

        adapter.addItems(0, elements);
    }

    @Override
    public void displayMore(SchoolWeek schoolWeek) {
        adapter.onLoadMoreComplete(mapLessonsForWeek(schoolWeek)
                .collect(Collectors.toList()));
    }

    @Override
    public void scrollToDay(LocalDate day) {
        //Scroll to default position after a delay to let recyclerview complete layout
        IHeader header = StreamSupport.stream(adapter.getHeaderItems())
                .filter(h -> ((LessonHeaderItem) h).getDate().equals(day))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No header item for " + day));
        new Handler().postDelayed(() -> recyclerView.smoothScrollToPosition(adapter.getGlobalPositionOf(header)), 50);
    }

    @Override
    public void displayDetails(FullLesson lesson) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getContext()).title(lesson.subject().name()).positiveText("Zamknij");

        LayoutInflater inflater = LayoutInflater.from(getContext());
        android.view.View details = inflater.inflate(R.layout.lesson_details, null);

        ViewGroup eventContainer = (ViewGroup) details.findViewById(R.id.lesson_details_event_container);
        ViewGroup teacherContainer = (ViewGroup) details.findViewById(R.id.lesson_details_teacher_container);

        TextView teacherTextView = (TextView) details.findViewById(R.id.lesson_details_teacher_value);
        TextView dateTextView = (TextView) details.findViewById(R.id.lesson_details_date_value);
        TextView timeTextView = (TextView) details.findViewById(R.id.lesson_details_time_value);
        TextView eventTextView = (TextView) details.findViewById(R.id.lesson_details_event_value);

        dateTextView.setText(new SpannableStringBuilder()
                .append(lesson.date().toString("EEEE, d MMMM yyyy", new Locale("pl")),
                        new StyleSpan(Typeface.BOLD), Spanned.SPAN_INCLUSIVE_INCLUSIVE));
        SpannableStringBuilder timeSSB = new SpannableStringBuilder();
        if (lesson.hourFrom().isPresent() && lesson.hourTo().isPresent()) {
            timeSSB.append(lesson.hourFrom().get().toString("HH:mm"))
                    .append(" - ")
                    .append(lesson.hourTo().get().toString("HH:mm"))
                    .append(' ');
        }
        timeSSB.append(String.valueOf(lesson.lessonNo()),
                new StyleSpan(Typeface.BOLD), Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                .append(". lekcja", new StyleSpan(Typeface.BOLD), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        timeTextView.setText(timeSSB);

        //TODO add Events
        if (lesson.teacher().name().isPresent()) {
            teacherContainer.setVisibility(android.view.View.VISIBLE);
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            if (lesson.substitutionClass() && lesson.orgTeacher().isPresent()) {
                Teacher orgTeacher = lesson.orgTeacher().get();
                if (orgTeacher.name().isPresent()) {
                    ssb
                            .append(orgTeacher.name().get())
                            .append(" -> ");
                }
            }
            ssb.append(lesson.teacher().name().get(),
                    new StyleSpan(Typeface.BOLD), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            teacherTextView.setText(ssb);
        } else {
            teacherContainer.setVisibility(android.view.View.GONE);
        }

        eventContainer.setVisibility(android.view.View.GONE);

        //TODO Ogarnianie odwołań
        builder.customView(details, true).show();
    }

    @Override
    public void setRefreshing(boolean b) {
        refreshLayout.setRefreshing(b);
    }
}