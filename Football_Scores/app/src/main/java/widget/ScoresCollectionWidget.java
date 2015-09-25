package widget;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import barqsoft.footballscores.R;

/**
 * Implementation of App Widget functionality.
 */
public class ScoresCollectionWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int id: appWidgetIds) {
            updateAppWidget(context, appWidgetManager, id);
        }
    }


    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.score_widget_list);
//            CharSequence widgetText = new Date().toString();
//            views.setTextViewText(R.id.appwidget_text, widgetText);

            Intent serviceIntent = new Intent(context, WidgetRemoteViewsService.class);
//            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
//            serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME))); // embed extras so they don't get ignored

            remoteViews.setRemoteAdapter(appWidgetId, R.id.listView, serviceIntent);
//            remoteViews.setEmptyView(R.id.stackWidgetView, R.id.stackWidgetEmptyView);

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }
}

