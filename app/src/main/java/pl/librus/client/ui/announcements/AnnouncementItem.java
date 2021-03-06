package pl.librus.client.ui.announcements;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import java.util.List;
import java.util.Locale;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractSectionableItem;
import eu.davidea.viewholders.FlexibleViewHolder;
import pl.librus.client.R;
import pl.librus.client.data.Reader;
import pl.librus.client.domain.announcement.FullAnnouncement;
import pl.librus.client.util.LibrusUtils;

/**
 * Created by szyme on 28.12.2016. librus-client
 */

public class AnnouncementItem extends AbstractSectionableItem<AnnouncementItem.ViewHolder, AnnouncementHeaderItem> {
    private final FullAnnouncement announcement;
    private View backgroundView;
    private TextView title;

    public AnnouncementItem(FullAnnouncement announcement, AnnouncementHeaderItem header) {
        super(header);
        this.announcement = announcement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AnnouncementItem)) return false;

        AnnouncementItem that = (AnnouncementItem) o;

        return announcement.equals(that.announcement);

    }

    public FullAnnouncement getAnnouncement() {
        return announcement;
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, ViewHolder holder, int position, List payloads) {
        this.backgroundView = holder.background;
        this.title = holder.announcementSubject;

        holder.announcementSubject.setText(announcement.subject());
        holder.background.setTransitionName("announcement_background_" + announcement.id());
        LibrusUtils.setTextViewValue(holder.announcementTeacherName, announcement.addedByName());
        holder.announcementContent.setText(announcement.content());

        Reader reader = new Reader(holder.itemView.getContext());

        if (!reader.isRead(announcement))
            holder.announcementSubject.setTypeface(holder.announcementSubject.getTypeface(), Typeface.BOLD);
        else
            holder.announcementSubject.setTypeface(null, Typeface.NORMAL);
        if (announcement.startDate().isBefore(LocalDate.now().withDayOfWeek(DateTimeConstants.MONDAY)))
            holder.announcementDate.setText(announcement.startDate().toString("d MMM."));
        else
            holder.announcementDate.setText(announcement.startDate().dayOfWeek().getAsShortText(new Locale("pl")));
    }

    @Override
    public ViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        View v = inflater.inflate(R.layout.three_line_list_item, parent, false);
        return new ViewHolder(v, adapter);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.three_line_list_item;
    }

    @Override
    public int hashCode() {
        return announcement.hashCode();
    }

    View getBackgroundView() {
        return backgroundView;
    }

    public TextView getTitle() {
        return title;
    }

    class ViewHolder extends FlexibleViewHolder {
        public final RelativeLayout background;
        final TextView announcementTeacherName,
                announcementSubject,
                announcementContent,
                announcementDate;

        ViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            background = (RelativeLayout) view.findViewById(R.id.three_line_list_item_background);
            announcementSubject = (TextView) view.findViewById(R.id.three_line_list_item_title);
            announcementTeacherName = (TextView) view.findViewById(R.id.three_line_list_item_first);
            announcementContent = (TextView) view.findViewById(R.id.three_line_list_item_second);
            announcementDate = (TextView) view.findViewById(R.id.three_line_list_item_date);
        }
    }

    int getHeaderOrder() {
        return getHeader().getOrder();
    }

    LocalDate getStartDate() {
        return announcement.startDate();
    }

    @Override
    public String toString() {
        return announcement.toString();
    }
}
