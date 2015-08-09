package xiao.love.bar.memory.file.base;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by guochang on 2015/8/9.
 * 文件中保存对象
 */
public class FileCache<T> {
    public void saveT(Context ctx, T data, String name, String folder) {
        save(ctx, data, name, folder);
    }

    public void save(Context ctx, ArrayList<T> data, String name, String folder) {
        save(ctx, data, name, folder);
    }

    private void save(Context ctx, Object data, String name, String folder) {
        if (ctx == null) {
            return;
        }

        File file;
        if (!TextUtils.isEmpty(folder)) {
            File fileDir = new File(ctx.getFilesDir(), folder);
            if (!fileDir.exists() || !fileDir.isDirectory()) {
                fileDir.mkdir();
            }
            file = new File(fileDir, name);
        } else {
            file = new File(ctx.getFilesDir(), name);
        }

        if (file.exists()) {
            file.delete();
        }

        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(data);
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public T loadT(Context ctx, String name, String folder) {
        ArrayList<T> list = load(ctx, name, folder);

        if(list.size() > 0){
            return list.get(0);
        }else {
            return null;
        }
    }

    public ArrayList<T> load(Context ctx, String name, String folder) {
        ArrayList<T> data = null;

        File file;
        if (!TextUtils.isEmpty(folder)) {
            File fileDir = new File(ctx.getFilesDir(), folder);
            if (!fileDir.exists() || !fileDir.isDirectory()) {
                fileDir.mkdir();
            }
            file = new File(fileDir, name);
        } else {
            file = new File(ctx.getFilesDir(), name);
        }

        if (file.exists()) {
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
                data = (ArrayList<T>) ois.readObject();
                ois.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (data == null) {
            data = new ArrayList<T>();
        }

        return data;
    }

    public void delete(Context ctx, String name, String folder) {
        File file = new File(ctx.getFilesDir() + "\\" + folder, name);
        if (file.exists()) {
            file.delete();
        }
    }
}
