package widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import barqsoft.footballscores.R;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ScoresWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ScoreRemoteViewsFactory(getApplicationContext(), intent);
    }
}

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
class ScoreRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private final Context mContext;
    private final Intent mIntent;

    public ScoreRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mIntent = intent;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        return new RemoteViews(mContext.getPackageName(), R.layout.score_widget_list_item);
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}