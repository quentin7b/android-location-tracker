package fr.quentinklein.slt;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;

import fr.quentinklein.aslt.R;

/**
 * @author Quentin Klein <klein.quentin@gmail.com>
 *         <p>
 *         Helper for providers and locations
 *         </p>
 */
public class LocationUtils {

    /**
     * Check if the gps provider is enabled or not
     *
     * @param context any context
     * @return true if gps provider is enabled, false otherwise
     */
    public static boolean isGpsProviderEnabled(@NonNull Context context) {
        return isProviderEnabled(context, LocationManager.GPS_PROVIDER);

    }

    /**
     * Check if the network provider is enabled or not
     *
     * @param context any context
     * @return true if the network provider is enabled, false otherwise
     */
    public static boolean isNetworkProviderEnabled(@NonNull Context context) {
        return isProviderEnabled(context, LocationManager.NETWORK_PROVIDER);
    }

    /**
     * Check if the passive provider is enabled or not
     *
     * @param context any context
     * @return true if the passive provider is enabled, false otherwise
     */
    public static boolean isPassiveProviderEnabled(@NonNull Context context) {
        return isProviderEnabled(context, LocationManager.PASSIVE_PROVIDER);
    }

    /**
     * Build a dialog to ask the user to change his location settings
     *
     * @param context a UI context
     */
    @UiThread
    public static void askEnableProviders(@NonNull final Context context) {
        askEnableProviders(context, R.string.provider_settings_message);
    }

    /**
     * Build a dialog to ask the user to change his location settings
     *
     * @param context         a UI Context
     * @param messageResource the message to show to the user in the dialog
     */
    @UiThread
    public static void askEnableProviders(@NonNull final Context context, @StringRes int messageResource) {
        askEnableProviders(context, messageResource, R.string.provider_settings_yes, R.string.provider_settings_yes);
    }

    /**
     * Build a dialog to ask the user to change his location settings
     *
     * @param context               a UI Context
     * @param messageResource       the message to show to the user in the dialog
     * @param positiveLabelResource the positive button text
     * @param negativeLabelResource the negative button text
     */
    @UiThread
    public static void askEnableProviders(@NonNull final Context context, @StringRes int messageResource, @StringRes int positiveLabelResource, @StringRes int negativeLabelResource) {
        new AlertDialog.Builder(context)
                .setMessage(messageResource)
                .setCancelable(false)
                .setPositiveButton(positiveLabelResource, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(negativeLabelResource, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    /**
     * Check if the provider is enabled of not
     *
     * @param context  any context
     * @param provider the provider to check
     * @return true if the provider is enabled, false otherwise
     */
    private static boolean isProviderEnabled(@NonNull Context context, @NonNull String provider) {
        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(provider);
    }

}
