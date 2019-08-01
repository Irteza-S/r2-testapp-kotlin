package org.readium.r2.testapp


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle




import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_word_search.*
import org.readium.r2.shared.Publication
import org.w3c.dom.Text
import java.io.InputStream


data class Sentence (var location: String, var sentence: String) {}

class WordDatabaseHelper(context: Context) :
        SQLiteOpenHelper(context,"SentenceDB" , null, DB_VERSIOM) {

    override fun onCreate(db: SQLiteDatabase?) {
        /*
        val CREATE_TABLE = "CREATE VIRTUAL TABLE IF NOT EXISTS $TABLE_NAME " +
                "USING fts4($ID Integer PRIMARY KEY, $FIRST_NAME TEXT, $LAST_NAME TEXT);"
        db?.execSQL(CREATE_TABLE)*/

        db?.execSQL("CREATE VIRTUAL TABLE $TABLE_NAME USING fts4 ($LOCATOR, $SENTENCE)")

    }

    fun populateDB(currentChapter: String, resourcesHtml: List<String>) {
        Log.d("currentChapter", currentChapter)
        for (i in 0..resourcesHtml.size - 1) {
            var test : Array<String> = arrayOf("Resource"+i, resourcesHtml.get(i))
            writableDatabase?.execSQL("INSERT INTO $TABLE_NAME VALUES (?, ?)", test);
        }
        var test : Array<String> = arrayOf("P0", currentChapter)
        writableDatabase?.execSQL("INSERT INTO $TABLE_NAME VALUES (?, ?)", test);
        var tmp ="The cold passed reluctantly from the earth, and the retiring fogs revealed an army stretched out on the hills, resting. As the landscape changed from brown to green, the army awakened, and began to tremble with eagerness at the noise of rumors. It cast its eyes upon the roads, which were growing from long troughs of liquid mud to proper thoroughfares. A river, amber-tinted in the shadow of its banks, purled at the army feet; and at night, when the stream had become of a sorrowful blackness, one could see across it the red, eyelike gleam of hostile camp-fires set in the low brows of distant hills."
        writableDatabase?.execSQL("INSERT INTO $TABLE_NAME VALUES ('P1','"+tmp+"')")
        tmp = "To his attentive audience he drew a loud and elaborate plan of a very brilliant campaign. When he had finished, the blue-clothed men scattered into small arguing groups between the rows of squat brown huts. A negro teamster who had been dancing upon a cracker box with the hilarious encouragement of twoscore soldiers was deserted. He sat mournfully down. Smoke drifted lazily from a multitude of quaint chimneys"
        writableDatabase?.execSQL("INSERT INTO $TABLE_NAME VALUES ('P2','"+tmp+"')")
        tmp = "\"Its a lie! thats all it is—a thunderin lie! said another private loudly. His smooth face was flushed, and his hands were thrust sulkily into his trousers pockets. He took the matter as an affront to him. \"I dont believe the derned old armys ever going to move. Were set. Ive got ready to move eight times in the last two weeks, and we aint moved yet.\""
        writableDatabase?.execSQL("INSERT INTO $TABLE_NAME VALUES ('P3','"+tmp+"')")

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Called when the database needs to be upgraded
    }

    fun resetDB() {
        this.writableDatabase.delete(TABLE_NAME, null,null)
    }

    //get all users
    fun getAllSentences(): String {
        var allSentences: String = ""
        val db = readableDatabase
        val selectALLQuery = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(selectALLQuery, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    var locator = cursor.getString(cursor.getColumnIndex(LOCATOR))
                    var sentence = cursor.getString(cursor.getColumnIndex(SENTENCE))

                    allSentences = "$allSentences\n\n$locator  $sentence"
                } while (cursor.moveToNext())
            }
        }
        cursor.close()
        return allSentences
    }


    fun getSentenceByWordSnippet(word: String) : String {
        var keyword : String = word + "*"
        var tmp : String =""
        val cursor = readableDatabase.rawQuery("SELECT snippet($TABLE_NAME), * FROM $TABLE_NAME WHERE $TABLE_NAME MATCH '"+keyword+"'", null)
        if (cursor.moveToFirst()) {
            do {
                tmp = tmp + cursor.getString(1) + " "
                tmp = tmp + cursor.getString(0) + "\n" + "\n"
            } while (cursor.moveToNext())
        }
        cursor?.close()
        return tmp
    }

    fun getSentenceByWordOffset(word: String) : String {
        var offset = 0
        var lenght = 0
        var keyword : String = word + "*"
        var tmp : String =""
        var cursor = readableDatabase.rawQuery("SELECT offsets($TABLE_NAME), * FROM $TABLE_NAME WHERE $TABLE_NAME MATCH '"+keyword+"'", null)
        if (cursor.moveToFirst()) {

            //Log.d("SEQUENCE", cursor.getString(2))
            do {
                Log.d("SEQUENCE", cursor.getString(1))
                tmp = tmp + cursor.getString(1) + "  "
                tmp = tmp + cursor.getString(0) + "\n" + "\n"
                var sequence = cursor.getString(0).split(" ")
                offset = sequence[2].toInt()
                lenght = sequence[3].toInt()
                var chars = cursor.getString(2)
                val bytes = chars.toByteArray(charset("UTF-8"))
                val prefix = String(bytes, 0, offset, Charsets.UTF_8)
                val match = String(bytes, offset, lenght, Charsets.UTF_8)
                val charOffset = prefix.length
                val charSize = match.length

                Log.d("SEQUENCE", match)
                Log.d("SEQUENCE", prefix)
                Log.d("SEQUENCE", charOffset.toString())
                Log.d("SEQUENCE", charSize.toString())
            } while (cursor.moveToNext())
        }

        cursor?.close()
        return tmp
    }

    companion object {
        //private val DB_NAME = "SentenceDB"
        private val DB_VERSIOM = 1
        private val TABLE_NAME = "SentenceTable"
        private val LOCATOR = "locator"
        private val SENTENCE = "sentence"


    }
}



class WordSearch : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_search)
        //init db
        var dbHandler = WordDatabaseHelper(this)

        val currentChapter = intent.getSerializableExtra("currentChapter") as String
        val resourcesList = intent.getSerializableExtra("resourcesHtml") as ArrayList<String>
        val tmp = resourcesList.toList()
        Log.d("currentChapter", "currentChapter: " + currentChapter)

        dbHandler.resetDB()
        dbHandler.populateDB(currentChapter, tmp)
        //on Click Save button

        //on Click show button
        searchButton.setOnClickListener(View.OnClickListener {
            val keyword = findViewById<TextView>(R.id.searchSentenceTextView)
            var sentences = dbHandler!!.getSentenceByWordSnippet(keyword.text.toString())
            resultsTextView.text = sentences
        })

        searchOffset.setOnClickListener(View.OnClickListener {
            val keyword = findViewById<TextView>(R.id.searchSentenceTextView)
            var sentences = dbHandler!!.getSentenceByWordOffset(keyword.text.toString())
            resultsTextView.text = sentences
        })

        clearButton.setOnClickListener(View.OnClickListener {
            resultsTextView.text = ""
        })

        allButton.setOnClickListener(View.OnClickListener {
            var sentences = dbHandler!!.getAllSentences()
            resultsTextView.text = sentences
        })
    }
    fun validation(): Boolean{
        var validate = false

        if (!searchSentenceTextView.text.toString().equals("")){
            validate = true
        }else{
            validate = false
            val toast = Toast.makeText(this,"Fill all details", Toast.LENGTH_LONG).show()
        }

        return validate
    }
}
