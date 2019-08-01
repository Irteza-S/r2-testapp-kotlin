package org.readium.r2.testapp.Results

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_outline_container.*
import kotlinx.android.synthetic.main.activity_results.*
import org.json.JSONArray
import org.json.JSONObject
import org.readium.r2.navigator.focusPosition
import org.readium.r2.navigator.resourceGlobal
import org.readium.r2.shared.Locations
import org.readium.r2.shared.Locator
import org.readium.r2.shared.Publication
import org.readium.r2.testapp.R





class ResultsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)



        val tmp = intent.getSerializableExtra("resultItems") as String
        var resultItems= JSONArray(tmp)
        var adapter : MyListAdapter? = null
        var resultList = mutableListOf<ResultItem>()
        for (i in 0..(resultItems.length() - 1)) {
            val obj = resultItems.getJSONObject(i)
            val resourceId = obj.getString("resourceId")
            val words = obj.getJSONArray("resultsList")

            for (i in 0..(words.length() - 1)) {
                val item = words.getString(i)
                var tmp = ResultItem(resourceId, i, item)
                resultList.add(tmp)
            }

        }

        adapter = MyListAdapter(this, resultList)

        listView.adapter = adapter

        listView.setOnItemClickListener { adapterView, view, i, l ->

            var counter = 0
            //GETTING RESOURCE REF BY ITERATING THROUGH resultItems = JSONObject ("resourceId", {[],[]...})
            var resourceHref = ""
            for (j in 0 until resultItems.length()) {
                var obj = resultItems[j] as JSONObject

                for (k in 0 until obj.getJSONArray("resultsList").length()) {
                    if(counter == i) {
                        resourceHref = obj.getString("resourceId")
                        focusPosition = k
                    }
                    counter++
                }

            }

            val resourceType = ""
            var publication = intent.getSerializableExtra("publication") as Publication

            Log.d("CHAP : ", resourceHref)

            resourceGlobal = resourceHref

            resourceHref?.let {
                val intent = Intent()
                intent.putExtra("locator", Locator(resourceHref, resourceType, publication.metadata.title, Locations(progression = 0.0),null))
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
            finish()
        }

/*
        listView.setOnItemClickListener { adapterView, view, i, id ->
            var counter = 0

            var resourceHref = ""
            for (j in 0 until resultItems.length()) {
                var obj = resultItems[j] as JSONObject

                for (k in 0 until obj.getJSONArray("resultsList").length()) {
                    if(counter == i) {
                        resourceHref = obj.getString("resourceId")
                    }
                    counter++
                }

            }

            Toast.makeText(this, "Selected resource is = "+ resourceHref, Toast.LENGTH_SHORT).show()
        }*/
    }

}
