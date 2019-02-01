package hsquad.greencityserver.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import hsquad.greencityserver.Model.Request;
import hsquad.greencityserver.Model.User;
import hsquad.greencityserver.Remote.APIService;
import hsquad.greencityserver.Remote.RetrofitClient;
import retrofit2.Retrofit;

public class Common {

    public static User currentUser;
    public static Request currentRequest;

    public static final String UPDATE = "Update";
    public static final String DELETE = "Delete";

    public static String PHONE_TEXT = "userPhone";

    public static final String fcmUrl = "https://fcm.googleapis.com/";

    public static final int PICK_IMAGE_REQUEST = 71;

    public static APIService getFCMClient(){
        return RetrofitClient.getClient(fcmUrl).create(APIService.class);
    }

    public static String convertCodeToStatus(String code){
        if(code.equals("0"))
            return "Placed";
        else if(code.equals("1"))
            return "On my way";
        else
            return "Shipped";
    }

    public static boolean isConnectedToInternet(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager != null){
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if(info != null){
                for(int i=0; i<info.length; i++){
                    if(info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }
}
