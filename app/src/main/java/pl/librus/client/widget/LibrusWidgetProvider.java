package pl.librus.client.widget;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

import pl.librus.client.R;
import pl.librus.client.api.LibrusData;
import pl.librus.client.api.LuckyNumber;

public class LibrusWidgetProvider extends AppWidgetProvider {
    LuckyNumber luckyNumber;
    LibrusData librusData;


    public void onEnabled(Context context) {
        String LuckyNumber = luckyNumber.getLuckyNumberDay().toString();
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.librus_widget);
        remoteViews.setTextViewText(R.id.intLN, LuckyNumber);


    }
    //TODO: Load properly widget
}