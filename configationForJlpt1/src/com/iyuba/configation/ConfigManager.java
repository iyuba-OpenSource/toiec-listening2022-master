package com.iyuba.configation;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

/**
 * @author gyx
 * <p>
 * 功能：配置文件管理
 */
public class ConfigManager {
    private volatile static ConfigManager instance;

    public static final String CONFIG_NAME = "config";

    private Context context;

    private SharedPreferences.Editor editor;

    private SharedPreferences preferences;

    public static ConfigManager Instance() {
        if (instance == null) {
            synchronized (ConfigManager.class) {
                if (instance == null) {
                    instance = new ConfigManager();
                }
            }
        }
        return instance;
    }

    private ConfigManager() {

        this.context = RuntimeManager.getApplication();//getContext();

        openEditor();
    }

    private ConfigManager(Context context) {

        this.context = context;

        openEditor();
    }

    // 创建或修改配置文件
    public void openEditor() {
        int mode = Activity.MODE_PRIVATE;
        if (context == null) {
            context = RuntimeManager.getApplication();
        }
        preferences = context.getSharedPreferences(CONFIG_NAME, mode);
        editor = preferences.edit();
    }

    public void putBoolean(String name, boolean value) {

        editor.putBoolean(name, value);
        editor.commit();
    }

    public void putFloat(String name, float value) {

        editor.putFloat(name, value);
        editor.commit();
    }

    public void putInt(String name, int value) {

        editor.putInt(name, value);
        editor.commit();
    }

    public void putLong(String name, long value) {

        editor.putLong(name, value);
        editor.commit();
    }

    public void putString(String name, String value) {
        editor.putString(name, value);
        editor.commit();
    }

    /**
     * 对象存储
     *
     * @param name
     * @param value
     * @throws IOException
     */
    public void putString(String name, Object value) throws IOException {
        // 把值对象以流的形式转化为字符串。
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(value);
        String productBase64 = new String(Base64.encodeBase64(baos
                .toByteArray()));
        putString(name, productBase64);
        oos.close();
    }

    public boolean loadBoolean(String key) {
        return preferences.getBoolean(key, true);
    }

    public boolean loadBoolean(String key, boolean def) {
        return preferences.getBoolean(key, def);
    }

    public float loadFloat(String key) {
        return preferences.getFloat(key, 0);
    }

    public int loadInt(String key) {
        return preferences.getInt(key, 0);
    }

    public int loadInt(String key, int num) {
        return preferences.getInt(key, num);
    }

    public long loadLong(String key) {
        return preferences.getLong(key, 0);
    }

    public String loadString(String key) {
        return preferences.getString(key, "");
    }

    public void removeKey(String key) {
        editor.remove(key);
        editor.commit();
    }

    /**
     * 读取对象格式
     *
     * @param key
     * @return
     * @throws IOException
     * @throws StreamCorruptedException
     * @throws ClassNotFoundException
     */
    public Object loadObject(String key) throws StreamCorruptedException,
            IOException, ClassNotFoundException {
        String objBase64String = loadString(key);
        byte[] b = Base64.decodeBase64(objBase64String.getBytes());
        InputStream bais = new ByteArrayInputStream(b);
        ObjectInputStream ois = new ObjectInputStream(bais); // something wrong
        Object obj = ois.readObject();
        ois.close();
        return obj;
    }
}
