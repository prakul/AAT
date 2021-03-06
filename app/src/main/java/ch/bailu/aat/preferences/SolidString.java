package ch.bailu.aat.preferences;

import java.util.ArrayList;

public class SolidString extends AbsSolidType {
    private final String key;
    private final Storage storage;


    public SolidString(Storage s, String k) {
        storage=s;
        key=k;
    }

    @Override
    public String getValueAsString() {
        return getStorage().readString(getKey());
    }

    @Override
    public void setValueFromString(String s) {
        setValue(s);
    }

    public void setValue(String v) {
        getStorage().writeString(key, v);
    }


    @Override
    public String getKey() {
        return key;
    }

    @Override
    public Storage getStorage() {
        return storage;
    }


    public ArrayList<String> buildSelection(ArrayList<String> strings) {
        return strings;
    }
}
