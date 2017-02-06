package pl.librus.client.attendances;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractSectionableItem;
import eu.davidea.viewholders.FlexibleViewHolder;
import io.requery.Persistable;
import io.requery.sql.EntityDataStore;
import pl.librus.client.R;
import pl.librus.client.datamodel.Attendance;
import pl.librus.client.datamodel.AttendanceCategory;
import pl.librus.client.datamodel.PlainLesson;
import pl.librus.client.datamodel.Subject;
import pl.librus.client.ui.MainApplication;

class AttendanceItem extends AbstractSectionableItem<AttendanceItem.ViewHolder, AttendanceHeaderItem> {
    private final AttendanceCategory category;
    private final Attendance attendance;

    AttendanceItem(AttendanceHeaderItem header, Attendance attendance, AttendanceCategory category) {
        super(header);
        this.attendance = attendance;
        this.category = category;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.attendance_item;
    }

    @Override
    public ViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, ViewHolder holder, int position, List payloads) {
        holder.shortName.setText(category.shortName());
        Context context = holder.itemView.getContext();
        String lessonNumber = context.getString(R.string.lesson) + " " + attendance.lessonNumber();
        holder.lesson.setText(lessonNumber);

        EntityDataStore<Persistable> data = MainApplication.getData();
        PlainLesson lesson = data.findByKey(PlainLesson.class, attendance.lesson().id());
        Subject subject = data.findByKey(Subject.class, lesson.subject().id());
        holder.subject.setText(subject.name());

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AttendanceItem that = (AttendanceItem) o;

        return attendance.equals(that.attendance);

    }

    @Override
    public int hashCode() {
        return attendance.hashCode();
    }

    public Attendance getAttendance() {
        return attendance;
    }

    public AttendanceCategory getCategory() {
        return category;
    }

    class ViewHolder extends FlexibleViewHolder {
        final TextView subject;
        final TextView lesson;
        final TextView shortName;

        public ViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            subject = (TextView) view.findViewById(R.id.attendance_item_lesson);
            lesson = (TextView) view.findViewById(R.id.attendance_item_lesson_number);
            shortName = (TextView) view.findViewById(R.id.attendance_item_shortType);
        }
    }
}
