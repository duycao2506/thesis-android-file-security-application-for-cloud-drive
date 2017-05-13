package thesis.tg.com.s_cloud.framework_components.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;


/**
 * Created by admin on 4/2/17.
 */

public class SuperObject extends Object implements Parcelable {

    @SerializedName("id")
    String id;

    @SerializedName("title")
    String name;

    @SerializedName("bodytag")
    String bodytag;

    @Override
    public String toString() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SuperObject() {
        super();
    }

    public SuperObject(JSONObject jso) {
        super();
    }

    protected SuperObject(Parcel in) {
    }

    public static final Creator<SuperObject> CREATOR = new Creator<SuperObject>() {
        @Override
        public SuperObject createFromParcel(Parcel in) {
            return new SuperObject(in);
        }

        @Override
        public SuperObject[] newArray(int size) {
            return new SuperObject[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}


