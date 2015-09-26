package widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class WidgetRemoteViewsService extends RemoteViewsService {
    public final String LOG_TAG = WidgetRemoteViewsService.class.getSimpleName();
    private static final String[] SCORE_COLUMNS = {
            DatabaseContract.scores_table.HOME_COL,
            DatabaseContract.scores_table.AWAY_COL,
            DatabaseContract.scores_table.HOME_GOALS_COL,
            DatabaseContract.scores_table.AWAY_GOALS_COL
    };
    // these indices must match the projection
    static final int INDEX_TEAM_HOME = 0;
    static final int INDEX_TEAM_AWAY = 1;
    static final int INDEX_GOALS_HOME = 2;
    static final int INDEX_GOALS_AWAY = 3;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }

                /* This method is called by the app hosting the widget (e.g., the launcher)
                However, our ContentProvider is not exported so it doesn't have access to the
                data. Therefore we need to clear (and finally restore) the calling identity so
                that calls use our process and permission */
                final long identityToken = Binder.clearCallingIdentity();
                String date = new SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis());
                data = getContentResolver().query(DatabaseContract.scores_table.buildScoreWithDate(),
                        SCORE_COLUMNS, null, new String[]{date}, null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.score_widget_list_item);

//                /* Set content descriptions where supported */
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
//                    views.setContentDescription(R.id.widget_icon, description);
//                }

                String homeTeam = data.getString(INDEX_TEAM_HOME);
                String awayTeam = data.getString(INDEX_TEAM_AWAY);
                int homeTeamRawScore = data.getInt(INDEX_GOALS_HOME);
                int awayTeamRawScore = data.getInt(INDEX_GOALS_AWAY);
                String homeTeamScore = homeTeamRawScore >= 0 ? Integer.toString(homeTeamRawScore) : "-";
                String awayTeamScore = awayTeamRawScore >= 0 ? Integer.toString(awayTeamRawScore) : "-";
                views.setTextViewText(R.id.widget_home, homeTeam);
                views.setTextViewText(R.id.widget_away, awayTeam);
                views.setTextViewText(R.id.widget_home_score, homeTeamScore);
                views.setTextViewText(R.id.widget_away_score, awayTeamScore);

//                /* When user clicks on this score-view it should take them to associated
//                activity */
//                final Intent fillInIntent = new Intent();
//                Uri uri = DatabaseContract.scores_table.buildUriHere()
//                fillInIntent.setData(uri);
                views.setOnClickFillInIntent(R.id.scores_widget_list_item, new Intent());

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.score_widget_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {

                /* TODO: Use stable Ids
                Stable IDs allow the ListView to optimize for the case when items remain the
                same between notifyDataSetChanged calls.
                http://stackoverflow .com/questions/18217416/android-what-is-the-meaning-of-stableids
                See example below. Remember to also return true from hasStableIds() */
//                if (data.moveToPosition(position))
//                    return data.getLong(INDEX_SCORE_ID);

                return position;
            }

            @Override
            public boolean hasStableIds() {
                return false;
            }
        };
    }
}
