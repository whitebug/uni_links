package name.avioli.unilinks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry;

/**
 * Flutter plugin for handling deep links and app links.
 * Supports both Android v1 and v2 embedding.
 */
public class UniLinksPlugin
        implements FlutterPlugin,
                MethodChannel.MethodCallHandler,
                EventChannel.StreamHandler,
                ActivityAware,
                PluginRegistry.NewIntentListener {

    private static final String MESSAGES_CHANNEL = "uni_links/messages";
    private static final String EVENTS_CHANNEL = "uni_links/events";

    @Nullable
    private BroadcastReceiver changeReceiver;

    @Nullable
    private String initialLink;
    
    @Nullable
    private String latestLink;
    
    @Nullable
    private Context context;
    
    private boolean initialIntent = true;

    private void handleIntent(@NonNull Context context, @NonNull Intent intent) {
        String action = intent.getAction();
        String dataString = intent.getDataString();

        if (Intent.ACTION_VIEW.equals(action) && dataString != null) {
            if (initialIntent) {
                initialLink = dataString;
                initialIntent = false;
            }
            latestLink = dataString;
            if (changeReceiver != null) {
                changeReceiver.onReceive(context, intent);
            }
        }
    }

    @NonNull
    private BroadcastReceiver createChangeReceiver(@NonNull final EventChannel.EventSink events) {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(@NonNull Context context, @NonNull Intent intent) {
                String dataString = intent.getDataString();

                if (dataString == null) {
                    events.error("UNAVAILABLE", "Link unavailable", null);
                } else {
                    events.success(dataString);
                }
            }
        };
    }

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        this.context = flutterPluginBinding.getApplicationContext();
        register(flutterPluginBinding.getBinaryMessenger(), this);
    }

    private static void register(@NonNull BinaryMessenger messenger, @NonNull UniLinksPlugin plugin) {
        final MethodChannel methodChannel = new MethodChannel(messenger, MESSAGES_CHANNEL);
        methodChannel.setMethodCallHandler(plugin);

        final EventChannel eventChannel = new EventChannel(messenger, EVENTS_CHANNEL);
        eventChannel.setStreamHandler(plugin);
    }

    /**
     * Plugin registration for v1 embedding.
     * @deprecated Use v2 embedding instead
     */
    @Deprecated
    public static void registerWith(@NonNull PluginRegistry.Registrar registrar) {
        // Detect if we've been launched in background
        if (registrar.activity() == null) {
            return;
        }

        final UniLinksPlugin instance = new UniLinksPlugin();
        instance.context = registrar.context();
        register(registrar.messenger(), instance);

        instance.handleIntent(registrar.context(), registrar.activity().getIntent());
        registrar.addNewIntentListener(instance);
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        this.context = null;
    }

    @Override
    public void onListen(@Nullable Object o, @NonNull EventChannel.EventSink eventSink) {
        changeReceiver = createChangeReceiver(eventSink);
    }

    @Override
    public void onCancel(@Nullable Object o) {
        changeReceiver = null;
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        switch (call.method) {
            case "getInitialLink":
                result.success(initialLink);
                break;
            case "getLatestLink":
                result.success(latestLink);
                break;
            default:
                result.notImplemented();
                break;
        }
    }

    @Override
    public boolean onNewIntent(@NonNull Intent intent) {
        if (context != null) {
            this.handleIntent(context, intent);
        }
        return false;
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding activityPluginBinding) {
        activityPluginBinding.addOnNewIntentListener(this);
        if (context != null) {
            this.handleIntent(this.context, activityPluginBinding.getActivity().getIntent());
        }
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {}

    @Override
    public void onReattachedToActivityForConfigChanges(
            @NonNull ActivityPluginBinding activityPluginBinding) {
        activityPluginBinding.addOnNewIntentListener(this);
        if (context != null) {
            this.handleIntent(this.context, activityPluginBinding.getActivity().getIntent());
        }
    }

    @Override
    public void onDetachedFromActivity() {}
}
