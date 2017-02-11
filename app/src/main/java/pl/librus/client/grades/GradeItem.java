package pl.librus.client.grades;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.viewholders.FlexibleViewHolder;
import pl.librus.client.R;
import pl.librus.client.api.Reader;
import pl.librus.client.datamodel.Grade;
import pl.librus.client.datamodel.GradeCategory;
import pl.librus.client.datamodel.LibrusColor;

class GradeItem extends AbstractFlexibleItem<GradeItem.ViewHolder> {
    private final Grade grade;
    private final GradeCategory gc;
    private final GradeHeaderItem header;
    private final LibrusColor color;

    GradeItem(GradeHeaderItem header, Grade grade, GradeCategory gc, LibrusColor color) {
        this.header = header;
        this.color = color;
        this.grade = grade;
        this.gc = gc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GradeItem gradeItem = (GradeItem) o;

        return grade.equals(gradeItem.grade);

    }

    @Override
    public int hashCode() {
        return grade.hashCode();
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, ViewHolder holder, int position, List payloads) {
        holder.grade.setText(grade.grade());
        holder.title.setText(gc.name());
        holder.subtitle.setText(grade.date().toString("EEEE, d MMMM", new Locale("pl")));
        holder.color.setBackgroundColor(color.colorInt());
        holder.unreadBadge.setVisibility(
                new Reader(holder.itemView.getContext()).isRead(grade) ? View.GONE : View.VISIBLE);
    }

    @Override
    public ViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.grade_item, parent, false), adapter);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.grade_item;
    }

    public Grade getGrade() {
        return grade;
    }

    GradeCategory getGradeCategory() {
        return gc;
    }

    public GradeHeaderItem getHeader() {
        return header;
    }


    class ViewHolder extends FlexibleViewHolder {

        private final ImageView unreadBadge;
        private final TextView grade, title, subtitle;
        private final View color;

        ViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            grade = (TextView) itemView.findViewById(R.id.grade_item_grade);
            title = (TextView) itemView.findViewById(R.id.grade_item_title);
            subtitle = (TextView) itemView.findViewById(R.id.grade_item_subtitle);
            unreadBadge = (ImageView) itemView.findViewById(R.id.grade_item_unread_badge);
            color = itemView.findViewById(R.id.grade_item_color);
        }
    }
}
