package xiao.love.bar.memory.db.base;

import java.io.IOException;
import java.sql.SQLException;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import xiao.love.bar.memory.db.model.NameValuePair;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */
public class DatabaseConfigUtil extends OrmLiteConfigUtil {

	public static void init(){
		try {
			writeConfigFile("ormlite_config.txt", new Class[]{NameValuePair.class});
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
