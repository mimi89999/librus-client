package pl.librus.client.attendances;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import io.requery.Persistable;
import io.requery.sql.EntityDataStore;
import pl.librus.client.R;
import pl.librus.client.datamodel.Attendance;
import pl.librus.client.datamodel.AttendanceCategory;
import pl.librus.client.ui.MainApplication;
import pl.librus.client.ui.MainFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class AttendanceFragment extends MainFragment {

    private OnSetupCompleteListener listener;

    public AttendanceFragment() {
        // Required empty public constructor
    }

    public static AttendanceFragment newInstance() {
        return new AttendanceFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_attendance, container, false);
        //Setup RecyclerView
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.fragment_attendances_main_list);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FlexibleAdapter<AbstractFlexibleItem> adapter = new FlexibleAdapter<>(null);
        adapter.expandItemsAtStartUp();
        recyclerView.setAdapter(adapter);

        EntityDataStore<Persistable> data = MainApplication.getData();

        List<Attendance> attendances = data.select(Attendance.class).get().toList();
        Map<LocalDate, AttendanceHeaderItem> headerItemMap = new HashMap<>();
        for (Attendance attendance : attendances) {
            LocalDate date = attendance.date();

            AttendanceCategory category = MainApplication.getData()
                    .findByKey(AttendanceCategory.class, attendance.type().id());

            if (!category.presenceKind()) {
                if (headerItemMap.get(date) == null) {
                    headerItemMap.put(date, new AttendanceHeaderItem(date));
                }
                AttendanceItem subItem = new AttendanceItem(
                        headerItemMap.get(date),
                        attendance,
                        category);
                headerItemMap.get(date)
                        .addSubItem(subItem);
            }
        }
        List<AttendanceHeaderItem> headers = new ArrayList<>(headerItemMap.values());
        Collections.sort(headers);
        for (AttendanceHeaderItem headerItem : headers) {
            adapter.addSection(headerItem);
        }
        if (listener != null) listener.run();


        return root;
    }

    @Override
    public void setOnSetupCompleteListener(OnSetupCompleteListener listener) {
        this.listener = listener;

    }

    @Override
    public void removeListener() {
        this.listener = null;
    }
}
