package ahmed.example.com.chatapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import ahmed.example.com.chatapp.R;
import ahmed.example.com.chatapp.ui.LoginActivity;

/**
 * Implementation of App Widget functionality.
 */
public class ChatAppWidget extends AppWidgetProvider {

    private static RemoteViews views;


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId
    ) {

        views = new RemoteViews(context.getPackageName(), R.layout.chat_app_widget);
        Intent intent = new Intent(context,
                                   LoginActivity.class
        );
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.container, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);


    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);

        }
        Intent intent = new Intent(context, MyIntentService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        context.startService(intent);
//        context.registerReceiver(broadcastReceiver, new IntentFilter(MyIntentService.BROADCAST));

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

