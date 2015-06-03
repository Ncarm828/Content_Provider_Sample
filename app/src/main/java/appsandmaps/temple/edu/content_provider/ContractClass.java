package appsandmaps.temple.edu.content_provider;

/**
 * Created by nickcarmen on 6/2/15.
 */
import android.net.Uri;
import android.provider.BaseColumns;

public class ContractClass {

    public ContractClass(){}

    public static final String AUTHORITY = "edu.temple.app.provider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY+ "/steps");

    public static final String DATABASE_NAME = "steps.db";
    public static final int DATABASE_VERSION = 1;

    public static final String CONTENT_TYPE_NOTES_ALL = "vnd.android.cursor.dir/vnd.edu.temple.provider.steps";
    public static final String CONTENT_TYPE_NOTES_ONE = "vnd.android.cursor.item/vnd.edu.temple.provider.steps";

    public static String DataBaseInfoHolder = "NULL";


    public class NotesTable implements BaseColumns {

        private NotesTable() {

        }

        public static final String TABLE_NAME = "tbl_notes";

        public static final String ID = "_id";
        public static final String TITLE = "title";
        public static final String CONTENT = "content";
    }


}
